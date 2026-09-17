/**
 * STYNO STAYS - OFFICIAL RESPONSIVE WEB APPLICATION
 * Zero Brokerage Accommodation Network
 * Real-time Owner-to-User Synchronization Engine
 */

// --------------------------------------------------------------------------
// 1. AUTHENTIC STYNO SEED DATA (Exact match to Android App Database)
// --------------------------------------------------------------------------

const STYNO_SEED_PROPERTIES = [
  {
    id: "prop-hostel-01",
    name: "Styno Orchid Girls Elite Hostel",
    propertyType: "HOSTEL",
    genderSuitability: "GIRLS_ONLY",
    description: "Premium student living with biometric 24/7 security, high-speed fiber internet, study lounge, and home-style 3-time North & South Indian meals. Walking distance from major colleges.",
    address: "Plot B-14, Sector 62",
    city: "Noida",
    state: "Uttar Pradesh",
    area: "Sector 62",
    pincode: "201309",
    latitude: 28.6280,
    longitude: 77.3649,
    startingPrice: 6500,
    durationType: "MONTHLY",
    rating: 4.8,
    reviewCount: 142,
    isVerified: true,
    isInstantBookable: true,
    isZeroBrokerage: true,
    featuredBadge: "Girls Elite Campus",
    images: [
      "https://images.unsplash.com/photo-1555854877-bab0e564b8d5?w=800&q=80",
      "https://images.unsplash.com/photo-1595526114035-0d45ed16cfbf?w=800&q=80",
      "https://images.unsplash.com/photo-1522771739844-6a9f6d5f14af?w=800&q=80"
    ],
    amenities: ["High-Speed Wi-Fi", "Air Conditioner", "3-Time Meals", "Attached Washroom", "24/7 Power Backup", "Biometric Security", "Daily Housekeeping", "Laundry Access"],
    roomOptions: [
      { type: "Triple Sharing (AC)", price: 6500, bedsAvailable: 4, deposit: 6500 },
      { type: "Double Sharing (AC)", price: 8500, bedsAvailable: 2, deposit: 8500 },
      { type: "Single Private Room", price: 12500, bedsAvailable: 1, deposit: 12500 }
    ],
    owner: {
      name: "Dr. Shalini Srivastava",
      phone: "+91 98112 34567",
      verified: true,
      experience: "8 years hosting"
    },
    rules: ["Gate closes at 10:00 PM", "Visitor entry till 7:00 PM", "Biometric punch required", "Quiet study hours after 10 PM"]
  },
  {
    id: "prop-hostel-02",
    name: "Styno Apex Boys Techno Hostel",
    propertyType: "HOSTEL",
    genderSuitability: "BOYS_ONLY",
    description: "Modern tech-enabled boys hostel with gaming/chill zone, ultra-fast 300 Mbps Wi-Fi, ergonomic study stations, and gymnasium. Ideal for engineering students and young tech professionals.",
    address: "Neo Town Road, Electronic City Phase 1",
    city: "Bengaluru",
    state: "Karnataka",
    area: "Electronic City",
    pincode: "560100",
    latitude: 12.8399,
    longitude: 77.6770,
    startingPrice: 7200,
    durationType: "MONTHLY",
    rating: 4.7,
    reviewCount: 98,
    isVerified: true,
    isInstantBookable: true,
    isZeroBrokerage: true,
    featuredBadge: "Tech Corridor Fav",
    images: [
      "https://images.unsplash.com/photo-1590490360182-c33d57733427?w=800&q=80",
      "https://images.unsplash.com/photo-1586023492125-27b2c045efd7?w=800&q=80"
    ],
    amenities: ["High-Speed Wi-Fi", "Air Conditioner", "Gymnasium", "24/7 Power Backup", "Attached Washroom", "Gaming Zone", "RO Water", "CCTV Security"],
    roomOptions: [
      { type: "Triple Sharing", price: 7200, bedsAvailable: 5, deposit: 7200 },
      { type: "Twin Sharing (AC)", price: 9500, bedsAvailable: 3, deposit: 9500 },
      { type: "Private Studio (AC)", price: 14000, bedsAvailable: 1, deposit: 14000 }
    ],
    owner: {
      name: "Karthik Gowda",
      phone: "+91 97400 88991",
      verified: true,
      experience: "5 years hosting"
    },
    rules: ["Gate open 24/7 with keycard", "Zero smoking indoors", "Visitors allowed in lounge"]
  },
  {
    id: "prop-hotel-01",
    name: "Styno Grand Boulevard Luxury Hotel",
    propertyType: "HOTEL",
    genderSuitability: "ALL",
    description: "Centrally located luxury boutique hotel in the heart of Connaught Place. Features fine dining, valet parking, 24/7 room service, and executive business meeting lounges.",
    address: "Block M, Outer Circle, Connaught Place",
    city: "New Delhi",
    state: "Delhi",
    area: "Connaught Place",
    pincode: "110001",
    latitude: 28.6315,
    longitude: 77.2167,
    startingPrice: 2499,
    durationType: "DAILY",
    rating: 4.9,
    reviewCount: 310,
    isVerified: true,
    isInstantBookable: true,
    isZeroBrokerage: true,
    featuredBadge: "Top Rated Hotel",
    images: [
      "https://images.unsplash.com/photo-1566073771259-6a8506099945?w=800&q=80",
      "https://images.unsplash.com/photo-1582719478250-c89cae4dc85b?w=800&q=80"
    ],
    amenities: ["Air Conditioner", "Free Breakfast Buffet", "High-Speed Wi-Fi", "Valet Parking", "Room Service 24h", "Attached Luxury Bath", "Smart TV", "Mini Fridge"],
    roomOptions: [
      { type: "Deluxe King Room", price: 2499, bedsAvailable: 6, deposit: 0 },
      { type: "Executive Suite", price: 4299, bedsAvailable: 2, deposit: 0 }
    ],
    owner: {
      name: "Boulevard Hospitality Pvt Ltd",
      phone: "+91 11 4321 0000",
      verified: true,
      experience: "Styno Premier Partner"
    },
    rules: ["Check-in: 12:00 PM", "Check-out: 11:00 AM", "Govt Photo ID required for all guests"]
  },
  {
    id: "prop-quick-01",
    name: "Styno Transit Pods & Quick Stay",
    propertyType: "QUICK_STAY",
    genderSuitability: "ALL",
    description: "Soundproof futuristic Japanese-style sleep pods directly opposite Airport Terminal 2. Perfect for flight layovers, quick power naps, hot showers, and high-speed device recharge.",
    address: "Sahar Airport Road, Near T2 Departure Gate",
    city: "Mumbai",
    state: "Maharashtra",
    area: "Andheri East",
    pincode: "400099",
    latitude: 19.0968,
    longitude: 72.8744,
    startingPrice: 399,
    durationType: "HOURLY",
    rating: 4.9,
    reviewCount: 420,
    isVerified: true,
    isInstantBookable: true,
    isZeroBrokerage: true,
    featuredBadge: "Instant Airport Transit",
    images: [
      "https://images.unsplash.com/photo-1596394516093-501ba68a0ba6?w=800&q=80",
      "https://images.unsplash.com/photo-1512918728675-ed5a9ecdebfd?w=800&q=80"
    ],
    amenities: ["Climate Controlled Pod", "Hot Power Shower", "Luggage Storage Locker", "300 Mbps Wi-Fi", "USB Fast Charger", "Noise Cancelling Headsets", "Complimentary Espresso"],
    roomOptions: [
      { type: "3 Hours Layover Pod", price: 399, bedsAvailable: 8, deposit: 0 },
      { type: "6 Hours Transit Sleep Pod", price: 699, bedsAvailable: 5, deposit: 0 },
      { type: "12 Hours Day/Night Pod", price: 999, bedsAvailable: 4, deposit: 0 },
      { type: "24 Hours Full Day Stay", price: 1599, bedsAvailable: 3, deposit: 0 }
    ],
    owner: {
      name: "Styno Transit Hubs Operations",
      phone: "+91 22 2899 1122",
      verified: true,
      experience: "Official Transit Station"
    },
    rules: ["Instant barcode entry", "Shoe lockers provided at pod zone", "Silent zone policy"]
  },
  {
    id: "prop-pg-01",
    name: "Styno Urban Living Premium PG",
    propertyType: "PG",
    genderSuitability: "BOYS_ONLY",
    description: "Designed for competitive exam aspirants (IIT-JEE & NEET). Ergonomic study table, silent library with AC, hygienic nutritious home meals, and zero brokerage.",
    address: "A-32, Rajiv Gandhi Nagar",
    city: "Kota",
    state: "Rajasthan",
    area: "Indra Vihar",
    pincode: "324005",
    latitude: 25.1389,
    longitude: 75.8458,
    startingPrice: 7800,
    durationType: "MONTHLY",
    rating: 4.7,
    reviewCount: 165,
    isVerified: true,
    isInstantBookable: true,
    isZeroBrokerage: true,
    featuredBadge: "Aspirant's Choice",
    images: [
      "https://images.unsplash.com/photo-1595526114035-0d45ed16cfbf?w=800&q=80",
      "https://images.unsplash.com/photo-1522771739844-6a9f6d5f14af?w=800&q=80"
    ],
    amenities: ["High-Speed Wi-Fi", "Air Conditioner", "3-Time Hygienic Meals", "Study Library Room", "24/7 Power Backup", "Doctor on Call", "Daily Cleaning"],
    roomOptions: [
      { type: "Double Sharing with AC", price: 7800, bedsAvailable: 6, deposit: 7800 },
      { type: "Single Private Study Room", price: 11500, bedsAvailable: 2, deposit: 11500 }
    ],
    owner: {
      name: "Rajendra Meena",
      phone: "+91 94140 12345",
      verified: true,
      experience: "10 years in Kota"
    },
    rules: ["Strict silent study hours", "Counsellor session on weekends", "No late night outings without guardian consent"]
  },
  {
    id: "prop-flat-01",
    name: "Styno Heights 2 BHK Furnished Apartment",
    propertyType: "FLAT",
    genderSuitability: "FAMILY",
    description: "Spacious fully furnished 2 BHK apartment inside a gated society with swimming pool, covered parking, clubhouse, modular kitchen, and pipeline gas.",
    address: "Tower 4, Green Valley Society, Phase 1",
    city: "Pune",
    state: "Maharashtra",
    area: "Hinjewadi",
    pincode: "411057",
    latitude: 18.5913,
    longitude: 73.7389,
    startingPrice: 22000,
    durationType: "MONTHLY",
    rating: 4.8,
    reviewCount: 64,
    isVerified: true,
    isInstantBookable: true,
    isZeroBrokerage: true,
    featuredBadge: "0% Brokerage Flat",
    images: [
      "https://images.unsplash.com/photo-1502672260266-1c1ef2d93688?w=800&q=80",
      "https://images.unsplash.com/photo-1560448204-e02f11c3d0e2?w=800&q=80"
    ],
    amenities: ["Modular Kitchen", "Air Conditioner", "Covered Car Parking", "Swimming Pool & Gym", "24/7 Security", "Piped Gas", "High-Speed Wi-Fi"],
    roomOptions: [
      { type: "Full 2 BHK Apartment", price: 22000, bedsAvailable: 1, deposit: 44000 }
    ],
    owner: {
      name: "Anand Deshpande",
      phone: "+91 98220 54321",
      verified: true,
      experience: "Direct landlord"
    },
    rules: ["Registered rent agreement included", "Pets allowed with prior consent"]
  },
  {
    id: "prop-room-01",
    name: "Styno Studio Independent Room with Balcony",
    propertyType: "ROOM",
    genderSuitability: "ALL",
    description: "Independent studio room with private entrance, attached modern washroom, large balcony overlooking greenery, mini pantry, and zero landlord interference.",
    address: "House 182, Phase 3, Near Cyber Hub",
    city: "Gurugram",
    state: "Haryana",
    area: "DLF Cyber City",
    pincode: "122002",
    latitude: 28.4952,
    longitude: 77.0895,
    startingPrice: 11000,
    durationType: "MONTHLY",
    rating: 4.6,
    reviewCount: 88,
    isVerified: true,
    isInstantBookable: true,
    isZeroBrokerage: true,
    featuredBadge: "Cyber City Studio",
    images: [
      "https://images.unsplash.com/photo-1598928506311-c55ded91a20c?w=800&q=80",
      "https://images.unsplash.com/photo-1505691938895-1758d7feb511?w=800&q=80"
    ],
    amenities: ["Air Conditioner", "Attached Washroom", "Private Balcony", "High-Speed Wi-Fi", "Mini Fridge", "Independent Key", "Power Backup"],
    roomOptions: [
      { type: "Studio Room with Balcony", price: 11000, bedsAvailable: 1, deposit: 11000 }
    ],
    owner: {
      name: "Col. Sanjeev Bakshi (Retd.)",
      phone: "+91 98100 99887",
      verified: true,
      experience: "Verified Owner"
    },
    rules: ["No loud music after 11 PM", "Keys provided directly on move-in"]
  },
  {
    id: "prop-bagaha-01",
    name: "Styno Valmiki Heritage Residency & PG",
    propertyType: "PG",
    genderSuitability: "ALL",
    description: "Affordable and peaceful accommodation close to nature and town center in Bagaha (West Champaran). Clean rooms, RO drinking water, power backup, and fresh home-cooked food.",
    address: "Station Road, Near Valmiki Nagar Junction",
    city: "Bagaha",
    state: "Bihar",
    area: "Station Road",
    pincode: "845105",
    latitude: 27.0988,
    longitude: 84.0901,
    startingPrice: 3800,
    durationType: "MONTHLY",
    rating: 4.8,
    reviewCount: 45,
    isVerified: true,
    isInstantBookable: true,
    isZeroBrokerage: true,
    featuredBadge: "Heritage PG Stay",
    images: [
      "https://images.unsplash.com/photo-1522771739844-6a9f6d5f14af?w=800&q=80",
      "https://images.unsplash.com/photo-1555854877-bab0e564b8d5?w=800&q=80"
    ],
    amenities: ["3-Time Meals", "High-Speed Wi-Fi", "Attached Washroom", "Power Backup", "RO Water", "Parking Space"],
    roomOptions: [
      { type: "Double Sharing", price: 3800, bedsAvailable: 4, deposit: 3800 },
      { type: "Single Room", price: 5500, bedsAvailable: 2, deposit: 5500 }
    ],
    owner: {
      name: "Rameshwar Prasad Yadav",
      phone: "+91 94312 34567",
      verified: true,
      experience: "Local Verified Host"
    },
    rules: ["Pure vegetarian mess available", "Family friendly environment"]
  },
  {
    id: "prop-quick-02",
    name: "Styno Airport Express Sleep Pods",
    propertyType: "QUICK_STAY",
    genderSuitability: "ALL",
    description: "Direct airport terminal sleep capsules with power charging, fresh linen, sanitized eye masks, and flight status displays.",
    address: "GST Road, Opposite International Terminal",
    city: "Chennai",
    state: "Tamil Nadu",
    area: "Meenambakkam",
    pincode: "600027",
    latitude: 12.9856,
    longitude: 80.1636,
    startingPrice: 349,
    durationType: "HOURLY",
    rating: 4.8,
    reviewCount: 220,
    isVerified: true,
    isInstantBookable: true,
    isZeroBrokerage: true,
    featuredBadge: "Transit Capsule",
    images: [
      "https://images.unsplash.com/photo-1596394516093-501ba68a0ba6?w=800&q=80"
    ],
    amenities: ["Climate Controlled Pod", "High-Speed Wi-Fi", "Flight Board Display", "Hot Shower", "Luggage Locker"],
    roomOptions: [
      { type: "3h Express Nap", price: 349, bedsAvailable: 6, deposit: 0 },
      { type: "6h Transit Rest", price: 599, bedsAvailable: 4, deposit: 0 },
      { type: "12h Stay Slot", price: 899, bedsAvailable: 2, deposit: 0 }
    ],
    owner: {
      name: "Styno Express Pods Chennai",
      phone: "+91 44 2256 0011",
      verified: true,
      experience: "Verified Transit Hub"
    },
    rules: ["Flight ticket verification at check-in", "Automated barcode entry"]
  }
];

