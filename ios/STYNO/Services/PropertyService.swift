import Foundation
import Combine

#if canImport(FirebaseFirestore)
import FirebaseFirestore
#endif

@MainActor
public class PropertyService: ObservableObject {
    @Published public var properties: [StynoProperty] = []
    @Published public var selectedCategory: String = "All"
    @Published public var isLoading: Bool = false
    @Published public var searchQuery: String = ""
    
    public let categories = ["All", "Hostels", "PGs", "Hotels", "Flats", "Rooms", "Quick Stays"]
    
    public init() {
        loadSampleProperties()
        fetchFirestoreProperties()
    }
    
    public var filteredProperties: [StynoProperty] {
        properties.filter { property in
            let matchesCategory = (selectedCategory == "All") || property.category.localizedCaseInsensitiveContains(selectedCategory.dropLast())
            let matchesSearch = searchQuery.isEmpty ||
                property.title.localizedCaseInsensitiveContains(searchQuery) ||
                property.city.localizedCaseInsensitiveContains(searchQuery) ||
                property.area.localizedCaseInsensitiveContains(searchQuery)
            return matchesCategory && matchesSearch
        }
    }
    
    public func fetchFirestoreProperties() {
        #if canImport(FirebaseFirestore)
        let db = Firestore.firestore()
        db.collection("properties").getDocuments { [weak self] snapshot, error in
            guard let self = self, let docs = snapshot?.documents, error == nil else { return }
            let loaded: [StynoProperty] = docs.compactMap { doc in
                let d = doc.data()
                return StynoProperty(
                    id: doc.documentID,
                    title: d["title"] as? String ?? "Cozy Stay",
                    category: d["category"] as? String ?? "Hostel",
                    address: d["address"] as? String ?? "",
                    city: d["city"] as? String ?? "New Delhi",
                    area: d["area"] as? String ?? "South Campus",
                    pricePerMonth: d["pricePerMonth"] as? Double ?? 6500.0,
                    deposit: d["deposit"] as? Double ?? 0.0,
                    rating: d["rating"] as? Double ?? 4.8,
                    reviewCount: d["reviewCount"] as? Int ?? 10,
                    isVerified: d["isVerified"] as? Bool ?? true,
                    ownerName: d["ownerName"] as? String ?? "STYNO Verified Host",
                    ownerPhone: d["ownerPhone"] as? String ?? "+919876543210"
                )
            }
            if !loaded.isEmpty {
                self.properties = loaded
            }
        }
        #endif
    }
    
    private func loadSampleProperties() {
        self.properties = [
            StynoProperty(
                title: "Zolo Starlight Luxury PG",
                category: "PG",
                address: "Koramangala 4th Block",
                city: "Bengaluru",
                area: "Koramangala",
                pricePerMonth: 9500,
                rating: 4.9,
                reviewCount: 42,
                amenities: ["Wi-Fi", "Daily Housekeeping", "Power Backup", "Food Included"]
            ),
            StynoProperty(
                title: "Student Hub Elite Boys Hostel",
                category: "Hostel",
                address: "North Campus, Hudson Lane",
                city: "Delhi",
                area: "Hudson Lane",
                pricePerMonth: 7500,
                rating: 4.7,
                reviewCount: 28,
                amenities: ["Study Room", "AC", "Gym", "High-Speed Wi-Fi"]
            ),
            StynoProperty(
                title: "The Urban Haven 1BHK Flat",
                category: "Flat",
                address: "Cyber City Phase 2",
                city: "Gurugram",
                area: "DLF Phase 2",
                pricePerMonth: 18000,
                rating: 4.8,
                reviewCount: 19,
                amenities: ["Modular Kitchen", "Balcony", "Security", "Parking"]
            ),
            StynoProperty(
                title: "Metro Stays Executive Room",
                category: "Room",
                address: "Powai Lake View",
                city: "Mumbai",
                area: "Powai",
                pricePerMonth: 14000,
                rating: 4.85,
                reviewCount: 31,
                amenities: ["Attached Bath", "Work Desk", "AC", "Laundry"]
            )
        ]
    }
}
