package com.example.ui.screens

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FilterVintage
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Park
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.WasteScanEntity
import com.example.data.model.WasteBinType
import com.example.ui.components.BinTypeBadge
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.example.data.model.LeaderboardUser
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
import com.example.ui.theme.SleekPillLight
import com.example.ui.theme.SleekSurface
import com.example.ui.theme.SleekTextBody
import com.example.ui.theme.SleekTextPrimary
import com.example.ui.theme.SleekTextSecondary
import com.example.viewmodel.WasteClassifierViewModel

@Composable
fun LeaderboardRewardsScreen(
  viewModel: WasteClassifierViewModel,
  modifier: Modifier = Modifier
) {
  val leaderboard by viewModel.leaderboard.collectAsStateWithLifecycle()
  val totalPoints by viewModel.totalPoints.collectAsStateWithLifecycle()
  val totalScans by viewModel.totalScansCount.collectAsStateWithLifecycle()
  val allScans by viewModel.allScans.collectAsStateWithLifecycle()

  var activeSegment by remember { mutableStateOf(0) } // 0 = Leaderboard, 1 = History

  val co2Saved = String.format("%.2f", totalPoints * 0.045f)
  val landfillDiverted = String.format("%.1f", totalPoints * 0.08f)
  val treesEquivalent = maxOf(1, totalPoints / 100)

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .padding(bottom = 32.dp),
    contentPadding = PaddingValues(16.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    // Sleek Hero User Profile & Rewards Banner
    item {
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .testTag("leaderboard_user_card"),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = SleekGreenPrimary)
      ) {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .padding(22.dp)
        ) {
          Column {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                  modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.2f)),
                  contentAlignment = Alignment.Center
                ) {
                  Icon(
                    imageVector = Icons.Default.EmojiEvents,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(26.dp)
                  )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column {
                  Text(
                    text = "Your Eco Standing",
                    style = MaterialTheme.typography.titleLarge.copy(
                      fontWeight = FontWeight.Bold,
                      color = Color.White
                    )
                  )
                  Text(
                    text = when {
                      totalPoints >= 1000 -> "Rank: Eco Guardian"
                      totalPoints >= 600 -> "Rank: Zero-Waste Champion"
                      totalPoints >= 300 -> "Rank: Recycle Specialist"
                      else -> "Rank: Eco Scout"
                    },
                    style = MaterialTheme.typography.bodySmall.copy(
                      color = SleekPillLight,
                      fontWeight = FontWeight.Medium
                    )
                  )
                }
              }

              // Current Streak Pill
              Surface(
                shape = CircleShape,
                color = Color.White.copy(alpha = 0.2f)
              ) {
                Row(
                  modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Icon(
                    imageVector = Icons.Default.LocalFireDepartment,
                    contentDescription = null,
                    tint = Color(0xFFFFB74D),
                    modifier = Modifier.size(16.dp)
                  )
                  Spacer(modifier = Modifier.width(4.dp))
                  Text(
                    text = "${maxOf(1, totalPoints / 50)}d Streak",
                    style = MaterialTheme.typography.labelMedium.copy(
                      color = Color.White,
                      fontWeight = FontWeight.Bold
                    )
                  )
                }
              }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Stats row (Points, Scans, Landfill Saved)
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              MetricStatBox(title = "Total Points", value = "$totalPoints pts")
              MetricStatBox(title = "Items Sorted", value = "$totalScans items")
              MetricStatBox(title = "CO₂ Saved", value = "$co2Saved kg")
            }
          }
        }
      }
    }

    // Environmental Impact Summary Card
    item {
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
          Text(
            text = "Your Real-World Environmental Impact",
            style = MaterialTheme.typography.titleMedium.copy(
              fontWeight = FontWeight.Bold,
              color = SleekTextPrimary
            )
          )
          Spacer(modifier = Modifier.height(12.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            ImpactChip(
              icon = Icons.Default.Public,
              label = "$landfillDiverted kg",
              sub = "Landfill Diverted",
              modifier = Modifier.weight(1f)
            )
            ImpactChip(
              icon = Icons.Default.Park,
              label = "$treesEquivalent",
              sub = "Trees Equivalent",
              modifier = Modifier.weight(1f)
            )
          }
        }
      }
    }

    // Segmented Navigation Header: Community Ranks vs My Activity Log
    item {
      Surface(
        shape = RoundedCornerShape(18.dp),
        color = SleekPillLight,
        modifier = Modifier
          .fillMaxWidth()
          .padding(top = 4.dp)
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
        ) {
          Surface(
            shape = RoundedCornerShape(14.dp),
            color = if (activeSegment == 0) SleekSurface else Color.Transparent,
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(14.dp))
              .clickable { activeSegment = 0 }
              .testTag("tab_community_ranks")
          ) {
            Row(
              modifier = Modifier.padding(vertical = 10.dp),
              horizontalArrangement = Arrangement.Center,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(
                imageVector = Icons.Default.EmojiEvents,
                contentDescription = null,
                tint = if (activeSegment == 0) SleekGreenPrimary else SleekTextSecondary,
                modifier = Modifier.size(16.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "Community Ranks",
                style = MaterialTheme.typography.labelMedium.copy(
                  fontWeight = if (activeSegment == 0) FontWeight.Bold else FontWeight.Medium,
                  color = if (activeSegment == 0) SleekGreenPrimary else SleekTextSecondary
                )
              )
            }
          }

          Surface(
            shape = RoundedCornerShape(14.dp),
            color = if (activeSegment == 1) SleekSurface else Color.Transparent,
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(14.dp))
              .clickable { activeSegment = 1 }
              .testTag("tab_activity_log")
          ) {
            Row(
              modifier = Modifier.padding(vertical = 10.dp),
              horizontalArrangement = Arrangement.Center,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(
                imageVector = Icons.Default.History,
                contentDescription = null,
                tint = if (activeSegment == 1) SleekGreenPrimary else SleekTextSecondary,
                modifier = Modifier.size(16.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "My Activity (${allScans.size})",
                style = MaterialTheme.typography.labelMedium.copy(
                  fontWeight = if (activeSegment == 1) FontWeight.Bold else FontWeight.Medium,
                  color = if (activeSegment == 1) SleekGreenPrimary else SleekTextSecondary
                )
              )
            }
          }
        }
      }
    }

    if (activeSegment == 0) {
      // Community Eco Leaderboard Items
      items(leaderboard) { user ->
        LeaderboardRow(user = user)
      }
    } else {
      // User's Local Segregation History Items
      if (allScans.isEmpty()) {
        item {
          Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = SleekSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, SleekCardBorder)
          ) {
            Column(
              modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              Icon(
                imageVector = Icons.Default.History,
                contentDescription = null,
                tint = SleekTextSecondary,
                modifier = Modifier.size(36.dp)
              )
              Spacer(modifier = Modifier.height(10.dp))
              Text(
                text = "No Waste Items Segregated Yet",
                fontWeight = FontWeight.SemiBold,
                color = SleekTextPrimary
              )
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = "Use the AI Scanner to identify waste and earn eco points.",
                style = MaterialTheme.typography.bodySmall.copy(color = SleekTextSecondary)
              )
            }
          }
        }
      } else {
        items(allScans) { scan ->
          val binType = try {
            WasteBinType.valueOf(scan.binType)
          } catch (e: Exception) {
            WasteBinType.RECYCLABLE_BLUE
          }
          val dateStr = SimpleDateFormat("MMM d, yyyy • h:mm a", Locale.getDefault())
            .format(Date(scan.timestamp))

          Card(
            modifier = Modifier
              .fillMaxWidth()
              .testTag("scan_item_${scan.id}"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = SleekSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, SleekCardBorder)
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Box(
                  modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(binType.backgroundColor),
                  contentAlignment = Alignment.Center
                ) {
                  Box(
                    modifier = Modifier
                      .size(14.dp)
                      .clip(CircleShape)
                      .background(binType.primaryColor)
                  )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                  Text(
                    text = scan.objectName,
                    style = MaterialTheme.typography.bodyLarge.copy(
                      fontWeight = FontWeight.Bold,
                      color = SleekTextPrimary
                    )
                  )
                  Text(
                    text = "$dateStr • ${binType.binColorName}",
                    style = MaterialTheme.typography.bodySmall.copy(color = SleekTextSecondary)
                  )
                }
              }

              Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                  shape = CircleShape,
                  color = SleekPill
                ) {
                  Text(
                    text = "+${scan.rewardPoints} Pts",
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelSmall.copy(
                      fontWeight = FontWeight.Bold,
                      color = SleekGreenPrimary
                    )
                  )
                }

                IconButton(
                  onClick = { viewModel.deleteScan(scan.id) },
                  modifier = Modifier.size(32.dp)
                ) {
                  Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = SleekTextSecondary.copy(alpha = 0.6f),
                    modifier = Modifier.size(16.dp)
                  )
                }
              }
            }
          }
        }
      }
    }
  }
}