// --------------------------------------------------------------------------
// 2. INDIA 36 STATES & UTs HIERARCHICAL GEOGRAPHIC DATA
// --------------------------------------------------------------------------

const INDIA_GEOGRAPHIC_DATA = [
  { state: "Uttar Pradesh", isUT: false, cities: ["Noida", "Greater Noida", "Lucknow", "Kanpur", "Varanasi", "Prayagraj", "Agra", "Meerut", "Ghaziabad"] },
  { state: "Karnataka", isUT: false, cities: ["Bengaluru", "Mysuru", "Mangaluru", "Hubballi", "Belagavi"] },
  { state: "Delhi", isUT: true, cities: ["New Delhi", "North Delhi", "South Delhi", "West Delhi", "Dwarka", "Rohini"] },
  { state: "Maharashtra", isUT: false, cities: ["Mumbai", "Pune", "Nagpur", "Thane", "Nashik", "Navi Mumbai", "Aurangabad"] },
  { state: "Rajasthan", isUT: false, cities: ["Kota", "Jaipur", "Jodhpur", "Udaipur", "Ajmer", "Bikaner"] },
  { state: "Haryana", isUT: false, cities: ["Gurugram", "Faridabad", "Panipat", "Ambala", "Karnal", "Sonipat"] },
  { state: "Bihar", isUT: false, cities: ["Patna", "Bagaha", "Gaya", "Muzaffarpur", "Bhagalpur", "Darbhanga", "Purnia"] },
  { state: "Tamil Nadu", isUT: false, cities: ["Chennai", "Coimbatore", "Madurai", "Tiruchirappalli", "Salem"] },
  { state: "Telangana", isUT: false, cities: ["Hyderabad", "Warangal", "Nizamabad", "Karimnagar"] },
  { state: "West Bengal", isUT: false, cities: ["Kolkata", "Howrah", "Durgapur", "Asansol", "Siliguri"] },
  { state: "Gujarat", isUT: false, cities: ["Ahmedabad", "Surat", "Vadodara", "Rajkot", "Gandhinagar"] },
  { state: "Madhya Pradesh", isUT: false, cities: ["Indore", "Bhopal", "Jabalpur", "Gwalior", "Ujjain"] },
  { state: "Kerala", isUT: false, cities: ["Kochi", "Thiruvananthapuram", "Kozhikode", "Thrissur"] },
  { state: "Punjab", isUT: false, cities: ["Ludhiana", "Amritsar", "Jalandhar", "Patiala", "Mohali"] },
  { state: "Chandigarh", isUT: true, cities: ["Chandigarh"] },
  { state: "Andhra Pradesh", isUT: false, cities: ["Visakhapatnam", "Vijayawada", "Guntur", "Tirupati"] },
  { state: "Odisha", isUT: false, cities: ["Bhubaneswar", "Cuttack", "Rourkela", "Puri"] },
  { state: "Assam", isUT: false, cities: ["Guwahati", "Silchar", "Dibrugarh", "Jorhat"] },
  { state: "Jharkhand", isUT: false, cities: ["Ranchi", "Jamshedpur", "Dhanbad", "Bokaro"] },
  { state: "Uttarakhand", isUT: false, cities: ["Dehradun", "Haridwar", "Rishikesh", "Haldwani"] },
  { state: "Himachal Pradesh", isUT: false, cities: ["Shimla", "Dharamshala", "Manali", "Solan"] },
  { state: "Goa", isUT: false, cities: ["Panaji", "Margao", "Vasco da Gama", "Mapusa"] },
  { state: "Jammu and Kashmir", isUT: true, cities: ["Srinagar", "Jammu"] },
  { state: "Ladakh", isUT: true, cities: ["Leh", "Kargil"] },
  { state: "Puducherry", isUT: true, cities: ["Puducherry", "Karaikal"] },
  { state: "Tripura", isUT: false, cities: ["Agartala"] },
  { state: "Meghalaya", isUT: false, cities: ["Shillong"] },
  { state: "Manipur", isUT: false, cities: ["Imphal"] },
  { state: "Nagaland", isUT: false, cities: ["Kohima", "Dimapur"] },
  { state: "Mizoram", isUT: false, cities: ["Aizawl"] },
  { state: "Arunachal Pradesh", isUT: false, cities: ["Itanagar"] },
  { state: "Sikkim", isUT: false, cities: ["Gangtok"] },
  { state: "Chhattisgarh", isUT: false, cities: ["Raipur", "Bhilai", "Bilaspur"] },
  { state: "Andaman and Nicobar Islands", isUT: true, cities: ["Port Blair"] },
  { state: "Dadra and Nagar Haveli and Daman and Diu", isUT: true, cities: ["Daman", "Silvassa"] },
  { state: "Lakshadweep", isUT: true, cities: ["Kavaratti"] }
];

