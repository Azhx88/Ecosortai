package com.example.data.model

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.BinBlueBackground
import com.example.ui.theme.BinBlueRecycle
import com.example.ui.theme.BinGreenBackground
import com.example.ui.theme.BinGreenCompost
import com.example.ui.theme.BinGreyBackground
import com.example.ui.theme.BinGreyLandfill
import com.example.ui.theme.BinRedBackground
import com.example.ui.theme.BinRedHazardous
import com.example.ui.theme.BinYellowBackground
import com.example.ui.theme.BinYellowEWaste

enum class WasteBinType(
  val title: String,
  val binColorName: String,
  val primaryColor: Color,
  val backgroundColor: Color,
  val description: String,
  val examples: List<String>
) {
  RECYCLABLE_BLUE(
    title = "Recyclable (Blue Bin)",
    binColorName = "Blue Bin",
    primaryColor = BinBlueRecycle,
    backgroundColor = BinBlueBackground,
    description = "Clean plastics, metals, paper, and glass that can be reprocessed into new products.",
    examples = listOf("Plastic bottles", "Aluminum cans", "Cardboard", "Glass jars", "Paper cartons")
  ),
  ORGANIC_GREEN(
    title = "Organic / Compost (Green Bin)",
    binColorName = "Green Bin",
    primaryColor = BinGreenCompost,
    backgroundColor = BinGreenBackground,
    description = "Biodegradable food scraps, garden trimmings, and compostable matter.",
    examples = listOf("Fruit peels", "Vegetable waste", "Coffee grounds", "Leaves", "Egg shells")
  ),
  E_WASTE_YELLOW(
    title = "E-Waste (Yellow/Orange Bin)",
    binColorName = "Yellow Bin",
    primaryColor = BinYellowEWaste,
    backgroundColor = BinYellowBackground,
    description = "Electronic parts, cables, circuit boards, and appliances requiring designated recycling facilities.",
    examples = listOf("Broken phones", "Charging cords", "Motherboards", "Remote controls")
  ),
  HAZARDOUS_RED(
    title = "Hazardous Waste (Red Bin)",
    binColorName = "Red Bin",
    primaryColor = BinRedHazardous,
    backgroundColor = BinRedBackground,
    description = "Chemicals, batteries, paint, and medical items that pose biological or chemical contamination risks.",
    examples = listOf("Lithium/Alkaline batteries", "Paint cans", "Medicines", "Aerosol cans")
  ),
  LANDFILL_GREY(
    title = "General / Landfill (Grey Bin)",
    binColorName = "Grey Bin",
    primaryColor = BinGreyLandfill,
    backgroundColor = BinGreyBackground,
    description = "Non-recyclable composite materials, sanitary waste, or contaminated packaging.",
    examples = listOf("Chip bags", "Thermal receipts", "Dirty cling wrap", "Styrofoam cups")
  );

  companion object {
    fun fromCategory(category: String, binStr: String): WasteBinType {
      val lower = "$category $binStr".lowercase()
      return when {
        lower.contains("hazard") || lower.contains("batter") || lower.contains("toxic") || lower.contains("paint") -> HAZARDOUS_RED
        lower.contains("e-waste") || lower.contains("electron") || lower.contains("phone") || lower.contains("cable") -> E_WASTE_YELLOW
        lower.contains("organ") || lower.contains("food") || lower.contains("compost") || lower.contains("fruit") || lower.contains("peel") || lower.contains("leaf") -> ORGANIC_GREEN
        lower.contains("landfill") || lower.contains("general") || lower.contains("non-recycl") || lower.contains("residual") -> LANDFILL_GREY
        else -> RECYCLABLE_BLUE
      }
    }
  }
}

data class WasteClassification(
  val objectName: String,
  val category: String,
  val binType: WasteBinType,
  val confidence: Float,
  val shapeFeature: String,
  val textureFeature: String,
  val colorFeature: String,
  val recyclingGuidance: String,
  val environmentalImpact: String,
  val rewardPoints: Int,
  val iotBinCommand: String,
  val timestamp: Long = System.currentTimeMillis()
)

data class LeaderboardUser(
  val rank: Int,
  val name: String,
  val points: Int,
  val streakDays: Int,
  val badge: String,
  val isCurrentUser: Boolean = false,
  val avatarInitials: String
)

data class SmartBinNode(
  val id: String,
  val name: String,
  val binType: WasteBinType,
  val fillPercentage: Int,
  val isLidOpen: Boolean = false,
  val batteryLevel: Int = 94,
  val status: String = "Online (BLE Mesh)"
)

data class PresetSampleItem(
  val id: String,
  val name: String,
  val categoryHint: String,
  val expectedBin: WasteBinType,
  val iconEmoji: String,
  val description: String,
  val shapeDescription: String,
  val textureDescription: String,
  val colorDescription: String,
  val sampleGuidance: String,
  val impactTip: String,
  val points: Int
)

data class RecyclingCenter(
  val id: String,
  val name: String,
  val categoryTag: String,
  val address: String,
  val latitude: Double,
  val longitude: Double,
  val primarySpecialty: WasteBinType,
  val acceptedWasteTypes: List<WasteBinType>,
  val acceptedMaterials: List<String>,
  val operatingHours: String,
  val isOpenNow: Boolean = true,
  val scheduleByDay: List<Pair<String, String>> = emptyList(),
  val preparationInstructions: List<String>,
  val phoneNumber: String,
  val rating: Float = 4.8f,
  val reviewsCount: Int = 128,
  val isMunicipalCertified: Boolean = true,
  val hasDriveThrough: Boolean = true,
  val acceptsBulkyItems: Boolean = false,
  val notes: String = ""
)

data class UserLocationState(
  val latitude: Double = 37.7749,
  val longitude: Double = -122.4194,
  val addressLabel: String = "Current Location (Downtown)",
  val isCustomAddress: Boolean = false
)
