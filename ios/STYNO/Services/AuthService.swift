import Foundation
import Combine
import AuthenticationServices
import CryptoKit

#if canImport(FirebaseAuth)
import FirebaseAuth
#endif
#if canImport(FirebaseFirestore)
import FirebaseFirestore
#endif

@MainActor
public class AuthService: NSObject, ObservableObject {
    @Published public var currentUser: StynoUser?
    @Published public var isLoading: Bool = false
    @Published public var errorMessage: String?
    @Published public var infoMessage: String?
    @Published public var isEmailVerificationSent: Bool = false
    
    private var currentNonce: String?
    #if canImport(FirebaseAuth)
    private var authStateHandle: AuthStateDidChangeListenerHandle?
    #endif
    
    public override init() {
        super.init()
        setupAuthStateListener()
    }
    
    deinit {
        #if canImport(FirebaseAuth)
        if let handle = authStateHandle {
            Auth.auth().removeStateDidChangeListener(handle)
        }
        #endif
    }
    
    // MARK: - Secure Session Management
    
    private func setupAuthStateListener() {
        #if canImport(FirebaseAuth)
        authStateHandle = Auth.auth().addStateDidChangeListener { [weak self] (_, user) in
            Task { @MainActor in
                guard let self = self else { return }
                if let firebaseUser = user {
                    self.currentUser = StynoUser(
                        id: firebaseUser.uid,
                        email: firebaseUser.email,
                        displayName: firebaseUser.displayName ?? "Styno Traveler",
                        phoneNumber: firebaseUser.phoneNumber,
                        photoUrl: firebaseUser.photoURL?.absoluteString,
                        isAnonymous: firebaseUser.isAnonymous,
                        isEmailVerified: firebaseUser.isEmailVerified,
                        providerId: firebaseUser.providerData.first?.providerId ?? "firebase"
                    )
                } else {
                    self.currentUser = nil
                }
            }
        }
        #endif
    }
    
    // MARK: - Sign in with Apple (iPhone पर Apple Sign-In)
    
    public func startAppleSignIn() -> (request: ASAuthorizationAppleIDRequest, nonce: String) {
        let nonce = randomNonceString()
        currentNonce = nonce
        let appleIDProvider = ASAuthorizationAppleIDProvider()
        let request = appleIDProvider.createRequest()
        request.requestedScopes = [.fullName, .email]
        request.nonce = sha256(nonce)
        return (request, nonce)
    }
    
    public func handleAppleAuthorization(_ authorization: ASAuthorization) async {
        guard let appleIDCredential = authorization.credential as? ASAuthorizationAppleIDCredential else {
            self.errorMessage = "Unable to read Apple credentials"
            return
        }
        
        guard let nonce = currentNonce else {
            self.errorMessage = "Invalid security state: Missing nonce for Apple Sign-In"
            return
        }
        
        guard let appleIDToken = appleIDCredential.identityToken,
              let idTokenString = String(data: appleIDToken, encoding: .utf8) else {
            self.errorMessage = "Unable to fetch identity token from Apple"
            return
        }
        
        self.isLoading = true
        self.errorMessage = nil
        
        #if canImport(FirebaseAuth)
        let credential = OAuthProvider.appleCredential(
            withIDToken: idTokenString,
            rawNonce: nonce,
            fullName: appleIDCredential.fullName
        )
        
        do {
            let authResult = try await Auth.auth().signIn(with: credential)
            let user = authResult.user
            
            // Extract Apple Full Name if provided
            var fullName: String? = nil
            if let givenName = appleIDCredential.fullName?.givenName {
                let family = appleIDCredential.fullName?.familyName ?? ""
                fullName = "\(givenName) \(family)".trimmingCharacters(in: .whitespaces)
            }
            
            if let name = fullName, !name.isEmpty, (user.displayName == nil || user.displayName?.isEmpty == true) {
                let changeRequest = user.createProfileChangeRequest()
                changeRequest.displayName = name
                try? await changeRequest.commitChanges()
            }
            
            let stynoUser = StynoUser(
                id: user.uid,
                email: user.email ?? appleIDCredential.email,
                displayName: fullName ?? user.displayName ?? "Apple Traveler",
                photoUrl: user.photoURL?.absoluteString,
                isAnonymous: false,
                isEmailVerified: true,
                providerId: "apple.com"
            )
            
            self.currentUser = stynoUser
            self.isLoading = false
            await syncUserToFirestore(stynoUser)
        } catch {
            self.isLoading = false
            self.errorMessage = error.localizedDescription
        }
        #else
        let name = [appleIDCredential.fullName?.givenName, appleIDCredential.fullName?.familyName]
            .compactMap { $0 }
            .joined(separator: " ")
        
        let stynoUser = StynoUser(
            id: appleIDCredential.user,
            email: appleIDCredential.email ?? "apple.user@icloud.com",
            displayName: name.isEmpty ? "Apple Traveler" : name,
            isAnonymous: false,
            isEmailVerified: true,
            providerId: "apple.com"
        )
        self.currentUser = stynoUser
        self.isLoading = false
        #endif
    }
    
