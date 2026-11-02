package com.example.data.api

import android.graphics.BitmapFactory
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONObject
import java.net.URLEncoder
import java.util.Locale
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.ln
import kotlin.math.pow
import kotlin.math.tan

data class OsmPlaceNode(
  val name: String,
  val addressLabel: String,
  val latitude: Double,
  val longitude: Double
)

object OpenStreetMapService {

  private const val USER_AGENT =
    "EcoSortSmartWasteApp/1.0 (Android; +https://www.openstreetmap.org)"

  private val httpClient: OkHttpClient by lazy {
    OkHttpClient.Builder()
      .connectTimeout(8, TimeUnit.SECONDS)
      .readTimeout(8, TimeUnit.SECONDS)
      .followRedirects(true)
      .build()
  }

  private val tileCache = ConcurrentHashMap<String, ImageBitmap>()

  // ---------------------------------------------------------------------------
  // Web Mercator (EPSG:3857) Slippy Map Tile Projection Math
  // ---------------------------------------------------------------------------

  fun lonToTileX(lon: Double, zoom: Int): Double {
    val n = 2.0.pow(zoom.toDouble())
    return ((lon + 180.0) / 360.0) * n
  }

  fun latToTileY(lat: Double, zoom: Int): Double {
    val clampedLat = lat.coerceIn(-85.0511, 85.0511)
    val latRad = Math.toRadians(clampedLat)
    val n = 2.0.pow(zoom.toDouble())
    return (1.0 - ln(tan(latRad) + 1.0 / cos(latRad)) / PI) / 2.0 * n
  }

  /**
   * Projects any (lat, lon) coordinate into exact pixel coordinates on the Compose Canvas
   * given the map center (centerLat, centerLon), zoom level, and tile size in pixels.
   */
  fun projectToCanvas(
    lat: Double,
    lon: Double,
    centerLat: Double,
    centerLon: Double,
    zoom: Int,
    canvasCenterX: Float,
    canvasCenterY: Float,
    tileSizePx: Float = 256f
  ): Offset {
    val centerTileX = lonToTileX(centerLon, zoom)
    val centerTileY = latToTileY(centerLat, zoom)
    val pointTileX = lonToTileX(lon, zoom)
    val pointTileY = latToTileY(lat, zoom)

    val dxPx = ((pointTileX - centerTileX) * tileSizePx).toFloat()
    val dyPx = ((pointTileY - centerTileY) * tileSizePx).toFloat()
    return Offset(canvasCenterX + dxPx, canvasCenterY + dyPx)
  }

  fun getCachedTile(zoom: Int, tileX: Int, tileY: Int): ImageBitmap? {
    val maxIndex = (1 shl zoom) - 1
    if (tileY < 0 || tileY > maxIndex) return null
    val wrappedX = ((tileX % (1 shl zoom)) + (1 shl zoom)) % (1 shl zoom)
    return tileCache["$zoom/$wrappedX/$tileY"]
  }

  /**
   * Fetches a single 256x256 PNG map tile from OpenStreetMap tile servers with automatic
   * fallback across official OSM & Humanitarian/Carto OSM mirrors.
   */
  suspend fun fetchTileBitmap(zoom: Int, tileX: Int, tileY: Int): ImageBitmap? =
    withContext(Dispatchers.IO) {
      val n = 1 shl zoom
      if (tileY < 0 || tileY >= n) return@withContext null
      val wrappedX = ((tileX % n) + n) % n
      val key = "$zoom/$wrappedX/$tileY"

      tileCache[key]?.let { return@withContext it }

      val subdomains = listOf("a", "b", "c")
      val sub = subdomains[(wrappedX + tileY) % subdomains.size]
      val candidateUrls = listOf(
        "https://tile.openstreetmap.org/$zoom/$wrappedX/$tileY.png",
        "https://$sub.tile.openstreetmap.fr/hot/$zoom/$wrappedX/$tileY.png",
        "https://$sub.basemaps.cartocdn.com/rastertiles/voyager/$zoom/$wrappedX/$tileY.png"
      )

      for (url in candidateUrls) {
        try {
          val request = Request.Builder()
            .url(url)
            .header("User-Agent", USER_AGENT)
            .header("Accept", "image/png,image/*;q=0.9,*/*;q=0.5")
            .build()

          httpClient.newCall(request).execute().use { response ->
            if (response.isSuccessful) {
              val bytes = response.body?.bytes()
              if (bytes != null && bytes.isNotEmpty()) {
                val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                if (bitmap != null) {
                  val imgBitmap = bitmap.asImageBitmap()
                  if (tileCache.size > 160) {
                    tileCache.clear()
                  }
                  tileCache[key] = imgBitmap
                  return@withContext imgBitmap
                }
              }
            }
          }
        } catch (_: Exception) {
          // Try next OSM tile mirror
        }
      }
      null
    }

