import SwiftUI

public struct HomeView: View {
    @EnvironmentObject var propertyService: PropertyService
    @EnvironmentObject var authService: AuthService
    @State private var selectedProperty: StynoProperty?
    
    public init() {}
    
    public var body: some View {
        NavigationStack {
            ScrollView {
                VStack(spacing: 20) {
                    // Header Bar
                    HStack {
                        VStack(alignment: .leading, spacing: 4) {
                            HStack(spacing: 6) {
                                Image(systemName: "mappin.and.ellipse")
                                    .foregroundColor(.accentColor)
                                Text("New Delhi, India")
                                    .font(.headline)
                                    .fontWeight(.bold)
                            }
                            Text("Find zero-brokerage stays near you")
                                .font(.caption)
                                .foregroundColor(.secondary)
                        }
                        Spacer()
                        
                        // User Avatar or Sign Out
                        Menu {
                            if let user = authService.currentUser {
                                Text(user.displayName ?? user.email ?? "Traveler")
                                if user.isAppleUser {
                                    Label("Apple Sign-In", systemImage: "applelogo")
                                }
                                Divider()
                                Button(role: .destructive) {
                                    authService.signOut()
                                } label: {
                                    Label("Sign Out", systemImage: "rectangle.portrait.and.arrow.right")
                                }
                            }
                        } label: {
                            ZStack {
                                Circle()
                                    .fill(Color.accentColor.opacity(0.15))
                                    .frame(width: 42, height: 42)
                                Image(systemName: authService.currentUser?.isAppleUser == true ? "applelogo" : "person.fill")
                                    .foregroundColor(.accentColor)
                            }
                        }
                    }
                    .padding(.horizontal)
                    .padding(.top, 12)
                    
                    // Search Bar
                    HStack {
                        Image(systemName: "magnifyingglass")
                            .foregroundColor(.secondary)
                        TextField("Search college, metro station, or area...", text: $propertyService.searchQuery)
                    }
                    .padding(14)
                    .background(Color(uiColor: .secondarySystemGroupedBackground))
                    .cornerRadius(14)
                    .padding(.horizontal)
                    
                    // Category Chips
                    ScrollView(.horizontal, showsIndicators: false) {
                        HStack(spacing: 10) {
                            ForEach(propertyService.categories, id: \.self) { cat in
                                Button {
                                    propertyService.selectedCategory = cat
                                } label: {
                                    Text(cat)
                                        .font(.subheadline)
                                        .fontWeight(propertyService.selectedCategory == cat ? .bold : .medium)
                                        .padding(.horizontal, 16)
                                        .padding(.vertical, 8)
                                        .background(propertyService.selectedCategory == cat ? Color.accentColor : Color(uiColor: .secondarySystemGroupedBackground))
                                        .foregroundColor(propertyService.selectedCategory == cat ? .white : .primary)
                                        .cornerRadius(20)
                                }
                            }
                        }
                        .padding(.horizontal)
                    }
                    
                    // Zero Brokerage Guarantee Banner
                    HStack(spacing: 12) {
                        Image(systemName: "checkmark.seal.fill")
                            .font(.title)
                            .foregroundColor(.green)
                        VStack(alignment: .leading, spacing: 2) {
                            Text("100% Zero Brokerage Stays")
                                .font(.subheadline)
                                .fontWeight(.bold)
                            Text("Direct connect with verified property managers & owners")
                                .font(.caption2)
                                .foregroundColor(.secondary)
                        }
                        Spacer()
                    }
                    .padding()
                    .background(Color.green.opacity(0.1))
                    .cornerRadius(16)
                    .padding(.horizontal)
                    
                    // Property Listings
                    VStack(alignment: .leading, spacing: 14) {
                        HStack {
                            Text("Verified Accommodations (\(propertyService.filteredProperties.count))")
                                .font(.headline)
                                .fontWeight(.bold)
                            Spacer()
                        }
                        .padding(.horizontal)
                        
                        ForEach(propertyService.filteredProperties) { property in
                            PropertyCardView(property: property) {
                                selectedProperty = property
                            }
                            .padding(.horizontal)
                        }
                    }
                }
                .padding(.bottom, 24)
            }
            .background(Color(uiColor: .systemGroupedBackground))
            .sheet(item: $selectedProperty) { property in
                PropertyDetailView(property: property)
            }
        }
    }
}

public struct PropertyCardView: View {
    public let property: StynoProperty
    public let onSelect: () -> Void
    
    public var body: some View {
        Button(action: onSelect) {
            VStack(alignment: .leading, spacing: 10) {
                ZStack(alignment: .topLeading) {
                    RoundedRectangle(cornerRadius: 16)
                        .fill(Color(uiColor: .tertiarySystemGroupedBackground))
                        .frame(height: 180)
                    
                    VStack {
                        Spacer()
                        HStack {
                            Spacer()
                            Image(systemName: "building.2.fill")
                                .font(.system(size: 48))
                                .foregroundColor(.secondary.opacity(0.3))
                            Spacer()
                        }
                        Spacer()
                    }
                    
                    HStack {
                        Text(property.category.uppercased())
                            .font(.caption2)
                            .fontWeight(.black)
                            .padding(.horizontal, 8)
                            .padding(.vertical, 4)
                            .background(Color.black.opacity(0.75))
                            .foregroundColor(.white)
                            .clipShape(Capsule())
                        
                        Spacer()
                        
                        HStack(spacing: 4) {
                            Image(systemName: "star.fill")
                                .foregroundColor(.yellow)
                                .font(.caption2)
                            Text(String(format: "%.1f", property.rating))
                                .font(.caption)
                                .fontWeight(.bold)
                                .foregroundColor(.white)
                        }
                        .padding(.horizontal, 8)
                        .padding(.vertical, 4)
                        .background(Color.black.opacity(0.75))
                        .clipShape(Capsule())
                    }
                    .padding(12)
                }
                
                VStack(alignment: .leading, spacing: 6) {
                    HStack {
                        Text(property.title)
                            .font(.headline)
                            .fontWeight(.bold)
                            .foregroundColor(.primary)
                            .lineLimit(1)
                        Spacer()
                        if property.isVerified {
                            Image(systemName: "checkmark.seal.fill")
                                .foregroundColor(.blue)
                        }
                    }
                    
                    Text("\(property.area), \(property.city)")
                        .font(.subheadline)
                        .foregroundColor(.secondary)
                    
                    HStack(alignment: .firstTextBaseline) {
                        Text("₹\(Int(property.pricePerMonth))")
                            .font(.title3)
                            .fontWeight(.black)
                            .foregroundColor(.accentColor)
                        Text("/ month")
                            .font(.caption)
                            .foregroundColor(.secondary)
                        
                        Spacer()
                        
                        Text("Zero Brokerage")
                            .font(.caption2)
                            .fontWeight(.bold)
                            .foregroundColor(.green)
                            .padding(.horizontal, 8)
                            .padding(.vertical, 3)
                            .background(Color.green.opacity(0.12))
                            .clipShape(Capsule())
                    }
                }
                .padding(.horizontal, 14)
                .padding(.bottom, 14)
            }
            .background(Color(uiColor: .secondarySystemGroupedBackground))
            .cornerRadius(18)
            .shadow(color: Color.black.opacity(0.04), radius: 8, y: 3)
        }
        .buttonStyle(.plain)
    }
}
