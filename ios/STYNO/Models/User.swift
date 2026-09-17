import Foundation

public enum AuthProviderType: String, Codable {
    case apple = "apple.com"
    case google = "google.com"
    case email = "password"
    case phone = "phone"
    case anonymous = "anonymous"
}

public struct StynoUser: Identifiable, Codable, Equatable {
    public let id: String
    public var email: String?
    public var displayName: String?
    public var phoneNumber: String?
    public var photoUrl: String?
    public var isAnonymous: Boolean
    public var isEmailVerified: Boolean
    public var providerId: String?
    public var createdAt: Date
    
    public typealias Boolean = Bool

    public init(
        id: String,
        email: String? = nil,
        displayName: String? = nil,
        phoneNumber: String? = nil,
        photoUrl: String? = nil,
        isAnonymous: Bool = false,
        isEmailVerified: Bool = false,
        providerId: String? = nil,
        createdAt: Date = Date()
    ) {
        self.id = id
        self.email = email
        self.displayName = displayName
        self.phoneNumber = phoneNumber
        self.photoUrl = photoUrl
        self.isAnonymous = isAnonymous
        self.isEmailVerified = isEmailVerified
        self.providerId = providerId
        self.createdAt = createdAt
    }
    
    public var isAppleUser: Bool {
        providerId == "apple.com"
    }
    
    public var isGoogleUser: Bool {
        providerId == "google.com"
    }
}
