package com.example.ui.screens

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.webkit.JavascriptInterface
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.EditLocation
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Park
import androidx.compose.material.icons.filled.Recycling
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.api.OpenStreetMapService
import com.example.data.model.RecyclingCenter
import com.example.data.model.WasteBinType
import java.util.Locale
import kotlin.math.floor
import kotlin.math.hypot
import com.example.ui.theme.CalmSage
import com.example.ui.theme.DeepRiverBlue
import com.example.ui.theme.EucalyptusGreen
import com.example.ui.theme.MapGridColor
import com.example.ui.theme.MapLandColor
import com.example.ui.theme.MapParkColor
import com.example.ui.theme.MapRoadColor
import com.example.ui.theme.MapWaterColor
import com.example.ui.theme.MutedSand
import com.example.ui.theme.RiverStoneGrey
import com.example.ui.theme.RiverWater
import com.example.ui.theme.SereneOceanBlue
import com.example.ui.theme.SleekCardBorder
import com.example.ui.theme.SleekDivider
import com.example.ui.theme.SleekGreenPrimary
import com.example.ui.theme.SleekGreenPulse
import com.example.ui.theme.SleekPill
import com.example.ui.theme.SleekPillLight
import com.example.ui.theme.SleekSurface
import com.example.ui.theme.SleekTextBody
import com.example.ui.theme.SleekTextPrimary
import com.example.ui.theme.SleekTextSecondary
import com.example.ui.theme.SoftClay
import com.example.ui.theme.SoftRiverBlue
import com.example.ui.theme.WarmEarthTone
import com.example.viewmodel.WasteClassifierViewModel
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RecyclingCentersMapScreen(
  viewModel: WasteClassifierViewModel
) {
  val context = LocalContext.current
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()
  val listState = rememberLazyListState()
  val coroutineScope = rememberCoroutineScope()

  val filteredCenters = viewModel.getFilteredCenters()
  val selectedPair = viewModel.getSelectedCenterWithDistance()
  val selectedCenter = selectedPair?.first

  var showAddressDialog by remember { mutableStateOf(false) }

  val locationPermissionLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.RequestMultiplePermissions()
  ) { permissions ->
    val fineGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
    val coarseGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
    if (fineGranted || coarseGranted) {
      viewModel.requestDeviceLocation()
    } else {
      Toast.makeText(
        context,
        "Location permission denied. Using selected district coordinates.",
        Toast.LENGTH_SHORT
      ).show()
    }
  }

  val requestCurrentGpsLocation = {
    val hasFine = ContextCompat.checkSelfPermission(
      context,
      Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED
    val hasCoarse = ContextCompat.checkSelfPermission(
      context,
      Manifest.permission.ACCESS_COARSE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    if (hasFine || hasCoarse) {
      viewModel.requestDeviceLocation()
    } else {
      locationPermissionLauncher.launch(
        arrayOf(
          Manifest.permission.ACCESS_FINE_LOCATION,
          Manifest.permission.ACCESS_COARSE_LOCATION
        )
      )
    }
  }

  // Automatically detect current location on initial open if still on default placeholder
  LaunchedEffect(Unit) {
    if (!uiState.userLocation.isCustomAddress &&
      uiState.userLocation.addressLabel == "Current Location (Downtown)"
    ) {
      requestCurrentGpsLocation()
    }
  }

  // Auto scroll to top when a center is selected
  LaunchedEffect(uiState.selectedCenterId) {
    if (uiState.selectedCenterId != null) {
      coroutineScope.launch {
        listState.animateScrollToItem(0)
      }
    }
  }

  LazyColumn(
    state = listState,
    modifier = Modifier
      .fillMaxSize()
      .testTag("recycling_centers_screen"),
    contentPadding = PaddingValues(16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // 1. Serene Header Card
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = SleekSurface),
        border = BorderStroke(1.dp, SleekCardBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                  modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(SoftRiverBlue),
                  contentAlignment = Alignment.Center
                ) {
                  Icon(
                    imageVector = Icons.Default.Map,
                    contentDescription = null,
                    tint = SereneOceanBlue,
                    modifier = Modifier.size(18.dp)
                  )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                  text = "Local Recycling Centers",
                  style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = SleekTextPrimary
                  )
                )
              }
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = "Find verified drop-off hubs with accepted streams & prep rules",
                style = MaterialTheme.typography.bodySmall.copy(
                  color = SleekTextSecondary
                )
              )
            }
          }

          Spacer(modifier = Modifier.height(16.dp))

          // Current User Location & Address Picker Row
          Surface(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(18.dp))
              .clickable { showAddressDialog = true }
              .testTag("location_origin_bar"),
            color = MutedSand,
            border = BorderStroke(1.dp, SleekCardBorder)
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Box(
                  modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(SleekPill),
                  contentAlignment = Alignment.Center
                ) {
                  Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "Origin Pin",
                    tint = SleekGreenPrimary,
                    modifier = Modifier.size(16.dp)
                  )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                  Text(
                    text = "SEARCHING NEAR",
                    style = MaterialTheme.typography.labelSmall.copy(
                      fontSize = 10.sp,
                      fontWeight = FontWeight.Bold,
                      color = SleekTextSecondary,
                      letterSpacing = 0.6.sp
                    )
                  )
                  Text(
                    text = if (uiState.isLocating) "Detecting current GPS location..." else uiState.userLocation.addressLabel,
                    style = MaterialTheme.typography.bodyMedium.copy(
                      fontWeight = FontWeight.SemiBold,
                      color = SleekTextPrimary
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                  )
                  Text(
                    text = String.format(
                      Locale.US,
                      "Lat %.4f, Lon %.4f",
                      uiState.userLocation.latitude,
                      uiState.userLocation.longitude
                    ),
                    style = MaterialTheme.typography.labelSmall.copy(
                      fontSize = 10.sp,
                      color = SleekTextSecondary
                    )
                  )
                }
              }

              Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                  shape = RoundedCornerShape(12.dp),
                  color = SleekSurface,
                  border = BorderStroke(1.dp, SleekCardBorder),
                  modifier = Modifier
                    .clickable { requestCurrentGpsLocation() }
                    .testTag("btn_gps_location")
                ) {
                  Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Icon(
                      imageVector = Icons.Default.MyLocation,
                      contentDescription = "GPS",
                      tint = SereneOceanBlue,
                      modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                      text = if (uiState.isLocating) "..." else "GPS",
                      style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = SereneOceanBlue
                      )
                    )
                  }
                }

                Spacer(modifier = Modifier.width(6.dp))

                Surface(
                  shape = RoundedCornerShape(12.dp),
                  color = SleekSurface,
                  border = BorderStroke(1.dp, SleekCardBorder),
                  modifier = Modifier
                    .clickable { showAddressDialog = true }
                    .testTag("btn_change_address")
                ) {
                  Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Icon(
                      imageVector = Icons.Default.EditLocation,
                      contentDescription = "Change",
                      tint = SleekTextSecondary,
                      modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                      text = "Change",
                      style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Medium,
                        color = SleekTextPrimary
                      )
                    )
                  }
                }
              }
            }
          }
        }
      }
    }

    // 2. Interactive Nature Map Canvas
    item {
      RecyclingMapCard(
        userLocation = uiState.userLocation,
        centers = viewModel.getAllCenters(),
        selectedCenterId = uiState.selectedCenterId,
        onCenterSelected = { centerId ->
          viewModel.selectRecyclingCenter(centerId)
        },
        zoomLevel = uiState.mapZoomLevel,
        onZoomChange = { delta -> viewModel.setMapZoom(delta) }
      )
    }

    // 3. Search & Stream Filter Chips
    item {
      Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        // Search text input
        OutlinedTextField(
          value = uiState.centerSearchQuery,
          onValueChange = { viewModel.setCenterSearchQuery(it) },
          placeholder = { Text("Search facility, materials (e.g., batteries, cans, boxes)...", fontSize = 13.sp, color = SleekTextSecondary) },
          leadingIcon = {
            Icon(
              imageVector = Icons.Default.Search,
              contentDescription = null,
              tint = SleekTextSecondary,
              modifier = Modifier.size(18.dp)
            )
          },
          trailingIcon = {
            if (uiState.centerSearchQuery.isNotEmpty()) {
              IconButton(onClick = { viewModel.setCenterSearchQuery("") }) {
                Icon(
                  imageVector = Icons.Default.Clear,
                  contentDescription = "Clear",
                  tint = SleekTextSecondary,
                  modifier = Modifier.size(16.dp)
                )
              }
            }
          },
          shape = RoundedCornerShape(18.dp),
          colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = SleekTextPrimary,
            unfocusedTextColor = SleekTextPrimary,
            cursorColor = SleekGreenPrimary,
            focusedPlaceholderColor = SleekTextSecondary,
            unfocusedPlaceholderColor = SleekTextSecondary,
            focusedContainerColor = SleekSurface,
            unfocusedContainerColor = SleekSurface,
            focusedBorderColor = SleekGreenPrimary,
            unfocusedBorderColor = SleekCardBorder
          ),
          singleLine = true,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("center_search_field")
        )

        // Stream Filter Row
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          FilterChip(
            selected = uiState.centerFilterStream == null,
            onClick = { viewModel.setCenterFilterStream(null) },
            label = { Text("All Streams (${viewModel.getAllCenters().size})", fontSize = 12.sp) },
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = SleekPill,
              selectedLabelColor = SleekGreenPrimary,
              containerColor = SleekSurface,
              labelColor = SleekTextSecondary
            ),
            border = BorderStroke(1.dp, if (uiState.centerFilterStream == null) SleekGreenPrimary.copy(alpha = 0.4f) else SleekCardBorder),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.testTag("filter_chip_all")
          )

          WasteBinType.values().forEach { binType ->
            val isSelected = uiState.centerFilterStream == binType
            FilterChip(
              selected = isSelected,
              onClick = {
                viewModel.setCenterFilterStream(if (isSelected) null else binType)
              },
              label = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Box(
                    modifier = Modifier
                      .size(8.dp)
                      .clip(CircleShape)
                      .background(binType.primaryColor)
                  )
                  Spacer(modifier = Modifier.width(6.dp))
                  Text(binType.binColorName, fontSize = 12.sp)
                }
              },
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = binType.backgroundColor,
                selectedLabelColor = binType.primaryColor,
                containerColor = SleekSurface,
                labelColor = SleekTextSecondary
              ),
              border = BorderStroke(1.dp, if (isSelected) binType.primaryColor.copy(alpha = 0.5f) else SleekCardBorder),
              shape = RoundedCornerShape(14.dp),
              modifier = Modifier.testTag("filter_chip_${binType.name}")
            )
          }
        }
      }
    }

    // 4. Selected Facility Deep-Dive Details Card
    if (selectedCenter != null) {
      item {
        SelectedFacilityCard(
          center = selectedCenter,
          distanceMiles = selectedPair.second,
          onClose = { viewModel.selectRecyclingCenter(null) },
          onGetDirections = {
            val lat = selectedCenter.latitude
            val lon = selectedCenter.longitude
            val geoUri = Uri.parse("geo:$lat,$lon?q=$lat,$lon(${Uri.encode(selectedCenter.name)})")
            val mapIntent = Intent(Intent.ACTION_VIEW, geoUri)
            try {
              context.startActivity(mapIntent)
            } catch (e: Exception) {
              try {
                context.startActivity(
                  Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://www.google.com/maps/search/?api=1&query=$lat,$lon")
                  )
                )
              } catch (_: Exception) {
                Toast.makeText(context, "Navigating to ${selectedCenter.name} (${selectedPair.second} mi away)", Toast.LENGTH_SHORT).show()
              }
            }
          },
          onCallFacility = {
            val phoneIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${selectedCenter.phoneNumber.replace("[^0-9]".toRegex(), "")}"))
            try {
              context.startActivity(phoneIntent)
            } catch (e: Exception) {
              Toast.makeText(context, "Calling ${selectedCenter.phoneNumber}", Toast.LENGTH_SHORT).show()
            }
          }
        )
      }
    }

    // 5. Facilities List Header
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "Nearby Facilities (${filteredCenters.size})",
          style = MaterialTheme.typography.titleMedium.copy(
            fontWeight = FontWeight.Bold,
            color = SleekTextPrimary
          )
        )
        Text(
          text = "Sorted by proximity",
          style = MaterialTheme.typography.bodySmall.copy(
            color = SleekTextSecondary
          )
        )
      }
    }

    // List of facilities
    if (filteredCenters.isEmpty()) {
      item {
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(20.dp),
          colors = CardDefaults.cardColors(containerColor = SleekSurface),
          border = BorderStroke(1.dp, SleekCardBorder)
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Icon(
              imageVector = Icons.Default.FilterList,
              contentDescription = null,
              tint = SleekTextSecondary,
              modifier = Modifier.size(36.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
              text = "No facilities match your search",
              fontWeight = FontWeight.SemiBold,
              color = SleekTextPrimary
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
              text = "Try clearing the material filter or searching for a different item.",
              style = MaterialTheme.typography.bodySmall.copy(color = SleekTextSecondary),
              textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
          }
        }
      }
    } else {
      items(filteredCenters) { (center, distance) ->
        FacilitySummaryCard(
          center = center,
          distanceMiles = distance,
          isSelected = center.id == uiState.selectedCenterId,
          onSelect = {
            viewModel.selectRecyclingCenter(center.id)
          }
        )
      }
    }

    item {
      Spacer(modifier = Modifier.height(16.dp))
    }
  }

  // Address Selection Dialog
  if (showAddressDialog) {
    AddressSelectionDialog(
      currentAddress = uiState.userLocation.addressLabel,
      presetNeighborhoods = viewModel.presetNeighborhoods,
      onSelectPreset = { preset ->
        viewModel.updateUserLocation(preset.latitude, preset.longitude, preset.addressLabel, true)
        showAddressDialog = false
      },
      onCustomAddressEntered = { customText ->
        viewModel.searchAndSetAddress(customText)
        showAddressDialog = false
      },
      onDismiss = { showAddressDialog = false }
    )
  }
}

