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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Recycling
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.WasteScanEntity
import com.example.data.model.WasteBinType
import com.example.ui.components.BinTypeBadge
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.ForestGreen
import com.example.ui.theme.MintLight
import com.example.ui.theme.PaleLeaf
import com.example.ui.theme.SageAccent
import com.example.ui.theme.SleekCardBorder
import com.example.ui.theme.SleekDivider
import com.example.ui.theme.SleekGreenPrimary
import com.example.ui.theme.SleekPill
import com.example.ui.theme.SleekSurface
import com.example.ui.theme.SleekTextBody
import com.example.ui.theme.SleekTextPrimary
import com.example.ui.theme.SleekTextSecondary
import com.example.viewmodel.WasteAppTab
import com.example.viewmodel.WasteClassifierViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HistoryScreen(
  viewModel: WasteClassifierViewModel,
  modifier: Modifier = Modifier
) {
  val scans by viewModel.allScans.collectAsStateWithLifecycle()

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .padding(bottom = 32.dp),
    contentPadding = PaddingValues(16.dp),
    verticalArrangement = Arrangement.spacedBy(12.dp)
  ) {
    // Sleek Header
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = SleekSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, SleekCardBorder)
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = "Segregation Log",
              style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                color = SleekGreenPrimary
              )
            )
            Text(
              text = "${scans.size} items segregated & recorded in Room DB",
              style = MaterialTheme.typography.bodySmall.copy(
                color = SleekTextSecondary
              )
            )
          }

          if (scans.isNotEmpty()) {
            OutlinedButton(
              onClick = { viewModel.clearAllHistory() },
              shape = RoundedCornerShape(14.dp),
              modifier = Modifier.testTag("clear_history_button"),
              border = androidx.compose.foundation.BorderStroke(1.dp, SleekCardBorder)
            ) {
              Icon(
                imageVector = Icons.Default.DeleteOutline,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = SleekTextSecondary
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text("Clear", fontSize = 12.sp, color = SleekTextSecondary)
            }
          }
        }
      }
    }

    if (scans.isEmpty()) {
      item {
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp),
          shape = RoundedCornerShape(28.dp),
          colors = CardDefaults.cardColors(containerColor = SleekSurface),
          border = androidx.compose.foundation.BorderStroke(1.dp, SleekCardBorder)
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Box(
              modifier = Modifier
                .size(64.dp)
                .background(SleekPill, shape = RoundedCornerShape(20.dp)),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.Recycling,
                contentDescription = null,
                tint = SleekGreenPrimary,
                modifier = Modifier.size(36.dp)
              )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
              text = "No Waste Items Segregated Yet",
              style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = SleekTextPrimary
              )
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
              text = "Scan or upload a photo of a waste item in the AI Scanner to receive personalized recycling instructions and earn eco-points!",
              style = MaterialTheme.typography.bodySmall.copy(
                color = SleekTextSecondary
              ),
              textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            Spacer(modifier = Modifier.height(20.dp))
            Button(
              onClick = { viewModel.setActiveTab(WasteAppTab.SCAN_CLASSIFY) },
              shape = RoundedCornerShape(16.dp),
              colors = ButtonDefaults.buttonColors(containerColor = SleekGreenPrimary)
            ) {
              Text("Start Segregating", fontWeight = FontWeight.SemiBold)
            }
          }
        }
      }
    } else {
      items(scans, key = { it.id }) { scan ->
        HistoryItemCard(
          scan = scan,
          onDelete = { viewModel.deleteScan(scan.id) }
        )
      }
    }
  }
}

@Composable
fun HistoryItemCard(
  scan: WasteScanEntity,
  onDelete: () -> Unit
) {
  val binType = try {
    WasteBinType.valueOf(scan.binType)
  } catch (e: Exception) {
    WasteBinType.fromCategory(scan.category, scan.binType)
  }

  val dateStr = SimpleDateFormat("MMM dd, yyyy • HH:mm", Locale.getDefault()).format(Date(scan.timestamp))

  Card(
    modifier = Modifier
      .fillMaxWidth()
      .testTag("history_item_${scan.id}"),
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
            text = scan.objectName,
            style = MaterialTheme.typography.titleMedium.copy(
              fontWeight = FontWeight.Bold,
              color = SleekTextPrimary
            )
          )
          Text(
            text = dateStr,
            style = MaterialTheme.typography.bodySmall.copy(
              color = SleekTextSecondary
            )
          )
        }

        IconButton(onClick = onDelete) {
          Icon(
            imageVector = Icons.Default.DeleteOutline,
            contentDescription = "Delete",
            tint = SleekTextSecondary,
            modifier = Modifier.size(20.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        BinTypeBadge(binType = binType)

        Surface(
          shape = CircleShape,
          color = SleekPill
        ) {
          Text(
            text = "+${scan.rewardPoints} Pts Earned",
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall.copy(
              fontWeight = FontWeight.Bold,
              color = SleekGreenPrimary
            )
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      Text(
        text = scan.recyclingGuidance,
        style = MaterialTheme.typography.bodySmall.copy(
          color = SleekTextBody,
          lineHeight = 18.sp
        )
      )
    }
  }
}
