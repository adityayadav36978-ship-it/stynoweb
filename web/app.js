/**
 * STYNO STAYS - OFFICIAL RESPONSIVE WEB APPLICATION
 * 1:1 Match to STYNO Android Mobile Application
 * Zero Brokerage Accommodation Network & Real-Time Sync Engine
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
    amenities: ["High-Speed Wi-Fi", "Air Conditioner", "3-Time Meals", "Attached Washroom", "24/7 Power Backup", "CCTV & Biometric Security", "Daily Housekeeping", "Washing Machine / Laundry"],
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
    rules: ["Gate closes at 10:30 PM", "Visitor entry till 7:00 PM", "Biometric punch required", "Quiet study hours after 10 PM"]
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
    amenities: ["High-Speed Wi-Fi", "Air Conditioner", "Gymnasium", "24/7 Power Backup", "Attached Washroom", "RO Purified Water", "CCTV Security"],
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
    amenities: ["Air Conditioner", "Free Breakfast Buffet", "High-Speed Wi-Fi", "Valet Parking", "Room Service 24h", "Attached Washroom", "Smart TV"],
    roomOptions: [
      { type: "Deluxe King Room", price: 2499, bedsAvailable: 6, deposit: 0 },
      { type: "Executive Suite", price: 4299, bedsAvailable: 2, deposit: 0 }
    ],
    owner: {
      name: "Sunil Mehra (General Manager)",
      phone: "+91 11 2341 9090",
      verified: true,
      experience: "12 years hospitality"
    },
    rules: ["Check-in 12:00 PM, Check-out 11:00 AM", "Govt ID required for all adult guests"]
  },
  {
    id: "prop-pg-01",
    name: "Styno Green Leaf Executive PG",
    propertyType: "PG",
    genderSuitability: "CO_ED",
    description: "Co-living community designed for corporate professionals working in Cyber Hub and Golf Course Road. Features ergonomic work from home desk, rooftop café, and weekend housekeeping.",
    address: "DLF Phase 2, Near Sikanderpur Metro",
    city: "Gurugram",
    state: "Haryana",
    area: "DLF Phase 2",
    pincode: "122002",
    latitude: 28.4817,
    longitude: 77.0878,
    startingPrice: 11000,
    durationType: "MONTHLY",
    rating: 4.8,
    reviewCount: 76,
    isVerified: true,
    isInstantBookable: true,
    isZeroBrokerage: true,
    featuredBadge: "Corporate Co-Living",
    images: [
      "https://images.unsplash.com/photo-1502672260266-1c1ef2d93688?w=800&q=80",
      "https://images.unsplash.com/photo-1560448204-e02f11c3d0e2?w=800&q=80"
    ],
    amenities: ["High-Speed Wi-Fi", "Air Conditioner", "3-Time Meals", "Attached Washroom", "Rooftop Lounge", "Daily Housekeeping", "Power Backup"],
    roomOptions: [
      { type: "Double Sharing", price: 11000, bedsAvailable: 3, deposit: 11000 },
      { type: "Private Studio", price: 18500, bedsAvailable: 1, deposit: 18500 }
    ],
    owner: {
      name: "Rajesh & Ananya Verma",
      phone: "+91 99100 54321",
      verified: true,
      experience: "Verified Superhost"
    },
    rules: ["Keycard smart lock access", "No loud music after 11 PM", "Guests allowed with prior notice"]
  },
  {
    id: "prop-flat-01",
    name: "Styno Silver Oak Furnished 2BHK Flat",
    propertyType: "FLAT",
    genderSuitability: "FAMILY",
    description: "Fully furnished 2 BHK apartment inside a gated luxury society with swimming pool, covered parking, modular kitchen, and round the clock security. Zero brokerage.",
    address: "Phase 3, Hinjewadi Rajiv Gandhi Infotech Park",
    city: "Pune",
    state: "Maharashtra",
    area: "Hinjewadi",
    pincode: "411057",
    latitude: 18.5913,
    longitude: 73.7389,
    startingPrice: 24000,
    durationType: "MONTHLY",
    rating: 4.9,
    reviewCount: 45,
    isVerified: true,
    isInstantBookable: true,
    isZeroBrokerage: true,
    featuredBadge: "Furnished 2BHK",
    images: [
      "https://images.unsplash.com/photo-1502005229762-ae1b460020e2?w=800&q=80",
      "https://images.unsplash.com/photo-1484154218962-a197022b5858?w=800&q=80"
    ],
    amenities: ["Modular Kitchen", "Air Conditioner", "Covered Parking", "Swimming Pool", "Gated Security", "High-Speed Wi-Fi", "Washing Machine / Laundry"],
    roomOptions: [
      { type: "Complete 2BHK Apartment", price: 24000, bedsAvailable: 1, deposit: 35000 }
    ],
    owner: {
      name: "Abhishek Kulkarni",
      phone: "+91 98230 11223",
      verified: true,
      experience: "Direct Owner"
    },
    rules: ["Family & working professionals preferred", "Society clubhouse guidelines apply"]
  },
  {
    id: "prop-quick-01",
    name: "Styno Metro Express Sleep Pod",
    propertyType: "QUICK_STAY",
    genderSuitability: "ALL",
    description: "Compact futuristic sleeping pods located inside the metro concourse. Ideal for travellers, freelancers, and interview candidates who need a quiet nap, hot shower, or power recharge.",
    address: "Concourse Level, Sector 18 Metro Station",
    city: "Noida",
    state: "Uttar Pradesh",
    area: "Sector 18",
    pincode: "201301",
    latitude: 28.5708,
    longitude: 77.3271,
    startingPrice: 299,
    durationType: "HOURLY",
    rating: 4.7,
    reviewCount: 188,
    isVerified: true,
    isInstantBookable: true,
    isZeroBrokerage: true,
    featuredBadge: "Hourly Transit Pod",
    images: [
      "https://images.unsplash.com/photo-1596394516093-501ba68a0ba6?w=800&q=80"
    ],
    amenities: ["Soundproof Pod", "Air Conditioner", "High-Speed Wi-Fi", "USB Charging Hub", "Hot Shower Facility", "Luggage Locker"],
    roomOptions: [
      { type: "3 Hours Nap Slot", price: 299, bedsAvailable: 8, deposit: 0 },
      { type: "6 Hours Transit Slot", price: 499, bedsAvailable: 5, deposit: 0 },
      { type: "12 Hours Day Slot", price: 799, bedsAvailable: 3, deposit: 0 },
      { type: "24 Hours Full Day Slot", price: 1299, bedsAvailable: 2, deposit: 0 }
    ],
    owner: {
      name: "Styno Pods India Operations",
      phone: "+91 1800 123 7890",
      verified: true,
      experience: "Managed Pod Network"
    },
    rules: ["Strictly single occupancy per pod", "Automated digital passcode check-in"]
  },
  {
    id: "prop-hostel-03",
    name: "Styno Krishna Boys Premier Hostel",
    propertyType: "HOSTEL",
    genderSuitability: "BOYS_ONLY",
    description: "Kota's top student accommodation located adjacent to Allen & Resonance coaching campuses. Includes biometric access, silent study library, doctor on call, and balanced nutritional food.",
    address: "Road No. 2, Indra Vihar",
    city: "Kota",
    state: "Rajasthan",
    area: "Indra Vihar",
    pincode: "324005",
    latitude: 25.1388,
    longitude: 75.8362,
    startingPrice: 8500,
    durationType: "MONTHLY",
    rating: 4.9,
    reviewCount: 230,
    isVerified: true,
    isInstantBookable: true,
    isZeroBrokerage: true,
    featuredBadge: "Allen Campus Nearby",
    images: [
      "https://images.unsplash.com/photo-1555854877-bab0e564b8d5?w=800&q=80",
      "https://images.unsplash.com/photo-1595526114035-0d45ed16cfbf?w=800&q=80"
    ],
    amenities: ["3-Time Meals", "Air Conditioner", "High-Speed Wi-Fi", "Study Library", "Attached Washroom", "Power Backup", "Doctor on Call"],
    roomOptions: [
      { type: "Double Sharing", price: 8500, bedsAvailable: 6, deposit: 8500 },
      { type: "Single Study Room", price: 13500, bedsAvailable: 2, deposit: 13500 }
    ],
    owner: {
      name: "Mahesh Chand Sharma",
      phone: "+91 94141 87654",
      verified: true,
      experience: "15 years Kota student housing"
    },
    rules: ["Curfew strictly 9:30 PM", "Daily parent attendance SMS", "No smartphone usage in library"]
  },
  {
    id: "prop-pg-02",
    name: "Styno Ganga Heritage PG & Rooms",
    propertyType: "PG",
    genderSuitability: "ALL",
    description: "Peaceful family-run stay close to railway transit and local markets. Offers clean sanitized rooms, pure vegetarian fresh meals, and generous parking space.",
    address: "Station Road, Near Railway Junction",
    city: "Bagaha",
    state: "Bihar",
    area: "Station Road",
    pincode: "845101",
    latitude: 27.0988,
    longitude: 84.0903,
    startingPrice: 3800,
    durationType: "MONTHLY",
    rating: 4.6,
    reviewCount: 42,
    isVerified: true,
    isInstantBookable: true,
    isZeroBrokerage: true,
    featuredBadge: "Heritage PG Stay",
    images: [
      "https://images.unsplash.com/photo-1522771739844-6a9f6d5f14af?w=800&q=80",
      "https://images.unsplash.com/photo-1555854877-bab0e564b8d5?w=800&q=80"
    ],
    amenities: ["3-Time Meals", "High-Speed Wi-Fi", "Attached Washroom", "Power Backup", "RO Purified Water", "Covered Parking"],
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
    amenities: ["Air Conditioner", "High-Speed Wi-Fi", "Flight Board Display", "Hot Shower Facility", "Luggage Locker"],
    roomOptions: [
      { type: "3 Hours Nap Slot", price: 349, bedsAvailable: 6, deposit: 0 },
      { type: "6 Hours Transit Slot", price: 599, bedsAvailable: 4, deposit: 0 },
      { type: "12 Hours Day Slot", price: 899, bedsAvailable: 2, deposit: 0 }
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
  { state: "Bihar", isUT: false, cities: ["Bagaha", "Patna", "Gaya", "Muzaffarpur", "Bhagalpur", "Darbhanga", "Purnia"] },
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
// 3. CATEGORIES DEFINITION (Exact match to Styno App)
// --------------------------------------------------------------------------

const STYNO_CATEGORIES = [
  { key: null, name: "All Stays", icon: "explore", emoji: "✨" },
  { key: "HOSTEL", name: "Hostels", icon: "apartment", emoji: "🏢" },
  { key: "HOTEL", name: "Hotels", icon: "hotel", emoji: "🏨" },
  { key: "PG", name: "PGs & Co-Living", icon: "bed", emoji: "🛏️" },
  { key: "ROOM", name: "Independent Rooms", icon: "meeting_room", emoji: "🚪" },
  { key: "FLAT", name: "Furnished Flats", icon: "home", emoji: "🏠" },
  { key: "QUICK_STAY", name: "Quick Stay ⚡", icon: "bolt", emoji: "⚡" }
];

// --------------------------------------------------------------------------
// 4. REAL-TIME DATA STORE & SYNCHRONIZATION ENGINE
// --------------------------------------------------------------------------

class StynoDataStore {
  constructor() {
    this.storageKeyProperties = "styno_properties_db_v2";
    this.storageKeyOwnerCustom = "styno_owner_custom_listings_v2";
    this.storageKeyBookings = "styno_user_bookings_v2";
    this.storageKeySaved = "styno_user_saved_v2";
    this.storageKeyOwnerPayment = "styno_owner_payment_v2";
    this.storageKeyQuickStayConfig = "styno_quick_stay_config_v2";
    
    this.initDatabase();

    // Cross-tab sync
    window.addEventListener("storage", (e) => {
      if (e.key === this.storageKeyOwnerCustom || e.key === this.storageKeyProperties || e.key === this.storageKeyBookings) {
        this.notifySyncListeners();
      }
    });

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
      // Seed an initial confirmed booking for demonstration
      const initialBooking = {
        id: "STY-2026-8942",
        propertyId: "prop-hostel-01",
        propertyName: "Styno Orchid Girls Elite Hostel",
        propertyArea: "Sector 62",
        propertyCity: "Noida",
        propertyImage: "https://images.unsplash.com/photo-1555854877-bab0e564b8d5?w=800&q=80",
        roomType: "Triple Sharing (AC)",
        checkInDate: "2026-09-20",
        durationMonths: 1,
        guestName: "Aditya Yadav",
        guestPhone: "+91 98765 43210",
        passcode: "STY-9941",
        status: "UPCOMING",
        amountPaid: 6649,
        paymentMode: "UPI",
        createdAt: Date.now() - 86400000
      };
      localStorage.setItem(this.storageKeyBookings, JSON.stringify([initialBooking]));
    }
    if (!localStorage.getItem(this.storageKeySaved)) {
      localStorage.setItem(this.storageKeySaved, JSON.stringify(["prop-hostel-01", "prop-quick-01"]));
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
    this.notifySyncListeners("Cloud Update", "synced");
  }

  getAllProperties() {
    const base = JSON.parse(localStorage.getItem(this.storageKeyProperties) || "[]");
    const ownerCustom = JSON.parse(localStorage.getItem(this.storageKeyOwnerCustom) || "[]");
    
    const combinedMap = new Map();
    base.forEach(p => combinedMap.set(p.id, p));
    ownerCustom.forEach(p => combinedMap.set(p.id, p));
    
    return Array.from(combinedMap.values());
  }

  getOwnerListings() {
    const custom = JSON.parse(localStorage.getItem(this.storageKeyOwnerCustom) || "[]");
    const all = this.getAllProperties();
    return all.filter(p => p.isOwnerCreated || custom.some(c => c.id === p.id));
  }

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

    localStorage.setItem(this.storageKeyOwnerCustom, JSON.stringify(ownerList));

    const all = this.getAllProperties();
    const map = new Map(all.map(p => [p.id, p]));
    map.set(propData.id, propData);
    localStorage.setItem(this.storageKeyProperties, JSON.stringify(Array.from(map.values())));

    if (this.firestore) {
      try {
        this.firestore.collection("properties").doc(propData.id).set(propData, { merge: true })
          .then(() => console.log("Cloud Firestore sync completed for " + propData.id))
          .catch(e => console.log("Firestore cloud sync queued:", e.message));
      } catch (e) {}
    }

    this.notifySyncListeners(propData.name, isNew ? "published" : "updated");
    return propData;
  }

  deletePropertyListing(propertyId) {
    let ownerList = JSON.parse(localStorage.getItem(this.storageKeyOwnerCustom) || "[]");
    ownerList = ownerList.filter(p => p.id !== propertyId);
    localStorage.setItem(this.storageKeyOwnerCustom, JSON.stringify(ownerList));

    let base = JSON.parse(localStorage.getItem(this.storageKeyProperties) || "[]");
    base = base.filter(p => p.id !== propertyId);
    localStorage.setItem(this.storageKeyProperties, JSON.stringify(base));

    this.notifySyncListeners("Property", "removed");
  }

  togglePropertyAvailability(propertyId, isAvailable) {
    const all = this.getAllProperties();
    const prop = all.find(p => p.id === propertyId);
    if (prop) {
      prop.isAvailable = isAvailable;
      this.savePropertyListing(prop);
    }
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
    this.notifySyncListeners(booking.propertyName, "booked");
    return booking;
  }

  cancelBooking(bookingId) {
    const list = this.getBookings();
    const item = list.find(b => b.id === bookingId);
    if (item) {
      item.status = "CANCELLED";
      localStorage.setItem(this.storageKeyBookings, JSON.stringify(list));
      this.notifySyncListeners("Booking #" + bookingId, "cancelled");
    }
  }

  // Saved Wishlist
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

  notifySyncListeners(propertyName = "Listing", action = "synced") {
    const event = new CustomEvent("styno_sync_event", {
      detail: { propertyName, action, timestamp: Date.now() }
    });
    window.dispatchEvent(event);
  }
}

const StynoDB = new StynoDataStore();

// --------------------------------------------------------------------------
// 5. APPLICATION STATE
// --------------------------------------------------------------------------

const AppState = {
  currentView: "HOME", // HOME, SEARCH, BOOKINGS, SAVED, PROFILE, OWNER
  userRole: "GUEST", // GUEST, OWNER
  selectedCategory: null,
  selectedState: null,
  selectedCity: null,
  selectedArea: null,
  searchQuery: "",
  verifiedOnly: false,
  genderFilter: null,
  maxBudget: 35000,
  activeAmenities: new Set(),
  sortBy: "RECOMMENDED",
  isMapViewActive: false,
  activeBookingsTab: "ALL",
  activeOwnerTab: "PROPERTIES",
  activePropertyForDetail: null,
  activePropertyForBooking: null,
  selectedRoomOption: null,
  locationStep: "COUNTRY"
};

// --------------------------------------------------------------------------
// 6. INITIALIZATION & VIEW MANAGEMENT
// --------------------------------------------------------------------------

document.addEventListener("DOMContentLoaded", () => {
  renderCategoryTrack();
  renderStateCloud();
  renderListings();
  renderOwnerPropertiesList();
  renderBookingsList();
  renderSavedList();
  updateHeaderBadges();
  populateStateDropdownForOwner();

  // Listen to the Live Sync broadcast event
  window.addEventListener("styno_sync_event", (e) => {
    showSyncNotification(e.detail);
    renderListings();
    renderOwnerPropertiesList();
    renderBookingsList();
    updateHeaderBadges();
  });

  // Close dropdowns on outside click
  document.addEventListener("click", (e) => {
    if (!e.target.closest(".filter-dropdown")) {
      document.querySelectorAll(".filter-dropdown-menu").forEach(el => el.classList.remove("show"));
    }
  });

  // Preset booking date
  const tomorrow = new Date();
  tomorrow.setDate(tomorrow.getDate() + 1);
  const dateInput = document.getElementById("bookingCheckInDate");
  if (dateInput) {
    dateInput.value = tomorrow.toISOString().split("T")[0];
    dateInput.min = new Date().toISOString().split("T")[0];
  }
});

/**
 * Main View Switcher (Simulates Android Navigation Compose)
 */
