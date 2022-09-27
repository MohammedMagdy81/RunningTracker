package com.magdy.runningapp.repository

import com.magdy.runningapp.db.Run
import com.magdy.runningapp.db.RunDao
import javax.inject.Inject

class MainRepository @Inject constructor(
     val runDao: RunDao
){
    suspend fun insertRun(run: Run)= runDao.insertRun(run)
    suspend fun deleteRun(run: Run)= runDao.deleteRun(run)

    fun getAllRunsSortedByDate()= runDao.getAllRunsSortedByDate()
    fun getAllRunsSortedByDistance()= runDao.getAllRunsSortedByDistanceInMeter()
    fun getAllRunsSortedByCaloriesBurned()= runDao.getAllRunsSortedByCaloriesBurned()
    fun getAllRunsSortedByAveSpeed()= runDao.getAllRunsSortedByAveSpeed()
    fun getAllRunsSortedByTimeInMillis()= runDao.getAllRunsSortedByTimeInMillis()

    fun getTotalTimeInMillis()= runDao.getTotalTimeInMillis()
    fun getTotalCaloriesBurned()= runDao.getTotalCaloriesBurned()
    fun getTotalAveSpeed()= runDao.getTotalAveSpeed()
    fun getTotalDistanceInMeter()= runDao.getTotalDistanceInMeter()
}
