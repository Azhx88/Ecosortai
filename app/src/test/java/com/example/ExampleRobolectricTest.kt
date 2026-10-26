package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.WasteBinType
import com.example.data.repository.WasteRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("EcoSort AI", appName)
  }

  @Test
  fun `verify waste bin mapping`() {
    val plasticBin = WasteBinType.fromCategory("Plastic Waste", "Blue Bin")
    assertEquals(WasteBinType.RECYCLABLE_BLUE, plasticBin)

    val organicBin = WasteBinType.fromCategory("Food Scraps", "Green Bin")
    assertEquals(WasteBinType.ORGANIC_GREEN, organicBin)

    val batteryBin = WasteBinType.fromCategory("Battery Cell", "Red Bin")
    assertEquals(WasteBinType.HAZARDOUS_RED, batteryBin)
  }

  @Test
  fun `verify recycling centers repository and distance calculations`() {
    val centers = WasteRepository.getAllRecyclingCenters()
    assertTrue(centers.isNotEmpty())

    // Verify distance from user location (37.7749, -122.4194)
    val distance = WasteRepository.calculateDistanceMiles(
      37.7749, -122.4194,
      37.7749, -122.4194
    )
    assertEquals(0.0, distance, 0.01)

    // Verify distance to first center is positive and reasonable (< 50 miles)
    val firstCenterDist = WasteRepository.calculateDistanceMiles(
      37.7749, -122.4194,
      centers.first().latitude, centers.first().longitude
    )
    assertTrue(firstCenterDist > 0.0 && firstCenterDist < 50.0)

    // Verify center contains operating hours and accepted materials
    val center = centers.first()
    assertTrue(center.acceptedMaterials.isNotEmpty())
    assertTrue(center.operatingHours.isNotEmpty())
    assertTrue(center.preparationInstructions.isNotEmpty())
  }

  @Test
  fun `verify suggested center for e-waste and plastic waste`() {
    // E-waste should match an electronic recycling specialist center
    val eWasteMatch = WasteRepository.getSuggestedCenterFor(
      category = "E-Waste",
      userLat = 37.7749,
      userLon = -122.4194
    )
    assertNotNull(eWasteMatch)
    assertTrue(
      eWasteMatch!!.first.acceptedWasteTypes.contains(WasteBinType.E_WASTE_YELLOW) ||
      eWasteMatch.first.primarySpecialty == WasteBinType.E_WASTE_YELLOW
    )

    // Plastic waste should match a facility accepting recyclables
    val plasticMatch = WasteRepository.getSuggestedCenterFor(
      category = "Plastic Waste",
      userLat = 37.7749,
      userLon = -122.4194
    )
    assertNotNull(plasticMatch)
    assertTrue(
      plasticMatch!!.first.acceptedWasteTypes.contains(WasteBinType.RECYCLABLE_BLUE) ||
      plasticMatch.first.primarySpecialty == WasteBinType.RECYCLABLE_BLUE
    )
  }
}
