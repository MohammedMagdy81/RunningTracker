package com.magdy.runningapp.viewModels

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.magdy.runningapp.db.Run
import com.magdy.runningapp.repository.MainRepository
import com.magdy.runningapp.utils.SortType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    val repository: MainRepository
) : ViewModel() {

    private val runsSortedByDate = repository.getAllRunsSortedByDate()
    private val runsSortedByTimeInMillis = repository.getAllRunsSortedByTimeInMillis()
    private val runsSortedByAveSpeed = repository.getAllRunsSortedByAveSpeed()
    private val runsSortedByCaloriesBurned = repository.getAllRunsSortedByCaloriesBurned()
    private val runsSortedByDistance = repository.getAllRunsSortedByDistance()

    val runs=MediatorLiveData<List<Run>>()
    var sortType=SortType.DATE

    init {
        runs.addSource(runsSortedByDate){result->
            if (sortType==SortType.DATE){
                result?.let {
                    runs.value=it
                }
            }
        }

        runs.addSource(runsSortedByTimeInMillis){result->
            if (sortType==SortType.TIME_RUNNING){
                result?.let {
                    runs.value=it
                }
            }
        }

        runs.addSource(runsSortedByAveSpeed){result->
            if (sortType==SortType.AVE_SPEED){
                result?.let {
                    runs.value=it
                }
            }
        }
        runs.addSource(runsSortedByCaloriesBurned){result->
            if (sortType==SortType.CALORIES_BURNED){
                result?.let {
                    runs.value=it
                }
            }
        }
        runs.addSource(runsSortedByDistance){result->
            if (sortType==SortType.DISTANCE){
                result?.let {
                    runs.value=it
                }
            }
        }
    }

    fun insertRun(run: Run) = viewModelScope.launch {
        repository.insertRun(run)
    }






    fun sortType(sortType: SortType)=
        when(sortType){
            SortType.DATE->runsSortedByDate.value.let { runs.value=it }
            SortType.TIME_RUNNING->runsSortedByTimeInMillis.value?.let { runs.value=it }
            SortType.AVE_SPEED->runsSortedByAveSpeed.value?.let { runs.value=it }
            SortType.CALORIES_BURNED->runsSortedByCaloriesBurned.value?.let { runs.value=it }
            SortType.DISTANCE->runsSortedByDistance.value?.let { runs.value=it }
        }.also {
            this.sortType=sortType
        }





}