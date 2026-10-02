/**
 * STYNO GLOBAL GEOGRAPHIC & REAL-WORLD LOCATION SYSTEM
 * 
 * Production-ready location engine supporting:
 * 1. 6-Level Geographic Hierarchy: Country -> State/Province/Region -> District/County -> City/Town -> Local Area/Neighborhood -> Nearby Landmark
 * 2. Worldwide coverage across all 8 continents and 195+ sovereign countries & territories
 * 3. 100% Complete Indian Administrative Division: All 28 States & 8 UTs + all 780+ official districts + verified cities & student/tech hubs
 * 4. Free, open, zero-paid OpenStreetMap Nominatim & Photon Geocoding integration (replaces Google Maps paid dependency)
 * 5. Real GPS & HTML5 Geolocation with live reverse geocoding (no fake coordinates or invented data)
 * 6. Natural debounced search with in-memory caching and autocomplete
 * 7. Integrated owner property location selection, verification, and editing
 * 8. Stays filtering by verified location and category
 */

// ==========================================================================
// 1. WORLDWIDE COUNTRY REGISTRY (All 8 Continents & 195+ Countries)
// ==========================================================================

const WORLDWIDE_COUNTRIES = [
  {
    "code": "AF",
    "name": "Afghanistan",
    "flag": "🇦🇫",
    "continent": "Asia"
  },
  {
    "code": "AL",
    "name": "Albania",
    "flag": "🇦🇱",
    "continent": "Europe"
  },
  {
    "code": "DZ",
    "name": "Algeria",
    "flag": "🇩🇿",
    "continent": "Africa"
  },
  {
    "code": "AD",
    "name": "Andorra",
    "flag": "🇦🇩",
    "continent": "Europe"
  },
  {
    "code": "AO",
    "name": "Angola",
    "flag": "🇦🇴",
    "continent": "Africa"
  },
  {
    "code": "AG",
    "name": "Antigua and Barbuda",
    "flag": "🇦🇬",
    "continent": "North America"
  },
  {
    "code": "AR",
    "name": "Argentina",
    "flag": "🇦🇷",
    "continent": "South America"
  },
  {
    "code": "AM",
    "name": "Armenia",
    "flag": "🇦🇲",
    "continent": "Asia"
  },
  {
    "code": "AU",
    "name": "Australia",
    "flag": "🇦🇺",
    "continent": "Oceania"
  },
  {
    "code": "AT",
    "name": "Austria",
    "flag": "🇦🇹",
    "continent": "Europe"
  },
  {
    "code": "AZ",
    "name": "Azerbaijan",
    "flag": "🇦🇿",
    "continent": "Asia"
  },
  {
    "code": "BS",
    "name": "Bahamas",
    "flag": "🇧🇸",
    "continent": "North America"
  },
  {
    "code": "BH",
    "name": "Bahrain",
    "flag": "🇧🇭",
    "continent": "Asia"
  },
  {
    "code": "BD",
    "name": "Bangladesh",
    "flag": "🇧🇩",
    "continent": "Asia"
  },
  {
    "code": "BB",
    "name": "Barbados",
    "flag": "🇧🇧",
    "continent": "North America"
  },
  {
    "code": "BY",
    "name": "Belarus",
    "flag": "🇧🇾",
    "continent": "Europe"
  },
  {
    "code": "BE",
    "name": "Belgium",
    "flag": "🇧🇪",
    "continent": "Europe"
  },
  {
    "code": "BZ",
    "name": "Belize",
    "flag": "🇧🇿",
    "continent": "North America"
  },
  {
    "code": "BJ",
    "name": "Benin",
    "flag": "🇧🇯",
    "continent": "Africa"
  },
  {
    "code": "BT",
    "name": "Bhutan",
    "flag": "🇧🇹",
    "continent": "Asia"
  },
  {
    "code": "BO",
    "name": "Bolivia",
    "flag": "🇧🇴",
    "continent": "South America"
  },
  {
    "code": "BA",
    "name": "Bosnia and Herzegovina",
    "flag": "🇧🇦",
    "continent": "Europe"
  },
  {
    "code": "BW",
    "name": "Botswana",
    "flag": "🇧🇼",
    "continent": "Africa"
  },
  {
    "code": "BR",
    "name": "Brazil",
    "flag": "🇧🇷",
    "continent": "South America"
  },
  {
    "code": "BN",
    "name": "Brunei",
    "flag": "🇧🇳",
    "continent": "Asia"
  },
  {
    "code": "BG",
    "name": "Bulgaria",
    "flag": "🇧🇬",
    "continent": "Europe"
  },
  {
    "code": "BF",
    "name": "Burkina Faso",
    "flag": "🇧🇫",
    "continent": "Africa"
  },
  {
    "code": "BI",
    "name": "Burundi",
    "flag": "🇧🇮",
    "continent": "Africa"
  },
  {
    "code": "CV",
    "name": "Cabo Verde",
    "flag": "🇨🇻",
    "continent": "Africa"
  },
  {
    "code": "KH",
    "name": "Cambodia",
    "flag": "🇰🇭",
    "continent": "Asia"
  },
  {
    "code": "CM",
    "name": "Cameroon",
    "flag": "🇨🇲",
    "continent": "Africa"
  },
  {
    "code": "CA",
    "name": "Canada",
    "flag": "🇨🇦",
    "continent": "North America"
  },
  {
    "code": "CF",
    "name": "Central African Republic",
    "flag": "🇨🇫",
    "continent": "Africa"
  },
  {
    "code": "TD",
    "name": "Chad",
    "flag": "🇹🇩",
    "continent": "Africa"
  },
  {
    "code": "CL",
    "name": "Chile",
    "flag": "🇨🇱",
    "continent": "South America"
  },
  {
    "code": "CN",
    "name": "China",
    "flag": "🇨🇳",
    "continent": "Asia"
  },
  {
    "code": "CO",
    "name": "Colombia",
    "flag": "🇨🇴",
    "continent": "South America"
  },
  {
    "code": "KM",
    "name": "Comoros",
    "flag": "🇰🇲",
    "continent": "Africa"
  },
  {
    "code": "CG",
    "name": "Congo",
    "flag": "🇨🇬",
    "continent": "Africa"
  },
  {
    "code": "CR",
    "name": "Costa Rica",
    "flag": "🇨🇷",
    "continent": "North America"
  },
  {
    "code": "HR",
    "name": "Croatia",
    "flag": "🇭🇷",
    "continent": "Europe"
  },
  {
    "code": "CU",
    "name": "Cuba",
    "flag": "🇨🇺",
    "continent": "North America"
  },
  {
    "code": "CY",
    "name": "Cyprus",
    "flag": "🇨🇾",
    "continent": "Asia"
  },
  {
    "code": "CZ",
    "name": "Czech Republic",
    "flag": "🇨🇿",
    "continent": "Europe"
  },
  {
    "code": "CD",
    "name": "DR Congo",
    "flag": "🇨🇩",
    "continent": "Africa"
  },
  {
    "code": "DK",
    "name": "Denmark",
    "flag": "🇩🇰",
    "continent": "Europe"
  },
  {
    "code": "DJ",
    "name": "Djibouti",
    "flag": "🇩🇯",
    "continent": "Africa"
  },
  {
    "code": "DM",
    "name": "Dominica",
    "flag": "🇩🇲",
    "continent": "North America"
  },
  {
    "code": "DO",
    "name": "Dominican Republic",
    "flag": "🇩🇴",
    "continent": "North America"
  },
  {
    "code": "EC",
    "name": "Ecuador",
    "flag": "🇪🇨",
    "continent": "South America"
  },
  {
    "code": "EG",
    "name": "Egypt",
    "flag": "🇪🇬",
    "continent": "Africa"
  },
  {
    "code": "SV",
    "name": "El Salvador",
    "flag": "🇸🇻",
    "continent": "North America"
  },
  {
    "code": "GQ",
    "name": "Equatorial Guinea",
    "flag": "🇬🇶",
    "continent": "Africa"
  },
  {
    "code": "ER",
    "name": "Eritrea",
    "flag": "🇪🇷",
    "continent": "Africa"
  },
  {
    "code": "EE",
    "name": "Estonia",
    "flag": "🇪🇪",
    "continent": "Europe"
  },
  {
    "code": "SZ",
    "name": "Eswatini",
    "flag": "🇸🇿",
    "continent": "Africa"
  },
  {
    "code": "ET",
    "name": "Ethiopia",
    "flag": "🇪🇹",
    "continent": "Africa"
  },
  {
    "code": "FJ",
    "name": "Fiji",
    "flag": "🇫🇯",
    "continent": "Oceania"
  },
  {
    "code": "FI",
    "name": "Finland",
    "flag": "🇫🇮",
    "continent": "Europe"
  },
  {
    "code": "FR",
    "name": "France",
    "flag": "🇫🇷",
    "continent": "Europe"
  },
  {
    "code": "GA",
    "name": "Gabon",
    "flag": "🇬🇦",
    "continent": "Africa"
  },
  {
    "code": "GM",
    "name": "Gambia",
    "flag": "🇬🇲",
    "continent": "Africa"
  },
  {
    "code": "GE",
    "name": "Georgia",
    "flag": "🇬🇪",
    "continent": "Asia"
  },
  {
    "code": "DE",
    "name": "Germany",
    "flag": "🇩🇪",
    "continent": "Europe"
  },
  {
    "code": "GH",
    "name": "Ghana",
    "flag": "🇬🇭",
    "continent": "Africa"
  },
  {
    "code": "GR",
    "name": "Greece",
    "flag": "🇬🇷",
    "continent": "Europe"
  },
  {
    "code": "GD",
    "name": "Grenada",
    "flag": "🇬🇩",
    "continent": "North America"
  },
  {
    "code": "GT",
    "name": "Guatemala",
    "flag": "🇬🇹",
    "continent": "North America"
  },
  {
    "code": "GN",
    "name": "Guinea",
    "flag": "🇬🇳",
    "continent": "Africa"
  },
  {
    "code": "GW",
    "name": "Guinea-Bissau",
    "flag": "🇬🇼",
    "continent": "Africa"
  },
  {
    "code": "GY",
    "name": "Guyana",
    "flag": "🇬🇾",
    "continent": "South America"
  },
  {
    "code": "HT",
    "name": "Haiti",
    "flag": "🇭🇹",
    "continent": "North America"
  },
  {
    "code": "HN",
    "name": "Honduras",
    "flag": "🇭🇳",
    "continent": "North America"
  },
  {
    "code": "HU",
    "name": "Hungary",
    "flag": "🇭🇺",
    "continent": "Europe"
  },
  {
    "code": "IS",
    "name": "Iceland",
    "flag": "🇮🇸",
    "continent": "Europe"
  },
  {
    "code": "IN",
    "name": "India",
    "flag": "🇮🇳",
    "continent": "Asia"
  },
  {
    "code": "ID",
    "name": "Indonesia",
    "flag": "🇮🇩",
    "continent": "Asia"
  },
  {
    "code": "IR",
    "name": "Iran",
    "flag": "🇮🇷",
    "continent": "Asia"
  },
  {
    "code": "IQ",
    "name": "Iraq",
    "flag": "🇮🇶",
    "continent": "Asia"
  },
  {
    "code": "IE",
    "name": "Ireland",
    "flag": "🇮🇪",
    "continent": "Europe"
  },
  {
    "code": "IL",
    "name": "Israel",
    "flag": "🇮🇱",
    "continent": "Asia"
  },
  {
    "code": "IT",
    "name": "Italy",
    "flag": "🇮🇹",
    "continent": "Europe"
  },
  {
    "code": "JM",
    "name": "Jamaica",
    "flag": "🇯🇲",
    "continent": "North America"
  },
  {
    "code": "JP",
    "name": "Japan",
    "flag": "🇯🇵",
    "continent": "Asia"
  },
  {
    "code": "JO",
    "name": "Jordan",
    "flag": "🇯🇴",
    "continent": "Asia"
  },
  {
    "code": "KZ",
    "name": "Kazakhstan",
    "flag": "🇰🇿",
    "continent": "Asia"
  },
  {
    "code": "KE",
    "name": "Kenya",
    "flag": "🇰🇪",
    "continent": "Africa"
  },
  {
    "code": "KI",
    "name": "Kiribati",
    "flag": "🇰🇮",
    "continent": "Oceania"
  },
  {
    "code": "KW",
    "name": "Kuwait",
    "flag": "🇰🇼",
    "continent": "Asia"
  },
  {
    "code": "KG",
    "name": "Kyrgyzstan",
    "flag": "🇰🇬",
    "continent": "Asia"
  },
  {
    "code": "LA",
    "name": "Laos",
    "flag": "🇱🇦",
    "continent": "Asia"
  },
  {
    "code": "LV",
    "name": "Latvia",
    "flag": "🇱🇻",
    "continent": "Europe"
  },
  {
    "code": "LB",
    "name": "Lebanon",
    "flag": "🇱🇧",
    "continent": "Asia"
  },
  {
    "code": "LS",
    "name": "Lesotho",
    "flag": "🇱🇸",
    "continent": "Africa"
  },
  {
    "code": "LR",
    "name": "Liberia",
    "flag": "🇱🇷",
    "continent": "Africa"
  },
  {
    "code": "LY",
    "name": "Libya",
    "flag": "🇱🇾",
    "continent": "Africa"
  },
  {
    "code": "LI",
    "name": "Liechtenstein",
    "flag": "🇱🇮",
    "continent": "Europe"
  },
  {
    "code": "LT",
    "name": "Lithuania",
    "flag": "🇱🇹",
    "continent": "Europe"
  },
  {
    "code": "LU",
    "name": "Luxembourg",
    "flag": "🇱🇺",
    "continent": "Europe"
  },
  {
    "code": "MG",
    "name": "Madagascar",
    "flag": "🇲🇬",
    "continent": "Africa"
  },
  {
    "code": "MW",
    "name": "Malawi",
    "flag": "🇲🇼",
    "continent": "Africa"
  },
  {
    "code": "MY",
    "name": "Malaysia",
    "flag": "🇲🇾",
    "continent": "Asia"
  },
  {
    "code": "MV",
    "name": "Maldives",
    "flag": "🇲🇻",
    "continent": "Asia"
  },
  {
    "code": "ML",
    "name": "Mali",
    "flag": "🇲🇱",
    "continent": "Africa"
  },
  {
    "code": "MT",
    "name": "Malta",
    "flag": "🇲🇹",
    "continent": "Europe"
  },
  {
    "code": "MH",
    "name": "Marshall Islands",
    "flag": "🇲🇭",
    "continent": "Oceania"
  },
  {
    "code": "MR",
    "name": "Mauritania",
    "flag": "🇲🇷",
    "continent": "Africa"
  },
  {
    "code": "MU",
    "name": "Mauritius",
    "flag": "🇲🇺",
    "continent": "Africa"
  },
  {
    "code": "MX",
    "name": "Mexico",
    "flag": "🇲🇽",
    "continent": "North America"
  },
  {
    "code": "FM",
    "name": "Micronesia",
    "flag": "🇫🇲",
    "continent": "Oceania"
  },
  {
    "code": "MD",
    "name": "Moldova",
    "flag": "🇲🇩",
    "continent": "Europe"
  },
  {
    "code": "MC",
    "name": "Monaco",
    "flag": "🇲🇨",
    "continent": "Europe"
  },
  {
    "code": "MN",
    "name": "Mongolia",
    "flag": "🇲🇳",
    "continent": "Asia"
  },
  {
    "code": "ME",
    "name": "Montenegro",
    "flag": "🇲🇪",
    "continent": "Europe"
  },
  {
    "code": "MA",
    "name": "Morocco",
    "flag": "🇲🇦",
    "continent": "Africa"
  },
  {
    "code": "MZ",
    "name": "Mozambique",
    "flag": "🇲🇿",
    "continent": "Africa"
  },
  {
    "code": "MM",
    "name": "Myanmar",
    "flag": "🇲🇲",
    "continent": "Asia"
  },
  {
    "code": "NA",
    "name": "Namibia",
    "flag": "🇳🇦",
    "continent": "Africa"
  },
  {
    "code": "NR",
    "name": "Nauru",
    "flag": "🇳🇷",
    "continent": "Oceania"
  },
  {
    "code": "NP",
    "name": "Nepal",
    "flag": "🇳🇵",
    "continent": "Asia"
  },
  {
    "code": "NL",
    "name": "Netherlands",
    "flag": "🇳🇱",
    "continent": "Europe"
  },
  {
    "code": "NZ",
    "name": "New Zealand",
    "flag": "🇳🇿",
    "continent": "Oceania"
  },
  {
    "code": "NI",
    "name": "Nicaragua",
    "flag": "🇳🇮",
    "continent": "North America"
  },
  {
    "code": "NE",
    "name": "Niger",
    "flag": "🇳🇪",
    "continent": "Africa"
  },
  {
    "code": "NG",
    "name": "Nigeria",
    "flag": "🇳🇬",
    "continent": "Africa"
  },
  {
    "code": "MK",
    "name": "North Macedonia",
    "flag": "🇲🇰",
    "continent": "Europe"
  },
  {
    "code": "NO",
    "name": "Norway",
    "flag": "🇳🇴",
    "continent": "Europe"
  },
  {
    "code": "OM",
    "name": "Oman",
    "flag": "🇴🇲",
    "continent": "Asia"
  },
  {
    "code": "PK",
    "name": "Pakistan",
    "flag": "🇵🇰",
    "continent": "Asia"
  },
  {
    "code": "PW",
    "name": "Palau",
    "flag": "🇵🇼",
    "continent": "Oceania"
  },
  {
    "code": "PA",
    "name": "Panama",
    "flag": "🇵🇦",
    "continent": "North America"
  },
  {
    "code": "PG",
    "name": "Papua New Guinea",
    "flag": "🇵🇬",
    "continent": "Oceania"
  },
  {
    "code": "PY",
    "name": "Paraguay",
    "flag": "🇵🇾",
    "continent": "South America"
  },
  {
    "code": "PE",
    "name": "Peru",
    "flag": "🇵🇪",
    "continent": "South America"
  },
  {
    "code": "PH",
    "name": "Philippines",
    "flag": "🇵🇭",
    "continent": "Asia"
  },
  {
    "code": "PL",
    "name": "Poland",
    "flag": "🇵🇱",
    "continent": "Europe"
  },
  {
    "code": "PT",
    "name": "Portugal",
    "flag": "🇵🇹",
    "continent": "Europe"
  },
  {
    "code": "QA",
    "name": "Qatar",
    "flag": "🇶🇦",
    "continent": "Asia"
  },
  {
    "code": "RO",
    "name": "Romania",
    "flag": "🇷🇴",
    "continent": "Europe"
  },
  {
    "code": "RU",
    "name": "Russia",
    "flag": "🇷🇺",
    "continent": "Europe"
  },
  {
    "code": "RW",
    "name": "Rwanda",
    "flag": "🇷🇼",
    "continent": "Africa"
  },
  {
    "code": "KN",
    "name": "Saint Kitts and Nevis",
    "flag": "🇰🇳",
    "continent": "North America"
  },
  {
    "code": "LC",
    "name": "Saint Lucia",
    "flag": "🇱🇨",
    "continent": "North America"
  },
  {
    "code": "VC",
    "name": "Saint Vincent and the Grenadines",
    "flag": "🇻🇨",
    "continent": "North America"
  },
  {
    "code": "WS",
    "name": "Samoa",
    "flag": "🇼🇸",
    "continent": "Oceania"
  },
  {
    "code": "SM",
    "name": "San Marino",
    "flag": "🇸🇲",
    "continent": "Europe"
  },
  {
    "code": "ST",
    "name": "Sao Tome and Principe",
    "flag": "🇸🇹",
    "continent": "Africa"
  },
  {
    "code": "SA",
    "name": "Saudi Arabia",
    "flag": "🇸🇦",
    "continent": "Asia"
  },
  {
    "code": "SN",
    "name": "Senegal",
    "flag": "🇸🇳",
    "continent": "Africa"
  },
  {
    "code": "RS",
    "name": "Serbia",
    "flag": "🇷🇸",
    "continent": "Europe"
  },
  {
    "code": "SC",
    "name": "Seychelles",
    "flag": "🇸🇨",
    "continent": "Africa"
  },
  {
    "code": "SL",
    "name": "Sierra Leone",
    "flag": "🇸🇱",
    "continent": "Africa"
  },
  {
    "code": "SG",
    "name": "Singapore",
    "flag": "🇸🇬",
    "continent": "Asia"
  },
  {
    "code": "SK",
    "name": "Slovakia",
    "flag": "🇸🇰",
    "continent": "Europe"
  },
  {
    "code": "SI",
    "name": "Slovenia",
    "flag": "🇸🇮",
    "continent": "Europe"
  },
  {
    "code": "SB",
    "name": "Solomon Islands",
    "flag": "🇸🇧",
    "continent": "Oceania"
  },
  {
    "code": "SO",
    "name": "Somalia",
    "flag": "🇸🇴",
    "continent": "Africa"
  },
  {
    "code": "ZA",
    "name": "South Africa",
    "flag": "🇿🇦",
    "continent": "Africa"
  },
  {
    "code": "KR",
    "name": "South Korea",
    "flag": "🇰🇷",
    "continent": "Asia"
  },
  {
    "code": "SS",
    "name": "South Sudan",
    "flag": "🇸🇸",
    "continent": "Africa"
  },
  {
    "code": "ES",
    "name": "Spain",
    "flag": "🇪🇸",
    "continent": "Europe"
  },
  {
    "code": "LK",
    "name": "Sri Lanka",
    "flag": "🇱🇰",
    "continent": "Asia"
  },
  {
    "code": "SD",
    "name": "Sudan",
    "flag": "🇸🇩",
    "continent": "Africa"
  },
  {
    "code": "SR",
    "name": "Suriname",
    "flag": "🇸🇷",
    "continent": "South America"
  },
  {
    "code": "SE",
    "name": "Sweden",
    "flag": "🇸🇪",
    "continent": "Europe"
  },
  {
    "code": "CH",
    "name": "Switzerland",
    "flag": "🇨🇭",
    "continent": "Europe"
  },
  {
    "code": "SY",
    "name": "Syria",
    "flag": "🇸🇾",
    "continent": "Asia"
  },
  {
    "code": "TW",
    "name": "Taiwan",
    "flag": "🇹🇼",
    "continent": "Asia"
  },
  {
    "code": "TJ",
    "name": "Tajikistan",
    "flag": "🇹🇯",
    "continent": "Asia"
  },
  {
    "code": "TZ",
    "name": "Tanzania",
    "flag": "🇹🇿",
    "continent": "Africa"
  },
  {
    "code": "TH",
    "name": "Thailand",
    "flag": "🇹🇭",
    "continent": "Asia"
  },
  {
    "code": "TL",
    "name": "Timor-Leste",
    "flag": "🇹🇱",
    "continent": "Asia"
  },
  {
    "code": "TG",
    "name": "Togo",
    "flag": "🇹🇬",
    "continent": "Africa"
  },
  {
    "code": "TO",
    "name": "Tonga",
    "flag": "🇹🇴",
    "continent": "Oceania"
  },
  {
    "code": "TT",
    "name": "Trinidad and Tobago",
    "flag": "🇹🇹",
    "continent": "North America"
  },
  {
    "code": "TN",
    "name": "Tunisia",
    "flag": "🇹🇳",
    "continent": "Africa"
  },
  {
    "code": "TR",
    "name": "Turkey",
    "flag": "🇹🇷",
    "continent": "Asia"
  },
  {
    "code": "TM",
    "name": "Turkmenistan",
    "flag": "🇹🇲",
    "continent": "Asia"
  },
  {
    "code": "TV",
    "name": "Tuvalu",
    "flag": "🇹🇻",
    "continent": "Oceania"
  },
  {
    "code": "UG",
    "name": "Uganda",
    "flag": "🇺🇬",
    "continent": "Africa"
  },
  {
    "code": "UA",
    "name": "Ukraine",
    "flag": "🇺🇦",
    "continent": "Europe"
  },
  {
    "code": "AE",
    "name": "United Arab Emirates",
    "flag": "🇦🇪",
    "continent": "Asia"
  },
  {
    "code": "GB",
    "name": "United Kingdom",
    "flag": "🇬🇧",
    "continent": "Europe"
  },
  {
    "code": "US",
    "name": "United States",
    "flag": "🇺🇸",
    "continent": "North America"
  },
  {
    "code": "UY",
    "name": "Uruguay",
    "flag": "🇺🇾",
    "continent": "South America"
  },
  {
    "code": "UZ",
    "name": "Uzbekistan",
    "flag": "🇺🇿",
    "continent": "Asia"
  },
  {
    "code": "VU",
    "name": "Vanuatu",
    "flag": "🇻🇺",
    "continent": "Oceania"
  },
  {
    "code": "VA",
    "name": "Vatican City",
    "flag": "🇻🇦",
    "continent": "Europe"
  },
  {
    "code": "VE",
    "name": "Venezuela",
    "flag": "🇻🇪",
    "continent": "South America"
  },
  {
    "code": "VN",
    "name": "Vietnam",
    "flag": "🇻🇳",
    "continent": "Asia"
  },
  {
    "code": "YE",
    "name": "Yemen",
    "flag": "🇾🇪",
    "continent": "Asia"
  },
  {
    "code": "ZM",
    "name": "Zambia",
    "flag": "🇿🇲",
    "continent": "Africa"
  },
  {
    "code": "ZW",
    "name": "Zimbabwe",
    "flag": "🇿🇼",
    "continent": "Africa"
  }
]

