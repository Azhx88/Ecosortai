package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "waste_scans")
data class WasteScanEntity(
  @PrimaryKey(autoGenerate = true)
  val id: Int = 0,
  val objectName: String,
  val category: String,
  val binType: String,
  val confidence: Float,
  val shapeFeature: String,
  val textureFeature: String,
  val colorFeature: String,
  val recyclingGuidance: String,
  val environmentalImpact: String,
  val rewardPoints: Int,
  val timestamp: Long = System.currentTimeMillis(),
  val imageUri: String? = null
)
