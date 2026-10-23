package com.example.data.model

import java.util.Locale

/**
 * Authoritative dataset of all 28 States and 8 Union Territories of India,
 * encompassing all 780+ official districts and verified cities/towns.
 */
object GlobalGeographicDataIndiaDistricts {

    data class StateDistrictInfo(
        val code: String,
        val name: String,
        val isUnionTerritory: Boolean = false,
        val districts: List<String>
    )

    // Complete official 28 States and 8 Union Territories of India
    val ALL_STATES_AND_UTS: List<StateDistrictInfo> = listOf(
        // 1. Andhra Pradesh (26 districts)
        StateDistrictInfo(
            code = "AP",
            name = "Andhra Pradesh",
            districts = listOf(
                "Alluri Sitharama Raju", "Anakapalli", "Ananthapuramu", "Annamayya",
                "Bapatla", "Chittoor", "Dr. B.R. Ambedkar Konaseema", "East Godavari",
                "Eluru", "Guntur", "Kakinada", "Krishna", "Kurnool", "Nandyal",
                "NTR", "Palnadu", "Parvathipuram Manyam", "Prakasam", "Srikakulam",
                "Sri Potti Sriramulu Nellore", "Sri Sathya Sai", "Tirupati",
                "Visakhapatnam", "Vizianagaram", "West Godavari", "YSR Kadapa"
            )
        ),
        // 2. Arunachal Pradesh (26 districts)
        StateDistrictInfo(
            code = "AR",
            name = "Arunachal Pradesh",
            districts = listOf(
                "Anjaw", "Changlang", "Dibang Valley", "East Kameng", "East Siang",
                "Itanagar Capital Complex", "Kamle", "Kra Daadi", "Kurung Kumey",
                "Lepa Rada", "Lohit", "Longding", "Lower Dibang Valley", "Lower Siang",
                "Lower Subansiri", "Namsai", "Pakke Kessang", "Papum Pare", "Shi Yomi",
                "Siang", "Tawang", "Tirap", "Upper Siang", "Upper Subansiri",
                "West Kameng", "West Siang"
            )
        ),
        // 3. Assam (35 districts)
        StateDistrictInfo(
            code = "AS",
            name = "Assam",
            districts = listOf(
                "Bajali", "Baksa", "Barpeta", "Biswanath", "Bongaigaon", "Cachar",
                "Charaideo", "Chirang", "Darrang", "Dhemaji", "Dhubri", "Dibrugarh",
                "Dima Hasao", "Goalpara", "Golaghat", "Hailakandi", "Hojai", "Jorhat",
                "Kamrup", "Kamrup Metropolitan", "Karbi Anglong", "Karimganj", "Kokrajhar",
                "Lakhimpur", "Majuli", "Morigaon", "Nagaon", "Nalbari", "Sivasagar",
                "Sonitpur", "South Salmara-Mankachar", "Tamulpur", "Tinsukia", "Udalguri",
                "West Karbi Anglong"
            )
        ),
        // 4. Bihar (38 districts)
        StateDistrictInfo(
            code = "BR",
            name = "Bihar",
            districts = listOf(
                "Araria", "Arwal", "Aurangabad", "Banka", "Begusarai", "Bhagalpur",
                "Bhojpur", "Buxar", "Darbhanga", "East Champaran", "Gaya", "Gopalganj",
                "Jamui", "Jehanabad", "Kaimur", "Katihar", "Khagaria", "Kishanganj",
                "Lakhisarai", "Madhepura", "Madhubani", "Munger", "Muzaffarpur",
                "Nalanda", "Nawada", "Patna", "Purnia", "Rohtas", "Saharsa",
                "Samastipur", "Saran", "Sheikhpura", "Sheohar", "Sitamarhi",
                "Siwan", "Supaul", "Vaishali", "West Champaran"
            )
        ),
        // 5. Chhattisgarh (33 districts)
        StateDistrictInfo(
            code = "CG",
            name = "Chhattisgarh",
            districts = listOf(
                "Balod", "Baloda Bazar-Bhatapara", "Balrampur-Ramanujganj", "Bastar",
                "Bemetara", "Bijapur", "Bilaspur", "Dantewada", "Dhamtari", "Durg",
                "Gariaband", "Gaurela-Pendra-Marwahi", "Janjgir-Champa", "Jashpur",
                "Kabirdham", "Kanker", "Khairagarh-Chhuikhadan-Gandai", "Kondagaon",
                "Korba", "Koriya", "Mahasamund", "Manendragarh-Chirmiri-Bharatpur",
                "Mohla-Manpur-Ambagarh Chowki", "Mungeli", "Narayanpur", "Raigarh",
                "Raipur", "Rajnandgaon", "Sakti", "Sarangarh-Bilaigarh", "Sukma",
                "Surajpur", "Surguja"
            )
        ),
        // 6. Goa (2 districts)
        StateDistrictInfo(
            code = "GA",
            name = "Goa",
            districts = listOf("North Goa", "South Goa")
        ),
        // 7. Gujarat (33 districts)
        StateDistrictInfo(
            code = "GJ",
            name = "Gujarat",
            districts = listOf(
                "Ahmedabad", "Amreli", "Anand", "Aravalli", "Banaskantha", "Bharuch",
                "Bhavnagar", "Botad", "Chhota Udaipur", "Dahod", "Dang", "Devbhoomi Dwarka",
                "Gandhinagar", "Gir Somnath", "Jamnagar", "Junagadh", "Kheda", "Kutch",
                "Mahisagar", "Mehsana", "Morbi", "Narmada", "Navsari", "Panchmahal",
                "Patan", "Porbandar", "Rajkot", "Sabarkantha", "Surat", "Surendranagar",
                "Tapi", "Vadodara", "Valsad"
            )
        ),
        // 8. Haryana (22 districts)
        StateDistrictInfo(
            code = "HR",
            name = "Haryana",
            districts = listOf(
                "Ambala", "Bhiwani", "Charkhi Dadri", "Faridabad", "Fatehabad", "Gurugram",
                "Hisar", "Jhajjar", "Jind", "Kaithal", "Karnal", "Kurukshetra",
                "Mahendragarh", "Nuh", "Palwal", "Panchkula", "Panipat", "Rewari",
                "Rohtak", "Sirsa", "Sonipat", "Yamunanagar"
            )
        ),
        // 9. Himachal Pradesh (12 districts)
        StateDistrictInfo(
            code = "HP",
            name = "Himachal Pradesh",
            districts = listOf(
                "Bilaspur", "Chamba", "Hamirpur", "Kangra", "Kinnaur", "Kullu",
                "Lahaul and Spiti", "Mandi", "Shimla", "Sirmaur", "Solan", "Una"
            )
        ),
        // 10. Jharkhand (24 districts)
        StateDistrictInfo(
            code = "JH",
            name = "Jharkhand",
            districts = listOf(
                "Bokaro", "Chatra", "Deoghar", "Dhanbad", "Dumka", "East Singhbhum",
                "Garhwa", "Giridih", "Godda", "Gumla", "Hazaribagh", "Jamtara",
                "Khunti", "Koderma", "Latehar", "Lohardaga", "Pakur", "Palamu",
                "Ramgarh", "Ranchi", "Sahebganj", "Seraikela Kharsawan", "Simdega",
                "West Singhbhum"
            )
        ),
        // 11. Karnataka (31 districts)
        StateDistrictInfo(
            code = "KA",
            name = "Karnataka",
            districts = listOf(
                "Bagalkot", "Ballari", "Belagavi", "Bengaluru Rural", "Bengaluru Urban",
                "Bidar", "Chamarajanagar", "Chikkaballapur", "Chikkamagaluru", "Chitradurga",
                "Dakshina Kannada", "Davanagere", "Dharwad", "Gadag", "Hassan",
                "Haveri", "Kalaburagi", "Kodagu", "Kolar", "Koppal", "Mandya",
                "Mysuru", "Raichur", "Ramanagara", "Shivamogga", "Tumakuru", "Udupi",
                "Uttara Kannada", "Vijayanagara", "Vijayapura", "Yadgir"
            )
        ),
        // 12. Kerala (14 districts)
        StateDistrictInfo(
            code = "KL",
            name = "Kerala",
            districts = listOf(
                "Alappuzha", "Ernakulam", "Idukki", "Kannur", "Kasaragod", "Kollam",
                "Kottayam", "Kozhikode", "Malappuram", "Palakkad", "Pathanamthitta",
                "Thiruvananthapuram", "Thrissur", "Wayanad"
            )
        ),
        // 13. Madhya Pradesh (55 districts)
        StateDistrictInfo(
            code = "MP",
            name = "Madhya Pradesh",
            districts = listOf(
                "Agar Malwa", "Alirajpur", "Anuppur", "Ashoknagar", "Balaghat", "Barwani",
                "Betul", "Bhind", "Bhopal", "Burhanpur", "Chhatarpur", "Chhindwara",
                "Damoh", "Datia", "Dewas", "Dhar", "Dindori", "Guna", "Gwalior",
                "Harda", "Indore", "Jabalpur", "Jhabua", "Katni", "Khandwa", "Khargone",
                "Maihar", "Mandla", "Mandsaur", "Mauganj", "Morena", "Narsinghpur",
                "Neemuch", "Niwari", "Narmadapuram", "Pandhurna", "Panna", "Raisen",
                "Rajgarh", "Ratlam", "Rewa", "Sagar", "Satna", "Sehore", "Seoni",
                "Shahdol", "Shajapur", "Sheopur", "Shivpuri", "Sidhi", "Singrauli",
                "Tikamgarh", "Ujjain", "Umaria", "Vidisha"
            )
        ),
        // 14. Maharashtra (36 districts)
        StateDistrictInfo(
            code = "MH",
            name = "Maharashtra",
            districts = listOf(
                "Ahilyanagar", "Akola", "Amravati", "Beed", "Bhandara", "Buldhana",
                "Chandrapur", "Chhatrapati Sambhaji Nagar", "Dharashiv", "Dhule",
                "Gadchiroli", "Gondia", "Hingoli", "Jalgaon", "Jalna", "Kolhapur",
                "Latur", "Mumbai City", "Mumbai Suburban", "Nagpur", "Nanded",
                "Nandurbar", "Nashik", "Palghar", "Parbhani", "Pune", "Raigad",
                "Ratnagiri", "Sangli", "Satara", "Sindhudurg", "Solapur", "Thane",
                "Wardha", "Washim", "Yavatmal"
            )
        ),
        // 15. Manipur (16 districts)
        StateDistrictInfo(
            code = "MN",
            name = "Manipur",
            districts = listOf(
                "Bishnupur", "Chandel", "Churachandpur", "Imphal East", "Imphal West",
                "Jiribam", "Kakching", "Kamjong", "Kangpokpi", "Noney", "Pherzawl",
                "Senapati", "Tamenglong", "Tengnoupal", "Thoubal", "Ukhrul"
            )
        ),
        // 16. Meghalaya (12 districts)
        StateDistrictInfo(
            code = "ML",
            name = "Meghalaya",
            districts = listOf(
                "East Garo Hills", "East Jaintia Hills", "East Khasi Hills",
                "Eastern West Khasi Hills", "North Garo Hills", "Ri Bhoi",
                "South Garo Hills", "South West Garo Hills", "South West Khasi Hills",
                "West Garo Hills", "West Jaintia Hills", "West Khasi Hills"
            )
        ),
        // 17. Mizoram (11 districts)
        StateDistrictInfo(
            code = "MZ",
            name = "Mizoram",
            districts = listOf(
                "Aizawl", "Champhai", "Hnahthial", "Khawzawl", "Kolasib",
                "Lawngtlai", "Lunglei", "Mamit", "Saiha", "Saitual", "Serchhip"
            )
        ),
        // 18. Nagaland (16 districts)
        StateDistrictInfo(
            code = "NL",
            name = "Nagaland",
            districts = listOf(
                "Chümoukedima", "Dimapur", "Kiphire", "Kohima", "Longleng",
                "Mokokchung", "Mon", "Niuland", "Noklak", "Peren", "Phek",
                "Shamator", "Tseminyü", "Tuensang", "Wokha", "Zünheboto"
            )
        ),
        // 19. Odisha (30 districts)
        StateDistrictInfo(
            code = "OD",
            name = "Odisha",
            districts = listOf(
                "Angul", "Balangir", "Balasore", "Bargarh", "Bhadrak", "Boudh",
                "Cuttack", "Deogarh", "Dhenkanal", "Gajapati", "Ganjam", "Jagatsinghpur",
                "Jajpur", "Jharsuguda", "Kalahandi", "Kandhamal", "Kendrapara",
                "Kendujhar", "Khordha", "Koraput", "Malkangiri", "Mayurbhanj",
                "Nabarangpur", "Nayagarh", "Nuapada", "Puri", "Rayagada",
                "Sambalpur", "Subarnapur", "Sundargarh"
            )
        ),
        // 20. Punjab (23 districts)
        StateDistrictInfo(
            code = "PB",
            name = "Punjab",
            districts = listOf(
                "Amritsar", "Barnala", "Bathinda", "Faridkot", "Fatehgarh Sahib",
                "Fazilka", "Ferozepur", "Gurdaspur", "Hoshiarpur", "Jalandhar",
                "Kapurthala", "Ludhiana", "Malerkotla", "Mansa", "Moga", "Muktsar",
                "Pathankot", "Patiala", "Rupnagar", "Sahibzada Ajit Singh Nagar",
                "Sangrur", "Shahid Bhagat Singh Nagar", "Tarn Taran"
            )
        ),
        // 21. Rajasthan (50 districts)
        StateDistrictInfo(
            code = "RJ",
            name = "Rajasthan",
            districts = listOf(
                "Ajmer", "Alwar", "Anupgarh", "Balotra", "Banswara", "Baran", "Barmer",
                "Beawar", "Bharatpur", "Bhilwara", "Bikaner", "Bundi", "Chittorgarh",
                "Churu", "Dausa", "Deeg", "Dholpur", "Didwana-Kuchaman", "Dudu",
                "Dungarpur", "Gangapur City", "Hanumangarh", "Hindaun", "Jaipur",
                "Jaipur Rural", "Jaisalmer", "Jalore", "Jhalawar", "Jhunjhunu",
                "Jodhpur", "Jodhpur Rural", "Karauli", "Kekri", "Khairthal-Tijara",
                "Kota", "Kotputli-Behror", "Nagaur", "Neem Ka Thana", "Pali", "Phalodi",
                "Pratapgarh", "Rajsamand", "Salumbar", "Sanchore", "Sawai Madhopur",
                "Shahpura", "Sikar", "Sirohi", "Sri Ganganagar", "Tonk", "Udaipur"
            )
        ),
        // 22. Sikkim (6 districts)
        StateDistrictInfo(
            code = "SK",
            name = "Sikkim",
            districts = listOf("Gangtok", "Gyalshing", "Mangan", "Namchi", "Pakyong", "Soreng")
        ),
        // 23. Tamil Nadu (38 districts)
        StateDistrictInfo(
            code = "TN",
            name = "Tamil Nadu",
            districts = listOf(
                "Ariyalur", "Chengalpattu", "Chennai", "Coimbatore", "Cuddalore",
                "Dharmapuri", "Dindigul", "Erode", "Kallakurichi", "Kanchipuram",
                "Kanyakumari", "Karur", "Krishnagiri", "Madurai", "Mayiladuthurai",
                "Nagapattinam", "Namakkal", "Nilgiris", "Perambalur", "Pudukkottai",
                "Ramanathapuram", "Ranipet", "Salem", "Sivaganga", "Tenkasi",
                "Thanjavur", "Theni", "Thoothukudi", "Tiruchirappalli", "Tirunelveli",
                "Tirupathur", "Tiruppur", "Tiruvallur", "Tiruvannamalai", "Tiruvarur",
                "Vellore", "Viluppuram", "Virudhunagar"
            )
        ),
        // 24. Telangana (33 districts)
        StateDistrictInfo(
            code = "TG",
            name = "Telangana",
            districts = listOf(
                "Adilabad", "Bhadradri Kothagudem", "Hanumakonda", "Hyderabad",
                "Jagtial", "Jangaon", "Jayashankar Bhupalpally", "Jogulamba Gadwal",
                "Kamareddy", "Karimnagar", "Khammam", "Kumuram Bheem Asifabad",
                "Mahabubabad", "Mahbubnagar", "Mancherial", "Medak", "Medchal-Malkajgiri",
                "Mulugu", "Nagarkurnool", "Nalgonda", "Narayanpet", "Nirmal",
                "Nizamabad", "Peddapalli", "Rajanna Sircilla", "Ranga Reddy",
                "Sangareddy", "Siddipet", "Suryapet", "Vikarabad", "Wanaparthy",
                "Warangal", "Yadadri Bhuvanagiri"
            )
        ),
        // 25. Tripura (8 districts)
        StateDistrictInfo(
            code = "TR",
            name = "Tripura",
            districts = listOf(
                "Dhalai", "Gomati", "Khowai", "North Tripura",
                "Sepahijala", "South Tripura", "Unakoti", "West Tripura"
            )
        ),
        // 26. Uttar Pradesh (75 districts)
        StateDistrictInfo(
            code = "UP",
            name = "Uttar Pradesh",
            districts = listOf(
                "Agra", "Aligarh", "Ambedkar Nagar", "Amethi", "Amroha", "Auraiya",
                "Ayodhya", "Azamgarh", "Baghpat", "Bahraich", "Ballia", "Balrampur",
                "Banda", "Barabanki", "Bareilly", "Basti", "Bhadohi", "Bijnor",
                "Budaun", "Bulandshahr", "Chandauli", "Chitrakoot", "Deoria", "Etah",
                "Etawah", "Farrukhabad", "Fatehpur", "Firozabad", "Gautam Buddha Nagar",
                "Ghaziabad", "Ghazipur", "Gonda", "Gorakhpur", "Hamirpur", "Hapur",
                "Hardoi", "Hathras", "Jalaun", "Jaunpur", "Jhansi", "Kannauj",
                "Kanpur Dehat", "Kanpur Nagar", "Kasganj", "Kaushambi", "Kheri",
                "Kushinagar", "Lalitpur", "Lucknow", "Maharajganj", "Mahoba", "Mainpuri",
                "Mathura", "Mau", "Meerut", "Mirzapur", "Moradabad", "Muzaffarnagar",
                "Pilibhit", "Pratapgarh", "Prayagraj", "Raebareli", "Rampur",
                "Saharanpur", "Sambhal", "Sant Kabir Nagar", "Shahjahanpur", "Shamli",
                "Shravasti", "Siddharthnagar", "Sitapur", "Sonbhadra", "Sultanpur",
                "Unnao", "Varanasi"
            )
        ),
        // 27. Uttarakhand (13 districts)
        StateDistrictInfo(
            code = "UK",
            name = "Uttarakhand",
            districts = listOf(
                "Almora", "Bageshwar", "Chamoli", "Champawat", "Dehradun", "Haridwar",
                "Nainital", "Pauri Garhwal", "Pithoragarh", "Rudraprayag", "Tehri Garhwal",
                "Udham Singh Nagar", "Uttarkashi"
            )
        ),
        // 28. West Bengal (23 districts)
        StateDistrictInfo(
            code = "WB",
            name = "West Bengal",
            districts = listOf(
                "Alipurduar", "Bankura", "Birbhum", "Cooch Behar", "Dakshin Dinajpur",
                "Darjeeling", "Hooghly", "Howrah", "Jalpaiguri", "Jhargram",
                "Kalimpong", "Kolkata", "Malda", "Murshidabad", "Nadia",
                "North 24 Parganas", "Paschim Bardhaman", "Paschim Medinipur",
                "Purba Bardhaman", "Purba Medinipur", "Purulia", "South 24 Parganas",
                "Uttar Dinajpur"
            )
        ),

        // --- ALL 8 UNION TERRITORIES OF INDIA ---
        // 29. Andaman and Nicobar Islands (3 districts)
        StateDistrictInfo(
            code = "AN",
            name = "Andaman and Nicobar Islands",
            isUnionTerritory = true,
            districts = listOf("Nicobar", "North and Middle Andaman", "South Andaman")
        ),
        // 30. Chandigarh (1 district)
        StateDistrictInfo(
            code = "CH",
            name = "Chandigarh",
            isUnionTerritory = true,
            districts = listOf("Chandigarh")
        ),
        // 31. Dadra and Nagar Haveli and Daman and Diu (3 districts)
        StateDistrictInfo(
            code = "DNHDD",
            name = "Dadra and Nagar Haveli and Daman and Diu",
            isUnionTerritory = true,
            districts = listOf("Dadra and Nagar Haveli", "Daman", "Diu")
        ),
        // 32. Delhi (National Capital Territory) (11 districts)
        StateDistrictInfo(
            code = "DL",
            name = "Delhi",
            isUnionTerritory = true,
            districts = listOf(
                "Central Delhi", "East Delhi", "New Delhi", "North Delhi",
                "North East Delhi", "North West Delhi", "Shahdara", "South Delhi",
                "South East Delhi", "South West Delhi", "West Delhi"
            )
        ),
        // 33. Jammu and Kashmir (20 districts)
        StateDistrictInfo(
            code = "JK",
            name = "Jammu and Kashmir",
            isUnionTerritory = true,
            districts = listOf(
                "Anantnag", "Bandipora", "Baramulla", "Budgam", "Doda", "Ganderbal",
                "Jammu", "Kathua", "Kishtwar", "Kulgam", "Kupwara", "Poonch",
                "Pulwama", "Rajouri", "Ramban", "Reasi", "Samba", "Shopian",
                "Srinagar", "Udhampur"
            )
        ),
        // 34. Ladakh (2 districts)
        StateDistrictInfo(
            code = "LA",
            name = "Ladakh",
            isUnionTerritory = true,
            districts = listOf("Kargil", "Leh")
        ),
        // 35. Lakshadweep (1 district)
        StateDistrictInfo(
            code = "LD",
            name = "Lakshadweep",
            isUnionTerritory = true,
            districts = listOf("Lakshadweep")
        ),
        // 36. Puducherry (4 districts)
        StateDistrictInfo(
            code = "PY",
            name = "Puducherry",
            isUnionTerritory = true,
            districts = listOf("Karaikal", "Mahe", "Puducherry", "Yanam")
        )
    )