    // MARK: - Sign in with Google (iOS Credential Exchange)
    
    public func signInWithGoogle(idToken: String, accessToken: String? = nil) async {
        self.isLoading = true
        self.errorMessage = nil
        
        #if canImport(FirebaseAuth)
        let credential = GoogleAuthProvider.credential(withIDToken: idToken, accessToken: accessToken ?? "")
        do {
            let result = try await Auth.auth().signIn(with: credential)
            let user = result.user
            let stynoUser = StynoUser(
                id: user.uid,
                email: user.email,
                displayName: user.displayName ?? "Google Traveler",
                phoneNumber: user.phoneNumber,
                photoUrl: user.photoURL?.absoluteString,
                isAnonymous: false,
                isEmailVerified: user.isEmailVerified,
                providerId: "google.com"
            )
            self.currentUser = stynoUser
            self.isLoading = false
            await syncUserToFirestore(stynoUser)
        } catch {
            self.isLoading = false
            self.errorMessage = error.localizedDescription
        }
        #else
        self.currentUser = StynoUser(
            id: "google_\(UUID().uuidString.prefix(8))",
            email: "google.user@gmail.com",
            displayName: "Google Traveler",
            isAnonymous: false,
            isEmailVerified: true,
            providerId: "google.com"
        )
        self.isLoading = false
        #endif
    }
    
    public func startGoogleSignIn() async {
        self.isLoading = true
        self.errorMessage = nil
        #if canImport(GoogleSignIn)
        guard let presentingViewController = (UIApplication.shared.connectedScenes.first as? UIWindowScene)?.windows.first?.rootViewController else {
            self.isLoading = false
            self.errorMessage = "Unable to find root view controller"
            return
        }
        do {
            let result = try await GIDSignIn.sharedInstance.signIn(withPresenting: presentingViewController)
            let user = result.user
            guard let idToken = user.idToken?.tokenString else {
                self.isLoading = false
                self.errorMessage = "Missing Google ID Token"
                return
            }
            await signInWithGoogle(idToken: idToken, accessToken: user.accessToken.tokenString)
        } catch {
            self.isLoading = false
            self.errorMessage = error.localizedDescription
        }
        #else
        // Fallback for environment where GoogleSignIn framework is linked at build time
        await signInWithGoogle(idToken: UUID().uuidString)
        #endif
    }
    
    // MARK: - Email / Password Registration & Verification
    