function switchView(viewName) {
  AppState.currentView = viewName;

  // 1. Hide all views
  document.querySelectorAll(".app-view").forEach(el => el.classList.remove("active"));

  // 2. Show target view
  const targetId = "view" + viewName.charAt(0).toUpperCase() + viewName.slice(1).toLowerCase();
  const targetEl = document.getElementById(targetId);
  if (targetEl) {
    targetEl.classList.add("active");
  }

  // 3. Update Mobile Bottom Bar active states
  const navMap = {
    HOME: "bNavHome",
    SEARCH: "bNavSearch",
    BOOKINGS: "bNavBookings",
    SAVED: "bNavSaved",
    PROFILE: "bNavProfile",
    OWNER: "bNavProfile"
  };

  document.querySelectorAll(".bottom-nav-item").forEach(b => b.classList.remove("active"));
  const activeNavId = navMap[viewName];
  if (activeNavId) {
    const navBtn = document.getElementById(activeNavId);
    if (navBtn) navBtn.classList.add("active");
  }

  // 4. View-specific renders
  if (viewName === "BOOKINGS") {
    renderBookingsList();
  } else if (viewName === "SAVED") {
    renderSavedList();
  } else if (viewName === "OWNER") {
    renderOwnerPropertiesList();
  } else if (viewName === "SEARCH") {
    const dedicatedInput = document.getElementById("dedicatedSearchInput");
    if (dedicatedInput) {
      dedicatedInput.value = AppState.searchQuery;
      renderDedicatedSearchResults();
    }
  }

  window.scrollTo({ top: 0, behavior: "smooth" });
}