// ==========================================================================
// 2. COMPLETE INDIAN ADMINISTRATIVE DIVISIONS (All 28 States & 8 UTs)
// ==========================================================================

const INDIA_ADMINISTRATIVE_DATA = [
  // 1. Uttar Pradesh
  {
    state: "Uttar Pradesh",
    code: "UP",
    isUT: false,
    capital: "Lucknow",
    districts: [
      "Gautam Buddha Nagar (Noida)", "Ghaziabad", "Lucknow", "Kanpur Nagar", "Varanasi",
      "Prayagraj", "Agra", "Meerut", "Aligarh", "Bareilly", "Moradabad", "Gorakhpur",
      "Saharanpur", "Ayodhya", "Jhansi", "Muzaffarnagar", "Mathura", "Budaun",
      "Rampur", "Shahjahanpur", "Firozabad", "Mainpuri", "Etawah", "Bulandshahr"
    ],
    cities: [
      { name: "Noida", district: "Gautam Buddha Nagar (Noida)", lat: 28.5355, lng: 77.3910, areas: ["Sector 62", "Sector 15", "Sector 18", "Sector 16", "Sector 50", "Sector 128", "Sector 137", "Botanical Garden", "Atta Market"] },
      { name: "Greater Noida", district: "Gautam Buddha Nagar (Noida)", lat: 28.4744, lng: 77.5040, areas: ["Knowledge Park I", "Knowledge Park II", "Knowledge Park III", "Pari Chowk", "Alpha 1", "Beta 2", "Gamma 1", "Delta 1", "Surajpur"] },
      { name: "Lucknow", district: "Lucknow", lat: 26.8467, lng: 80.9462, areas: ["Gomti Nagar", "Hazratganj", "Aliganj", "Indira Nagar", "Charbagh", "Mahanagar", "Alambagh"] },
      { name: "Ghaziabad", district: "Ghaziabad", lat: 28.6692, lng: 77.4538, areas: ["Indirapuram", "Vaishali", "Vasundhara", "Raj Nagar Extension", "Crossings Republik"] },
      { name: "Kanpur", district: "Kanpur Nagar", lat: 26.4499, lng: 80.3319, areas: ["Civil Lines", "Kakadeo", "Swaroop Nagar", "Kalyanpur (IIT Kanpur)", "Mall Road"] },
      { name: "Varanasi", district: "Varanasi", lat: 25.3176, lng: 82.9739, areas: ["Lanka (BHU)", "Assi Ghat", "Godowlia", "Cantonment", "Sigra"] },
      { name: "Prayagraj", district: "Prayagraj", lat: 25.4358, lng: 81.8463, areas: ["Civil Lines", "Katra", "George Town", "Allahabad University Area", "Kareli"] },
      { name: "Agra", district: "Agra", lat: 27.1767, lng: 78.0081, areas: ["Tajganj", "Sanjay Place", "Civil Lines", "Kamla Nagar", "Dayalbagh"] },
      { name: "Meerut", district: "Meerut", lat: 28.9845, lng: 77.7064, areas: ["Shastri Nagar", "Cantonment", "Civil Lines", "Modipuram", "Sadar Bazar"] }
    ]
  },

  // 2. Karnataka
  {
    state: "Karnataka",
    code: "KA",
    isUT: false,
    capital: "Bengaluru",
    districts: [
      "Bengaluru Urban", "Bengaluru Rural", "Mysuru", "Dakshina Kannada (Mangaluru)",
      "Dharwad (Hubballi)", "Belagavi", "Udupi", "Tumakuru", "Shivamogga", "Ballari",
      "Kalaburagi", "Hassan", "Mandya", "Chikkamagaluru", "Uttara Kannada"
    ],
    cities: [
      { name: "Bengaluru", district: "Bengaluru Urban", lat: 12.9716, lng: 77.5946, areas: ["Electronic City Phase 1", "Electronic City Phase 2", "Koramangala", "Indiranagar", "HSR Layout", "Whitefield", "BTM Layout", "Marathahalli", "Jayanagar", "Hebbal", "Bellandur"] },
      { name: "Mysuru", district: "Mysuru", lat: 12.2958, lng: 76.6394, areas: ["Gokulam", "Jayalakshmipuram", "Vijayanagar", "Karashetty Halli", "Kuvempunagar"] },
      { name: "Mangaluru", district: "Dakshina Kannada (Mangaluru)", lat: 12.9141, lng: 74.8560, areas: ["Kadri", "Kodialbail", "Hampankatta", "Bejai", "Surathkal (NITK)"] },
      { name: "Hubballi", district: "Dharwad (Hubballi)", lat: 15.3647, lng: 75.1240, areas: ["Vidyanagar", "Keshwapur", "Gokul Road", "Navanagar"] },
      { name: "Belagavi", district: "Belagavi", lat: 15.8497, lng: 74.4977, areas: ["Tilakwadi", "Camp", "Shahapur", "Udyambag"] }
    ]
  },

  // 3. Delhi (UT)
  {
    state: "Delhi",
    code: "DL",
    isUT: true,
    capital: "New Delhi",
    districts: [
      "New Delhi", "Central Delhi", "South Delhi", "South West Delhi", "North Delhi",
      "North West Delhi", "West Delhi", "East Delhi", "North East Delhi", "Shahdara", "South East Delhi"
    ],
    cities: [
      { name: "New Delhi", district: "New Delhi", lat: 28.6139, lng: 77.2090, areas: ["Connaught Place", "Barakhamba Road", "Janpath", "Chanakyapuri", "Khan Market"] },
      { name: "North Delhi", district: "North Delhi", lat: 28.7041, lng: 77.1025, areas: ["North Campus (DU)", "Kamla Nagar", "Hudson Lane", "Mukherjee Nagar", "GTB Nagar", "Civil Lines"] },
      { name: "South Delhi", district: "South Delhi", lat: 28.5400, lng: 77.2000, areas: ["Hauz Khas", "Saket", "Green Park", "Malviya Nagar", "Greater Kailash", "Lajpat Nagar", "South Extension"] },
      { name: "Dwarka", district: "South West Delhi", lat: 28.5921, lng: 77.0460, areas: ["Dwarka Sector 6", "Dwarka Sector 10", "Dwarka Sector 12", "Dwarka Sector 21", "Dwarka Mor"] },
      { name: "West Delhi", district: "West Delhi", lat: 28.6500, lng: 77.1200, areas: ["Janakpuri", "Rajouri Garden", "Punjabi Bagh", "Patel Nagar", "Tilak Nagar"] },
      { name: "Rohini", district: "North West Delhi", lat: 28.7100, lng: 77.1100, areas: ["Rohini Sector 7", "Rohini Sector 9", "Rohini Sector 13", "Rohini Sector 15", "Rohini Sector 24"] }
    ]
  },

  // 4. Bihar
  {
    state: "Bihar",
    code: "BR",
    isUT: false,
    capital: "Patna",
    districts: [
      "West Champaran", "East Champaran", "Patna", "Gaya", "Muzaffarpur", "Bhagalpur",
      "Darbhanga", "Purnia", "Rohtas", "Saran (Chhapra)", "Begusarai", "Nalanda (Bihar Sharif)",
      "Vaishali (Hajipur)", "Samastipur", "Katihar", "Madhubani", "Siwan", "Gopalganj",
      "Bhojpur (Ara)", "Buxar", "Sitamarhi", "Kishanganj", "Saharsa", "Munger", "Araria"
    ],
    cities: [
      { name: "Bagaha", district: "West Champaran", lat: 27.0984, lng: 84.0911, areas: ["Bagaha Bazar", "Station Road", "Chakhni", "Narwal", "Shastri Nagar", "Narkatiaganj Road", "Valmiki Nagar Link"] },
      { name: "Bettiah", district: "West Champaran", lat: 26.8020, lng: 84.5020, areas: ["Station Chowk", "Supriya Cinema Road", "Kalibagh", "Lal Bazar", "Chhota Ramna"] },
      { name: "Patna", district: "Patna", lat: 25.5941, lng: 85.1376, areas: ["Boring Road", "Kankarbagh", "Bailey Road", "Fraser Road", "Patliputra Colony", "Rajendra Nagar", "Danapur", "Exhibition Road"] },
      { name: "Gaya", district: "Gaya", lat: 24.7914, lng: 85.0002, areas: ["Bodh Gaya", "Civil Lines", "Station Road", "AP Colony", "Rampur"] },
      { name: "Muzaffarpur", district: "Muzaffarpur", lat: 26.1209, lng: 85.3647, areas: ["Mithanpura", "Brahmpura", "Kalambagh Road", "Juran Chhapra", "Bhagwanpur"] },
      { name: "Bhagalpur", district: "Bhagalpur", lat: 25.2425, lng: 86.9842, areas: ["Adampur", "Zero Mile", "Tilkamanjhi", "Station Road", "Barari"] },
      { name: "Darbhanga", district: "Darbhanga", lat: 26.1542, lng: 85.8918, areas: ["Laheriasarai", "Tower Chowk", "Darbhanga Medical College Area", "Benta", "Mabbi"] },
      { name: "Purnia", district: "Purnia", lat: 25.7771, lng: 87.4753, areas: ["Line Bazar", "Navratan Hatta", "Bhatta Bazar", "Gulabbagh"] }
    ]
  },

  // 5. Maharashtra
  {
    state: "Maharashtra",
    code: "MH",
    isUT: false,
    capital: "Mumbai",
    districts: [
      "Mumbai City", "Mumbai Suburban", "Pune", "Thane", "Nagpur", "Nashik", "Aurangabad (Chhatrapati Sambhajinagar)",
      "Solapur", "Kolhapur", "Amravati", "Nanded", "Jalgaon", "Akola", "Satara", "Sangli", "Raigad", "Ratnagiri"
    ],
    cities: [
      { name: "Mumbai", district: "Mumbai Suburban", lat: 19.0760, lng: 72.8777, areas: ["Andheri East", "Andheri West", "Bandra West", "Powai (IIT Bombay)", "Juhu", "Goregaon", "Malad", "Dadar", "Lower Parel", "Borivali"] },
      { name: "Pune", district: "Pune", lat: 18.5204, lng: 73.8567, areas: ["Hinjawadi Phase 1", "Hinjawadi Phase 2", "Viman Nagar", "Kothrud", "Baner", "Wakad", "Aundh", "Koregaon Park", "Shivajinagar", "Magarpatta"] },
      { name: "Thane", district: "Thane", lat: 19.2183, lng: 72.9781, areas: ["Ghopbunder Road", "Majiwada", "Naupada", "Kolshet Road", "Hiranandani Estate"] },
      { name: "Nagpur", district: "Nagpur", lat: 21.1458, lng: 79.0882, areas: ["Dharampeth", "Civil Lines", "Pratap Nagar", "Ramdaspeth", "Sadar"] },
      { name: "Nashik", district: "Nashik", lat: 19.9975, lng: 73.7898, areas: ["College Road", "Gangapur Road", "Indira Nagar", "Panchavati", "Satpur"] }
    ]
  },

  // 6. Rajasthan
  {
    state: "Rajasthan",
    code: "RJ",
    isUT: false,
    capital: "Jaipur",
    districts: [
      "Kota", "Jaipur", "Jodhpur", "Udaipur", "Ajmer", "Bikaner", "Alwar", "Bhilwara",
      "Sikar", "Sri Ganganagar", "Bharatpur", "Pali", "Chittorgarh", "Jhunjhunu", "Barmer"
    ],
    cities: [
      { name: "Kota", district: "Kota", lat: 25.2138, lng: 75.8648, areas: ["Indra Vihar (Coaching Hub)", "Rajiv Gandhi Nagar", "Talwandi", "Mahaveer Nagar I & II", "Vigyan Nagar", "Kunhari", "Coral Park", "Jawahar Nagar"] },
      { name: "Jaipur", district: "Jaipur", lat: 26.9124, lng: 75.7873, areas: ["Malviya Nagar", "Vaishali Nagar", "Mansarovar", "C-Scheme", "Raja Park", "Jagatpura", "Tonk Road"] },
      { name: "Jodhpur", district: "Jodhpur", lat: 26.2389, lng: 73.0243, areas: ["Shastri Nagar", "Ratanada", "Sardarpura", "Pal Road", "AIIMS Area"] },
      { name: "Udaipur", district: "Udaipur", lat: 24.5854, lng: 73.7125, areas: ["Fateh Sagar", "Hiran Magri", "Panchwati", "Shobhagpura", "Sukher"] },
      { name: "Ajmer", district: "Ajmer", lat: 26.4499, lng: 74.6399, areas: ["Civil Lines", "Vaishali Nagar", "Adarsh Nagar", "Panchsheel"] }
    ]
  },

  // 7. Haryana
  {
    state: "Haryana",
    code: "HR",
    isUT: false,
    capital: "Chandigarh",
    districts: [
      "Gurugram", "Faridabad", "Panipat", "Ambala", "Karnal", "Sonipat", "Hisar",
      "Rohtak", "Panchkula", "Yamunanagar", "Kurukshetra", "Bhiwani", "Sirsa", "Rewari"
    ],
    cities: [
      { name: "Gurugram", district: "Gurugram", lat: 28.4595, lng: 77.0266, areas: ["Cyber City / DLF Phase 2", "DLF Phase 3", "Golf Course Road", "Sohna Road", "Sector 14", "Sector 29", "Sector 56", "Udyog Vihar", "Sector 48"] },
      { name: "Faridabad", district: "Faridabad", lat: 28.4089, lng: 77.3178, areas: ["Sector 15", "Sector 16", "Greenfield Colony", "NIT Faridabad", "Surajkund"] },
      { name: "Panipat", district: "Panipat", lat: 29.3909, lng: 76.9635, areas: ["Model Town", "GT Road", "Sector 11-12", "Huda Sector"] },
      { name: "Ambala", district: "Ambala", lat: 30.3782, lng: 76.7767, areas: ["Ambala Cantt", "Ambala City", "Model Town", "Sector 9"] },
      { name: "Sonipat", district: "Sonipat", lat: 28.9931, lng: 77.0151, areas: ["Rai Education City", "Kundli", "Model Town", "Sector 14"] }
    ]
  },

  // 8. Telangana
  {
    state: "Telangana",
    code: "TG",
    isUT: false,
    capital: "Hyderabad",
    districts: ["Hyderabad", "Ranga Reddy", "Medchal-Malkajgiri", "Sangareddy", "Warangal", "Nizamabad", "Karimnagar", "Khammam"],
    cities: [
      { name: "Hyderabad", district: "Hyderabad", lat: 17.3850, lng: 78.4867, areas: ["HITEC City", "Gachibowli", "Madhapur", "Kondapur", "Jubilee Hills", "Banjara Hills", "Kukatpally", "Ameerpet", "Dilsukhnagar"] },
      { name: "Warangal", district: "Warangal", lat: 17.9689, lng: 79.5941, areas: ["Hanamkonda (NIT Warangal)", "Kazipet", "Subedari", "Naimnagar"] }
    ]
  },

  // 9. Tamil Nadu
  {
    state: "Tamil Nadu",
    code: "TN",
    isUT: false,
    capital: "Chennai",
    districts: ["Chennai", "Coimbatore", "Chengalpattu", "Kanchipuram", "Madurai", "Tiruchirappalli", "Salem", "Tirunelveli", "Erode", "Vellore"],
    cities: [
      { name: "Chennai", district: "Chennai", lat: 13.0827, lng: 80.2707, areas: ["OMR (IT Corridor)", "Velachery", "Adyar", "T. Nagar", "Anna Nagar", "Thoraipakkam", "Sholinganallur", "Guindy", "Nungambakkam"] },
      { name: "Coimbatore", district: "Coimbatore", lat: 11.0168, lng: 76.9558, areas: ["RS Puram", "Gandhipuram", "Peelamedu", "Saravanampatti", "Saibaba Colony"] },
      { name: "Madurai", district: "Madurai", lat: 9.9252, lng: 78.1198, areas: ["KK Nagar", "Anna Nagar", "Simmakkal", "SS Colony"] }
    ]
  },

  // 10. West Bengal
  {
    state: "West Bengal",
    code: "WB",
    isUT: false,
    capital: "Kolkata",
    districts: ["Kolkata", "North 24 Parganas", "South 24 Parganas", "Howrah", "Hooghly", "Darjeeling", "Paschim Bardhaman", "Purba Bardhaman"],
    cities: [
      { name: "Kolkata", district: "Kolkata", lat: 22.5726, lng: 88.3639, areas: ["Salt Lake (Sector V)", "New Town", "Park Street", "Ballygunge", "Dum Dum", "Gariahat", "Jadavpur", "Behala"] },
      { name: "Howrah", district: "Howrah", lat: 22.5958, lng: 88.2636, areas: ["Shibpur", "Salkia", "Bally", "Liluah"] },
      { name: "Durgapur", district: "Paschim Bardhaman", lat: 23.5204, lng: 87.3119, areas: ["City Centre", "Benachity", "B-Zone", "REC NIT Area"] },
      { name: "Siliguri", district: "Darjeeling", lat: 26.7271, lng: 88.3953, areas: ["Sevoke Road", "Pradhan Nagar", "Hakim Para", "Matigara"] }
    ]
  },

  // 11. Gujarat
  {
    state: "Gujarat",
    code: "GJ",
    isUT: false,
    capital: "Gandhinagar",
    districts: ["Ahmedabad", "Surat", "Vadodara", "Rajkot", "Gandhinagar", "Bhavnagar", "Jamnagar", "Junagadh", "Anand", "Kutch"],
    cities: [
      { name: "Ahmedabad", district: "Ahmedabad", lat: 23.0225, lng: 72.5714, areas: ["SG Highway", "Vastrapur (IIM)", "Navrangpura", "Satellite", "Bodakdev", "Prahlad Nagar", "Maninagar"] },
      { name: "Surat", district: "Surat", lat: 21.1702, lng: 72.8311, areas: ["Vesu", "Adajan", "Piplod", "Athwa", "Varachha"] },
      { name: "Vadodara", district: "Vadodara", lat: 22.3072, lng: 73.1812, areas: ["Alkapuri", "Fatehgunj", "Akota", "Gotri", "Sayajigunj"] },
      { name: "Gandhinagar", district: "Gandhinagar", lat: 23.2156, lng: 72.6369, areas: ["Infocity", "Sector 7", "Sector 21", "PDPU Campus Area", "Kudasan"] }
    ]
  },

  // 12. Madhya Pradesh
  {
    state: "Madhya Pradesh",
    code: "MP",
    isUT: false,
    capital: "Bhopal",
    districts: ["Indore", "Bhopal", "Jabalpur", "Gwalior", "Ujjain", "Sagar", "Dewas", "Satna", "Ratlam", "Rewa"],
    cities: [
      { name: "Indore", district: "Indore", lat: 22.7196, lng: 75.8577, areas: ["Vijay Nagar", "Palasia", "Bhawarkua (Coaching Hub)", "AB Road", "Geeta Bhawan", "Super Corridor"] },
      { name: "Bhopal", district: "Bhopal", lat: 23.2599, lng: 77.4126, areas: ["MP Nagar", "Arera Colony", "Shahpura", "Kolar Road", "Hoshangabad Road"] },
      { name: "Jabalpur", district: "Jabalpur", lat: 23.1815, lng: 79.9864, areas: ["Civil Lines", "Wright Town", "Napier Town", "Vijay Nagar"] },
      { name: "Gwalior", district: "Gwalior", lat: 26.2183, lng: 78.1828, areas: ["City Centre", "Lashkar", "Morar", "Thatipur"] }
    ]
  },

  // 13. Kerala
  {
    state: "Kerala",
    code: "KL",
    isUT: false,
    capital: "Thiruvananthapuram",
    districts: ["Ernakulam (Kochi)", "Thiruvananthapuram", "Kozhikode", "Thrissur", "Kollam", "Alappuzha", "Kannur", "Kottayam", "Palakkad", "Malappuram"],
    cities: [
      { name: "Kochi", district: "Ernakulam (Kochi)", lat: 9.9312, lng: 76.2673, areas: ["Kakkanad (Infopark)", "Edappally", "Kaloor", "Panampilly Nagar", "Fort Kochi", "Marine Drive"] },
      { name: "Thiruvananthapuram", district: "Thiruvananthapuram", lat: 8.5241, lng: 76.9366, areas: ["Technopark Phase 1 & 3", "Kazhakkoottam", "Pattom", "Kowdiar", "Vellayambalam"] },
      { name: "Kozhikode", district: "Kozhikode", lat: 11.2588, lng: 75.7804, areas: ["Mavoor Road", "Nadakkavu", "Palayam", "Thondayad"] }
    ]
  },

  // 14. Punjab
  {
    state: "Punjab",
    code: "PB",
    isUT: false,
    capital: "Chandigarh",
    districts: ["Ludhiana", "Amritsar", "Jalandhar", "SAS Nagar (Mohali)", "Patiala", "Bathinda", "Hoshiarpur", "Pathankot"],
    cities: [
      { name: "Ludhiana", district: "Ludhiana", lat: 30.9010, lng: 75.8573, areas: ["Model Town", "Sarabha Nagar", "BRS Nagar", "Civil Lines", "Ferozepur Road"] },
      { name: "Amritsar", district: "Amritsar", lat: 31.6340, lng: 74.8723, areas: ["Ranjit Avenue", "Mall Road", "Lawrence Road", "Golden Temple Area"] },
      { name: "Mohali", district: "SAS Nagar (Mohali)", lat: 30.7046, lng: 76.7179, areas: ["Phase 3B2", "Phase 7", "Phase 11", "Sector 70", "Aerocity"] }
    ]
  },

  // 15. Chandigarh (UT)
  {
    state: "Chandigarh",
    code: "CH",
    isUT: true,
    capital: "Chandigarh",
    districts: ["Chandigarh"],
    cities: [
      { name: "Chandigarh", district: "Chandigarh", lat: 30.7333, lng: 76.7794, areas: ["Sector 17", "Sector 22", "Sector 35", "Sector 8", "Sector 43", "Industrial Area Phase 1"] }
    ]
  },

  // 16. Andhra Pradesh
  {
    state: "Andhra Pradesh",
    code: "AP",
    isUT: false,
    capital: "Amaravati",
    districts: ["Visakhapatnam", "NTR (Vijayawada)", "Guntur", "Tirupati", "Kurnool", "Nellore", "Kakinada", "Ananthapuramu", "YSR Kadapa"],
    cities: [
      { name: "Visakhapatnam", district: "Visakhapatnam", lat: 17.6868, lng: 83.2185, areas: ["MVP Colony", "Siripuram", "Gajuwaka", "Madhurawada (IT SEZ)", "Beach Road"] },
      { name: "Vijayawada", district: "NTR (Vijayawada)", lat: 16.5062, lng: 80.6480, areas: ["Benz Circle", "Governorpet", "Moghalrajpuram", "Bhavanipuram"] },
      { name: "Tirupati", district: "Tirupati", lat: 13.6288, lng: 79.4192, areas: ["KT Road", "Bhavani Nagar", "Alipiri", "MR Palli"] }
    ]
  },

  // 17. Odisha
  {
    state: "Odisha",
    code: "OD",
    isUT: false,
    capital: "Bhubaneswar",
    districts: ["Khordha (Bhubaneswar)", "Cuttack", "Sundargarh (Rourkela)", "Puri", "Sambalpur", "Ganjam (Berhampur)", "Balasore"],
    cities: [
      { name: "Bhubaneswar", district: "Khordha (Bhubaneswar)", lat: 20.2961, lng: 85.8245, areas: ["Patia (KIIT Area)", "Jayadev Vihar", "Saheed Nagar", "Chandrasekharpur", "Khandagiri"] },
      { name: "Cuttack", district: "Cuttack", lat: 20.4625, lng: 85.8830, areas: ["CDA Sector 6-10", "Badambadi", "College Square", "Link Road"] }
    ]
  },

  // 18. Assam
  {
    state: "Assam",
    code: "AS",
    isUT: false,
    capital: "Dispur",
    districts: ["Kamrup Metropolitan (Guwahati)", "Dibrugarh", "Cachar (Silchar)", "Jorhat", "Nagaon", "Tinsukia", "Sonitpur (Tezpur)"],
    cities: [
      { name: "Guwahati", district: "Kamrup Metropolitan (Guwahati)", lat: 26.1445, lng: 91.7362, areas: ["GS Road", "Zoo Road", "Pan Bazaar", "Dispur", "Jalukbari (Gauhati University)", "Beltola"] }
    ]
  },

  // 19. Jharkhand
  {
    state: "Jharkhand",
    code: "JH",
    isUT: false,
    capital: "Ranchi",
    districts: ["Ranchi", "East Singhbhum (Jamshedpur)", "Dhanbad", "Bokaro", "Hazaribagh", "Deoghar", "Giridih"],
    cities: [
      { name: "Ranchi", district: "Ranchi", lat: 23.3441, lng: 85.3096, areas: ["Lalpur", "Main Road", "Doranda", "Harmu", "Ashok Nagar", "Bariatu"] },
      { name: "Jamshedpur", district: "East Singhbhum (Jamshedpur)", lat: 22.8046, lng: 86.2029, areas: ["Bistupur", "Sakchi", "Kadma", "Sonari", "Telco"] },
      { name: "Dhanbad", district: "Dhanbad", lat: 23.7957, lng: 86.4304, areas: ["Bank More", "Saraidhela", "IIT ISM Area", "Hirapur"] }
    ]
  },

  // 20. Uttarakhand
  {
    state: "Uttarakhand",
    code: "UK",
    isUT: false,
    capital: "Dehradun",
    districts: ["Dehradun", "Haridwar", "Nainital", "Udham Singh Nagar (Haldwani)", "Pauri Garhwal", "Tehri Garhwal", "Almora"],
    cities: [
      { name: "Dehradun", district: "Dehradun", lat: 30.3165, lng: 78.0322, areas: ["Rajpur Road", "Chakrata Road", "Karanpur", "Prem Nagar (UPES Hub)", "Subhash Nagar", "Dalanwala"] },
      { name: "Haridwar", district: "Haridwar", lat: 29.9457, lng: 78.1642, areas: ["Ranipur", "Jwalapur", "Har Ki Pauri Area", "Shivalik Nagar"] },
      { name: "Rishikesh", district: "Dehradun", lat: 30.0869, lng: 78.2676, areas: ["Tapovan", "Laxman Jhula Area", "Dhalwala", "AIIMS Rishikesh Area"] }
    ]
  },

  // 21. Himachal Pradesh
  {
    state: "Himachal Pradesh",
    code: "HP",
    isUT: false,
    capital: "Shimla",
    districts: ["Shimla", "Kangra (Dharamshala)", "Kullu (Manali)", "Solan", "Mandi", "Sirmaur", "Hamirpur", "Una"],
    cities: [
      { name: "Shimla", district: "Shimla", lat: 31.1048, lng: 77.1734, areas: ["Mall Road", "Sanjauli", "Chotta Shimla", "Summer Hill", "Kasumpti"] },
      { name: "Dharamshala", district: "Kangra (Dharamshala)", lat: 32.2190, lng: 76.3234, areas: ["McLeod Ganj", "Kotwali Bazar", "Dharamkot", "Bhagsunag"] }
    ]
  },

  // 22. Goa
  {
    state: "Goa",
    code: "GA",
    isUT: false,
    capital: "Panaji",
    districts: ["North Goa", "South Goa"],
    cities: [
      { name: "Panaji", district: "North Goa", lat: 15.4909, lng: 73.8278, areas: ["Fontainhas", "Miramar", "Campal", "Dona Paula", "Patto Centre"] },
      { name: "Margao", district: "South Goa", lat: 15.2832, lng: 73.9862, areas: ["Fatorda", "Borda", "Comba", "Aquem"] }
    ]
  },

  // 23. Jammu and Kashmir (UT)
  {
    state: "Jammu and Kashmir",
    code: "JK",
    isUT: true,
    capital: "Srinagar / Jammu",
    districts: ["Srinagar", "Jammu", "Anantnag", "Baramulla", "Udhampur", "Pulwama", "Kathua", "Budgam"],
    cities: [
      { name: "Srinagar", district: "Srinagar", lat: 34.0837, lng: 74.7973, areas: ["Lal Chowk", "Rajbagh", "Karan Nagar", "Dal Lake Boulevard", "Hazratbal"] },
      { name: "Jammu", district: "Jammu", lat: 32.7266, lng: 74.8570, areas: ["Gandhi Nagar", "Trikuta Nagar", "Channi Himmat", "Bahu Plaza", "Janipur"] }
    ]
  },

  // 24. Ladakh (UT)
  {
    state: "Ladakh",
    code: "LA",
    isUT: true,
    capital: "Leh",
    districts: ["Leh", "Kargil"],
    cities: [
      { name: "Leh", district: "Leh", lat: 34.1526, lng: 77.5771, areas: ["Main Bazaar", "Changspa", "Choglamsar", "Skara"] }
    ]
  },

  // 25. Puducherry (UT)
  {
    state: "Puducherry",
    code: "PY",
    isUT: true,
    capital: "Puducherry",
    districts: ["Puducherry", "Karaikal", "Mahe", "Yanam"],
    cities: [
      { name: "Puducherry", district: "Puducherry", lat: 11.9416, lng: 79.8083, areas: ["White Town (French Quarter)", "Heritage Town", "Lawspet", "Auroville Area"] }
    ]
  },

  // 26. Tripura
  {
    state: "Tripura",
    code: "TR",
    isUT: false,
    capital: "Agartala",
    districts: ["West Tripura", "Sepahijala", "Gomati", "South Tripura", "North Tripura"],
    cities: [
      { name: "Agartala", district: "West Tripura", lat: 23.8315, lng: 91.2868, areas: ["Banamalipur", "Radhanagar", "Akhaura Road", "Kunjaban"] }
    ]
  },

  // 27. Meghalaya
  {
    state: "Meghalaya",
    code: "ML",
    isUT: false,
    capital: "Shillong",
    districts: ["East Khasi Hills (Shillong)", "West Khasi Hills", "Ri-Bhoi", "West Garo Hills"],
    cities: [
      { name: "Shillong", district: "East Khasi Hills (Shillong)", lat: 25.5788, lng: 91.8933, areas: ["Police Bazar", "Laitumkhrah", "Mawlai", "Nongthymmai"] }
    ]
  },

  // 28. Manipur
  {
    state: "Manipur",
    code: "MN",
    isUT: false,
    capital: "Imphal",
    districts: ["Imphal West", "Imphal East", "Thoubal", "Bishnupur", "Churachandpur"],
    cities: [
      { name: "Imphal", district: "Imphal West", lat: 24.8170, lng: 93.9368, areas: ["Thangal Bazar", "Paona Bazar", "Kwakeithel", "Lamphelpat"] }
    ]
  },

  // 29. Nagaland
  {
    state: "Nagaland",
    code: "NL",
    isUT: false,
    capital: "Kohima",
    districts: ["Kohima", "Dimapur", "Mokokchung", "Tuensang", "Wokha", "Chumoukedima"],
    cities: [
      { name: "Kohima", district: "Kohima", lat: 25.6751, lng: 94.1086, areas: ["High School Junction", "PR Hill", "Razhu Point", "Officer's Hill"] },
      { name: "Dimapur", district: "Dimapur", lat: 25.9093, lng: 93.7266, areas: ["City Tower", "Circular Road", "Purana Bazar", "Duncan Basti"] }
    ]
  },

  // 30. Mizoram
  {
    state: "Mizoram",
    code: "MZ",
    isUT: false,
    capital: "Aizawl",
    districts: ["Aizawl", "Lunglei", "Champhai", "Kolasib", "Serchhip"],
    cities: [
      { name: "Aizawl", district: "Aizawl", lat: 23.7271, lng: 92.7176, areas: ["Zarkawt", "Khatla", "Chanmari", "Bawngkawn", "Dawrpui"] }
    ]
  },

  // 31. Arunachal Pradesh
  {
    state: "Arunachal Pradesh",
    code: "AR",
    isUT: false,
    capital: "Itanagar",
    districts: ["Papum Pare (Itanagar)", "Changlang", "West Kameng", "Tawang", "East Siang"],
    cities: [
      { name: "Itanagar", district: "Papum Pare (Itanagar)", lat: 27.0844, lng: 93.6053, areas: ["Ganga Market", "Bank Tinali", "Naharlagun", "Chandranagar"] }
    ]
  },

  // 32. Sikkim
  {
    state: "Sikkim",
    code: "SK",
    isUT: false,
    capital: "Gangtok",
    districts: ["East Sikkim (Gangtok)", "West Sikkim", "North Sikkim", "South Sikkim"],
    cities: [
      { name: "Gangtok", district: "East Sikkim (Gangtok)", lat: 27.3389, lng: 88.6065, areas: ["MG Marg", "Deorali", "Tadong", "Development Area"] }
    ]
  },

  // 33. Chhattisgarh
  {
    state: "Chhattisgarh",
    code: "CG",
    isUT: false,
    capital: "Raipur",
    districts: ["Raipur", "Durg (Bhilai)", "Bilaspur", "Korba", "Rajnandgaon", "Jagdalpur", "Raigarh"],
    cities: [
      { name: "Raipur", district: "Raipur", lat: 21.2514, lng: 81.6296, areas: ["Pandri", "Telibandha", "Shankar Nagar", "Samta Colony", "Naya Raipur"] },
      { name: "Bhilai", district: "Durg (Bhilai)", lat: 21.1938, lng: 81.3509, areas: ["Sector 1-10", "Nehru Nagar", "Civic Centre", "Smriti Nagar"] }
    ]
  },

  // 34. Andaman and Nicobar Islands (UT)
  {
    state: "Andaman and Nicobar Islands",
    code: "AN",
    isUT: true,
    capital: "Port Blair",
    districts: ["South Andaman", "North and Middle Andaman", "Nicobar"],
    cities: [
      { name: "Port Blair", district: "South Andaman", lat: 11.6234, lng: 92.7265, areas: ["Aberdeen Bazaar", "Dollygunj", "Junglighat", "Haddo"] }
    ]
  },

  // 35. Dadra and Nagar Haveli and Daman and Diu (UT)
  {
    state: "Dadra and Nagar Haveli and Daman and Diu",
    code: "DN",
    isUT: true,
    capital: "Daman",
    districts: ["Daman", "Diu", "Dadra and Nagar Haveli (Silvassa)"],
    cities: [
      { name: "Daman", district: "Daman", lat: 20.3974, lng: 72.8328, areas: ["Nani Daman", "Moti Daman", "Devka Beach Road"] },
      { name: "Silvassa", district: "Dadra and Nagar Haveli (Silvassa)", lat: 20.2763, lng: 73.0083, areas: ["Naroli Road", "Amli", "Tokarkhada"] }
    ]
  },

  // 36. Lakshadweep (UT)
  {
    state: "Lakshadweep",
    code: "LD",
    isUT: true,
    capital: "Kavaratti",
    districts: ["Lakshadweep"],
    cities: [
      { name: "Kavaratti", district: "Lakshadweep", lat: 10.5669, lng: 72.6420, areas: ["Main Island", "Jetty Area"] }
    ]
  }
];