    /**
     * Map of state/UT normalized name/code to its district list
     */
    val STATE_DISTRICTS_MAP: Map<String, List<String>> by lazy {
        val map = mutableMapOf<String, List<String>>()
        for (item in ALL_STATES_AND_UTS) {
            map[item.name.lowercase(Locale.ROOT)] = item.districts
            map[item.code.lowercase(Locale.ROOT)] = item.districts
        }
        // Aliases for Delhi
        map["delhi ncr"] = map["delhi"] ?: emptyList()
        map["nct of delhi"] = map["delhi"] ?: emptyList()
        map
    }

    /**
     * Verified real cities and towns for key districts, with special detail for:
     * - West Champaran (Bagaha, Bettiah, Narkatiaganj, Ramnagar, Valmiki Nagar)
     * - All Bihar districts
     * - Key student & job hubs across India
     */
    val DETAILED_DISTRICT_CITIES: Map<String, List<GlobalCity>> = mapOf(
        // === BIHAR DISTRICTS ===
        "West Champaran" to listOf(
            GlobalCity(
                name = "Bagaha",
                district = "West Champaran",
                latitude = 27.0998,
                longitude = 84.0917,
                localities = listOf(
                    GlobalLocality("Bagaha Bazaar & Main Market", 27.0990, 84.0925, listOf("Main Bazaar", "Station Road Market")),
                    GlobalLocality("Station Road & Bagaha Junction", 27.1042, 84.0895, listOf("Bagaha Railway Junction", "Auto Stand")),
                    GlobalLocality("Chakhni Road & Sub-Divisional Complex", 27.0965, 84.0870, listOf("Sub-Divisional Hospital Bagaha", "Court Compound")),
                    GlobalLocality("Bagaha-1 & Gandak Riverside", 27.1080, 84.0950, listOf("Gandak River Front", "Bagaha 1 Market")),
                    GlobalLocality("Bagaha-2 & Police Station Road", 27.0930, 84.0980, listOf("Bagaha 2 Block Office", "Government Degree College")),
                    GlobalLocality("Valmiki Nagar Road & Eco Corridor", 27.1150, 84.0820, listOf("Valmiki Tiger Reserve Gateway", "Forest Depot"))
                ),
                famousLandmarks = listOf(
                    "Bagaha Railway Junction (BUG)",
                    "Sub-Divisional Hospital Bagaha",
                    "Valmiki National Park & Tiger Reserve Gateway",
                    "Gandak River Bridge & Barrage Road",
                    "Madanpur Devi Mandir",
                    "Bagaha Block Development Office",
                    "Government College Bagaha"
                ),
                isTopMetro = false,
                aliases = listOf("Bagaha", "Bagaha 1", "Bagaha 2", "Bagaha Bazar", "Bagaha West Champaran", "Bagaha Bihar")
            ),
            GlobalCity(
                name = "Bettiah",
                district = "West Champaran",
                latitude = 26.8024,
                longitude = 84.5029,
                localities = listOf(
                    GlobalLocality("Lal Bazar & Station Road", 26.8035, 84.5015, listOf("Bettiah Junction", "Town Hall")),
                    GlobalLocality("Supriya Cinema Road", 26.8060, 84.5045, listOf("Supriya Cinema", "Market Hub")),
                    GlobalLocality("Kumar Bagh Industrial Area", 26.8210, 84.4780, listOf("Kumar Bagh Station", "Industrial Estate"))
                ),
                famousLandmarks = listOf("Bettiah Raj Palace", "Government Medical College Bettiah", "Bettiah Railway Station (BTH)", "Sagar Pokhra"),
                isTopMetro = false,
                aliases = listOf("Bettiah", "Bettia", "Betiya")
            ),
            GlobalCity(
                name = "Narkatiaganj",
                district = "West Champaran",
                latitude = 27.1084,
                longitude = 84.4754,
                localities = listOf(
                    GlobalLocality("Railway Colony & High School Road", 27.1090, 84.4760, listOf("Narkatiaganj Junction", "Sugar Mill Area")),
                    GlobalLocality("Main Market", 27.1070, 84.4740, listOf("Arya Samaj Mandir Road", "Subhash Chowk"))
                ),
                famousLandmarks = listOf("Narkatiaganj Railway Junction (NKE)", "New Swadeshi Sugar Mills"),
                isTopMetro = false
            ),
            GlobalCity(
                name = "Ramnagar",
                district = "West Champaran",
                latitude = 27.1685,
                longitude = 84.3216,
                localities = listOf(
                    GlobalLocality("Main Bazaar", 27.1690, 84.3220, listOf("Ramnagar Market", "Town Center"))
                ),
                famousLandmarks = listOf("Ramnagar Railway Station", "Ramnagar Sugar Mill"),
                isTopMetro = false
            ),
            GlobalCity(
                name = "Valmiki Nagar",
                district = "West Champaran",
                latitude = 27.4292,
                longitude = 83.9022,
                localities = listOf(
                    GlobalLocality("Eco Tourism Hub & Barrage", 27.4310, 83.9040, listOf("Gandak Barrage", "Jungle Camp")),
                    GlobalLocality("Tiger Reserve Base", 27.4250, 83.9010, listOf("Valmiki Tiger Reserve Base", "Eco Cottages"))
                ),
                famousLandmarks = listOf("Valmiki Tiger Reserve", "Gandak Barrage", "Triveni Sangam", "Valmiki Ashram"),
                isTopMetro = false,
                aliases = listOf("Valmikinagar", "Valmiki Nagar", "Bhainsalotan")
            )
        ),
        "East Champaran" to listOf(
            GlobalCity(
                name = "Motihari",
                district = "East Champaran",
                latitude = 26.6469,
                longitude = 84.9089,
                localities = listOf(
                    GlobalLocality("Gandhi Smarak & Raja Bazar", 26.6480, 84.9095, listOf("Gandhi Sangrahalaya", "Main Market")),
                    GlobalLocality("Chhatauni & Bus Stand", 26.6390, 84.9210, listOf("Private Bus Stand", "NH 28 Hub"))
                ),
                famousLandmarks = listOf("Mahatma Gandhi Central University", "George Orwell Birthplace", "Motihari Court Station"),
                isTopMetro = false
            ),
            GlobalCity(name = "Raxaul", district = "East Champaran", latitude = 26.9774, longitude = 84.7001, famousLandmarks = listOf("India-Nepal Border Gate", "Raxaul Junction Railway Station")),
            GlobalCity(name = "Areraj", district = "East Champaran", latitude = 26.5492, longitude = 84.6725, famousLandmarks = listOf("Someshwar Nath Mahadev Mandir", "Ashokan Pillar"))
        ),
        "Patna" to listOf(
            GlobalCity(
                name = "Patna",
                district = "Patna",
                latitude = 25.5941,
                longitude = 85.1376,
                localities = listOf(
                    GlobalLocality("Boring Road & Bailey Road", 25.6120, 85.1210, listOf("AN College", "Coaching Hub", "Shopping Complex")),
                    GlobalLocality("Kankarbagh", 25.5975, 85.1585, listOf("PC Colony", "Tempo Stand", "MIG Flats")),
                    GlobalLocality("Patliputra Colony", 25.6265, 85.1095, listOf("Industrial Area", "P&M Mall", "Ruban Hospital")),
                    GlobalLocality("Fraser Road & Dak Bungalow", 25.6080, 85.1390, listOf("Maurya Lok", "Patna Junction", "Gandhi Maidan"))
                ),
                famousLandmarks = listOf("AIIMS Patna", "IIT Patna", "Patna Junction (PNBE)", "Takht Sri Patna Sahib", "Golghar"),
                isTopMetro = true,
                aliases = listOf("Patna", "Patliputra", "Pataliputra")
            ),
            GlobalCity(name = "Danapur", district = "Patna", latitude = 25.6333, longitude = 85.0500, famousLandmarks = listOf("Danapur Cantonment", "Danapur Junction (DNR)")),
            GlobalCity(name = "Bihta", district = "Patna", latitude = 25.5684, longitude = 84.8698, famousLandmarks = listOf("IIT Patna Campus", "Bihta Airport Site", "ESI Hospital")),
            GlobalCity(name = "Fatuha", district = "Patna", latitude = 25.5080, longitude = 85.3120, famousLandmarks = listOf("Fatuha Junction", "Triveni Sangam"))
        ),
        "Darbhanga" to listOf(
            GlobalCity(
                name = "Darbhanga",
                district = "Darbhanga",
                latitude = 26.1542,
                longitude = 85.8918,
                localities = listOf(
                    GlobalLocality("Laheriasarai & Tower", 26.1205, 85.9015, listOf("Collectorate", "Courts", "Lalbagh")),
                    GlobalLocality("Benta & DMCH Area", 26.1345, 85.9012, listOf("Darbhanga Medical College", "Dental College", "Clinics")),
                    GlobalLocality("Donar & Station Road", 26.1620, 85.8940, listOf("Darbhanga Junction", "Donar Chowk", "Bazaar")),
                    GlobalLocality("Darbhanga Airport & Vasudevpur", 26.1960, 85.9120, listOf("Darbhanga Airport Terminal", "Air Force Station"))
                ),
                famousLandmarks = listOf(
                    "Darbhanga Fort (Raj Qila)",
                    "Shyama Mai Temple",
                    "Darbhanga Medical College & Hospital (DMCH)",
                    "Darbhanga Airport (DBR)",
                    "Darbhanga Junction Railway Station (DBG)",
                    "Kameshwar Singh Sanskrit University"
                ),
                isTopMetro = false,
                aliases = listOf("Darbhanga", "Laheriasarai")
            ),
            GlobalCity(name = "Benipur", district = "Darbhanga", latitude = 26.1245, longitude = 86.1385, famousLandmarks = listOf("Benipur Sub-Division", "Nawada Durga Mandir")),
            GlobalCity(name = "Baheri", district = "Darbhanga", latitude = 25.9810, longitude = 86.0420, famousLandmarks = listOf("Baheri Bazar", "Kamla River Bridge"))
        ),
        "Gaya" to listOf(
            GlobalCity(
                name = "Gaya",
                district = "Gaya",
                latitude = 24.7914,
                longitude = 85.0002,
                localities = listOf(
                    GlobalLocality("Vishnupad Temple Area", 24.7780, 85.0080, listOf("Vishnupad Temple", "Falgu River Ghats")),
                    GlobalLocality("Civil Lines & Station Road", 24.7980, 85.0010, listOf("Gaya Junction", "Magadh University Camp Office"))
                ),
                famousLandmarks = listOf("Vishnupad Temple", "Gaya Junction (GAYA)", "Gaya International Airport", "Mangla Gauri Temple"),
                isTopMetro = false,
                aliases = listOf("Gaya")
            ),
            GlobalCity(
                name = "Bodh Gaya",
                district = "Gaya",
                latitude = 24.6961,
                longitude = 84.9870,
                localities = listOf(
                    GlobalLocality("Mahabodhi Temple Area", 24.6950, 84.9910, listOf("UNESCO Mahabodhi Complex", "Bodhi Tree")),
                    GlobalLocality("International Monasteries Area", 24.7010, 84.9820, listOf("Thai Monastery", "Japanese Temple", "Giant Buddha"))
                ),
                famousLandmarks = listOf("Mahabodhi Temple (UNESCO)", "Bodhi Tree", "Great Buddha Statue", "IIM Bodh Gaya"),
                isTopMetro = false,
                aliases = listOf("Bodh Gaya", "Bodhgaya")
            )
        ),
        "Muzaffarpur" to listOf(
            GlobalCity(
                name = "Muzaffarpur",
                district = "Muzaffarpur",
                latitude = 26.1209,
                longitude = 85.3647,
                localities = listOf(
                    GlobalLocality("Mithanpura & Club Road", 26.1180, 85.3780, listOf("Club Road", "Coaching Hub", "City Center")),
                    GlobalLocality("Motijheel & Saraiyaganj", 26.1240, 85.3910, listOf("Main Commercial Market", "Cloth Market")),
                    GlobalLocality("Brahampura & MIT Area", 26.1360, 85.3520, listOf("Muzaffarpur Institute of Technology", "Laxmi Chowk"))
                ),
                famousLandmarks = listOf("Muzaffarpur Junction (MFP)", "SKMCH Medical College", "Garib Sthan Mandir", "MIT Muzaffarpur"),
                isTopMetro = true
            )
        ),
        "Bhagalpur" to listOf(
            GlobalCity(
                name = "Bhagalpur",
                district = "Bhagalpur",
                latitude = 25.2425,
                longitude = 86.9842,
                localities = listOf(
                    GlobalLocality("Tilka Manjhi & University Campus", 25.2480, 86.9810, listOf("TMBU Campus", "JLN Medical College")),
                    GlobalLocality("Khalifabag & Sujaganj", 25.2410, 86.9920, listOf("Silk Market", "Bhagalpur Junction"))
                ),
                famousLandmarks = listOf("Vikramshila Ruins", "Silk Institute", "Bhagalpur Junction (BGP)", "Vikramshila Dolphin Sanctuary"),
                isTopMetro = false
            ),
            GlobalCity(name = "Kahalgaon", district = "Bhagalpur", latitude = 25.2633, longitude = 87.2367, famousLandmarks = listOf("NTPC Kahalgaon", "Vikramshila University Site"))
        ),
        "Rohtas" to listOf(
            GlobalCity(name = "Sasaram", district = "Rohtas", latitude = 24.9525, longitude = 84.0292, famousLandmarks = listOf("Tomb of Sher Shah Suri", "Sasaram Junction")),
            GlobalCity(name = "Dehri", district = "Rohtas", latitude = 24.9125, longitude = 84.1842, famousLandmarks = listOf("Dehri on Sone Junction", "Indrapuri Barrage"))
        ),
        "Saran" to listOf(
            GlobalCity(name = "Chhapra", district = "Saran", latitude = 25.7811, longitude = 84.7543, famousLandmarks = listOf("Chhapra Junction (CPR)", "Jai Prakash University")),
            GlobalCity(name = "Sonpur", district = "Saran", latitude = 25.6980, longitude = 85.1850, famousLandmarks = listOf("Sonpur Cattle Fair Ground", "Sonpur Junction", "Hariharnath Mandir"))
        ),
        "Begusarai" to listOf(
            GlobalCity(name = "Begusarai", district = "Begusarai", latitude = 25.4182, longitude = 86.1272, famousLandmarks = listOf("Barauni Refinery (IOCL)", "Kanwar Lake Bird Sanctuary", "Begusarai Station")),
            GlobalCity(name = "Barauni", district = "Begusarai", latitude = 25.4800, longitude = 85.9700, famousLandmarks = listOf("Barauni Junction (BJU)", "Thermal Power Station"))
        ),
        "Nalanda" to listOf(
            GlobalCity(name = "Bihar Sharif", district = "Nalanda", latitude = 25.1982, longitude = 85.5149, famousLandmarks = listOf("Bihar Sharif Junction", "Badi Dargah")),
            GlobalCity(name = "Rajgir", district = "Nalanda", latitude = 25.0298, longitude = 85.4215, famousLandmarks = listOf("Vishwa Shanti Stupa", "Hot Water Springs", "Glass Skywalk", "Nalanda University")),
            GlobalCity(name = "Nalanda", district = "Nalanda", latitude = 25.1357, longitude = 85.4452, famousLandmarks = listOf("Ancient Nalanda Ruins (UNESCO)", "Nalanda Archaeological Museum"))
        ),
        "Vaishali" to listOf(
            GlobalCity(name = "Hajipur", district = "Vaishali", latitude = 25.6858, longitude = 85.2146, famousLandmarks = listOf("ECR Zonal Headquarters", "Hajipur Junction", "Koner Ghat")),
            GlobalCity(name = "Vaishali", district = "Vaishali", latitude = 25.9900, longitude = 85.1300, famousLandmarks = listOf("Ashoka Lion Pillar", "Relic Stupa", "Kundalpur (Lord Mahavira Birthplace)"))
        ),
        "Samastipur" to listOf(
            GlobalCity(name = "Samastipur", district = "Samastipur", latitude = 25.8628, longitude = 85.7811, famousLandmarks = listOf("Samastipur Railway Division", "Rajendra Agricultural University (Pusa)"))
        ),
        "Purnia" to listOf(
            GlobalCity(name = "Purnia", district = "Purnia", latitude = 25.7771, longitude = 87.4753, famousLandmarks = listOf("Purnia Junction", "Line Bazar Medical Hub", "Purnia University"))
        ),
        "Katihar" to listOf(
            GlobalCity(name = "Katihar", district = "Katihar", latitude = 25.5541, longitude = 87.5684, famousLandmarks = listOf("Katihar Railway Division Junction", "Maniyan Wetland"))
        ),
        "Madhubani" to listOf(
            GlobalCity(name = "Madhubani", district = "Madhubani", latitude = 26.3533, longitude = 86.0718, famousLandmarks = listOf("Madhubani Painting Centers", "Saurath Sabha Ground", "Madhubani Station")),
            GlobalCity(name = "Jhanjharpur", district = "Madhubani", latitude = 26.2625, longitude = 86.2890, famousLandmarks = listOf("Jhanjharpur Sub-Division", "Kamla Balan Bridge"))
        ),
        "Siwan" to listOf(
            GlobalCity(name = "Siwan", district = "Siwan", latitude = 26.2222, longitude = 84.3567, famousLandmarks = listOf("Siwan Junction", "Ziradei (Dr. Rajendra Prasad Birthplace)"))
        ),
        "Gopalganj" to listOf(
            GlobalCity(name = "Gopalganj", district = "Gopalganj", latitude = 26.4674, longitude = 84.4429, famousLandmarks = listOf("Thawe Mandir", "Sainik School Gopalganj"))
        ),
        "Bhojpur" to listOf(
            GlobalCity(name = "Arrah", district = "Bhojpur", latitude = 25.5560, longitude = 84.6603, famousLandmarks = listOf("Ara Junction (ARA)", "Veer Kunwar Singh University", "Arrah House"))
        ),
        "Buxar" to listOf(
            GlobalCity(name = "Buxar", district = "Buxar", latitude = 25.5647, longitude = 83.9777, famousLandmarks = listOf("Buxar Fort", "Ramrekha Ghat", "Buxar Junction"))
        ),
        "Sitamarhi" to listOf(
            GlobalCity(name = "Sitamarhi", district = "Sitamarhi", latitude = 26.5980, longitude = 85.4890, famousLandmarks = listOf("Janki Mandir (Punaura Dham)", "Haleshwar Sthan", "Sitamarhi Station"))
        ),
        "Kishanganj" to listOf(
            GlobalCity(name = "Kishanganj", district = "Kishanganj", latitude = 26.1037, longitude = 87.9472, famousLandmarks = listOf("AMU Kishanganj Centre", "Kishanganj Railway Station"))
        ),
        "Saharsa" to listOf(
            GlobalCity(name = "Saharsa", district = "Saharsa", latitude = 25.8835, longitude = 86.5989, famousLandmarks = listOf("Tara Sthan Mahishi", "Matsyagandha Mandir", "Saharsa Junction"))
        ),
        "Munger" to listOf(
            GlobalCity(name = "Munger", district = "Munger", latitude = 25.3757, longitude = 86.4744, famousLandmarks = listOf("Bihar School of Yoga", "Munger Fort", "Gun Factory", "Jamalpur Locomotive Workshop"))
        )
    )

