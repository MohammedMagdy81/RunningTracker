package com.magdy.runningapp.viewModels

import androidx.lifecycle.ViewModel
import com.magdy.runningapp.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    val repository: MainRepository
):ViewModel() {

    val totalTimes=repository.getTotalTimeInMillis()
    val totalDistance=repository.getTotalDistanceInMeter()
    val totalCaloriesBurned=repository.getTotalCaloriesBurned()
    val totalAveSpeed=repository.getTotalAveSpeed()
    val runsSortedByDate=repository.getAllRunsSortedByDate()



}