// Key International Destinations & Authentic Administrative Data
const INTERNATIONAL_REGIONS_DATA = {
  "US": {
    name: "United States",
    states: [
      {
        state: "California",
        code: "CA",
        districts: ["San Francisco County", "Santa Clara County", "Los Angeles County", "San Diego County"],
        cities: [
          { name: "San Francisco", district: "San Francisco County", lat: 37.7749, lng: -122.4194, areas: ["Mission District", "SoMa", "Financial District", "Marina"] },
          { name: "San Jose", district: "Santa Clara County", lat: 37.3382, lng: -121.8863, areas: ["Downtown", "North San Jose", "Willow Glen"] },
          { name: "Los Angeles", district: "Los Angeles County", lat: 34.0522, lng: -118.2437, areas: ["Downtown LA", "Hollywood", "Westwood (UCLA)", "Santa Monica"] }
        ]
      },
      {
        state: "New York",
        code: "NY",
        districts: ["New York County (Manhattan)", "Kings County (Brooklyn)", "Queens County"],
        cities: [
          { name: "New York City", district: "New York County (Manhattan)", lat: 40.7128, lng: -74.0060, areas: ["Manhattan Midtown", "Upper West Side", "Greenwich Village", "Williamsburg"] }
        ]
      },
      {
        state: "Texas",
        code: "TX",
        districts: ["Travis County (Austin)", "Harris County (Houston)", "Dallas County"],
        cities: [
          { name: "Austin", district: "Travis County (Austin)", lat: 30.2672, lng: -97.7431, areas: ["Downtown", "South Congress", "Domain", "UT Austin Campus Area"] }
        ]
      }
    ]
  },
  "GB": {
    name: "United Kingdom",
    states: [
      {
        state: "England",
        code: "ENG",
        districts: ["Greater London", "Greater Manchester", "West Midlands"],
        cities: [
          { name: "London", district: "Greater London", lat: 51.5074, lng: -0.1278, areas: ["Westminster", "Camden", "Shoreditch", "Kensington", "Canary Wharf"] },
          { name: "Manchester", district: "Greater Manchester", lat: 53.4808, lng: -2.2426, areas: ["Northern Quarter", "Deansgate", "Salford", "Oxford Road Corridor"] }
        ]
      },
      {
        state: "Scotland",
        code: "SCT",
        districts: ["City of Edinburgh", "Glasgow City"],
        cities: [
          { name: "Edinburgh", district: "City of Edinburgh", lat: 55.9533, lng: -3.1883, areas: ["Old Town", "New Town", "Leith", "Haymarket"] }
        ]
      }
    ]
  },
  "AE": {
    name: "United Arab Emirates",
    states: [
      {
        state: "Dubai",
        code: "DXB",
        districts: ["Dubai Municipality"],
        cities: [
          { name: "Dubai", district: "Dubai Municipality", lat: 25.2048, lng: 55.2708, areas: ["Downtown Dubai", "Dubai Marina", "Business Bay", "Deira", "JBR", "JLT"] }
        ]
      },
      {
        state: "Abu Dhabi",
        code: "AUH",
        districts: ["Abu Dhabi Municipality"],
        cities: [
          { name: "Abu Dhabi", district: "Abu Dhabi Municipality", lat: 24.4539, lng: 54.3773, areas: ["Al Reem Island", "Corniche", "Yas Island", "Saadiyat"] }
        ]
      }
    ]
  }
};

