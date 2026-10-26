package com.example.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Router
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.SettingsInputAntenna
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.SmartBinNode
import com.example.ui.components.CalmingFeedbackBanner
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.ForestGreen
import com.example.ui.theme.MintLight
import com.example.ui.theme.PaleLeaf
import com.example.ui.theme.SageAccent
import com.example.ui.theme.SleekCardBorder
import com.example.ui.theme.SleekDivider
import com.example.ui.theme.SleekGreenPrimary
import com.example.ui.theme.SleekGreenPulse
import com.example.ui.theme.SleekPill
import com.example.ui.theme.SleekSurface
import com.example.ui.theme.SleekTextBody
import com.example.ui.theme.SleekTextPrimary
import com.example.ui.theme.SleekTextSecondary
import com.example.viewmodel.WasteClassifierViewModel

@Composable
fun SmartDustbinIoTScreen(
  viewModel: WasteClassifierViewModel,
  modifier: Modifier = Modifier
) {
  val smartBins by viewModel.smartBins.collectAsStateWithLifecycle()
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()
  val scrollState = rememberScrollState()

  Column(
    modifier = modifier
      .fillMaxSize()
      .verticalScroll(scrollState)
      .padding(bottom = 32.dp),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    // Sleek IoT Hub Header
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp),
      shape = RoundedCornerShape(28.dp),
      colors = CardDefaults.cardColors(
        containerColor = SleekSurface
      ),
      border = androidx.compose.foundation.BorderStroke(1.dp, SleekCardBorder)
    ) {
      Column(modifier = Modifier.padding(20.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text(
              text = "Future IoT Integration",
              style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                color = SleekGreenPrimary
              )
            )
            Text(
              text = "Connected Smart Dustbins & Automated Collection",
              style = MaterialTheme.typography.bodySmall.copy(
                color = SleekTextSecondary,
                fontWeight = FontWeight.Medium
              )
            )
          }

          Surface(
            shape = CircleShape,
            color = SleekPill
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Box(
                modifier = Modifier
                  .size(8.dp)
                  .clip(CircleShape)
                  .background(SleekGreenPulse)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "Mesh Active",
                style = MaterialTheme.typography.labelSmall.copy(
                  fontWeight = FontWeight.Bold,
                  color = SleekGreenPrimary
                )
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
          text = "Demonstrates automated waste collection integration. Smart dustbins feature ultrasonic fill sensors, automatic servo lid actuation upon AI identification, and automated collection route dispatch.",
          style = MaterialTheme.typography.bodyMedium.copy(
            color = SleekTextBody,
            lineHeight = 20.sp
          )
        )
      }
    }

    // Feedback notification
    CalmingFeedbackBanner(
      message = uiState.feedbackNotification,
      onDismiss = { viewModel.dismissFeedback() }
    )

    // Collection Truck Dispatch Action
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 4.dp),
      shape = RoundedCornerShape(24.dp),
      colors = CardDefaults.cardColors(
        containerColor = SleekSurface
      ),
      elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
      border = androidx.compose.foundation.BorderStroke(1.dp, SleekCardBorder)
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = "Automated Municipal Fleet",
            style = MaterialTheme.typography.titleMedium.copy(
              fontWeight = FontWeight.Bold,
              color = SleekTextPrimary
            )
          )
          Text(
            text = "Dispatches electric collection truck when bins exceed 80% capacity.",
            style = MaterialTheme.typography.bodySmall.copy(
              color = SleekTextSecondary
            )
          )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Button(
          onClick = {
            smartBins.forEach { bin ->
              if (bin.fillPercentage > 50) {
                viewModel.emptySmartBin(bin.id)
              }
            }
          },
          shape = RoundedCornerShape(16.dp),
          colors = ButtonDefaults.buttonColors(
            containerColor = SleekGreenPrimary
          ),
          modifier = Modifier.testTag("simulate_collection_truck")
        ) {
          Icon(imageVector = Icons.Default.LocalShipping, contentDescription = null, modifier = Modifier.size(18.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("Empty Bins", fontWeight = FontWeight.Bold)
        }
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    // Connected Dustbin Nodes List
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
      smartBins.forEach { bin ->
        SmartBinCard(
          bin = bin,
          onToggleLid = { viewModel.toggleBinLidManually(bin.id) },
          onEmpty = { viewModel.emptySmartBin(bin.id) }
        )
      }
    }

    Spacer(modifier = Modifier.height(20.dp))

    // IoT Architecture Technical Details
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp),
      shape = RoundedCornerShape(24.dp),
      colors = CardDefaults.cardColors(
        containerColor = SleekSurface
      ),
      border = androidx.compose.foundation.BorderStroke(1.dp, SleekCardBorder)
    ) {
      Column(modifier = Modifier.padding(18.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.Hub,
            contentDescription = null,
            tint = SleekGreenPrimary,
            modifier = Modifier.size(22.dp)
          )
          Spacer(modifier = Modifier.width(10.dp))
          Text(
            text = "How Smart Dustbin IoT Works",
            style = MaterialTheme.typography.titleMedium.copy(
              fontWeight = FontWeight.Bold,
              color = SleekTextPrimary
            )
          )
        }

        Spacer(modifier = Modifier.height(10.dp))

        IoTFeatureBullet(
          step = "1",
          title = "Visual Edge AI Detection",
          desc = "Optical sensors inspect object characteristics and classify waste into recyclable, organic, e-waste, or hazardous streams."
        )

        Spacer(modifier = Modifier.height(8.dp))

        IoTFeatureBullet(
          step = "2",
          title = "Automatic Servo Lid Actuation",
          desc = "Microcontroller signals 90° servo motors to open the designated segregation compartment lid, preventing human touch."
        )

        Spacer(modifier = Modifier.height(8.dp))

        IoTFeatureBullet(
          step = "3",
          title = "Ultrasonic Fill-Level Telemetry",
          desc = "Dual ultrasonic sensors measure waste height and notify local sanitation teams via MQTT / LoRaWAN before overflow."
        )
      }
    }
  }
}

