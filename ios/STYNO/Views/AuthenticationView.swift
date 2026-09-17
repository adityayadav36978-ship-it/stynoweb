import SwiftUI
import AuthenticationServices

public struct AuthenticationView: View {
    @EnvironmentObject var authService: AuthService
    @State private var email: String = ""
    @State private var password: String = ""
    @State private var displayName: String = ""
    @State private var isSignUp: Bool = false
    @State private var showingForgotPassword: Bool = false
    @State private var forgotPasswordEmail: String = ""
    @State private var showingAlert: Bool = false
    @State private var alertMessage: String = ""
    
    public init() {}
    
    public var body: some View {
        NavigationStack {
            ZStack {
                Color(uiColor: .systemGroupedBackground)
                    .ignoresSafeArea()
                
                ScrollView {
                    VStack(spacing: 24) {
                        // App Branding Header
                        VStack(spacing: 8) {
                            ZStack {
                                RoundedRectangle(cornerRadius: 22, style: .continuous)
                                    .fill(
                                        LinearGradient(
                                            colors: [Color.accentColor, Color.accentColor.opacity(0.8)],
                                            startPoint: .topLeading,
                                            endPoint: .bottomTrailing
                                        )
                                    )
                                    .frame(width: 84, height: 84)
                                
                                Image(systemName: "house.fill")
                                    .font(.system(size: 40, weight: .bold))
                                    .foregroundColor(.white)
                            }
                            .shadow(color: Color.accentColor.opacity(0.35), radius: 14, y: 6)
                            
                            Text("STYNO")
                                .font(.system(size: 34, weight: .black, design: .rounded))
                                .tracking(1.5)
                            
                            Text("Hostels • Hotels • PGs • Flats • Quick Stays")
                                .font(.subheadline)
                                .foregroundColor(.secondary)
                            
                            HStack(spacing: 6) {
                                Image(systemName: "checkmark.seal.fill")
                                    .foregroundColor(.green)
                                    .font(.caption)
                                Text("0% Brokerage • 100% Verified Stays")
                                    .font(.caption)
                                    .fontWeight(.bold)
                                    .foregroundColor(.green)
                            }
                            .padding(.horizontal, 12)
                            .padding(.vertical, 5)
                            .background(Color.green.opacity(0.12))
                            .clipShape(Capsule())
                        }
                        .padding(.top, 20)
                        
                        // Status Alerts / Info Banners
                        if let info = authService.infoMessage {
                            HStack(spacing: 10) {
                                Image(systemName: "info.circle.fill")
                                    .foregroundColor(.blue)
                                Text(info)
                                    .font(.subheadline)
                                    .foregroundColor(.primary)
                                Spacer()
                            }
                            .padding()
                            .background(Color.blue.opacity(0.1))
                            .cornerRadius(12)
                            .padding(.horizontal)
                        }
                        
                        // Auth Card
                        VStack(spacing: 18) {
                            Picker("Auth Mode", selection: $isSignUp) {
                                Text("Sign In").tag(false)
                                Text("Sign Up").tag(true)
                            }
                            .pickerStyle(.segmented)
                            .padding(.bottom, 6)
                            
                            // Name Field (Only on Sign Up)
                            if isSignUp {
                                HStack {
                                    Image(systemName: "person")
                                        .foregroundColor(.secondary)
                                        .frame(width: 22)
                                    TextField("Full Name (e.g. Rahul Sharma)", text: $displayName)
                                        .textContentType(.name)
                                        .autocapitalization(.words)
                                }
                                .padding()
                                .background(Color(uiColor: .secondarySystemGroupedBackground))
                                .cornerRadius(12)
                                .transition(.opacity.combined(with: .move(edge: .top)))
                            }
                            
                            // Email Field
                            HStack {
                                Image(systemName: "envelope")
                                    .foregroundColor(.secondary)
                                    .frame(width: 22)
                                TextField("Email Address", text: $email)
                                    .textContentType(.emailAddress)
                                    .keyboardType(.emailAddress)
                                    .autocapitalization(.none)
                                    .disableAutocorrection(true)
                            }
                            .padding()
                            .background(Color(uiColor: .secondarySystemGroupedBackground))
                            .cornerRadius(12)
                            
                            // Password Field
                            HStack {
                                Image(systemName: "lock")
                                    .foregroundColor(.secondary)
                                    .frame(width: 22)
                                SecureField("Password (min. 6 characters)", text: $password)
                                    .textContentType(isSignUp ? .newPassword : .password)
                            }
                            .padding()
                            .background(Color(uiColor: .secondarySystemGroupedBackground))
                            .cornerRadius(12)
                            
                            // Forgot Password Link (Only in Sign In mode)
                            if !isSignUp {
                                HStack {
                                    Spacer()
                                    Button {
                                        forgotPasswordEmail = email
                                        showingForgotPassword = true
                                    } label: {
                                        Text("Forgot Password?")
                                            .font(.footnote)
                                            .fontWeight(.semibold)
                                            .foregroundColor(.accentColor)
                                    }
                                }
                                .padding(.top, -6)
                            }
                            
                            // Primary Submit Button
                            Button {
                                Task {
                                    if isSignUp {
                                        await authService.signUpWithEmail(email: email, password: password, displayName: displayName)
                                    } else {
                                        await authService.signInWithEmail(email: email, password: password)
                                    }
                                }
                            } label: {
                                HStack {
                                    if authService.isLoading {
                                        ProgressView()
                                            .tint(.white)
                                    } else {
                                        Text(isSignUp ? "Create STYNO Account" : "Sign In with Email")
                                            .font(.headline)
                                            .fontWeight(.bold)
                                    }
                                }
                                .frame(maxWidth: .infinity)
                                .frame(height: 52)
                                .background(
                                    canSubmitEmail ? Color.accentColor : Color.accentColor.opacity(0.4)
                                )
                                .foregroundColor(.white)
                                .cornerRadius(14)
                            }
                            .disabled(!canSubmitEmail || authService.isLoading)
                            
                            // Divider
                            HStack {
                                Rectangle().fill(Color.secondary.opacity(0.25)).frame(height: 1)
                                Text("OR CONTINUE WITH")
                                    .font(.caption2)
                                    .fontWeight(.heavy)
                                    .foregroundColor(.secondary)
                                Rectangle().fill(Color.secondary.opacity(0.25)).frame(height: 1)
                            }
                            .padding(.vertical, 4)
                            
                            // Native Sign in with Apple Button (iPhone पर Apple Sign-In)
                            SignInWithAppleButton(
                                isSignUp ? .signUp : .signIn,
                                onRequest: { request in
                                    let (appleRequest, _) = authService.startAppleSignIn()
                                    request.requestedScopes = appleRequest.requestedScopes
                                    request.nonce = appleRequest.nonce
                                },
                                onCompletion: { result in
                                    switch result {
                                    case .success(let authorization):
                                        Task {
                                            await authService.handleAppleAuthorization(authorization)
                                        }
                                    case .failure(let error):
                                        alertMessage = error.localizedDescription
                                        showingAlert = true
                                    }
                                }
                            )
                            .signInWithAppleButtonStyle(.black)
                            .frame(height: 52)
                            .cornerRadius(14)
                            .shadow(color: Color.black.opacity(0.12), radius: 6, y: 3)
                            
                            // Continue with Google Button
                            Button {
                                Task {
                                    await authService.startGoogleSignIn()
                                }
                            } label: {
                                HStack(spacing: 12) {
                                    Image(systemName: "globe.americas.fill")
                                        .font(.system(size: 20))
                                        .foregroundColor(.blue)
                                    Text(isSignUp ? "Sign Up with Google" : "Continue with Google")
                                        .font(.headline)
                                        .fontWeight(.semibold)
                                }
                                .frame(maxWidth: .infinity)
                                .frame(height: 52)
                                .background(Color(uiColor: .secondarySystemGroupedBackground))
                                .foregroundColor(.primary)
                                .cornerRadius(14)
                                .overlay(
                                    RoundedRectangle(cornerRadius: 14)
                                        .stroke(Color.secondary.opacity(0.25), lineWidth: 1)
                                Bio: nil
                                )
                            }
                            
                            // Guest Access Button
                            Button {
                                Task {
                                    await authService.continueAsGuest()
                                }
                            } label: {
                                Text("Explore Stays as Guest (No Account Required)")
                                    .font(.subheadline)
                                    .fontWeight(.semibold)
                                    .foregroundColor(.secondary)
                                    .padding(.vertical, 6)
                            }
                        }
                        .padding(22)
                        .background(Color(uiColor: .systemBackground))
                        .cornerRadius(22)
                        .shadow(color: Color.black.opacity(0.04), radius: 12, y: 6)
                        .padding(.horizontal)
                        
                        // Terms & Policies
                        Text("By logging in or creating an account, you agree to STYNO's Terms of Service and Privacy Policy.")
                            .font(.caption2)
                            .foregroundColor(.secondary)
                            .multilineTextAlignment(.center)
                            .padding(.horizontal, 32)
                            .padding(.bottom, 24)
                    }
                }
            }
            .navigationBarHidden(true)
            .sheet(isPresented: $showingForgotPassword) {
                ForgotPasswordSheet(email: $forgotPasswordEmail)
            }
            .alert("Authentication", isPresented: $showingAlert) {
                Button("OK", role: .cancel) {}
            } message: {
                Text(alertMessage)
            }
            .onChange(of: authService.errorMessage) { newValue in
                if let err = newValue {
                    alertMessage = err
                    showingAlert = true
                }
            }
        }
    }
    
