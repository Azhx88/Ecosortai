package com.example.ui.screens

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.PickVisualMediaRequest
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import java.io.File
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Nature
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.ViewInAr
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.PresetSampleItem
import com.example.data.model.WasteBinType
import com.example.ui.components.BinTypeBadge
import com.example.ui.components.CalmingFeedbackBanner
import com.example.ui.components.CalmingPulseWave
import com.example.ui.theme.DeepRiverBlue
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.ForestGreen
import com.example.ui.theme.MintLight
import com.example.ui.theme.MutedSand
import com.example.ui.theme.PaleLeaf
import com.example.ui.theme.SageAccent
import com.example.ui.theme.SereneOceanBlue
import com.example.ui.theme.SleekBackground
import com.example.ui.theme.SleekCameraBackdrop
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
import com.example.ui.theme.SoftRiverBlue
import com.example.ui.theme.WarmEarthTone
import com.example.viewmodel.WasteClassifierViewModel

@Composable
fun ScanClassifierScreen(
  viewModel: WasteClassifierViewModel,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()
  val totalPoints by viewModel.totalPoints.collectAsStateWithLifecycle()

  var tempCameraUri by remember { mutableStateOf<Uri?>(null) }

  // Fallback thumbnail camera launcher
  val cameraThumbnailLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.TakePicturePreview()
  ) { bitmap: Bitmap? ->
    if (bitmap != null) {
      viewModel.onImageCapturedOrUploaded(bitmap, null, "captured waste photo")
    }
  }

  // Gallery Picker (Photo Picker zero-permission contract)
  val photoPickerLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.PickVisualMedia()
  ) { uri: Uri? ->
    if (uri != null) {
      try {
        val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
          val source = ImageDecoder.createSource(context.contentResolver, uri)
          ImageDecoder.decodeBitmap(source) { decoder, _, _ ->
            decoder.isMutableRequired = true
          }
        } else {
          @Suppress("DEPRECATION")
          MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
        }
        viewModel.onImageCapturedOrUploaded(bitmap, uri.toString(), "uploaded image")
      } catch (e: Exception) {
        viewModel.notifyFeedback("Failed to load selected image: ${e.localizedMessage ?: "Unknown error"}")
      }
    }
  }

  // High-res FileProvider Camera Launcher
  val takePictureLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.TakePicture()
  ) { success: Boolean ->
    if (success && tempCameraUri != null) {
      try {
        val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
          val source = ImageDecoder.createSource(context.contentResolver, tempCameraUri!!)
          ImageDecoder.decodeBitmap(source) { decoder, _, _ ->
            decoder.isMutableRequired = true
          }
        } else {
          @Suppress("DEPRECATION")
          MediaStore.Images.Media.getBitmap(context.contentResolver, tempCameraUri!!)
        }
        viewModel.onImageCapturedOrUploaded(bitmap, tempCameraUri.toString(), "captured waste photo")
      } catch (e: Exception) {
        viewModel.notifyFeedback("Failed to process captured photo: ${e.localizedMessage ?: "Unknown error"}")
      }
    }
  }

  fun launchCameraInternal() {
    try {
      val photoFile = File.createTempFile("waste_photo_", ".jpg", context.cacheDir).apply {
        deleteOnExit()
      }
      val authority = "${context.packageName}.fileprovider"
      val uri = FileProvider.getUriForFile(context, authority, photoFile)
      tempCameraUri = uri
      takePictureLauncher.launch(uri)
    } catch (e: ActivityNotFoundException) {
      viewModel.notifyFeedback("No camera app found on this device. Opening gallery picker...")
      try {
        photoPickerLauncher.launch(
          PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
        )
      } catch (_: Exception) {}
    } catch (e: SecurityException) {
      viewModel.notifyFeedback("Camera permission is required. You can choose a photo from gallery.")
    } catch (e: Exception) {
      // Attempt thumbnail launcher fallback
      try {
        cameraThumbnailLauncher.launch(null)
      } catch (e2: Exception) {
        viewModel.notifyFeedback("Camera is unavailable on this device. Please pick an image from gallery or select a test sample.")
      }
    }
  }

  // Camera Permission Launcher
  val cameraPermissionLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.RequestPermission()
  ) { isGranted: Boolean ->
    if (isGranted) {
      launchCameraInternal()
    } else {
      viewModel.notifyFeedback("Camera permission is needed to take photos. You can also pick an image from gallery or select a test sample.")
    }
  }

  val onCameraRequested: () -> Unit = {
    val hasPermission = ContextCompat.checkSelfPermission(
      context,
      Manifest.permission.CAMERA
    ) == PackageManager.PERMISSION_GRANTED

    if (hasPermission) {
      launchCameraInternal()
    } else {
      try {
        cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
      } catch (e: Exception) {
        viewModel.notifyFeedback("Unable to request camera permission. Please choose an image from gallery.")
      }
    }
  }

  val scrollState = rememberScrollState()

  Column(
    modifier = modifier
      .fillMaxSize()
      .verticalScroll(scrollState)
      .padding(bottom = 28.dp),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    // Sleek Header section: "EcoSort AI", "Smart Waste Assistant", and points pill
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 20.dp, vertical = 14.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column {
        Text(
          text = "EcoSort AI",
          style = MaterialTheme.typography.headlineMedium.copy(
            fontWeight = FontWeight.SemiBold,
            color = SleekGreenPrimary,
            letterSpacing = (-0.5).sp
          )
        )
        Text(
          text = "Smart Waste Assistant",
          style = MaterialTheme.typography.bodySmall.copy(
            fontWeight = FontWeight.Medium,
            color = SleekTextSecondary
          )
        )
      }

      // Sleek Points Badge Pill: bg-[#E1EAD3] py-1.5 px-3 rounded-full
      Surface(
        shape = CircleShape,
        color = SleekPillLight
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Box(
            modifier = Modifier
              .size(24.dp)
              .clip(CircleShape)
              .background(SleekGreenPrimary),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.Eco,
              contentDescription = null,
              tint = Color.White,
              modifier = Modifier.size(14.dp)
            )
          }
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "$totalPoints pts",
            style = MaterialTheme.typography.labelMedium.copy(
              fontWeight = FontWeight.Bold,
              color = SleekTextPrimary
            )
          )
        }
      }
    }

    // Feedback notification banner
    CalmingFeedbackBanner(
      message = uiState.feedbackNotification,
      onDismiss = { viewModel.dismissFeedback() }
    )

    // Sleek Camera & Viewport Card (Hero Component)
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .height(260.dp)
        .padding(horizontal = 16.dp)
        .testTag("image_preview_card"),
      shape = RoundedCornerShape(32.dp),
      colors = CardDefaults.cardColors(containerColor = SleekCameraBackdrop),
      border = androidx.compose.foundation.BorderStroke(3.dp, Color.White),
      elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
      Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
      ) {
        if (uiState.selectedBitmap != null) {
          Image(
            bitmap = uiState.selectedBitmap!!.asImageBitmap(),
            contentDescription = "Waste Item Image",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Fit
          )

          // Reset button overlay
          Surface(
            modifier = Modifier
              .align(Alignment.TopEnd)
              .padding(12.dp),
            shape = CircleShape,
            color = Color.Black.copy(alpha = 0.5f)
          ) {
            IconButton(
              onClick = { viewModel.clearCurrentScan() },
              modifier = Modifier.size(36.dp)
            ) {
              Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = "Clear",
                tint = Color.White,
                modifier = Modifier.size(18.dp)
              )
            }
          }
        } else {
          // Camera Reticle Viewfinder
          Box(
            modifier = Modifier
              .fillMaxSize()
              .clickable { onCameraRequested() }
              .padding(24.dp)
              .border(
                1.5.dp,
                Color.White.copy(alpha = 0.35f),
                RoundedCornerShape(20.dp)
              ),
            contentAlignment = Alignment.Center
          ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Icon(
                imageVector = Icons.Default.CameraAlt,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.6f),
                modifier = Modifier.size(42.dp)
              )
              Spacer(modifier = Modifier.height(8.dp))
              Text(
                text = "Point camera at waste item",
                style = MaterialTheme.typography.bodySmall.copy(
                  color = Color.White.copy(alpha = 0.7f),
                  fontWeight = FontWeight.Medium
                )
              )
            }
          }
        }

        // Top Left Status Badge
        Surface(
          modifier = Modifier
            .align(Alignment.TopStart)
            .padding(14.dp),
          shape = CircleShape,
          color = Color.Black.copy(alpha = 0.45f)
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(
              modifier = Modifier
                .size(7.dp)
                .clip(CircleShape)
                .background(SleekGreenPulse)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = if (uiState.isAnalyzing) "AI SCANNING..." else if (uiState.selectedBitmap != null) "OBJECT DETECTED" else "CAMERA READY",
              style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                color = Color.White,
                letterSpacing = 1.sp,
                fontSize = 10.sp
              )
            )
          }
        }

        // Floating Glass-Morphic Result Overlay (when classified)
        if (!uiState.isAnalyzing && uiState.currentClassification != null) {
          val res = uiState.currentClassification!!
          Card(
            modifier = Modifier
              .align(Alignment.BottomCenter)
              .fillMaxWidth()
              .padding(12.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f)),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Box(
                modifier = Modifier
                  .size(42.dp)
                  .clip(RoundedCornerShape(12.dp))
                  .background(SleekPill),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = Icons.Default.Eco,
                  contentDescription = null,
                  tint = SleekGreenPrimary,
                  modifier = Modifier.size(22.dp)
                )
              }

              Spacer(modifier = Modifier.width(12.dp))

              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = "DETECTED",
                  style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = SleekTextSecondary,
                    letterSpacing = 1.sp,
                    fontSize = 9.sp
                  )
                )
                Text(
                  text = res.objectName,
                  style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = SleekTextPrimary
                  )
                )
              }

              Text(
                text = "${(res.confidence * 100).toInt()}% Match",
                style = MaterialTheme.typography.labelMedium.copy(
                  fontWeight = FontWeight.Bold,
                  color = SleekGreenPrimary
                )
              )
            }
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(14.dp))

    // Image Capture & Upload Action Bar
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 2.dp),
      horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      Button(
        onClick = { onCameraRequested() },
        modifier = Modifier
          .weight(1f)
          .height(50.dp)
          .testTag("camera_capture_button"),
        shape = RoundedCornerShape(18.dp),
        colors = ButtonDefaults.buttonColors(
          containerColor = SleekGreenPrimary,
          contentColor = Color.White
        )
      ) {
        Icon(
          imageVector = Icons.Default.CameraAlt,
          contentDescription = "Capture Camera",
          modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text("Capture Photo", fontWeight = FontWeight.SemiBold)
      }

      FilledTonalButton(
        onClick = {
          photoPickerLauncher.launch(
            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
          )
        },
        modifier = Modifier
          .weight(1f)
          .height(50.dp)
          .testTag("upload_image_button"),
        shape = RoundedCornerShape(18.dp),
        colors = ButtonDefaults.filledTonalButtonColors(
          containerColor = SleekPill,
          contentColor = SleekGreenPrimary
        )
      ) {
        Icon(
          imageVector = Icons.Default.AddPhotoAlternate,
          contentDescription = "Upload Photo",
          modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text("Upload Image", fontWeight = FontWeight.SemiBold)
      }
    }

    Spacer(modifier = Modifier.height(14.dp))

    // Quick Test Presets Carousel
    Column(
      modifier = Modifier.fillMaxWidth()
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 20.dp, vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Icon(
          imageVector = Icons.Default.AutoAwesome,
          contentDescription = "Samples",
          tint = SleekGreenPrimary,
          modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = "Try Sample Waste Objects (1-Tap AI Test)",
          style = MaterialTheme.typography.labelLarge.copy(
            fontWeight = FontWeight.SemiBold,
            color = SleekGreenPrimary
          )
        )
      }

      LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        items(viewModel.presetSamples) { preset ->
          PresetItemChip(
            preset = preset,
            onClick = { viewModel.selectPresetSample(preset) }
          )
        }
      }
    }

    // Analyzing State: Calming Visual Feedback
    if (uiState.isAnalyzing) {
      Spacer(modifier = Modifier.height(12.dp))
      CalmingPulseWave(
        text = uiState.analysisStep,
        modifier = Modifier.testTag("analyzing_pulse")
      )
    }

    // Sleek Disposal Guide & Classification Results Card
    AnimatedVisibility(
      visible = !uiState.isAnalyzing && uiState.currentClassification != null,
      enter = fadeIn() + slideInVertically()
    ) {
      val result = uiState.currentClassification
      if (result != null) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Card(
            modifier = Modifier
              .fillMaxWidth()
              .testTag("classification_result_card"),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(
              containerColor = SleekSurface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, SleekCardBorder)
          ) {
            Column(
              modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
            ) {
              // Sleek Disposal Guide Card Header
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = "Disposal Guide",
                  style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = SleekTextPrimary
                  )
                )

                Surface(
                  shape = RoundedCornerShape(8.dp),
                  color = SleekPill
                ) {
                  Text(
                    text = result.category.uppercase(),
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelSmall.copy(
                      fontWeight = FontWeight.Bold,
                      color = SleekGreenPrimary
                    )
                  )
                }
              }

              Spacer(modifier = Modifier.height(12.dp))

              // Bulleted Disposal Steps with sleek green dot pips
              Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
              ) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  verticalAlignment = Alignment.Top
                ) {
                  Box(
                    modifier = Modifier
                      .padding(top = 7.dp)
                      .size(6.dp)
                      .clip(CircleShape)
                      .background(SleekGreenPrimary)
                  )
                  Spacer(modifier = Modifier.width(10.dp))
                  Text(
                    text = "Deposit in ${result.binType.title} bin.",
                    style = MaterialTheme.typography.bodyMedium.copy(
                      color = SleekTextBody,
                      lineHeight = 20.sp
                    )
                  )
                }

                Row(
                  modifier = Modifier.fillMaxWidth(),
                  verticalAlignment = Alignment.Top
                ) {
                  Box(
                    modifier = Modifier
                      .padding(top = 7.dp)
                      .size(6.dp)
                      .clip(CircleShape)
                      .background(SleekGreenPrimary)
                  )
                  Spacer(modifier = Modifier.width(10.dp))
                  Text(
                    text = result.recyclingGuidance,
                    style = MaterialTheme.typography.bodyMedium.copy(
                      color = SleekTextBody,
                      lineHeight = 20.sp
                    )
                  )
                }
              }

              Spacer(modifier = Modifier.height(16.dp))
              HorizontalDivider(color = SleekDivider)
              Spacer(modifier = Modifier.height(14.dp))

              // AI Visual Characteristics Analysis (Shape, Texture, Color)
              Text(
                text = "Visual Characteristics Analyzed",
                style = MaterialTheme.typography.labelLarge.copy(
                  fontWeight = FontWeight.Bold,
                  color = SleekGreenPrimary
                )
              )

              Spacer(modifier = Modifier.height(8.dp))

              VisualFeatureRow(
                icon = Icons.Default.ViewInAr,
                title = "Shape & Geometry",
                detail = result.shapeFeature
              )

              Spacer(modifier = Modifier.height(8.dp))

              VisualFeatureRow(
                icon = Icons.Default.Layers,
                title = "Texture & Material",
                detail = result.textureFeature
              )

              Spacer(modifier = Modifier.height(8.dp))

              VisualFeatureRow(
                icon = Icons.Default.ColorLens,
                title = "Color & Visual Features",
                detail = result.colorFeature
              )

              Spacer(modifier = Modifier.height(14.dp))
              HorizontalDivider(color = SleekDivider)
              Spacer(modifier = Modifier.height(12.dp))

              // Environmental Benefit
              Text(
                text = "Environmental Benefit",
                style = MaterialTheme.typography.labelLarge.copy(
                  fontWeight = FontWeight.Bold,
                  color = SleekGreenPrimary
                )
              )

              Spacer(modifier = Modifier.height(4.dp))

              Text(
                text = result.environmentalImpact,
                style = MaterialTheme.typography.bodySmall.copy(
                  color = SleekTextSecondary,
                  lineHeight = 18.sp
                )
              )

              Spacer(modifier = Modifier.height(18.dp))

              // Claim Reward Points Button (IoT action commented out for now)
              Button(
                onClick = { viewModel.claimRewardAndTriggerIoT() },
                enabled = !uiState.hasClaimedPoints,
                modifier = Modifier
                  .fillMaxWidth()
                  .height(52.dp)
                  .testTag("claim_points_button"),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(
                  containerColor = if (uiState.hasClaimedPoints) SageAccent else SleekGreenPrimary,
                  contentColor = Color.White
                )
              ) {
                if (uiState.hasClaimedPoints) {
                  Icon(imageVector = Icons.Default.Check, contentDescription = null)
                  Spacer(modifier = Modifier.width(8.dp))
                  Text(
                    text = "Points Claimed (+${result.rewardPoints} Pts)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                  )
                } else {
                  Icon(imageVector = Icons.Default.Eco, contentDescription = null)
                  Spacer(modifier = Modifier.width(8.dp))
                  Text(
                    text = "Deposit & Claim +${result.rewardPoints} Eco Points",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                  )
                }
              }

              /*
              // IoT automated lid actuation notification commented out for now - will be implemented in future
              if (uiState.lastIoTActionNotice != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                  text = uiState.lastIoTActionNotice!!,
                  style = MaterialTheme.typography.labelSmall.copy(
                    color = SleekGreenPrimary,
                    fontWeight = FontWeight.SemiBold
                  ),
                  textAlign = TextAlign.Center,
                  modifier = Modifier.fillMaxWidth()
                )
              }
              */

              Spacer(modifier = Modifier.height(16.dp))
              HorizontalDivider(color = SleekDivider)
              Spacer(modifier = Modifier.height(12.dp))

              // Sleek Community Impact Footer
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Row(
                  horizontalArrangement = Arrangement.spacedBy((-6).dp)
                ) {
                  Box(
                    modifier = Modifier
                      .size(26.dp)
                      .clip(CircleShape)
                      .background(Color(0xFF94A3B8))
                      .border(1.5.dp, Color.White, CircleShape)
                  )
                  Box(
                    modifier = Modifier
                      .size(26.dp)
                      .clip(CircleShape)
                      .background(Color(0xFF64748B))
                      .border(1.5.dp, Color.White, CircleShape)
                  )
                  Box(
                    modifier = Modifier
                      .size(26.dp)
                      .clip(CircleShape)
                      .background(SleekPill)
                      .border(1.5.dp, Color.White, CircleShape),
                    contentAlignment = Alignment.Center
                  ) {
                    Text(
                      text = "+12",
                      style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = SleekGreenPrimary
                      )
                    )
                  }
                }

                Text(
                  text = "520 people recycled this today",
                  style = MaterialTheme.typography.labelSmall.copy(
                    color = SleekTextSecondary,
                    fontWeight = FontWeight.Medium
                  )
                )
              }
            }
          }

          // Recommended Local Recycling Center Card (Appropriate facility for disposal)
          if (uiState.suggestedCenterForScan != null) {
            val (center, dist) = uiState.suggestedCenterForScan!!
            Spacer(modifier = Modifier.height(14.dp))
            Card(
              modifier = Modifier
                .fillMaxWidth()
                .testTag("recommended_center_card"),
              shape = RoundedCornerShape(26.dp),
              colors = CardDefaults.cardColors(containerColor = SleekSurface),
              border = androidx.compose.foundation.BorderStroke(1.dp, SleekCardBorder),
              elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
              Column(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(18.dp)
              ) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Surface(
                    shape = CircleShape,
                    color = SoftRiverBlue
                  ) {
                    Row(
                      modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                      verticalAlignment = Alignment.CenterVertically
                    ) {
                      Icon(
                        imageVector = Icons.Default.Place,
                        contentDescription = null,
                        tint = SereneOceanBlue,
                        modifier = Modifier.size(13.dp)
                      )
                      Spacer(modifier = Modifier.width(4.dp))
                      Text(
                        text = "Recommended Disposal Facility",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = SereneOceanBlue
                      )
                    }
                  }

                  Surface(
                    shape = CircleShape,
                    color = SleekPill
                  ) {
                    Text(
                      text = "$dist mi away",
                      modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                      fontSize = 11.sp,
                      fontWeight = FontWeight.SemiBold,
                      color = SleekGreenPrimary
                    )
                  }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                  text = center.name,
                  style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = SleekTextPrimary
                  )
                )

                Text(
                  text = center.address,
                  style = MaterialTheme.typography.bodySmall.copy(color = SleekTextSecondary)
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Preparation instruction for this facility
                Surface(
                  shape = RoundedCornerShape(14.dp),
                  color = MutedSand,
                  border = androidx.compose.foundation.BorderStroke(1.dp, SleekCardBorder),
                  modifier = Modifier.fillMaxWidth()
                ) {
                  Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.Top
                  ) {
                    Icon(
                      imageVector = Icons.Default.Info,
                      contentDescription = null,
                      tint = WarmEarthTone,
                      modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                      Text(
                        text = "Facility Preparation Rule:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = WarmEarthTone
                      )
                      Text(
                        text = center.preparationInstructions.firstOrNull() ?: "Rinse container clean before disposal.",
                        fontSize = 12.sp,
                        color = SleekTextBody,
                        lineHeight = 16.sp
                      )
                    }
                  }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                  onClick = { viewModel.navigateToCenterOnMap(center.id) },
                  shape = RoundedCornerShape(16.dp),
                  colors = ButtonDefaults.buttonColors(containerColor = SereneOceanBlue),
                  modifier = Modifier
                    .fillMaxWidth()
                    .testTag("btn_view_on_map")
                ) {
                  Icon(
                    imageVector = Icons.Default.Map,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                  )
                  Spacer(modifier = Modifier.width(8.dp))
                  Text(
                    text = "View on Recycling Map & Directions",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp
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
fun VisualFeatureRow(
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  title: String,
  detail: String
) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    verticalAlignment = Alignment.Top
  ) {
    Box(
      modifier = Modifier
        .size(28.dp)
        .clip(CircleShape)
        .background(SleekPill),
      contentAlignment = Alignment.Center
    ) {
      Icon(
        imageVector = icon,
        contentDescription = null,
        tint = SleekGreenPrimary,
        modifier = Modifier.size(16.dp)
      )
    }

    Spacer(modifier = Modifier.width(10.dp))

    Column(modifier = Modifier.weight(1f)) {
      Text(
        text = title,
        style = MaterialTheme.typography.labelMedium.copy(
          fontWeight = FontWeight.Bold,
          color = SleekGreenPrimary
        )
      )
      Text(
        text = detail,
        style = MaterialTheme.typography.bodySmall.copy(
          color = SleekTextBody
        )
      )
    }
  }
}

@Composable
fun PresetItemChip(
  preset: PresetSampleItem,
  onClick: () -> Unit
) {
  Surface(
    onClick = onClick,
    shape = RoundedCornerShape(16.dp),
    color = preset.expectedBin.backgroundColor,
    border = androidx.compose.foundation.BorderStroke(1.dp, preset.expectedBin.primaryColor.copy(alpha = 0.35f)),
    modifier = Modifier.testTag("preset_${preset.id}")
  ) {
    Row(
      modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = preset.iconEmoji,
        fontSize = 18.sp
      )
      Spacer(modifier = Modifier.width(8.dp))
      Column {
        Text(
          text = preset.name,
          style = MaterialTheme.typography.labelMedium.copy(
            fontWeight = FontWeight.Bold,
            color = SleekTextPrimary
          )
        )
        Text(
          text = "+${preset.points} pts",
          style = MaterialTheme.typography.labelSmall.copy(
            color = preset.expectedBin.primaryColor,
            fontWeight = FontWeight.SemiBold
          )
        )
      }
    }
  }
}