@Composable
fun SmartBinCard(
  bin: SmartBinNode,
  onToggleLid: () -> Unit,
  onEmpty: () -> Unit
) {
  val animatedFill by animateFloatAsState(
    targetValue = bin.fillPercentage / 100f,
    animationSpec = tween(600),
    label = "fill"
  )

  Card(
    modifier = Modifier
      .fillMaxWidth()
      .testTag("smart_bin_${bin.id}"),
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
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(16.dp)
              .clip(CircleShape)
              .background(bin.binType.primaryColor)
          )
          Spacer(modifier = Modifier.width(10.dp))
          Column {
            Text(
              text = bin.name,
              style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = SleekTextPrimary
              )
            )
            Text(
              text = "ID: ${bin.id} • ${bin.status}",
              style = MaterialTheme.typography.bodySmall.copy(
                color = SleekTextSecondary
              )
            )
          }
        }

        // Lid status badge
        Surface(
          shape = CircleShape,
          color = if (bin.isLidOpen) SleekPill else SleekDivider
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              imageVector = if (bin.isLidOpen) Icons.Default.LockOpen else Icons.Default.Lock,
              contentDescription = null,
              tint = if (bin.isLidOpen) SleekGreenPrimary else SleekTextSecondary,
              modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = if (bin.isLidOpen) "LID OPEN" else "CLOSED",
              style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                color = if (bin.isLidOpen) SleekGreenPrimary else SleekTextSecondary,
                fontSize = 10.sp
              )
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Fill Level Progress
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Text(
          text = "Ultrasonic Fill Level",
          style = MaterialTheme.typography.labelSmall.copy(
            fontWeight = FontWeight.Medium,
            color = SleekTextSecondary
          )
        )
        Text(
          text = "${bin.fillPercentage}% full",
          style = MaterialTheme.typography.labelMedium.copy(
            fontWeight = FontWeight.Bold,
            color = if (bin.fillPercentage >= 80) Color(0xFFC62828) else SleekGreenPrimary
          )
        )
      }

      Spacer(modifier = Modifier.height(6.dp))

      LinearProgressIndicator(
        progress = { animatedFill },
        modifier = Modifier
          .fillMaxWidth()
          .height(8.dp)
          .clip(CircleShape),
        color = if (bin.fillPercentage >= 80) Color(0xFFC62828) else bin.binType.primaryColor,
        trackColor = SleekDivider
      )

      Spacer(modifier = Modifier.height(14.dp))

      // Controls
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        OutlinedButton(
          onClick = onToggleLid,
          modifier = Modifier.weight(1f),
          shape = RoundedCornerShape(14.dp),
          border = androidx.compose.foundation.BorderStroke(1.dp, SleekCardBorder)
        ) {
          Icon(
            imageVector = if (bin.isLidOpen) Icons.Default.Lock else Icons.Default.LockOpen,
            contentDescription = null,
            tint = SleekGreenPrimary,
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = if (bin.isLidOpen) "Close Lid" else "Test Open Lid",
            style = MaterialTheme.typography.labelMedium.copy(
              color = SleekTextPrimary,
              fontWeight = FontWeight.SemiBold
            )
          )
        }

        FilledTonalButton(
          onClick = onEmpty,
          modifier = Modifier.weight(1f),
          shape = RoundedCornerShape(14.dp),
          colors = ButtonDefaults.filledTonalButtonColors(
            containerColor = SleekPill,
            contentColor = SleekGreenPrimary
          )
        ) {
          Icon(
            imageVector = Icons.Default.DeleteSweep,
            contentDescription = null,
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "Empty Bin",
            style = MaterialTheme.typography.labelMedium.copy(
              fontWeight = FontWeight.SemiBold
            )
          )
        }
      }
    }
  }
}

@Composable
fun IoTFeatureBullet(
  step: String,
  title: String,
  desc: String
) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    verticalAlignment = Alignment.Top
  ) {
    Box(
      modifier = Modifier
        .size(24.dp)
        .clip(CircleShape)
        .background(SleekPill),
      contentAlignment = Alignment.Center
    ) {
      Text(
        text = step,
        style = MaterialTheme.typography.labelSmall.copy(
          fontWeight = FontWeight.Bold,
          color = SleekGreenPrimary
        )
      )
    }

    Spacer(modifier = Modifier.width(10.dp))

    Column(modifier = Modifier.weight(1f)) {
      Text(
        text = title,
        style = MaterialTheme.typography.bodyMedium.copy(
          fontWeight = FontWeight.Bold,
          color = SleekTextPrimary
        )
      )
      Text(
        text = desc,
        style = MaterialTheme.typography.bodySmall.copy(
          color = SleekTextSecondary,
          lineHeight = 18.sp
        )
      )
    }
  }
}
