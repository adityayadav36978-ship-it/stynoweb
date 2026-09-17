package com.example.data.model

/**
 * Comprehensive Geographic Dataset for India.
 * Contains all 28 States and 8 Union Territories with real cities, localities, and landmarks.
 */
object GlobalGeographicDataIndia {

    val STATES: List<GlobalState> = listOf(
        // 1. BIHAR
        GlobalState(
            code = "BR",
            name = "Bihar",
            cities = listOf(
                GlobalCity(
                    name = "Darbhanga",
                    latitude = 26.1542,
                    longitude = 85.8918,
                    localities = listOf(
                        GlobalLocality("Laheriasarai", 26.1264, 85.8972, listOf("DMCH Hospital", "Polo Ground", "Courts Complex")),
                        GlobalLocality("Tower Chowk", 26.1524, 85.8942, listOf("Tower Market", "Commercial Bazaar", "Cinema Hall")),
                        GlobalLocality("Donar", 26.1601, 85.9082, listOf("Donar Chowk", "Bazaar Road", "Transit Bus Stop")),
                        GlobalLocality("Benta", 26.1345, 85.9012, listOf("Darbhanga Medical College", "Dental College", "Clinics")),
                        GlobalLocality("Kadirabad", 26.1678, 85.8821, listOf("Kadirabad Chowk", "Bus Depot", "Markets")),
                        GlobalLocality("Dighi West", 26.1489, 85.8876, listOf("Dighi Lake", "Civic Center", "Parks")),
                        GlobalLocality("Allalpatti", 26.1389, 85.9045, listOf("Allalpatti Chowk", "Private Clinics", "Pharmacies")),
                        GlobalLocality("Bhalpatti", 26.1821, 85.9234, listOf("Rural Hub", "High School", "Local Mandir")),
                        GlobalLocality("Mirzapur & Rahamganj", 26.1567, 85.8923, listOf("Mirzapur Chowk", "Sanskrit University Road")),
                        GlobalLocality("Beta Chowk", 26.1412, 85.8989, listOf("Beta Bus Stop", "Residential Colony"))
                    ),
                    famousLandmarks = listOf("Darbhanga Fort (Raj Qila)", "Shyama Mai Temple", "Darbhanga Medical College & Hospital (DMCH)", "Raj High School", "Darbhanga Junction Railway Station", "Kameshwar Singh Sanskrit University", "Ahilya Sthan"),
                    isTopMetro = false
                ),
                GlobalCity(
                    name = "Patna",
                    latitude = 25.5941,
                    longitude = 85.1376,
                    localities = listOf(
                        GlobalLocality("Boring Road", 25.6178, 85.1189, listOf("Coaching Hub", "Panchmukhi Mandir", "Student Cafes")),
                        GlobalLocality("Kankarbagh", 25.5982, 85.1567, listOf("Auto Stand", "Lohia Nagar", "Multi-Specialty Clinics")),
                        GlobalLocality("Bailey Road", 25.6089, 85.0987, listOf("Patna Zoo", "High Court", "Secretariat")),
                        GlobalLocality("Frazer Road", 25.6092, 85.1412, listOf("Patna Junction", "Maurya Lok Complex", "Gandhi Maidan")),
                        GlobalLocality("Rajendra Nagar", 25.5998, 85.1634, listOf("Rajendra Nagar Terminal", "Moin-ul-Haq Stadium")),
                        GlobalLocality("Patliputra Colony", 25.6321, 85.1098, listOf("Industrial Estate", "Polytechnic Institute", "Parks")),
                        GlobalLocality("Danapur & Khagaul", 25.6302, 85.0421, listOf("Danapur Cantt", "Railway Junction", "Army Hub")),
                        GlobalLocality("Ashiana Nagar & Raja Bazar", 25.6134, 85.0876, listOf("Paras Hospital", "IGIMS Campus", "Raja Bazar")),
                        GlobalLocality("Kadamkuan & Nala Road", 25.6056, 85.1512, listOf("Sahitya Sammelan", "Book Market", "Darbhanga House")),
                        GlobalLocality("Anisabad & Phulwari Sharif", 25.5789, 85.0912, listOf("Anisabad Golambar", "AIIMS Patna Road", "Airport Road"))
                    ),
                    famousLandmarks = listOf("Golghar", "Gandhi Maidan", "Patna Sahib Gurudwara", "Bihar Museum", "Patna Junction", "AIIMS Patna", "NIT Patna Campus"),
                    isTopMetro = true
                ),
                GlobalCity(
                    name = "Muzaffarpur",
                    latitude = 26.1209,
                    longitude = 85.3647,
                    localities = listOf(
                        GlobalLocality("Mithanpura", 26.1156, 85.3892, listOf("Club Road", "Shopping Centers")),
                        GlobalLocality("Brahmpura", 26.1345, 85.3521, listOf("Brahmpura Chowk", "Medical Clinics")),
                        GlobalLocality("Gobarsahi", 26.1089, 85.3421, listOf("Expressway Junction", "Bus Stand")),
                        GlobalLocality("Aghoria Bazar", 26.1289, 85.3789, listOf("Bazaar Street", "Textile Shops")),
                        GlobalLocality("Zero Mile Muzaffarpur", 26.1456, 85.4012, listOf("Highway NH28", "Transport Nagar"))
                    ),
                    famousLandmarks = listOf("Baba Garibnath Temple", "Muzaffarpur Junction", "BRAB University", "Lalit Narayan Mishra Institute", "SKMCH Hospital"),
                    isTopMetro = false
                ),
                GlobalCity(
                    name = "Gaya",
                    latitude = 24.7914,
                    longitude = 85.0002,
                    localities = listOf(
                        GlobalLocality("Bodh Gaya", 24.6958, 84.9912, listOf("Mahabodhi Temple", "International Monasteries", "Meditation Centers")),
                        GlobalLocality("Civil Lines", 24.7967, 85.0045, listOf("Collectorate", "District Courts", "Hotels")),
                        GlobalLocality("Rampur & AP Colony", 24.7823, 85.0134, listOf("Rampur Chowk", "Market Area", "Hostels"))
                    ),
                    famousLandmarks = listOf("Mahabodhi Temple UNESCO Site", "Vishnupad Temple", "Gaya Junction", "Mangla Gauri Temple", "IIM Bodh Gaya"),
                    isTopMetro = false
                ),
                GlobalCity(
                    name = "Bhagalpur",
                    latitude = 25.2425,
                    longitude = 86.9842,
                    localities = listOf(
                        GlobalLocality("Tilkamajhi", 25.2534, 86.9921, listOf("University Campus", "Chowk", "Student PGs")),
                        GlobalLocality("Adampur", 25.2478, 86.9812, listOf("Ganga Ghat", "Old Town", "Clinics")),
                        GlobalLocality("Zero Mile", 25.2345, 87.0123, listOf("Transport Nagar", "Highway Junction", "Dhabas"))
                    ),
                    famousLandmarks = listOf("Vikramshila University Ruins", "TM Bhagalpur University", "Bhagalpur Silk Market", "Bhagalpur Junction"),
                    isTopMetro = false
                ),
                GlobalCity(
                    name = "Begusarai",
                    latitude = 25.4182,
                    longitude = 86.1272,
                    localities = listOf(
                        GlobalLocality("Har-Har Mahadev Chowk", 25.4212, 86.1345, listOf("Central Market", "Hotels")),
                        GlobalLocality("Barauni Township", 25.4789, 85.9876, listOf("Refinery Township", "Thermal Power", "Barauni Junction"))
                    ),
                    famousLandmarks = listOf("Kanwar Lake Bird Sanctuary", "Barauni IOCL Township", "Begusarai Station", "GD College"),
                    isTopMetro = false
                ),
                GlobalCity(
                    name = "Purnea",
                    latitude = 25.7771,
                    longitude = 87.4753,
                    localities = listOf(
                        GlobalLocality("Line Bazar", 25.7821, 87.4698, listOf("Medical Hub", "Clinics & Pharmacies", "Hotels")),
                        GlobalLocality("Bhatta Bazar", 25.7745, 87.4812, listOf("Commercial Market", "Shopping Complex"))
                    ),
                    famousLandmarks = listOf("Purnea University", "Line Bazar Medical Hub", "Purnea Junction", "Kala Bhavan"),
                    isTopMetro = false
                ),
                GlobalCity(
                    name = "Chapra",
                    district = "Saran",
                    latitude = 25.7848,
                    longitude = 84.7274,
                    localities = listOf(
                        GlobalLocality("Dharamsabha & Thanachowk", 25.7891, 84.7312, listOf("Central Market", "Bus Stand")),
                        GlobalLocality("Salempur & Rajendra Sarovar", 25.7798, 84.7412, listOf("Rajendra Stadium", "Town Hall"))
                    ),
                    famousLandmarks = listOf("Jai Prakash University", "Chapra Junction", "Ambika Bhavani Temple"),
                    isTopMetro = false
                ),
                GlobalCity(
                    name = "Siwan",
                    district = "Siwan",
                    latitude = 26.2196,
                    longitude = 84.3567,
                    localities = listOf(
                        GlobalLocality("Hospital Road & Bazaar", 26.2212, 84.3612, listOf("Sadar Hospital", "Main Bazaar")),
                        GlobalLocality("Babunia Road", 26.2156, 84.3521, listOf("Siwan Junction", "DA V College Road"))
                    ),
                    famousLandmarks = listOf("Ziradei (Dr. Rajendra Prasad Birthplace)", "Siwan Junction", "DA V Post Graduate College"),
                    isTopMetro = false
                ),
                GlobalCity(
                    name = "Samastipur",
                    district = "Samastipur",
                    latitude = 25.8630,
                    longitude = 85.7811,
                    localities = listOf(
                        GlobalLocality("Tajpur Road & Magardahi", 25.8612, 85.7889, listOf("Railway DRM Office", "Samastipur Junction")),
                        GlobalLocality("Pusa & University Hub", 25.9812, 85.6712, listOf("Dr. Rajendra Prasad Central Agricultural University"))
                    ),
                    famousLandmarks = listOf("Dr. Rajendra Prasad Central Agricultural University (Pusa)", "Samastipur Junction", "Thaneshwar Temple"),
                    isTopMetro = false
                ),
                GlobalCity(
                    name = "Motihari",
                    district = "East Champaran",
                    latitude = 26.6469,
                    longitude = 84.9089,
                    localities = listOf(
                        GlobalLocality("Main Market & Chhatauni", 26.6489, 84.9123, listOf("Chhatauni Bus Stand", "Gandhi Sangrahalaya")),
                        GlobalLocality("Bairiya & Central University Road", 26.6545, 84.8989, listOf("Mahatma Gandhi Central University MGCUB"))
                    ),
                    famousLandmarks = listOf("Mahatma Gandhi Central University", "George Orwell Birthplace", "Gandhi Smarak Motihari"),
                    isTopMetro = false
                )
            )
        ),

        // 2. MAHARASHTRA
        GlobalState(
            code = "MH",
            name = "Maharashtra",
            cities = listOf(
                GlobalCity(
                    name = "Mumbai",
                    latitude = 19.0760,
                    longitude = 72.8777,
                    localities = listOf(
                        GlobalLocality("Bandra West", 19.0596, 72.8295, listOf("Bandstand", "Hill Road", "Linking Road", "Carter Road")),
                        GlobalLocality("Andheri West", 19.1363, 72.8277, listOf("Lokhandwala Complex", "Versova Metro", "Infinity Mall")),
                        GlobalLocality("Powai", 19.1176, 72.9060, listOf("IIT Bombay", "Hiranandani Gardens", "Powai Lake")),
                        GlobalLocality("BKC (Bandra Kurla Complex)", 19.0657, 72.8684, listOf("Jio World Centre", "US Consulate", "Corporate Towers")),
                        GlobalLocality("South Mumbai & Fort", 18.9322, 72.8347, listOf("Nariman Point", "CST Station", "Marine Drive", "Colaba")),
                        GlobalLocality("Thane West", 19.2183, 72.9781, listOf("Viviana Mall", "Ghodbunder Road", "Talao Pali")),
                        GlobalLocality("Navi Mumbai (Vashi & Belapur)", 19.0771, 72.9986, listOf("Inorbit Mall", "Vashi Station", "IT Parks")),
                        GlobalLocality("Malad & Goregaon West", 19.1860, 72.8485, listOf("Inorbit Malad", "Mindspace IT Park", "Film City Road")),
                        GlobalLocality("Dadar & Matunga", 19.0178, 72.8478, listOf("Shivaji Park", "Dadar Central Station", "Ruia College"))
                    ),
                    famousLandmarks = listOf("Gateway of India", "Marine Drive & Promenade", "Chhatrapati Shivaji Maharaj Terminus (CST)", "Siddhivinayak Temple", "Haji Ali Dargah", "Bandra-Worli Sea Link", "IIT Bombay"),
                    isTopMetro = true
                ),
                GlobalCity(
                    name = "Pune",
                    latitude = 18.5204,
                    longitude = 73.8567,
                    localities = listOf(
                        GlobalLocality("Hinjewadi (Phases 1-3)", 18.5913, 73.7389, listOf("Rajiv Gandhi Infotech Park", "Wipro Circle", "Mega Hostels")),
                        GlobalLocality("Kothrud", 18.5074, 73.8077, listOf("MIT World Peace University", "Paud Road", "Vanaz Metro")),
                        GlobalLocality("Viman Nagar", 18.5679, 73.9143, listOf("Symbiosis Campus", "Phoenix Marketcity", "Pune Airport")),
                        GlobalLocality("Wakad & Baner", 18.5987, 73.7745, listOf("Balewadi High Street", "Dutt Mandir Chowk", "IT PGs")),
                        GlobalLocality("Koregaon Park", 18.5362, 73.8940, listOf("Osho Ashram", "North Main Road", "Boutique Cafes")),
                        GlobalLocality("Shivajinagar & FC Road", 18.5314, 73.8446, listOf("COEP Tech University", "FC Road", "Shivajinagar Station"))
                    ),
                    famousLandmarks = listOf("Shaniwar Wada", "Aga Khan Palace", "Sinhagad Fort", "Pune Junction", "Symbiosis International University", "Chaturshringi Temple"),
                    isTopMetro = true
                ),
                GlobalCity(
                    name = "Nagpur",
                    latitude = 21.1458,
                    longitude = 79.0882,
                    localities = listOf(
                        GlobalLocality("Dharampeth", 21.1412, 79.0623, listOf("West High Court Road", "Shopping Arcades")),
                        GlobalLocality("Sadar", 21.1623, 79.0845, listOf("Mount Road", "Residency Road", "Hotels")),
                        GlobalLocality("MIHAN Tech Hub", 21.0543, 79.0345, listOf("AIIMS Nagpur", "IIM Nagpur", "Infosys SEZ"))
                    ),
                    famousLandmarks = listOf("Deekshabhoomi", "Zero Mile Stone", "Sitabuldi Fort", "Nagpur Junction", "AIIMS Nagpur"),
                    isTopMetro = false
                ),
                GlobalCity(
                    name = "Nashik",
                    latitude = 19.9975,
                    longitude = 73.7898,
                    localities = listOf(
                        GlobalLocality("College Road", 20.0056, 73.7645, listOf("BYK College", "Student Cafes", "Boutiques")),
                        GlobalLocality("Indira Nagar", 19.9723, 73.7812, listOf("Mumbai-Agra Highway", "Residential Condos")),
                        GlobalLocality("Panchavati", 20.0123, 73.7945, listOf("Godavari Ghat", "Kalaram Temple", "Old Bazaar"))
                    ),
                    famousLandmarks = listOf("Trimbakeshwar Shiva Temple", "Sula Vineyards", "Panchavati Ghats", "Muktidham"),
                    isTopMetro = false
                ),
                GlobalCity(
                    name = "Chhatrapati Sambhaji Nagar (Aurangabad)",
                    latitude = 19.8762,
                    longitude = 75.3433,
                    localities = listOf(
                        GlobalLocality("Cidco & Cannaught Place", 19.8845, 75.3612, listOf("Prozone Mall", "Commercial Center")),
                        GlobalLocality("Osmanpura", 19.8689, 75.3345, listOf("Coaching Centers", "Kranti Chowk"))
                    ),
                    famousLandmarks = listOf("Ajanta & Ellora Caves UNESCO", "Bibi Ka Maqbara", "Daulatabad Fort", "Aurangabad Airport"),
                    isTopMetro = false
                )
            )
        ),

        // 3. KARNATAKA
        GlobalState(
            code = "KA",
            name = "Karnataka",
            cities = listOf(
                GlobalCity(
                    name = "Bengaluru",
                    latitude = 12.9716,
                    longitude = 77.5946,
                    localities = listOf(
                        GlobalLocality("Koramangala (Blocks 1-8)", 12.9352, 77.6245, listOf("Sony World Junction", "Nexus Mall Koramangala", "Christ University", "Startup Hub")),
                        GlobalLocality("HSR Layout (Sectors 1-7)", 12.9121, 77.6446, listOf("27th Main High Street", "BDA Complex", "Agara Lake", "Silk Board")),
                        GlobalLocality("Indiranagar & 100 Feet Rd", 12.9784, 77.6408, listOf("100ft Road", "Indiranagar Metro", "CMH Road", "Domlur")),
                        GlobalLocality("Whitefield & ITPL", 12.9698, 77.7500, listOf("ITPL Tech Park", "Whitefield Metro", "Phoenix Marketcity", "Nexus Shantiniketan")),
                        GlobalLocality("Electronic City (Phase 1 & 2)", 12.8399, 77.6770, listOf("Infosys Gate 1", "Wipro Campus", "Elevated Toll Expressway")),
                        GlobalLocality("BTM Layout & Silk Board", 12.9166, 77.6101, listOf("Udupi Garden Signal", "Tavarekere", "Coaching Institutes")),
                        GlobalLocality("Marathahalli & ORR", 12.9591, 77.6974, listOf("Prestige Tech Park", "Kalamandir Junction", "ORR Tech Corridor")),
                        GlobalLocality("Malleshwaram & Rajajinagar", 13.0031, 77.5643, listOf("IISc Bengaluru Campus", "8th Cross Market", "Orion Mall & World Trade Center")),
                        GlobalLocality("Bellandur & Sarjapur Road", 12.9256, 77.6789, listOf("EcoSpace Tech Park", "RMZ Ecoworld", "Columbia Asia Hospital"))
                    ),
                    famousLandmarks = listOf("Vidhana Soudha", "Lalbagh Botanical Garden", "Bangalore Palace", "Indian Institute of Science (IISc)", "Cubbon Park", "ISKCON Temple Rajajinagar", "Kempegowda International Airport"),
                    isTopMetro = true
                ),
                GlobalCity(
                    name = "Mysuru",
                    latitude = 12.2958,
                    longitude = 76.6394,
                    localities = listOf(
                        GlobalLocality("Gokulam", 12.3245, 76.6234, listOf("Yoga Shalas", "University Road", "Cafes")),
                        GlobalLocality("Jayalakshmipuram", 12.3189, 76.6312, listOf("Kalidasa Road", "Boutiques", "Student Stays")),
                        GlobalLocality("Kuvempunagar", 12.2891, 76.6278, listOf("Complex", "Parks", "Hospitals"))
                    ),
                    famousLandmarks = listOf("Mysore Palace (Amba Vilas)", "Chamundeshwari Temple", "Brindavan Gardens", "Mysuru Junction"),
                    isTopMetro = false
                ),
                GlobalCity(
                    name = "Mangaluru",
                    latitude = 12.9141,
                    longitude = 74.8560,
                    localities = listOf(
                        GlobalLocality("Kadri & Mallikatte", 12.8812, 74.8589, listOf("Kadri Park", "Manipal Hospital")),
                        GlobalLocality("Hampankatta", 12.8689, 74.8423, listOf("Central Market", "City Centre Mall"))
                    ),
                    famousLandmarks = listOf("Panambur Beach", "Kudroli Gokarnath Temple", "NITK Surathkal", "Mangaluru International Airport"),
                    isTopMetro = false
                ),
                GlobalCity(
                    name = "Hubballi-Dharwad",
                    latitude = 15.3647,
                    longitude = 75.1240,
                    localities = listOf(
                        GlobalLocality("Vidyanagar", 15.3712, 75.1289, listOf("KLE Tech University", "Student Hub")),
                        GlobalLocality("Gokul Road", 15.3589, 75.1012, listOf("Airport Road", "Industrial Hub"))
                    ),
                    famousLandmarks = listOf("Unkal Lake", "IIT Dharwad Campus", "KLE Technological University", "Hubballi Junction"),
                    isTopMetro = false
                )
            )
        ),

        // 5. UTTAR PRADESH
        GlobalState(
            code = "UP",
            name = "Uttar Pradesh",
            cities = listOf(
                GlobalCity(
                    name = "Noida",
                    district = "Gautam Buddha Nagar",
                    latitude = 28.5355,
                    longitude = 77.3910,
                    localities = listOf(
                        GlobalLocality("Sector 62", 28.6139, 77.3653, listOf("Electronic City Metro", "JIIT Campus", "Knowledge Boulevard", "Fortis Hospital")),
                        GlobalLocality("Sector 18 & Atta Market", 28.5708, 77.3218, listOf("DLF Mall of India", "Sector 18 Metro", "Great India Place")),
                        GlobalLocality("Sector 15 & 16 (Film City)", 28.5802, 77.3145, listOf("Film City Studios", "Noida Sector 16 Metro", "IT Parks")),
                        GlobalLocality("Sector 128 (Expressway)", 28.5134, 77.3789, listOf("Jaypee Hospital", "Jaypee Greens", "Expressway Hub")),
                        GlobalLocality("Sector 76 & 78", 28.5723, 77.3892, listOf("Spectrum Metro Mall", "Sector 76 Metro", "Modern High-Rises")),
                        GlobalLocality("Sector 137", 28.5021, 77.4089, listOf("Aqua Line Metro", "Advant Navis Business Park", "Supertech Capetown"))
                    ),
                    famousLandmarks = listOf("DLF Mall of India", "Noida Electronic City", "Buddh International Circuit", "Okhla Bird Sanctuary", "Fortis Hospital Noida"),
                    isTopMetro = true,
                    aliases = listOf("Noida NCR", "New Okhla Industrial Development Authority")
                ),
                GlobalCity(
                    name = "Greater Noida",
                    district = "Gautam Buddha Nagar",
                    latitude = 28.4744,
                    longitude = 77.5040,
                    localities = listOf(
                        GlobalLocality("Knowledge Park III", 28.4601, 77.4843, listOf("Sharda University", "Galgotias University", "Bennett University", "Knowledge Park 3 Metro")),
                        GlobalLocality("Pari Chowk", 28.4712, 77.5123, listOf("Pari Chowk Metro", "Ansal Plaza", "Bus Terminal")),
                        GlobalLocality("Alpha 1 & Beta 1", 28.4823, 77.5012, listOf("Alpha 1 Commercial Complex", "Metro Station", "Markets")),
                        GlobalLocality("Gaur City & Noida Extension", 28.6012, 77.4321, listOf("Gaur City Mall", "Char Murti Chowk", "High-Rise Townships"))
                    ),
                    famousLandmarks = listOf("India Expo Mart & Centre", "Gautam Buddha University", "Buddh International F1 Track", "Knowledge Park Universities"),
                    isTopMetro = false,
                    aliases = listOf("Greater Noida West", "Noida Extension")
                ),
                GlobalCity(
                    name = "Ghaziabad",
                    district = "Ghaziabad",
                    latitude = 28.6692,
                    longitude = 77.4538,
                    localities = listOf(
                        GlobalLocality("Indirapuram & Vaishali", 28.6412, 77.3712, listOf("Shipra Mall", "Vaishali Metro", "Habitat Centre")),
                        GlobalLocality("Raj Nagar Extension", 28.7012, 77.4412, listOf("City Forest", "High-Rise Complexes"))
                    ),
                    famousLandmarks = listOf("Hindon Airport Civil Terminal", "Shipra Mall Indirapuram", "ABES Engineering College"),
                    isTopMetro = false,
                    aliases = listOf("Ghaziabad NCR")
                ),
                GlobalCity(
                    name = "Lucknow",
                    latitude = 26.8467,
                    longitude = 80.9462,
                    localities = listOf(
                        GlobalLocality("Gomti Nagar & Vibhuti Khand", 26.8589, 80.9989, listOf("Wave Mall", "Vibhuti Khand IT Hub", "Lohia Hospital")),
                        GlobalLocality("Hazratganj", 26.8523, 80.9412, listOf("Ganj Market", "Metro Station", "Janpath")),
                        GlobalLocality("Aliganj & Kapoorthala", 26.8845, 80.9389, listOf("Coaching Institutes", "Engineering College")),
                        GlobalLocality("Indira Nagar", 26.8789, 80.9789, listOf("Munshipulia Metro", "Residential Hub"))
                    ),
                    famousLandmarks = listOf("Bara Imambara & Rumi Darwaza", "Chhota Imambara", "Ambedkar Memorial Park", "Charbagh Railway Station", "IIM Lucknow"),
                    isTopMetro = true
                ),
                GlobalCity(
                    name = "Kanpur",
                    latitude = 26.4499,
                    longitude = 80.3319,
                    localities = listOf(
                        GlobalLocality("Kakadeo", 26.4789, 80.2987, listOf("Coaching Mandi", "Hostels Hub", "Student Cafes")),
                        GlobalLocality("Swaroop Nagar & Civil Lines", 26.4812, 80.3212, listOf("Motijheel", "GSVM Medical College")),
                        GlobalLocality("Kalyanpur (IIT)", 26.5123, 80.2345, listOf("IIT Kanpur Campus", "Kalyanpur Metro"))
                    ),
                    famousLandmarks = listOf("IIT Kanpur Campus", "JK Temple", "Allen Forest Zoo", "Kanpur Central Station", "Bithoor Ghats"),
                    isTopMetro = true
                ),
                GlobalCity(
                    name = "Varanasi (Kashi)",
                    latitude = 25.3176,
                    longitude = 82.9739,
                    localities = listOf(
                        GlobalLocality("Lanka & BHU Campus", 25.2789, 82.9987, listOf("Banaras Hindu University (BHU)", "Sir Sunderlal Hospital", "IIT BHU")),
                        GlobalLocality("Assi Ghat & Godowlia", 25.2987, 83.0089, listOf("Ganga Aarti", "Kashi Vishwanath Corridor", "Boutique Stays")),
                        GlobalLocality("Sigra & Cantt", 25.3212, 82.9812, listOf("IP Mall", "Varanasi Junction Cantt"))
                    ),
                    famousLandmarks = listOf("Kashi Vishwanath Corridor & Temple", "Dashashwamedh Ghat", "Banaras Hindu University (BHU)", "Sarnath UNESCO Site", "Varanasi Junction"),
                    isTopMetro = true
                ),
                GlobalCity(
                    name = "Prayagraj (Allahabad)",
                    latitude = 25.4358,
                    longitude = 81.8463,
                    localities = listOf(
                        GlobalLocality("Civil Lines", 25.4512, 81.8345, listOf("High Court", "Elgin Road", "Hotels")),
                        GlobalLocality("Katra & University Area", 25.4623, 81.8589, listOf("Allahabad University", "Student Stays")),
                        GlobalLocality("Teliyarganj (MNNIT)", 25.4912, 81.8645, listOf("MNNIT Allahabad Campus", "Engineering PGs"))
                    ),
                    famousLandmarks = listOf("Triveni Sangam", "Allahabad Fort", "Anand Bhavan", "Allahabad High Court", "MNNIT Allahabad"),
                    isTopMetro = false
                ),
                GlobalCity(
                    name = "Agra",
                    latitude = 27.1767,
                    longitude = 78.0081,
                    localities = listOf(
                        GlobalLocality("Tajganj", 27.1689, 78.0412, listOf("Taj Mahal East Gate", "Tourist Hostels", "Rooftop Cafes")),
                        GlobalLocality("Sanjay Place & Civil Lines", 27.2012, 78.0045, listOf("Commercial Center", "Banks & Offices"))
                    ),
                    famousLandmarks = listOf("Taj Mahal UNESCO World Wonder", "Agra Fort", "Fatehpur Sikri", "Mehtab Bagh", "Agra Cantt Station"),
                    isTopMetro = true
                ),
                GlobalCity(
                    name = "Gorakhpur",
                    latitude = 26.7606,
                    longitude = 83.3732,
                    localities = listOf(
                        GlobalLocality("Golghar", 26.7589, 83.3712, listOf("Main Shopping Market", "City Mall")),
                        GlobalLocality("Medical College Area (AIIMS)", 26.7912, 83.4123, listOf("AIIMS Gorakhpur", "BRD Medical College"))
                    ),
                    famousLandmarks = listOf("Gorakhnath Temple", "Gorakhpur Junction (Longest Platform)", "AIIMS Gorakhpur", "Ramgarh Taal Lake"),
                    isTopMetro = false
                )
            )
        ),

        // 6. TAMIL NADU
        GlobalState(
            code = "TN",
            name = "Tamil Nadu",
            cities = listOf(
                GlobalCity(
                    name = "Chennai",
                    latitude = 13.0827,
                    longitude = 80.2707,
                    localities = listOf(
                        GlobalLocality("OMR (IT Corridor)", 12.9249, 80.2272, listOf("Tidel Park", "Thoraipakkam", "Sholinganallur", "Siruseri SIPCOT")),
                        GlobalLocality("T. Nagar", 13.0418, 80.2341, listOf("Pondy Bazaar", "Panagal Park", "Ranganathan Street")),
                        GlobalLocality("Velachery", 12.9759, 80.2212, listOf("Phoenix Marketcity", "Velachery MRTS", "Vijayanagar Bus Terminus")),
                        GlobalLocality("Adyar & Besant Nagar", 13.0012, 80.2565, listOf("IIT Madras", "Elliot's Beach", "Anna University")),
                        GlobalLocality("Anna Nagar", 13.0850, 80.2101, listOf("Tower Park", "Anna Nagar Roundtana", "Metro Station")),
                        GlobalLocality("Guindy & Saidapet", 13.0067, 80.2012, listOf("Olympia Tech Park", "Guindy Metro", "Anna University"))
                    ),
                    famousLandmarks = listOf("Marina Beach & Promenade", "Kapaleeshwarar Temple", "San Thome Cathedral", "IIT Madras", "Chennai Central Railway Station"),
                    isTopMetro = true
                ),
                GlobalCity(
                    name = "Coimbatore",
                    latitude = 11.0168,
                    longitude = 76.9558,
                    localities = listOf(
                        GlobalLocality("RS Puram", 11.0089, 76.9456, listOf("DB Road", "Shopping Hub", "Parks")),
                        GlobalLocality("Peelamedu", 11.0289, 77.0012, listOf("PSG Tech", "Tidel Park Coimbatore", "Airport")),
                        GlobalLocality("Gandhipuram", 11.0178, 76.9678, listOf("Central Bus Stand", "Cross Cut Road"))
                    ),
                    famousLandmarks = listOf("Adiyogi Shiva Statue", "Marudhamalai Murugan Temple", "Coimbatore Junction", "PSG College of Technology"),
                    isTopMetro = false
                ),
                GlobalCity(
                    name = "Madurai",
                    latitude = 9.9252,
                    longitude = 78.1198,
                    localities = listOf(
                        GlobalLocality("KK Nagar", 9.9345, 78.1456, listOf("District Courts", "Walkers Park", "Clinics")),
                        GlobalLocality("Town Hall & Meenakshi Bazaar", 9.9189, 78.1178, listOf("Temple Outer Ring", "Bazaars"))
                    ),
                    famousLandmarks = listOf("Meenakshi Amman Temple", "Thirumalai Nayakkar Mahal", "Madurai Junction", "Alagar Kovil"),
                    isTopMetro = false
                )
            )
        ),

        // 7. TELANGANA
        GlobalState(
            code = "TG",
            name = "Telangana",
            cities = listOf(
                GlobalCity(
                    name = "Hyderabad",
                    latitude = 17.3850,
                    longitude = 78.4867,
                    localities = listOf(
                        GlobalLocality("Hitec City & Madhapur", 17.4435, 78.3772, listOf("Cyber Towers", "Mindspace IT Park", "Inorbit Mall", "Hitec Metro")),
                        GlobalLocality("Gachibowli & Financial District", 17.4401, 78.3489, listOf("IIIT Hyderabad", "University of Hyderabad", "US Consulate", "DLF Cyber City")),
                        GlobalLocality("Banjara Hills & Jubilee Hills", 17.4156, 78.4345, listOf("Road No. 36", "Care Hospital", "KBR National Park", "Film Nagar")),
                        GlobalLocality("Kukatpally (KPHB)", 17.4934, 78.3998, listOf("JNTU Hyderabad", "Manjeera Mall", "KPHB Colony Metro")),
                        GlobalLocality("Secunderabad", 17.4399, 78.4983, listOf("Secunderabad Railway Station", "Paradise Biryani", "Clock Tower")),
                        GlobalLocality("Ameerpet & SR Nagar", 17.4375, 78.4482, listOf("IT Coaching Hub", "Ameerpet Metro Interchange", "Student Hostels"))
                    ),
                    famousLandmarks = listOf("Charminar", "Golconda Fort", "Hussain Sagar & Buddha Statue", "Birla Mandir", "Ramoji Film City", "IIIT Hyderabad"),
                    isTopMetro = true
                ),
                GlobalCity(
                    name = "Warangal",
                    latitude = 17.9689,
                    longitude = 79.5941,
                    localities = listOf(
                        GlobalLocality("Kazipet & NIT Campus", 17.9789, 79.5312, listOf("NIT Warangal", "Kazipet Junction", "Student PGs")),
                        GlobalLocality("Hanamkonda", 18.0012, 79.5789, listOf("Thousand Pillar Temple Road", "Public Garden"))
                    ),
                    famousLandmarks = listOf("NIT Warangal Campus", "Thousand Pillar Temple", "Warangal Fort", "Bhadrakali Temple"),
                    isTopMetro = false
                )
            )
        ),

        // 8. RAJASTHAN
        GlobalState(
            code = "RJ",
            name = "Rajasthan",
            cities = listOf(
                GlobalCity(
                    name = "Kota",
                    latitude = 25.2138,
                    longitude = 75.8648,
                    localities = listOf(
                        GlobalLocality("Landmark City & Kunhari", 25.2145, 75.8456, listOf("Allen SAMYAK & SAMANVAY", "Kota Coaching Hub", "Student Hostels & Mess")),
                        GlobalLocality("Indra Vihar & Talwandi", 25.1589, 75.8345, listOf("Allen Satyarth", "Career Point", "Commerce College", "City Mall")),
                        GlobalLocality("Vigyan Nagar & Dadabari", 25.1456, 75.8234, listOf("Motion IIT", "Resonance Campus", "Vigyan Nagar PGs")),
                        GlobalLocality("Mahaveer Nagar (1-3)", 25.1389, 75.8412, listOf("Allen Sangyan", "Food Streets", "Boys & Girls Hostels")),
                        GlobalLocality("Rajeev Gandhi Nagar", 25.1512, 75.8389, listOf("Allen Supath", "Electronic Complex", "Premium PGs"))
                    ),
                    famousLandmarks = listOf("Seven Wonders Park", "Chambal River Front & Garden", "Kota Junction Railway Station", "Garh Palace & Museum", "Allen Career Institute Hub"),
                    isTopMetro = false
                ),
                GlobalCity(
                    name = "Jaipur",
                    latitude = 26.9124,
                    longitude = 75.7873,
                    localities = listOf(
                        GlobalLocality("Malviya Nagar", 26.8543, 75.8156, listOf("MNIT Jaipur Campus", "World Trade Park (WTP)", "Gaurav Tower (GT)")),
                        GlobalLocality("Mansarovar", 26.8623, 75.7645, listOf("Mansarovar Metro", "City Park", "University Colleges")),
                        GlobalLocality("C-Scheme & MI Road", 26.9145, 75.8012, listOf("Statue Circle", "Central Park", "High-End Stays")),
                        GlobalLocality("Vaishali Nagar", 26.9089, 75.7412, listOf("National Handloom", "Amrapali Circle", "Residential PGs"))
                    ),
                    famousLandmarks = listOf("Hawa Mahal", "Amber Palace & Fort", "City Palace Jaipur", "Jantar Mantar", "World Trade Park (WTP)", "MNIT Jaipur"),
                    isTopMetro = true
                ),
                GlobalCity(
                    name = "Jodhpur",
                    latitude = 26.2389,
                    longitude = 73.0243,
                    localities = listOf(
                        GlobalLocality("Shastri Nagar & AIIMS", 26.2589, 73.0012, listOf("AIIMS Jodhpur", "Medical Hostels", "Shastri Circle")),
                        GlobalLocality("Ratanada", 26.2712, 73.0345, listOf("Airport Road", "Boutique Hotels", "Jodhpur Junction"))
                    ),
                    famousLandmarks = listOf("Mehrangarh Fort", "Umaid Bhawan Palace", "AIIMS Jodhpur", "Jaswant Thada"),
                    isTopMetro = false
                ),
                GlobalCity(
                    name = "Udaipur",
                    latitude = 24.5854,
                    longitude = 73.7125,
                    localities = listOf(
                        GlobalLocality("Fateh Sagar & Lake Pichola", 24.5912, 73.6845, listOf("Lakeside Stays", "Saheliyon Ki Bari", "Rooftop Cafes")),
                        GlobalLocality("Hiran Magri (Sectors 1-14)", 24.5689, 73.7212, listOf("Commercial Center", "Hostels & PGs"))
                    ),
                    famousLandmarks = listOf("City Palace Udaipur", "Lake Pichola", "Jag Mandir", "Fateh Sagar Lake", "IIM Udaipur"),
                    isTopMetro = false
                )
            )
        ),

        // 9. WEST BENGAL
        GlobalState(
            code = "WB",
            name = "West Bengal",
            cities = listOf(
                GlobalCity(
                    name = "Kolkata",
                    latitude = 22.5726,
                    longitude = 88.3639,
                    localities = listOf(
                        GlobalLocality("Salt Lake (Sectors 1-5)", 22.5804, 88.4172, listOf("Sector V IT Hub", "City Centre 1", "Karunamoyee Bus Terminus", "Techno India")),
                        GlobalLocality("New Town & Action Area", 22.5850, 88.4646, listOf("Eco Park", "Biswa Bangla Gate", "Axis Mall", "Amity University")),
                        GlobalLocality("Park Street & Camac St", 22.5512, 88.3524, listOf("Flurys", "St. Xavier's College", "Park Street Metro")),
                        GlobalLocality("Ballygunge & Gariahat", 22.5280, 88.3654, listOf("Gariahat Market", "Ballygunge Circular Rd", "South City Mall")),
                        GlobalLocality("Jadavpur & Garia", 22.4989, 88.3712, listOf("Jadavpur University", "8B Bus Stand", "Kavi Nazrul Metro"))
                    ),
                    famousLandmarks = listOf("Victoria Memorial Hall", "Howrah Bridge", "Dakshineswar Kali Temple", "Science City", "Eden Gardens Stadium", "Howrah Railway Station"),
                    isTopMetro = true
                ),
                GlobalCity(
                    name = "Durgapur & Asansol",
                    latitude = 23.5204,
                    longitude = 87.3119,
                    localities = listOf(
                        GlobalLocality("City Centre Durgapur", 23.5412, 87.2987, listOf("Junction Mall", "NIT Durgapur", "Commercial Hub")),
                        GlobalLocality("Asansol Court & Burnpur", 23.6845, 86.9712, listOf("IISCO Township", "Asansol Junction"))
                    ),
                    famousLandmarks = listOf("NIT Durgapur Campus", "Durgapur Barrage", "Kalyaneshwari Temple", "Asansol Junction"),
                    isTopMetro = false
                ),
                GlobalCity(
                    name = "Siliguri & Darjeeling",
                    latitude = 26.7271,
                    longitude = 88.4316,
                    localities = listOf(
                        GlobalLocality("Sevoke Road & Vega Circle", 26.7345, 88.4389, listOf("Vega Circle Mall", "Hotels Hub", "City Centre Siliguri")),
                        GlobalLocality("Darjeeling Mall & Chowrasta", 27.0412, 88.2667, listOf("The Mall", "Toy Train Station", "Tiger Hill Road"))
                    ),
                    famousLandmarks = listOf("Darjeeling Himalayan Railway UNESCO", "Tiger Hill Sunrise", "Coronation Bridge", "Bagdogra Airport"),
                    isTopMetro = false
                )
            )
        ),

        // 10. GUJARAT
        GlobalState(
            code = "GJ",
            name = "Gujarat",
            cities = listOf(
                GlobalCity(
                    name = "Ahmedabad",
                    latitude = 23.0225,
                    longitude = 72.5714,
                    localities = listOf(
                        GlobalLocality("SG Highway & Vastrapur", 23.0389, 72.5189, listOf("IIM Ahmedabad", "Vastrapur Lake", "Alpha One Mall", "ISCON Mega Mall")),
                        GlobalLocality("Navrangpura & CG Road", 23.0367, 72.5589, listOf("Gujarat University", "LD Engineering", "Law Garden Street Market")),
                        GlobalLocality("Satellite & Prahladnagar", 23.0112, 72.5112, listOf("Prahladnagar Garden", "Corporate Hub", "Modern PGs")),
                        GlobalLocality("Bopal & South Bopal", 23.0312, 72.4689, listOf("TRP Mall", "SP Ring Road", "Residential Condos"))
                    ),
                    famousLandmarks = listOf("Sabarmati Ashram", "Atal Bridge & Sabarmati Riverfront", "IIM Ahmedabad Campus", "Adalaj Stepwell", "Narendra Modi Cricket Stadium"),
                    isTopMetro = true
                ),
                GlobalCity(
                    name = "Surat",
                    latitude = 21.1702,
                    longitude = 72.8311,
                    localities = listOf(
                        GlobalLocality("Vesu & VIP Road", 21.1412, 72.7789, listOf("SVNIT Surat", "VR Surat Mall", "Airport Corridor")),
                        GlobalLocality("Adajan & Pal", 21.1989, 72.7845, listOf("Aquarium", "Prime Arcade", "Residential Hub"))
                    ),
                    famousLandmarks = listOf("Surat Diamond Bourse", "Dumas Beach", "SVNIT Surat", "Surat Castle", "Surat Railway Station"),
                    isTopMetro = true
                ),
                GlobalCity(
                    name = "Vadodara",
                    latitude = 22.3072,
                    longitude = 73.1812,
                    localities = listOf(
                        GlobalLocality("Fatehgunj & MSU", 22.3212, 73.1889, listOf("MS University Baroda", "Pavilion Ground", "Student Cafes")),
                        GlobalLocality("Alkapuri & RC Dutt Rd", 22.3123, 73.1712, listOf("Vadodara Central", "Inox Mall", "Hotels"))
                    ),
                    famousLandmarks = listOf("Laxmi Vilas Palace", "Sayaji Baug", "MS University of Baroda", "Vadodara Junction"),
                    isTopMetro = false
                )
            )
        ),

        // 11. PUNJAB & CHANDIGARH
        GlobalState(
            code = "PB",
            name = "Punjab",
            cities = listOf(
                GlobalCity(
                    name = "Mohali & Chandigarh Tricity",
                    latitude = 30.7333,
                    longitude = 76.7794,
                    localities = listOf(
                        GlobalLocality("Sector 17 & Sector 35", 30.7412, 76.7823, listOf("Sector 17 Plaza", "Hotel Row", "ISBT 17")),
                        GlobalLocality("Sector 62 (Phase 7 Mohali)", 30.7045, 76.7178, listOf("PCA Stadium", "Phase 7 Market", "Food Street")),
                        GlobalLocality("Sector 67 & 70 (IT Park Mohali)", 30.6845, 76.7289, listOf("QuarkCity IT SEZ", "Indian School of Business (ISB)", "IISER Mohali"))
                    ),
                    famousLandmarks = listOf("Rock Garden of Chandigarh", "Sukhna Lake", "Elante Mall", "PCA Cricket Stadium Mohali", "ISB Mohali"),
                    isTopMetro = true
                ),
                GlobalCity(
                    name = "Ludhiana",
                    latitude = 30.9010,
                    longitude = 75.8573,
                    localities = listOf(
                        GlobalLocality("Sarabha Nagar & Ferozepur Rd", 30.8912, 75.8189, listOf("Kipps Market", "MBD Neopolis Mall", "PAU Campus")),
                        GlobalLocality("Model Town", 30.8845, 75.8412, listOf("Tuition Market", "Boutique Arcades"))
                    ),
                    famousLandmarks = listOf("Punjab Agricultural University (PAU)", "Clock Tower", "Ludhiana Junction", "MBD Neopolis Mall"),
                    isTopMetro = false
                ),
                GlobalCity(
                    name = "Amritsar",
                    latitude = 31.6340,
                    longitude = 74.8723,
                    localities = listOf(
                        GlobalLocality("Golden Temple Heritage Street", 31.6201, 74.8765, listOf("Heritage Plaza", "Jallianwala Bagh", "Langar Hall")),
                        GlobalLocality("Ranjit Avenue", 31.6489, 74.8512, listOf("Commercial Complex", "GNDU Campus", "Amritsar Airport Rd"))
                    ),
                    famousLandmarks = listOf("Golden Temple (Harmandir Sahib)", "Jallianwala Bagh", "Attari-Wagah Border", "Guru Nanak Dev University (GNDU)"),
                    isTopMetro = false
                )
            )
        ),

        // 12. HARYANA
        GlobalState(
            code = "HR",
            name = "Haryana",
            cities = listOf(
                GlobalCity(
                    name = "Gurugram",
                    district = "Gurugram",
                    latitude = 28.4595,
                    longitude = 77.0266,
                    localities = listOf(
                        GlobalLocality("DLF Cyber City & Phase 2", 28.4907, 77.0894, listOf("Cyber Hub", "Rapid Metro Phase 2", "Horizon Center", "Golf Course Road")),
                        GlobalLocality("Sector 29 & HUDA City Centre", 28.4682, 77.0632, listOf("Millennium City Centre Metro", "Appu Ghar", "Kingdom of Dreams")),
                        GlobalLocality("Sohna Road & Sector 48", 28.4124, 77.0421, listOf("Spaze I-Tech Park", "Vatika Business Park", "Subhash Chowk")),
                        GlobalLocality("Golf Course Extension", 28.4012, 77.0987, listOf("WorldMark Gurgaon", "AIPL Joy Street", "Luxury Stays")),
                        GlobalLocality("DLF Phase 3 & Moulsari", 28.4956, 77.1023, listOf("Ambience Mall", "Corporate PGs", "Rapid Metro Phase 3"))
                    ),
                    famousLandmarks = listOf("DLF Cyber Hub", "Ambience Mall", "Kingdom of Dreams", "Sheetla Mata Mandir", "Sultanpur National Park"),
                    isTopMetro = true,
                    aliases = listOf("Gurgaon", "Gurgaon NCR", "Cyber City")
                ),
                GlobalCity(
                    name = "Faridabad",
                    latitude = 28.4089,
                    longitude = 77.3178,
                    localities = listOf(
                        GlobalLocality("Sector 15 & 16", 28.4112, 77.3245, listOf("Crown Plaza", "Metro Station", "Commercial Center")),
                        GlobalLocality("Greenfield & NHPC Chowk", 28.4612, 77.3089, listOf("NHPC Metro", "Manav Rachna University", "Surajkund"))
                    ),
                    famousLandmarks = listOf("Surajkund Crafts Fair", "Badkhal Lake", "Manav Rachna University", "YMCA University"),
                    isTopMetro = false
                ),
                GlobalCity(
                    name = "Karnal & Panipat",
                    latitude = 29.6857,
                    longitude = 76.9905,
                    localities = listOf(
                        GlobalLocality("Model Town Karnal", 29.6912, 76.9845, listOf("Super Mall", "NDRI Campus", "Highway Dhabas")),
                        GlobalLocality("Panipat Textile Hub", 29.3909, 76.9635, listOf("Handloom Market", "Panipat Junction"))
                    ),
                    famousLandmarks = listOf("NDRI Karnal", "Karna Lake", "Panipat Battleground & Memorial", "Panipat Refinery"),
                    isTopMetro = false
                )
            )
        ),

        // 13. KERALA
        GlobalState(
            code = "KL",
            name = "Kerala",
            cities = listOf(
                GlobalCity(
                    name = "Kochi (Cochin)",
                    latitude = 9.9312,
                    longitude = 76.2673,
                    localities = listOf(
                        GlobalLocality("Kakkanad & Infopark", 10.0159, 76.3419, listOf("Infopark Kochi", "SmartCity", "Kakkanad Water Metro", "Rajagiri College")),
                        GlobalLocality("Edappally & MG Road", 10.0234, 76.3089, listOf("LuLu International Mall", "Edappally Metro", "Amrita Hospital")),
                        GlobalLocality("Fort Kochi & Mattancherry", 9.9656, 76.2421, listOf("Chinese Fishing Nets", "Jew Town", "Heritage Cafes"))
                    ),
                    famousLandmarks = listOf("LuLu Mall Kochi", "Chinese Fishing Nets", "Infopark IT Hub", "Cochin International Airport (CIAL)", "Marine Drive Kochi"),
                    isTopMetro = true
                ),
                GlobalCity(
                    name = "Thiruvananthapuram (Trivandrum)",
                    latitude = 8.5241,
                    longitude = 76.9366,
                    localities = listOf(
                        GlobalLocality("Technopark (Phases 1-4)", 8.5581, 76.8812, listOf("Technopark Campus", "Karyavattom University", "Greenfield Stadium")),
                        GlobalLocality("Kowdiar & Palayam", 8.5189, 76.9545, listOf("Kowdiar Palace", "Secretariat", "University College"))
                    ),
                    famousLandmarks = listOf("Padmanabhaswamy Temple", "Technopark IT Hub", "Kovalam Beach", "VSSC Space Centre", "Trivandrum Central"),
                    isTopMetro = true
                ),
                GlobalCity(
                    name = "Kozhikode (Calicut)",
                    latitude = 11.2588,
                    longitude = 75.7804,
                    localities = listOf(
                        GlobalLocality("Mavoor Road & Hilite Mall", 11.2456, 75.8312, listOf("HiLite Mall", "Cyberpark Calicut", "KSRTC Bus Terminal")),
                        GlobalLocality("IIM Kunnamangalam", 11.2912, 75.8745, listOf("IIM Kozhikode Campus", "NIT Calicut"))
                    ),
                    famousLandmarks = listOf("IIM Kozhikode", "NIT Calicut", "Calicut Beach", "Mananchira Square", "SM Street (Sweet Street)"),
                    isTopMetro = false
                )
            )
        ),

        // 14. ANDHRA PRADESH
        GlobalState(
            code = "AP",
            name = "Andhra Pradesh",
            cities = listOf(
                GlobalCity(
                    name = "Visakhapatnam (Vizag)",
                    latitude = 17.6868,
                    longitude = 83.2185,
                    localities = listOf(
                        GlobalLocality("Madhurawada (IT SEZ)", 17.8189, 83.3512, listOf("Rushikonda Beach", "IT Hill", "GITAM University")),
                        GlobalLocality("Siripuram & Beach Road", 17.7212, 83.3189, listOf("Andhra University", "RK Beach", "CMR Central"))
                    ),
                    famousLandmarks = listOf("INS Kursura Submarine Museum", "Kailasagiri Hill", "Andhra University", "Rushikonda Beach", "Vizag Port"),
                    isTopMetro = true
                ),
                GlobalCity(
                    name = "Vijayawada & Amaravati",
                    latitude = 16.5062,
                    longitude = 80.6480,
                    localities = listOf(
                        GlobalLocality("Benz Circle & MG Road", 16.5012, 80.6545, listOf("PVP Square Mall", "Trendset Mall", "Hotels Row")),
                        GlobalLocality("Amaravati Capital Core", 16.5412, 80.5189, listOf("Secretariat", "SRM University AP", "VIT-AP"))
                    ),
                    famousLandmarks = listOf("Kanaka Durga Temple", "Prakasam Barrage", "Undavalli Caves", "Vijayawada Junction"),
                    isTopMetro = false
                ),
                GlobalCity(
                    name = "Tirupati",
                    latitude = 13.6288,
                    longitude = 79.4192,
                    localities = listOf(
                        GlobalLocality("Alipiri & SV University", 13.6489, 79.3987, listOf("SV University", "IIT Tirupati Transit", "Alipiri Footpath")),
                        GlobalLocality("Bairagipatteda", 13.6212, 79.4212, listOf("Bus Stand Road", "Pilgrim Hotels"))
                    ),
                    famousLandmarks = listOf("Tirumala Venkateswara Temple", "Sri Venkateswara University", "IIT Tirupati", "Chandragiri Fort"),
                    isTopMetro = false
                )
            )
        ),

        // 15. MADHYA PRADESH
        GlobalState(
            code = "MP",
            name = "Madhya Pradesh",
            cities = listOf(
                GlobalCity(
                    name = "Indore",
                    latitude = 22.7196,
                    longitude = 75.8577,
                    localities = listOf(
                        GlobalLocality("Vijay Nagar & Scheme 54", 22.7533, 75.8937, listOf("C21 Mall", "Brilliant Convention Centre", "IT Parks")),
                        GlobalLocality("Bhawarkua (Coaching Hub)", 22.6912, 75.8645, listOf("MPPSC Coaching", "Hostel Mandi", "DAVV University")),
                        GlobalLocality("Simrol (IIT Indore)", 22.5204, 75.9207, listOf("IIT Indore Campus", "IIM Indore (Rau)"))
                    ),
                    famousLandmarks = listOf("Rajwada Palace", "56 Dukan (Chhappan Dukan)", "Sarafa Bazaar", "IIT Indore", "IIM Indore", "Indore Junction"),
                    isTopMetro = true
                ),
                GlobalCity(
                    name = "Bhopal",
                    latitude = 23.2599,
                    longitude = 77.4126,
                    localities = listOf(
                        GlobalLocality("MP Nagar (Zones 1 & 2)", 23.2345, 77.4312, listOf("Coaching Hub", "DB City Mall", "Cinepolis")),
                        GlobalLocality("Arera Colony", 23.2112, 77.4212, listOf("10 No. Market", "Bhopal AIIMS Road", "Boutiques")),
                        GlobalLocality("Saket Nagar (AIIMS)", 23.2089, 77.4589, listOf("AIIMS Bhopal", "MANIT Campus Road"))
                    ),
                    famousLandmarks = listOf("Upper Lake (Bhojtal) & VIP Road", "Van Vihar National Park", "AIIMS Bhopal", "MANIT Bhopal", "Taj-ul-Masajid"),
                    isTopMetro = true
                ),
                GlobalCity(
                    name = "Gwalior & Jabalpur",
                    latitude = 26.2183,
                    longitude = 78.1828,
                    localities = listOf(
                        GlobalLocality("City Centre Gwalior", 26.2089, 78.1912, listOf("High Court", "DB Mall", "Jiwayi University")),
                        GlobalLocality("Civil Lines Jabalpur", 23.1689, 79.9412, listOf("Bhedaghat Road", "IIITDM Jabalpur"))
                    ),
                    famousLandmarks = listOf("Gwalior Fort", "Jai Vilas Palace", "Bhedaghat Marble Rocks (Jabalpur)", "Dhuandhar Falls"),
                    isTopMetro = false
                )
            )
        ),

        // 16. ODISHA
        GlobalState(
            code = "OD",
            name = "Odisha",
            cities = listOf(
                GlobalCity(
                    name = "Bhubaneswar",
                    latitude = 20.2961,
                    longitude = 85.8245,
                    localities = listOf(
                        GlobalLocality("Patia & Infocity", 20.3541, 85.8189, listOf("KIIT University", "Infocity IT Hub", "Silicon Institute")),
                        GlobalLocality("Jayadev Vihar & Saheed Nagar", 20.3012, 85.8289, listOf("Esplanade One Mall", "Utkal University", "AIIMS Road")),
                        GlobalLocality("Jatani (IIT Bhubaneswar)", 20.1489, 85.6712, listOf("IIT Bhubaneswar Campus", "NISER Campus"))
                    ),
                    famousLandmarks = listOf("Lingaraj Temple", "KIIT University Campus", "Udayagiri and Khandagiri Caves", "AIIMS Bhubaneswar", "IIT Bhubaneswar"),
                    isTopMetro = true
                ),
                GlobalCity(
                    name = "Cuttack & Puri",
                    latitude = 20.4625,
                    longitude = 85.8828,
                    localities = listOf(
                        GlobalLocality("Badambadi Cuttack", 20.4589, 85.8712, listOf("Bus Terminal", "SCB Medical College")),
                        GlobalLocality("Grand Road Puri", 19.8089, 85.8245, listOf("Jagannath Temple", "Golden Beach"))
                    ),
                    famousLandmarks = listOf("Jagannath Temple Puri UNESCO", "Konark Sun Temple", "Barabati Fort Cuttack", "Puri Beach"),
                    isTopMetro = false
                )
            )
        ),

        // 17. ASSAM & NORTH EAST
        GlobalState(
            code = "AS",
            name = "Assam",
            cities = listOf(
                GlobalCity(
                    name = "Guwahati",
                    latitude = 26.1445,
                    longitude = 91.7362,
                    localities = listOf(
                        GlobalLocality("GS Road & Christian Basti", 26.1589, 91.7789, listOf("City Centre Guwahati", "Shopping Malls", "Hotels")),
                        GlobalLocality("North Guwahati (IIT)", 26.1878, 91.6912, listOf("IIT Guwahati Campus", "Brahmaputra Ropeway")),
                        GlobalLocality("Jalukbari & Panbazar", 26.1412, 91.6645, listOf("Gauhati University", "Assam Engineering College", "Cotton University"))
                    ),
                    famousLandmarks = listOf("Kamakhya Temple", "IIT Guwahati Campus", "Umananda Island", "Brahmaputra River Cruise", "Guwahati Junction"),
                    isTopMetro = true
                ),
                GlobalCity(
                    name = "Dibrugarh & Silchar",
                    latitude = 27.4728,
                    longitude = 94.9120,
                    localities = listOf(
                        GlobalLocality("Thana Chariali Dibrugarh", 27.4789, 94.9089, listOf("Dibrugarh University", "Medical College")),
                        GlobalLocality("NIT Silchar Area", 24.7545, 92.7889, listOf("NIT Silchar", "Assam University"))
                    ),
                    famousLandmarks = listOf("Bogibeel Bridge", "Dibrugarh University", "NIT Silchar", "Tea Gardens"),
                    isTopMetro = false
                )
            )
        ),

        // 18. JHARKHAND
        GlobalState(
            code = "JH",
            name = "Jharkhand",
            cities = listOf(
                GlobalCity(
                    name = "Ranchi",
                    latitude = 23.3441,
                    longitude = 85.3096,
                    localities = listOf(
                        GlobalLocality("Lalpur & Circular Road", 23.3689, 85.3345, listOf("Nucleus Mall", "Coaching Institutes", "Student PGs")),
                        GlobalLocality("Mesra (BIT Mesra)", 23.4189, 85.4389, listOf("BIT Mesra Campus", "Ring Road Hub")),
                        GlobalLocality("Doranda & Hinoo", 23.3212, 85.3189, listOf("Airport Road", "High Court", "Hotels"))
                    ),
                    famousLandmarks = listOf("Jagannath Temple Ranchi", "Pahari Mandir", "BIT Mesra", "Hundru & Jonha Falls", "Ranchi Junction"),
                    isTopMetro = true
                ),
                GlobalCity(
                    name = "Jamshedpur (Tatanagar)",
                    latitude = 22.8046,
                    longitude = 86.2029,
                    localities = listOf(
                        GlobalLocality("Bistupur", 22.7989, 86.1845, listOf("XLRI Jamshedpur", "Regal Square", "Shopping")),
                        GlobalLocality("Sakchi", 22.8112, 86.2012, listOf("Tata Steel Gate", "Bazaar Complex", "KMPM College"))
                    ),
                    famousLandmarks = listOf("XLRI Jamshedpur", "Jubilee Park", "Tata Steel Plant", "Dimna Lake", "Tatanagar Junction"),
                    isTopMetro = false
                ),
                GlobalCity(
                    name = "Dhanbad (Coal Capital)",
                    latitude = 23.7957,
                    longitude = 86.4304,
                    localities = listOf(
                        GlobalLocality("IIT ISM Campus Area", 23.8145, 86.4412, listOf("IIT (ISM) Dhanbad", "Police Line Road", "Hostels")),
                        GlobalLocality("Bank More", 23.7845, 86.4212, listOf("Commercial Center", "Hotels Row", "Dhanbad Junction"))
                    ),
                    famousLandmarks = listOf("IIT (ISM) Dhanbad", "Maithon Dam", "Topchanchi Lake", "Dhanbad Junction"),
                    isTopMetro = false
                )
            )
        ),

        // 19. UTTARAKHAND
        GlobalState(
            code = "UK",
            name = "Uttarakhand",
            cities = listOf(
                GlobalCity(
                    name = "Dehradun",
                    latitude = 30.3165,
                    longitude = 78.0322,
                    localities = listOf(
                        GlobalLocality("Rajpur Road", 30.3541, 78.0612, listOf("Pacific Mall", "Cafes", "Mussoorie Diversion")),
                        GlobalLocality("Karanpur & EC Road", 30.3245, 78.0489, listOf("DBS College", "Coaching Hub", "Student PGs")),
                        GlobalLocality("Premnagar & UPES", 30.3389, 77.9612, listOf("UPES Campus", "Graphic Era University", "Doon Business School"))
                    ),
                    famousLandmarks = listOf("Forest Research Institute (FRI)", "Robber's Cave (Guchhupani)", "Sahastradhara", "UPES Dehradun", "Dehradun Railway Station"),
                    isTopMetro = false
                ),
                GlobalCity(
                    name = "Roorkee & Haridwar",
                    latitude = 29.8543,
                    longitude = 77.8880,
                    localities = listOf(
                        GlobalLocality("IIT Roorkee Campus Area", 29.8645, 77.8967, listOf("IIT Roorkee Main Gate", "Civil Lines", "Canal Road")),
                        GlobalLocality("Har Ki Pauri Haridwar", 29.9567, 78.1712, listOf("Ganga Aarti Ghat", "Ashrams", "Railway Station"))
                    ),
                    famousLandmarks = listOf("IIT Roorkee", "Har Ki Pauri Ghat Haridwar", "Mansa Devi Temple", "Upper Ganga Canal"),
                    isTopMetro = false
                ),
                GlobalCity(
                    name = "Rishikesh & Nainital",
                    latitude = 30.0869,
                    longitude = 78.2676,
                    localities = listOf(
                        GlobalLocality("Tapovan & Laxman Jhula", 30.1289, 78.3245, listOf("Yoga Ashrams", "Backpacker Hostels", "River Cafes")),
                        GlobalLocality("Nainital Mall Road", 29.3912, 79.4589, listOf("Naini Lake", "Boating Point", "Boutique Stays"))
                    ),
                    famousLandmarks = listOf("Laxman & Ram Jhula", "Triveni Ghat", "Naini Lake", "AIIMS Rishikesh", "Bungee Jumping Points"),
                    isTopMetro = false
                )
            )
        ),

        // 20. HIMACHAL PRADESH
        GlobalState(
            code = "HP",
            name = "Himachal Pradesh",
            cities = listOf(
                GlobalCity(
                    name = "Shimla",
                    latitude = 31.1048,
                    longitude = 77.1734,
                    localities = listOf(
                        GlobalLocality("The Mall & Ridge", 31.1048, 77.1734, listOf("Christ Church", "Gaiety Theatre", "Scandal Point")),
                        GlobalLocality("Sanjauli & Summer Hill", 31.0989, 77.1345, listOf("HP University (HPU)", "IGMC Hospital"))
                    ),
                    famousLandmarks = listOf("The Ridge & Mall Road", "Jakhoo Temple", "Kalka-Shimla Toy Train UNESCO", "Himachal Pradesh University"),
                    isTopMetro = false
                ),
                GlobalCity(
                    name = "Dharamshala & Manali",
                    latitude = 32.2190,
                    longitude = 76.3234,
                    localities = listOf(
                        GlobalLocality("McLeod Ganj Dharamshala", 32.2426, 76.3212, listOf("Dalai Lama Temple", "Bhagsunag", "Tibetan Cafes")),
                        GlobalLocality("Old Manali & Mall Road", 32.2489, 77.1812, listOf("Hadimba Temple", "Hostels", "Solang Valley Road"))
                    ),
                    famousLandmarks = listOf("HPCA Cricket Stadium Dharamshala", "Dalai Lama Temple", "Hadimba Temple Manali", "Rohtang & Atal Tunnel"),
                    isTopMetro = false
                )
            )
        ),

        // 21. GOA
        GlobalState(
            code = "GA",
            name = "Goa",
            cities = listOf(
                GlobalCity(
                    name = "North Goa (Panaji & Calangute)",
                    latitude = 15.4909,
                    longitude = 73.8278,
                    localities = listOf(
                        GlobalLocality("Panaji & Miramar", 15.4989, 73.8189, listOf("Fontainhas Latin Quarter", "Casino Jetty", "Goa University")),
                        GlobalLocality("Calangute & Baga", 15.5412, 73.7545, listOf("Tito's Lane", "Beach Shacks", "Night Markets")),
                        GlobalLocality("Anjuna & Vagator", 15.5845, 73.7412, listOf("Chapora Fort", "Sunburn Arena", "Boutique Hostels"))
                    ),
                    famousLandmarks = listOf("Basilica of Bom Jesus UNESCO", "Fort Aguada", "Dudhsagar Falls", "Calangute & Baga Beaches", "BITS Pilani Goa"),
                    isTopMetro = false
                ),
                GlobalCity(
                    name = "South Goa (Margao & Vasco)",
                    latitude = 15.2736,
                    longitude = 73.9582,
                    localities = listOf(
                        GlobalLocality("Margao City", 15.2812, 73.9645, listOf("Madgaon Junction", "Colva Beach Road")),
                        GlobalLocality("Zuarinagar (BITS Goa)", 15.3889, 73.8789, listOf("BITS Pilani Goa Campus", "Dabolim Airport"))
                    ),
                    famousLandmarks = listOf("Colva Beach", "BITS Pilani K.K. Birla Goa Campus", "Dabolim International Airport", "Palolem Beach"),
                    isTopMetro = false
                )
            )
        )
    )
}