// --------------------------------------------------------------------------
// 3. CATEGORIES DEFINITION (Exact 6 Categories from Styno App)
// --------------------------------------------------------------------------

const STYNO_CATEGORIES = [
  { key: null, name: "All Stays", icon: "explore", emoji: "✨" },
  { key: "HOTEL", name: "Hotels", icon: "hotel", emoji: "🏨" },
  { key: "HOSTEL", name: "Hostels", icon: "apartment", emoji: "🏢" },
  { key: "PG", name: "PGs & Co-Living", icon: "bed", emoji: "🛏️" },
  { key: "ROOM", name: "Independent Rooms", icon: "meeting_room", emoji: "🚪" },
  { key: "FLAT", name: "Furnished Flats", icon: "home", emoji: "🏠" },
  { key: "QUICK_STAY", name: "Quick Stay (Hourly)", icon: "bolt", emoji: "⚡" }
];

// --------------------------------------------------------------------------
// 4. REAL-TIME DATA STORE & SYNCHRONIZATION ENGINE
// --------------------------------------------------------------------------

class StynoDataStore {
  constructor() {
    this.storageKeyProperties = "styno_properties_db_v1";
    this.storageKeyOwnerCustom = "styno_owner_custom_listings_v1";
    this.storageKeyBookings = "styno_user_bookings_v1";
    this.storageKeySaved = "styno_user_saved_v1";
    
    // Initialize Local Storage with seed properties if not present
    this.initDatabase();

    // Listen for storage events (cross-tab sync)
    window.addEventListener("storage", (e) => {
      if (e.key === this.storageKeyOwnerCustom || e.key === this.storageKeyProperties) {
        this.notifySyncListeners();
      }
    });

    // Initialize Firebase Firestore connection if SDK is available
    this.initFirebase();
  }

  initDatabase() {
    if (!localStorage.getItem(this.storageKeyProperties)) {
      localStorage.setItem(this.storageKeyProperties, JSON.stringify(STYNO_SEED_PROPERTIES));
    }
    if (!localStorage.getItem(this.storageKeyOwnerCustom)) {
      localStorage.setItem(this.storageKeyOwnerCustom, JSON.stringify([]));
    }
    if (!localStorage.getItem(this.storageKeyBookings)) {
      localStorage.setItem(this.storageKeyBookings, JSON.stringify([]));
    }
    if (!localStorage.getItem(this.storageKeySaved)) {
      localStorage.setItem(this.storageKeySaved, JSON.stringify([]));
    }
  }

  initFirebase() {
    try {
      if (window.firebase && !firebase.apps.length) {
        firebase.initializeApp({
          projectId: "styno-stays",
          apiKey: "AIzaSyD-stynoProductionApiKeyAndroid123456",
          authDomain: "styno-stays.firebaseapp.com"
        });
        this.firestore = firebase.firestore();
        console.log("Styno Firebase Firestore initialized successfully");
        
        // Listen to live updates from cloud collection
        this.firestore.collection("properties").onSnapshot((snapshot) => {
          if (!snapshot.empty) {
            const cloudProps = [];
            snapshot.forEach(doc => cloudProps.push({ id: doc.id, ...doc.data() }));
            this.mergeCloudProperties(cloudProps);
          }
        }, (err) => {
          console.log("Firestore cloud snapshot note: offline/mock fallback active", err.message);
        });
      }
    } catch (e) {
      console.log("Firebase init note:", e.message);
    }
  }

  mergeCloudProperties(cloudProps) {
    if (!cloudProps || !cloudProps.length) return;
    const current = this.getAllProperties();
    const map = new Map();
    current.forEach(p => map.set(p.id, p));
    cloudProps.forEach(cp => map.set(cp.id, { ...map.get(cp.id), ...cp }));
    localStorage.setItem(this.storageKeyProperties, JSON.stringify(Array.from(map.values())));
    this.notifySyncListeners();
  }

  /**
   * Retrieves all properties (Combined Seed + Owner custom properties)
   * Priority: Owner-created / updated listings take precedence
   */
  getAllProperties() {
    const base = JSON.parse(localStorage.getItem(this.storageKeyProperties) || "[]");
    const ownerCustom = JSON.parse(localStorage.getItem(this.storageKeyOwnerCustom) || "[]");
    
    // Combine with distinct by ID (Owner custom items override base)
    const combinedMap = new Map();
    base.forEach(p => combinedMap.set(p.id, p));
    ownerCustom.forEach(p => combinedMap.set(p.id, p));
    
    return Array.from(combinedMap.values());
  }

  /**
   * Returns all listings owned/created by the property owner
   */
  getOwnerListings() {
    const custom = JSON.parse(localStorage.getItem(this.storageKeyOwnerCustom) || "[]");
    // Also include properties from seed that have an owner contact matching owner mode
    const all = this.getAllProperties();
    return all.filter(p => p.isOwnerCreated || custom.some(c => c.id === p.id));
  }

  /**
   * PUBLISH / UPDATE PROPERTY (Owner Portal -> Real-time sync to User section)
   */
  savePropertyListing(propData) {
    const ownerList = JSON.parse(localStorage.getItem(this.storageKeyOwnerCustom) || "[]");
    
    let isNew = false;
    let index = ownerList.findIndex(p => p.id === propData.id);
    if (index >= 0) {
      ownerList[index] = { ...ownerList[index], ...propData, updatedAt: Date.now() };
    } else {
      isNew = true;
      if (!propData.id) {
        propData.id = "styno-owner-" + Date.now().toString(36);
      }
      propData.isOwnerCreated = true;
      propData.isVerified = true;
      propData.isZeroBrokerage = true;
      propData.rating = 5.0;
      propData.reviewCount = 1;
      propData.createdAt = Date.now();
      ownerList.unshift(propData);
    }

    // 1. Save to localStorage
    localStorage.setItem(this.storageKeyOwnerCustom, JSON.stringify(ownerList));

    // 2. Also update base properties map
    const all = this.getAllProperties();
    const map = new Map(all.map(p => [p.id, p]));
    map.set(propData.id, propData);
    localStorage.setItem(this.storageKeyProperties, JSON.stringify(Array.from(map.values())));

    // 3. Sync to Cloud Firestore if connected
    if (this.firestore) {
      try {
        this.firestore.collection("properties").doc(propData.id).set(propData, { merge: true })
          .then(() => console.log("Cloud Firestore sync completed for " + propData.id))
          .catch(e => console.log("Firestore cloud sync queued: ", e.message));
      } catch (e) {}
    }

    // 4. Fire reactive sync broadcast so User section re-renders immediately!
    this.notifySyncListeners(propData.name, isNew ? "published" : "updated");
    return propData;
  }

  /**
   * Updates pricing or availability of an existing stay inline
   */
  updatePropertyPricing(propertyId, newPrice, durationType) {
    const all = this.getAllProperties();
    const prop = all.find(p => p.id === propertyId);
    if (prop) {
      prop.startingPrice = Number(newPrice);
      if (durationType) prop.durationType = durationType;
      this.savePropertyListing(prop);
    }
  }

  /**
   * Deletes a property listing
   */
  deletePropertyListing(propertyId) {
    let ownerList = JSON.parse(localStorage.getItem(this.storageKeyOwnerCustom) || "[]");
    ownerList = ownerList.filter(p => p.id !== propertyId);
    localStorage.setItem(this.storageKeyOwnerCustom, JSON.stringify(ownerList));

    let base = JSON.parse(localStorage.getItem(this.storageKeyProperties) || "[]");
    base = base.filter(p => p.id !== propertyId);
    localStorage.setItem(this.storageKeyProperties, JSON.stringify(base));

    this.notifySyncListeners("Property", "removed");
  }