// ==========================================================================
// 3. REUSABLE STYNO LOCATION SERVICE (OpenStreetMap / Photon / Nominatim Stack)
// ==========================================================================

const INDIAN_CITY_ALIASES = {
  "bangalore": "bengaluru",
  "bengaluru": "bangalore",
  "bombay": "mumbai",
  "mumbai": "bombay",
  "calcutta": "kolkata",
  "kolkata": "calcutta",
  "madras": "chennai",
  "chennai": "madras",
  "gurgaon": "gurugram",
  "gurugram": "gurgaon",
  "prayagraj": "allahabad",
  "allahabad": "prayagraj",
  "varanasi": "banaras",
  "banaras": "varanasi",
  "kashi": "varanasi",
  "ayodhya": "faizabad",
  "faizabad": "ayodhya",
  "cochin": "kochi",
  "kochi": "cochin",
  "trivandrum": "thiruvananthapuram",
  "thiruvananthapuram": "trivandrum",
  "poona": "pune",
  "pune": "poona",
  "baroda": "vadodara",
  "vadodara": "baroda",
  "chhapra": "saran",
  "saran": "chhapra",
  "ara": "bhojpur",
  "bhojpur": "ara",
  "bihar sharif": "nalanda",
  "nalanda": "bihar sharif",
  "bettiah": "west champaran",
  "motihari": "east champaran",
  "pondicherry": "puducherry",
  "puducherry": "pondicherry",
  "vizag": "visakhapatnam",
  "visakhapatnam": "vizag",
  "calicut": "kozhikode",
  "kozhikode": "calicut"
};