// -----------------------------------------------------------------------------
// Interactive Nature Map Card
// -----------------------------------------------------------------------------

@Composable
fun RecyclingMapCard(
  userLocation: com.example.data.model.UserLocationState,
  centers: List<RecyclingCenter>,
  selectedCenterId: String?,
  onCenterSelected: (String) -> Unit,
  zoomLevel: Float,
  onZoomChange: (Float) -> Unit
) {
  val context = LocalContext.current
  val infiniteTransition = rememberInfiniteTransition(label = "pulse")
  val pulseRadius by infiniteTransition.animateFloat(
    initialValue = 12f,
    targetValue = 30f,
    animationSpec = infiniteRepeatable(
      animation = tween(1500, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Restart
    ),
    label = "radius"
  )
  val pulseAlpha by infiniteTransition.animateFloat(
    initialValue = 0.55f,
    targetValue = 0.0f,
    animationSpec = infiniteRepeatable(
      animation = tween(1500, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Restart
    ),
    label = "alpha"
  )

  var panOffsetX by remember { mutableFloatStateOf(0f) }
  var panOffsetY by remember { mutableFloatStateOf(0f) }

  // Default to Live OpenStreetMap (OSM) Tile Engine
  var useOsmMapPreview by remember { mutableStateOf(true) }
  var focusSelectedFacilityOnMap by remember { mutableStateOf(false) }

  // Reset pan when user location changes
  LaunchedEffect(userLocation.latitude, userLocation.longitude) {
    panOffsetX = 0f
    panOffsetY = 0f
    focusSelectedFacilityOnMap = false
  }

  val osmZoom = remember(zoomLevel) {
    (14 + ((zoomLevel - 1.0f) * 3f).roundToInt()).coerceIn(11, 17)
  }

  val selectedCenterObj = remember(centers, selectedCenterId) {
    centers.find { it.id == selectedCenterId }
  }

  val targetLat = if (focusSelectedFacilityOnMap && selectedCenterObj != null) {
    selectedCenterObj.latitude
  } else {
    userLocation.latitude
  }
  val targetLon = if (focusSelectedFacilityOnMap && selectedCenterObj != null) {
    selectedCenterObj.longitude
  } else {
    userLocation.longitude
  }

  // Trigger real OpenStreetMap 256x256 PNG tile downloads in parallel for the current viewport
  var loadedTileCount by remember { mutableIntStateOf(0) }
  var tileRefreshTick by remember { mutableIntStateOf(0) }

  LaunchedEffect(targetLat, targetLon, osmZoom, useOsmMapPreview) {
    if (!useOsmMapPreview) return@LaunchedEffect
    val visibleTiles = OpenStreetMapService.getVisibleTileIndices(
      centerLat = targetLat,
      centerLon = targetLon,
      zoom = osmZoom,
      radiusTiles = 2
    )
    var count = 0
    visibleTiles.forEach { (tx, ty) ->
      if (OpenStreetMapService.getCachedTile(osmZoom, tx, ty) != null) {
        count++
      }
    }
    loadedTileCount = count

    visibleTiles.forEach { (tx, ty) ->
      launch {
        val bmp = OpenStreetMapService.fetchTileBitmap(osmZoom, tx, ty)
        if (bmp != null) {
          tileRefreshTick++
          loadedTileCount = visibleTiles.count { (x, y) ->
            OpenStreetMapService.getCachedTile(osmZoom, x, y) != null
          }
        }
      }
    }
  }

  // Paint objects for native canvas text labels on the map
  val pinLabelPaint = remember {
    android.graphics.Paint().apply {
      isAntiAlias = true
      color = android.graphics.Color.parseColor("#191C1A")
      textSize = 27f
      typeface = android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.BOLD)
    }
  }
  val pinSubLabelPaint = remember {
    android.graphics.Paint().apply {
      isAntiAlias = true
      color = android.graphics.Color.parseColor("#2E6F40")
      textSize = 23f
      typeface = android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.BOLD)
    }
  }
  val pillBgPaint = remember {
    android.graphics.Paint().apply {
      isAntiAlias = true
      color = android.graphics.Color.argb(238, 255, 255, 255)
      style = android.graphics.Paint.Style.FILL
    }
  }
  val pillBorderPaint = remember {
    android.graphics.Paint().apply {
      isAntiAlias = true
      color = android.graphics.Color.parseColor("#C7D8CC")
      style = android.graphics.Paint.Style.STROKE
      strokeWidth = 2.5f
    }
  }

  Card(
    modifier = Modifier
      .fillMaxWidth()
      .height(320.dp)
      .testTag("recycling_map_container"),
    shape = RoundedCornerShape(28.dp),
    colors = CardDefaults.cardColors(containerColor = MapLandColor),
    border = BorderStroke(1.dp, SleekCardBorder),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
  ) {
    Box(modifier = Modifier.fillMaxSize()) {
      // 1. Native OpenStreetMap Slippy Tile Canvas + Vector Base + Interactive Pins
      Canvas(
        modifier = Modifier
          .fillMaxSize()
          .testTag("osm_map_webview_preview")
          .pointerInput(centers, targetLat, targetLon, osmZoom) {
            detectTapGestures { tapOffset ->
              val centerX = size.width / 2f + panOffsetX
              val centerY = size.height / 2f + panOffsetY
              val tileRenderPx = 380f
              // Hit-test facility pins on tap
              val tappedCenter = centers.minByOrNull { center ->
                val pos = OpenStreetMapService.projectToCanvas(
                  lat = center.latitude,
                  lon = center.longitude,
                  centerLat = targetLat,
                  centerLon = targetLon,
                  zoom = osmZoom,
                  canvasCenterX = centerX,
                  canvasCenterY = centerY,
                  tileSizePx = tileRenderPx
                )
                hypot(tapOffset.x - pos.x, tapOffset.y - pos.y)
              }
              if (tappedCenter != null) {
                val pos = OpenStreetMapService.projectToCanvas(
                  lat = tappedCenter.latitude,
                  lon = tappedCenter.longitude,
                  centerLat = targetLat,
                  centerLon = targetLon,
                  zoom = osmZoom,
                  canvasCenterX = centerX,
                  canvasCenterY = centerY,
                  tileSizePx = tileRenderPx
                )
                if (hypot(tapOffset.x - pos.x, tapOffset.y - pos.y) <= 68f) {
                  onCenterSelected(tappedCenter.id)
                }
              }
            }
          }
          .pointerInput(Unit) {
            detectDragGestures { change, dragAmount ->
              change.consume()
              panOffsetX = (panOffsetX + dragAmount.x).coerceIn(-260f, 260f)
              panOffsetY = (panOffsetY + dragAmount.y).coerceIn(-200f, 200f)
            }
          }
      ) {
        // Read tileRefreshTick so Compose redraws immediately as each OSM tile arrives
        val _tick = tileRefreshTick
        val width = size.width
        val height = size.height
        val centerX = width / 2f + panOffsetX
        val centerY = height / 2f + panOffsetY
        val tileRenderPx = 380f
        val tileRenderInt = tileRenderPx.roundToInt() + 1

        // --- LAYER A: Instant Vector Base Map (always visible underneath / while tiles load) ---
        drawRect(color = MapLandColor)

        val step = 46f * zoomLevel
        var gx = (centerX % step) - step
        while (gx < width + step) {
          drawLine(
            color = MapGridColor,
            start = Offset(gx, 0f),
            end = Offset(gx, height),
            strokeWidth = 1.5f
          )
          gx += step
        }
        var gy = (centerY % step) - step
        while (gy < height + step) {
          drawLine(
            color = MapGridColor,
            start = Offset(0f, gy),
            end = Offset(width, gy),
            strokeWidth = 1.5f
          )
          gy += step
        }

        // Green parks & waterway features
        drawRoundRect(
          color = MapParkColor,
          topLeft = Offset(centerX - 140f * zoomLevel, centerY - 110f * zoomLevel),
          size = Size(105f * zoomLevel, 70f * zoomLevel),
          cornerRadius = CornerRadius(22f, 22f)
        )
        drawRoundRect(
          color = MapParkColor,
          topLeft = Offset(centerX + 55f * zoomLevel, centerY + 30f * zoomLevel),
          size = Size(95f * zoomLevel, 65f * zoomLevel),
          cornerRadius = CornerRadius(18f, 18f)
        )

        val riverPath = Path().apply {
          moveTo(0f, centerY + 75f)
          cubicTo(
            centerX - 100f, centerY + 95f,
            centerX + 35f, centerY - 20f,
            width, centerY - 75f
          )
        }
        drawPath(
          path = riverPath,
          color = MapWaterColor,
          style = Stroke(width = 24f * zoomLevel)
        )

        val mainRoadPath = Path().apply {
          moveTo(0f, centerY - 60f * zoomLevel)
          cubicTo(
            centerX - 80f, centerY - 40f,
            centerX + 60f, centerY + 30f,
            width, centerY + 50f * zoomLevel
          )
        }
        drawPath(
          path = mainRoadPath,
          color = MapRoadColor,
          style = Stroke(width = 14f * zoomLevel)
        )

        val crossRoadPath = Path().apply {
          moveTo(centerX - 90f * zoomLevel, 0f)
          cubicTo(
            centerX - 60f, centerY - 40f,
            centerX + 40f, centerY + 60f,
            centerX + 70f * zoomLevel, height
          )
        }
        drawPath(
          path = crossRoadPath,
          color = MapRoadColor,
          style = Stroke(width = 12f * zoomLevel)
        )

        // --- LAYER B: Real OpenStreetMap 256x256 Raster PNG Tiles (when OSM mode active) ---
        if (useOsmMapPreview) {
          val exactCenterTileX = OpenStreetMapService.lonToTileX(targetLon, osmZoom)
          val exactCenterTileY = OpenStreetMapService.latToTileY(targetLat, osmZoom)
          val baseTileX = floor(exactCenterTileX).toInt()
          val baseTileY = floor(exactCenterTileY).toInt()

          for (dy in -2..2) {
            for (dx in -2..2) {
              val tx = baseTileX + dx
              val ty = baseTileY + dy
              val tileBitmap = OpenStreetMapService.getCachedTile(osmZoom, tx, ty)
              if (tileBitmap != null) {
                val leftPx = centerX + ((tx.toDouble() - exactCenterTileX) * tileRenderPx).toFloat()
                val topPx = centerY + ((ty.toDouble() - exactCenterTileY) * tileRenderPx).toFloat()
                if (leftPx + tileRenderPx >= 0f && leftPx <= width &&
                  topPx + tileRenderPx >= 0f && topPx <= height
                ) {
                  drawImage(
                    image = tileBitmap,
                    dstOffset = IntOffset(leftPx.roundToInt(), topPx.roundToInt()),
                    dstSize = IntSize(tileRenderInt, tileRenderInt)
                  )
                }
              }
            }
          }
        }

        // --- LAYER C: Nearby Recycling Facility Pins & Callout Labels ---
        centers.forEach { center ->
          val pos = OpenStreetMapService.projectToCanvas(
            lat = center.latitude,
            lon = center.longitude,
            centerLat = targetLat,
            centerLon = targetLon,
            zoom = osmZoom,
            canvasCenterX = centerX,
            canvasCenterY = centerY,
            tileSizePx = tileRenderPx
          )
          val pinX = pos.x
          val pinY = pos.y

          val isSelected = center.id == selectedCenterId
          val pinRadius = if (isSelected) 22f else 15f

          if (isSelected) {
            drawCircle(
              color = center.primarySpecialty.primaryColor.copy(alpha = 0.28f),
              radius = pinRadius + 13f,
              center = Offset(pinX, pinY)
            )
            drawCircle(
              color = center.primarySpecialty.primaryColor.copy(alpha = 0.48f),
              radius = pinRadius + 6f,
              center = Offset(pinX, pinY)
            )
          }

          // White outer ring
          drawCircle(
            color = Color.White,
            radius = pinRadius + 3.5f,
            center = Offset(pinX, pinY)
          )
          // Bin specialty color fill
          drawCircle(
            color = center.primarySpecialty.primaryColor,
            radius = pinRadius,
            center = Offset(pinX, pinY)
          )
          // Inner white dot
          drawCircle(
            color = Color.White,
            radius = if (isSelected) 7f else 5f,
            center = Offset(pinX, pinY)
          )

          // Draw crisp floating facility name pill next to selected or nearby pins
          val shortName = center.name.take(20) + if (center.name.length > 20) "…" else ""
          val textWidth = pinLabelPaint.measureText(shortName)
          val pillW = textWidth + 24f
          val pillH = 38f
          val pillLeft = (pinX - pillW / 2f).coerceIn(12f, (width - pillW - 12f).coerceAtLeast(12f))
          val pillTop = pinY - pinRadius - pillH - 8f

          if (pillTop > 46f && pillTop < height - 56f) {
            val rect = android.graphics.RectF(pillLeft, pillTop, pillLeft + pillW, pillTop + pillH)
            drawContext.canvas.nativeCanvas.drawRoundRect(rect, 14f, 14f, pillBgPaint)
            drawContext.canvas.nativeCanvas.drawRoundRect(rect, 14f, 14f, pillBorderPaint)
            drawContext.canvas.nativeCanvas.drawText(
              shortName,
              pillLeft + 12f,
              pillTop + 27f,
              if (isSelected) pinSubLabelPaint else pinLabelPaint
            )
          }
        }

        // --- LAYER D: User "You Are Here" GPS Marker & Label ---
        val userPos = OpenStreetMapService.projectToCanvas(
          lat = userLocation.latitude,
          lon = userLocation.longitude,
          centerLat = targetLat,
          centerLon = targetLon,
          zoom = osmZoom,
          canvasCenterX = centerX,
          canvasCenterY = centerY,
          tileSizePx = tileRenderPx
        )
        drawCircle(
          color = SereneOceanBlue.copy(alpha = pulseAlpha),
          radius = pulseRadius * zoomLevel,
          center = userPos
        )
        drawCircle(
          color = Color.White,
          radius = 13f,
          center = userPos
        )
        drawCircle(
          color = SereneOceanBlue,
          radius = 9.5f,
          center = userPos
        )

        // Draw "You: <Area>" badge below user GPS pin
        val youLabel = "You • ${userLocation.addressLabel.replace(" (GPS)", "").take(18)}"
        val youWidth = pinSubLabelPaint.measureText(youLabel) + 24f
        val youLeft = (userPos.x - youWidth / 2f).coerceIn(12f, (width - youWidth - 12f).coerceAtLeast(12f))
        val youTop = userPos.y + 16f
        if (youTop < height - 48f) {
          val youRect = android.graphics.RectF(youLeft, youTop, youLeft + youWidth, youTop + 36f)
          drawContext.canvas.nativeCanvas.drawRoundRect(youRect, 14f, 14f, pillBgPaint)
          drawContext.canvas.nativeCanvas.drawRoundRect(youRect, 14f, 14f, pillBorderPaint)
          drawContext.canvas.nativeCanvas.drawText(
            youLabel,
            youLeft + 12f,
            youTop + 26f,
            pinSubLabelPaint
          )
        }
      }

      // 2. Interactive Facility Marker Click Overlays (Preserved for tests & pin selection)
      centers.forEach { center ->
        Box(
          modifier = Modifier
            .fillMaxSize()
            .padding(top = 0.dp),
          contentAlignment = Alignment.Center
        ) {
          val isSelected = center.id == selectedCenterId
          Box(
            modifier = Modifier
              .size(if (useOsmMapPreview) 1.dp else if (isSelected) 44.dp else 36.dp)
              .clip(CircleShape)
              .clickable {
                onCenterSelected(center.id)
                focusSelectedFacilityOnMap = true
              }
              .testTag("map_pin_${center.id}")
          )
        }
      }

      // 3. Top Controls Bar: Toggle between OpenStreetMap Live Preview & Eco Radar + Target Toggle
      Row(
        modifier = Modifier
          .align(Alignment.TopStart)
          .fillMaxWidth()
          .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Left badge: OpenStreetMap Current Location / Selected Facility Focus
        Surface(
          shape = RoundedCornerShape(14.dp),
          color = SleekSurface.copy(alpha = 0.94f),
          border = BorderStroke(1.dp, SleekCardBorder),
          shadowElevation = 2.dp,
          modifier = Modifier
            .clickable {
              if (useOsmMapPreview) {
                focusSelectedFacilityOnMap = !focusSelectedFacilityOnMap
              }
            }
            .testTag("btn_osm_target_toggle")
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(
              modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(if (useOsmMapPreview) SleekGreenPrimary else SereneOceanBlue)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = if (useOsmMapPreview) {
                if (focusSelectedFacilityOnMap && selectedCenterObj != null) {
                  "OSM: ${selectedCenterObj.name.take(18)}…"
                } else if (loadedTileCount > 0) {
                  "OSM Live (${userLocation.addressLabel.replace(" (GPS)", "").take(14)})"
                } else {
                  "OSM Map (${userLocation.addressLabel.replace(" (GPS)", "").take(14)})"
                }
              } else {
                "Your Location • Drag to explore"
              },
              style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.SemiBold,
                color = SleekTextPrimary
              )
            )
          }
        }

        // Right toggle chip: Switch between OpenStreetMap Live & Eco Radar
        Surface(
          shape = RoundedCornerShape(14.dp),
          color = if (useOsmMapPreview) SleekGreenPrimary else SleekSurface.copy(alpha = 0.94f),
          border = BorderStroke(1.dp, if (useOsmMapPreview) SleekGreenPrimary else SleekCardBorder),
          shadowElevation = 2.dp,
          modifier = Modifier
            .clickable { useOsmMapPreview = !useOsmMapPreview }
            .testTag("btn_toggle_map_mode")
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              imageVector = Icons.Default.Map,
              contentDescription = "Switch Map View",
              tint = if (useOsmMapPreview) Color.White else SereneOceanBlue,
              modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = if (useOsmMapPreview) "Radar View" else "OSM Live",
              style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                color = if (useOsmMapPreview) Color.White else SleekTextPrimary
              )
            )
          }
        }
      }

      // 4. Bottom-Left Action: Open Current Location or Facility in OpenStreetMap / Maps App
      Surface(
        modifier = Modifier
          .align(Alignment.BottomStart)
          .padding(14.dp)
          .clickable {
            val lat = if (focusSelectedFacilityOnMap && selectedCenterObj != null) {
              selectedCenterObj.latitude
            } else {
              userLocation.latitude
            }
            val lon = if (focusSelectedFacilityOnMap && selectedCenterObj != null) {
              selectedCenterObj.longitude
            } else {
              userLocation.longitude
            }
            val osmWebUri = Uri.parse(
              "https://www.openstreetmap.org/?mlat=$lat&mlon=$lon#map=$osmZoom/$lat/$lon"
            )
            try {
              context.startActivity(Intent(Intent.ACTION_VIEW, osmWebUri))
            } catch (_: Exception) {
              Toast.makeText(context, "OpenStreetMap ($lat, $lon)", Toast.LENGTH_SHORT).show()
            }
          }
          .testTag("btn_open_in_osm"),
        shape = RoundedCornerShape(14.dp),
        color = SleekSurface.copy(alpha = 0.95f),
        border = BorderStroke(1.dp, SleekCardBorder),
        shadowElevation = 2.dp
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(
            imageVector = Icons.Default.Directions,
            contentDescription = "Open in OpenStreetMap",
            tint = SereneOceanBlue,
            modifier = Modifier.size(15.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "Open in OpenStreetMap",
            style = MaterialTheme.typography.labelSmall.copy(
              fontWeight = FontWeight.Bold,
              color = SereneOceanBlue
            )
          )
        }
      }

      // 5. Floating Map Controls (Zoom & Recenter)
      Column(
        modifier = Modifier
          .align(Alignment.BottomEnd)
          .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Surface(
          shape = CircleShape,
          color = SleekSurface.copy(alpha = 0.95f),
          border = BorderStroke(1.dp, SleekCardBorder),
          shadowElevation = 2.dp,
          modifier = Modifier
            .size(38.dp)
            .clip(CircleShape)
            .clickable {
              panOffsetX = 0f
              panOffsetY = 0f
              focusSelectedFacilityOnMap = false
            }
            .testTag("btn_recenter_map")
        ) {
          Box(contentAlignment = Alignment.Center) {
            Icon(
              imageVector = Icons.Default.Navigation,
              contentDescription = "Recenter on Current Location",
              tint = SereneOceanBlue,
              modifier = Modifier.size(18.dp)
            )
          }
        }

        Surface(
          shape = RoundedCornerShape(16.dp),
          color = SleekSurface.copy(alpha = 0.95f),
          border = BorderStroke(1.dp, SleekCardBorder),
          shadowElevation = 2.dp,
          modifier = Modifier.width(38.dp)
        ) {
          Column(
            modifier = Modifier.width(38.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            IconButton(
              onClick = { onZoomChange(0.25f) },
              modifier = Modifier.size(38.dp).testTag("btn_zoom_in")
            ) {
              Icon(Icons.Default.Add, contentDescription = "Zoom In", tint = SleekTextPrimary, modifier = Modifier.size(18.dp))
            }
            androidx.compose.material3.HorizontalDivider(thickness = 0.5.dp, color = SleekDivider)
            IconButton(
              onClick = { onZoomChange(-0.25f) },
              modifier = Modifier.size(38.dp).testTag("btn_zoom_out")
            ) {
              Icon(Icons.Default.Remove, contentDescription = "Zoom Out", tint = SleekTextPrimary, modifier = Modifier.size(18.dp))
            }
          }
        }
      }
    }
  }
}