/**
 * Toggle User Role (Guest <-> Owner)
 */
function toggleUserRole() {
  if (AppState.userRole === "GUEST") {
    switchToOwnerDashboard();
  } else {
    switchToGuestMode();
  }
}

function switchToOwnerDashboard() {
  AppState.userRole = "OWNER";
  const btn = document.getElementById("ownerToggleBtn");
  const icon = document.getElementById("ownerToggleIcon");
  const text = document.getElementById("ownerToggleText");
  const badge = document.getElementById("ownerRoleBadge");
  
  if (btn) btn.classList.add("active");
  if (icon) icon.textContent = "travel_explore";
  if (text) text.textContent = "Guest View";
  if (badge) badge.textContent = "Host Mode";

  switchView("OWNER");
  showToast("Switched to Styno Host & Owner Dashboard");
}

function switchToGuestMode() {
  AppState.userRole = "GUEST";
  const btn = document.getElementById("ownerToggleBtn");
  const icon = document.getElementById("ownerToggleIcon");
  const text = document.getElementById("ownerToggleText");
  const badge = document.getElementById("ownerRoleBadge");

  if (btn) btn.classList.remove("active");
  if (icon) icon.textContent = "add_business";
  if (text) text.textContent = "Owner Dashboard";
  if (badge) badge.textContent = "0% Fee";

  switchView("HOME");
  showToast("Switched to Guest Explorer View");
}

// --------------------------------------------------------------------------
// 7. RENDERING COMPONENTS (Category Track, Listings, Cards, Maps)
// --------------------------------------------------------------------------

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

function selectCategory(catKey) {
  AppState.selectedCategory = catKey || null;
  renderCategoryTrack();
  renderListings();
}

function renderStateCloud() {
  const container = document.getElementById("coverageStatesCloud");
  if (!container) return;

  container.innerHTML = INDIA_GEOGRAPHIC_DATA.map(st => `
    <button class="state-chip" onclick="selectStateFilter('${st.state}')">
      ${st.state} ${st.isUT ? '(UT)' : ''}
    </button>
  `).join("");
}

function selectStateFilter(stateName) {
  AppState.selectedState = stateName;
  AppState.selectedCity = null;
  AppState.selectedArea = null;
  updateLocationHeader();
  renderListings();
  showToast(`Filtered by ${stateName}`);
}

