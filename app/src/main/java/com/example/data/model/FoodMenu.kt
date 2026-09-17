package com.example.data.model

data class FoodMenuItem(
    val id: String,
    val name: String,
    val description: String = "",
    val price: Double,
    val isVeg: Boolean = true,
    val category: String = "Main Course", // "Breakfast", "Lunch", "Dinner", "Snacks", "Beverages", "Dessert"
    val availabilityTiming: String = "All Day (8 AM - 10 PM)",
    val isAvailable: Boolean = true,
    val photoUrl: String = "",
    val allowsRoomDelivery: Boolean = true
)

data class DailyMeal(
    val mealType: String,       // "Breakfast", "Lunch", "Evening Snacks", "Dinner"
    val timing: String,         // "7:30 AM - 9:30 AM"
    val items: List<String>,    // ["Aloo Paratha & Curd", "Masala Chai", "Sprouts / Banana", "Butter & Pickle"]
    val isVeg: Boolean = true,
    val specialNote: String = "Unlimited serving"
)

data class FoodMenu(
    val propertyId: String,
    val todayDate: String = "Today's Fresh Menu",
    val meals: List<DailyMeal> = listOf(
        DailyMeal(
            mealType = "Breakfast",
            timing = "7:30 AM - 9:45 AM",
            items = listOf("Hot Poha with Sev & Lemon", "Boiled Eggs / Sprouts", "Masala Chai / Filter Coffee", "Bread Toast & Jam"),
            isVeg = false,
            specialNote = "Fresh hot breakfast buffet"
        ),
        DailyMeal(
            mealType = "Lunch",
            timing = "12:30 PM - 2:30 PM",
            items = listOf("Shahi Paneer", "Dal Tadka", "Steamed Basmati Rice", "Tawa Butter Roti (Unlimited)", "Boondi Raita", "Green Salad"),
            isVeg = true,
            specialNote = "Pure desi ghee preparation"
        ),
        DailyMeal(
            mealType = "Evening Snacks",
            timing = "5:00 PM - 6:30 PM",
            items = listOf("Veg Pakoda / Samosa", "Ginger Cardamom Tea", "Biscuits"),
            isVeg = true,
            specialNote = "Evening refreshment"
        ),
        DailyMeal(
            mealType = "Dinner",
            timing = "7:45 PM - 10:00 PM",
            items = listOf("Mix Vegetable Korma", "Rajma / Dal Makhani", "Jeera Rice", "Phulkas", "Gulab Jamun (Sweet)"),
            isVeg = true,
            specialNote = "Sunday special dinner available"
        )
    ),
    val aLaCarteItems: List<FoodMenuItem> = listOf(
        FoodMenuItem("1", "Paneer Butter Masala", "Cottage cheese cubes in rich spiced butter gravy", 180.0, isVeg = true, category = "Main Course"),
        FoodMenuItem("2", "Dal Makhani with Jeera Rice", "Slow cooked black lentils with aromatic cumin rice", 150.0, isVeg = true, category = "Main Course"),
        FoodMenuItem("3", "Egg Curry (2 Eggs) with Rotis", "Homestyle spiced egg curry served with 3 hot phulkas", 140.0, isVeg = false, category = "Main Course"),
        FoodMenuItem("4", "Chicken Biryani with Raita", "Dum cooked spiced chicken basmati rice", 220.0, isVeg = false, category = "Main Course"),
        FoodMenuItem("5", "Cold Coffee / Fresh Juice", "Chilled whipped coffee or seasonal fruit juice", 60.0, isVeg = true, category = "Beverages"),
        FoodMenuItem("6", "Aloo Paratha with Curd & Butter", "2 large stuffed tandoor parathas", 90.0, isVeg = true, category = "Breakfast")
    ),
    val hygieneScore: Double = 4.9,
    val fssaiCertified: Boolean = true,
    val monthlyFoodIncluded: Boolean = true,
    val optionalMealPrice: Double = 120.0,
    val diningHallRules: String = "Clean cutlery provided. RO water stations available at dining tables.",
    val offersRoomService: Boolean = true,
    val offersPropertyDelivery: Boolean = true
)