  // Bookings Management
  getBookings() {
    return JSON.parse(localStorage.getItem(this.storageKeyBookings) || "[]");
  }

  saveBooking(booking) {
    const list = this.getBookings();
    list.unshift(booking);
    localStorage.setItem(this.storageKeyBookings, JSON.stringify(list));
    
    if (this.firestore) {
      try {
        this.firestore.collection("bookings").doc(booking.id).set(booking);
      } catch (e) {}
    }
    return booking;
  }

  // Saved Stays Management
  getSavedPropertyIds() {
    return JSON.parse(localStorage.getItem(this.storageKeySaved) || "[]");
  }

  toggleSaveProperty(propertyId) {
    let saved = this.getSavedPropertyIds();
    const idx = saved.indexOf(propertyId);
    let isSaved = false;
    if (idx >= 0) {
      saved.splice(idx, 1);
      isSaved = false;
    } else {
      saved.push(propertyId);
      isSaved = true;
    }
    localStorage.setItem(this.storageKeySaved, JSON.stringify(saved));
    return isSaved;
  }

  // Real-time Event Broadcaster
  notifySyncListeners(propertyName = "Listing", action = "synced") {
    const event = new CustomEvent("styno_sync_event", {
      detail: { propertyName, action, timestamp: Date.now() }
    });
    window.dispatchEvent(event);
  }
}

// Instantiate Global Data Store
const StynoDB = new StynoDataStore();

// --------------------------------------------------------------------------
// 5. APPLICATION STATE & CONTROLLER
// --------------------------------------------------------------------------

const AppState = {
  selectedCategory: null,
  selectedState: null,
  selectedCity: null,
  selectedArea: null,
  searchQuery: "",
  genderFilter: null,
  maxBudget: 35000,
  activeAmenities: new Set(),
  sortBy: "RECOMMENDED",
  activePropertyForDetail: null,
  activePropertyForBooking: null,
  activeLocationHierarchyStep: "COUNTRY" // COUNTRY, STATE, CITY, AREA
};

// --------------------------------------------------------------------------
// 6. DOM ELEMENTS & INITIALIZATION
// --------------------------------------------------------------------------

document.addEventListener("DOMContentLoaded", () => {
  renderCategoryTrack();
  renderStateCloud();
  renderListings();
  updateHeaderBadges();
  populateStateDropdownForOwner();

  // Listen to the Live Sync broadcast event!
  window.addEventListener("styno_sync_event", (e) => {
    showSyncNotification(e.detail);
    renderListings();
    renderOwnerListingsTable();
  });

  // Close dropdowns on outside click
  document.addEventListener("click", (e) => {
    if (!e.target.closest(".filter-dropdown")) {
      document.querySelectorAll(".filter-dropdown-menu").forEach(el => el.classList.remove("show"));
    }
  });

  // Preset check-in date to tomorrow
  const tomorrow = new Date();
  tomorrow.setDate(tomorrow.getDate() + 1);
  const dateInput = document.getElementById("bookingCheckInDate");
  if (dateInput) {
    dateInput.value = tomorrow.toISOString().split("T")[0];
    dateInput.min = new Date().toISOString().split("T")[0];
  }
});

// --------------------------------------------------------------------------
// 7. RENDERING COMPONENTS
// --------------------------------------------------------------------------

/**
 * Category Navigation Track
 */
function renderCategoryTrack() {
  const container = document.getElementById("categoriesTrack");
  if (!container) return;

  const allProps = StynoDB.getAllProperties();
  
  container.innerHTML = STYNO_CATEGORIES.map(cat => {
    const count = cat.key 
      ? allProps.filter(p => p.propertyType === cat.key).length 
      : allProps.length;
    
    const isActive = AppState.selectedCategory === cat.key;
    return `
      <button class="category-tab-btn ${isActive ? 'active' : ''}" onclick="selectCategory('${cat.key || ''}')">
        <span class="material-symbols-rounded">${cat.icon}</span>
        <span>${cat.name}</span>
        <span class="cat-badge">${count}</span>
      </button>
    `;
  }).join("");
}

/**
 * Nationwide State Cloud
 */
function renderStateCloud() {
  const container = document.getElementById("coverageStatesCloud");
  if (!container) return;

  container.innerHTML = INDIA_GEOGRAPHIC_DATA.map(st => `
    <button class="state-chip" onclick="selectStateFilter('${st.state}')">
      ${st.state} ${st.isUT ? '(UT)' : ''}
    </button>
  `).join("");
}

/**
 * Main Listings Cards Grid
 */
function renderListings() {
  const grid = document.getElementById("listingsGrid");
  const emptyState = document.getElementById("emptyState");
  const subtitle = document.getElementById("listingsCountSubtitle");
  if (!grid) return;

  let properties = StynoDB.getAllProperties();
  const savedIds = new Set(StynoDB.getSavedPropertyIds());

  // 1. Filter by Category
  if (AppState.selectedCategory) {
    properties = properties.filter(p => p.propertyType === AppState.selectedCategory);
  }

  // 2. Filter by Location (State, City, Area)
  if (AppState.selectedState) {
    properties = properties.filter(p => p.state.toLowerCase() === AppState.selectedState.toLowerCase());
  }
  if (AppState.selectedCity) {
    properties = properties.filter(p => p.city.toLowerCase() === AppState.selectedCity.toLowerCase());
  }
  if (AppState.selectedArea) {
    properties = properties.filter(p => p.area.toLowerCase().includes(AppState.selectedArea.toLowerCase()));
  }

  // 3. Filter by Text Query (Search)
  if (AppState.searchQuery.trim()) {
    const q = AppState.searchQuery.toLowerCase().trim();
    properties = properties.filter(p => 
      p.name.toLowerCase().includes(q) ||
      p.city.toLowerCase().includes(q) ||
      p.area.toLowerCase().includes(q) ||
      p.address.toLowerCase().includes(q) ||
      p.propertyType.toLowerCase().includes(q) ||
      (p.amenities && p.amenities.some(a => a.toLowerCase().includes(q)))
    );
  }

  // 4. Filter by Gender / Occupant Type
  if (AppState.genderFilter) {
    properties = properties.filter(p => p.genderSuitability === AppState.genderFilter || p.genderSuitability === "ALL");
  }

  // 5. Filter by Price Budget
  properties = properties.filter(p => p.startingPrice <= AppState.maxBudget);

  // 6. Filter by Selected Amenities
  if (AppState.activeAmenities.size > 0) {
    properties = properties.filter(p => {
      if (!p.amenities) return false;
      for (const reqAmenity of AppState.activeAmenities) {
        const has = p.amenities.some(a => a.toLowerCase().includes(reqAmenity.toLowerCase()));
        if (!has) return false;
      }
      return true;
    });
  }

  // 7. Sorting
  if (AppState.sortBy === "PRICE_LOW") {
    properties.sort((a, b) => a.startingPrice - b.startingPrice);
  } else if (AppState.sortBy === "PRICE_HIGH") {
    properties.sort((a, b) => b.startingPrice - a.startingPrice);
  } else if (AppState.sortBy === "RATING") {
    properties.sort((a, b) => (b.rating || 0) - (a.rating || 0));
  } else {
    // RECOMMENDED: Verified and latest first
    properties.sort((a, b) => (b.isVerified ? 1 : 0) - (a.isVerified ? 1 : 0));
  }

  // Update Count Subtitle
  const catName = STYNO_CATEGORIES.find(c => c.key === AppState.selectedCategory)?.name || "Accommodations";
  subtitle.textContent = `Showing ${properties.length} verified ${catName.toLowerCase()} with 0% brokerage`;

  // Render Grid or Empty State
  if (properties.length === 0) {
    grid.innerHTML = "";
    emptyState.style.display = "block";
    return;
  }

  emptyState.style.display = "none";
  grid.innerHTML = properties.map(p => {
    const isSaved = savedIds.has(p.id);
    const heroImage = (p.images && p.images.length) ? p.images[0] : "https://images.unsplash.com/photo-1555854877-bab0e564b8d5?w=800&q=80";
    const durationUnit = getDurationUnitLabel(p.durationType);
    const categoryBadge = getCategoryBadgeInfo(p.propertyType);

    return `
      <div class="stay-card" onclick="openPropertyDetail('${p.id}')">
        <div class="card-media-wrapper">
          <img class="card-media-img" src="${heroImage}" alt="${p.name}" loading="lazy" onerror="this.src='https://images.unsplash.com/photo-1522771739844-6a9f6d5f14af?w=800&q=80'">
          
          <div class="card-floating-badges">
            <span class="badge badge-emerald">0% BROKERAGE</span>
            ${p.isOwnerCreated ? '<span class="badge" style="background:#4338CA;color:#FFF;">Owner Live</span>' : ''}
          </div>

          <button class="card-save-btn ${isSaved ? 'active' : ''}" onclick="event.stopPropagation(); toggleSave('${p.id}');" title="Save stay">
            <span class="material-symbols-rounded">${isSaved ? 'favorite' : 'favorite_border'}</span>
          </button>

          <span class="card-category-indicator">${categoryBadge.emoji} ${categoryBadge.name}</span>
        </div>

        <div class="card-content">
          <div class="card-meta-row">
            <span class="rating-pill">
              <span class="material-symbols-rounded" style="font-size: 1rem; color: #F59E0B;">star</span>
              ${p.rating || '4.8'} (${p.reviewCount || 10})
            </span>
            <span class="badge badge-verified">
              <span class="material-symbols-rounded" style="font-size: 0.9rem;">verified</span> Verified
            </span>
          </div>

          <h3 class="stay-title">${p.name}</h3>
          <div class="stay-location-line">
            <span class="material-symbols-rounded" style="font-size: 1rem;">location_on</span>
            <span>${p.area}, ${p.city}</span>
          </div>

          <div class="card-amenities-row">
            ${(p.amenities || []).slice(0, 3).map(a => `<span class="amenity-micro-tag">${a}</span>`).join("")}
            ${(p.amenities && p.amenities.length > 3) ? `<span class="amenity-micro-tag">+${p.amenities.length - 3} more</span>` : ''}
          </div>

          <div class="card-bottom-row">
            <div class="price-display">
              <div>
                <span class="price-amount">₹${p.startingPrice.toLocaleString('en-IN')}</span>
                <span class="price-unit">${durationUnit}</span>
              </div>
              <span class="zero-brokerage-tag">Direct Owner &bull; No Brokerage</span>
            </div>

            <button class="btn btn-sm btn-primary" onclick="event.stopPropagation(); quickBookStay('${p.id}');">
              Book
            </button>
          </div>
        </div>
      </div>
    `;
  }).join("");

  renderCategoryTrack();
}