function renderListings() {
  const grid = document.getElementById("listingsGrid");
  const emptyState = document.getElementById("emptyState");
  const subtitle = document.getElementById("listingsCountSubtitle");
  if (!grid) return;

  let properties = StynoDB.getAllProperties();
  const savedIds = new Set(StynoDB.getSavedPropertyIds());

  // Filters
  if (AppState.selectedCategory) {
    properties = properties.filter(p => p.propertyType === AppState.selectedCategory);
  }
  if (AppState.selectedState) {
    properties = properties.filter(p => p.state.toLowerCase() === AppState.selectedState.toLowerCase());
  }
  if (AppState.selectedCity) {
    properties = properties.filter(p => p.city.toLowerCase() === AppState.selectedCity.toLowerCase());
  }
  if (AppState.selectedArea) {
    properties = properties.filter(p => p.area.toLowerCase().includes(AppState.selectedArea.toLowerCase()));
  }
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
  if (AppState.verifiedOnly) {
    properties = properties.filter(p => p.isVerified);
  }
  if (AppState.genderFilter) {
    properties = properties.filter(p => p.genderSuitability === AppState.genderFilter || p.genderSuitability === "ALL");
  }
  properties = properties.filter(p => p.startingPrice <= AppState.maxBudget);

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

  // Sort
  if (AppState.sortBy === "PRICE_LOW") {
    properties.sort((a, b) => a.startingPrice - b.startingPrice);
  } else if (AppState.sortBy === "PRICE_HIGH") {
    properties.sort((a, b) => b.startingPrice - a.startingPrice);
  } else if (AppState.sortBy === "RATING") {
    properties.sort((a, b) => (b.rating || 0) - (a.rating || 0));
  } else {
    properties.sort((a, b) => (b.isVerified ? 1 : 0) - (a.isVerified ? 1 : 0));
  }

  const catName = STYNO_CATEGORIES.find(c => c.key === AppState.selectedCategory)?.name || "Accommodations";
  if (subtitle) {
    subtitle.textContent = `Showing ${properties.length} verified ${catName.toLowerCase()} with 0% brokerage`;
  }

  if (properties.length === 0) {
    grid.innerHTML = "";
    if (emptyState) emptyState.style.display = "block";
    return;
  }

  if (emptyState) emptyState.style.display = "none";

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
            ${p.isOwnerCreated ? '<span class="badge" style="background:#0F2B5C;color:#FFF;">Owner Live</span>' : ''}
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
              <span class="zero-brokerage-tag">Direct Host &bull; 0% Fee</span>
            </div>

            <button class="btn btn-sm btn-primary" onclick="event.stopPropagation(); quickBookStay('${p.id}');">
              Book
            </button>
          </div>
        </div>
      </div>
    `;
  }).join("");

  renderMapMarkers(properties);
}

// --------------------------------------------------------------------------
// 8. INTERACTIVE MAP VIEW LOGIC
// --------------------------------------------------------------------------

function toggleMapViewMode() {
  AppState.isMapViewActive = !AppState.isMapViewActive;
  const mapSection = document.getElementById("mapViewSection");
  const listSection = document.getElementById("listingsSection");
  const toggleBtn = document.getElementById("mapViewToggleBtn");
  const toggleText = document.getElementById("mapToggleText");
  const toggleIcon = document.getElementById("mapToggleIcon");

  if (AppState.isMapViewActive) {
    if (mapSection) mapSection.style.display = "block";
    if (listSection) listSection.style.display = "none";
    if (toggleText) toggleText.textContent = "List View";
    if (toggleIcon) toggleIcon.textContent = "view_list";
  } else {
    if (mapSection) mapSection.style.display = "none";
    if (listSection) listSection.style.display = "block";
    if (toggleText) toggleText.textContent = "Map View";
    if (toggleIcon) toggleIcon.textContent = "map";
  }
}

function renderMapMarkers(properties) {
  const container = document.getElementById("mapMarkersLayer");
  if (!container) return;

  container.innerHTML = properties.map((p, idx) => {
    // Generate organic spread positions across simulated canvas
    const leftPct = 15 + ((idx * 27) % 70);
    const topPct = 20 + ((idx * 33) % 60);

    return `
      <div class="map-pin" style="left: ${leftPct}%; top: ${topPct}%;" onclick="showMapFloatingPreview('${p.id}')">
        ₹${p.startingPrice.toLocaleString('en-IN')}
      </div>
    `;
  }).join("");
}

function showMapFloatingPreview(propertyId) {
  const p = StynoDB.getAllProperties().find(item => item.id === propertyId);
  const preview = document.getElementById("mapFloatingPreview");
  if (!p || !preview) return;

  const heroImage = (p.images && p.images.length) ? p.images[0] : "";
  preview.style.display = "flex";
  preview.innerHTML = `
    <img src="${heroImage}" style="width: 80px; height: 80px; border-radius: 8px; object-fit: cover;">
    <div style="flex:1;">
      <h4 style="font-size: 0.95rem; font-weight: 700; margin-bottom: 0.2rem;">${p.name}</h4>
      <p style="font-size: 0.8rem; color: #64748B;">${p.area}, ${p.city}</p>
      <div style="font-size: 1rem; font-weight: 800; color: #0F2B5C; margin-top: 0.3rem;">
        ₹${p.startingPrice.toLocaleString('en-IN')} <small style="font-size: 0.75rem; font-weight: normal;">${getDurationUnitLabel(p.durationType)}</small>
      </div>
    </div>
    <button class="btn btn-sm btn-primary" onclick="openPropertyDetail('${p.id}')">View</button>
  `;
}

// --------------------------------------------------------------------------
// 9. DEDICATED SEARCH VIEW LOGIC (1:1 with SearchScreen.kt)
// --------------------------------------------------------------------------

function handleDedicatedSearch(val) {
  AppState.searchQuery = val;
  const clearBtn = document.getElementById("dedicatedSearchClear");
  if (clearBtn) clearBtn.style.display = val ? "flex" : "none";
  renderDedicatedSearchResults();
}

function clearDedicatedSearch() {
  AppState.searchQuery = "";
  const input = document.getElementById("dedicatedSearchInput");
  if (input) input.value = "";
  const clearBtn = document.getElementById("dedicatedSearchClear");
  if (clearBtn) clearBtn.style.display = "none";
  renderDedicatedSearchResults();
}

function applySearchTag(tagText) {
  AppState.searchQuery = tagText;
  const input = document.getElementById("dedicatedSearchInput");
  if (input) input.value = tagText;
  const clearBtn = document.getElementById("dedicatedSearchClear");
  if (clearBtn) clearBtn.style.display = "flex";
  renderDedicatedSearchResults();
}

function renderDedicatedSearchResults() {
  const grid = document.getElementById("searchResultsGrid");
  const title = document.getElementById("searchResultsTitle");
  const badge = document.getElementById("searchResultCountBadge");
  if (!grid) return;

  let properties = StynoDB.getAllProperties();
  if (AppState.searchQuery.trim()) {
    const q = AppState.searchQuery.toLowerCase().trim();
    properties = properties.filter(p => 
      p.name.toLowerCase().includes(q) ||
      p.city.toLowerCase().includes(q) ||
      p.area.toLowerCase().includes(q) ||
      p.propertyType.toLowerCase().includes(q) ||
      (p.amenities && p.amenities.some(a => a.toLowerCase().includes(q)))
    );
    if (title) title.textContent = `Results for "${AppState.searchQuery}"`;
  } else {
    if (title) title.textContent = "All Verified Stays";
  }

  if (badge) badge.textContent = `${properties.length} Stays`;

  const savedIds = new Set(StynoDB.getSavedPropertyIds());
  grid.innerHTML = properties.map(p => {
    const isSaved = savedIds.has(p.id);
    const heroImage = (p.images && p.images.length) ? p.images[0] : "";
    return `
      <div class="stay-card" onclick="openPropertyDetail('${p.id}')">
        <div class="card-media-wrapper">
          <img class="card-media-img" src="${heroImage}" alt="${p.name}">
          <div class="card-floating-badges"><span class="badge badge-emerald">0% BROKERAGE</span></div>
          <button class="card-save-btn ${isSaved ? 'active' : ''}" onclick="event.stopPropagation(); toggleSave('${p.id}');">
            <span class="material-symbols-rounded">${isSaved ? 'favorite' : 'favorite_border'}</span>
          </button>
        </div>
        <div class="card-content">
          <h3 class="stay-title">${p.name}</h3>
          <p class="stay-location-line">${p.area}, ${p.city}</p>
          <div class="card-bottom-row">
            <span class="price-amount">₹${p.startingPrice.toLocaleString('en-IN')}</span>
            <button class="btn btn-sm btn-primary" onclick="event.stopPropagation(); quickBookStay('${p.id}')">Book</button>
          </div>
        </div>
      </div>
    `;
  }).join("");
}

// --------------------------------------------------------------------------
// 10. MY BOOKINGS VIEW LOGIC (1:1 with MyBookingsScreen.kt)
// --------------------------------------------------------------------------

function filterBookingsTab(tabName) {
  AppState.activeBookingsTab = tabName;
  document.querySelectorAll(".bookings-tab-btn").forEach(b => b.classList.remove("active"));
  const activeBtn = document.getElementById("bkTab" + tabName.charAt(0).toUpperCase() + tabName.slice(1).toLowerCase());
  if (activeBtn) activeBtn.classList.add("active");
  renderBookingsList();
}

function renderBookingsList() {
  const container = document.getElementById("bookingsList");
  const emptyState = document.getElementById("emptyBookingsState");
  if (!container) return;

  let bookings = StynoDB.getBookings();
  if (AppState.activeBookingsTab !== "ALL") {
    bookings = bookings.filter(b => b.status === AppState.activeBookingsTab);
  }

  const badge = document.getElementById("bookingsCountBadge");
  const bNavBadge = document.getElementById("bNavBookingsBadge");
  const activeCount = StynoDB.getBookings().filter(b => b.status === "UPCOMING" || b.status === "ACTIVE").length;
  if (badge) {
    badge.textContent = activeCount;
    badge.style.display = activeCount > 0 ? "flex" : "none";
  }
  if (bNavBadge) {
    bNavBadge.textContent = activeCount;
    bNavBadge.style.display = activeCount > 0 ? "flex" : "none";
  }

  const pTotalBookings = document.getElementById("pTotalBookings");
  if (pTotalBookings) pTotalBookings.textContent = StynoDB.getBookings().length;

  if (bookings.length === 0) {
    container.innerHTML = "";
    if (emptyState) emptyState.style.display = "block";
    return;
  }

  if (emptyState) emptyState.style.display = "none";

  container.innerHTML = bookings.map(bk => `
    <div class="booking-item-card">
      <div class="booking-thumb-wrap">
        <img src="${bk.propertyImage}" alt="${bk.propertyName}">
      </div>

      <div class="bk-details">
        <div style="display: flex; align-items: center; gap: 0.5rem; margin-bottom: 0.3rem;">
          <span class="badge ${bk.status === 'CANCELLED' ? 'badge-primary' : 'badge-emerald'}">${bk.status}</span>
          <span style="font-size: 0.8rem; color: #64748B;">ID: #${bk.id}</span>
        </div>
        <h3>${bk.propertyName}</h3>
        <p class="bk-address"><span class="material-symbols-rounded" style="font-size: 0.95rem;">location_on</span> ${bk.propertyArea}, ${bk.propertyCity}</p>
        <div class="bk-meta-tags">
          <span class="amenity-micro-tag">Room: ${bk.roomType}</span>
          <span class="amenity-micro-tag">Check-in: ${bk.checkInDate}</span>
          <span class="amenity-micro-tag">Paid: ₹${bk.amountPaid.toLocaleString('en-IN')} (${bk.paymentMode})</span>
        </div>
      </div>

      <div class="bk-actions-col">
        <div class="bk-passcode-badge">
          <span class="bk-passcode-label">Check-in Passcode</span>
          <span class="bk-passcode-val">${bk.passcode}</span>
        </div>
        <div style="display: flex; gap: 0.4rem;">
          <a href="tel:+919811234567" class="btn btn-sm btn-outline">
            <span class="material-symbols-rounded">call</span> Call Host
          </a>
          ${bk.status !== 'CANCELLED' ? `
            <button class="btn btn-sm btn-outline" onclick="cancelUserBooking('${bk.id}')" style="color:#E11D48;">Cancel</button>
          ` : ''}
        </div>
      </div>
    </div>
  `).join("");
}

function cancelUserBooking(bookingId) {
  if (confirm("Are you sure you want to cancel this booking?")) {
    StynoDB.cancelBooking(bookingId);
    renderBookingsList();
    showToast("Booking cancelled successfully");
  }
}

// --------------------------------------------------------------------------
// 11. SAVED WISHLIST LOGIC (1:1 with SavedScreen.kt)
// --------------------------------------------------------------------------

function renderSavedList() {
  const grid = document.getElementById("savedListGrid");
  const emptyState = document.getElementById("emptySavedState");
  if (!grid) return;

  const savedIds = StynoDB.getSavedPropertyIds();
  const allProps = StynoDB.getAllProperties();
  const savedProps = allProps.filter(p => savedIds.includes(p.id));

  const badge = document.getElementById("savedCountBadge");
  const bNavBadge = document.getElementById("bNavSavedBadge");
  if (badge) {
    badge.textContent = savedIds.length;
    badge.style.display = savedIds.length > 0 ? "flex" : "none";
  }
  if (bNavBadge) {
    bNavBadge.textContent = savedIds.length;
    bNavBadge.style.display = savedIds.length > 0 ? "flex" : "none";
  }

  const pTotalSaved = document.getElementById("pTotalSaved");
  if (pTotalSaved) pTotalSaved.textContent = savedIds.length;

  if (savedProps.length === 0) {
    grid.innerHTML = "";
    if (emptyState) emptyState.style.display = "block";
    return;
  }

  if (emptyState) emptyState.style.display = "none";

  grid.innerHTML = savedProps.map(p => {
    const heroImage = (p.images && p.images.length) ? p.images[0] : "";
    return `
      <div class="stay-card" onclick="openPropertyDetail('${p.id}')">
        <div class="card-media-wrapper">
          <img class="card-media-img" src="${heroImage}" alt="${p.name}">
          <div class="card-floating-badges"><span class="badge badge-emerald">0% BROKERAGE</span></div>
          <button class="card-save-btn active" onclick="event.stopPropagation(); toggleSave('${p.id}');">
            <span class="material-symbols-rounded">favorite</span>
          </button>
        </div>
        <div class="card-content">
          <h3 class="stay-title">${p.name}</h3>
          <p class="stay-location-line">${p.area}, ${p.city}</p>
          <div class="card-bottom-row">
            <span class="price-amount">₹${p.startingPrice.toLocaleString('en-IN')}</span>
            <button class="btn btn-sm btn-primary" onclick="event.stopPropagation(); quickBookStay('${p.id}')">Book</button>
          </div>
        </div>
      </div>
    `;
  }).join("");
}

// --------------------------------------------------------------------------
// 12. OWNER DASHBOARD (1:1 with OwnerDashboardScreen.kt)
// --------------------------------------------------------------------------

function selectOwnerSubTab(tabName) {
  AppState.activeOwnerTab = tabName;
  document.querySelectorAll(".owner-tab-item").forEach(b => b.classList.remove("active"));
  document.querySelectorAll(".owner-sub-content").forEach(c => c.classList.remove("active"));

  const tabBtnMap = {
    PROPERTIES: "tabOwnerProps",
    BOOKINGS: "tabOwnerBookings",
    QUICK_STAY: "tabOwnerQuick",
    PAYMENT: "tabOwnerPayment",
    CANTEEN: "tabOwnerCanteen",
    REVIEWS: "tabOwnerReviews",
    EARNINGS: "tabOwnerEarnings",
    VERIFICATION: "tabOwnerVerification"
  };

  const contentMap = {
    PROPERTIES: "ownerSubContentProps",
    BOOKINGS: "ownerSubContentBookings",
    QUICK_STAY: "ownerSubContentQuick",
    PAYMENT: "ownerSubContentPayment",
    CANTEEN: "ownerSubContentCanteen",
    REVIEWS: "ownerSubContentReviews",
    EARNINGS: "ownerSubContentEarnings",
    VERIFICATION: "ownerSubContentVerification"
  };

  const activeTabBtn = document.getElementById(tabBtnMap[tabName]);
  const activeContent = document.getElementById(contentMap[tabName]);

  if (activeTabBtn) activeTabBtn.classList.add("active");
  if (activeContent) {
    activeContent.classList.add("active");
    activeContent.style.display = "block";
  }

  // Specific tab actions
  if (tabName === "BOOKINGS") {
    renderOwnerBookingsTable();
  } else if (tabName === "REVIEWS") {
    renderOwnerReviewsFeed();
  }
}

function renderOwnerPropertiesList() {
  const container = document.getElementById("ownerPropertiesList");
  const countBadge = document.getElementById("ownerPropsCount");
  if (!container) return;

  const props = StynoDB.getOwnerListings();
  if (countBadge) countBadge.textContent = props.length;

  if (props.length === 0) {
    container.innerHTML = `
      <div class="empty-results-card">
        <h3>No properties listed yet</h3>
        <p>List your stay on STYNO and connect directly with thousands of verified guests with 0% brokerage.</p>
        <button class="btn btn-primary" onclick="openAddPropertyModal()">Add Your First Stay</button>
      </div>
    `;
    return;
  }

  container.innerHTML = props.map(p => {
    const heroImage = (p.images && p.images.length) ? p.images[0] : "";
    return `
      <div class="owner-prop-row-card">
        <div class="owner-prop-thumb">
          <img src="${heroImage}" alt="${p.name}">
        </div>
        <div>
          <div style="display: flex; gap: 0.5rem; align-items: center; margin-bottom: 0.25rem;">
            <span class="badge badge-emerald">${p.propertyType}</span>
            <span style="font-size: 0.8rem; font-weight: 700; color: #047857;">0% Commission</span>
          </div>
          <h3 style="font-size: 1.1rem; font-weight: 700; color: #0F2B5C;">${p.name}</h3>
          <p style="font-size: 0.85rem; color: #64748B;">${p.area}, ${p.city}, ${p.state}</p>
          <div style="font-size: 0.95rem; font-weight: 800; color: #2563EB; margin-top: 0.35rem;">
            ₹${p.startingPrice.toLocaleString('en-IN')} <small style="font-size: 0.75rem; color: #64748B;">${getDurationUnitLabel(p.durationType)}</small>
          </div>
        </div>
        <div class="owner-prop-actions">
          <button class="btn btn-sm btn-outline" onclick="editPropertyAsOwner('${p.id}')">
            <span class="material-symbols-rounded">edit</span> Edit
          </button>
          <button class="btn btn-sm btn-outline" onclick="deletePropertyAsOwner('${p.id}')" style="color: #E11D48;">
            <span class="material-symbols-rounded">delete</span>
          </button>
        </div>
      </div>
    `;
  }).join("");
}

function renderOwnerBookingsTable() {
  const container = document.getElementById("ownerBookingsTable");
  const countBadge = document.getElementById("ownerBookingsCount");
  if (!container) return;

  const bookings = StynoDB.getBookings();
  if (countBadge) countBadge.textContent = bookings.length;

  container.innerHTML = bookings.map(b => `
    <div class="owner-prop-row-card" style="grid-template-columns: 1fr auto;">
      <div>
        <div style="display:flex; gap:0.5rem; align-items:center; margin-bottom:0.25rem;">
          <span class="badge badge-emerald">${b.status}</span>
          <span style="font-size:0.8rem; color:#64748B;">Passcode: <strong>${b.passcode}</strong></span>
        </div>
        <h4 style="font-size: 1.05rem; font-weight: 700; color: #0F2B5C;">Guest: ${b.guestName} (${b.guestPhone})</h4>
        <p style="font-size: 0.85rem; color: #64748B;">Property: ${b.propertyName} &bull; Room: ${b.roomType}</p>
        <p style="font-size: 0.825rem; font-weight: 600; color: #059669;">Settlement: ₹${b.amountPaid.toLocaleString('en-IN')} (Direct Instant UPI)</p>
      </div>
      <div>
        <a href="tel:${b.guestPhone}" class="btn btn-sm btn-primary">
          <span class="material-symbols-rounded">call</span> Call Guest
        </a>
      </div>
    </div>
  `).join("");
}

function renderOwnerReviewsFeed() {
  const container = document.getElementById("ownerReviewsFeed");
  if (!container) return;

  const mockReviews = [
    { author: "Sneha Patel", rating: 5, text: "Excellent stay! Clean RO drinking water, fast Wi-Fi and safe biometric entry.", time: "Yesterday" },
    { author: "Rohit Kumar", rating: 4.8, text: "The 3-time meals are authentic and tasty. Warden is very supportive.", time: "3 days ago" }
  ];

  container.innerHTML = mockReviews.map(r => `
    <div style="padding: 1rem; border-bottom: 1px solid #E2E8F0; margin-bottom: 0.5rem;">
      <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 0.3rem;">
        <strong>${r.author}</strong>
        <span class="rating-pill"><span class="material-symbols-rounded" style="color: #F59E0B; font-size: 1rem;">star</span> ${r.rating} &starf;</span>
      </div>
      <p style="font-size: 0.875rem; color: #334155;">${r.text}</p>
      <small style="color: #64748B;">${r.time}</small>
    </div>
  `).join("");
}

function openAddPropertyModal() {
  document.getElementById("ownerPropertyId").value = "";
  document.getElementById("ownerAddPropertyForm").reset();
  populateStateDropdownForOwner();
  openModal("addPropertyModal");
}

function editPropertyAsOwner(propertyId) {
  const prop = StynoDB.getAllProperties().find(p => p.id === propertyId);
  if (!prop) return;

  document.getElementById("ownerPropertyId").value = prop.id;
  document.getElementById("ownerPropType").value = prop.propertyType;
  document.getElementById("ownerGender").value = prop.genderSuitability || "ALL";
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

  openModal("addPropertyModal");
}

function deletePropertyAsOwner(propertyId) {
  if (confirm("Are you sure you want to remove this property listing from STYNO?")) {
    StynoDB.deletePropertyListing(propertyId);
    renderOwnerPropertiesList();
    renderListings();
    showToast("Property listing deleted successfully");
  }
}

function handleOwnerFormSubmit(e) {
  e.preventDefault();

  const id = document.getElementById("ownerPropertyId").value;
  const propType = document.getElementById("ownerPropType").value;
  const gender = document.getElementById("ownerGender").value;
  const name = document.getElementById("ownerPropName").value;
  const state = document.getElementById("ownerState").value;
  const city = document.getElementById("ownerCity").value;
  const area = document.getElementById("ownerArea").value;
  const landmark = document.getElementById("ownerLandmark")?.value || "";
  const address = document.getElementById("ownerAddress").value;
  const startingPrice = Number(document.getElementById("ownerStartingPrice").value);
  const durationType = document.getElementById("ownerDurationType").value;
  const deposit = Number(document.getElementById("ownerSecurityDeposit")?.value || 0);
  const ownerName = document.getElementById("ownerName").value;
  const ownerPhone = document.getElementById("ownerPhone").value;

  // Selected Amenities
  const amenities = [];
  document.querySelectorAll("input[name='ownerAmenity']:checked").forEach(cb => amenities.push(cb.value));

  const propData = {
    id: id || undefined,
    name,
    propertyType: propType,
    genderSuitability: gender,
    description: `Verified ${propType.toLowerCase()} accommodation in ${area}, ${city}. Direct host stay with 0% brokerage.`,
    address,
    city,
    state,
    area,
    pincode: "110001",
    startingPrice,
    durationType,
    amenities,
    roomOptions: [
      { type: "Standard Room", price: startingPrice, bedsAvailable: 3, deposit }
    ],
    owner: {
      name: ownerName,
      phone: ownerPhone,
      verified: true
    },
    images: [
      "https://images.unsplash.com/photo-1555854877-bab0e564b8d5?w=800&q=80",
      "https://images.unsplash.com/photo-1522771739844-6a9f6d5f14af?w=800&q=80"
    ],
    isVerified: true,
    isInstantBookable: true,
    isZeroBrokerage: true,
    isAvailable: true
  };

  StynoDB.savePropertyListing(propData);
  closeModal("addPropertyModal");
  renderOwnerPropertiesList();
  renderListings();
  showToast(`Property "${name}" published and synced live!`);
}

function populateStateDropdownForOwner() {
  const stateSelect = document.getElementById("ownerState");
  if (!stateSelect) return;

  stateSelect.innerHTML = INDIA_GEOGRAPHIC_DATA.map(st => `
    <option value="${st.state}">${st.state} ${st.isUT ? '(UT)' : ''}</option>
  `).join("");

  populateCitiesForOwner(INDIA_GEOGRAPHIC_DATA[0].state);
}

function populateCitiesForOwner(stateName) {
  const citySelect = document.getElementById("ownerCity");
  if (!citySelect) return;

  const found = INDIA_GEOGRAPHIC_DATA.find(s => s.state === stateName);
  const cities = found ? found.cities : ["Main City"];

  citySelect.innerHTML = cities.map(c => `
    <option value="${c}">${c}</option>
  `).join("");
}

function saveQuickStayPricing() {
  showToast("Quick Stay hourly rates updated successfully");
}

function saveOwnerPaymentDetails(e) {
  e.preventDefault();
  showToast("Host payment settlement details saved & verified");
}

// --------------------------------------------------------------------------
// 13. BOOKING FLOW & CHECKOUT LOGIC
// --------------------------------------------------------------------------

function openPropertyDetail(propertyId) {
  const prop = StynoDB.getAllProperties().find(p => p.id === propertyId);
  if (!prop) return;

  AppState.activePropertyForDetail = prop;

  // Fill in modal details
  document.getElementById("modalPropertyName").textContent = prop.name;
  document.getElementById("modalPropertyAddress").innerHTML = `<span class="material-symbols-rounded">location_on</span> ${prop.area}, ${prop.city}, ${prop.state}`;
  document.getElementById("modalPropertyTypeBadge").textContent = prop.propertyType;
  document.getElementById("modalGenderBadge").textContent = prop.genderSuitability || "ALL GUESTS";
  document.getElementById("modalStartingPrice").textContent = `₹${prop.startingPrice.toLocaleString('en-IN')}`;
  document.getElementById("modalDurationType").textContent = prop.durationType;
  document.getElementById("modalRating").innerHTML = `&#9733; ${prop.rating || 4.8} (${prop.reviewCount || 10} reviews)`;
  document.getElementById("modalFooterPrice").textContent = `₹${prop.startingPrice.toLocaleString('en-IN')}`;
  document.getElementById("modalFooterPriceSub").textContent = `${getDurationUnitLabel(prop.durationType)} &bull; 0% brokerage`;

  // Gallery
  const gallery = document.getElementById("modalGalleryGrid");
  if (gallery) {
    const imgs = prop.images && prop.images.length ? prop.images : ["https://images.unsplash.com/photo-1555854877-bab0e564b8d5?w=800&q=80"];
    gallery.innerHTML = `
      <img src="${imgs[0]}" alt="${prop.name}">
      <img src="${imgs[1] || imgs[0]}" alt="${prop.name}">
    `;
  }

  // Room Options
  const roomGrid = document.getElementById("modalRoomOptionsGrid");
  if (roomGrid) {
    const opts = prop.roomOptions && prop.roomOptions.length ? prop.roomOptions : [{ type: "Standard Room", price: prop.startingPrice, bedsAvailable: 2 }];
    AppState.selectedRoomOption = opts[0];

    roomGrid.innerHTML = opts.map((opt, idx) => `
      <div class="room-option-card ${idx === 0 ? 'selected' : ''}" onclick="selectRoomOption(${idx}, this)">
        <div style="font-weight: 700; color: #0F2B5C; margin-bottom: 0.25rem;">${opt.type}</div>
        <div style="font-size: 1.15rem; font-weight: 800; color: #2563EB;">₹${opt.price.toLocaleString('en-IN')}</div>
        <div style="font-size: 0.75rem; color: #059669; font-weight: 600;">${opt.bedsAvailable || 1} available</div>
      </div>
    `).join("");
  }

  // Amenities
  const amenitiesGrid = document.getElementById("modalAmenitiesGrid");
  if (amenitiesGrid) {
    amenitiesGrid.innerHTML = (prop.amenities || []).map(a => `
      <span class="amenity-tag"><span class="material-symbols-rounded text-emerald">check_circle</span> ${a}</span>
    `).join("");
  }

  // Guidelines & Curfew
  const rulesGrid = document.getElementById("modalRulesGrid");
  if (rulesGrid) {
    rulesGrid.innerHTML = (prop.rules || ["Gate curfew 10:30 PM", "Zero smoking inside premises", "Govt ID proof required at check-in"]).map(r => `
      <div class="rule-item"><span class="material-symbols-rounded text-primary">policy</span> ${r}</div>
    `).join("");
  }

  // Owner Card
  const ownerCard = document.getElementById("modalOwnerCard");
  if (ownerCard) {
    ownerCard.innerHTML = `
      <div class="owner-meta-row">
        <div class="owner-avatar-icon"><span class="material-symbols-rounded">person</span></div>
        <div>
          <h4 style="font-weight: 700; color: #0F2B5C;">${prop.owner?.name || "Verified Property Host"}</h4>
          <span class="badge badge-verified"><span class="material-symbols-rounded" style="font-size: 0.8rem;">verified</span> Verified Styno Host</span>
        </div>
      </div>
      <a href="tel:${prop.owner?.phone || '+919811234567'}" class="btn btn-sm btn-outline">
        <span class="material-symbols-rounded">call</span> Contact Host
      </a>
    `;
  }

  openModal("propertyDetailModal");
}