@Composable
fun MetricStatBox(
  title: String,
  value: String
) {
  Column {
    Text(
      text = title,
      style = MaterialTheme.typography.labelSmall.copy(
        color = Color.White.copy(alpha = 0.75f)
      )
    )
    Text(
      text = value,
      style = MaterialTheme.typography.titleMedium.copy(
        fontWeight = FontWeight.Bold,
        color = Color.White
      )
    )
  }
}

@Composable
fun ImpactChip(
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  label: String,
  sub: String,
  modifier: Modifier = Modifier
) {
  Surface(
    modifier = modifier,
    shape = RoundedCornerShape(16.dp),
    color = SleekPill
  ) {
    Row(
      modifier = Modifier.padding(12.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Box(
        modifier = Modifier
          .size(34.dp)
          .clip(CircleShape)
          .background(Color.White),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = icon,
          contentDescription = null,
          tint = SleekGreenPrimary,
          modifier = Modifier.size(20.dp)
        )
      }
      Spacer(modifier = Modifier.width(10.dp))
      Column {
        Text(
          text = label,
          style = MaterialTheme.typography.titleSmall.copy(
            fontWeight = FontWeight.Bold,
            color = SleekGreenPrimary
          )
        )
        Text(
          text = sub,
          style = MaterialTheme.typography.labelSmall.copy(
            color = SleekTextSecondary
          )
        )
      }
    }
  }
}