    public func signUpWithEmail(email: String, password: String, displayName: String) async {
        guard !email.trimmingCharacters(in: .whitespaces).isEmpty else {
            self.errorMessage = "Please enter an email address."
            return
        }
        guard password.count >= 6 else {
            self.errorMessage = "Password must be at least 6 characters long."
            return
        }
        
        self.isLoading = true
        self.errorMessage = nil
        
        #if canImport(FirebaseAuth)
        do {
            let authResult = try await Auth.auth().createUser(withEmail: email.trimmingCharacters(in: .whitespaces), password: password)
            let user = authResult.user
            
            if !displayName.trimmingCharacters(in: .whitespaces).isEmpty {
                let changeRequest = user.createProfileChangeRequest()
                changeRequest.displayName = displayName.trimmingCharacters(in: .whitespaces)
                try? await changeRequest.commitChanges()
            }
            
            // Send email verification
            try? await user.sendEmailVerification()
            self.isEmailVerificationSent = true
            self.infoMessage = "Account created! Verification link sent to \(email)."
            
            let stynoUser = StynoUser(
                id: user.uid,
                email: user.email,
                displayName: displayName.isEmpty ? (user.displayName ?? "Styno Traveler") : displayName,
                photoUrl: user.photoURL?.absoluteString,
                isAnonymous: false,
                isEmailVerified: false,
                providerId: "password"
            )
            self.currentUser = stynoUser
            self.isLoading = false
            await syncUserToFirestore(stynoUser)
        } catch {
            self.isLoading = false
            self.errorMessage = error.localizedDescription
        }
        #else
        self.currentUser = StynoUser(
            id: UUID().uuidString,
            email: email,
            displayName: displayName.isEmpty ? "Styno Traveler" : displayName,
            isAnonymous: false,
            isEmailVerified: false,
            providerId: "password"
        )
        self.isEmailVerificationSent = true
        self.infoMessage = "Account created! Verification email sent to \(email)."
        self.isLoading = false
        #endif
    }
    
    // MARK: - Email / Password Sign In
    
    public func signInWithEmail(email: String, password: String) async {
        guard !email.trimmingCharacters(in: .whitespaces).isEmpty, !password.isEmpty else {
            self.errorMessage = "Email and password cannot be empty."
            return
        }
        
        self.isLoading = true
        self.errorMessage = nil
        
        #if canImport(FirebaseAuth)
        do {
            let result = try await Auth.auth().signIn(withEmail: email.trimmingCharacters(in: .whitespaces), password: password)
            let user = result.user
            try? await user.reload()
            
            let stynoUser = StynoUser(
                id: user.uid,
                email: user.email,
                displayName: user.displayName ?? "Styno Traveler",
                phoneNumber: user.phoneNumber,
                photoUrl: user.photoURL?.absoluteString,
                isAnonymous: false,
                isEmailVerified: user.isEmailVerified,
                providerId: "password"
            )
            self.currentUser = stynoUser
            self.isLoading = false
            await syncUserToFirestore(stynoUser)
        } catch {
            self.isLoading = false
            self.errorMessage = error.localizedDescription
        }
        #else
        self.currentUser = StynoUser(
            id: UUID().uuidString,
            email: email,
            displayName: "Styno Traveler",
            isAnonymous: false,
            isEmailVerified: true,
            providerId: "password"
        )
        self.isLoading = false
        #endif
    }
    
    // MARK: - Resend Email Verification
    
    public func resendEmailVerification() async {
        #if canImport(FirebaseAuth)
        guard let user = Auth.auth().currentUser else {
            self.errorMessage = "No active session to verify."
            return
        }
        do {
            try await user.sendEmailVerification()
            self.infoMessage = "Verification link sent to \(user.email ?? "your email")."
        } catch {
            self.errorMessage = error.localizedDescription
        }
        #else
        self.infoMessage = "Verification email resent."
        #endif
    }
    
    // MARK: - Password Reset (Forgot Password)
    