function selectRoomOption(index, el) {
  document.querySelectorAll(".room-option-card").forEach(c => c.classList.remove("selected"));
  if (el) el.classList.add("selected");
  if (AppState.activePropertyForDetail?.roomOptions) {
    AppState.selectedRoomOption = AppState.activePropertyForDetail.roomOptions[index];
    const price = AppState.selectedRoomOption.price;
    document.getElementById("modalFooterPrice").textContent = `₹${price.toLocaleString('en-IN')}`;
  }
}

function quickBookStay(propertyId) {
  const prop = StynoDB.getAllProperties().find(p => p.id === propertyId);
  if (!prop) return;
  AppState.activePropertyForBooking = prop;
  AppState.selectedRoomOption = (prop.roomOptions && prop.roomOptions.length) ? prop.roomOptions[0] : { type: "Standard Room", price: prop.startingPrice };
  openBookingCheckoutModal();
}

function proceedToBookingFromModal() {
  closeModal("propertyDetailModal");
  if (AppState.activePropertyForDetail) {
    AppState.activePropertyForBooking = AppState.activePropertyForDetail;
    openBookingCheckoutModal();
  }
}

function openBookingCheckoutModal() {
  const prop = AppState.activePropertyForBooking;
  if (!prop) return;

  const room = AppState.selectedRoomOption || { type: "Standard Room", price: prop.startingPrice };

  // Summary
  const summaryBox = document.getElementById("bookingStayMiniSummary");
  if (summaryBox) {
    summaryBox.innerHTML = `
      <div style="display: flex; gap: 1rem; align-items: center; padding: 1rem; background: #F1F5F9; border-radius: 12px; margin-bottom: 1rem;">
        <img src="${prop.images?.[0] || ''}" style="width: 70px; height: 70px; border-radius: 8px; object-fit: cover;">
        <div>
          <h4 style="font-weight: 700; color: #0F2B5C;">${prop.name}</h4>
          <p style="font-size: 0.825rem; color: #64748B;">${prop.area}, ${prop.city}</p>
          <span class="badge badge-emerald" style="margin-top: 0.2rem;">Room: ${room.type}</span>
        </div>
      </div>
    `;
  }

  recalculateBookingTotal();
  openModal("bookingModal");
}