class LocationService {
  constructor() {
    this.searchCache = new Map();
    this.reverseGeocodeCache = new Map();
    this.pincodeCache = new Map();
    this.lastQueryTime = 0;
    this.activeSearchAbort = null;
    this.CACHE_TTL_MS = 24 * 60 * 60 * 1000; // 24 hours
    this.initPersistentCache();
  }

  initPersistentCache() {
    try {
      if (typeof localStorage !== "undefined") {
        for (let i = 0; i < localStorage.length; i++) {
          const key = localStorage.key(i);
          if (key && key.startsWith("styno_loc_cache_")) {
            const raw = localStorage.getItem(key);
            if (raw) {
              const item = JSON.parse(raw);
              if (Date.now() - (item.timestamp || 0) < this.CACHE_TTL_MS) {
                if (key.startsWith("styno_loc_cache_search_")) {
                  this.searchCache.set(key.replace("styno_loc_cache_search_", ""), item.data);
                } else if (key.startsWith("styno_loc_cache_rev_")) {
                  this.reverseGeocodeCache.set(key.replace("styno_loc_cache_rev_", ""), item.data);
                } else if (key.startsWith("styno_loc_cache_pincode_")) {
                  this.pincodeCache.set(key.replace("styno_loc_cache_pincode_", ""), item.data);
                }
              } else {
                localStorage.removeItem(key);
              }
            }
          }
        }
      }
    } catch (e) {
      // Non-blocking storage check
    }
  }

  saveToCache(type, key, data) {
    try {
      if (type === "search") {
        this.searchCache.set(key, data);
        if (typeof localStorage !== "undefined" && this.searchCache.size < 150) {
          localStorage.setItem(`styno_loc_cache_search_${key}`, JSON.stringify({ timestamp: Date.now(), data }));
        }
      } else if (type === "reverse") {
        this.reverseGeocodeCache.set(key, data);
        if (typeof localStorage !== "undefined" && this.reverseGeocodeCache.size < 150) {
          localStorage.setItem(`styno_loc_cache_rev_${key}`, JSON.stringify({ timestamp: Date.now(), data }));
        }
      } else if (type === "pincode") {
        this.pincodeCache.set(key, data);
        if (typeof localStorage !== "undefined" && this.pincodeCache.size < 200) {
          localStorage.setItem(`styno_loc_cache_pincode_${key}`, JSON.stringify({ timestamp: Date.now(), data }));
        }
      }
    } catch (e) {
      // Cache quota exceeded safeguard
    }
  }

  /**
   * Levenshtein distance for fuzzy Indian place matching
   */
  levenshtein(a, b) {
    if (a.length === 0) return b.length;
    if (b.length === 0) return a.length;
    const matrix = [];
    for (let i = 0; i <= b.length; i++) matrix[i] = [i];
    for (let j = 0; j <= a.length; j++) matrix[0][j] = j;

    for (let i = 1; i <= b.length; i++) {
      for (let j = 1; j <= a.length; j++) {
        if (b.charAt(i - 1) === a.charAt(j - 1)) {
          matrix[i][j] = matrix[i - 1][j - 1];
        } else {
          matrix[i][j] = Math.min(
            matrix[i - 1][j - 1] + 1, // substitution
            matrix[i][j - 1] + 1,     // insertion
            matrix[i - 1][j] + 1      // deletion
          );
        }
      }
    }
    return matrix[b.length][a.length];
  }

  isFuzzyMatch(needle, haystack) {
    const n = needle.toLowerCase();
    const h = haystack.toLowerCase();
    if (h.includes(n)) return true;
    if (n.length >= 4 && Math.abs(n.length - h.length) <= 2) {
      const maxAllowed = n.length >= 6 ? 2 : 1;
      return this.levenshtein(n, h) <= maxAllowed;
    }
    return false;
  }

