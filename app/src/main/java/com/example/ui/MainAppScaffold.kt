package com.example.ui

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Recycling
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.screens.HistoryScreen
import com.example.ui.screens.LeaderboardRewardsScreen
import com.example.ui.screens.RecyclingCentersMapScreen
import com.example.ui.screens.RecyclingGuideScreen
import com.example.ui.screens.ScanClassifierScreen
import com.example.ui.screens.SmartDustbinIoTScreen
import com.example.ui.theme.SleekBackground
import com.example.ui.theme.SleekDivider
import com.example.ui.theme.SleekGreenPrimary
import com.example.ui.theme.SleekPill
import com.example.ui.theme.SleekSurface
import com.example.ui.theme.SleekTextSecondary
import com.example.viewmodel.WasteAppTab
import com.example.viewmodel.WasteClassifierViewModel

@Composable
fun MainAppScaffold(
  viewModel: WasteClassifierViewModel
) {
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()

  Scaffold(
    modifier = Modifier.fillMaxSize(),
    contentWindowInsets = WindowInsets.safeDrawing,
    containerColor = SleekBackground,
    bottomBar = {
      androidx.compose.foundation.layout.Column(
        modifier = Modifier
          .background(SleekSurface)
          .windowInsetsPadding(WindowInsets.navigationBars)
      ) {
        androidx.compose.material3.HorizontalDivider(
          thickness = 1.dp,
          color = SleekDivider
        )
        NavigationBar(
          modifier = Modifier.testTag("bottom_nav_bar"),
          containerColor = SleekSurface,
          tonalElevation = 0.dp
        ) {
          NavigationBarItem(
            selected = uiState.activeTab == WasteAppTab.SCAN_CLASSIFY,
            onClick = { viewModel.setActiveTab(WasteAppTab.SCAN_CLASSIFY) },
            icon = {
              Icon(
                imageVector = Icons.Default.AutoAwesome,
                contentDescription = "AI Scanner"
              )
            },
            label = {
              Text(
                text = "Scanner",
                fontSize = 11.sp,
                fontWeight = if (uiState.activeTab == WasteAppTab.SCAN_CLASSIFY) FontWeight.Bold else FontWeight.Medium
              )
            },
            colors = NavigationBarItemDefaults.colors(
              selectedIconColor = SleekGreenPrimary,
              selectedTextColor = SleekGreenPrimary,
              indicatorColor = SleekPill,
              unselectedIconColor = SleekTextSecondary,
              unselectedTextColor = SleekTextSecondary
            ),
            modifier = Modifier.testTag("nav_tab_scanner")
          )

          NavigationBarItem(
            selected = uiState.activeTab == WasteAppTab.RECYCLING_CENTERS,
            onClick = { viewModel.setActiveTab(WasteAppTab.RECYCLING_CENTERS) },
            icon = {
              Icon(
                imageVector = Icons.Default.Map,
                contentDescription = "Eco Map"
              )
            },
            label = {
              Text(
                text = "Eco Map",
                fontSize = 11.sp,
                fontWeight = if (uiState.activeTab == WasteAppTab.RECYCLING_CENTERS) FontWeight.Bold else FontWeight.Medium
              )
            },
            colors = NavigationBarItemDefaults.colors(
              selectedIconColor = SleekGreenPrimary,
              selectedTextColor = SleekGreenPrimary,
              indicatorColor = SleekPill,
              unselectedIconColor = SleekTextSecondary,
              unselectedTextColor = SleekTextSecondary
            ),
            modifier = Modifier.testTag("nav_tab_eco_map")
          )

          /*
          // IoT Bins option commented out for now - will be implemented in future
          NavigationBarItem(
            selected = uiState.activeTab == WasteAppTab.SMART_IOT_BINS,
            onClick = { viewModel.setActiveTab(WasteAppTab.SMART_IOT_BINS) },
            icon = {
              Icon(
                imageVector = Icons.Default.Sensors,
                contentDescription = "IoT Dustbin"
              )
            },
            label = {
              Text(
                text = "IoT Bins",
                fontSize = 11.sp,
                fontWeight = if (uiState.activeTab == WasteAppTab.SMART_IOT_BINS) FontWeight.Bold else FontWeight.Medium
              )
            },
            colors = NavigationBarItemDefaults.colors(
              selectedIconColor = SleekGreenPrimary,
              selectedTextColor = SleekGreenPrimary,
              indicatorColor = SleekPill,
              unselectedIconColor = SleekTextSecondary,
              unselectedTextColor = SleekTextSecondary
            ),
            modifier = Modifier.testTag("nav_tab_iot")
          )
          */

          NavigationBarItem(
            selected = uiState.activeTab == WasteAppTab.SEGREGATION_GUIDE,
            onClick = { viewModel.setActiveTab(WasteAppTab.SEGREGATION_GUIDE) },
            icon = {
              Icon(
                imageVector = Icons.Default.Recycling,
                contentDescription = "Bin Guide"
              )
            },
            label = {
              Text(
                text = "Bin Guide",
                fontSize = 11.sp,
                fontWeight = if (uiState.activeTab == WasteAppTab.SEGREGATION_GUIDE) FontWeight.Bold else FontWeight.Medium
              )
            },
            colors = NavigationBarItemDefaults.colors(
              selectedIconColor = SleekGreenPrimary,
              selectedTextColor = SleekGreenPrimary,
              indicatorColor = SleekPill,
              unselectedIconColor = SleekTextSecondary,
              unselectedTextColor = SleekTextSecondary
            ),
            modifier = Modifier.testTag("nav_tab_guide")
          )

          NavigationBarItem(
            selected = uiState.activeTab == WasteAppTab.LEADERBOARD,
            onClick = { viewModel.setActiveTab(WasteAppTab.LEADERBOARD) },
            icon = {
              Icon(
                imageVector = Icons.Default.EmojiEvents,
                contentDescription = "Leaderboard"
              )
            },
            label = {
              Text(
                text = "Eco Ranks",
                fontSize = 11.sp,
                fontWeight = if (uiState.activeTab == WasteAppTab.LEADERBOARD) FontWeight.Bold else FontWeight.Medium
              )
            },
            colors = NavigationBarItemDefaults.colors(
              selectedIconColor = SleekGreenPrimary,
              selectedTextColor = SleekGreenPrimary,
              indicatorColor = SleekPill,
              unselectedIconColor = SleekTextSecondary,
              unselectedTextColor = SleekTextSecondary
            ),
            modifier = Modifier.testTag("nav_tab_leaderboard")
          )

          NavigationBarItem(
            selected = uiState.activeTab == WasteAppTab.HISTORY,
            onClick = { viewModel.setActiveTab(WasteAppTab.HISTORY) },
            icon = {
              Icon(
                imageVector = Icons.Default.History,
                contentDescription = "History"
              )
            },
            label = {
              Text(
                text = "History",
                fontSize = 11.sp,
                fontWeight = if (uiState.activeTab == WasteAppTab.HISTORY) FontWeight.Bold else FontWeight.Medium
              )
            },
            colors = NavigationBarItemDefaults.colors(
              selectedIconColor = SleekGreenPrimary,
              selectedTextColor = SleekGreenPrimary,
              indicatorColor = SleekPill,
              unselectedIconColor = SleekTextSecondary,
              unselectedTextColor = SleekTextSecondary
            ),
            modifier = Modifier.testTag("nav_tab_history")
          )
        }
      }
    }
  ) { innerPadding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
    ) {
      Crossfade(
        targetState = uiState.activeTab,
        animationSpec = tween(280),
        label = "tab_transition"
      ) { tab ->
        when (tab) {
          WasteAppTab.SCAN_CLASSIFY -> ScanClassifierScreen(viewModel = viewModel)
          WasteAppTab.RECYCLING_CENTERS -> RecyclingCentersMapScreen(viewModel = viewModel)
          /* WasteAppTab.SMART_IOT_BINS -> SmartDustbinIoTScreen(viewModel = viewModel) // Commented out for now */
          WasteAppTab.SMART_IOT_BINS -> ScanClassifierScreen(viewModel = viewModel)
          WasteAppTab.SEGREGATION_GUIDE -> RecyclingGuideScreen()
          WasteAppTab.LEADERBOARD -> LeaderboardRewardsScreen(viewModel = viewModel)
          WasteAppTab.HISTORY -> HistoryScreen(viewModel = viewModel)
        }
      }
    }
  }
}
