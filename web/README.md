# Styno Responsive Website & Portal

The official, responsive web application for **Styno - India's 0% Brokerage Accommodation Network**.

## Features

- **Same Brand, Logo & Design**: Uses Styno's exact design language (Styno Blue `#1E40AF`, `#2563EB`, Emerald `#059669`, Plus Jakarta Sans typography, and custom SVG logos).
- **All 6 Styno Categories**:
  - 🏨 **Hotel** (Daily / Nightly)
  - 🏢 **Hostel** (Monthly Student & Executive)
  - 🛏️ **PG** (Paying Guest & Co-living)
  - 🚪 **Independent Room** (Monthly / Studio)
  - 🏠 **Furnished Flat** (1/2/3 BHK)
  - ⚡ **Quick Stay** (Hourly Transit Pods: 3h, 6h, 12h, 24h)
- **Nationwide Hierarchy**: 36 Indian States and Union Territories with direct City & Area navigation.
- **Zero Fake Data**: Authentic seed stays (Noida Sector 62, Bengaluru Electronic City, New Delhi Connaught Place, Mumbai Airport T2, Kota Indra Vihar, Pune Hinjewadi, Gurugram Cyber City, Bagaha Station Road, Chennai Airport).
- **Live Owner-to-User Real-time Sync**:
  - Owners can open the **Owner Portal** in the website header, edit prices, toggle availability, or publish brand new stays.
  - Changes instantly synchronize with Firebase Firestore (`styno-stays`) and broadcast to the User section, updating the listings grid immediately without requiring a page reload.
- **Full Booking & Payment Workflow**:
  - Dynamic duration pricing (1, 3, 6, 12 months with transparent discounts).
  - 100% Free 0% Brokerage guarantee.
  - Payment selector: Instant UPI QR, Debit/Credit Cards, and Pay at Check-In.
  - Digital Passcode confirmation generation and stored in "My Bookings".
- **Responsive Across Devices**:
  - **Mobile**: Touch-optimized bottom navigation, quick filters, full-screen modals.
  - **Tablet**: Adaptive 2-column layout and split views.
  - **Desktop**: Full navigation bar, search pill, 3/4-column listing grid, and rich modals.

## Running the Web Version

You can serve this directory using any static file server:
```bash
# Using Python:
python3 -m http.server 8080 --directory web

# Or using Node.js:
npx serve web
```