    private var canSubmitEmail: Bool {
        !email.trimmingCharacters(in: .whitespaces).isEmpty && password.count >= 6
    }
}

// MARK: - Forgot Password Sheet

struct ForgotPasswordSheet: View {
    @Binding var email: String
    @EnvironmentObject var authService: AuthService
    @Environment(\.dismiss) var dismiss
    @State private var isSent: Bool = false
    
    var body: some View {
        NavigationStack {
            VStack(spacing: 20) {
                Image(systemName: "lock.rotation")
                    .font(.system(size: 60))
                    .foregroundColor(.accentColor)
                    .padding(.top, 30)
                
                Text("Reset Your Password")
                    .font(.title2)
                    .fontWeight(.bold)
                
                Text("Enter the email associated with your STYNO account. We will send you a link to reset your password.")
                    .font(.subheadline)
                    .foregroundColor(.secondary)
                    .multilineTextAlignment(.center)
                    .padding(.horizontal)
                
                HStack {
                    Image(systemName: "envelope")
                        .foregroundColor(.secondary)
                    TextField("Registered Email Address", text: $email)
                        .textContentType(.emailAddress)
                        .keyboardType(.emailAddress)
                        .autocapitalization(.none)
                }
                .padding()
                .background(Color(uiColor: .secondarySystemGroupedBackground))
                .cornerRadius(12)
                .padding(.horizontal)
                
                Button {
                    Task {
                        let success = await authService.sendPasswordReset(email: email)
                        if success {
                            isSent = true
                        }
                    }
                } label: {
                    HStack {
                        if authService.isLoading {
                            ProgressView().tint(.white)
                        } else {
                            Text("Send Reset Link")
                                .font(.headline)
                                .fontWeight(.bold)
                        }
                    }
                    .frame(maxWidth: .infinity)
                    .frame(height: 50)
                    .background(email.isEmpty ? Color.accentColor.opacity(0.4) : Color.accentColor)
                    .foregroundColor(.white)
                    .cornerRadius(14)
                }
                .disabled(email.isEmpty || authService.isLoading)
                .padding(.horizontal)
                
                if isSent {
                    Text("Check your inbox for the reset link.")
                        .font(.footnote)
                        .foregroundColor(.green)
                        .fontWeight(.semibold)
                }
                
                Spacer()
            }
            .padding()
            .toolbar {
                ToolbarItem(placement: .cancellationAction) {
                    Button("Close") {
                        dismiss()
                    }
                }
            }
        }
        .presentationDetents([.medium])
    }
}