/**
 * Real-Time Sync Notification Display
 */
function showSyncNotification(detail) {
  const banner = document.getElementById("syncBanner");
  const bannerText = document.getElementById("syncBannerText");
  if (!banner || !bannerText) return;

  bannerText.textContent = `Sync Complete: "${detail.propertyName}" was ${detail.action} and is now live across Styno!`;
  banner.style.display = "flex";

  showToast(`⚡ Real-time sync: "${detail.propertyName}" updated`);

  setTimeout(() => {
    banner.style.display = "none";
  }, 6000);
}

// --------------------------------------------------------------------------
// 8. FILTER & SEARCH HANDLERS
// --------------------------------------------------------------------------

function selectCategory(catKey) {
  AppState.selectedCategory = catKey || null;
  const title = document.getElementById("listingsSectionTitle");
  if (title) {
    const cat = STYNO_CATEGORIES.find(c => c.key === AppState.selectedCategory);
    title.textContent = cat ? `${cat.emoji} ${cat.name}` : "Verified Stays";
  }
  updateResetFilterVisibility();
  renderListings();
}

function handleSearchInput(val) {
  AppState.searchQuery = val;
  const clearBtnDesk = document.getElementById("headerSearchClearBtn");
  const clearBtnMob = document.getElementById("mobileSearchClearBtn");
  
  if (clearBtnDesk) clearBtnDesk.style.display = val ? "flex" : "none";
  if (clearBtnMob) clearBtnMob.style.display = val ? "flex" : "none";
  
  updateResetFilterVisibility();
  renderListings();
}

function clearSearch() {
  AppState.searchQuery = "";
  document.getElementById("headerSearchInput").value = "";
  document.getElementById("mobileSearchInput").value = "";
  document.getElementById("headerSearchClearBtn").style.display = "none";
  document.getElementById("mobileSearchClearBtn").style.display = "none";
  renderListings();
}

function selectGenderFilter(gender) {
  AppState.genderFilter = gender;
  const label = document.getElementById("genderDropdownLabel");
  if (label) {
    label.textContent = gender ? `Guest: ${formatGenderLabel(gender)}` : "Guest: Anyone";
  }
  document.getElementById("genderDropdownMenu")?.classList.remove("show");
  updateResetFilterVisibility();
  renderListings();
}

function handlePriceRangeChange(val) {
  AppState.maxBudget = Number(val);
  const display = document.getElementById("priceSliderValue");
  const label = document.getElementById("priceDropdownLabel");
  if (display) display.textContent = `Up to ₹${Number(val).toLocaleString('en-IN')}`;
  if (label) label.textContent = `Budget: ≤ ₹${Number(val).toLocaleString('en-IN')}`;
  updateResetFilterVisibility();
  renderListings();
}

function setPriceBudget(budget) {
  document.getElementById("priceRangeInput").value = budget;
  handlePriceRangeChange(budget);
  document.getElementById("priceDropdownMenu")?.classList.remove("show");
}

function toggleAmenityFilter(amenity) {
  const chipIdMap = {
    "AC": "chipAc",
    "Wi-Fi": "chipWifi",
    "Food": "chipFood",
    "Attached Washroom": "chipAttached"
  };

  if (AppState.activeAmenities.has(amenity)) {
    AppState.activeAmenities.delete(amenity);
    document.getElementById(chipIdMap[amenity])?.classList.remove("active");
  } else {
    AppState.activeAmenities.add(amenity);
    document.getElementById(chipIdMap[amenity])?.classList.add("active");
  }

  updateResetFilterVisibility();
  renderListings();
}

function filterByQuickStayDuration(slot) {
  AppState.selectedCategory = "QUICK_STAY";
  AppState.searchQuery = "";
  selectCategory("QUICK_STAY");
  window.scrollTo({ top: document.querySelector(".listings-section").offsetTop - 80, behavior: "smooth" });
}

function handleSortChange(sortVal) {
  AppState.sortBy = sortVal;
  renderListings();
}

function resetAllFilters() {
  AppState.selectedCategory = null;
  AppState.selectedState = null;
  AppState.selectedCity = null;
  AppState.selectedArea = null;
  AppState.searchQuery = "";
  AppState.genderFilter = null;
  AppState.maxBudget = 35000;
  AppState.activeAmenities.clear();

  // Reset Inputs
  document.getElementById("headerSearchInput").value = "";
  document.getElementById("mobileSearchInput").value = "";
  document.getElementById("priceRangeInput").value = 35000;
  document.getElementById("genderDropdownLabel").textContent = "Guest: Anyone";
  document.getElementById("priceDropdownLabel").textContent = "Budget: Any";
  document.getElementById("headerCurrentLocation").textContent = "All India";
  document.getElementById("activeLocationBreadcrumb").textContent = "All India • All Verified Stays";

  document.querySelectorAll(".chip-filter").forEach(el => el.classList.remove("active"));
  updateResetFilterVisibility();
  renderListings();
  showToast("Filters reset to default");
}

function updateResetFilterVisibility() {
  const isFiltered = AppState.selectedCategory !== null ||
    AppState.selectedState !== null ||
    AppState.selectedCity !== null ||
    AppState.searchQuery !== "" ||
    AppState.genderFilter !== null ||
    AppState.maxBudget < 35000 ||
    AppState.activeAmenities.size > 0;

  const btn = document.getElementById("resetFiltersBtn");
  if (btn) btn.style.display = isFiltered ? "inline-flex" : "none";
}

// --------------------------------------------------------------------------
// 9. LOCATION HIERARCHY SELECTOR (Country -> State -> City -> Area)
// --------------------------------------------------------------------------

function openLocationPickerModal() {
  openModal("locationPickerModal");
  setLocationStep(AppState.activeLocationHierarchyStep || "STATE");
}

function setLocationStep(step) {
  AppState.activeLocationHierarchyStep = step;
  const list = document.getElementById("locationItemsList");
  const stepCountryBtn = document.getElementById("stepCountryBtn");
  const stepStateBtn = document.getElementById("stepStateBtn");
  const stepCityBtn = document.getElementById("stepCityBtn");
  const stepAreaBtn = document.getElementById("stepAreaBtn");

  [stepCountryBtn, stepStateBtn, stepCityBtn, stepAreaBtn].forEach(b => b?.classList.remove("active"));

  if (step === "COUNTRY") {
    stepCountryBtn?.classList.add("active");
    list.innerHTML = `
      <div class="location-select-item" onclick="resetToAllIndia()">
        <span>🇮🇳 India (All 36 States & UTs)</span>
        <span class="material-symbols-rounded">check</span>
      </div>
    `;
  } else if (step === "STATE") {
    stepStateBtn?.classList.add("active");
    list.innerHTML = INDIA_GEOGRAPHIC_DATA.map(s => `
      <div class="location-select-item" onclick="handleStateSelectedFromPicker('${s.state}')">
        <span>${s.state} ${s.isUT ? '(UT)' : ''}</span>
        <span class="material-symbols-rounded">arrow_forward</span>
      </div>
    `).join("");
  } else if (step === "CITY") {
    stepCityBtn?.classList.add("active");
    const stObj = INDIA_GEOGRAPHIC_DATA.find(s => s.state === AppState.selectedState);
    const cities = stObj ? stObj.cities : ["Noida", "Bengaluru", "New Delhi", "Mumbai", "Kota", "Pune"];
    list.innerHTML = cities.map(c => `
      <div class="location-select-item" onclick="handleCitySelectedFromPicker('${c}')">
        <span>${c}</span>
        <span class="material-symbols-rounded">arrow_forward</span>
      </div>
    `).join("");
  } else if (step === "AREA") {
    stepAreaBtn?.classList.add("active");
    // Show areas based on stays in this city
    const allProps = StynoDB.getAllProperties().filter(p => !AppState.selectedCity || p.city.toLowerCase() === AppState.selectedCity.toLowerCase());
    const areas = Array.from(new Set(allProps.map(p => p.area)));
    if (!areas.length) areas.push("Central Town", "University Corridor", "Metro Hub");
    list.innerHTML = areas.map(a => `
      <div class="location-select-item" onclick="handleAreaSelectedFromPicker('${a}')">
        <span>${a}</span>
        <span class="material-symbols-rounded">check</span>
      </div>
    `).join("");
  }
}

