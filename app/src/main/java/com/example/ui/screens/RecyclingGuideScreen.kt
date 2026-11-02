package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.api.GeminiApiService
import com.example.data.model.WasteBinType
import com.example.data.model.WasteDisposalGuideResult
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.ForestGreen
import com.example.ui.theme.MintLight
import com.example.ui.theme.PaleLeaf
import com.example.ui.theme.SageAccent
import com.example.ui.theme.SleekBackground
import com.example.ui.theme.SleekCardBorder
import com.example.ui.theme.SleekDivider
import com.example.ui.theme.SleekGreenPrimary
import com.example.ui.theme.SleekPill
import com.example.ui.theme.SleekPillLight
import com.example.ui.theme.SleekSurface
import com.example.ui.theme.SleekTextBody
import com.example.ui.theme.SleekTextPrimary
import com.example.ui.theme.SleekTextSecondary

data class GuideItem(
  val name: String,
  val binType: WasteBinType,
  val preparationTip: String,
  val decompositionPeriod: String,
  val doItems: List<String>,
  val dontItems: List<String>
)

@Composable
fun RecyclingGuideScreen(
  modifier: Modifier = Modifier
) {
  var searchQuery by remember { mutableStateOf("") }
  var selectedFilter by remember { mutableStateOf<WasteBinType?>(null) }
  var aiGuideResult by remember { mutableStateOf<WasteDisposalGuideResult?>(null) }
  var isSearchingAi by remember { mutableStateOf(false) }
  val coroutineScope = rememberCoroutineScope()

  val quickWasteSearches = remember {
    listOf(
      "Plastic Bottle",
      "Banana Peel",
      "AA Battery",
      "Cooked Rice",
      "Cardboard Box",
      "Vegetable Scraps",
      "Aluminum Can",
      "Glass Jar"
    )
  }

  // Automatically fetch AI Disposal & Decomposition guide when user types a query
  LaunchedEffect(searchQuery) {
    val trimmed = searchQuery.trim()
    if (trimmed.length < 2) {
      aiGuideResult = null
      isSearchingAi = false
    } else {
      isSearchingAi = true
      delay(450)
      val result = GeminiApiService.fetchWasteDecompositionGuide(trimmed)
      aiGuideResult = result
      isSearchingAi = false
    }
  }

  val guideItems = remember {
    listOf(
      GuideItem(
        name = "Food Scraps & Organic Waste",
        binType = WasteBinType.ORGANIC_GREEN,
        preparationTip = "Keep separate from dry recyclables. Chop into small pieces for natural home composting or municipal green bin collection.",
        decompositionPeriod = "2 - 6 Weeks",
        doItems = listOf(
          "Fruit & vegetable peelings",
          "Used coffee grounds & loose tea leaves",
          "Crushed eggshells",
          "Dry garden leaves & flowers",
          "Leftover food scraps"
        ),
        dontItems = listOf(
          "Plastic stickers on fruit",
          "Chemically treated wood",
          "Heavy cooking oil or grease",
          "Synthetic nylon tea bags"
        )
      ),
      GuideItem(
        name = "Plastic Waste & Containers",
        binType = WasteBinType.RECYCLABLE_BLUE,
        preparationTip = "Empty all liquids. Quick rinse with water. Flatten bottles to save volume.",
        decompositionPeriod = "450 Years in Landfill",
        doItems = listOf("Rigid plastic bottles (#1 PET, #2 HDPE)", "Clean yogurt tubs", "Shampoo bottles", "Clear plastic jugs"),
        dontItems = listOf("Plastic cling wrap", "Chip bags / Metallized film", "Styrofoam containers", "Straws")
      ),
      GuideItem(
        name = "Paper & Cardboard",
        binType = WasteBinType.RECYCLABLE_BLUE,
        preparationTip = "Remove plastic packaging tape. Flatten boxes completely and keep dry before placing in the Blue Bin.",
        decompositionPeriod = "3 - 6 Weeks",
        doItems = listOf("Clean corrugated cardboard", "Newspapers & magazines", "Office paper", "Cereal packaging boxes"),
        dontItems = listOf("Greasy pizza boxes (compost instead)", "Thermal cash receipts (BPA coated)", "Waxed drink cups")
      ),
      GuideItem(
        name = "Metal & Aluminum Cans",
        binType = WasteBinType.RECYCLABLE_BLUE,
        preparationTip = "Rinse out food remains. Tabs can stay attached. Do not need to remove paper labels.",
        decompositionPeriod = "200 - 500 Years",
        doItems = listOf("Aluminum beverage cans", "Clean tin soup cans", "Metal bottle crowns", "Clean aluminum foil"),
        dontItems = listOf("Aerosol spray cans with residue", "Paint cans", "Gas canisters", "Needles/Sharps")
      ),
      GuideItem(
        name = "Electronic Waste (E-Waste)",
        binType = WasteBinType.E_WASTE_YELLOW,
        preparationTip = "Back up and erase personal data. Keep parts together for metal recovery.",
        decompositionPeriod = "1,000+ Years (Toxic)",
        doItems = listOf("Old mobile phones & tablets", "Power cables & USB cords", "Computer motherboards & RAM", "Calculators & remote controls"),
        dontItems = listOf("Leaking battery packs (treat as hazardous)", "Broken CRT cathode glass", "Loose incandescent bulbs")
      ),
      GuideItem(
        name = "Batteries & Hazardous Chemicals",
        binType = WasteBinType.HAZARDOUS_RED,
        preparationTip = "Tape positive/negative terminals with electrical tape to prevent accidental short circuits.",
        decompositionPeriod = "Non-degradable (Groundwater Risk)",
        doItems = listOf("AA, AAA, 9V alkaline batteries", "Rechargeable Lithium-ion cells", "Expired prescription pills", "Paint thinners & solvent bottles"),
        dontItems = listOf("Tossing in municipal garbage (causes truck fires)", "Pouring chemical solvents down domestic drains")
      ),
      GuideItem(
        name = "General Non-Recyclable Residuals",
        binType = WasteBinType.LANDFILL_GREY,
        preparationTip = "Use as last resort. Ensure items are completely free of hazardous/flammable liquids.",
        decompositionPeriod = "Indefinite / Permanent",
        doItems = listOf("Multi-layer foil snack pouches", "Contaminated wipes", "Broken ceramics", "Toothbrushes"),
        dontItems = listOf("Recyclable plastics & paper", "Lithium batteries", "Biodegradable food waste")
      )
    )
  }

  val filteredItems = guideItems.filter { item ->
    val matchesFilter = selectedFilter == null || item.binType == selectedFilter
    val matchesSearch = searchQuery.isBlank() ||
      item.name.contains(searchQuery, ignoreCase = true) ||
      item.doItems.any { it.contains(searchQuery, ignoreCase = true) } ||
      item.dontItems.any { it.contains(searchQuery, ignoreCase = true) }
    matchesFilter && matchesSearch
  }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .padding(bottom = 32.dp),
    contentPadding = PaddingValues(16.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    // Sleek Header + AI Waste Disposal & Conditional Decomposition Search
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = SleekSurface),
        border = BorderStroke(1.dp, SleekCardBorder)
      ) {
        Column(modifier = Modifier.padding(20.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(SleekPill),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.Eco,
                contentDescription = null,
                tint = SleekGreenPrimary,
                modifier = Modifier.size(18.dp)
              )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Text(
              text = "Smart Bin & Disposal Guide",
              style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                color = SleekGreenPrimary
              )
            )
          }
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = "Search any waste item to see how to dispose of it properly. If the item is decomposable, AI also suggests natural composting ideas.",
            style = MaterialTheme.typography.bodyMedium.copy(
              color = SleekTextSecondary,
              lineHeight = 19.sp
            )
          )

          Spacer(modifier = Modifier.height(14.dp))

          // Search Field
          OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = {
              Text(
                "Search waste item to dispose (e.g. plastic bottle, banana peel, battery)...",
                color = SleekTextSecondary,
                fontSize = 13.sp
              )
            },
            leadingIcon = {
              Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = SleekGreenPrimary
              )
            },
            trailingIcon = {
              if (searchQuery.isNotEmpty()) {
                IconButton(
                  onClick = {
                    searchQuery = ""
                    aiGuideResult = null
                  },
                  modifier = Modifier.testTag("btn_clear_guide_search")
                ) {
                  Icon(
                    imageVector = Icons.Default.Clear,
                    contentDescription = "Clear",
                    tint = SleekTextSecondary
                  )
                }
              }
            },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(
              onSearch = {
                if (searchQuery.trim().isNotEmpty()) {
                  coroutineScope.launch {
                    isSearchingAi = true
                    aiGuideResult = GeminiApiService.fetchWasteDecompositionGuide(searchQuery.trim())
                    isSearchingAi = false
                  }
                }
              }
            ),
            modifier = Modifier
              .fillMaxWidth()
              .testTag("guide_search_field"),
            shape = RoundedCornerShape(18.dp),
            colors = OutlinedTextFieldDefaults.colors(
              focusedTextColor = SleekTextPrimary,
              unfocusedTextColor = SleekTextPrimary,
              cursorColor = SleekGreenPrimary,
              focusedPlaceholderColor = SleekTextSecondary,
              unfocusedPlaceholderColor = SleekTextSecondary,
              focusedContainerColor = SleekBackground,
              unfocusedContainerColor = SleekBackground,
              focusedBorderColor = SleekGreenPrimary,
              unfocusedBorderColor = SleekCardBorder
            ),
            singleLine = true
          )

          Spacer(modifier = Modifier.height(10.dp))

          // Quick-Tap Waste Search Chips
          Text(
            text = "QUICK WASTE DISPOSAL LOOKUP:",
            style = MaterialTheme.typography.labelSmall.copy(
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold,
              color = SleekTextSecondary,
              letterSpacing = 0.5.sp
            )
          )
          Spacer(modifier = Modifier.height(6.dp))
          LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            items(quickWasteSearches) { sample ->
              val isSelectedSample = searchQuery.equals(sample, ignoreCase = true)
              Surface(
                shape = RoundedCornerShape(12.dp),
                color = if (isSelectedSample) SleekGreenPrimary else SleekPillLight,
                border = BorderStroke(1.dp, if (isSelectedSample) SleekGreenPrimary else SleekCardBorder),
                modifier = Modifier
                  .clickable { searchQuery = sample }
                  .testTag("quick_guide_chip_${sample.replace(" ", "_")}")
              ) {
                Text(
                  text = sample,
                  modifier = Modifier.padding(horizontal = 11.dp, vertical = 6.dp),
                  style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = if (isSelectedSample) Color.White else SleekGreenPrimary
                  )
                )
              }
            }
          }

          if (searchQuery.trim().isNotEmpty()) {
            Spacer(modifier = Modifier.height(10.dp))
            Button(
              onClick = {
                coroutineScope.launch {
                  isSearchingAi = true
                  aiGuideResult = GeminiApiService.fetchWasteDecompositionGuide(searchQuery.trim())
                  isSearchingAi = false
                }
              },
              shape = RoundedCornerShape(14.dp),
              colors = ButtonDefaults.buttonColors(
                containerColor = SleekGreenPrimary,
                contentColor = Color.White
              ),
              modifier = Modifier
                .fillMaxWidth()
                .testTag("btn_ask_ai_decomposition")
            ) {
              Icon(
                imageVector = Icons.Default.AutoAwesome,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(16.dp)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "Check Disposal Guide for '${searchQuery.trim()}'",
                color = Color.White,
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp
              )
            }
          }
        }
      }
    }

    // Loading state for AI Lookup
    if (isSearchingAi) {
      item {
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .testTag("ai_guide_loading_card"),
          shape = RoundedCornerShape(24.dp),
          colors = CardDefaults.cardColors(containerColor = SleekSurface),
          border = BorderStroke(1.dp, SleekCardBorder)
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
          ) {
            CircularProgressIndicator(
              modifier = Modifier.size(22.dp),
              color = SleekGreenPrimary,
              strokeWidth = 2.5.dp
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
              text = "Checking disposal & decomposition details for '${searchQuery.trim()}'...",
              style = MaterialTheme.typography.bodySmall.copy(
                fontWeight = FontWeight.Medium,
                color = SleekTextPrimary
              )
            )
          }
        }
      }
    }

    // AI Disposal & Conditional Decomposition Result Card
    val currentAiResult = aiGuideResult
    if (!isSearchingAi && currentAiResult != null && searchQuery.trim().isNotEmpty()) {
      item {
        AiDecompositionGuideCard(result = currentAiResult)
      }
    }

    // Filter Chips
    item {
      LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 4.dp)
      ) {
        item {
          FilterChip(
            selected = selectedFilter == null,
            onClick = { selectedFilter = null },
            label = { Text("All Streams") },
            shape = RoundedCornerShape(14.dp),
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = SleekGreenPrimary,
              selectedLabelColor = Color.White
            )
          )
        }

        items(WasteBinType.values()) { bin ->
          FilterChip(
            selected = selectedFilter == bin,
            onClick = { selectedFilter = if (selectedFilter == bin) null else bin },
            label = { Text(bin.binColorName) },
            shape = RoundedCornerShape(14.dp),
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = bin.primaryColor,
              selectedLabelColor = Color.White
            )
          )
        }
      }
    }

    // Guide Items List
    items(filteredItems) { guide ->
      GuideCard(guide = guide)
    }

    // Did You Know Card
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
          containerColor = SleekSurface
        ),
        border = androidx.compose.foundation.BorderStroke(1.dp, SleekCardBorder)
      ) {
        Row(
          modifier = Modifier.padding(18.dp),
          verticalAlignment = Alignment.Top
        ) {
          Box(
            modifier = Modifier
              .size(36.dp)
              .clip(CircleShape)
              .background(SleekPill),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.Lightbulb,
              contentDescription = null,
              tint = SleekGreenPrimary,
              modifier = Modifier.size(20.dp)
            )
          }
          Spacer(modifier = Modifier.width(12.dp))
          Column {
            Text(
              text = "Eco Awareness: The Contamination Rule",
              style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = SleekTextPrimary
              )
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = "Even a single unrinsed jar of peanut butter or oily pizza box can contaminate an entire recycling bale, turning 500kg of recyclable paper into landfill waste. When in doubt: empty, rinse, and keep dry!",
              style = MaterialTheme.typography.bodySmall.copy(
                color = SleekTextSecondary,
                lineHeight = 18.sp
              )
            )
          }
        }
      }
    }
  }
}