    public func sendPasswordReset(email: String) async -> Bool {
        guard !email.trimmingCharacters(in: .whitespaces).isEmpty else {
            self.errorMessage = "Please enter your registered email address."
            return false
        }
        
        self.isLoading = true
        self.errorMessage = nil
        
        #if canImport(FirebaseAuth)
        do {
            try await Auth.auth().sendPasswordReset(withEmail: email.trimmingCharacters(in: .whitespaces))
            self.isLoading = false
            self.infoMessage = "Password reset instructions sent to \(email)."
            return true
        } catch {
            self.isLoading = false
            self.errorMessage = error.localizedDescription
            return false
        }
        #else
        self.isLoading = false
        self.infoMessage = "Password reset instructions sent to \(email)."
        return true
        #endif
    }
    
    // MARK: - Reload Session Status
    
    public func reloadSession() async {
        #if canImport(FirebaseAuth)
        guard let user = Auth.auth().currentUser else { return }
        try? await user.reload()
        self.currentUser?.isEmailVerified = user.isEmailVerified
        #endif
    }
    
    // MARK: - Anonymous Guest Sign-In
    
    public func continueAsGuest() async {
        self.isLoading = true
        self.errorMessage = nil
        
        #if canImport(FirebaseAuth)
        do {
            let result = try await Auth.auth().signInAnonymously()
            let user = result.user
            let stynoUser = StynoUser(
                id: user.uid,
                displayName: "Guest Traveler",
                isAnonymous: true,
                providerId: "anonymous"
            )
            self.currentUser = stynoUser
            self.isLoading = false
        } catch {
            self.isLoading = false
            self.errorMessage = error.localizedDescription
        }
        #else
        self.currentUser = StynoUser(
            id: UUID().uuidString,
            displayName: "Guest Traveler",
            isAnonymous: true,
            providerId: "anonymous"
        )
        self.isLoading = false
        #endif
    }
    
    // MARK: - Sign Out
    
    public func signOut() {
        #if canImport(FirebaseAuth)
        try? Auth.auth().signOut()
        #endif
        self.currentUser = nil
        self.infoMessage = nil
        self.errorMessage = nil
    }
    
    // MARK: - Firestore Profile Sync
    
    private func syncUserToFirestore(_ user: StynoUser) async {
        #if canImport(FirebaseFirestore)
        let db = Firestore.firestore()
        let userData: [String: Any] = [
            "uid": user.id,
            "email": user.email ?? "",
            "displayName": user.displayName ?? "Styno Traveler",
            "phoneNumber": user.phoneNumber ?? "",
            "photoUrl": user.photoUrl ?? "",
            "providerId": user.providerId ?? "password",
            "isEmailVerified": user.isEmailVerified,
            "platform": "iOS",
            "role": "traveler",
            "lastLoginAt": Timestamp(date: Date()),
            "isCloudSynced": true
        ]
        try? await db.collection("users").document(user.id).setData(userData, merge: true)
        try? await db.collection("user_profiles").document("usr_\(user.id)").setData(userData, merge: true)
        #endif
    }
    
    // MARK: - Cryptographic Nonce Helpers for Apple Sign In
    
    private func randomNonceString(length: Int = 32) -> String {
        precondition(length > 0)
        var randomBytes = [UInt8](repeating: 0, count: length)
        let errorCode = SecRandomCopyBytes(kSecRandomDefault, randomBytes.count, &randomBytes)
        if errorCode != errSecSuccess {
            fatalError("Unable to generate nonce. SecRandomCopyBytes failed with OSStatus \(errorCode)")
        }
        let charset: [Character] = Array("0123456789ABCDEFGHIJKLMNOPQRSTUVXYZabcdefghijklmnopqrstuvwxyz-._")
        let nonce = randomBytes.map { byte in
            charset[Int(byte) % charset.count]
        }
        return String(nonce)
    }
    
    private func sha256(_ input: String) -> String {
        let inputData = Data(input.utf8)
        let hashedData = SHA256.hash(data: inputData)
        let hashString = hashedData.compactMap {
            String(format: "%02x", $0)
        }.joined()
        return hashString
    }
}