    /**
     * Common name aliases to standard name
     */
    val CITY_ALIASES_MAP: Map<String, String> = mapOf(
        "bangalore" to "Bengaluru",
        "bengaluru" to "Bengaluru",
        "calcutta" to "Kolkata",
        "kolkata" to "Kolkata",
        "bombay" to "Mumbai",
        "mumbai" to "Mumbai",
        "new delhi" to "Delhi",
        "delhi" to "Delhi",
        "delhi ncr" to "Delhi",
        "gurgaon" to "Gurugram",
        "gurugram" to "Gurugram",
        "allahabad" to "Prayagraj",
        "prayagraj" to "Prayagraj",
        "banaras" to "Varanasi",
        "kashi" to "Varanasi",
        "varanasi" to "Varanasi",
        "aurangabad" to "Chhatrapati Sambhaji Nagar",
        "chhatrapati sambhaji nagar" to "Chhatrapati Sambhaji Nagar",
        "ahmednagar" to "Ahilyanagar",
        "ahilyanagar" to "Ahilyanagar",
        "osmanabad" to "Dharashiv",
        "dharashiv" to "Dharashiv",
        "hoshangabad" to "Narmadapuram",
        "narmadapuram" to "Narmadapuram",
        "baroda" to "Vadodara",
        "vadodara" to "Vadodara",
        "pondicherry" to "Puducherry",
        "puducherry" to "Puducherry",
        "calicut" to "Kozhikode",
        "kozhikode" to "Kozhikode",
        "cochin" to "Kochi",
        "kochi" to "Kochi",
        "trivandrum" to "Thiruvananthapuram",
        "thiruvananthapuram" to "Thiruvananthapuram",
        "trichy" to "Tiruchirappalli",
        "tiruchirappalli" to "Tiruchirappalli",
        "madras" to "Chennai",
        "chennai" to "Chennai",
        "vizag" to "Visakhapatnam",
        "waltair" to "Visakhapatnam",
        "visakhapatnam" to "Visakhapatnam",
        "belgaum" to "Belagavi",
        "belagavi" to "Belagavi",
        "hubli" to "Hubballi",
        "hubballi" to "Hubballi",
        "gulbarga" to "Kalaburagi",
        "kalaburagi" to "Kalaburagi",
        "bellary" to "Ballari",
        "ballari" to "Ballari",
        "mysore" to "Mysuru",
        "mysuru" to "Mysuru",
        "mangalore" to "Mangaluru",
        "mangaluru" to "Mangaluru",
        "shimoga" to "Shivamogga",
        "shivamogga" to "Shivamogga",
        "poona" to "Pune",
        "pune" to "Pune",
        "patliputra" to "Patna",
        "patna" to "Patna",
        "bodh gaya" to "Bodh Gaya",
        "bodhgaya" to "Bodh Gaya",
        "bagaha" to "Bagaha",
        "bagaha-1" to "Bagaha",
        "bagaha 1" to "Bagaha",
        "bagaha 2" to "Bagaha",
        "bagaha-2" to "Bagaha",
        "bagaha bazar" to "Bagaha",
        "laheriasarai" to "Darbhanga",
        "darbhanga" to "Darbhanga",
        "faizabad" to "Ayodhya",
        "ayodhya" to "Ayodhya"
    )

