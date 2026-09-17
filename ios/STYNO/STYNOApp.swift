import SwiftUI

#if canImport(FirebaseCore)
import FirebaseCore
#endif

class AppDelegate: NSObject, UIApplicationDelegate {
    func application(
        _ application: UIApplication,
        didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey : Any]? = nil
    ) -> Bool {
        #if canImport(FirebaseCore)
        FirebaseApp.configure()
        #endif
        return true
    }
}

@main
public struct STYNOApp: App {
    @UIApplicationDelegateAdaptor(AppDelegate.self) var delegate
    @StateObject private var authService = AuthService()
    @StateObject private var propertyService = PropertyService()
    
    public init() {}
    
    public var body: some Scene {
        WindowGroup {
            Group {
                if authService.currentUser != nil {
                    HomeView()
                } else {
                    AuthenticationView()
                }
            }
            .environmentObject(authService)
            .environmentObject(propertyService)
        }
    }
}