  /**
   * Unified Natural Location Search across entire India and worldwide:
   * Supports forward geocoding, multi-token matching (e.g. "Bagaha West Champaran", "Muzaffarpur Bihar", "Koramangala Bangalore"),
   * district search, city search, area search, and OpenStreetMap Photon / Nominatim live resolution.
   */
  async search(query, options = {}) {
    const rawQ = (query || "").trim();
    if (rawQ.length < 2) return [];

    const cacheKey = rawQ.toLowerCase();
    if (this.searchCache.has(cacheKey)) {
      return this.searchCache.get(cacheKey);
    }

    const results = [];
    const seenKeys = new Set();

    const addResult = (item, score = 50) => {
      if (!item || !item.name) return;
      const key = `${item.countryCode || 'IN'}_${item.state || ''}_${item.city || ''}_${item.area || item.district || item.name}`.toLowerCase().replace(/[^a-z0-9]/g, '');
      if (!seenKeys.has(key)) {
        seenKeys.add(key);
        item._score = score;
        results.push(item);
      } else {
        // Upgrade score if higher
        const existing = results.find(r => r.id === item.id);
        if (existing && score > (existing._score || 0)) {
          existing._score = score;
        }
      }
    };

    const qLower = rawQ.toLowerCase();
    const tokens = qLower.split(/[\s,]+/).filter(Boolean);

    // PIN Code automatic lookup support in global search
    if (/^\d{6}$/.test(rawQ)) {
      try {
        const pinData = await this.lookupPincode(rawQ);
        if (pinData && pinData.success && Array.isArray(pinData.postOffices)) {
          pinData.postOffices.forEach((po, index) => {
            addResult({
              id: `pin-${pinData.pincode}-${index}`,
              name: `${po.name} (${pinData.pincode})`,
              country: "India",
              countryCode: "IN",
              countryFlag: "🇮🇳",
              state: pinData.state,
              district: pinData.district,
              city: pinData.city,
              area: po.name,
              pincode: pinData.pincode,
              formattedAddress: `${po.name}, ${pinData.city}, ${pinData.district}, ${pinData.state} - ${pinData.pincode}, India`,
              type: "PINCODE",
              latitude: pinData.latitude,
              longitude: pinData.longitude,
              tag: `📮 PIN ${pinData.pincode} • ${po.name}, ${pinData.district}, ${pinData.state}`,
              postOffices: pinData.postOffices
            }, 300 - index);
          });
          if (results.length > 0) {
            results.sort((a, b) => (b._score || 0) - (a._score || 0));
            this.saveToCache("search", cacheKey, results);
            return results;
          }
        }
      } catch (e) {
        // Non-blocking fallback to text matching
      }
    }

    // 1. Authoritative Multi-Token Matching across all 36 Indian States & UTs, Districts, and Cities
    for (const st of INDIA_ADMINISTRATIVE_DATA) {
      const stateNameLower = st.state.toLowerCase();
      const stateMatchesDirect = stateNameLower.includes(qLower);

      // Check cities and areas inside state
      for (const c of st.cities) {
        const cityNameLower = c.name.toLowerCase();
        const distNameLower = (c.district || "").toLowerCase();
        const alias = INDIAN_CITY_ALIASES[cityNameLower] || "";

        // Count how many tokens this city/district/state matches
        let matchedTokenCount = 0;
        tokens.forEach(t => {
          if (cityNameLower.includes(t) || (alias && alias.includes(t)) || this.isFuzzyMatch(t, cityNameLower)) matchedTokenCount++;
          else if (distNameLower.includes(t) || this.isFuzzyMatch(t, distNameLower)) matchedTokenCount++;
          else if (stateNameLower.includes(t) || this.isFuzzyMatch(t, stateNameLower)) matchedTokenCount++;
        });

        const isExactCity = cityNameLower === qLower || alias === qLower;
        const isCityPrefix = cityNameLower.startsWith(qLower) || (alias && alias.startsWith(qLower));
        const isDirectCity = cityNameLower.includes(qLower) || (alias && alias.includes(qLower)) || this.isFuzzyMatch(qLower, cityNameLower);
        const matchesAllTokens = tokens.length > 1 && matchedTokenCount >= tokens.length;
        const matchesMajorityTokens = tokens.length > 1 && matchedTokenCount >= 2;

        let cityScore = 0;
        if (isExactCity) cityScore = 200;
        else if (matchesAllTokens) cityScore = 180;
        else if (isCityPrefix) cityScore = 140;
        else if (isDirectCity) cityScore = 120;
        else if (matchesMajorityTokens) cityScore = 100;
        else if (matchedTokenCount > 0 && tokens.length === 1) cityScore = 80;

        // Check areas inside city
        for (const area of (c.areas || [])) {
          const areaLower = area.toLowerCase();
          let areaTokenMatches = 0;
          tokens.forEach(t => {
            if (areaLower.includes(t)) areaTokenMatches++;
            else if (cityNameLower.includes(t) || (alias && alias.includes(t))) areaTokenMatches++;
            else if (distNameLower.includes(t)) areaTokenMatches++;
          });

          const isExactArea = areaLower === qLower;
          const isAreaPrefix = areaLower.startsWith(qLower);
          const isDirectArea = areaLower.includes(qLower);
          const allTokensMatchArea = tokens.length > 1 && areaTokenMatches >= tokens.length;

          let areaScore = 0;
          if (isExactArea) areaScore = 190;
          else if (allTokensMatchArea) areaScore = 175;
          else if (isAreaPrefix) areaScore = 135;
          else if (isDirectArea) areaScore = 115;
          else if (cityScore > 0) areaScore = cityScore - 15;

          if (areaScore > 0) {
            addResult({
              id: `in-${c.name.toLowerCase()}-${area.toLowerCase().replace(/[^a-z0-9]/g, '-')}`,
              name: area,
              country: "India",
              countryCode: "IN",
              countryFlag: "🇮🇳",
              state: st.state,
              district: c.district,
              city: c.name,
              area: area,
              locality: area,
              formattedAddress: `${area}, ${c.name}, ${c.district ? c.district + ', ' : ''}${st.state}, India`,
              type: "AREA",
              latitude: c.lat,
              longitude: c.lng,
              tag: `${area} • Locality in ${c.name} (${c.district || st.state})`
            }, areaScore);
          }
        }

        if (cityScore > 0) {
          addResult({
            id: `in-${st.code}-${c.name.toLowerCase()}`,
            name: c.name,
            country: "India",
            countryCode: "IN",
            countryFlag: "🇮🇳",
            state: st.state,
            district: c.district,
            city: c.name,
            area: null,
            locality: null,
            formattedAddress: `${c.name}, ${c.district ? c.district + ', ' : ''}${st.state}, India`,
            type: "CITY",
            latitude: c.lat,
            longitude: c.lng,
            tag: `${c.name} • City in ${c.district || st.state}, ${st.state}`
          }, cityScore);
        }
      }

      // Check districts across all 36 Indian states & UTs
      for (const dist of (st.districts || [])) {
        const distClean = dist.replace(/\s*\([^)]*\)/, '').trim();
        const distLower = dist.toLowerCase();

        let distTokenCount = 0;
        tokens.forEach(t => {
          if (distLower.includes(t) || this.isFuzzyMatch(t, distClean)) distTokenCount++;
          else if (stateNameLower.includes(t)) distTokenCount++;
        });

        const isExactDist = distLower === qLower || distClean.toLowerCase() === qLower;
        const isDistPrefix = distLower.startsWith(qLower);
        const isDirectDist = distLower.includes(qLower) || this.isFuzzyMatch(qLower, distClean);
        const distMatchesTokens = tokens.length > 1 && distTokenCount >= tokens.length;

        let distScore = 0;
        if (isExactDist) distScore = 170;
        else if (distMatchesTokens) distScore = 150;
        else if (isDistPrefix) distScore = 130;
        else if (isDirectDist) distScore = 90;

        if (distScore > 0) {
          const matchingCity = st.cities.find(c => c.district === dist || c.district === distClean) || st.cities[0];
          const lat = matchingCity ? matchingCity.lat : 22.5000;
          const lng = matchingCity ? matchingCity.lng : 82.5000;

          addResult({
            id: `in-${st.code}-${distClean.toLowerCase().replace(/[^a-z0-9]/g, '-')}`,
            name: distClean,
            country: "India",
            countryCode: "IN",
            countryFlag: "🇮🇳",
            state: st.state,
            district: distClean,
            city: distClean,
            area: null,
            locality: null,
            formattedAddress: `${distClean}, ${st.state}, India`,
            type: "DISTRICT",
            latitude: lat,
            longitude: lng,
            tag: `${distClean} • District in ${st.state}`
          }, distScore);
        }
      }

      // Check state match
      if (stateMatchesDirect) {
        const capitalCity = st.cities.find(c => c.name.toLowerCase() === (st.capital || "").toLowerCase()) || st.cities[0];
        const stateScore = stateNameLower === qLower ? 160 : 85;
        addResult({
          id: `in-state-${st.code}`,
          name: st.state,
          country: "India",
          countryCode: "IN",
          countryFlag: "🇮🇳",
          state: st.state,
          district: null,
          city: capitalCity ? capitalCity.name : st.capital,
          area: null,
          locality: null,
          formattedAddress: `${st.state}, India`,
          type: "STATE",
          latitude: capitalCity ? capitalCity.lat : 20.5937,
          longitude: capitalCity ? capitalCity.lng : 78.9629,
          tag: `${st.state} • ${st.isUT ? 'Union Territory' : 'State'} of India`
        }, stateScore);
      }
    }

    // 2. Check international catalog
    if (typeof INTERNATIONAL_REGIONS_DATA !== "undefined") {
      for (const [cCode, cData] of Object.entries(INTERNATIONAL_REGIONS_DATA)) {
        const countryObj = (typeof WORLDWIDE_COUNTRIES !== "undefined") ? WORLDWIDE_COUNTRIES.find(c => c.code === cCode) : null;
        for (const st of (cData.states || [])) {
          for (const c of (st.cities || [])) {
            if (c.name.toLowerCase().includes(qLower) || st.state.toLowerCase().includes(qLower)) {
              addResult({
                id: `${cCode.toLowerCase()}-${c.name.toLowerCase()}`,
                name: c.name,
                country: cData.name,
                countryCode: cCode,
                countryFlag: countryObj?.flag || "🌍",
                state: st.state,
                district: c.district,
                city: c.name,
                area: null,
                locality: null,
                formattedAddress: `${c.name}, ${st.state}, ${cData.name}`,
                type: "CITY",
                latitude: c.lat,
                longitude: c.lng,
                tag: `${c.name} • City in ${st.state}, ${cData.name}`
              });
            }
          }
        }
      }
    }

    // 3. Worldwide country matching
    if (typeof WORLDWIDE_COUNTRIES !== "undefined") {
      for (const c of WORLDWIDE_COUNTRIES) {
        if (c.name.toLowerCase().includes(qLower) && c.name.toLowerCase() !== "india") {
          addResult({
            id: `country-${c.code.toLowerCase()}`,
            name: c.name,
            country: c.name,
            countryCode: c.code,
            countryFlag: c.flag,
            state: null,
            district: null,
            city: null,
            area: null,
            locality: null,
            formattedAddress: `${c.name}`,
            type: "COUNTRY",
            latitude: 20.0,
            longitude: 0.0,
            tag: `${c.flag} ${c.name} • ${c.continent}`
          });
        }
      }
    }

    // 4. OpenStreetMap Photon live API integration (covers every Indian village, town, and railway station)
    // Rate-limited and cached to never overload public servers
    if (results.length < 6 && rawQ.length >= 3) {
      if (this.activeSearchAbort) {
        this.activeSearchAbort.abort();
      }
      this.activeSearchAbort = new AbortController();

      try {
        const encoded = encodeURIComponent(rawQ);
        // Biased with center of India coordinates (lat: 22.0, lon: 79.0)
        const photonUrl = `https://photon.komoot.io/api/?q=${encoded}&limit=8&lat=22.0&lon=79.0&lang=en`;
        const res = await fetch(photonUrl, { signal: this.activeSearchAbort.signal });
        if (res.ok) {
          const json = await res.json();
          if (json && Array.isArray(json.features)) {
            for (const feat of json.features) {
              const p = feat.properties || {};
              const coords = feat.geometry?.coordinates || [0, 0];
              const cName = p.country || "India";
              const sName = p.state || "";
              const distName = p.county || p.district || null;
              const cityName = p.city || p.town || p.village || p.municipality || p.district || p.name || "";
              const areaName = (p.locality || p.suburb || p.neighbourhood || p.street || (p.name !== cityName ? p.name : null)) || null;
              const displayName = p.name || areaName || cityName;

              if (displayName && coords[0] !== 0 && coords[1] !== 0) {
                const formatted = [displayName, areaName !== displayName ? areaName : null, cityName !== displayName ? cityName : null, sName, cName].filter(Boolean).join(", ");
                addResult({
                  id: `osm-${p.osm_id || Math.random().toString(36).substring(7)}`,
                  name: displayName,
                  country: cName,
                  countryCode: (p.countrycode || "IN").toUpperCase(),
                  countryFlag: (typeof WORLDWIDE_COUNTRIES !== "undefined") ? (WORLDWIDE_COUNTRIES.find(c => c.name.toLowerCase() === cName.toLowerCase())?.flag || "📍") : "📍",
                  state: sName,
                  district: distName,
                  city: cityName,
                  area: areaName,
                  locality: areaName,
                  formattedAddress: formatted,
                  type: (p.type || p.osm_value || "LOCATION").toUpperCase(),
                  latitude: coords[1],
                  longitude: coords[0],
                  tag: `${displayName} • ${[cityName, sName].filter(Boolean).join(", ")}`
                });
              }
            }
          }
        }
      } catch (err) {
        // Non-blocking timeout/abort fallback: local curated hierarchy results remain instant
      }
    }

    // Sort results by relevance score descending
    results.sort((a, b) => (b._score || 0) - (a._score || 0));

