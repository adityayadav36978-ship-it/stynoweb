import Foundation

public struct StynoProperty: Identifiable, Codable, Equatable {
    public let id: String
    public var title: String
    public var category: String // "Hostel", "PG", "Hotel", "Flat", "Room", "Quick Stay"
    public var address: String
    public var city: String
    public var area: String
    public var pricePerMonth: Double
    public var deposit: Double
    public var rating: Double
    public var reviewCount: Int
    public var images: [String]
    public var amenities: [String]
    public var isVerified: Bool
    public var ownerName: String
    public var ownerPhone: String
    public var latitude: Double
    public var longitude: Double
    public var description: String

    public init(
        id: String = UUID().uuidString,
        title: String,
        category: String,
        address: String,
        city: String,
        area: String,
        pricePerMonth: Double,
        deposit: Double = 0.0,
        rating: Double = 4.8,
        reviewCount: Int = 12,
        images: [String] = [],
        amenities: [String] = ["High-Speed Wi-Fi", "AC", "Power Backup", "CCTV", "RO Water"],
        isVerified: Bool = true,
        ownerName: String = "STYNO Verified Host",
        ownerPhone: String = "+919876543210",
        latitude: Double = 28.6139,
        longitude: Double = 77.2090,
        description: String = "Clean, safe, fully furnished stay with zero brokerage and high security."
    ) {
        self.id = id
        self.title = title
        self.category = category
        self.address = address
        self.city = city
        self.area = area
        self.pricePerMonth = pricePerMonth
        self.deposit = deposit
        self.rating = rating
        self.reviewCount = reviewCount
        self.images = images
        self.amenities = amenities
        self.isVerified = isVerified
        self.ownerName = ownerName
        self.ownerPhone = ownerPhone
        self.latitude = latitude
        self.longitude = longitude
        self.description = description
    }
}