function recalculateBookingTotal() {
  const prop = AppState.activePropertyForBooking;
  if (!prop) return;

  const room = AppState.selectedRoomOption || { price: prop.startingPrice };
  const durationMonths = Number(document.getElementById("bookingDuration")?.value || 1);

  const basePrice = room.price * durationMonths;
  const serviceFee = 149;
  const finalTotal = basePrice + serviceFee;

  document.getElementById("bkBaseRent").textContent = `₹${basePrice.toLocaleString('en-IN')}`;
  document.getElementById("bkServiceFee").textContent = `₹${serviceFee}`;
  document.getElementById("bkFinalAmount").textContent = `₹${finalTotal.toLocaleString('en-IN')}`;
  document.getElementById("btnPayAmount").textContent = `₹${finalTotal.toLocaleString('en-IN')}`;
}

function confirmAndExecuteBooking() {
  const prop = AppState.activePropertyForBooking;
  if (!prop) return;

  const guestName = document.getElementById("bookingGuestName").value.trim();
  const guestPhone = document.getElementById("bookingGuestPhone").value.trim();
  const guestEmail = document.getElementById("bookingGuestEmail").value.trim();
  const checkInDate = document.getElementById("bookingCheckInDate").value;
  const durationMonths = Number(document.getElementById("bookingDuration").value);
  const paymentMode = document.querySelector("input[name='paymentMode']:checked")?.value || "UPI";

  if (!guestName || !guestPhone) {
    alert("Please enter primary guest name and mobile phone.");
    return;
  }

  const bookingId = "STY-2026-" + Math.floor(1000 + Math.random() * 9000);
  const passcode = "STY-" + Math.floor(1000 + Math.random() * 9000);
  const room = AppState.selectedRoomOption || { type: "Standard Room", price: prop.startingPrice };
  const amountPaid = (room.price * durationMonths) + 149;

  const bookingRecord = {
    id: bookingId,
    propertyId: prop.id,
    propertyName: prop.name,
    propertyArea: prop.area,
    propertyCity: prop.city,
    propertyImage: prop.images?.[0] || "",
    roomType: room.type,
    checkInDate,
    durationMonths,
    guestName,
    guestPhone,
    guestEmail,
    passcode,
    status: "UPCOMING",
    amountPaid,
    paymentMode,
    createdAt: Date.now()
  };

  StynoDB.saveBooking(bookingRecord);
  closeModal("bookingModal");

  // Show Confirmation Modal
  document.getElementById("successBookingId").textContent = `Booking ID: #${bookingId}`;
  document.getElementById("successPasscode").textContent = passcode;
  document.getElementById("bookingSuccessDetails").innerHTML = `
    <div style="background: #F8FAFC; border-radius: 12px; padding: 1rem; margin: 1rem 0; text-align: left; font-size: 0.85rem;">
      <p><strong>Property:</strong> ${prop.name}</p>
      <p><strong>Check-in Date:</strong> ${checkInDate}</p>
      <p><strong>Primary Guest:</strong> ${guestName} (${guestPhone})</p>
      <p><strong>Host Direct Contact:</strong> ${prop.owner?.phone || '+91 98112 34567'}</p>
      <p><strong>Brokerage Fee:</strong> ₹0 FREE</p>
    </div>
  `;

  openModal("bookingSuccessModal");
  showToast(`Booking #${bookingId} confirmed! Check-in passcode generated.`);
}

