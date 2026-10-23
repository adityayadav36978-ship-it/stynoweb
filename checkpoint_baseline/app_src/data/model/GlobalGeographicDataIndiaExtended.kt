package com.example.data.model

/**
 * Extended Geographic Dataset for India:
 * Contains remaining North-Eastern states, Central states (Chhattisgarh),
 * and all 8 Union Territories with real districts, cities, and coordinates.
 */
object GlobalGeographicDataIndiaExtended {

    val STATES_AND_UTS: List<GlobalState> = listOf(
        // 1. CHHATTISGARH
        GlobalState(
            code = "CG",
            name = "Chhattisgarh",
            cities = listOf(
                GlobalCity(
                    name = "Raipur",
                    district = "Raipur",
                    latitude = 21.2514,
                    longitude = 81.6296,
                    localities = listOf(
                        GlobalLocality("Telibandha & Marine Drive", 21.2412, 81.6612, listOf("Marine Drive", "Magneto The Mall")),
                        GlobalLocality("Pandri & Shankar Nagar", 21.2589, 81.6489, listOf("City Center", "Cloth Market")),
                        GlobalLocality("NIT & AIIMS Area", 21.2612, 81.6045, listOf("NIT Raipur", "AIIMS Raipur Campus"))
                    ),
                    famousLandmarks = listOf("NIT Raipur", "AIIMS Raipur", "Swami Vivekananda Airport", "Telibandha Talab"),
                    isTopMetro = true
                ),
                GlobalCity(
                    name = "Bhilai",
                    district = "Durg",
                    latitude = 21.2120,
                    longitude = 81.3733,
                    localities = listOf(
                        GlobalLocality("Civic Center & Sector 6", 21.2089, 81.3812, listOf("Bhilai Steel Plant", "Nehru Art Gallery")),
                        GlobalLocality("Nehru Nagar", 21.2245, 81.3589, listOf("Surya Treasure Mall", "Educational Hub"))
                    ),
                    famousLandmarks = listOf("IIT Bhilai", "Bhilai Steel Plant (SAIL)", "Maitri Bagh"),
                    isTopMetro = false
                ),
                GlobalCity(
                    name = "Bilaspur",
                    district = "Bilaspur",
                    latitude = 22.0797,
                    longitude = 82.1409,
                    localities = listOf(
                        GlobalLocality("Vyapar Vihar", 22.0712, 82.1489, listOf("High Court Road", "City Mall 36")),
                        GlobalLocality("Koni & University Campus", 22.1289, 82.1389, listOf("Guru Ghasidas Central University", "GEC Bilaspur"))
                    ),
                    famousLandmarks = listOf("Chhattisgarh High Court", "Guru Ghasidas Central University", "Bilaspur Junction"),
                    isTopMetro = false
                ),
                GlobalCity(
                    name = "Korba",
                    district = "Korba",
                    latitude = 22.3595,
                    longitude = 82.7501,
                    localities = listOf(
                        GlobalLocality("Transport Nagar & TP Nagar", 22.3512, 82.7412, listOf("Commercial Center", "NTPC Township"))
                    ),
                    famousLandmarks = listOf("NTPC Super Thermal Power Station", "Balco Township"),
                    isTopMetro = false
                )
            )
        ),

        // 2. ARUNACHAL PRADESH
        GlobalState(
            code = "AR",
            name = "Arunachal Pradesh",
            cities = listOf(
                GlobalCity(
                    name = "Itanagar",
                    district = "Papum Pare",
                    latitude = 27.0844,
                    longitude = 93.6053,
                    localities = listOf(
                        GlobalLocality("Ganga Market & Secretariat", 27.0912, 93.6123, listOf("State Secretariat", "Raj Bhavan", "Jawaharlal Nehru State Museum")),
                        GlobalLocality("Naharlagun Hub", 27.1089, 93.6989, listOf("Naharlagun Railway Station", "Tomo Riba Hospital"))
                    ),
                    famousLandmarks = listOf("Ita Fort", "Ganga Lake (Gyakar Sinyi)", "NERIST Nirjuli", "Donyi Polo Airport"),
                    isTopMetro = false
                ),
                GlobalCity(
                    name = "Pasighat",
                    district = "East Siang",
                    latitude = 28.0664,
                    longitude = 95.3267,
                    localities = listOf(
                        GlobalLocality("Main Bazaar & Siang View", 28.0689, 95.3289, listOf("Siang River Promenade", "College of Horticulture"))
                    ),
                    famousLandmarks = listOf("Siang River Bridge", "Daying Ering Wildlife Sanctuary"),
                    isTopMetro = false
                ),
                GlobalCity(
                    name = "Tawang",
                    district = "Tawang",
                    latitude = 27.5861,
                    longitude = 91.8594,
                    localities = listOf(
                        GlobalLocality("Old Market & Monastery Road", 27.5889, 91.8612, listOf("Tawang Monastery", "War Memorial"))
                    ),
                    famousLandmarks = listOf("Tawang Monastery", "Sela Pass", "Tawang War Memorial"),
                    isTopMetro = false
                )
            )
        ),

        // 3. MANIPUR
        GlobalState(
            code = "MN",
            name = "Manipur",
            cities = listOf(
                GlobalCity(
                    name = "Imphal",
                    district = "Imphal West",
                    latitude = 24.8170,
                    longitude = 93.9368,
                    localities = listOf(
                        GlobalLocality("Thangal Bazar & Paona Bazar", 24.8089, 93.9389, listOf("Ima Keithel Women Market", "Kangla Fort")),
                        GlobalLocality("Canchipur & University Area", 24.7512, 93.9489, listOf("Manipur University", "RIMS Hospital"))
                    ),
                    famousLandmarks = listOf("Kangla Fort", "Ima Keithel (Mother Market)", "Loktak Lake", "NIT Manipur"),
                    isTopMetro = false
                ),
                GlobalCity(
                    name = "Churachandpur",
                    district = "Churachandpur",
                    latitude = 24.3333,
                    longitude = 93.6667,
                    localities = listOf(
                        GlobalLocality("Tuibong & Tedim Road", 24.3412, 93.6712, listOf("District Headquarters", "Central Market"))
                    ),
                    famousLandmarks = listOf("Khuga Dam", "Tedim Road Bazaar"),
                    isTopMetro = false
                )
            )
        ),

        // 4. MEGHALAYA
        GlobalState(
            code = "ML",
            name = "Meghalaya",
            cities = listOf(
                GlobalCity(
                    name = "Shillong",
                    district = "East Khasi Hills",
                    latitude = 25.5788,
                    longitude = 91.8933,
                    localities = listOf(
                        GlobalLocality("Police Bazar & Commercial Hub", 25.5812, 91.8845, listOf("Centre Point", "Ward's Lake", "Cafes")),
                        GlobalLocality("Laitumkhrah & College Area", 25.5689, 91.8989, listOf("St. Anthony's College", "Cathedral", "Student PGs")),
                        GlobalLocality("Mawlai & NEHU Campus", 25.6112, 91.9012, listOf("North-Eastern Hill University", "IIM Shillong Transit"))
                    ),
                    famousLandmarks = listOf("IIM Shillong", "NEHU Shillong", "Elephant Falls", "Ward's Lake", "Shillong Peak"),
                    isTopMetro = false
                ),
                GlobalCity(
                    name = "Tura",
                    district = "West Garo Hills",
                    latitude = 25.5139,
                    longitude = 90.2033,
                    localities = listOf(
                        GlobalLocality("Hawakhana & Tura Bazaar", 25.5189, 90.2089, listOf("DC Office", "Tura Peak Road"))
                    ),
                    famousLandmarks = listOf("Tura Peak", "Pelga Falls", "Nokrek Biosphere Reserve"),
                    isTopMetro = false
                ),
                GlobalCity(
                    name = "Cherrapunji",
                    district = "East Khasi Hills",
                    latitude = 25.2986,
                    longitude = 91.7303,
                    localities = listOf(
                        GlobalLocality("Sohra Market & Eco Park", 25.2889, 91.7245, listOf("Nohkalikai Viewpoint", "Seven Sisters Falls"))
                    ),
                    famousLandmarks = listOf("Double Decker Living Root Bridge", "Nohkalikai Falls", "Mawsmai Cave"),
                    isTopMetro = false
                )
            )
        ),

        // 5. MIZORAM
        GlobalState(
            code = "MZ",
            name = "Mizoram",
            cities = listOf(
                GlobalCity(
                    name = "Aizawl",
                    district = "Aizawl",
                    latitude = 23.7271,
                    longitude = 92.7176,
                    localities = listOf(
                        GlobalLocality("Bara Bazar & Chanmari", 23.7345, 92.7212, listOf("City Center", "Millennium Centre", "Mizoram University Road")),
                        GlobalLocality("Zarkawt & Khatla", 23.7212, 92.7145, listOf("Secretariat Complex", "Civil Hospital"))
                    ),
                    famousLandmarks = listOf("Mizoram University (MZU)", "Solomon's Temple", "Durtlang Hills", "Lengpui Airport"),
                    isTopMetro = false
                ),
                GlobalCity(
                    name = "Lunglei",
                    district = "Lunglei",
                    latitude = 22.8833,
                    longitude = 92.7333,
                    localities = listOf(
                        GlobalLocality("Venglai & Bazar Veng", 22.8889, 92.7389, listOf("District Headquarters", "Lunglei Bridge"))
                    ),
                    famousLandmarks = listOf("Lunglei Rock Bridge", "Khawnglung Wildlife Sanctuary"),
                    isTopMetro = false
                )
            )
        ),

        // 6. NAGALAND
        GlobalState(
            code = "NL",
            name = "Nagaland",
            cities = listOf(
                GlobalCity(
                    name = "Kohima",
                    district = "Kohima",
                    latitude = 25.6751,
                    longitude = 94.1086,
                    localities = listOf(
                        GlobalLocality("BOC & Main Town", 25.6712, 94.1123, listOf("Kohima War Cemetery", "State Museum")),
                        GlobalLocality("High School Colony & Secretariat", 25.7012, 94.0989, listOf("Nagaland Civil Secretariat", "Kisama Heritage Road"))
                    ),
                    famousLandmarks = listOf("Kohima War Cemetery", "Kisama Heritage Village (Hornbill Festival)", "Dzukou Valley"),
                    isTopMetro = false
                ),
                GlobalCity(
                    name = "Dimapur",
                    district = "Dimapur",
                    latitude = 25.9090,
                    longitude = 93.7265,
                    localities = listOf(
                        GlobalLocality("City Tower & Circular Road", 25.9123, 93.7312, listOf("Hong Kong Market", "Dimapur Railway Station")),
                        GlobalLocality("Chumukedima & 7th Mile", 25.8245, 93.7845, listOf("NIT Nagaland", "Police Training Complex"))
                    ),
                    famousLandmarks = listOf("Kachari Ruins", "Dimapur Airport", "NIT Nagaland Chumukedima"),
                    isTopMetro = false
                )
            )
        ),

        // 7. SIKKIM
        GlobalState(
            code = "SK",
            name = "Sikkim",
            cities = listOf(
                GlobalCity(
                    name = "Gangtok",
                    district = "East Sikkim",
                    latitude = 27.3389,
                    longitude = 88.6065,
                    localities = listOf(
                        GlobalLocality("MG Marg & Lal Bazaar", 27.3312, 88.6145, listOf("Pedestrian Boulevard", "Lal Market", "Kanchendzonga View")),
                        GlobalLocality("Tadong & 5th Mile", 27.3112, 88.5989, listOf("Sikkim University", "Manipal Institute of Medical Sciences SMIMS")),
                        GlobalLocality("Deorali & Namnang", 27.3245, 88.6089, listOf("Ropeway Station", "Do-Drul Chorten"))
                    ),
                    famousLandmarks = listOf("MG Marg Pedestrian Zone", "Sikkim Manipal University (SMU)", "Rumtek Monastery", "Nathula Pass & Tsomgo Lake"),
                    isTopMetro = false
                ),
                GlobalCity(
                    name = "Namchi",
                    district = "South Sikkim",
                    latitude = 27.1667,
                    longitude = 88.3500,
                    localities = listOf(
                        GlobalLocality("Namchi Bazaar & Central Park", 27.1689, 88.3545, listOf("Char Dham Complex", "Samdruptse"))
                    ),
                    famousLandmarks = listOf("Siddhesvara Dhaam (Char Dham)", "Samdruptse Hill Statue", "Tarey Bhir"),
                    isTopMetro = false
                )
            )
        ),

        // 8. TRIPURA
        GlobalState(
            code = "TR",
            name = "Tripura",
            cities = listOf(
                GlobalCity(
                    name = "Agartala",
                    district = "West Tripura",
                    latitude = 23.8315,
                    longitude = 91.2868,
                    localities = listOf(
                        GlobalLocality("Palace Compound & Kaman Chowmuhani", 23.8389, 91.2845, listOf("Ujjayanta Palace", "City Center Mall")),
                        GlobalLocality("College Tilla & Jirania", 23.8412, 91.3123, listOf("NIT Agartala Campus", "Tripura University", "MBB College"))
                    ),
                    famousLandmarks = listOf("Ujjayanta Palace", "NIT Agartala", "Neermahal Water Palace", "MBB Airport Agartala"),
                    isTopMetro = false
                ),
                GlobalCity(
                    name = "Dharmanagar",
                    district = "North Tripura",
                    latitude = 24.3739,
                    longitude = 92.1645,
                    localities = listOf(
                        GlobalLocality("Station Road & Main Bazaar", 24.3789, 92.1689, listOf("Railway Station", "Kalibari"))
                    ),
                    famousLandmarks = listOf("Unakoti Rock Sculptures (Near)", "Dharmanagar Railway Station"),
                    isTopMetro = false
                )
            )
        ),

        // UNION TERRITORIES (All 8)

        // 9. DELHI (NCT of Delhi)
        GlobalState(
            code = "DL",
            name = "Delhi",
            cities = listOf(
                GlobalCity(
                    name = "New Delhi",
                    district = "New Delhi",
                    latitude = 28.6139,
                    longitude = 77.2090,
                    localities = listOf(
                        GlobalLocality("Connaught Place (CP)", 28.6315, 77.2167, listOf("Rajiv Chowk Metro", "Inner Circle", "Janpath")),
                        GlobalLocality("Karol Bagh", 28.6521, 77.1895, listOf("Gaffar Market", "IAS Coaching Hub", "Metro Station")),
                        GlobalLocality("South Extension & AIIMS", 28.5689, 77.2189, listOf("AIIMS Delhi", "Safdarjung Hospital", "M-Block Market")),
                        GlobalLocality("Hauz Khas & IIT Delhi", 28.5494, 77.1926, listOf("IIT Delhi", "Hauz Khas Village", "Deer Park")),
                        GlobalLocality("Mukherjee Nagar & GTB Nagar", 28.7089, 77.2089, listOf("Delhi University North Campus", "UPSC Hub", "Hudson Lane")),
                        GlobalLocality("Dwarka", 28.5921, 77.0460, listOf("Vegas Mall", "NSUT Dwarka", "Sector 21 Metro Interchange")),
                        GlobalLocality("Rohini", 28.7383, 77.0822, listOf("DTU Bawana", "Rithala Metro", "Unity One Mall")),
                        GlobalLocality("Lajpat Nagar & Defence Colony", 28.5689, 77.2412, listOf("Central Market", "Ring Road", "Fine Dining"))
                    ),
                    famousLandmarks = listOf("India Gate", "Red Fort", "Qutub Minar", "AIIMS New Delhi", "IIT Delhi", "Rashtrapati Bhavan"),
                    isTopMetro = true
                )
            )
        ),

        // 10. CHANDIGARH
        GlobalState(
            code = "CH",
            name = "Chandigarh",
            cities = listOf(
                GlobalCity(
                    name = "Chandigarh",
                    district = "Chandigarh",
                    latitude = 30.7333,
                    longitude = 76.7794,
                    localities = listOf(
                        GlobalLocality("Sector 17 & City Centre", 30.7412, 76.7845, listOf("Sector 17 Plaza", "ISBT 17", "Parade Ground")),
                        GlobalLocality("Sector 35 & 22 Commercial", 30.7289, 76.7689, listOf("Hotel Row", "Aroma Complex", "Shops")),
                        GlobalLocality("Panjab University & Sector 14", 30.7589, 76.7689, listOf("Panjab University Campus", "PGIMER Hospital")),
                        GlobalLocality("Sector 43 & IT Park", 30.7189, 76.7412, listOf("ISBT 43", "Rajiv Gandhi IT Park", "Elante Mall Road"))
                    ),
                    famousLandmarks = listOf("Rock Garden of Chandigarh", "Sukhna Lake", "Panjab University (PU)", "PGIMER Chandigarh", "Elante Mall"),
                    isTopMetro = true
                )
            )
        ),

        // 11. LADAKH
        GlobalState(
            code = "LA",
            name = "Ladakh",
            cities = listOf(
                GlobalCity(
                    name = "Leh",
                    district = "Leh",
                    latitude = 34.1526,
                    longitude = 77.5771,
                    localities = listOf(
                        GlobalLocality("Main Bazaar & Fort Road", 34.1645, 77.5845, listOf("Leh Palace", "Central Market", "Homestays")),
                        GlobalLocality("Choglamsar & Airport Road", 34.1289, 77.5989, listOf("Kushok Bakula Rimpochee Airport", "Central Institute of Buddhist Studies"))
                    ),
                    famousLandmarks = listOf("Pangong Tso", "Nubra Valley", "Shanti Stupa", "Thiksey Monastery", "Khardung La Pass"),
                    isTopMetro = false
                ),
                GlobalCity(
                    name = "Kargil",
                    district = "Kargil",
                    latitude = 34.5539,
                    longitude = 76.1349,
                    localities = listOf(
                        GlobalLocality("Kargil Main Town & Suru Valley", 34.5589, 76.1389, listOf("Suru River Front", "Kargil War Memorial Road"))
                    ),
                    famousLandmarks = listOf("Kargil War Memorial (Drass)", "Suru Valley", "Mulbekh Monastery"),
                    isTopMetro = false
                )
            )
        ),

        // 12. JAMMU AND KASHMIR
        GlobalState(
            code = "JK",
            name = "Jammu and Kashmir",
            cities = listOf(
                GlobalCity(
                    name = "Srinagar",
                    district = "Srinagar",
                    latitude = 34.0837,
                    longitude = 74.7973,
                    localities = listOf(
                        GlobalLocality("Dal Lake & Boulevard", 34.0912, 74.8345, listOf("Houseboats", "Shikara Ghats", "Mughal Gardens")),
                        GlobalLocality("Lal Chowk & Residency Road", 34.0712, 74.8112, listOf("Clock Tower", "Kashmir Arts Market")),
                        GlobalLocality("Hazratbal & University Area", 34.1289, 74.8412, listOf("University of Kashmir", "NIT Srinagar", "Hazratbal Dargah"))
                    ),
                    famousLandmarks = listOf("Dal Lake", "Shalimar Bagh", "Hazratbal Shrine", "NIT Srinagar", "Gulmarg (50km)"),
                    isTopMetro = true
                ),
                GlobalCity(
                    name = "Jammu",
                    district = "Jammu",
                    latitude = 32.7266,
                    longitude = 74.8570,
                    localities = listOf(
                        GlobalLocality("Gandhi Nagar & Bahu Plaza", 32.7089, 74.8689, listOf("Commercial Center", "Wave Mall", "Jammu Tawi Station")),
                        GlobalLocality("Katra Foothills", 32.9912, 74.9312, listOf("Mata Vaishno Devi Shrine Yatra Base", "Katra Station"))
                    ),
                    famousLandmarks = listOf("Mata Vaishno Devi Shrine", "Raghunath Temple", "Bahu Fort", "IIT Jammu"),
                    isTopMetro = false
                ),
                GlobalCity(
                    name = "Anantnag",
                    district = "Anantnag",
                    latitude = 33.7311,
                    longitude = 75.1487,
                    localities = listOf(
                        GlobalLocality("Khanabal & Lal Chowk Anantnag", 33.7389, 75.1523, listOf("District Hospital", "Martand Sun Temple Road"))
                    ),
                    famousLandmarks = listOf("Martand Sun Temple", "Pahalgam Gateway", "Amarnath Yatra Route"),
                    isTopMetro = false
                )
            )
        ),

        // 13. PUDUCHERRY
        GlobalState(
            code = "PY",
            name = "Puducherry",
            cities = listOf(
                GlobalCity(
                    name = "Puducherry",
                    district = "Puducherry",
                    latitude = 11.9416,
                    longitude = 79.8083,
                    localities = listOf(
                        GlobalLocality("White Town (French Quarter)", 11.9345, 79.8345, listOf("Promenade Beach", "Sri Aurobindo Ashram", "French Cafes")),
                        GlobalLocality("Heritage Town & Mission Street", 11.9389, 79.8289, listOf("Cathedral", "Goubert Market")),
                        GlobalLocality("Auroville & ECR", 12.0069, 79.8106, listOf("Matrimandir", "Auroville International Township", "JIPMER Campus"))
                    ),
                    famousLandmarks = listOf("Promenade Beach", "Sri Aurobindo Ashram", "Matrimandir Auroville", "JIPMER Puducherry"),
                    isTopMetro = false
                ),
                GlobalCity(
                    name = "Karaikal",
                    district = "Karaikal",
                    latitude = 10.9254,
                    longitude = 79.8380,
                    localities = listOf(
                        GlobalLocality("Beach Road & Town", 10.9289, 79.8412, listOf("Karaikal Port", "Ammaiyar Temple"))
                    ),
                    famousLandmarks = listOf("Karaikal Port", "Karaikal Beach"),
                    isTopMetro = false
                )
            )
        ),

        // 14. ANDAMAN AND NICOBAR ISLANDS
        GlobalState(
            code = "AN",
            name = "Andaman and Nicobar Islands",
            cities = listOf(
                GlobalCity(
                    name = "Port Blair",
                    district = "South Andaman",
                    latitude = 11.6234,
                    longitude = 92.7265,
                    localities = listOf(
                        GlobalLocality("Aberdeen Bazaar & Marina", 11.6689, 92.7412, listOf("Cellular Jail", "Water Sports Complex", "Market")),
                        GlobalLocality("Haddo & Phoenix Bay", 11.6789, 92.7245, listOf("Haddo Jetty", "Inter-Island Ferry"))
                    ),
                    famousLandmarks = listOf("Cellular Jail National Memorial", "Radhanagar Beach (Havelock)", "Ross Island (Netaji Subhash Chandra Bose Dweep)"),
                    isTopMetro = false
                )
            )
        ),

        // 15. DADRA AND NAGAR HAVELI AND DAMAN AND DIU
        GlobalState(
            code = "DN",
            name = "Dadra and Nagar Haveli and Daman and Diu",
            cities = listOf(
                GlobalCity(
                    name = "Daman",
                    district = "Daman",
                    latitude = 20.3974,
                    longitude = 72.8328,
                    localities = listOf(
                        GlobalLocality("Nani Daman & Devka Beach", 20.4189, 72.8345, listOf("Devka Beach", "Hotels Row")),
                        GlobalLocality("Moti Daman & Fort", 20.3989, 72.8289, listOf("Moti Daman Fort", "Lighthouse"))
                    ),
                    famousLandmarks = listOf("Moti Daman Fort", "Devka Beach", "Jampore Beach"),
                    isTopMetro = false
                ),
                GlobalCity(
                    name = "Silvassa",
                    district = "Dadra and Nagar Haveli",
                    latitude = 20.2763,
                    longitude = 73.0083,
                    localities = listOf(
                        GlobalLocality("Silvassa Town & Kilvani", 20.2789, 73.0112, listOf("Tribal Museum", "Vasona Lion Safari"))
                    ),
                    famousLandmarks = listOf("Dudhni Lake", "Tribal Cultural Museum", "Nakshatra Garden"),
                    isTopMetro = false
                )
            )
        ),

        // 16. LAKSHADWEEP
        GlobalState(
            code = "LD",
            name = "Lakshadweep",
            cities = listOf(
                GlobalCity(
                    name = "Kavaratti",
                    district = "Lakshadweep",
                    latitude = 10.5667,
                    longitude = 72.6417,
                    localities = listOf(
                        GlobalLocality("Kavaratti Island Centre", 10.5689, 72.6445, listOf("Marine Aquarium", "Administrative Complex", "Lagoon Jetty"))
                    ),
                    famousLandmarks = listOf("Kavaratti Lagoon", "Urjara Mosque", "Marine Aquarium"),
                    isTopMetro = false
                ),
                GlobalCity(
                    name = "Agatti",
                    district = "Lakshadweep",
                    latitude = 10.8533,
                    longitude = 72.1947,
                    localities = listOf(
                        GlobalLocality("Agatti Airport & Beach", 10.8589, 72.1989, listOf("Agatti Airport Runway", "Coral Reef Lagoon"))
                    ),
                    famousLandmarks = listOf("Agatti Aerodrome", "Coral Reefs & Scuba Diving"),
                    isTopMetro = false
                )
            )
        )
    )
}
