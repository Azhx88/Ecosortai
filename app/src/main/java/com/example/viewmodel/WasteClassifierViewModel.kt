package com.example.viewmodel

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color as AndroidColor
import android.graphics.Paint
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.WasteScanEntity
import com.example.data.model.LeaderboardUser
import com.example.data.model.PresetSampleItem
import com.example.data.model.RecyclingCenter
import com.example.data.model.SmartBinNode
import com.example.data.model.UserLocationState
import com.example.data.model.WasteBinType
import com.example.data.model.WasteClassification
import com.example.data.repository.WasteRepository
import java.util.Locale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

enum class WasteAppTab(val title: String) {
  SCAN_CLASSIFY("AI Scanner"),
  RECYCLING_CENTERS("Eco Map"),
  SMART_IOT_BINS("Smart IoT Bin"),
  SEGREGATION_GUIDE("Bin Guide"),
  LEADERBOARD("Eco Ranks"),
  HISTORY("Activity Log")
}

data class WasteClassifierUiState(
  val activeTab: WasteAppTab = WasteAppTab.SCAN_CLASSIFY,
  val selectedBitmap: Bitmap? = null,
  val selectedImageUri: String? = null,
  val isAnalyzing: Boolean = false,
  val analysisStep: String = "",
  val currentClassification: WasteClassification? = null,
  val suggestedCenterForScan: Pair<RecyclingCenter, Double>? = null,
  val hasClaimedPoints: Boolean = false,
  val feedbackNotification: String? = null,
  val lastIoTActionNotice: String? = null,
  // Recycling Centers Map & Directory State
  val userLocation: UserLocationState = UserLocationState(),
  val isLocating: Boolean = false,
  val localizedCenterAddresses: Map<String, String> = emptyMap(),
  val selectedCenterId: String? = "CENTER-01",
  val centerFilterStream: WasteBinType? = null,
  val centerSearchQuery: String = "",
  val isCustomAddressDialogVisible: Boolean = false,
  val mapZoomLevel: Float = 1.0f
)

class WasteClassifierViewModel(application: Application) : AndroidViewModel(application) {
  private val repository = WasteRepository(application)

  private val _uiState = MutableStateFlow(WasteClassifierUiState())
  val uiState: StateFlow<WasteClassifierUiState> = _uiState.asStateFlow()

