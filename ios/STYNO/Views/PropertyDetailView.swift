import SwiftUI

public struct PropertyDetailView: View {
    public let property: StynoProperty
    @Environment(\.dismiss) var dismiss
    @State private var showingBookingConfirmation = false
    
    public init(property: StynoProperty) {
        self.property = property
    }
    
    public var body: some View {
        NavigationStack {
            ScrollView {
                VStack(alignment: .leading, spacing: 18) {
                    // Hero Image Banner
                    ZStack(alignment: .topTrailing) {
                        RoundedRectangle(cornerRadius: 0)
                            .fill(Color(uiColor: .tertiarySystemGroupedBackground))
                            .frame(height: 260)
                            .overlay(
                                Image(systemName: "photo.on.rectangle.angled")
                                    .font(.system(size: 60))
                                    .foregroundColor(.secondary.opacity(0.3))
                            )
                        
                        Button {
                            dismiss()
                        } label: {
                            Image(systemName: "xmark.circle.fill")
                                .font(.title)
                                .foregroundColor(.white.opacity(0.85))
                                .padding()
                        }
                    }
                    
                    VStack(alignment: .leading, spacing: 16) {
                        // Title & Rating
                        HStack(alignment: .top) {
                            VStack(alignment: .leading, spacing: 6) {
                                Text(property.title)
                                    .font(.title2)
                                    .fontWeight(.bold)
                                Text("\(property.address), \(property.city)")
                                    .font(.subheadline)
                                    .foregroundColor(.secondary)
                            }
                            Spacer()
                            
                            HStack(spacing: 4) {
                                Image(systemName: "star.fill")
                                    .foregroundColor(.yellow)
                                Text(String(format: "%.1f", property.rating))
                                    .fontWeight(.bold)
                            }
                            .padding(.horizontal, 10)
                            .padding(.vertical, 6)
                            .background(Color.yellow.opacity(0.2))
                            .clipShape(Capsule())
                        }
                        
                        Divider()
                        
                        // Price Banner
                        HStack {
                            VStack(alignment: .leading, spacing: 2) {
                                Text("Monthly Rent")
                                    .font(.caption)
                                    .foregroundColor(.secondary)
                                Text("₹\(Int(property.pricePerMonth))")
                                    .font(.title)
                                    .fontWeight(.black)
                                    .foregroundColor(.accentColor)
                            }
                            Spacer()
                            
                            VStack(alignment: .trailing, spacing: 2) {
                                Text("Security Deposit")
                                    .font(.caption)
                                    .foregroundColor(.secondary)
                                Text(property.deposit > 0 ? "₹\(Int(property.deposit))" : "Zero Deposit")
                                    .font(.headline)
                                    .foregroundColor(.primary)
                            }
                        }
                        .padding()
                        .background(Color(uiColor: .secondarySystemGroupedBackground))
                        .cornerRadius(14)
                        
                        // Amenities
                        Text("Amenities & Features")
                            .font(.headline)
                            .fontWeight(.bold)
                        
                        LazyVGrid(columns: [GridItem(.flexible()), GridItem(.flexible())], spacing: 10) {
                            ForEach(property.amenities, id: \.self) { amenity in
                                HStack {
                                    Image(systemName: "checkmark.circle.fill")
                                        .foregroundColor(.green)
                                    Text(amenity)
                                        .font(.subheadline)
                                    Spacer()
                                }
                                .padding(10)
                                .background(Color(uiColor: .secondarySystemGroupedBackground))
                                .cornerRadius(10)
                            }
                        }
                        
                        // Host Information
                        Text("Verified Host & Manager")
                            .font(.headline)
                            .fontWeight(.bold)
                        
                        HStack(spacing: 14) {
                            ZStack {
                                Circle()
                                    .fill(Color.blue.opacity(0.15))
                                    .frame(width: 50, height: 50)
                                Image(systemName: "person.badge.shield.checkmark.fill")
                                    .foregroundColor(.blue)
                                    .font(.title3)
                            }
                            
                            VStack(alignment: .leading, spacing: 4) {
                                Text(property.ownerName)
                                    .font(.headline)
                                Text("STYNO Partner • Verified ID & Property Title")
                                    .font(.caption)
                                    .foregroundColor(.secondary)
                            }
                            Spacer()
                        }
                        .padding()
                        .background(Color(uiColor: .secondarySystemGroupedBackground))
                        .cornerRadius(14)
                        
                        // Description
                        Text("About this Stay")
                            .font(.headline)
                            .fontWeight(.bold)
                        Text(property.description)
                            .font(.body)
                            .foregroundColor(.secondary)
                    }
                    .padding(.horizontal)
                }
                .padding(.bottom, 100)
            }
            .overlay(alignment: .bottom) {
                // Instant Book Bottom Bar
                VStack(spacing: 0) {
                    Divider()
                    HStack {
                        VStack(alignment: .leading, spacing: 2) {
                            Text("Total Rent")
                                .font(.caption2)
                                .foregroundColor(.secondary)
                            Text("₹\(Int(property.pricePerMonth)) / mo")
                                .font(.headline)
                                .fontWeight(.bold)
                        }
                        Spacer()
                        
                        Button {
                            showingBookingConfirmation = true
                        } label: {
                            Text("Instant Book Now")
                                .font(.headline)
                                .foregroundColor(.white)
                                .padding(.horizontal, 24)
                                .padding(.vertical, 14)
                                .background(Color.accentColor)
                                .cornerRadius(14)
                        }
                    }
                    .padding()
                    .background(Color(uiColor: .systemBackground))
                }
            }
            .navigationBarHidden(true)
            .alert("Booking Confirmed!", isPresented: $showingBookingConfirmation) {
                Button("Great!", role: .cancel) {
                    dismiss()
                }
            } message: {
                Text("Your reservation request for \(property.title) has been registered with 0% brokerage.")
            }
        }
    }
}