function filterLocationList(q) {
  const items = document.querySelectorAll(".location-select-item");
  const query = q.toLowerCase().trim();
  items.forEach(item => {
    item.style.display = item.textContent.toLowerCase().includes(query) ? "flex" : "none";
  });
}

function handleStateSelectedFromPicker(stateName) {
  AppState.selectedState = stateName;
  AppState.selectedCity = null;
  AppState.selectedArea = null;
  updateLocationHeaderBreadcrumb();
  setLocationStep("CITY");
  renderListings();
}

function handleCitySelectedFromPicker(cityName) {
  AppState.selectedCity = cityName;
  AppState.selectedArea = null;
  updateLocationHeaderBreadcrumb();
  setLocationStep("AREA");
  renderListings();
}

function handleAreaSelectedFromPicker(areaName) {
  AppState.selectedArea = areaName;
  updateLocationHeaderBreadcrumb();
  closeModal("locationPickerModal");
  renderListings();
  showToast(`Location set to ${areaName}, ${AppState.selectedCity}`);
}

function selectStateFilter(stateName) {
  AppState.selectedState = stateName;
  AppState.selectedCity = null;
  AppState.selectedArea = null;
  updateLocationHeaderBreadcrumb();
  renderListings();
  window.scrollTo({ top: document.querySelector(".listings-section").offsetTop - 80, behavior: "smooth" });
}

function selectCityFilter(cityName) {
  const foundState = INDIA_GEOGRAPHIC_DATA.find(s => s.cities.includes(cityName));
  if (foundState) AppState.selectedState = foundState.state;
  AppState.selectedCity = cityName;
  AppState.selectedArea = null;
  updateLocationHeaderBreadcrumb();
  renderListings();
  window.scrollTo({ top: document.querySelector(".listings-section").offsetTop - 80, behavior: "smooth" });
}

function resetToAllIndia() {
  AppState.selectedState = null;
  AppState.selectedCity = null;
  AppState.selectedArea = null;
  updateLocationHeaderBreadcrumb();
  closeModal("locationPickerModal");
  renderListings();
}

function updateLocationHeaderBreadcrumb() {
  let breadcrumb = "All India";
  if (AppState.selectedArea && AppState.selectedCity) {
    breadcrumb = `${AppState.selectedArea}, ${AppState.selectedCity}`;
  } else if (AppState.selectedCity) {
    breadcrumb = `${AppState.selectedCity}, ${AppState.selectedState || 'India'}`;
  } else if (AppState.selectedState) {
    breadcrumb = `${AppState.selectedState}, India`;
  }

  document.getElementById("headerCurrentLocation").textContent = breadcrumb;
  document.getElementById("activeLocationBreadcrumb").textContent = `${breadcrumb} • Verified Stays`;
}

// --------------------------------------------------------------------------
// 10. PROPERTY DETAIL & GALLERY MODAL
// --------------------------------------------------------------------------

function openPropertyDetail(propId) {
  const prop = StynoDB.getAllProperties().find(p => p.id === propId);
  if (!prop) return;

  AppState.activePropertyForDetail = prop;

  document.getElementById("modalPropertyName").textContent = prop.name;
  document.getElementById("modalPropertyAddress").innerHTML = `<span class="material-symbols-rounded">location_on</span> ${prop.address}, ${prop.city}, ${prop.state}`;
  document.getElementById("modalPropertyTypeBadge").textContent = prop.propertyType;
  document.getElementById("modalGenderBadge").textContent = formatGenderLabel(prop.genderSuitability);
  
  const unit = getDurationUnitLabel(prop.durationType);
  document.getElementById("modalStartingPrice").textContent = `₹${prop.startingPrice.toLocaleString('en-IN')}${unit}`;
  document.getElementById("modalDurationType").textContent = formatDurationType(prop.durationType);
  document.getElementById("modalRating").innerHTML = `★ ${prop.rating || 4.8} (${prop.reviewCount || 50} reviews)`;
  document.getElementById("modalFooterPrice").textContent = `₹${prop.startingPrice.toLocaleString('en-IN')}`;
  document.getElementById("modalFooterPriceSub").textContent = `${unit} • 0% brokerage`;

  // Gallery
  const gallery = document.getElementById("modalGalleryGrid");
  const imgs = (prop.images && prop.images.length) ? prop.images : ["https://images.unsplash.com/photo-1555854877-bab0e564b8d5?w=800&q=80"];
  gallery.innerHTML = `
    <img class="gallery-primary-img" src="${imgs[0]}" alt="${prop.name}">
    <div class="gallery-sub-col">
      <img class="gallery-sub-img" src="${imgs[1] || imgs[0]}" alt="Room view">
      <img class="gallery-sub-img" src="${imgs[2] || imgs[0]}" alt="Facility view">
    </div>
  `;

  // Room Options
  const roomsGrid = document.getElementById("modalRoomOptionsGrid");
  const rooms = prop.roomOptions || [
    { type: "Standard Room", price: prop.startingPrice, bedsAvailable: 2, deposit: prop.startingPrice }
  ];
  roomsGrid.innerHTML = rooms.map(r => `
    <div class="room-option-box">
      <div class="room-option-title">${r.type}</div>
      <div class="room-option-price">₹${r.price.toLocaleString('en-IN')}${unit}</div>
      <div class="room-option-features">Beds Available: ${r.bedsAvailable} &bull; Deposit: ₹${(r.deposit || 0).toLocaleString('en-IN')}</div>
    </div>
  `).join("");

  // Amenities
  const amenitiesGrid = document.getElementById("modalAmenitiesGrid");
  amenitiesGrid.innerHTML = (prop.amenities || ["Wi-Fi", "Air Conditioner", "Attached Washroom"]).map(a => `
    <div class="amenity-tag-card">
      <span class="material-symbols-rounded text-emerald" style="font-size: 1rem;">check_circle</span>
      <span>${a}</span>
    </div>
  `).join("");

  // Rules
  const rulesGrid = document.getElementById("modalRulesGrid");
  const rules = prop.rules || ["Valid Govt ID required at check-in", "Zero smoking in shared corridors", "Visitors allowed in common lobby"];
  rulesGrid.innerHTML = rules.map(rule => `
    <div class="rule-item">
      <span class="material-symbols-rounded text-primary" style="font-size: 1rem;">info</span>
      <span>${rule}</span>
    </div>
  `).join("");

  // Owner contact card
  const ownerCard = document.getElementById("modalOwnerCard");
  const owner = prop.owner || { name: "Styno Verified Host", phone: "+91 98000 12345", verified: true, experience: "Verified Partner" };
  ownerCard.innerHTML = `
    <div class="owner-info-left">
      <div class="owner-avatar-circle">${owner.name.charAt(0)}</div>
      <div>
        <strong>${owner.name}</strong>
        <div style="font-size: 0.8rem; color: var(--text-muted);">${owner.experience || 'Direct Host'} &bull; 0% Commission</div>
      </div>
    </div>
    <div style="display: flex; gap: 0.5rem;">
      <a href="tel:${owner.phone}" class="btn btn-sm btn-secondary" onclick="event.stopPropagation();">
        <span class="material-symbols-rounded">call</span> Call Host
      </a>
    </div>
  `;

  // Save button state
  const isSaved = StynoDB.getSavedPropertyIds().includes(prop.id);
  const saveBtn = document.getElementById("modalSaveBtn");
  if (saveBtn) {
    saveBtn.innerHTML = `<span class="material-symbols-rounded">${isSaved ? 'favorite' : 'favorite_border'}</span> ${isSaved ? 'Saved' : 'Save'}`;
  }

  openModal("propertyDetailModal");
}

function proceedToBookingFromModal() {
  const prop = AppState.activePropertyForDetail;
  closeModal("propertyDetailModal");
  if (prop) quickBookStay(prop.id);
}

// --------------------------------------------------------------------------
// 11. BOOKING & CHECKOUT ENGINE
// --------------------------------------------------------------------------

function quickBookStay(propId) {
  const prop = StynoDB.getAllProperties().find(p => p.id === propId);
  if (!prop) return;

  AppState.activePropertyForBooking = prop;

  const miniSummary = document.getElementById("bookingStayMiniSummary");
  miniSummary.innerHTML = `
    <img class="mini-thumb" src="${(prop.images && prop.images[0]) || 'https://images.unsplash.com/photo-1555854877-bab0e564b8d5?w=200&q=80'}" alt="">
    <div>
      <h4 style="font-size: 0.95rem;">${prop.name}</h4>
      <p style="font-size: 0.8rem; color: var(--text-muted);">${prop.area}, ${prop.city} &bull; ${prop.propertyType}</p>
    </div>
  `;

  recalculateBookingTotal();
  openModal("bookingModal");
}

function recalculateBookingTotal() {
  const prop = AppState.activePropertyForBooking;
  if (!prop) return;

  const durationSelect = document.getElementById("bookingDuration");
  const durationMonths = Number(durationSelect.value || 1);

  let discountFactor = 1.0;
  if (durationMonths === 3) discountFactor = 0.95;
  if (durationMonths === 6) discountFactor = 0.90;
  if (durationMonths === 12) discountFactor = 0.85;

  const basePrice = prop.startingPrice;
  const rawTotal = basePrice * durationMonths;
  const discountedTotal = Math.round(rawTotal * discountFactor);

  document.getElementById("bkBaseRent").textContent = `₹${rawTotal.toLocaleString('en-IN')}`;
  document.getElementById("bkFinalAmount").textContent = `₹${discountedTotal.toLocaleString('en-IN')}`;
  document.getElementById("btnPayAmount").textContent = `₹${discountedTotal.toLocaleString('en-IN')}`;
}