  val allScans: StateFlow<List<WasteScanEntity>> = repository.allScans
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val totalPoints: StateFlow<Int> = repository.totalPoints
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 120)

  val totalScansCount: StateFlow<Int> = repository.totalScansCount
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

  private val _smartBins = MutableStateFlow(repository.getInitialSmartBins())
  val smartBins: StateFlow<List<SmartBinNode>> = _smartBins.asStateFlow()

  val leaderboard: StateFlow<List<LeaderboardUser>> = totalPoints.combine(_smartBins) { points, _ ->
    repository.getCommunityLeaderboard(points)
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), repository.getCommunityLeaderboard(120))

  val presetSamples = listOf(
    PresetSampleItem(
      id = "sample_plastic_bottle",
      name = "Plastic Water Bottle",
      categoryHint = "plastic bottle",
      expectedBin = WasteBinType.RECYCLABLE_BLUE,
      iconEmoji = "🧴",
      description = "Standard 500ml single-use water bottle made of PET plastic.",
      shapeDescription = "Contoured cylindrical ribbed bottle with narrow neck",
      textureDescription = "Flexible, clear thermoplastic with creased reflections",
      colorDescription = "Transparent clear polymer with cobalt blue cap",
      sampleGuidance = "Empty and rinse before recycling. Place in the recyclable waste bin. Compress vertically to save bin volume.",
      impactTip = "Saves ~0.08 kg of CO2 and prevents plastic from polluting marine ecosystems.",
      points = 25
    ),
    PresetSampleItem(
      id = "sample_banana_peel",
      name = "Banana Peel / Food Scraps",
      categoryHint = "peel organic food",
      expectedBin = WasteBinType.ORGANIC_GREEN,
      iconEmoji = "🍌",
      description = "Organic biodegradable fruit skin from fresh consumption.",
      shapeDescription = "Natural organic curved strips tapering to a stem point",
      textureDescription = "Fibrous, soft, moisture-rich organic cell structure",
      colorDescription = "Vibrant yellow skin with speckled organic brown spots",
      sampleGuidance = "Place directly in the Green Compost Bin. Do not enclose in standard plastic bags.",
      impactTip = "Aerobic composting prevents anaerobic methane emissions in closed landfills.",
      points = 20
    ),
    PresetSampleItem(
      id = "sample_soda_can",
      name = "Aluminum Beverage Can",
      categoryHint = "aluminum can metal",
      expectedBin = WasteBinType.RECYCLABLE_BLUE,
      iconEmoji = "🥫",
      description = "Recyclable 330ml aluminum can with pull-tab lid.",
      shapeDescription = "Cylindrical extruded aluminum body with rolled rim",
      textureDescription = "Smooth specular metallic surface with matte paint coat",
      colorDescription = "Printed glossy red and silver reflective finish",
      sampleGuidance = "Ensure can is fully drained. Quick water rinse. Deposit in Blue Recyclable Bin.",
      impactTip = "Recycling saves 95% of the energy needed to refine bauxite into new aluminum.",
      points = 35
    ),
    PresetSampleItem(
      id = "sample_cardboard_box",
      name = "Cardboard Shipping Box",
      categoryHint = "cardboard box packaging paper",
      expectedBin = WasteBinType.RECYCLABLE_BLUE,
      iconEmoji = "📦",
      description = "Corrugated shipping packaging from online delivery.",
      shapeDescription = "Rectangular folded carton with layered corrugated flutes",
      textureDescription = "Matte, fibrous kraft cellulose paper fibers",
      colorDescription = "Warm kraft brown with black shipping print",
      sampleGuidance = "Strip off plastic adhesive tape and labels. Flatten box flat before placing in Blue Bin.",
      impactTip = "Preserves mature trees and reduces industrial water consumption during pulp processing.",
      points = 30
    ),
    PresetSampleItem(
      id = "sample_phone_ewaste",
      name = "Decommissioned Phone",
      categoryHint = "phone electronic e-waste",
      expectedBin = WasteBinType.E_WASTE_YELLOW,
      iconEmoji = "📱",
      description = "Broken smartphone containing lithium battery and circuitry.",
      shapeDescription = "Slim rectangular unibody with camera lens bezel",
      textureDescription = "Hardened glass panel, anodized metal frame",
      colorDescription = "Reflective slate black with silver accents",
      sampleGuidance = "Back up or wipe personal data. Deposit in specialized E-Waste Yellow Bin for rare metal extraction.",
      impactTip = "Recovers precious gold, silver, palladium, and prevents heavy metal ground pollution.",
      points = 45
    ),
    PresetSampleItem(
      id = "sample_battery_hazardous",
      name = "Alkaline / Li-Ion Battery",
      categoryHint = "battery chemical hazardous",
      expectedBin = WasteBinType.HAZARDOUS_RED,
      iconEmoji = "🔋",
      description = "Discharged cylindrical AA battery containing chemical electrolyte.",
      shapeDescription = "Small rigid metal cylinder with raised positive terminal",
      textureDescription = "Steel casing with plastic shrink insulation",
      colorDescription = "Metallic gold and black industrial livery",
      sampleGuidance = "DO NOT toss in general municipal bins. Tape terminals to prevent fire risk. Drop off in Red Hazardous Bin.",
      impactTip = "Safely isolates toxic cadmium, lead, and acid from infiltrating drinking reservoirs.",
      points = 50
    )
  )

  fun setActiveTab(tab: WasteAppTab) {
    _uiState.update { it.copy(activeTab = tab) }
  }

  fun dismissFeedback() {
    _uiState.update { it.copy(feedbackNotification = null) }
  }

  fun notifyFeedback(message: String) {
    _uiState.update { it.copy(feedbackNotification = message) }
  }

  fun onImageCapturedOrUploaded(bitmap: Bitmap, imageUri: String? = null, hint: String = "") {
    _uiState.update {
      it.copy(
        selectedBitmap = bitmap,
        selectedImageUri = imageUri,
        isAnalyzing = true,
        analysisStep = "Scanning visual characteristics (shape, texture, color)...",
        currentClassification = null,
        hasClaimedPoints = false,
        feedbackNotification = null
      )
    }

    viewModelScope.launch {
      // Calming visual analysis step animation
      delay(600)
      _uiState.update { it.copy(analysisStep = "Examining material edges, surface reflections & object features...") }
      delay(700)
      _uiState.update { it.copy(analysisStep = "Determining recycling category & disposal recommendations...") }

      val classification = repository.classifyImage(bitmap, hint)
      val suggested = repository.getSuggestedCenterFor(
        classification,
        _uiState.value.userLocation.latitude,
        _uiState.value.userLocation.longitude
      )

      _uiState.update {
        it.copy(
          isAnalyzing = false,
          currentClassification = classification,
          suggestedCenterForScan = suggested,
          feedbackNotification = "AI Classified: ${classification.objectName} (Confidence ${(classification.confidence * 100).toInt()}%)"
        )
      }
    }
  }

  fun selectPresetSample(preset: PresetSampleItem) {
    // Generate a clean representational bitmap for the preset
    val bitmap = createSampleBitmap(preset)
    onImageCapturedOrUploaded(bitmap, null, "${preset.name} ${preset.categoryHint}")
  }

  fun claimRewardAndTriggerIoT() {
    val current = _uiState.value.currentClassification ?: return
    if (_uiState.value.hasClaimedPoints) return

    viewModelScope.launch {
      // Record scan in Room database
      repository.recordScan(current, _uiState.value.selectedImageUri)

      /*
      // IoT Smart Dustbin lid simulation commented out for future implementation
      val targetBinType = current.binType
      _smartBins.update { bins ->
        bins.map { bin ->
          if (bin.binType == targetBinType) {
            bin.copy(
              isLidOpen = true,
              fillPercentage = minOf(100, bin.fillPercentage + 4),
              status = "Lid Open - Waste Accepted (${current.objectName})"
            )
          } else {
            bin
          }
        }
      }
      */

      val binColorNotice = current.binType.binColorName
      _uiState.update {
        it.copy(
          hasClaimedPoints = true,
          feedbackNotification = "+${current.rewardPoints} Eco-Points Claimed! Deposited in $binColorNotice."
        )
      }

      /*
      // Auto-close lid after 4 seconds (IoT simulated cycle)
      delay(4000)
      _smartBins.update { bins ->
        bins.map { bin ->
          if (bin.binType == targetBinType) {
            bin.copy(
              isLidOpen = false,
              status = "Online (Sensor Active - ${bin.fillPercentage}% Full)"
            )
          } else {
            bin
          }
        }
      }
      */
    }
  }

  fun toggleBinLidManually(binId: String) {
    _smartBins.update { bins ->
      bins.map { bin ->
        if (bin.id == binId) {
          val nextState = !bin.isLidOpen
          bin.copy(
            isLidOpen = nextState,
            status = if (nextState) "Manual Override: Lid Open" else "Online (Lid Closed)"
          )
        } else {
          bin
        }
      }
    }
  }

  fun emptySmartBin(binId: String) {
    _smartBins.update { bins ->
      bins.map { bin ->
        if (bin.id == binId) {
          bin.copy(
            fillPercentage = 0,
            status = "Emptied by Automated IoT Collection Service"
          )
        } else {
          bin
        }
      }
    }
    _uiState.update {
      it.copy(feedbackNotification = "Smart Bin emptied & reset to 0% fill level.")
    }
  }

  fun clearCurrentScan() {
    _uiState.update {
      it.copy(
        selectedBitmap = null,
        selectedImageUri = null,
        currentClassification = null,
        suggestedCenterForScan = null,
        hasClaimedPoints = false,
        feedbackNotification = null
      )
    }
  }

  // --- Local Recycling Centers Feature ---

  val presetNeighborhoods = listOf(
    UserLocationState(37.7749, -122.4194, "Downtown Central", true),
    UserLocationState(37.7885, -122.4080, "Northside Industrial Sector", true),
    UserLocationState(37.7690, -122.4280, "River Park Community", true),
    UserLocationState(37.7650, -122.3990, "Tech Innovation District", true),
    UserLocationState(37.7550, -122.3850, "South Port Waterfront", true)
  )

  fun getAllCenters(): List<RecyclingCenter> {
    val state = _uiState.value
    val loc = state.userLocation
    return repository.getRecyclingCentersNear(
      loc.latitude,
      loc.longitude,
      loc.addressLabel,
      state.localizedCenterAddresses
    )
  }

  fun getFilteredCenters(): List<Pair<RecyclingCenter, Double>> {
    val state = _uiState.value
    val userLat = state.userLocation.latitude
    val userLon = state.userLocation.longitude
    val query = state.centerSearchQuery.trim().lowercase()
    val stream = state.centerFilterStream

    return getAllCenters()
      .filter { center ->
        val matchesStream = stream == null || center.acceptedWasteTypes.contains(stream)
        val matchesQuery = query.isEmpty() ||
          center.name.lowercase().contains(query) ||
          center.address.lowercase().contains(query) ||
          center.acceptedMaterials.any { it.lowercase().contains(query) }
        matchesStream && matchesQuery
      }
      .map { center ->
        center to repository.calculateDistanceMiles(userLat, userLon, center.latitude, center.longitude)
      }
      .sortedBy { it.second }
  }

  fun getSelectedCenterWithDistance(): Pair<RecyclingCenter, Double>? {
    val selectedId = _uiState.value.selectedCenterId ?: return null
    val center = getAllCenters().find { it.id == selectedId } ?: return null
    val dist = repository.calculateDistanceMiles(
      _uiState.value.userLocation.latitude,
      _uiState.value.userLocation.longitude,
      center.latitude,
      center.longitude
    )
    return center to dist
  }

  fun selectRecyclingCenter(centerId: String?) {
    _uiState.update { it.copy(selectedCenterId = centerId) }
  }

  fun setCenterFilterStream(binType: WasteBinType?) {
    _uiState.update { it.copy(centerFilterStream = binType) }
  }

  fun setCenterSearchQuery(query: String) {
    _uiState.update { it.copy(centerSearchQuery = query) }
  }

  fun updateUserLocation(lat: Double, lon: Double, label: String, isCustom: Boolean = false) {
    _uiState.update {
      val currentClassification = it.currentClassification
      val updatedSuggestion = if (currentClassification != null) {
        repository.getSuggestedCenterFor(currentClassification, lat, lon)
      } else {
        it.suggestedCenterForScan
      }
      it.copy(
        userLocation = UserLocationState(lat, lon, label, isCustom),
        isLocating = false,
        localizedCenterAddresses = emptyMap(),
        suggestedCenterForScan = updatedSuggestion,
        feedbackNotification = "Location updated: $label"
      )
    }

    // Resolve real local streets & neighborhoods for each nearby facility coordinate
    viewModelScope.launch {
      val context = getApplication<Application>()
      val baseCenters = repository.getRecyclingCentersNear(lat, lon, label)
      val resolvedMap = withContext(Dispatchers.IO) {
        val map = mutableMapOf<String, String>()
        try {
          if (Geocoder.isPresent()) {
            val geocoder = Geocoder(context, Locale.getDefault())
            for (center in baseCenters) {
              try {
                @Suppress("DEPRECATION")
                val list = geocoder.getFromLocation(center.latitude, center.longitude, 1)
                if (!list.isNullOrEmpty()) {
                  val addr = list[0]
                  val streetOrArea = addr.thoroughfare ?: addr.subLocality ?: addr.featureName
                  val subArea = addr.subLocality ?: addr.locality
                  val city = addr.locality ?: addr.subAdminArea ?: addr.adminArea
                  val parts = listOfNotNull(streetOrArea, subArea, city)
                    .map { it.trim() }
                    .filter { it.isNotEmpty() }
                    .distinct()
                  if (parts.isNotEmpty()) {
                    map[center.id] = parts.joinToString(", ")
                  }
                }
              } catch (_: Exception) {}
            }
          }
        } catch (_: Exception) {}

        // Also enrich any unresolved facility address via OpenStreetMap Nominatim API
        for (center in baseCenters) {
          if (!map.containsKey(center.id)) {
            val osmAddr = com.example.data.api.OpenStreetMapService.reverseGeocodeNominatim(
              center.latitude,
              center.longitude
            )
            if (!osmAddr.isNullOrBlank()) {
              map[center.id] = osmAddr
            }
          }
        }
        map
      }
      if (resolvedMap.isNotEmpty()) {
        _uiState.update { it.copy(localizedCenterAddresses = resolvedMap) }
      }
    }
  }

  @Suppress("DEPRECATION")
  private suspend fun reverseGeocodeLocation(context: Context, lat: Double, lon: Double): String =
    withContext(Dispatchers.IO) {
      try {
        if (Geocoder.isPresent()) {
          val geocoder = Geocoder(context, Locale.getDefault())
          val addresses = geocoder.getFromLocation(lat, lon, 1)
          if (!addresses.isNullOrEmpty()) {
            val addr = addresses[0]
            val subLocality = addr.subLocality ?: addr.thoroughfare
            val locality = addr.locality ?: addr.subAdminArea ?: addr.adminArea
            val labelParts = listOfNotNull(subLocality, locality).distinct()
            if (labelParts.isNotEmpty()) {
              return@withContext "${labelParts.joinToString(", ")} (GPS)"
            }
          }
        }
      } catch (_: Exception) {
        // Fallback to OpenStreetMap Nominatim reverse geocoding
      }
      val osmReverse = com.example.data.api.OpenStreetMapService.reverseGeocodeNominatim(lat, lon)
      if (!osmReverse.isNullOrBlank()) {
        return@withContext "$osmReverse (GPS)"
      }
      String.format(Locale.US, "Current GPS (%.4f, %.4f)", lat, lon)
    }

  @Suppress("DEPRECATION")
  fun searchAndSetAddress(rawQuery: String) {
    val cleanQuery = rawQuery.trim()
    if (cleanQuery.isEmpty()) return
    _uiState.update { it.copy(isLocating = true) }
    viewModelScope.launch {
      val context = getApplication<Application>()
      val resolved = withContext(Dispatchers.IO) {
        try {
          if (Geocoder.isPresent()) {
            val geocoder = Geocoder(context, Locale.getDefault())
            val results = geocoder.getFromLocationName(cleanQuery, 1)
            if (!results.isNullOrEmpty()) {
              val match = results[0]
              val city = match.locality ?: match.subAdminArea ?: match.adminArea ?: cleanQuery
              return@withContext Triple(match.latitude, match.longitude, city)
            }
          }
        } catch (_: Exception) {
          // Geocoder unavailable or network offline
        }
        // Fallback to OpenStreetMap Nominatim place search
        com.example.data.api.OpenStreetMapService.searchPlaceNominatim(cleanQuery)
      }

      if (resolved != null) {
        updateUserLocation(resolved.first, resolved.second, resolved.third, true)
      } else {
        // Fallback offset around current base coordinates
        val hashOffset = ((cleanQuery.hashCode() % 20) * 0.0012)
        val curLat = _uiState.value.userLocation.latitude
        val curLon = _uiState.value.userLocation.longitude
        updateUserLocation(curLat + hashOffset, curLon - hashOffset, cleanQuery, true)
      }
    }
  }

  @Suppress("MissingPermission")
  fun requestDeviceLocation() {
    val context = getApplication<Application>()
    val hasFine = androidx.core.content.ContextCompat.checkSelfPermission(
      context,
      android.Manifest.permission.ACCESS_FINE_LOCATION
    ) == android.content.pm.PackageManager.PERMISSION_GRANTED
    val hasCoarse = androidx.core.content.ContextCompat.checkSelfPermission(
      context,
      android.Manifest.permission.ACCESS_COARSE_LOCATION
    ) == android.content.pm.PackageManager.PERMISSION_GRANTED

    if (!hasFine && !hasCoarse) {
      _uiState.update {
        it.copy(
          isLocating = false,
          feedbackNotification = "Location permission needed to detect GPS coordinates."
        )
      }
      return
    }

    _uiState.update { it.copy(isLocating = true) }

    viewModelScope.launch {
      try {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
        if (locationManager != null) {
          val providers = listOf(
            LocationManager.GPS_PROVIDER,
            LocationManager.NETWORK_PROVIDER,
            LocationManager.PASSIVE_PROVIDER
          )
          val bestKnown: Location? = providers
            .mapNotNull { provider ->
              try {
                if (locationManager.isProviderEnabled(provider)) {
                  locationManager.getLastKnownLocation(provider)
                } else null
              } catch (_: Exception) {
                null
              }
            }
            .maxByOrNull { it.time }

          if (bestKnown != null) {
            val label = reverseGeocodeLocation(context, bestKnown.latitude, bestKnown.longitude)
            updateUserLocation(bestKnown.latitude, bestKnown.longitude, label, false)
            return@launch
          }

          // Request a fresh single location update if no cached lastKnownLocation exists
          val activeProvider = when {
            locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) -> LocationManager.GPS_PROVIDER
            locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) -> LocationManager.NETWORK_PROVIDER
            else -> null
          }

          if (activeProvider != null) {
            var receivedLocation: Location? = null
            val listener = object : LocationListener {
              override fun onLocationChanged(location: Location) {
                receivedLocation = location
                try {
                  locationManager.removeUpdates(this)
                } catch (_: Exception) {}
              }
              override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
              override fun onProviderEnabled(provider: String) {}
              override fun onProviderDisabled(provider: String) {}
            }
            try {
              locationManager.requestLocationUpdates(
                activeProvider,
                0L,
                0f,
                listener,
                Looper.getMainLooper()
              )
              // Wait up to 2.5 seconds for GPS/Network fix
              var waited = 0
              while (receivedLocation == null && waited < 25) {
                delay(100)
                waited++
              }
              locationManager.removeUpdates(listener)
            } catch (_: Exception) {
              try {
                locationManager.removeUpdates(listener)
              } catch (_: Exception) {}
            }

            val freshLoc = receivedLocation
            if (freshLoc != null) {
              val label = reverseGeocodeLocation(context, freshLoc.latitude, freshLoc.longitude)
              updateUserLocation(freshLoc.latitude, freshLoc.longitude, label, false)
              return@launch
            }
          }
        }
      } catch (_: Exception) {
        // Graceful fallback below
      }

      // Fallback when emulator/device has no GPS provider signal configured yet
      val currentLoc = _uiState.value.userLocation
      val label = reverseGeocodeLocation(context, currentLoc.latitude, currentLoc.longitude)
      updateUserLocation(currentLoc.latitude, currentLoc.longitude, label, false)
    }
  }

  fun navigateToCenterOnMap(centerId: String) {
    _uiState.update {
      it.copy(
        activeTab = WasteAppTab.RECYCLING_CENTERS,
        selectedCenterId = centerId
      )
    }
  }

  fun showCustomAddressDialog(show: Boolean) {
    _uiState.update { it.copy(isCustomAddressDialogVisible = show) }
  }

  fun setMapZoom(delta: Float) {
    _uiState.update {
      val newZoom = (it.mapZoomLevel + delta).coerceIn(0.6f, 2.5f)
      it.copy(mapZoomLevel = newZoom)
    }
  }


  fun deleteScan(id: Int) {
    viewModelScope.launch {
      repository.deleteScan(id)
    }
  }

  fun clearAllHistory() {
    viewModelScope.launch {
      repository.clearHistory()
      _uiState.update { it.copy(feedbackNotification = "Scan history cleared.") }
    }
  }

  private fun createSampleBitmap(preset: PresetSampleItem): Bitmap {
    val size = 512
    val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)

    // Fill background with soft tinted color
    val bgPaint = Paint().apply {
      isAntiAlias = true
      color = when (preset.expectedBin) {
        WasteBinType.RECYCLABLE_BLUE -> AndroidColor.parseColor("#E3F2FD")
        WasteBinType.ORGANIC_GREEN -> AndroidColor.parseColor("#E8F5E9")
        WasteBinType.E_WASTE_YELLOW -> AndroidColor.parseColor("#FFFDE7")
        WasteBinType.HAZARDOUS_RED -> AndroidColor.parseColor("#FFEBEE")
        WasteBinType.LANDFILL_GREY -> AndroidColor.parseColor("#ECEFF1")
      }
    }
    canvas.drawRect(0f, 0f, size.toFloat(), size.toFloat(), bgPaint)

    // Center circle
    val circlePaint = Paint().apply {
      isAntiAlias = true
      color = AndroidColor.WHITE
    }
    canvas.drawCircle(size / 2f, size / 2f, size * 0.38f, circlePaint)

    // Draw preset label text
    val textPaint = Paint().apply {
      isAntiAlias = true
      color = AndroidColor.DKGRAY
      textSize = 34f
      textAlign = Paint.Align.CENTER
    }
    canvas.drawText(preset.name, size / 2f, size / 2f - 20f, textPaint)

    val subPaint = Paint().apply {
      isAntiAlias = true
      color = AndroidColor.GRAY
      textSize = 24f
      textAlign = Paint.Align.CENTER
    }
    canvas.drawText("AI Waste Sample", size / 2f, size / 2f + 30f, subPaint)

    val emojiPaint = Paint().apply {
      isAntiAlias = true
      textSize = 72f
      textAlign = Paint.Align.CENTER
    }
    canvas.drawText(preset.iconEmoji, size / 2f, size / 2f - 90f, emojiPaint)

    return bitmap
  }
}