  /**
   * Computes all visible (tileX, tileY) coordinates for a 3x3 or 4x4 grid around (centerLat, centerLon).
   */
  fun getVisibleTileIndices(
    centerLat: Double,
    centerLon: Double,
    zoom: Int,
    radiusTiles: Int = 2
  ): List<Pair<Int, Int>> {
    val centerTileX = floor(lonToTileX(centerLon, zoom)).toInt()
    val centerTileY = floor(latToTileY(centerLat, zoom)).toInt()
    val list = mutableListOf<Pair<Int, Int>>()
    for (dy in -radiusTiles..radiusTiles) {
      for (dx in -radiusTiles..radiusTiles) {
        list.add((centerTileX + dx) to (centerTileY + dy))
      }
    }
    return list
  }

  // ---------------------------------------------------------------------------
  // OpenStreetMap Nominatim Reverse Geocoding & Place Search API
  // ---------------------------------------------------------------------------

  suspend fun reverseGeocodeNominatim(lat: Double, lon: Double): String? =
    withContext(Dispatchers.IO) {
      try {
        val latStr = String.format(Locale.US, "%.6f", lat)
        val lonStr = String.format(Locale.US, "%.6f", lon)
        val url =
          "https://nominatim.openstreetmap.org/reverse?format=jsonv2&lat=$latStr&lon=$lonStr&zoom=18&addressdetails=1&accept-language=en"
        val request = Request.Builder()
          .url(url)
          .header("User-Agent", USER_AGENT)
          .build()

        httpClient.newCall(request).execute().use { response ->
          if (!response.isSuccessful) return@withContext null
          val body = response.body?.string() ?: return@withContext null
          val json = JSONObject(body)
          val addr = json.optJSONObject("address")
          if (addr != null) {
            val roadOrSuburb = listOf(
              addr.optString("road"),
              addr.optString("neighbourhood"),
              addr.optString("suburb"),
              addr.optString("hamlet"),
              addr.optString("village")
            ).firstOrNull { it.isNotBlank() }

            val townOrCity = listOf(
              addr.optString("town"),
              addr.optString("city"),
              addr.optString("municipality"),
              addr.optString("county"),
              addr.optString("state_district")
            ).firstOrNull { it.isNotBlank() }

            val parts = listOfNotNull(roadOrSuburb, townOrCity)
              .map { it.trim() }
              .filter { it.isNotEmpty() }
              .distinct()

            if (parts.isNotEmpty()) {
              return@withContext parts.joinToString(", ")
            }
          }
          val display = json.optString("display_name")
          if (display.isNotBlank()) {
            return@withContext display.split(",").take(2).joinToString(", ") { it.trim() }
          }
        }
      } catch (_: Exception) {}
      null
    }

  suspend fun searchPlaceNominatim(query: String): Triple<Double, Double, String>? =
    withContext(Dispatchers.IO) {
      try {
        val encoded = URLEncoder.encode(query.trim(), "UTF-8")
        val url =
          "https://nominatim.openstreetmap.org/search?format=jsonv2&q=$encoded&limit=1&addressdetails=1&accept-language=en"
        val request = Request.Builder()
          .url(url)
          .header("User-Agent", USER_AGENT)
          .build()

        httpClient.newCall(request).execute().use { response ->
          if (!response.isSuccessful) return@withContext null
          val body = response.body?.string() ?: return@withContext null
          val arr = JSONArray(body)
          if (arr.length() > 0) {
            val first = arr.getJSONObject(0)
            val lat = first.optString("lat").toDoubleOrNull() ?: return@withContext null
            val lon = first.optString("lon").toDoubleOrNull() ?: return@withContext null
            val name = first.optString("name").ifBlank {
              first.optString("display_name").split(",").firstOrNull()?.trim() ?: query
            }
            return@withContext Triple(lat, lon, name)
          }
        }
      } catch (_: Exception) {}
      null
    }
}