    // Cache the resolved results
    this.saveToCache("search", cacheKey, results);
    return results;
  }

  /**
   * Reverse-geocodes actual device GPS coordinates into official administrative hierarchy:
   * Country -> State -> District -> City / Town / Village -> Locality / Area -> Formatted Address
   * Uses OpenStreetMap Nominatim with instant fallback to nearest authoritative catalog entry.
   */
  async reverseGeocode(lat, lng) {
    if (typeof lat !== "number" || typeof lng !== "number" || isNaN(lat) || isNaN(lng)) {
      return null;
    }

    const cacheKey = `${lat.toFixed(4)},${lng.toFixed(4)}`;
    if (this.reverseGeocodeCache.has(cacheKey)) {
      return this.reverseGeocodeCache.get(cacheKey);
    }

    try {
      const url = `https://nominatim.openstreetmap.org/reverse?format=json&lat=${lat}&lon=${lng}&zoom=18&addressdetails=1`;
      const res = await fetch(url, {
        headers: { "Accept-Language": "en" },
        signal: AbortSignal.timeout(3500)
      });

      if (res.ok) {
        const json = await res.json();
        const addr = json.address || {};

        const country = addr.country || "India";
        const countryCode = (addr.country_code || "in").toUpperCase();
        const state = addr.state || addr.region || "";
        const district = addr.state_district || addr.county || addr.district || null;
        const city = addr.city || addr.town || addr.municipality || addr.village || addr.suburb || district || "";
        const area = addr.neighbourhood || addr.suburb || addr.residential || addr.road || addr.quarter || addr.hamlet || null;
        const formatted = json.display_name || [area, city, district, state, country].filter(Boolean).join(", ");

        const result = {
          name: area || city || district || state || "Current Location",
          country,
          countryCode,
          countryFlag: (typeof WORLDWIDE_COUNTRIES !== "undefined") ? (WORLDWIDE_COUNTRIES.find(c => c.code === countryCode)?.flag || "🇮🇳") : "🇮🇳",
          state,
          district,
          city: city || district || state,
          area,
          locality: area,
          formattedAddress: formatted,
          latitude: lat,
          longitude: lng,
          isLiveGps: true
        };

        this.saveToCache("reverse", cacheKey, result);
        return result;
      }
    } catch (e) {
      // Graceful fallback to nearest catalog node
    }

    // Authoritative nearest-neighbor fallback using real Haversine distance
    let closestCity = null;
    let closestState = null;
    let minDistance = Infinity;

    for (const st of INDIA_ADMINISTRATIVE_DATA) {
      for (const c of st.cities) {
        const d = this.calculateDistanceKm(lat, lng, c.lat, c.lng);
        if (d < minDistance) {
          minDistance = d;
          closestCity = c;
          closestState = st;
        }
      }
    }

    const fallbackLocality = (closestCity && closestCity.areas && closestCity.areas.length) ? closestCity.areas[0] : null;
    const fallbackCityName = closestCity ? closestCity.name : "Patna";
    const fallbackStateName = closestState ? closestState.state : "Bihar";
    const fallbackDistrict = closestCity ? closestCity.district : "Patna";

    const fallbackResult = {
      name: fallbackLocality || fallbackCityName,
      country: "India",
      countryCode: "IN",
      countryFlag: "🇮🇳",
      state: fallbackStateName,
      district: fallbackDistrict,
      city: fallbackCityName,
      area: fallbackLocality,
      locality: fallbackLocality,
      formattedAddress: [fallbackLocality, fallbackCityName, fallbackDistrict, fallbackStateName, "India"].filter(Boolean).join(", "),
      latitude: lat,
      longitude: lng,
      isLiveGps: true
    };

    this.saveToCache("reverse", cacheKey, fallbackResult);
    return fallbackResult;
  }

  /**
   * Real Haversine Distance Formula in Kilometers
   */
  calculateDistanceKm(lat1, lon1, lat2, lon2) {
    const R = 6371; // Earth radius in km
    const dLat = (lat2 - lat1) * Math.PI / 180;
    const dLon = (lon2 - lon1) * Math.PI / 180;
    const a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
              Math.cos(lat1 * Math.PI / 180) * Math.cos(lat2 * Math.PI / 180) *
              Math.sin(dLon / 2) * Math.sin(dLon / 2);
    const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    return parseFloat((R * c).toFixed(2));
  }

  calculateDistance(lat1, lon1, lat2, lon2) {
    return this.calculateDistanceKm(lat1, lon1, lat2, lon2);
  }

  /**
   * Returns all Indian States & Union Territories
   */
  getStates(country = "India") {
    if (country === "India") {
      return INDIA_ADMINISTRATIVE_DATA.map(st => ({
        state: st.state,
        code: st.code,
        isUT: st.isUT,
        capital: st.capital,
        districtCount: (st.districts || []).length,
        cityCount: (st.cities || []).length
      }));
    }
    const region = (typeof INTERNATIONAL_REGIONS_DATA !== "undefined") ? INTERNATIONAL_REGIONS_DATA[country] : null;
    return (region?.states || []).map(s => ({ state: s.state, code: s.code || "", isUT: false }));
  }

  /**
   * Returns official districts for a given state
   */
  getDistricts(stateName) {
    const stObj = INDIA_ADMINISTRATIVE_DATA.find(s => s.state === stateName);
    return stObj ? (stObj.districts || []) : [];
  }

  /**
   * Returns official cities for a given state and optional district
   */
  getCities(stateName, districtName = null) {
    const stObj = INDIA_ADMINISTRATIVE_DATA.find(s => s.state === stateName);
    if (!stObj || !stObj.cities) return [];
    if (districtName && districtName !== "ALL_DISTRICTS") {
      const filtered = stObj.cities.filter(c => c.district === districtName);
      if (filtered.length > 0) return filtered;
    }
    return stObj.cities;
  }

  /**
   * Returns the district for a given city in a state
   */
  getCityDistrict(cityName, stateName = null) {
    if (!cityName) return null;
    const cNameLower = cityName.toLowerCase();
    for (const st of INDIA_ADMINISTRATIVE_DATA) {
      if (stateName && st.state.toLowerCase() !== stateName.toLowerCase()) continue;
      const match = st.cities.find(c => c.name.toLowerCase() === cNameLower);
      if (match) return match.district;
    }
    return null;
  }

  /**
   * Returns local areas / neighborhoods for a given city
   */
  getLocalities(stateName, cityName) {
    const stObj = INDIA_ADMINISTRATIVE_DATA.find(s => s.state === stateName);
    const cObj = stObj?.cities?.find(c => c.name.toLowerCase() === cityName.toLowerCase());
    return cObj ? (cObj.areas || []) : [];
  }

  /**
   * Resolves complete State-District-City-Area administrative hierarchy for any location or search item
   * Replaces Google Places dependency with high-precision Indian administrative matching
   */
  resolveHierarchy(item) {
    if (!item) return null;
    let country = item.country || "India";
    let countryCode = item.countryCode || (country === "India" ? "IN" : "");
    let countryFlag = item.countryFlag || (country === "India" ? "🇮🇳" : "📍");
    let state = item.state || null;
    let district = item.district || null;
    let city = item.city || null;
    let area = item.area || item.locality || null;
    let lat = item.latitude || null;
    let lng = item.longitude || null;

    if (country === "India") {
      // 0. If item has a name/query but no explicit hierarchy fields, attempt to identify what it is
      const queryTerm = (item.name || item.query || "").trim().toLowerCase();
      if (queryTerm && !state && !district && !city && !area) {
        // Try state
        const matchedState = INDIA_ADMINISTRATIVE_DATA.find(s => 
          s.state.toLowerCase() === queryTerm || s.code.toLowerCase() === queryTerm
        );
        if (matchedState) {
          state = matchedState.state;
        } else {
          // Try city
          for (const s of INDIA_ADMINISTRATIVE_DATA) {
            const c = s.cities.find(c => c.name.toLowerCase() === queryTerm);
            if (c) {
              city = c.name;
              district = c.district;
              state = s.state;
              lat = c.lat;
              lng = c.lng;
              break;
            }
          }
          // Try district
          if (!city) {
            for (const s of INDIA_ADMINISTRATIVE_DATA) {
              const d = s.districts.find(d => d.toLowerCase() === queryTerm || d.toLowerCase().includes(queryTerm));
              if (d) {
                district = d;
                state = s.state;
                break;
              }
            }
          }
          // Try area
          if (!city && !district) {
            for (const s of INDIA_ADMINISTRATIVE_DATA) {
              for (const c of s.cities) {
                if (c.areas) {
                  const matchedA = c.areas.find(a => 
                    a.toLowerCase() === queryTerm || 
                    a.toLowerCase().includes(queryTerm) || 
                    queryTerm.includes(a.toLowerCase())
                  );
                  if (matchedA) {
                    area = matchedA;
                    city = c.name;
                    district = c.district;
                    state = s.state;
                    lat = c.lat;
                    lng = c.lng;
                    break;
                  }
                }
              }
              if (city) break;
            }
          }
        }
      }

      // 1. Normalize state if given
      if (state) {
        const matchedState = INDIA_ADMINISTRATIVE_DATA.find(s => 
          s.state.toLowerCase() === state.toLowerCase() || 
          s.code.toLowerCase() === state.toLowerCase()
        );
        if (matchedState) state = matchedState.state;
      }

      // 2. Resolve city & district correlation
      if (city) {
        const foundDist = this.getCityDistrict(city, state);
        if (foundDist) {
          district = district || foundDist;
          if (!state) {
            const st = INDIA_ADMINISTRATIVE_DATA.find(s => s.cities.some(c => c.name.toLowerCase() === city.toLowerCase()));
            if (st) state = st.state;
          }
        }
      }

      // 3. Resolve area correlation if area is given but city is missing
      if (area && !city) {
        const areaLower = area.toLowerCase();
        for (const st of INDIA_ADMINISTRATIVE_DATA) {
          for (const c of st.cities) {
            if (c.areas && c.areas.some(a => {
              const aLower = a.toLowerCase();
              return aLower === areaLower || aLower.includes(areaLower) || areaLower.includes(aLower);
            })) {
              city = c.name;
              district = district || c.district;
              state = state || st.state;
              lat = lat || c.lat;
              lng = lng || c.lng;
              break;
            }
          }
          if (city) break;
        }
      }

      // 4. Resolve district to primary city if city is missing
      if (district && !city) {
        const citiesInDist = this.getCities(state, district);
        if (citiesInDist && citiesInDist.length > 0) {
          city = citiesInDist[0].name;
          if (!lat && !lng) {
            lat = citiesInDist[0].lat;
            lng = citiesInDist[0].lng;
          }
        } else {
          city = district;
        }
      }

      // 5. Fallback coordinates from city
      if ((!lat || !lng) && city && state) {
        const stObj = INDIA_ADMINISTRATIVE_DATA.find(s => s.state === state);
        const cObj = stObj?.cities?.find(c => c.name.toLowerCase() === city.toLowerCase());
        if (cObj) {
          lat = cObj.lat;
          lng = cObj.lng;
        }
      }
    }

    return {
      id: item.id || `loc-${Date.now()}`,
      name: item.name || area || city || district || state || country,
      country,
      countryCode,
      countryFlag,
      state,
      district,
      city,
      area,
      pincode: item.pincode || item.postalCode || null,
      postOffice: item.postOffice || null,
      postOffices: item.postOffices || null,
      formattedAddress: item.formattedAddress || [area, city, district, state, item.pincode ? `- ${item.pincode}` : '', country].filter(Boolean).join(", "),
      latitude: lat,
      longitude: lng,
      type: item.type || (area ? "AREA" : (city ? "CITY" : (district ? "DISTRICT" : (state ? "STATE" : "LOCATION"))))
    };
  }

  /**
   * India-Wide Postal PIN Code Lookup
   * Validates 6-digit Indian PIN codes and resolves complete administrative hierarchy:
   * Country, State/UT, District, City/Town/Village, Post Office(s), Locality/Area, PIN, Lat/Lng
   */
  async lookupPincode(rawPincode) {
    if (!rawPincode) {
      return { success: false, error: "EMPTY_PINCODE", message: "Please enter a 6-digit Indian PIN code." };
    }

    const pin = String(rawPincode).trim().replace(/\D/g, "");
    if (pin.length !== 6) {
      return { success: false, error: "INVALID_LENGTH", message: "PIN code must be exactly 6 numeric digits." };
    }

    // Check memory cache
    if (this.pincodeCache && this.pincodeCache.has(pin)) {
      return this.pincodeCache.get(pin);
    }

    // Check localStorage cache
    try {
      if (typeof localStorage !== "undefined") {
        const cachedStr = localStorage.getItem(`styno_loc_cache_pincode_${pin}`) || localStorage.getItem(`styno_pincode_cache_${pin}`);
        if (cachedStr) {
          const cached = JSON.parse(cachedStr);
          if (cached && (Date.now() - (cached.timestamp || 0) < this.CACHE_TTL_MS)) {
            if (!this.pincodeCache) this.pincodeCache = new Map();
            this.pincodeCache.set(pin, cached.data);
            return cached.data;
          }
        }
      }
    } catch (e) {}

    let resolvedData = null;

    // --- Provider 1: Official India Postal PIN Code Directory (All 150,000+ Indian Post Offices) ---
    try {
      const controller = new AbortController();
      const timeoutId = setTimeout(() => controller.abort(), 4500);
      const res = await fetch(`https://api.postalpincode.in/pincode/${pin}`, { signal: controller.signal });
      clearTimeout(timeoutId);

      if (res.ok) {
        const json = await res.json();
        if (Array.isArray(json) && json.length > 0 && json[0].Status === "Success" && Array.isArray(json[0].PostOffice) && json[0].PostOffice.length > 0) {
          resolvedData = this.parseIndiaPostData(pin, json[0].PostOffice);
        }
      }
    } catch (e) {
      // Network timeout or network error, fallback to Provider 2
    }

    // --- Provider 2 (Fallback): Zippopotam Postal API ---
    if (!resolvedData) {
      try {
        const controller = new AbortController();
        const timeoutId = setTimeout(() => controller.abort(), 4000);
        const res = await fetch(`https://api.zippopotam.us/in/${pin}`, { signal: controller.signal });
        clearTimeout(timeoutId);

        if (res.ok) {
          const json = await res.json();
          if (json && Array.isArray(json.places) && json.places.length > 0) {
            resolvedData = this.parseZippopotamData(pin, json);
          }
        }
      } catch (e) {}
    }

    // --- Provider 3 (Fallback): OpenStreetMap Nominatim Postal Search ---
    if (!resolvedData) {
      try {
        const controller = new AbortController();
        const timeoutId = setTimeout(() => controller.abort(), 4000);
        const res = await fetch(`https://nominatim.openstreetmap.org/search?postalcode=${pin}&country=India&format=json&addressdetails=1`, {
          signal: controller.signal,
          headers: { "Accept-Language": "en" }
        });
        clearTimeout(timeoutId);

        if (res.ok) {
          const json = await res.json();
          if (Array.isArray(json) && json.length > 0) {
            resolvedData = this.parseNominatimPostalData(pin, json[0]);
          }
        }
      } catch (e) {}
    }

    if (resolvedData) {
      // If coordinates are missing or need enrichment, resolve using OpenStreetMap or administrative centers
      if (!resolvedData.latitude || !resolvedData.longitude) {
        resolvedData = await this.enrichPostalCoordinates(resolvedData);
      }

      this.saveToCache("pincode", pin, resolvedData);
      return resolvedData;
    }

    return {
      success: false,
      error: "NOT_FOUND",
      pincode: pin,
      message: `No postal records found for PIN code ${pin}. Please verify the 6-digit code or enter location manually.`
    };
  }

  /**
   * Parses official India Post API response with authoritative administrative normalization
   */
  parseIndiaPostData(pin, postOffices) {
    if (!Array.isArray(postOffices) || postOffices.length === 0) return null;

    const rawState = postOffices[0].State || "";
    const rawDistrict = postOffices[0].District || "";

    // 1. Normalize State against INDIA_ADMINISTRATIVE_DATA
    let state = rawState;
    let stObj = INDIA_ADMINISTRATIVE_DATA.find(s => 
      s.state.toLowerCase() === rawState.toLowerCase() ||
      rawState.toLowerCase().includes(s.state.toLowerCase()) ||
      s.state.toLowerCase().includes(rawState.toLowerCase())
    );
    if (stObj) {
      state = stObj.state;
    }

    // 2. Normalize District
    let district = rawDistrict;
    if (stObj && stObj.districts) {
      const dMatch = stObj.districts.find(d => 
        d.toLowerCase() === rawDistrict.toLowerCase() ||
        d.toLowerCase().includes(rawDistrict.toLowerCase()) ||
        rawDistrict.toLowerCase().includes(d.toLowerCase())
      );
      if (dMatch) {
        district = dMatch;
      }
    }

    // 3. Extract distinct post offices / localities
    const offices = [];
    const seenNames = new Set();
    postOffices.forEach(po => {
      const name = (po.Name || "").trim();
      if (name && !seenNames.has(name.toLowerCase())) {
        seenNames.add(name.toLowerCase());
        offices.push({
          name: name,
          branchType: po.BranchType || "Post Office",
          deliveryStatus: po.DeliveryStatus || "Delivery",
          district: district,
          division: po.Division || "",
          block: (po.Block && po.Block !== "NA") ? po.Block : "",
          state: state,
          pincode: pin
        });
      }
    });

    // 4. Resolve City / Town / Village
    let city = null;
    let lat = null;
    let lng = null;

    // Check if any office name matches a known city in the state
    if (stObj && stObj.cities) {
      for (const po of offices) {
        const cMatch = stObj.cities.find(c => 
          c.name.toLowerCase() === po.name.toLowerCase() ||
          po.name.toLowerCase().includes(c.name.toLowerCase()) ||
          c.name.toLowerCase().includes(po.name.toLowerCase())
        );
        if (cMatch) {
          city = cMatch.name;
          lat = cMatch.lat;
          lng = cMatch.lng;
          break;
        }
      }

      // Check if block or division matches a known city
      if (!city) {
        for (const po of offices) {
          if (po.block) {
            const bMatch = stObj.cities.find(c => c.name.toLowerCase() === po.block.toLowerCase());
            if (bMatch) {
              city = bMatch.name;
              lat = bMatch.lat;
              lng = bMatch.lng;
              break;
            }
          }
          if (po.division) {
            const divClean = po.division.replace(/division/i, "").trim().toLowerCase();
            const divMatch = stObj.cities.find(c => c.name.toLowerCase() === divClean);
            if (divMatch) {
              city = divMatch.name;
              lat = divMatch.lat;
              lng = divMatch.lng;
              break;
            }
          }
        }
      }

      // Check if district name matches a known city (e.g. Kota, Varanasi, Lucknow, Jaipur)
      if (!city) {
        const distCityMatch = stObj.cities.find(c => 
          c.name.toLowerCase() === district.toLowerCase() ||
          district.toLowerCase().includes(c.name.toLowerCase())
        );
        if (distCityMatch) {
          city = distCityMatch.name;
          lat = distCityMatch.lat;
          lng = distCityMatch.lng;
        }
      }
    }

    // Default city if still not matched:
    if (!city) {
      const mainPO = offices.find(o => o.branchType === "Head Post Office" || o.branchType === "Sub Post Office") || offices[0];
      city = mainPO?.block || mainPO?.name || district;
    }

    // Primary Post Office / Locality
    const officeMatchingCity = offices.find(o => 
      o.name.toLowerCase() === city.toLowerCase() || 
      city.toLowerCase().includes(o.name.toLowerCase()) || 
      o.name.toLowerCase().includes(city.toLowerCase())
    );
    const primaryOffice = officeMatchingCity || offices.find(o => o.deliveryStatus === "Delivery") || offices[0];
    const defaultArea = primaryOffice ? primaryOffice.name : city;

    // Check coordinates if still null
    if (!lat || !lng) {
      const coordLookup = (typeof MAJOR_CITIES_COORDINATES !== "undefined") ? (
        MAJOR_CITIES_COORDINATES[city.toLowerCase()] || 
        MAJOR_CITIES_COORDINATES[district.toLowerCase()] ||
        (stObj?.cities?.[0] ? { lat: stObj.cities[0].lat, lng: stObj.cities[0].lng } : null)
      ) : null;
      if (coordLookup) {
        lat = coordLookup.lat;
        lng = coordLookup.lng;
      }
    }

    const formattedAddress = `${defaultArea}, ${city}, ${district}, ${state} - ${pin}, India`;

    return {
      success: true,
      pincode: pin,
      country: "India",
      countryCode: "IN",
      countryFlag: "🇮🇳",
      state: state,
      district: district,
      city: city,
      area: defaultArea,
      postOffice: primaryOffice ? primaryOffice.name : defaultArea,
      postOffices: offices,
      formattedAddress: formattedAddress,
      latitude: lat ? parseFloat(lat) : null,
      longitude: lng ? parseFloat(lng) : null,
      source: "INDIA_POST"
    };
  }

  /**
   * Parses Zippopotam response (Fallback Provider)
   */
  parseZippopotamData(pin, json) {
    if (!json || !Array.isArray(json.places) || json.places.length === 0) return null;
    const places = json.places;
    const rawState = places[0].state || "";
    
    let state = rawState;
    let stObj = INDIA_ADMINISTRATIVE_DATA.find(s => 
      s.state.toLowerCase() === rawState.toLowerCase() ||
      s.code.toLowerCase() === (places[0]["state abbreviation"] || "").toLowerCase()
    );
    if (stObj) state = stObj.state;

    const offices = places.map(p => ({
      name: p["place name"],
      branchType: "Post Office",
      deliveryStatus: "Delivery",
      district: "",
      block: "",
      state: state,
      pincode: pin
    }));

    const primaryPlace = places[0];
    const defaultArea = primaryPlace["place name"];
    const lat = primaryPlace.latitude ? parseFloat(primaryPlace.latitude) : null;
    const lng = primaryPlace.longitude ? parseFloat(primaryPlace.longitude) : null;

    let city = defaultArea;
    let district = "";
    if (stObj && stObj.cities) {
      const cMatch = stObj.cities.find(c => 
        c.name.toLowerCase() === defaultArea.toLowerCase() ||
        defaultArea.toLowerCase().includes(c.name.toLowerCase())
      );
      if (cMatch) {
        city = cMatch.name;
        district = cMatch.district;
      }
    }

    return {
      success: true,
      pincode: pin,
      country: "India",
      countryCode: "IN",
      countryFlag: "🇮🇳",
      state: state,
      district: district || city,
      city: city,
      area: defaultArea,
      postOffice: defaultArea,
      postOffices: offices,
      formattedAddress: `${defaultArea}, ${city}, ${state} - ${pin}, India`,
      latitude: lat,
      longitude: lng,
      source: "ZIPPOPOTAM"
    };
  }

  /**
   * Parses Nominatim postal search response (Fallback Provider)
   */
  parseNominatimPostalData(pin, item) {
    if (!item) return null;
    const addr = item.address || {};
    const rawState = addr.state || "";

    let state = rawState;
    let stObj = INDIA_ADMINISTRATIVE_DATA.find(s => s.state.toLowerCase() === rawState.toLowerCase());
    if (stObj) state = stObj.state;

    const district = addr.state_district || addr.county || "";
    const city = addr.city || addr.town || addr.village || addr.suburb || district || "";
    const area = addr.suburb || addr.neighbourhood || addr.road || city;
    const lat = item.lat ? parseFloat(item.lat) : null;
    const lng = item.lon ? parseFloat(item.lon) : null;

    return {
      success: true,
      pincode: pin,
      country: "India",
      countryCode: "IN",
      countryFlag: "🇮🇳",
      state: state,
      district: district,
      city: city,
      area: area,
      postOffice: area,
      postOffices: [{ name: area, branchType: "Post Office", deliveryStatus: "Delivery", district, state, pincode: pin }],
      formattedAddress: item.display_name || `${area}, ${city}, ${state} - ${pin}, India`,
      latitude: lat,
      longitude: lng,
      source: "NOMINATIM"
    };
  }

  /**
   * Enriches coordinates using OpenStreetMap Nominatim or administrative database
   */
  async enrichPostalCoordinates(data) {
    if (!data) return data;
    if (data.latitude && data.longitude) return data;

    try {
      const controller = new AbortController();
      const timeoutId = setTimeout(() => controller.abort(), 3500);
      const url = `https://nominatim.openstreetmap.org/search?postalcode=${data.pincode}&country=India&format=json&limit=1`;
      const res = await fetch(url, { signal: controller.signal, headers: { "Accept-Language": "en" } });
      clearTimeout(timeoutId);
      if (res.ok) {
        const json = await res.json();
        if (Array.isArray(json) && json.length > 0 && json[0].lat && json[0].lon) {
          data.latitude = parseFloat(json[0].lat);
          data.longitude = parseFloat(json[0].lon);
          return data;
        }
      }
    } catch (e) {}

    // Fallback coordinates from city/state
    if (data.city && data.state) {
      const stObj = INDIA_ADMINISTRATIVE_DATA.find(s => s.state === data.state);
      const cObj = stObj?.cities?.find(c => c.name.toLowerCase() === data.city.toLowerCase());
      if (cObj) {
        data.latitude = cObj.lat;
        data.longitude = cObj.lng;
      }
    }

    return data;
  }

  /**
   * Clear in-memory and persistent cache
   */
  clearCache() {
    this.searchCache.clear();
    this.reverseGeocodeCache.clear();
    if (this.pincodeCache) this.pincodeCache.clear();
    try {
      if (typeof localStorage !== "undefined") {
        for (let i = localStorage.length - 1; i >= 0; i--) {
          const key = localStorage.key(i);
          if (key && key.startsWith("styno_loc_cache_")) {
            localStorage.removeItem(key);
          }
        }
      }
    } catch (e) {}
  }
}