function confirmAndExecuteBooking() {
  const prop = AppState.activePropertyForBooking;
  if (!prop) return;

  const guestName = document.getElementById("bookingGuestName").value.trim();
  const guestPhone = document.getElementById("bookingGuestPhone").value.trim();
  const guestEmail = document.getElementById("bookingGuestEmail").value.trim();
  const checkInDate = document.getElementById("bookingCheckInDate").value;
  const durationMonths = document.getElementById("bookingDuration").value;

  if (!guestName || !guestPhone || !checkInDate) {
    alert("Please fill in your name, mobile number, and check-in date.");
    return;
  }

  const bookingId = "STYNO-" + Math.floor(10000 + Math.random() * 90000);
  const passcode = "STY-" + Math.floor(1000 + Math.random() * 9000);

  const durationSelect = document.getElementById("bookingDuration");
  let discountFactor = 1.0;
  if (Number(durationMonths) === 3) discountFactor = 0.95;
  if (Number(durationMonths) === 6) discountFactor = 0.90;
  if (Number(durationMonths) === 12) discountFactor = 0.85;
  const finalPaidAmount = Math.round(prop.startingPrice * Number(durationMonths) * discountFactor);

  const bookingRecord = {
    id: bookingId,
    passcode: passcode,
    propertyId: prop.id,
    propertyName: prop.name,
    propertyAddress: `${prop.address}, ${prop.city}`,
    propertyType: prop.propertyType,
    guestName,
    guestPhone,
    guestEmail,
    checkInDate,
    duration: `${durationMonths} Month(s)`,
    totalAmount: finalPaidAmount,
    status: "CONFIRMED",
    paymentMode: document.querySelector("input[name='paymentMode']:checked")?.value || "UPI",
    bookingTimestamp: Date.now()
  };

  StynoDB.saveBooking(bookingRecord);
  updateHeaderBadges();

  closeModal("bookingModal");

  // Show Success Modal
  document.getElementById("successBookingId").textContent = `Booking ID: #${bookingId}`;
  document.getElementById("successPasscode").textContent = passcode;
  document.getElementById("bookingSuccessDetails").innerHTML = `
    <p><strong>Stay:</strong> ${prop.name}</p>
    <p><strong>Location:</strong> ${prop.address}, ${prop.city}</p>
    <p><strong>Check-In Date:</strong> ${checkInDate}</p>
    <p><strong>Amount Paid:</strong> ₹${finalPaidAmount.toLocaleString('en-IN')} (0% Brokerage)</p>
  `;

  openModal("bookingSuccessModal");
  showToast(`🎉 Booking confirmed! Passcode: ${passcode}`);
}

// --------------------------------------------------------------------------
// 12. OWNER PORTAL & REAL-TIME LISTINGS SYNCHRONIZATION
// --------------------------------------------------------------------------

function openOwnerPortalModal() {
  switchOwnerTab("LISTINGS");
  openModal("ownerPortalModal");
}

function switchOwnerTab(tabName) {
  const tabListings = document.getElementById("tabOwnerListings");
  const tabAdd = document.getElementById("tabOwnerAdd");
  const contentListings = document.getElementById("ownerTabContentListings");
  const contentAdd = document.getElementById("ownerTabContentAdd");

  if (tabName === "LISTINGS") {
    tabListings.classList.add("active");
    tabAdd.classList.remove("active");
    contentListings.style.display = "block";
    contentAdd.style.display = "none";
    renderOwnerListingsTable();
  } else {
    tabAdd.classList.add("active");
    tabListings.classList.remove("active");
    contentAdd.style.display = "block";
    contentListings.style.display = "none";
  }
}

function renderOwnerListingsTable() {
  const table = document.getElementById("ownerListingsTable");
  const countBadge = document.getElementById("ownerListingsCount");
  if (!table) return;

  const allProps = StynoDB.getAllProperties();
  countBadge.textContent = allProps.length;

  table.innerHTML = allProps.map(prop => `
    <div class="owner-stay-row">
      <div class="owner-stay-thumb-group">
        <img class="owner-stay-thumb" src="${(prop.images && prop.images[0]) || 'https://images.unsplash.com/photo-1555854877-bab0e564b8d5?w=200&q=80'}" alt="">
        <div>
          <div class="owner-stay-name">${prop.name}</div>
          <div class="owner-stay-loc">${prop.area}, ${prop.city} &bull; <span class="badge badge-emerald">0% Fee</span></div>
        </div>
      </div>

      <div style="display: flex; align-items: center; gap: 0.75rem;">
        <div>
          <label style="font-size: 0.7rem; color: var(--text-muted);">Current Price</label>
          <div style="display: flex; align-items: center; gap: 4px;">
            <span>₹</span>
            <input type="number" value="${prop.startingPrice}" style="width: 80px; padding: 4px; border-radius: 4px; border: 1px solid var(--border);" onchange="quickUpdatePrice('${prop.id}', this.value)">
          </div>
        </div>

        <div class="owner-stay-actions">
          <button class="btn btn-sm btn-secondary" onclick="editListingInForm('${prop.id}')" title="Edit details">
            <span class="material-symbols-rounded">edit</span>
          </button>
          <button class="btn btn-sm btn-outline" style="color: var(--rose);" onclick="deleteListing('${prop.id}')" title="Delete listing">
            <span class="material-symbols-rounded">delete</span>
          </button>
        </div>
      </div>
    </div>
  `).join("");
}

function quickUpdatePrice(propId, newPrice) {
  StynoDB.updatePropertyPricing(propId, newPrice);
  showToast(`Updated price to ₹${newPrice}. Synced across users!`);
}

function deleteListing(propId) {
  if (confirm("Are you sure you want to remove this listing? It will immediately stop appearing for users.")) {
    StynoDB.deletePropertyListing(propId);
  }
}

function editListingInForm(propId) {
  const prop = StynoDB.getAllProperties().find(p => p.id === propId);
  if (!prop) return;

  document.getElementById("ownerPropertyId").value = prop.id;
  document.getElementById("ownerPropType").value = prop.propertyType;
  document.getElementById("ownerGender").value = prop.genderSuitability;
  document.getElementById("ownerPropName").value = prop.name;
  document.getElementById("ownerState").value = prop.state;
  populateCitiesForOwner(prop.state);
  document.getElementById("ownerCity").value = prop.city;
  document.getElementById("ownerArea").value = prop.area;
  document.getElementById("ownerAddress").value = prop.address;
  document.getElementById("ownerStartingPrice").value = prop.startingPrice;
  document.getElementById("ownerDurationType").value = prop.durationType;
  document.getElementById("ownerName").value = prop.owner?.name || "";
  document.getElementById("ownerPhone").value = prop.owner?.phone || "";

  switchOwnerTab("ADD");
  document.getElementById("btnSaveOwnerListing").innerHTML = `<span class="material-symbols-rounded">sync</span> Update & Sync Listing`;
}

function handleOwnerFormSubmit(e) {
  e.preventDefault();

  const id = document.getElementById("ownerPropertyId").value;
  const propertyType = document.getElementById("ownerPropType").value;
  const genderSuitability = document.getElementById("ownerGender").value;
  const name = document.getElementById("ownerPropName").value.trim();
  const state = document.getElementById("ownerState").value;
  const city = document.getElementById("ownerCity").value;
  const area = document.getElementById("ownerArea").value.trim();
  const address = document.getElementById("ownerAddress").value.trim();
  const startingPrice = Number(document.getElementById("ownerStartingPrice").value);
  const durationType = document.getElementById("ownerDurationType").value;
  const securityDeposit = Number(document.getElementById("ownerSecurityDeposit").value || 0);

  const selectedAmenities = [];
  document.querySelectorAll("input[name='ownerAmenity']:checked").forEach(cb => selectedAmenities.push(cb.value));

  const ownerName = document.getElementById("ownerName").value.trim();
  const ownerPhone = document.getElementById("ownerPhone").value.trim();

  // Category specific curated high-res image presets
  const imagePresets = {
    "HOTEL": ["https://images.unsplash.com/photo-1566073771259-6a8506099945?w=800&q=80", "https://images.unsplash.com/photo-1582719478250-c89cae4dc85b?w=800&q=80"],
    "HOSTEL": ["https://images.unsplash.com/photo-1555854877-bab0e564b8d5?w=800&q=80", "https://images.unsplash.com/photo-1595526114035-0d45ed16cfbf?w=800&q=80"],
    "PG": ["https://images.unsplash.com/photo-1522771739844-6a9f6d5f14af?w=800&q=80", "https://images.unsplash.com/photo-1595526114035-0d45ed16cfbf?w=800&q=80"],
    "ROOM": ["https://images.unsplash.com/photo-1598928506311-c55ded91a20c?w=800&q=80", "https://images.unsplash.com/photo-1505691938895-1758d7feb511?w=800&q=80"],
    "FLAT": ["https://images.unsplash.com/photo-1502672260266-1c1ef2d93688?w=800&q=80", "https://images.unsplash.com/photo-1560448204-e02f11c3d0e2?w=800&q=80"],
    "QUICK_STAY": ["https://images.unsplash.com/photo-1596394516093-501ba68a0ba6?w=800&q=80", "https://images.unsplash.com/photo-1512918728675-ed5a9ecdebfd?w=800&q=80"]
  };

  const propData = {
    id: id || "",
    name,
    propertyType,
    genderSuitability,
    state,
    city,
    area,
    address,
    startingPrice,
    durationType,
    securityDepositAmount: securityDeposit,
    amenities: selectedAmenities,
    images: imagePresets[propertyType] || imagePresets["PG"],
    owner: {
      name: ownerName,
      phone: ownerPhone,
      verified: true,
      experience: "Owner Direct Host"
    },
    roomOptions: [
      { type: "Standard Room (" + propertyType + ")", price: startingPrice, bedsAvailable: 2, deposit: securityDeposit }
    ]
  };

  StynoDB.savePropertyListing(propData);

  // Reset form
  document.getElementById("ownerListingForm").reset();
  document.getElementById("ownerPropertyId").value = "";
  document.getElementById("btnSaveOwnerListing").innerHTML = `<span class="material-symbols-rounded">cloud_upload</span> Publish Stay & Sync to Users`;

  switchOwnerTab("LISTINGS");
  showToast(`🎉 "${name}" published and synced to user section!`);
}