// --------------------------------------------------------------------------
// 14. LOCATION HIERARCHY SELECTOR (Country -> State -> City -> Area)
// --------------------------------------------------------------------------

function openLocationPickerModal() {
  AppState.locationStep = "COUNTRY";
  renderLocationHierarchy();
  openModal("locationPickerModal");
}

function setLocationStep(step) {
  AppState.locationStep = step;
  renderLocationHierarchy();
}

function renderLocationHierarchy() {
  const list = document.getElementById("locationItemsList");
  if (!list) return;

  // Update pills
  ["stepCountryBtn", "stepStateBtn", "stepCityBtn", "stepAreaBtn"].forEach(id => {
    const el = document.getElementById(id);
    if (el) el.classList.remove("active");
  });

  if (AppState.locationStep === "COUNTRY") {
    document.getElementById("stepCountryBtn")?.classList.add("active");
    list.innerHTML = `
      <button class="loc-item-btn" onclick="setLocationStep('STATE')">
        <strong>🇮🇳 India</strong> (36 States &amp; UTs)
      </button>
    `;
  } else if (AppState.locationStep === "STATE") {
    document.getElementById("stepStateBtn")?.classList.add("active");
    list.innerHTML = INDIA_GEOGRAPHIC_DATA.map(st => `
      <button class="loc-item-btn" onclick="selectStateFromPicker('${st.state}')">
        ${st.state} ${st.isUT ? '<small>(UT)</small>' : ''}
      </button>
    `).join("");
  } else if (AppState.locationStep === "CITY") {
    document.getElementById("stepCityBtn")?.classList.add("active");
    const found = INDIA_GEOGRAPHIC_DATA.find(s => s.state === AppState.selectedState);
    const cities = found ? found.cities : ["Noida", "Bengaluru", "New Delhi", "Kota", "Bagaha"];
    list.innerHTML = cities.map(c => `
      <button class="loc-item-btn" onclick="selectCityFromPicker('${c}')">
        <span class="material-symbols-rounded text-primary" style="font-size:1rem;">location_city</span> ${c}
      </button>
    `).join("");
  } else if (AppState.locationStep === "AREA") {
    document.getElementById("stepAreaBtn")?.classList.add("active");
    const areas = ["All Areas", "Sector 62", "Electronic City", "Connaught Place", "Cyber City", "Indra Vihar", "Station Road"];
    list.innerHTML = areas.map(a => `
      <button class="loc-item-btn" onclick="selectAreaFromPicker('${a}')">
        ${a}
      </button>
    `).join("");
  }
}

function selectStateFromPicker(stName) {
  AppState.selectedState = stName;
  AppState.locationStep = "CITY";
  renderLocationHierarchy();
}

function selectCityFromPicker(cityName) {
  AppState.selectedCity = cityName;
  AppState.locationStep = "AREA";
  renderLocationHierarchy();
}

function selectAreaFromPicker(areaName) {
  AppState.selectedArea = areaName === "All Areas" ? null : areaName;
  updateLocationHeader();
  renderListings();
  closeModal("locationPickerModal");
  showToast(`Location set to ${AppState.selectedCity || AppState.selectedState}`);
}

function resetToAllIndia() {
  AppState.selectedState = null;
  AppState.selectedCity = null;
  AppState.selectedArea = null;
  updateLocationHeader();
  renderListings();
  closeModal("locationPickerModal");
  showToast("Showing all verified stays across India");
}