@Composable
fun LeaderboardRow(user: LeaderboardUser) {
  val isMe = user.isCurrentUser

  Card(
    modifier = Modifier
      .fillMaxWidth()
      .testTag("leaderboard_user_${user.rank}"),
    shape = RoundedCornerShape(20.dp),
    colors = CardDefaults.cardColors(
      containerColor = if (isMe) SleekPillLight else SleekSurface
    ),
    elevation = CardDefaults.cardElevation(defaultElevation = if (isMe) 2.dp else 1.dp),
    border = androidx.compose.foundation.BorderStroke(1.dp, if (isMe) SleekGreenPrimary.copy(alpha = 0.4f) else SleekCardBorder)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 14.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      // Rank Badge
      Box(
        modifier = Modifier
          .size(32.dp)
          .clip(CircleShape)
          .background(
            when (user.rank) {
              1 -> Color(0xFFFFD700) // Gold
              2 -> Color(0xFFC0C0C0) // Silver
              3 -> Color(0xFFCD7F32) // Bronze
              else -> if (isMe) SleekGreenPrimary else SleekDivider
            }
          ),
        contentAlignment = Alignment.Center
      ) {
        Text(
          text = "#${user.rank}",
          style = MaterialTheme.typography.labelMedium.copy(
            fontWeight = FontWeight.Bold,
            color = if (user.rank in 1..3) Color.Black else if (isMe) Color.White else SleekTextSecondary
          )
        )
      }

      Spacer(modifier = Modifier.width(12.dp))

      // User Initials Avatar
      Box(
        modifier = Modifier
          .size(38.dp)
          .clip(CircleShape)
          .background(if (isMe) SleekGreenPrimary else SleekPill),
        contentAlignment = Alignment.Center
      ) {
        Text(
          text = user.avatarInitials,
          style = MaterialTheme.typography.labelMedium.copy(
            fontWeight = FontWeight.Bold,
            color = if (isMe) Color.White else SleekGreenPrimary
          )
        )
      }

      Spacer(modifier = Modifier.width(12.dp))

      // Name and badge
      Column(modifier = Modifier.weight(1f)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = user.name,
            style = MaterialTheme.typography.bodyMedium.copy(
              fontWeight = if (isMe) FontWeight.Bold else FontWeight.SemiBold,
              color = SleekTextPrimary
            )
          )
          if (isMe) {
            Spacer(modifier = Modifier.width(6.dp))
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = SleekGreenPrimary
            ) {
              Text(
                text = "YOU",
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                style = MaterialTheme.typography.labelSmall.copy(
                  color = Color.White,
                  fontWeight = FontWeight.Bold,
                  fontSize = 9.sp
                )
              )
            }
          }
        }
        Text(
          text = "${user.badge} • ${user.streakDays}d streak",
          style = MaterialTheme.typography.bodySmall.copy(
            color = SleekTextSecondary
          )
        )
      }

      // Points
      Column(horizontalAlignment = Alignment.End) {
        Text(
          text = "${user.points}",
          style = MaterialTheme.typography.titleMedium.copy(
            fontWeight = FontWeight.Bold,
            color = SleekGreenPrimary
          )
        )
        Text(
          text = "points",
          style = MaterialTheme.typography.labelSmall.copy(
            color = SleekTextSecondary
          )
        )
      }
    }
  }
}
