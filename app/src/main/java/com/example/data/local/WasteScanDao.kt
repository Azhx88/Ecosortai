package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface WasteScanDao {
  @Query("SELECT * FROM waste_scans ORDER BY timestamp DESC")
  fun getAllScans(): Flow<List<WasteScanEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertScan(scan: WasteScanEntity): Long

  @Query("DELETE FROM waste_scans WHERE id = :id")
  suspend fun deleteScanById(id: Int)

  @Query("DELETE FROM waste_scans")
  suspend fun clearAll()

  @Query("SELECT SUM(rewardPoints) FROM waste_scans")
  fun getTotalPoints(): Flow<Int?>

  @Query("SELECT COUNT(*) FROM waste_scans")
  fun getTotalCount(): Flow<Int>
}