function updateLocationHeader() {
  const current = document.getElementById("headerCurrentLocation");
  const breadcrumb = document.getElementById("activeLocationBreadcrumb");
  const locString = AppState.selectedArea 
    ? `${AppState.selectedArea}, ${AppState.selectedCity}` 
    : (AppState.selectedCity || AppState.selectedState || "All India");

  if (current) current.textContent = locString;
  if (breadcrumb) breadcrumb.textContent = `${locString} • 0% Brokerage`;
}

function filterLocationList(searchQuery) {
  const q = searchQuery.toLowerCase().trim();
  const buttons = document.querySelectorAll(".loc-item-btn");
  buttons.forEach(btn => {
    btn.style.display = btn.textContent.toLowerCase().includes(q) ? "block" : "none";
  });
}

// --------------------------------------------------------------------------
// 15. SAFETY CENTER & EMERGENCY SOS (1:1 with SafetySosDialog.kt)
// --------------------------------------------------------------------------

function openSafetyCenterModal() {
  openModal("safetySosModal");
}

function openNotificationsModal() {
  const list = document.getElementById("notificationsList");
  if (list) {
    list.innerHTML = `
      <div style="padding: 0.85rem; border-bottom: 1px solid #E2E8F0;">
        <span class="badge badge-emerald">Booking Confirmed</span>
        <p style="font-size: 0.875rem; margin-top: 0.25rem;">Your stay at Styno Orchid Girls Elite Hostel is confirmed. Passcode: STY-9941</p>
        <small style="color: #64748B;">2 hours ago</small>
      </div>
      <div style="padding: 0.85rem;">
        <span class="badge badge-primary">0% Brokerage Saved</span>
        <p style="font-size: 0.875rem; margin-top: 0.25rem;">You saved ₹6,500 in agent fees by booking direct with host.</p>
        <small style="color: #64748B;">1 day ago</small>
      </div>
    `;
  }
  openModal("notificationsModal");
}

// --------------------------------------------------------------------------
// 16. FILTER HANDLERS & HELPERS
// --------------------------------------------------------------------------

function toggleVerifiedOnlyFilter() {
  AppState.verifiedOnly = !AppState.verifiedOnly;
  const chip = document.getElementById("chipVerifiedOnly");
  if (chip) chip.classList.toggle("active", AppState.verifiedOnly);
  renderListings();
}

function toggleDropdown(menuId) {
  const menu = document.getElementById(menuId);
  if (menu) menu.classList.toggle("show");
}

function selectGenderFilter(gender) {
  AppState.genderFilter = gender;
  const label = document.getElementById("genderDropdownLabel");
  const genderNames = {
    BOYS_ONLY: "Boys Only",
    GIRLS_ONLY: "Girls Only",
    CO_ED: "Co-Ed",
    FAMILY: "Family Stays"
  };
  if (label) label.textContent = gender ? `Guest: ${genderNames[gender]}` : "Guest: Anyone";
  document.getElementById("genderDropdownMenu")?.classList.remove("show");
  renderListings();
}

function handlePriceRangeChange(val) {
  AppState.maxBudget = Number(val);
  const display = document.getElementById("priceSliderValue");
  if (display) display.textContent = `Up to ₹${Number(val).toLocaleString('en-IN')}`;
  const label = document.getElementById("priceDropdownLabel");
  if (label) label.textContent = `Budget: ₹${Number(val) / 1000}k`;
  renderListings();
}

function setPriceBudget(budget) {
  const input = document.getElementById("priceRangeInput");
  if (input) input.value = budget;
  handlePriceRangeChange(budget);
  document.getElementById("priceDropdownMenu")?.classList.remove("show");
}

function toggleAmenityFilter(amenity) {
  const chipMap = {
    "AC": "chipAc",
    "Wi-Fi": "chipWifi",
    "Meals": "chipFood",
    "Attached Washroom": "chipAttached"
  };

  if (AppState.activeAmenities.has(amenity)) {
    AppState.activeAmenities.delete(amenity);
    document.getElementById(chipMap[amenity])?.classList.remove("active");
  } else {
    AppState.activeAmenities.add(amenity);
    document.getElementById(chipMap[amenity])?.classList.add("active");
  }

  const resetBtn = document.getElementById("resetFiltersBtn");
  if (resetBtn) resetBtn.style.display = (AppState.activeAmenities.size > 0 || AppState.verifiedOnly || AppState.genderFilter) ? "inline-flex" : "none";

  renderListings();
}

function resetAllFilters() {
  AppState.selectedCategory = null;
  AppState.selectedState = null;
  AppState.selectedCity = null;
  AppState.selectedArea = null;
  AppState.searchQuery = "";
  AppState.verifiedOnly = false;
  AppState.genderFilter = null;
  AppState.maxBudget = 35000;
  AppState.activeAmenities.clear();

  document.querySelectorAll(".chip-filter").forEach(c => c.classList.remove("active"));
  document.getElementById("resetFiltersBtn").style.display = "none";
  updateLocationHeader();
  renderCategoryTrack();
  renderListings();
  showToast("All filters cleared");
}

function handleSearchInput(val) {
  AppState.searchQuery = val;
  const headerClear = document.getElementById("headerSearchClearBtn");
  const mobileClear = document.getElementById("mobileSearchClearBtn");
  if (headerClear) headerClear.style.display = val ? "flex" : "none";
  if (mobileClear) mobileClear.style.display = val ? "flex" : "none";
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

function handleSortChange(sortVal) {
  AppState.sortBy = sortVal;
  renderListings();
}

function filterByQuickStayDuration(hours) {
  selectCategory("QUICK_STAY");
  showToast(`Filtering Quick Stay pods for ${hours}`);
}

function toggleSave(propertyId) {
  const isSaved = StynoDB.toggleSaveProperty(propertyId);
  renderListings();
  renderSavedList();
  showToast(isSaved ? "Saved to wishlist" : "Removed from wishlist");
}

function toggleSaveCurrentProperty() {
  if (AppState.activePropertyForDetail) {
    toggleSave(AppState.activePropertyForDetail.id);
  }
}

function updateHeaderBadges() {
  const savedIds = StynoDB.getSavedPropertyIds();
  const bookings = StynoDB.getBookings().filter(b => b.status === "UPCOMING" || b.status === "ACTIVE");

  const sBadge = document.getElementById("savedCountBadge");
  const bBadge = document.getElementById("bookingsCountBadge");
  const bNavS = document.getElementById("bNavSavedBadge");
  const bNavB = document.getElementById("bNavBookingsBadge");

  if (sBadge) {
    sBadge.textContent = savedIds.length;
    sBadge.style.display = savedIds.length > 0 ? "flex" : "none";
  }
  if (bBadge) {
    bBadge.textContent = bookings.length;
    bBadge.style.display = bookings.length > 0 ? "flex" : "none";
  }
  if (bNavS) {
    bNavS.textContent = savedIds.length;
    bNavS.style.display = savedIds.length > 0 ? "flex" : "none";
  }
  if (bNavB) {
    bNavB.textContent = bookings.length;
    bNavB.style.display = bookings.length > 0 ? "flex" : "none";
  }
}

function showSyncNotification(detail) {
  const banner = document.getElementById("syncBanner");
  const text = document.getElementById("syncBannerText");
  if (!banner || !text) return;

  text.textContent = `Real-time sync complete: "${detail.propertyName}" was ${detail.action} across Styno`;
  banner.style.display = "flex";
  setTimeout(() => {
    banner.style.display = "none";
  }, 4500);
}

function showToast(msg) {
  const container = document.getElementById("toastContainer");
  if (!container) return;

  const toast = document.createElement("div");
  toast.className = "toast";
  toast.innerHTML = `<span class="material-symbols-rounded text-emerald">info</span> <span>${msg}</span>`;
  container.appendChild(toast);

  setTimeout(() => {
    toast.remove();
  }, 3200);
}

function getDurationUnitLabel(durationType) {
  switch (durationType) {
    case "DAILY": return "/night";
    case "HOURLY": return "/slot";
    case "WEEKLY": return "/week";
    default: return "/month";
  }
}

function getCategoryBadgeInfo(propType) {
  const found = STYNO_CATEGORIES.find(c => c.key === propType);
  return found || { name: "Stay", emoji: "🏠" };
}

// Modal open/close helpers
function openModal(modalId) {
  const el = document.getElementById(modalId);
  if (el) el.classList.add("show");
  document.body.style.overflow = "hidden";
}

function closeModal(modalId) {
  const el = document.getElementById(modalId);
  if (el) el.classList.remove("show");
  document.body.style.overflow = "";
}

function closeOnBackdrop(event, modalId) {
  if (event.target.classList.contains("styno-modal-overlay")) {
    closeModal(modalId);
  }
}