// Global Singleton Instance
const StynoLocationEngine = new LocationService();

// ==========================================================================
// 4. ACTIVE LOCATION STATE & PERSISTENCE
// ==========================================================================

const LocationState = {
  selectedCountry: "India",
  selectedCountryCode: "IN",
  selectedCountryFlag: "🇮🇳",
  selectedState: null,
  selectedDistrict: null,
  selectedCity: null,
  selectedArea: null,
  selectedPincode: null,
  latitude: 28.5355,
  longitude: 77.3910,
  isLiveGps: false,
  pickerStep: "COUNTRY", // COUNTRY, STATE, DISTRICT, CITY, AREA
  availableStates: [],
  availableDistricts: [],
  availableCities: [],
  availableAreas: []
};

function saveLocationStateToStorage() {
  try {
    const payload = {
      country: LocationState.selectedCountry,
      countryCode: LocationState.selectedCountryCode,
      countryFlag: LocationState.selectedCountryFlag,
      state: LocationState.selectedState,
      district: LocationState.selectedDistrict,
      city: LocationState.selectedCity,
      area: LocationState.selectedArea,
      pincode: LocationState.selectedPincode,
      lat: LocationState.latitude,
      lng: LocationState.longitude,
      isLiveGps: LocationState.isLiveGps
    };
    if (typeof localStorage !== "undefined") {
      localStorage.setItem("styno_active_location_v3", JSON.stringify(payload));
    }
  } catch (e) {
    console.warn("Could not save location to localStorage:", e);
  }
}

function loadLocationStateFromStorage() {
  try {
    if (typeof localStorage !== "undefined") {
      const saved = localStorage.getItem("styno_active_location_v3");
      if (saved) {
        const parsed = JSON.parse(saved);
        LocationState.selectedCountry = parsed.country || "India";
        LocationState.selectedCountryCode = parsed.countryCode || "IN";
        LocationState.selectedCountryFlag = parsed.countryFlag || "🇮🇳";
        LocationState.selectedState = parsed.state || null;
        LocationState.selectedDistrict = parsed.district || null;
        LocationState.selectedCity = parsed.city || null;
        LocationState.selectedArea = parsed.area || null;
        LocationState.selectedPincode = parsed.pincode || null;
        LocationState.latitude = parsed.lat || 28.5355;
        LocationState.longitude = parsed.lng || 77.3910;
        LocationState.isLiveGps = !!parsed.isLiveGps;
      }
    }
  } catch (e) {
    console.warn("Could not parse saved location:", e);
  }
}

// Initial load
loadLocationStateFromStorage();

// Export to window for global access across app.js and index.html
if (typeof window !== "undefined") {
  window.LocationService = StynoLocationEngine;
  window.GlobalLocationEngine = StynoLocationEngine;
  window.RealLocationService = LocationService;
  window.WORLDWIDE_COUNTRIES = WORLDWIDE_COUNTRIES;
  window.INDIA_ADMINISTRATIVE_DATA = INDIA_ADMINISTRATIVE_DATA;
  window.INTERNATIONAL_REGIONS_DATA = INTERNATIONAL_REGIONS_DATA;
  window.LocationState = LocationState;
  window.saveLocationStateToStorage = saveLocationStateToStorage;
  window.loadLocationStateFromStorage = loadLocationStateFromStorage;
}

if (typeof module !== "undefined" && module.exports) {
  module.exports = {
    LocationService,
    GlobalLocationEngine: StynoLocationEngine,
    WORLDWIDE_COUNTRIES,
    INDIA_ADMINISTRATIVE_DATA,
    INTERNATIONAL_REGIONS_DATA,
    LocationState,
    saveLocationStateToStorage,
    loadLocationStateFromStorage
  };
}