@Composable
fun GuideCard(guide: GuideItem) {
  Card(
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(24.dp),
    colors = CardDefaults.cardColors(
      containerColor = SleekSurface
    ),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    border = androidx.compose.foundation.BorderStroke(1.dp, SleekCardBorder)
  ) {
    Column(modifier = Modifier.padding(18.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = guide.name,
            style = MaterialTheme.typography.titleMedium.copy(
              fontWeight = FontWeight.Bold,
              color = SleekTextPrimary
            )
          )
          Text(
            text = guide.binType.title,
            style = MaterialTheme.typography.bodySmall.copy(
              color = guide.binType.primaryColor,
              fontWeight = FontWeight.SemiBold
            )
          )
        }

        Surface(
          shape = CircleShape,
          color = guide.binType.backgroundColor
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              imageVector = Icons.Default.HourglassBottom,
              contentDescription = null,
              tint = guide.binType.primaryColor,
              modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = guide.decompositionPeriod,
              style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                color = guide.binType.primaryColor
              )
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Preparation Tip
      Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = SleekPill
      ) {
        Row(
          modifier = Modifier.padding(10.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(
            imageVector = Icons.Default.Info,
            contentDescription = null,
            tint = SleekGreenPrimary,
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = guide.preparationTip,
            style = MaterialTheme.typography.bodySmall.copy(
              color = SleekGreenPrimary,
              fontWeight = FontWeight.Medium
            )
          )
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Acceptable Items
      Text(
        text = "YES — Place in Bin:",
        style = MaterialTheme.typography.labelMedium.copy(
          fontWeight = FontWeight.Bold,
          color = SleekGreenPrimary
        )
      )
      Spacer(modifier = Modifier.height(4.dp))
      guide.doItems.forEach { item ->
        Row(
          modifier = Modifier.padding(vertical = 2.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            tint = SleekGreenPrimary,
            modifier = Modifier.size(14.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(text = item, style = MaterialTheme.typography.bodySmall.copy(color = SleekTextBody))
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Unacceptable Items
      Text(
        text = "NO — Keep Out:",
        style = MaterialTheme.typography.labelMedium.copy(
          fontWeight = FontWeight.Bold,
          color = Color(0xFFC62828)
        )
      )
      Spacer(modifier = Modifier.height(4.dp))
      guide.dontItems.forEach { item ->
        Row(
          modifier = Modifier.padding(vertical = 2.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(
            imageVector = Icons.Default.Cancel,
            contentDescription = null,
            tint = Color(0xFFC62828),
            modifier = Modifier.size(14.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(text = item, style = MaterialTheme.typography.bodySmall.copy(color = SleekTextSecondary))
        }
      }
    }
  }
}

@Composable
fun AiDecompositionGuideCard(result: WasteDisposalGuideResult) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .testTag("ai_decomposition_result_card"),
    shape = RoundedCornerShape(26.dp),
    colors = CardDefaults.cardColors(containerColor = SleekSurface),
    border = BorderStroke(1.5.dp, result.binType.primaryColor.copy(alpha = 0.55f)),
    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(20.dp)
    ) {
      // Top AI Badge + Bin Stream Pill
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Surface(
          shape = CircleShape,
          color = SleekPill
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              imageVector = Icons.Default.AutoAwesome,
              contentDescription = null,
              tint = SleekGreenPrimary,
              modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = if (result.isOrganicDecomposable) {
                "Decomposable • Compostable"
              } else {
                "Non-Decomposable •Segregated Disposal"
              },
              style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                color = SleekGreenPrimary
              )
            )
          }
        }

        Surface(
          shape = CircleShape,
          color = result.binType.backgroundColor
        ) {
          Text(
            text = result.binType.binColorName,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
            style = MaterialTheme.typography.labelSmall.copy(
              fontWeight = FontWeight.Bold,
              color = result.binType.primaryColor
            )
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      Text(
        text = result.itemName,
        style = MaterialTheme.typography.titleLarge.copy(
          fontWeight = FontWeight.Bold,
          color = SleekTextPrimary
        )
      )

      Spacer(modifier = Modifier.height(4.dp))

      Text(
        text = "Recommended Method: ${result.recommendedMethod}",
        style = MaterialTheme.typography.bodySmall.copy(
          fontWeight = FontWeight.SemiBold,
          color = result.binType.primaryColor
        )
      )

      Spacer(modifier = Modifier.height(12.dp))

      // Decomposition / Persistence Period Banner
      Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = result.binType.backgroundColor.copy(alpha = 0.7f)
      ) {
        Row(
          modifier = Modifier.padding(12.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(
            imageVector = Icons.Default.HourglassBottom,
            contentDescription = null,
            tint = result.binType.primaryColor,
            modifier = Modifier.size(18.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Column {
            Text(
              text = if (result.isOrganicDecomposable) {
                "DECOMPOSITION TIMELINE"
              } else {
                "ENVIRONMENTAL PERSISTENCE"
              },
              style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = result.binType.primaryColor
              )
            )
            Text(
              text = result.decompositionTimeframe,
              style = MaterialTheme.typography.bodySmall.copy(
                fontWeight = FontWeight.Bold,
                color = SleekTextPrimary
              )
            )
          }
        }
      }

      // ONLY show Natural Decomposition / Vermicomposting Idea & C:N Ratio when the item IS decomposable!
      if (result.isOrganicDecomposable && result.vermicompostGuide.isNotBlank()) {
        Spacer(modifier = Modifier.height(14.dp))

        Surface(
          modifier = Modifier
            .fillMaxWidth()
            .testTag("vermicompost_guide_box"),
          shape = RoundedCornerShape(16.dp),
          color = SleekPillLight,
          border = BorderStroke(1.dp, SleekGreenPrimary.copy(alpha = 0.35f))
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.Eco,
                contentDescription = null,
                tint = SleekGreenPrimary,
                modifier = Modifier.size(17.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "Natural Decomposition & Composting Idea",
                style = MaterialTheme.typography.titleSmall.copy(
                  fontWeight = FontWeight.Bold,
                  color = SleekGreenPrimary
                )
              )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
              text = result.vermicompostGuide,
              style = MaterialTheme.typography.bodySmall.copy(
                color = SleekTextBody,
                lineHeight = 18.sp
              )
            )
          }
        }

        if (result.carbonNitrogenBalance.isNotBlank()) {
          Spacer(modifier = Modifier.height(12.dp))

          // Carbon-to-Nitrogen Balance (only for decomposable items)
          Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            color = SleekBackground,
            border = BorderStroke(1.dp, SleekCardBorder)
          ) {
            Row(
              modifier = Modifier.padding(12.dp),
              verticalAlignment = Alignment.Top
            ) {
              Icon(
                imageVector = Icons.Default.Science,
                contentDescription = null,
                tint = SleekGreenPrimary,
                modifier = Modifier.size(17.dp)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Column {
                Text(
                  text = "Carbon : Nitrogen (C:N) Composting Ratio",
                  style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = SleekTextPrimary
                  )
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                  text = result.carbonNitrogenBalance,
                  style = MaterialTheme.typography.bodySmall.copy(
                    color = SleekTextSecondary,
                    lineHeight = 17.sp
                  )
                )
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Step-by-Step Decomposition / Disposal Instructions
      Text(
        text = if (result.isOrganicDecomposable) {
          "How to Decompose / Compost Step-by-Step:"
        } else {
          "How to Prepare & Dispose Step-by-Step:"
        },
        style = MaterialTheme.typography.titleSmall.copy(
          fontWeight = FontWeight.Bold,
          color = SleekTextPrimary
        )
      )
      Spacer(modifier = Modifier.height(6.dp))
      result.homeCompostingSteps.forEach { step ->
        Row(
          modifier = Modifier.padding(vertical = 3.dp),
          verticalAlignment = Alignment.Top
        ) {
          Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            tint = SleekGreenPrimary,
            modifier = Modifier.size(15.dp).padding(top = 2.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = step,
            style = MaterialTheme.typography.bodySmall.copy(
              color = SleekTextBody,
              lineHeight = 18.sp
            )
          )
        }
      }

      Spacer(modifier = Modifier.height(12.dp))
      HorizontalDivider(thickness = 0.5.dp, color = SleekDivider)
      Spacer(modifier = Modifier.height(12.dp))

      // Soil Nutrient / Environmental Value
      Row(verticalAlignment = Alignment.Top) {
        Icon(
          imageVector = Icons.Default.Lightbulb,
          contentDescription = null,
          tint = SleekGreenPrimary,
          modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Column {
          Text(
            text = if (result.isOrganicDecomposable) "Soil & Fertilizer Value:" else "Environmental Impact:",
            style = MaterialTheme.typography.labelMedium.copy(
              fontWeight = FontWeight.Bold,
              color = SleekGreenPrimary
            )
          )
          Spacer(modifier = Modifier.height(2.dp))
          Text(
            text = result.nutrientOrEcoValue,
            style = MaterialTheme.typography.bodySmall.copy(
              color = SleekTextBody,
              lineHeight = 17.sp
            )
          )
        }
      }

      if (result.precautionsAndDonts.isNotEmpty()) {
        Spacer(modifier = Modifier.height(12.dp))
        Text(
          text = "Precautions & What to Avoid:",
          style = MaterialTheme.typography.labelMedium.copy(
            fontWeight = FontWeight.Bold,
            color = Color(0xFFC62828)
          )
        )
        Spacer(modifier = Modifier.height(4.dp))
        result.precautionsAndDonts.forEach { dont ->
          Row(
            modifier = Modifier.padding(vertical = 2.dp),
            verticalAlignment = Alignment.Top
          ) {
            Icon(
              imageVector = Icons.Default.Cancel,
              contentDescription = null,
              tint = Color(0xFFC62828),
              modifier = Modifier.size(14.dp).padding(top = 2.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = dont,
              style = MaterialTheme.typography.bodySmall.copy(
                color = SleekTextSecondary,
                lineHeight = 17.sp
              )
            )
          }
        }
      }
    }
  }
}

