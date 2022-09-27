package com.magdy.runningapp.db

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface RunDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRun(run: Run)

    @Delete
    suspend fun deleteRun(run: Run)

    @Query("SELECT* FROM running_table ORDER BY timeStamp DESC")
    fun getAllRunsSortedByDate():LiveData<List<Run>>

    @Query("SELECT* FROM running_table ORDER BY timeInMilliSecond DESC")
    fun getAllRunsSortedByTimeInMillis():LiveData<List<Run>>

    @Query("SELECT* FROM running_table ORDER BY aveSpeedInKMH DESC")
    fun getAllRunsSortedByAveSpeed():LiveData<List<Run>>

    @Query("SELECT* FROM running_table ORDER BY caloriesBurned DESC")
    fun getAllRunsSortedByCaloriesBurned():LiveData<List<Run>>

    @Query("SELECT* FROM running_table ORDER BY distanceInMeter DESC")
    fun getAllRunsSortedByDistanceInMeter():LiveData<List<Run>>

    @Query("SELECT SUM(timeInMilliSecond) FROM running_table")
    fun getTotalTimeInMillis():LiveData<Long>

    @Query("SELECT SUM(caloriesBurned) FROM running_table")
    fun getTotalCaloriesBurned():LiveData<Int>

    @Query("SELECT SUM(distanceInMeter) FROM running_table")
    fun getTotalDistanceInMeter():LiveData<Int>

    @Query("SELECT AVG(aveSpeedInKMH) FROM running_table")
    fun getTotalAveSpeed():LiveData<Float>

}