function populateStateDropdownForOwner() {
  const stateSelect = document.getElementById("ownerState");
  if (!stateSelect) return;

  stateSelect.innerHTML = INDIA_GEOGRAPHIC_DATA.map(s => `
    <option value="${s.state}">${s.state} ${s.isUT ? '(UT)' : ''}</option>
  `).join("");

  populateCitiesForOwner(INDIA_GEOGRAPHIC_DATA[0].state);
}

function populateCitiesForOwner(stateName) {
  const citySelect = document.getElementById("ownerCity");
  if (!citySelect) return;

  const stObj = INDIA_GEOGRAPHIC_DATA.find(s => s.state === stateName);
  const cities = stObj ? stObj.cities : ["Central"];

  citySelect.innerHTML = cities.map(c => `
    <option value="${c}">${c}</option>
  `).join("");
}

function handleOwnerCategoryChange(cat) {
  const durSelect = document.getElementById("ownerDurationType");
  if (!durSelect) return;
  if (cat === "QUICK_STAY") durSelect.value = "HOURLY";
  else if (cat === "HOTEL") durSelect.value = "DAILY";
  else durSelect.value = "MONTHLY";
}

// --------------------------------------------------------------------------
// 13. BOOKINGS & SAVED MODALS
// --------------------------------------------------------------------------

function openBookingsModal() {
  const bookings = StynoDB.getBookings();
  const list = document.getElementById("myBookingsList");
  if (!list) return;

  if (!bookings.length) {
    list.innerHTML = `
      <div class="empty-state" style="padding: 2rem 1rem;">
        <span class="material-symbols-rounded" style="font-size: 2.5rem; color: var(--text-muted);">receipt_long</span>
        <h4>No confirmed bookings yet</h4>
        <p class="text-muted" style="font-size: 0.85rem;">Browse our 0% brokerage stays and book your ideal accommodation.</p>
      </div>
    `;
  } else {
    list.innerHTML = bookings.map(b => `
      <div style="padding: 1rem; border: 1px solid var(--border); border-radius: var(--radius-md); margin-bottom: 0.75rem; background: var(--surface-variant);">
        <div style="display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 0.5rem;">
          <div>
            <strong>${b.propertyName}</strong>
            <div style="font-size: 0.8rem; color: var(--text-muted);">${b.propertyAddress}</div>
          </div>
          <span class="badge badge-emerald">CONFIRMED</span>
        </div>

        <div style="background: #FFFFFF; border: 1px dashed var(--primary); padding: 0.5rem 0.75rem; border-radius: var(--radius-sm); margin: 0.5rem 0; display: flex; justify-content: space-between; align-items: center;">
          <span style="font-size: 0.75rem; color: var(--text-muted);">PASSCODE</span>
          <span style="font-size: 1.1rem; font-weight: 800; color: var(--primary); letter-spacing: 0.05em;">${b.passcode}</span>
        </div>

        <div style="font-size: 0.8rem; color: var(--text-sub); display: flex; justify-content: space-between;">
          <span>Check-In: ${b.checkInDate}</span>
          <span>Paid: ₹${b.totalAmount.toLocaleString('en-IN')} (0% Brokerage)</span>
        </div>
      </div>
    `).join("");
  }

  openModal("myBookingsModal");
}

function openSavedModal() {
  const savedIds = StynoDB.getSavedPropertyIds();
  const list = document.getElementById("savedList");
  if (!list) return;

  const allProps = StynoDB.getAllProperties();
  const savedProps = allProps.filter(p => savedIds.includes(p.id));

  if (!savedProps.length) {
    list.innerHTML = `
      <div class="empty-state" style="padding: 2rem 1rem;">
        <span class="material-symbols-rounded" style="font-size: 2.5rem; color: var(--text-muted);">favorite_border</span>
        <h4>No saved accommodations yet</h4>
        <p class="text-muted" style="font-size: 0.85rem;">Tap the heart icon on any stay card to shortlist it here.</p>
      </div>
    `;
  } else {
    list.innerHTML = savedProps.map(p => `
      <div style="display: flex; align-items: center; justify-content: space-between; padding: 0.75rem; border: 1px solid var(--border); border-radius: var(--radius-md); margin-bottom: 0.5rem;">
        <div style="display: flex; align-items: center; gap: 0.75rem;">
          <img src="${(p.images && p.images[0]) || 'https://images.unsplash.com/photo-1555854877-bab0e564b8d5?w=100&q=80'}" style="width: 48px; height: 48px; border-radius: 6px; object-fit: cover;">
          <div>
            <strong style="font-size: 0.9rem;">${p.name}</strong>
            <div style="font-size: 0.75rem; color: var(--text-muted);">${p.area}, ${p.city} &bull; ₹${p.startingPrice.toLocaleString('en-IN')}</div>
          </div>
        </div>
        <button class="btn btn-sm btn-primary" onclick="closeModal('savedModal'); openPropertyDetail('${p.id}');">
          View
        </button>
      </div>
    `).join("");
  }

  openModal("savedModal");
}

function toggleSave(propId) {
  const isSaved = StynoDB.toggleSaveProperty(propId);
  updateHeaderBadges();
  renderListings();
  showToast(isSaved ? "Saved to your shortlist" : "Removed from shortlist");
}

function toggleSaveCurrentProperty() {
  if (!AppState.activePropertyForDetail) return;
  toggleSave(AppState.activePropertyForDetail.id);
  const isSaved = StynoDB.getSavedPropertyIds().includes(AppState.activePropertyForDetail.id);
  const btn = document.getElementById("modalSaveBtn");
  if (btn) {
    btn.innerHTML = `<span class="material-symbols-rounded">${isSaved ? 'favorite' : 'favorite_border'}</span> ${isSaved ? 'Saved' : 'Save'}`;
  }
}

function updateHeaderBadges() {
  const bookings = StynoDB.getBookings();
  const saved = StynoDB.getSavedPropertyIds();

  const bkBadge = document.getElementById("bookingsCountBadge");
  if (bkBadge) {
    bkBadge.textContent = bookings.length;
    bkBadge.style.display = bookings.length ? "inline-block" : "none";
  }

  const svBadge = document.getElementById("savedCountBadge");
  if (svBadge) {
    svBadge.textContent = saved.length;
    svBadge.style.display = saved.length ? "inline-block" : "none";
  }
}

// --------------------------------------------------------------------------
// 14. HELPER UTILITIES & MODAL CONTROLS
// --------------------------------------------------------------------------

function openModal(modalId) {
  const el = document.getElementById(modalId);
  if (el) el.classList.add("open");
  document.body.style.overflow = "hidden";
}

function closeModal(modalId) {
  const el = document.getElementById(modalId);
  if (el) el.classList.remove("open");
  document.body.style.overflow = "";
}

function closeOnBackdrop(e, modalId) {
  if (e.target.id === modalId) {
    closeModal(modalId);
  }
}

function toggleDropdown(menuId) {
  const menu = document.getElementById(menuId);
  if (!menu) return;
  const isShown = menu.classList.contains("show");
  document.querySelectorAll(".filter-dropdown-menu").forEach(el => el.classList.remove("show"));
  if (!isShown) menu.classList.add("show");
}

function toggleMobileMenu() {
  const drawer = document.getElementById("mobileDrawer");
  if (drawer) drawer.classList.toggle("open");
}

function showToast(msg) {
  const container = document.getElementById("toastContainer");
  if (!container) return;

  const toast = document.createElement("div");
  toast.className = "toast-msg";
  toast.innerHTML = `<span class="material-symbols-rounded" style="color:#10B981;">check_circle</span> <span>${msg}</span>`;
  container.appendChild(toast);

  setTimeout(() => {
    toast.style.opacity = "0";
    toast.style.transform = "translateY(12px)";
    toast.style.transition = "all 0.3s ease";
    setTimeout(() => toast.remove(), 300);
  }, 3500);
}

function getDurationUnitLabel(durationType) {
  switch (durationType) {
    case "HOURLY": return "/slot";
    case "DAILY": return "/night";
    case "WEEKLY": return "/week";
    default: return "/mo";
  }
}

function formatDurationType(durationType) {
  switch (durationType) {
    case "HOURLY": return "Hourly Transit";
    case "DAILY": return "Nightly Hotel";
    case "WEEKLY": return "Weekly Stay";
    default: return "Monthly Rental";
  }
}

function formatGenderLabel(gender) {
  switch (gender) {
    case "BOYS_ONLY": return "Boys Only";
    case "GIRLS_ONLY": return "Girls Only";
    case "CO_ED": return "Co-Ed";
    case "FAMILY": return "Family";
    default: return "Co-Ed / All";
  }
}

function getCategoryBadgeInfo(type) {
  const match = STYNO_CATEGORIES.find(c => c.key === type);
  return match || { emoji: "🏡", name: type };
}