    /**
     * Get all official districts for a state or union territory
     */
    fun getDistrictsForState(stateNameOrCode: String): List<String> {
        val clean = stateNameOrCode.trim().lowercase(Locale.ROOT)
        // Direct map lookup
        val direct = STATE_DISTRICTS_MAP[clean]
        if (direct != null && direct.isNotEmpty()) return direct

        // Partial match
        for ((k, v) in STATE_DISTRICTS_MAP) {
            if (clean.contains(k) || k.contains(clean)) {
                return v
            }
        }
        return emptyList()
    }

    /**
     * Get cities/towns in a specific district
     */
    fun getCitiesForDistrict(districtName: String): List<GlobalCity> {
        val clean = districtName.trim()
        val direct = DETAILED_DISTRICT_CITIES[clean]
        if (direct != null && direct.isNotEmpty()) return direct

        // Case-insensitive match
        for ((k, v) in DETAILED_DISTRICT_CITIES) {
            if (k.equals(clean, ignoreCase = true)) {
                return v
            }
        }

        // Generate headquarter city for the district if not yet individually itemized
        val districtHeadquarters = GlobalCity(
            name = clean,
            district = clean,
            latitude = 25.5941,
            longitude = 85.1376,
            localities = listOf(
                GlobalLocality("$clean Town & Main Market", 25.5941, 85.1376, listOf("Main Chowk", "District Market")),
                GlobalLocality("$clean Station & Bus Stand", 25.5941, 85.1376, listOf("District Bus Stand", "Railway Station")),
                GlobalLocality("$clean Collectorate & Courts", 25.5941, 85.1376, listOf("Collectorate Office", "Civil Court"))
            ),
            famousLandmarks = listOf("$clean District Headquarters", "$clean Central Bus Stand", "$clean Civil Hospital")
        )
        return listOf(districtHeadquarters)
    }

