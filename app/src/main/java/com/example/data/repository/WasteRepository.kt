package com.example.data.repository

import android.content.Context
import android.graphics.Bitmap
import com.example.data.api.GeminiApiService
import com.example.data.local.AppDatabase
import com.example.data.local.WasteScanDao
import com.example.data.local.WasteScanEntity
import com.example.data.model.LeaderboardUser
import com.example.data.model.RecyclingCenter
import com.example.data.model.SmartBinNode
import com.example.data.model.WasteBinType
import com.example.data.model.WasteClassification
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class WasteRepository(context: Context) {
  private val database = AppDatabase.getDatabase(context)
  private val dao: WasteScanDao = database.wasteScanDao()

  val allScans: Flow<List<WasteScanEntity>> = dao.getAllScans()
  val totalPoints: Flow<Int> = dao.getTotalPoints().map { (it ?: 0) + 120 } // Base starter bonus + earned
  val totalScansCount: Flow<Int> = dao.getTotalCount()

  suspend fun classifyImage(bitmap: Bitmap, contextHint: String = ""): WasteClassification {
    val result = GeminiApiService.classifyWasteImage(bitmap, contextHint)
    return result.getOrElse {
      GeminiApiService.classifyFallback(bitmap, contextHint)
    }
  }

  suspend fun recordScan(classification: WasteClassification, imageUri: String? = null): Long {
    val entity = WasteScanEntity(
      objectName = classification.objectName,
      category = classification.category,
      binType = classification.binType.name,
      confidence = classification.confidence,
      shapeFeature = classification.shapeFeature,
      textureFeature = classification.textureFeature,
      colorFeature = classification.colorFeature,
      recyclingGuidance = classification.recyclingGuidance,
      environmentalImpact = classification.environmentalImpact,
      rewardPoints = classification.rewardPoints,
      timestamp = classification.timestamp,
      imageUri = imageUri
    )
    return dao.insertScan(entity)
  }

  suspend fun deleteScan(id: Int) {
    dao.deleteScanById(id)
  }

  suspend fun clearHistory() {
    dao.clearAll()
  }

  fun getCommunityLeaderboard(userPoints: Int): List<LeaderboardUser> {
    val communityList = listOf(
      LeaderboardUser(1, "Elena Vance", 1240, 14, "Eco Guardian", false, "EV"),
      LeaderboardUser(2, "Marcus Chen", 980, 11, "Zero-Waste Champion", false, "MC"),
      LeaderboardUser(3, "Priya Sharma", 840, 9, "Recycle Specialist", false, "PS"),
      LeaderboardUser(4, "Kenji Sato", 620, 7, "Green Pioneer", false, "KS"),
      LeaderboardUser(5, "Sophia Miller", 480, 5, "Eco Scout", false, "SM"),
      LeaderboardUser(6, "Liam O'Connor", 310, 3, "Eco Scout", false, "LO"),
      LeaderboardUser(7, "Amina Yusuf", 210, 2, "Novice Recycler", false, "AY")
    )

    // Insert user into list and sort by points
    val userEntry = LeaderboardUser(
      rank = 0,
      name = "You (Eco Warrior)",
      points = userPoints,
      streakDays = maxOf(1, (userPoints / 50)),
      badge = when {
        userPoints >= 1000 -> "Eco Guardian"
        userPoints >= 600 -> "Zero-Waste Champion"
        userPoints >= 300 -> "Recycle Specialist"
        else -> "Eco Scout"
      },
      isCurrentUser = true,
      avatarInitials = "ME"
    )

    val combined = (communityList + userEntry).sortedByDescending { it.points }
    return combined.mapIndexed { index, user ->
      user.copy(rank = index + 1)
    }
  }

  fun getInitialSmartBins(): List<SmartBinNode> {
    return listOf(
      SmartBinNode(
        id = "BIN-01-RECYCLE",
        name = "Blue Recyclables Bin",
        binType = WasteBinType.RECYCLABLE_BLUE,
        fillPercentage = 42,
        isLidOpen = false,
        batteryLevel = 94,
        status = "Online (BLE Mesh - Sensor Active)"
      ),
      SmartBinNode(
        id = "BIN-02-ORGANIC",
        name = "Green Compost Bin",
        binType = WasteBinType.ORGANIC_GREEN,
        fillPercentage = 68,
        isLidOpen = false,
        batteryLevel = 88,
        status = "Online (Ultrasonic Fill Active)"
      ),
      SmartBinNode(
        id = "BIN-03-EWASTE",
        name = "Yellow E-Waste Kiosk",
        binType = WasteBinType.E_WASTE_YELLOW,
        fillPercentage = 23,
        isLidOpen = false,
        batteryLevel = 98,
        status = "Online (Locking Solenoid Ready)"
      ),
      SmartBinNode(
        id = "BIN-04-LANDFILL",
        name = "Grey Landfill Residuals",
        binType = WasteBinType.LANDFILL_GREY,
        fillPercentage = 81,
        isLidOpen = false,
        batteryLevel = 85,
        status = "Online (Collection Alert Near)"
      )
    )
  }

  fun getAllRecyclingCenters(): List<RecyclingCenter> = Companion.getAllRecyclingCenters()

  fun calculateDistanceMiles(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double =
    Companion.calculateDistanceMiles(lat1, lon1, lat2, lon2)

  fun getSuggestedCenterFor(
    classification: WasteClassification,
    userLat: Double,
    userLon: Double
  ): Pair<RecyclingCenter, Double> =
    Companion.getSuggestedCenterFor(classification, userLat, userLon)

  companion object {
    fun getAllRecyclingCenters(): List<RecyclingCenter> {
    return listOf(
      RecyclingCenter(
        id = "CENTER-01",
        name = "Verdant Hills Eco-Recovery Facility",
        categoryTag = "Full-Stream Material Recovery (MRF)",
        address = "740 Eco Vista Way, Central District",
        latitude = 37.7790,
        longitude = -122.4140,
        primarySpecialty = WasteBinType.RECYCLABLE_BLUE,
        acceptedWasteTypes = listOf(
          WasteBinType.RECYCLABLE_BLUE,
          WasteBinType.ORGANIC_GREEN,
          WasteBinType.LANDFILL_GREY
        ),
        acceptedMaterials = listOf(
          "PET & HDPE Plastics (#1, #2, #5)",
          "Corrugated Cardboard & Paper",
          "Aluminum Beverage & Food Cans",
          "Glass Jars & Clean Bottles",
          "Flattened Steel Containers"
        ),
        operatingHours = "Mon–Sat: 7:30 AM – 6:00 PM • Sun: 9:00 AM – 4:00 PM",
        isOpenNow = true,
        scheduleByDay = listOf(
          "Mon – Fri" to "7:30 AM – 6:00 PM",
          "Saturday" to "8:00 AM – 5:00 PM",
          "Sunday" to "9:00 AM – 4:00 PM"
        ),
        preparationInstructions = listOf(
          "Empty and rinse all beverage & food containers clean with water",
          "Flatten corrugated cardboard boxes into bundles under 35 lbs",
          "Remove plastic caps and loose adhesive shipping tapes",
          "Keep paper dry and separate from greasy food cartons"
        ),
        phoneNumber = "(555) 382-9400",
        rating = 4.9f,
        reviewsCount = 312,
        isMunicipalCertified = true,
        hasDriveThrough = true,
        acceptsBulkyItems = true,
        notes = "Automated drive-through weighbridge available. Free paper shredding every Wednesday."
      ),
      RecyclingCenter(
        id = "CENTER-02",
        name = "Metro Electronics & Hazardous Disposal Depot",
        categoryTag = "Certified Hazardous & E-Waste Station",
        address = "1420 Industrial Way, North Sector",
        latitude = 37.7885,
        longitude = -122.4080,
        primarySpecialty = WasteBinType.HAZARDOUS_RED,
        acceptedWasteTypes = listOf(
          WasteBinType.HAZARDOUS_RED,
          WasteBinType.E_WASTE_YELLOW
        ),
        acceptedMaterials = listOf(
          "Lithium-Ion, Alkaline & NiCd Batteries",
          "Household Paints, Solvents & Stains",
          "Fluorescent Tubes & CFL Bulbs",
          "Smartphones, Tablets & Motherboards",
          "Motor Oil, Aerosols & Cleaners"
        ),
        operatingHours = "Tue–Sat: 8:30 AM – 5:00 PM • Sun–Mon: Closed",
        isOpenNow = true,
        scheduleByDay = listOf(
          "Tue – Fri" to "8:30 AM – 5:00 PM",
          "Saturday" to "9:00 AM – 4:00 PM",
          "Sun – Mon" to "Closed"
        ),
        preparationInstructions = listOf(
          "Apply clear electrical or scotch tape across terminals of all lithium and 9V batteries",
          "Transport liquid chemicals strictly upright in leak-proof original containers",
          "Factory-reset electronic devices or request on-site physical drive degaussing",
          "Keep automotive oils separate from household cleaners and solvents"
        ),
        phoneNumber = "(555) 749-1120",
        rating = 4.8f,
        reviewsCount = 186,
        isMunicipalCertified = true,
        hasDriveThrough = true,
        acceptsBulkyItems = false,
        notes = "EPA-certified handling. Zero-landfill hazardous material containment guarantee."
      ),
      RecyclingCenter(
        id = "CENTER-03",
        name = "Community Soil & Organics Composting Station",
        categoryTag = "Aerobic Composting & Green Waste Hub",
        address = "310 Meadow Green Lane, River Park",
        latitude = 37.7690,
        longitude = -122.4280,
        primarySpecialty = WasteBinType.ORGANIC_GREEN,
        acceptedWasteTypes = listOf(
          WasteBinType.ORGANIC_GREEN
        ),
        acceptedMaterials = listOf(
          "Fruit Rinds, Vegetable Trimmings & Peels",
          "Coffee Grounds & Unbleached Filters",
          "Crushed Eggshells & Nut Shells",
          "Dry Leaves, Grass & Garden Cuttings",
          "BPI-Certified 100% Compostable Foodware"
        ),
        operatingHours = "Daily: 7:00 AM – 7:00 PM (Drop-off Hopper)",
        isOpenNow = true,
        scheduleByDay = listOf(
          "Mon – Sun" to "7:00 AM – 7:00 PM (Automated Hopper)"
        ),
        preparationInstructions = listOf(
          "Never deposit organics in non-biodegradable polyethylene plastic bags",
          "Peel off fruit produce PLU stickers, twist ties, and rubber bands",
          "Separate meat bones, oils, and pet waste into the high-temperature anaerobic digester chute",
          "Free rich organic soil compost pickup available for local community gardeners"
        ),
        phoneNumber = "(555) 628-3391",
        rating = 4.95f,
        reviewsCount = 240,
        isMunicipalCertified = true,
        hasDriveThrough = false,
        acceptsBulkyItems = false,
        notes = "Free compost collection bins for residents. Community urban farming partnership."
      ),
      RecyclingCenter(
        id = "CENTER-04",
        name = "Silicon Valley Circular E-Waste & Tech Lab",
        categoryTag = "Rare Metal & Electronics Circular Recovery",
        address = "890 Innovation Blvd, Tech Corridor",
        latitude = 37.7650,
        longitude = -122.3990,
        primarySpecialty = WasteBinType.E_WASTE_YELLOW,
        acceptedWasteTypes = listOf(
          WasteBinType.E_WASTE_YELLOW,
          WasteBinType.RECYCLABLE_BLUE
        ),
        acceptedMaterials = listOf(
          "Laptops, PC Towers, GPUs & CPUs",
          "Broken Phones, Smartwatches & Wearables",
          "HDMI, USB, AC Power Cables & Adapters",
          "Monitors, CRT Displays & Smart TVs",
          "Printers, Modems, Routers & Small Appliances"
        ),
        operatingHours = "Mon–Fri: 9:00 AM – 6:30 PM • Sat: 10:00 AM – 3:00 PM",
        isOpenNow = true,
        scheduleByDay = listOf(
          "Mon – Fri" to "9:00 AM – 6:30 PM",
          "Saturday" to "10:00 AM – 3:00 PM",
          "Sunday" to "Closed"
        ),
        preparationInstructions = listOf(
          "Remove removable lithium batteries where feasible and place in safety bins",
          "Remove ink/toner cartridges before dropping off printers",
          "Bundle power cords and peripheral cables neatly with ties",
          "Complimentary DoD 5220.22-M data sanitization certificate provided"
        ),
        phoneNumber = "(555) 914-7700",
        rating = 4.7f,
        reviewsCount = 142,
        isMunicipalCertified = true,
        hasDriveThrough = true,
        acceptsBulkyItems = true,
        notes = "98.4% material circularity rate. Precious gold, silver, and neodymium magnets reclaimed."
      ),
      RecyclingCenter(
        id = "CENTER-05",
        name = "Harbor Bay Scrap Metals, Glass & Bulk Depot",
        categoryTag = "Heavy Industrial Scrap & Cash Redemption",
        address = "2200 Pier Side Road, Port District",
        latitude = 37.7550,
        longitude = -122.3850,
        primarySpecialty = WasteBinType.RECYCLABLE_BLUE,
        acceptedWasteTypes = listOf(
          WasteBinType.RECYCLABLE_BLUE,
          WasteBinType.LANDFILL_GREY
        ),
        acceptedMaterials = listOf(
          "Copper Pipes, Brass Fixtures & Aluminum Siding",
          "Bulk Beverage Cans & Glass Bottles (Cash CRV)",
          "Large Household Appliances (Stoves, Washers)",
          "Automotive Rims & Heavy Steel Structural Scrap",
          "Bulk HDPE Barrels & Plastic Crates"
        ),
        operatingHours = "Mon–Fri: 6:00 AM – 4:30 PM • Sat: 7:00 AM – 1:00 PM",
        isOpenNow = true,
        scheduleByDay = listOf(
          "Mon – Fri" to "6:00 AM – 4:30 PM",
          "Saturday" to "7:00 AM – 1:00 PM",
          "Sunday" to "Closed"
        ),
        preparationInstructions = listOf(
          "Separate non-ferrous metals (copper, brass, aluminum) for top redemption payout",
          "Refrigerators and AC units require EPA Freon evacuation certified tag",
          "Sturdy closed-toe boots and safety eyewear required on processing deck",
          "Commercial trucks must check in at South Scale entrance"
        ),
        phoneNumber = "(555) 433-8822",
        rating = 4.6f,
        reviewsCount = 98,
        isMunicipalCertified = true,
        hasDriveThrough = true,
        acceptsBulkyItems = true,
        notes = "Cash payout for aluminum cans, copper, and brass scrap on scale weight."
      )
    )
  }

  fun calculateDistanceMiles(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
    val earthRadiusMiles = 3958.8
    val dLat = Math.toRadians(lat2 - lat1)
    val dLon = Math.toRadians(lon2 - lon1)
    val a = sin(dLat / 2) * sin(dLat / 2) +
      cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
      sin(dLon / 2) * sin(dLon / 2)
    val c = 2 * atan2(sqrt(a), sqrt(1 - a))
    val dist = earthRadiusMiles * c
    return kotlin.math.round(dist * 10) / 10.0
  }

  fun getSuggestedCenterFor(
    classification: WasteClassification,
    userLat: Double,
    userLon: Double
  ): Pair<RecyclingCenter, Double> {
    val centers = getAllRecyclingCenters()
    // First try exact specialty match
    val specialtyMatch = centers.filter { it.primarySpecialty == classification.binType }
    val candidateCenters = if (specialtyMatch.isNotEmpty()) {
      specialtyMatch
    } else {
      val accepting = centers.filter { it.acceptedWasteTypes.contains(classification.binType) }
      if (accepting.isNotEmpty()) accepting else centers
    }

    // Sort by distance to user
    val sorted = candidateCenters.map { center ->
      center to calculateDistanceMiles(userLat, userLon, center.latitude, center.longitude)
    }.sortedBy { it.second }

    return sorted.firstOrNull() ?: (centers.first() to 0.8)
  }

  fun getSuggestedCenterFor(
    category: String,
    userLat: Double,
    userLon: Double
  ): Pair<RecyclingCenter, Double> {
    val binType = WasteBinType.fromCategory(category, "")
    val centers = getAllRecyclingCenters()
    val specialtyMatch = centers.filter { it.primarySpecialty == binType }
    val candidateCenters = if (specialtyMatch.isNotEmpty()) {
      specialtyMatch
    } else {
      val accepting = centers.filter { it.acceptedWasteTypes.contains(binType) }
      if (accepting.isNotEmpty()) accepting else centers
    }
    val sorted = candidateCenters.map { center ->
      center to calculateDistanceMiles(userLat, userLon, center.latitude, center.longitude)
    }.sortedBy { it.second }
    return sorted.firstOrNull() ?: (centers.first() to 0.8)
  }
}
}

