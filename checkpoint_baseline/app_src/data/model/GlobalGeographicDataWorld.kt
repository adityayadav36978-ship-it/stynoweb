package com.example.data.model

/**
 * Comprehensive Geographic Dataset for International Countries & Major Global Regions.
 */
object GlobalGeographicDataWorld {

    val COUNTRIES: List<GlobalCountry> = listOf(
        // 1. UNITED STATES
        GlobalCountry(
            code = "US",
            name = "United States",
            flag = "🇺🇸",
            states = listOf(
                GlobalState(
                    code = "CA",
                    name = "California",
                    cities = listOf(
                        GlobalCity(
                            name = "San Francisco & Bay Area",
                            latitude = 37.7749,
                            longitude = -122.4194,
                            localities = listOf(
                                GlobalLocality("SoMa & Financial District", 37.7879, -122.4012, listOf("Salesforce Tower", "Moscone Center", "Tech HQs")),
                                GlobalLocality("Silicon Valley & Palo Alto", 37.4419, -122.1430, listOf("Stanford University", "University Ave", "Sand Hill Road")),
                                GlobalLocality("Berkeley", 37.8715, -122.2730, listOf("UC Berkeley Campus", "Telegraph Ave", "BART Station")),
                                GlobalLocality("San Jose & Santana Row", 37.3382, -121.8863, listOf("Adobe HQ", "San Jose State University", "SAP Center"))
                            ),
                            famousLandmarks = listOf("Golden Gate Bridge", "Stanford University", "Fisherman's Wharf", "Alcatraz Island", "Lombard Street"),
                            isTopMetro = true
                        ),
                        GlobalCity(
                            name = "Los Angeles",
                            latitude = 34.0522,
                            longitude = -118.2437,
                            localities = listOf(
                                GlobalLocality("Westwood & UCLA", 34.0689, -118.4452, listOf("UCLA Campus", "Westwood Village", "Hammer Museum")),
                                GlobalLocality("Hollywood & West Hollywood", 34.0928, -118.3287, listOf("Walk of Fame", "Sunset Strip", "Dolby Theatre")),
                                GlobalLocality("Santa Monica & Venice", 34.0195, -118.4912, listOf("Santa Monica Pier", "Venice Boardwalk", "Abbot Kinney")),
                                GlobalLocality("Downtown LA (DTLA)", 34.0407, -118.2468, listOf("Crypto.com Arena", "The Broad Museum", "USC Campus"))
                            ),
                            famousLandmarks = listOf("Hollywood Sign", "Santa Monica Pier", "Griffith Observatory", "UCLA Campus", "Universal Studios"),
                            isTopMetro = true
                        )
                    )
                ),
                GlobalState(
                    code = "NY",
                    name = "New York",
                    cities = listOf(
                        GlobalCity(
                            name = "New York City",
                            latitude = 40.7128,
                            longitude = -74.0060,
                            localities = listOf(
                                GlobalLocality("Manhattan (Midtown & Times Square)", 40.7580, -73.9855, listOf("Times Square", "Empire State Building", "Rockefeller Center", "Grand Central")),
                                GlobalLocality("Manhattan (Downtown & Financial District)", 40.7075, -74.0090, listOf("Wall Street", "One World Trade Center", "Battery Park", "NYU Campus")),
                                GlobalLocality("Manhattan (Upper West Side & Morningside)", 40.8075, -73.9626, listOf("Columbia University", "Central Park West", "Lincoln Center")),
                                GlobalLocality("Brooklyn (Williamsburg & DUMBO)", 40.7081, -73.9571, listOf("Brooklyn Bridge Park", "Bedford Ave", "Waterfront Cafes")),
                                GlobalLocality("Queens (Long Island City & Astoria)", 40.7447, -73.9485, listOf("Gantry Plaza", "MoMA PS1", "Queensboro Bridge"))
                            ),
                            famousLandmarks = listOf("Statue of Liberty", "Empire State Building", "Times Square & Broadway", "Central Park", "Brooklyn Bridge", "Columbia University", "NYU"),
                            isTopMetro = true
                        )
                    )
                ),
                GlobalState(
                    code = "TX",
                    name = "Texas",
                    cities = listOf(
                        GlobalCity(
                            name = "Austin",
                            latitude = 30.2672,
                            longitude = -97.7431,
                            localities = listOf(
                                GlobalLocality("Downtown & UT Austin", 30.2849, -97.7341, listOf("UT Austin Campus", "State Capitol", "6th Street Entertainment")),
                                GlobalLocality("South Congress (SoCo)", 30.2489, -97.7501, listOf("Boutiques", "Food Trucks", "Live Music Venues")),
                                GlobalLocality("Domain & North Tech Corridor", 30.4018, -97.7245, listOf("The Domain", "Apple Campus", "IBM Tech Hub"))
                            ),
                            famousLandmarks = listOf("Texas State Capitol", "University of Texas at Austin", "Barton Springs Pool", "Lady Bird Lake"),
                            isTopMetro = true
                        ),
                        GlobalCity(
                            name = "Dallas & Houston",
                            latitude = 32.7767,
                            longitude = -96.7970,
                            localities = listOf(
                                GlobalLocality("Downtown Dallas & Uptown", 32.7845, -96.8012, listOf("Arts District", "Klyde Warren Park", "American Airlines Center")),
                                GlobalLocality("Houston Medical Center & Rice", 29.7174, -95.4018, listOf("Texas Medical Center", "Rice University", "Museum District"))
                            ),
                            famousLandmarks = listOf("NASA Space Center Houston", "Reunion Tower Dallas", "Rice University", "Dallas Museum of Art"),
                            isTopMetro = true
                        )
                    )
                ),
                GlobalState(
                    code = "WA",
                    name = "Washington",
                    cities = listOf(
                        GlobalCity(
                            name = "Seattle & Bellevue",
                            latitude = 47.6062,
                            longitude = -122.3321,
                            localities = listOf(
                                GlobalLocality("Downtown & Pike Place", 47.6089, -122.3401, listOf("Pike Place Market", "Space Needle", "Amazon Spheres HQ")),
                                GlobalLocality("University District (UW)", 47.6553, -122.3035, listOf("University of Washington", "The Ave", "Husky Stadium")),
                                GlobalLocality("Downtown Bellevue & Redmond", 47.6101, -122.2015, listOf("Bellevue Square", "Microsoft Redmond Campus", "Transit Center"))
                            ),
                            famousLandmarks = listOf("Space Needle", "Pike Place Market", "University of Washington", "Mount Rainier", "Museum of Pop Culture"),
                            isTopMetro = true
                        )
                    )
                ),
                GlobalState(
                    code = "MA",
                    name = "Massachusetts",
                    cities = listOf(
                        GlobalCity(
                            name = "Boston & Cambridge",
                            latitude = 42.3601,
                            longitude = -71.0589,
                            localities = listOf(
                                GlobalLocality("Cambridge (Harvard & MIT)", 42.3736, -71.1097, listOf("Harvard Yard", "MIT Campus & Kendall Sq", "Harvard Square", "Central Square")),
                                GlobalLocality("Back Bay & Fenway", 42.3496, -71.0825, listOf("Northeastern University", "Fenway Park", "Newbury Street", "Boston University")),
                                GlobalLocality("Downtown & Seaport", 42.3522, -71.0450, listOf("Boston Common", "Faneuil Hall", "Innovation District"))
                            ),
                            famousLandmarks = listOf("Harvard University", "Massachusetts Institute of Technology (MIT)", "Fenway Park", "Freedom Trail", "Boston Common"),
                            isTopMetro = true
                        )
                    )
                )
            )
        ),

        // 2. UNITED KINGDOM
        GlobalCountry(
            code = "GB",
            name = "United Kingdom",
            flag = "🇬🇧",
            states = listOf(
                GlobalState(
                    code = "ENG",
                    name = "England",
                    cities = listOf(
                        GlobalCity(
                            name = "London",
                            latitude = 51.5074,
                            longitude = -0.1278,
                            localities = listOf(
                                GlobalLocality("Central London & Westminster", 51.4994, -0.1272, listOf("Big Ben", "Trafalgar Square", "London School of Economics (LSE)", "King's College London")),
                                GlobalLocality("Bloomsbury & King's Cross", 51.5246, -0.1340, listOf("University College London (UCL)", "British Museum", "St Pancras International", "Google UK")),
                                GlobalLocality("South Kensington & Chelsea", 51.4988, -0.1749, listOf("Imperial College London", "Natural History Museum", "Royal Albert Hall")),
                                GlobalLocality("Shoreditch & City of London", 51.5229, -0.0777, listOf("Old Street Silicon Roundabout", "Bank Station", "Boutique Stays")),
                                GlobalLocality("Canary Wharf", 51.5054, -0.0235, listOf("Financial Towers", "Elizabeth Line Station", "Crossrail Place"))
                            ),
                            famousLandmarks = listOf("Big Ben & Parliament", "Tower Bridge & Tower of London", "London Eye", "Buckingham Palace", "Imperial College London", "UCL"),
                            isTopMetro = true
                        ),
                        GlobalCity(
                            name = "Oxford & Cambridge",
                            latitude = 51.7520,
                            longitude = -1.2577,
                            localities = listOf(
                                GlobalLocality("Oxford City Centre & Colleges", 51.7534, -1.2589, listOf("University of Oxford", "Bodleian Library", "Radcliffe Camera", "Christ Church")),
                                GlobalLocality("Cambridge Centre & Silicon Fen", 52.2053, 0.1218, listOf("University of Cambridge", "King's College Chapel", "River Cam Punting", "Cambridge Science Park"))
                            ),
                            famousLandmarks = listOf("University of Oxford", "University of Cambridge", "Bodleian Library", "King's College Chapel"),
                            isTopMetro = false
                        ),
                        GlobalCity(
                            name = "Manchester & Birmingham",
                            latitude = 53.4808,
                            longitude = -2.2426,
                            localities = listOf(
                                GlobalLocality("Manchester City Centre & Oxford Rd", 53.4668, -2.2339, listOf("University of Manchester", "Piccadilly Station", "MediaCityUK")),
                                GlobalLocality("Birmingham City Centre & Edgbaston", 52.4862, -1.8904, listOf("University of Birmingham", "New Street Station", "Bullring Mall"))
                            ),
                            famousLandmarks = listOf("Old Trafford & Etihad Stadium", "University of Manchester", "Bullring Birmingham", "MediaCityUK"),
                            isTopMetro = true
                        )
                    )
                ),
                GlobalState(
                    code = "SCT",
                    name = "Scotland",
                    cities = listOf(
                        GlobalCity(
                            name = "Edinburgh & Glasgow",
                            latitude = 55.9533,
                            longitude = -3.1883,
                            localities = listOf(
                                GlobalLocality("Edinburgh Old Town & University", 55.9486, -3.1890, listOf("University of Edinburgh", "Royal Mile", "Edinburgh Castle", "Princes Street")),
                                GlobalLocality("Glasgow City Centre & West End", 55.8724, -4.2890, listOf("University of Glasgow", "Kelvingrove Park", "Buchanan Street"))
                            ),
                            famousLandmarks = listOf("Edinburgh Castle", "University of Edinburgh", "University of Glasgow", "Royal Mile", "Arthur's Seat"),
                            isTopMetro = false
                        )
                    )
                )
            )
        ),

        // 3. UNITED ARAB EMIRATES
        GlobalCountry(
            code = "AE",
            name = "United Arab Emirates",
            flag = "🇦🇪",
            states = listOf(
                GlobalState(
                    code = "DXB",
                    name = "Dubai",
                    cities = listOf(
                        GlobalCity(
                            name = "Dubai",
                            latitude = 25.2048,
                            longitude = 55.2708,
                            localities = listOf(
                                GlobalLocality("Downtown Dubai", 25.1972, 55.2744, listOf("Burj Khalifa", "Dubai Mall", "Dubai Fountain", "Dubai Opera", "Burj Khalifa Metro")),
                                GlobalLocality("Dubai Marina & JBR", 25.0819, 55.1367, listOf("The Walk JBR", "Marina Mall", "Marina Promenade", "DMCC Metro")),
                                GlobalLocality("Business Bay", 25.1837, 55.2666, listOf("Dubai Water Canal", "Executive Towers", "Business Bay Metro")),
                                GlobalLocality("Deira & Bur Dubai", 25.2697, 55.3095, listOf("Gold Souk", "Al Fahidi Historical Neighbourhood", "Dubai Frame", "Union Metro")),
                                GlobalLocality("Dubai Silicon Oasis & Academic City", 25.1228, 55.3789, listOf("BITS Pilani Dubai", "Heriot-Watt University", "Manipal Dubai", "Silicon Park")),
                                GlobalLocality("Al Barsha & Mall of the Emirates", 25.1189, 55.2012, listOf("Mall of the Emirates", "Mashreq Metro", "Ski Dubai"))
                            ),
                            famousLandmarks = listOf("Burj Khalifa (World's Tallest Tower)", "The Dubai Mall", "Palm Jumeirah & Atlantis", "Dubai Marina", "Museum of the Future", "Dubai Frame"),
                            isTopMetro = true
                        )
                    )
                ),
                GlobalState(
                    code = "AUH",
                    name = "Abu Dhabi",
                    cities = listOf(
                        GlobalCity(
                            name = "Abu Dhabi",
                            latitude = 24.4539,
                            longitude = 54.3773,
                            localities = listOf(
                                GlobalLocality("Corniche & Al Khalidiyah", 24.4712, 54.3456, listOf("Abu Dhabi Corniche", "Marina Mall", "Emirates Palace")),
                                GlobalLocality("Al Reem Island & Maryah Island", 24.4989, 54.4012, listOf("Sorbonne University Abu Dhabi", "Cleveland Clinic", "Galleria Mall")),
                                GlobalLocality("Yas Island & Saadiyat Island", 24.4891, 54.6012, listOf("Ferrari World", "Yas Marina Circuit", "Louvre Abu Dhabi", "NYU Abu Dhabi"))
                            ),
                            famousLandmarks = listOf("Sheikh Zayed Grand Mosque", "Louvre Abu Dhabi", "Ferrari World Abu Dhabi", "Emirates Palace", "NYU Abu Dhabi"),
                            isTopMetro = true
                        )
                    )
                ),
                GlobalState(
                    code = "SHJ",
                    name = "Sharjah & Northern Emirates",
                    cities = listOf(
                        GlobalCity(
                            name = "Sharjah",
                            latitude = 25.3463,
                            longitude = 55.4209,
                            localities = listOf(
                                GlobalLocality("University City Sharjah", 25.2989, 55.4812, listOf("American University of Sharjah (AUS)", "University of Sharjah")),
                                GlobalLocality("Al Majaz & Al Qasba", 25.3245, 55.3845, listOf("Sharjah Waterfront", "Eye of the Emirates", "Corniche"))
                            ),
                            famousLandmarks = listOf("Sharjah Art Museum", "Al Noor Mosque", "University City of Sharjah", "Al Majaz Waterfront"),
                            isTopMetro = false
                        )
                    )
                )
            )
        ),

        // 4. CANADA
        GlobalCountry(
            code = "CA",
            name = "Canada",
            flag = "🇨🇦",
            states = listOf(
                GlobalState(
                    code = "ON",
                    name = "Ontario",
                    cities = listOf(
                        GlobalCity(
                            name = "Toronto & GTA",
                            latitude = 43.6532,
                            longitude = -79.3832,
                            localities = listOf(
                                GlobalLocality("Downtown Toronto & U of T", 43.6629, -79.3957, listOf("University of Toronto (U of T)", "Eaton Centre", "Dundas Square", "Union Station")),
                                GlobalLocality("Financial & Entertainment District", 43.6481, -79.3871, listOf("CN Tower", "Scotiabank Arena", "King St West", "PATH Network")),
                                GlobalLocality("Mississauga & Brampton", 43.5890, -79.6441, listOf("Square One Shopping Centre", "UTM Campus", "Sheridan College")),
                                GlobalLocality("Waterloo & Kitchener", 43.4643, -80.5204, listOf("University of Waterloo", "Wilfrid Laurier University", "Google Waterloo"))
                            ),
                            famousLandmarks = listOf("CN Tower", "Ripley's Aquarium of Canada", "Royal Ontario Museum (ROM)", "University of Toronto", "Niagara Falls (Near)"),
                            isTopMetro = true
                        ),
                        GlobalCity(
                            name = "Ottawa",
                            latitude = 45.4215,
                            longitude = -75.6972,
                            localities = listOf(
                                GlobalLocality("Downtown & ByWard Market", 45.4275, -75.6924, listOf("Parliament Hill", "University of Ottawa", "Rideau Centre"))
                            ),
                            famousLandmarks = listOf("Parliament Hill", "Rideau Canal UNESCO", "University of Ottawa", "National Gallery of Canada"),
                            isTopMetro = false
                        )
                    )
                ),
                GlobalState(
                    code = "BC",
                    name = "British Columbia",
                    cities = listOf(
                        GlobalCity(
                            name = "Vancouver & Metro Vancouver",
                            latitude = 49.2827,
                            longitude = -123.1207,
                            localities = listOf(
                                GlobalLocality("Downtown Vancouver & Yaletown", 49.2789, -123.1212, listOf("Robson Street", "Waterfront SkyTrain", "Gastown Clock")),
                                GlobalLocality("Point Grey (UBC Campus)", 49.2606, -123.2460, listOf("University of British Columbia (UBC)", "Wreck Beach", "AMS Nest")),
                                GlobalLocality("Burnaby & Metrotown", 49.2276, -123.0076, listOf("Simon Fraser University (SFU)", "Metropolis at Metrotown", "SkyTrain"))
                            ),
                            famousLandmarks = listOf("Stanley Park & Seawall", "Capilano Suspension Bridge", "University of British Columbia (UBC)", "Granville Island"),
                            isTopMetro = true
                        )
                    )
                ),
                GlobalState(
                    code = "QC",
                    name = "Quebec",
                    cities = listOf(
                        GlobalCity(
                            name = "Montreal",
                            latitude = 45.5017,
                            longitude = -73.5673,
                            localities = listOf(
                                GlobalLocality("Downtown & McGill University", 45.5048, -73.5772, listOf("McGill University Campus", "Sainte-Catherine Street", "Place des Arts")),
                                GlobalLocality("Plateau-Mont-Royal & Mile End", 45.5234, -73.5845, listOf("Mount Royal Park", "Boutique Cafes", "Student Apartments")),
                                GlobalLocality("Old Montreal (Vieux-Montréal)", 45.5074, -73.5542, listOf("Notre-Dame Basilica", "Old Port Promenade", "Cobblestone Streets"))
                            ),
                            famousLandmarks = listOf("Notre-Dame Basilica of Montreal", "Mount Royal Park", "McGill University", "Old Port of Montreal"),
                            isTopMetro = true
                        )
                    )
                )
            )
        ),

        // 5. AUSTRALIA
        GlobalCountry(
            code = "AU",
            name = "Australia",
            flag = "🇦🇺",
            states = listOf(
                GlobalState(
                    code = "NSW",
                    name = "New South Wales",
                    cities = listOf(
                        GlobalCity(
                            name = "Sydney",
                            latitude = -33.8688,
                            longitude = 151.2093,
                            localities = listOf(
                                GlobalLocality("Sydney CBD & Circular Quay", -33.8615, 151.2108, listOf("Sydney Opera House", "Sydney Harbour Bridge", "Town Hall Station", "Martin Place")),
                                GlobalLocality("Camperdown & Darlinghurst (USYD)", -33.8886, 151.1873, listOf("University of Sydney (USYD)", "Broadway Shopping Centre", "Newtown King St")),
                                GlobalLocality("Kensington & Randwick (UNSW)", -33.9173, 151.2313, listOf("UNSW Sydney Campus", "Light Rail Stop", "Coogee Beach Road")),
                                GlobalLocality("Parramatta & Western Sydney", -33.8150, 151.0011, listOf("Western Sydney University", "Westfield Parramatta", "Parramatta Square"))
                            ),
                            famousLandmarks = listOf("Sydney Opera House", "Sydney Harbour Bridge", "Bondi Beach", "University of Sydney", "Darling Harbour"),
                            isTopMetro = true
                        )
                    )
                ),
                GlobalState(
                    code = "VIC",
                    name = "Victoria",
                    cities = listOf(
                        GlobalCity(
                            name = "Melbourne",
                            latitude = -37.8136,
                            longitude = 144.9631,
                            localities = listOf(
                                GlobalLocality("Melbourne CBD & Carlton", -37.7963, 144.9614, listOf("University of Melbourne", "RMIT University", "Flinders Street Station", "Swanston St Tram")),
                                GlobalLocality("Clayton & Caulfield (Monash)", -37.9105, 145.1362, listOf("Monash University Clayton Campus", "Monash Tech Precinct")),
                                GlobalLocality("Southbank & Docklands", -37.8228, 144.9654, listOf("Crown Casino", "Eureka Skydeck", "Yarra River Walk", "Marvel Stadium"))
                            ),
                            famousLandmarks = listOf("Flinders Street Railway Station", "Federation Square", "University of Melbourne", "Royal Botanic Gardens", "Melbourne Cricket Ground (MCG)"),
                            isTopMetro = true
                        )
                    )
                ),
                GlobalState(
                    code = "QLD",
                    name = "Queensland",
                    cities = listOf(
                        GlobalCity(
                            name = "Brisbane & Gold Coast",
                            latitude = -27.4698,
                            longitude = 153.0251,
                            localities = listOf(
                                GlobalLocality("Brisbane CBD & South Bank", -27.4764, 153.0189, listOf("Queensland University of Technology (QUT)", "South Bank Parklands")),
                                GlobalLocality("St Lucia (UQ Campus)", -27.4975, 153.0137, listOf("University of Queensland (UQ)", "CityCat Ferry Wharf", "Great Court"))
                            ),
                            famousLandmarks = listOf("South Bank Parklands", "University of Queensland (UQ)", "Story Bridge", "Surfers Paradise Gold Coast"),
                            isTopMetro = true
                        )
                    )
                )
            )
        ),

        // 6. SINGAPORE
        GlobalCountry(
            code = "SG",
            name = "Singapore",
            flag = "🇸🇬",
            states = listOf(
                GlobalState(
                    code = "SG-MAIN",
                    name = "Singapore",
                    cities = listOf(
                        GlobalCity(
                            name = "Singapore",
                            latitude = 1.3521,
                            longitude = 103.8198,
                            localities = listOf(
                                GlobalLocality("Marina Bay & Downtown Core", 1.2838, 103.8591, listOf("Marina Bay Sands", "Gardens by the Bay", "Raffles Place MRT", "SMU Campus")),
                                GlobalLocality("Orchard & Dhoby Ghaut", 1.3048, 103.8318, listOf("ION Orchard", "Dhoby Ghaut MRT Interchange", "Somerset", "Student Stays")),
                                GlobalLocality("Kent Ridge & Buona Vista (NUS & One-North)", 1.2966, 103.7764, listOf("National University of Singapore (NUS)", "One-North Tech Park", "INSEAD Singapore", "Fusionopolis")),
                                GlobalLocality("Jurong West (NTU Campus)", 1.3483, 103.6831, listOf("Nanyang Technological University (NTU)", "The Hive", "Jurong Innovation District")),
                                GlobalLocality("Bugis & Rochor", 1.3008, 103.8560, listOf("Bugis Junction", "LASALLE College of the Arts", "NAFA", "Arab Street & Haji Lane"))
                            ),
                            famousLandmarks = listOf("Marina Bay Sands & SkyPark", "Gardens by the Bay & Supertrees", "Jewel Changi Airport", "National University of Singapore (NUS)", "Sentosa Island", "Universal Studios Singapore"),
                            isTopMetro = true
                        )
                    )
                )
            )
        ),

        // 7. GERMANY
        GlobalCountry(
            code = "DE",
            name = "Germany",
            flag = "🇩🇪",
            states = listOf(
                GlobalState(
                    code = "BY",
                    name = "Bavaria",
                    cities = listOf(
                        GlobalCity(
                            name = "Munich",
                            latitude = 48.1351,
                            longitude = 11.5820,
                            localities = listOf(
                                GlobalLocality("Maxvorstadt & Schwabing (LMU & TUM)", 48.1509, 11.5802, listOf("TUM Main Campus", "LMU Munich", "Odeonsplatz", "Englischer Garten")),
                                GlobalLocality("Garching (TUM Science Campus)", 48.2625, 11.6678, listOf("TUM Garching Campus", "Max Planck Institute", "U-Bahn Garching-Forschungszentrum")),
                                GlobalLocality("Altstadt & Marienplatz", 48.1372, 11.5755, listOf("Marienplatz", "Frauenkirche", "Munich Central Station (Hbf)"))
                            ),
                            famousLandmarks = listOf("Marienplatz & Neues Rathaus", "Englischer Garten", "Technical University of Munich (TUM)", "Nymphenburg Palace", "Allianz Arena"),
                            isTopMetro = true
                        )
                    )
                ),
                GlobalState(
                    code = "BE",
                    name = "Berlin",
                    cities = listOf(
                        GlobalCity(
                            name = "Berlin",
                            latitude = 52.5200,
                            longitude = 13.4050,
                            localities = listOf(
                                GlobalLocality("Mitte & Museum Island", 52.5186, 13.3934, listOf("Humboldt University of Berlin", "Brandenburg Gate", "Alexanderplatz", "Unter den Linden")),
                                GlobalLocality("Charlottenburg (TU Berlin)", 52.5125, 13.3269, listOf("Technical University of Berlin (TU Berlin)", "Zoologischer Garten", "Kurfürstendamm")),
                                GlobalLocality("Kreuzberg & Friedrichshain", 52.4989, 13.4012, listOf("East Side Gallery", "Startup Hubs", "Cafes & Tech Workspaces"))
                            ),
                            famousLandmarks = listOf("Brandenburg Gate", "Reichstag Building", "Museum Island UNESCO", "Berlin Wall Memorial", "Alexanderplatz TV Tower"),
                            isTopMetro = true
                        )
                    )
                ),
                GlobalState(
                    code = "HE",
                    name = "Hesse",
                    cities = listOf(
                        GlobalCity(
                            name = "Frankfurt",
                            latitude = 50.1109,
                            longitude = 8.6821,
                            localities = listOf(
                                GlobalLocality("Innenstadt & Bankenviertel", 50.1145, 8.6734, listOf("European Central Bank", "Frankfurt Stock Exchange", "Main Tower")),
                                GlobalLocality("Westend & Bockenheim (Goethe)", 50.1267, 8.6656, listOf("Goethe University Frankfurt", "Palmengarten", "Bockenheimer Warte"))
                            ),
                            famousLandmarks = listOf("Römerberg", "Main Tower & Financial District", "Goethe University Frankfurt", "Frankfurt Airport"),
                            isTopMetro = true
                        )
                    )
                )
            )
        ),

        // 8. FRANCE
        GlobalCountry(
            code = "FR",
            name = "France",
            flag = "🇫🇷",
            states = listOf(
                GlobalState(
                    code = "IDF",
                    name = "Île-de-France",
                    cities = listOf(
                        GlobalCity(
                            name = "Paris",
                            latitude = 48.8566,
                            longitude = 2.3522,
                            localities = listOf(
                                GlobalLocality("Latin Quarter & 5th Arrondissement", 48.8489, 2.3444, listOf("Sorbonne University", "Panthéon", "Jardin du Luxembourg", "Boulevard Saint-Michel")),
                                GlobalLocality("7th & 8th Arrondissement (Eiffel & Champs)", 48.8584, 2.2945, listOf("Eiffel Tower", "Champs-Élysées", "Arc de Triomphe", "Sciences Po")),
                                GlobalLocality("1st & 4th Arrondissement (Louvre & Le Marais)", 48.8606, 2.3376, listOf("Louvre Museum", "Notre-Dame Cathedral", "Centre Pompidou")),
                                GlobalLocality("Plateau de Saclay (IP Paris & Paris-Saclay)", 48.7112, 2.1645, listOf("École Polytechnique", "Université Paris-Saclay", "HEC Paris Campus"))
                            ),
                            famousLandmarks = listOf("Eiffel Tower", "Louvre Museum", "Notre-Dame Cathedral", "Arc de Triomphe", "Sorbonne University", "Sacré-Cœur Basilica"),
                            isTopMetro = true
                        )
                    )
                ),
                GlobalState(
                    code = "ARA",
                    name = "Auvergne-Rhône-Alpes",
                    cities = listOf(
                        GlobalCity(
                            name = "Lyon",
                            latitude = 45.7640,
                            longitude = 4.8357,
                            localities = listOf(
                                GlobalLocality("Presqu'île & Part-Dieu", 45.7589, 4.8512, listOf("Place Bellecour", "Part-Dieu TGV Station", "Shopping Centre")),
                                GlobalLocality("Villeurbanne (INSA & Lyon 1)", 45.7812, 4.8789, listOf("Université Claude Bernard Lyon 1", "INSA Lyon Campus", "Parc de la Tête d'Or"))
                            ),
                            famousLandmarks = listOf("Basilique Notre-Dame de Fourvière", "Vieux Lyon UNESCO", "Place Bellecour", "INSA Lyon"),
                            isTopMetro = false
                        )
                    )
                )
            )
        ),

        // 9. JAPAN
        GlobalCountry(
            code = "JP",
            name = "Japan",
            flag = "🇯🇵",
            states = listOf(
                GlobalState(
                    code = "13",
                    name = "Tokyo",
                    cities = listOf(
                        GlobalCity(
                            name = "Tokyo",
                            latitude = 35.6762,
                            longitude = 139.6503,
                            localities = listOf(
                                GlobalLocality("Shinjuku & Shibuya", 35.6895, 139.7004, listOf("Shibuya Scramble Crossing", "Shinjuku Station (World's Busiest)", "Hachiko Statue", "Meiji Shrine")),
                                GlobalLocality("Bunkyo (University of Tokyo / Todai)", 35.7126, 139.7619, listOf("University of Tokyo Hongo Campus", "Akamon Gate", "Tokyo Dome")),
                                GlobalLocality("Chiyoda & Akihabara", 35.6983, 139.7731, listOf("Electric Town", "Tokyo Central Station", "Imperial Palace East Gardens")),
                                GlobalLocality("Minato & Roppongi", 35.6586, 139.7454, listOf("Tokyo Tower", "Roppongi Hills", "Keio University Mita Campus", "Akasaka"))
                            ),
                            famousLandmarks = listOf("Tokyo Tower", "Shibuya Crossing", "Senso-ji Temple", "The University of Tokyo (Todai)", "Tokyo Skytree", "Meiji Jingu Shrine"),
                            isTopMetro = true
                        )
                    )
                ),
                GlobalState(
                    code = "27",
                    name = "Osaka",
                    cities = listOf(
                        GlobalCity(
                            name = "Osaka",
                            latitude = 34.6937,
                            longitude = 135.5023,
                            localities = listOf(
                                GlobalLocality("Umeda & Kita", 34.7024, 135.4959, listOf("Osaka Station City", "Umeda Sky Building", "Grand Front Osaka")),
                                GlobalLocality("Namba & Dotonbori (Minami)", 34.6687, 135.5013, listOf("Glico Running Man Sign", "Dotonbori Canal", "Namba Parks", "Kuromon Market")),
                                GlobalLocality("Suita (Osaka University)", 34.8212, 135.5245, listOf("Osaka University Suita Campus", "Expo '70 Commemorative Park"))
                            ),
                            famousLandmarks = listOf("Osaka Castle", "Dotonbori", "Universal Studios Japan (USJ)", "Osaka University", "Umeda Sky Building"),
                            isTopMetro = true
                        )
                    )
                ),
                GlobalState(
                    code = "26",
                    name = "Kyoto",
                    cities = listOf(
                        GlobalCity(
                            name = "Kyoto",
                            latitude = 35.0116,
                            longitude = 135.7681,
                            localities = listOf(
                                GlobalLocality("Sakyo (Kyoto University)", 35.0262, 135.7808, listOf("Kyoto University Yoshida Campus", "Philosopher's Path", "Ginkaku-ji")),
                                GlobalLocality("Gion & Higashiyama", 35.0037, 135.7772, listOf("Kiyomizu-dera Temple", "Yasaka Shrine", "Traditional Machiya Houses"))
                            ),
                            famousLandmarks = listOf("Fushimi Inari-taisha Shrine", "Kinkaku-ji (Golden Pavilion)", "Kiyomizu-dera Temple", "Kyoto University", "Arashiyama Bamboo Grove"),
                            isTopMetro = false
                        )
                    )
                )
            )
        ),

        // 10. SAUDI ARABIA
        GlobalCountry(
            code = "SA",
            name = "Saudi Arabia",
            flag = "🇸🇦",
            states = listOf(
                GlobalState(
                    code = "RUH",
                    name = "Riyadh Region",
                    cities = listOf(
                        GlobalCity(
                            name = "Riyadh",
                            latitude = 24.7136,
                            longitude = 46.6753,
                            localities = listOf(
                                GlobalLocality("King Abdullah Financial District (KAFD)", 24.7645, 46.6412, listOf("KAFD Towers", "KAFD Metro Hub", "Corporate HQs")),
                                GlobalLocality("Al Olaya & King Fahd Rd", 24.6989, 46.6845, listOf("Kingdom Centre Tower", "Al Faisaliah Tower", "King Saud University"))
                            ),
                            famousLandmarks = listOf("Kingdom Centre Sky Bridge", "King Abdullah Financial District (KAFD)", "Masmak Fortress", "King Saud University"),
                            isTopMetro = true
                        )
                    )
                ),
                GlobalState(
                    code = "MKK",
                    name = "Makkah Region",
                    cities = listOf(
                        GlobalCity(
                            name = "Jeddah & Mecca",
                            latitude = 21.5433,
                            longitude = 39.1728,
                            localities = listOf(
                                GlobalLocality("Jeddah Corniche & Al Balad", 21.5212, 39.1645, listOf("King Fahd's Fountain", "Red Sea Mall", "Historic Al Balad")),
                                GlobalLocality("Kaust (Thuwal)", 22.3089, 39.1045, listOf("KAUST University Campus", "Innovation Center"))
                            ),
                            famousLandmarks = listOf("King Fahd's Fountain Jeddah", "Historic Al-Balad UNESCO", "KAUST Campus", "Jeddah Corniche"),
                            isTopMetro = true
                        )
                    )
                )
            )
        ),

        // 11. QATAR
        GlobalCountry(
            code = "QA",
            name = "Qatar",
            flag = "🇶🇦",
            states = listOf(
                GlobalState(
                    code = "DOH",
                    name = "Doha & Al Rayyan",
                    cities = listOf(
                        GlobalCity(
                            name = "Doha",
                            latitude = 25.2854,
                            longitude = 51.5310,
                            localities = listOf(
                                GlobalLocality("Education City (Al Rayyan)", 25.3145, 51.4412, listOf("Qatar Foundation", "Carnegie Mellon Qatar", "Texas A&M Qatar", "Education City Stadium")),
                                GlobalLocality("West Bay & Corniche", 25.3212, 51.5345, listOf("City Center Doha", "Financial Towers", "Doha Metro")),
                                GlobalLocality("The Pearl-Qatar & Lusail", 25.3712, 51.5489, listOf("Porto Arabia", "Lusail Marina Promenade", "Lusail Iconic Stadium"))
                            ),
                            famousLandmarks = listOf("Museum of Islamic Art", "Souq Waqif", "The Pearl-Qatar", "Education City", "Lusail Stadium"),
                            isTopMetro = true
                        )
                    )
                )
            )
        )
    )
}
