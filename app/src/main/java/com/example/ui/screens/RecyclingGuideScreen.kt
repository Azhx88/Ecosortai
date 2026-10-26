package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.WasteBinType
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

  val guideItems = remember {
    listOf(
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
        preparationTip = "Remove plastic packaging tape. Flatten boxes flat. Keep strictly dry.",
        decompositionPeriod = "2 - 6 Months",
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
        name = "Food Scraps & Biodegradables",
        binType = WasteBinType.ORGANIC_GREEN,
        preparationTip = "Collect in aerated container or compostable paper. Do NOT use plastic carrier bags.",
        decompositionPeriod = "2 - 4 Weeks",
        doItems = listOf("Fruit & vegetable peelings", "Coffee grounds & paper filters", "Egg shells", "Cut flowers & dry leaves"),
        dontItems = listOf("Plastic stickers on fruit", "Chemically treated wood", "Pet waste", "Synthetic tea bags")
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
        doItems = listOf("Multi-layer foil snack pouches", "Contaminated wipes", "Broken ceramics & ceramics", "Toothbrushes"),
        dontItems = listOf("Recyclable plastics & paper", "Lithium batteries", "Food waste that can be composted")
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
    // Sleek Header
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = SleekSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, SleekCardBorder)
      ) {
        Column(modifier = Modifier.padding(20.dp)) {
          Text(
            text = "Recycling & Segregation Guide",
            style = MaterialTheme.typography.titleLarge.copy(
              fontWeight = FontWeight.Bold,
              color = SleekGreenPrimary
            )
          )
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = "Learn proper segregation rules, decomposition periods, and accepted materials to avoid bin contamination.",
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
            placeholder = { Text("Search waste item (e.g. bottle, battery, peel)...", color = SleekTextSecondary) },
            leadingIcon = {
              Icon(imageVector = Icons.Default.Search, contentDescription = "Search", tint = SleekGreenPrimary)
            },
            modifier = Modifier
              .fillMaxWidth()
              .testTag("guide_search_field"),
            shape = RoundedCornerShape(18.dp),
            colors = OutlinedTextFieldDefaults.colors(
              focusedContainerColor = SleekBackground,
              unfocusedContainerColor = SleekBackground,
              focusedBorderColor = SleekGreenPrimary,
              unfocusedBorderColor = SleekCardBorder
            ),
            singleLine = true
          )
        }
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