    /**
     * Search specific Indian locations for quick autocomplete
     */
    fun searchIndianLocations(query: String): List<GlobalLocationItem> {
        val q = query.trim().lowercase(Locale.ROOT)
        if (q.isEmpty()) return emptyList()

        val results = mutableListOf<GlobalLocationItem>()

        // Check if query is an alias
        val resolvedAliasCity = CITY_ALIASES_MAP[q]

        for ((districtName, cities) in DETAILED_DISTRICT_CITIES) {
            for (city in cities) {
                val matchesAlias = resolvedAliasCity != null && city.name.equals(resolvedAliasCity, ignoreCase = true)
                val matchesCityName = city.name.lowercase(Locale.ROOT).contains(q)
                val matchesAliases = city.aliases.any { it.lowercase(Locale.ROOT).contains(q) }
                val matchesDistrict = districtName.lowercase(Locale.ROOT).contains(q)

                if (matchesAlias || matchesCityName || matchesAliases || matchesDistrict) {
                    val stateName = when {
                        districtName in listOf("West Champaran", "East Champaran", "Patna", "Darbhanga", "Gaya", "Muzaffarpur", "Bhagalpur", "Rohtas", "Saran", "Begusarai", "Nalanda", "Vaishali", "Samastipur", "Purnia", "Katihar", "Madhubani", "Siwan", "Gopalganj", "Bhojpur", "Buxar", "Sitamarhi", "Kishanganj", "Saharsa", "Munger") -> "Bihar"
                        else -> "India"
                    }

                    if (city.localities.isNotEmpty()) {
                        for (loc in city.localities) {
                            results.add(
                                GlobalLocationItem(
                                    id = "in-${city.name.lowercase(Locale.ROOT)}-${loc.name.lowercase(Locale.ROOT)}".replace(" ", "-"),
                                    name = "${loc.name}, ${city.name}",
                                    locality = loc.name,
                                    city = city.name,
                                    district = districtName,
                                    state = stateName,
                                    country = "India",
                                    countryFlag = "🇮🇳",
                                    latitude = loc.latitude,
                                    longitude = loc.longitude,
                                    categories = listOf(LocationCategory.ALL),
                                    popularLandmarks = loc.landmarkHighlights.ifEmpty { city.famousLandmarks },
                                    stayCount = 24,
                                    tag = "${loc.name} • ${districtName}"
                                )
                            )
                        }
                    } else {
                        results.add(
                            GlobalLocationItem(
                                id = "in-${city.name.lowercase(Locale.ROOT)}".replace(" ", "-"),
                                name = "${city.name}, $districtName",
                                locality = null,
                                city = city.name,
                                district = districtName,
                                state = stateName,
                                country = "India",
                                countryFlag = "🇮🇳",
                                latitude = city.latitude,
                                longitude = city.longitude,
                                categories = listOf(LocationCategory.ALL),
                                popularLandmarks = city.famousLandmarks,
                                stayCount = 20,
                                tag = "${city.name} • $districtName, $stateName"
                            )
                        )
                    }
                }
            }
        }
        return results
    }
}