// -----------------------------------------------------------------------------
// Selected Facility Deep-Dive Details Card
// -----------------------------------------------------------------------------

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SelectedFacilityCard(
  center: RecyclingCenter,
  distanceMiles: Double,
  onClose: () -> Unit,
  onGetDirections: () -> Unit,
  onCallFacility: () -> Unit
) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .testTag("selected_facility_detail_card"),
    shape = RoundedCornerShape(26.dp),
    colors = CardDefaults.cardColors(containerColor = SleekSurface),
    border = BorderStroke(1.5.dp, center.primarySpecialty.primaryColor.copy(alpha = 0.4f)),
    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(20.dp)
    ) {
      // Header with specialty badge, close button & distance
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Surface(
            shape = CircleShape,
            color = center.primarySpecialty.backgroundColor
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Box(
                modifier = Modifier
                  .size(8.dp)
                  .clip(CircleShape)
                  .background(center.primarySpecialty.primaryColor)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = center.categoryTag,
                style = MaterialTheme.typography.labelSmall.copy(
                  fontWeight = FontWeight.Bold,
                  color = center.primarySpecialty.primaryColor
                )
              )
            }
          }

          Spacer(modifier = Modifier.height(8.dp))

          Text(
            text = center.name,
            style = MaterialTheme.typography.titleLarge.copy(
              fontWeight = FontWeight.Bold,
              color = SleekTextPrimary
            )
          )

          Spacer(modifier = Modifier.height(4.dp))

          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.LocationOn,
              contentDescription = null,
              tint = SleekTextSecondary,
              modifier = Modifier.size(15.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = center.address,
              style = MaterialTheme.typography.bodySmall.copy(color = SleekTextSecondary)
            )
          }
        }

        IconButton(
          onClick = onClose,
          modifier = Modifier.testTag("btn_close_detail")
        ) {
          Icon(
            imageVector = Icons.Default.Close,
            contentDescription = "Close",
            tint = SleekTextSecondary
          )
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Quick highlights: Distance, Rating, Open Status
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Surface(
          shape = RoundedCornerShape(12.dp),
          color = SoftRiverBlue,
          modifier = Modifier.weight(1f)
        ) {
          Column(modifier = Modifier.padding(10.dp)) {
            Text("PROXIMITY", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = SereneOceanBlue)
            Text("$distanceMiles mi away", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = DeepRiverBlue)
          }
        }

        Surface(
          shape = RoundedCornerShape(12.dp),
          color = SleekPill,
          modifier = Modifier.weight(1f)
        ) {
          Column(modifier = Modifier.padding(10.dp)) {
            Text("STATUS", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = SleekGreenPrimary)
            Text(if (center.isOpenNow) "Open Today" else "Closed", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = EucalyptusGreen)
          }
        }

        Surface(
          shape = RoundedCornerShape(12.dp),
          color = MutedSand,
          modifier = Modifier.weight(1f)
        ) {
          Column(modifier = Modifier.padding(10.dp)) {
            Text("RATING", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = WarmEarthTone)
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.Star, contentDescription = null, tint = SoftClay, modifier = Modifier.size(13.dp))
              Spacer(modifier = Modifier.width(2.dp))
              Text("${center.rating} (${center.reviewsCount})", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = SleekTextPrimary)
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Operating Hours Section
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
          imageVector = Icons.Default.AccessTime,
          contentDescription = null,
          tint = SleekGreenPrimary,
          modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = "Operating Hours",
          style = MaterialTheme.typography.titleSmall.copy(
            fontWeight = FontWeight.Bold,
            color = SleekTextPrimary
          )
        )
      }
      Spacer(modifier = Modifier.height(6.dp))
      Text(
        text = center.operatingHours,
        style = MaterialTheme.typography.bodySmall.copy(color = SleekTextBody)
      )

      if (center.scheduleByDay.isNotEmpty()) {
        Spacer(modifier = Modifier.height(6.dp))
        Surface(
          shape = RoundedCornerShape(12.dp),
          color = SleekDivider.copy(alpha = 0.5f),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(10.dp)) {
            center.scheduleByDay.forEach { (days, hours) ->
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(vertical = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Text(days, fontSize = 11.sp, color = SleekTextSecondary, fontWeight = FontWeight.Medium)
                Text(hours, fontSize = 11.sp, color = SleekTextPrimary, fontWeight = FontWeight.SemiBold)
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Accepted Materials Section
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
          imageVector = Icons.Default.Recycling,
          contentDescription = null,
          tint = SleekGreenPrimary,
          modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = "Accepted Materials & Streams",
          style = MaterialTheme.typography.titleSmall.copy(
            fontWeight = FontWeight.Bold,
            color = SleekTextPrimary
          )
        )
      }
      Spacer(modifier = Modifier.height(8.dp))
      FlowRow(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        center.acceptedMaterials.forEach { material ->
          Surface(
            shape = RoundedCornerShape(10.dp),
            color = MutedSand,
            border = BorderStroke(1.dp, SleekCardBorder)
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Box(
                modifier = Modifier
                  .size(6.dp)
                  .clip(CircleShape)
                  .background(center.primarySpecialty.primaryColor)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = material,
                style = MaterialTheme.typography.labelSmall.copy(
                  color = SleekTextPrimary,
                  fontSize = 11.sp
                )
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Specific Preparation Instructions Section
      Surface(
        shape = RoundedCornerShape(16.dp),
        color = SoftRiverBlue.copy(alpha = 0.4f),
        border = BorderStroke(1.dp, SereneOceanBlue.copy(alpha = 0.2f)),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.Info,
              contentDescription = null,
              tint = SereneOceanBlue,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "Specific Preparation Instructions",
              style = MaterialTheme.typography.titleSmall.copy(
                fontWeight = FontWeight.Bold,
                color = DeepRiverBlue
              )
            )
          }

          Spacer(modifier = Modifier.height(8.dp))

          center.preparationInstructions.forEach { instruction ->
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 3.dp),
              verticalAlignment = Alignment.Top
            ) {
              Text(
                text = "•",
                color = SereneOceanBlue,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(end = 6.dp)
              )
              Text(
                text = instruction,
                style = MaterialTheme.typography.bodySmall.copy(
                  color = SleekTextBody,
                  lineHeight = 17.sp
                )
              )
            }
          }
        }
      }

      if (center.notes.isNotEmpty()) {
        Spacer(modifier = Modifier.height(10.dp))
        Text(
          text = "Note: ${center.notes}",
          style = MaterialTheme.typography.labelSmall.copy(
            color = SleekTextSecondary,
            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
          )
        )
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Action Buttons: Get Directions & Call Facility
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        Button(
          onClick = onGetDirections,
          shape = RoundedCornerShape(16.dp),
          colors = ButtonDefaults.buttonColors(
            containerColor = SleekGreenPrimary,
            contentColor = Color.White
          ),
          modifier = Modifier
            .weight(1f)
            .testTag("btn_get_directions")
        ) {
          Icon(Icons.Default.Directions, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("Get Directions", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
        }

        OutlinedButton(
          onClick = onCallFacility,
          shape = RoundedCornerShape(16.dp),
          border = BorderStroke(1.dp, SleekCardBorder),
          colors = ButtonDefaults.outlinedButtonColors(contentColor = SleekTextPrimary),
          modifier = Modifier
            .weight(1f)
            .testTag("btn_call_facility")
        ) {
          Icon(Icons.Default.Call, contentDescription = null, tint = SereneOceanBlue, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("Call Facility", fontWeight = FontWeight.Medium, fontSize = 13.sp)
        }
      }
    }
  }
}

// -----------------------------------------------------------------------------
// Facility Summary List Card
// -----------------------------------------------------------------------------

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FacilitySummaryCard(
  center: RecyclingCenter,
  distanceMiles: Double,
  isSelected: Boolean,
  onSelect: () -> Unit
) {
  val borderColor by animateColorAsState(
    targetValue = if (isSelected) center.primarySpecialty.primaryColor else SleekCardBorder,
    label = "border"
  )

  Card(
    modifier = Modifier
      .fillMaxWidth()
      .clickable { onSelect() }
      .testTag("facility_card_${center.id}"),
    shape = RoundedCornerShape(22.dp),
    colors = CardDefaults.cardColors(
      containerColor = if (isSelected) SleekPillLight else SleekSurface
    ),
    border = BorderStroke(if (isSelected) 1.5.dp else 1.dp, borderColor),
    elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 2.dp else 1.dp)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = center.name,
            style = MaterialTheme.typography.titleMedium.copy(
              fontWeight = FontWeight.Bold,
              color = SleekTextPrimary
            )
          )
          Spacer(modifier = Modifier.height(2.dp))
          Text(
            text = center.address,
            style = MaterialTheme.typography.bodySmall.copy(
              color = SleekTextSecondary
            )
          )
        }

        Surface(
          shape = CircleShape,
          color = SoftRiverBlue
        ) {
          Text(
            text = "$distanceMiles mi",
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall.copy(
              fontWeight = FontWeight.Bold,
              color = SereneOceanBlue
            )
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Accepted streams preview chips
      FlowRow(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        center.acceptedWasteTypes.forEach { type ->
          Surface(
            shape = CircleShape,
            color = type.backgroundColor
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Box(
                modifier = Modifier
                  .size(6.dp)
                  .clip(CircleShape)
                  .background(type.primaryColor)
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = type.binColorName,
                style = MaterialTheme.typography.labelSmall.copy(
                  fontSize = 10.sp,
                  fontWeight = FontWeight.SemiBold,
                  color = type.primaryColor
                )
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = center.operatingHours.substringBefore("•"),
          style = MaterialTheme.typography.labelSmall.copy(
            color = SleekTextSecondary
          )
        )

        TextButton(
          onClick = onSelect,
          contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
        ) {
          Text(
            text = if (isSelected) "Viewing Details" else "View Details & Prep Rules →",
            style = MaterialTheme.typography.labelSmall.copy(
              fontWeight = FontWeight.Bold,
              color = center.primarySpecialty.primaryColor
            )
          )
        }
      }
    }
  }
}

// -----------------------------------------------------------------------------
// Address & Location Picker Dialog
// -----------------------------------------------------------------------------

@Composable
fun AddressSelectionDialog(
  currentAddress: String,
  presetNeighborhoods: List<com.example.data.model.UserLocationState>,
  onSelectPreset: (com.example.data.model.UserLocationState) -> Unit,
  onCustomAddressEntered: (String) -> Unit,
  onDismiss: () -> Unit
) {
  var manualInput by remember { mutableStateOf("") }

  AlertDialog(
    onDismissRequest = onDismiss,
    shape = RoundedCornerShape(26.dp),
    containerColor = SleekSurface,
    title = {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
          imageVector = Icons.Default.EditLocation,
          contentDescription = null,
          tint = SleekGreenPrimary,
          modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = "Set Search Location",
          style = MaterialTheme.typography.titleLarge.copy(
            fontWeight = FontWeight.Bold,
            color = SleekTextPrimary
          )
        )
      }
    },
    text = {
      Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
      ) {
        Text(
          text = "Choose your location to calculate accurate distances to nearby recycling facilities.",
          style = MaterialTheme.typography.bodySmall.copy(color = SleekTextSecondary)
        )

        // Manual text entry
        OutlinedTextField(
          value = manualInput,
          onValueChange = { manualInput = it },
          placeholder = { Text("Enter street address, city, or zip code...", fontSize = 12.sp, color = SleekTextSecondary) },
          shape = RoundedCornerShape(16.dp),
          colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = SleekTextPrimary,
            unfocusedTextColor = SleekTextPrimary,
            cursorColor = SleekGreenPrimary,
            focusedPlaceholderColor = SleekTextSecondary,
            unfocusedPlaceholderColor = SleekTextSecondary,
            focusedContainerColor = SleekSurface,
            unfocusedContainerColor = SleekSurface,
            focusedBorderColor = SleekGreenPrimary,
            unfocusedBorderColor = SleekCardBorder
          ),
          singleLine = true,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("input_custom_address")
        )

        if (manualInput.isNotEmpty()) {
          Button(
            onClick = { onCustomAddressEntered(manualInput) },
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
              containerColor = SleekGreenPrimary,
              contentColor = Color.White
            ),
            modifier = Modifier
              .fillMaxWidth()
              .testTag("btn_confirm_address")
          ) {
            Text("Set Address: $manualInput", color = Color.White, maxLines = 1, overflow = TextOverflow.Ellipsis)
          }
        }

        Text(
          text = "OR SELECT NEARBY DISTRICT PRESETS:",
          style = MaterialTheme.typography.labelSmall.copy(
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = SleekTextSecondary,
            letterSpacing = 0.5.sp
          )
        )

        Column(
          modifier = Modifier.fillMaxWidth(),
          verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          presetNeighborhoods.forEach { preset ->
            val isCurrent = preset.addressLabel == currentAddress
            Surface(
              shape = RoundedCornerShape(14.dp),
              color = if (isCurrent) SleekPillLight else MutedSand,
              border = BorderStroke(1.dp, if (isCurrent) SleekGreenPrimary.copy(alpha = 0.4f) else SleekCardBorder),
              modifier = Modifier
                .fillMaxWidth()
                .clickable { onSelectPreset(preset) }
                .testTag("preset_location_${preset.addressLabel.replace(" ", "_")}")
            ) {
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = if (isCurrent) SleekGreenPrimary else SleekTextSecondary,
                    modifier = Modifier.size(16.dp)
                  )
                  Spacer(modifier = Modifier.width(8.dp))
                  Text(
                    text = preset.addressLabel,
                    style = MaterialTheme.typography.bodyMedium.copy(
                      fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Medium,
                      color = SleekTextPrimary
                    )
                  )
                }

                if (isCurrent) {
                  Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Active",
                    tint = SleekGreenPrimary,
                    modifier = Modifier.size(16.dp)
                  )
                }
              }
            }
          }
        }
      }
    },
    confirmButton = {
      TextButton(
        onClick = onDismiss,
        modifier = Modifier.testTag("btn_dismiss_dialog")
      ) {
        Text("Cancel", color = SleekTextSecondary)
      }
    }
  )
}
