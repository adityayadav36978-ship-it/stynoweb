# Styno Responsive Website & Portal

The official, responsive web application for **Styno - India's 0% Brokerage Accommodation Network**, built as the 1:1 functional web counterpart of the STYNO Android application.

## 1:1 Parity Features

- **Official Brand & Identity**: Uses the official `styno_logo.jpg`, Styno Royal Navy (`#0F2B5C`), Electric Blue (`#2563EB`), Emerald Green (`#059669`), and Plus Jakarta Sans typography.
- **Complete Authentication Flow**:
  - Phone OTP Authentication (6-digit simulated SMS token generation, 45-second countdown timer, automatic digit advancing).
  - Email & Password Sign In with password visibility toggle.
  - New Account Sign Up with instant Role Selection (Guest Tenant vs. Property Host/Owner).
  - Forgot Password reset workflow.
  - Quick Social Sign-In buttons (Google & Apple ID).
- **All 6 Styno Categories**:
  - 🏨 **Hotel** (Daily / Nightly)
  - 🏢 **Hostel** (Monthly Student & Executive)
  - 🛏️ **PG** (Paying Guest & Co-living)
  - 🚪 **Independent Room** (Monthly / Studio)
  - 🏠 **Furnished Flat** (1/2/3 BHK)
  - ⚡ **Quick Stay** (Hourly Transit Pods: 3h, 6h, 12h, 24h)
- **User Section with Complete Account Modules**:
  - **Account Overview**: Profile stats, verified credentials, and total brokerage saved counter.
  - **Profile Editor**: Modal to modify full name, email, phone number, and personal bio.
  - **Government KYC Verification**: Aadhaar UIDAI, College/Work ID, Voter ID, or Passport upload with immediate badge verification.
  - **Food & Mess Preferences**: Pure Vegetarian, Eggetarian, Non-Vegetarian, Jain Diet selection, 3-time meals vs. custom mess subscription, and special culinary instructions.
  - **Stay & Living Preferences**: Room sharing preferences (Single, Double, Triple, Private Studio) and curfew tolerance (Strict, Moderate, 24/7 Open).
  - **Complaints & Grievances Desk**: File structured tickets (Wi-Fi, Water/Electricity, Mess Food, Security/Roommate) with real-time status tracking (Pending, In Progress, Resolved).
  - **Role Switching**: Instant toggle between Guest/Tenant mode and Host/Owner mode.
- **Owner Dashboard & Management Tools**:
  - **Fast Reception Check-In Tool**: Instant passcode verification (`STY-XXXX`) that marks guest arrivals as "CHECKED-IN" in real time.
  - **Property Manager**: Create, edit, toggle availability, and delete listings with full pricing and amenity configurations.
  - **Bookings Table**: Real-time guest roster with direct phone contact links and instant settlement figures.
  - **Guest Reviews & Host Reply**: Interactive review feed with direct host reply capability.
  - **Bank & UPI Settlement Settings**: Manage UPI IDs and bank account details for zero-fee payouts.
- **Full Booking, Pricing & Payment Workflow**:
  - Dynamic duration pricing (1, 3, 6, 12 months with transparent discounts).
  - Promo code discounts (e.g. `STYNOFIRST`, `ZEROFEE` for ₹500 instant deduction).
  - 100% Free 0% Brokerage guarantee.
  - Payment options: Instant UPI QR, Debit/Credit Cards, and Pay at Check-In.
  - Digital Passcode confirmation generation and stored in "My Bookings".
- **Nationwide Hierarchy**: 36 Indian States and Union Territories with direct City & Area navigation.
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

