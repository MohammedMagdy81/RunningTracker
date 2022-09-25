package com.magdy.runningapp.db

import android.graphics.Bitmap
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "running_table")
data class Run(
    var img:Bitmap?=null,
    var timeStamp:Long=0L,
    var aveSpeedInKMH:Float=0f,
    var distanceInMeter:Int=0,
    var caloriesBurned:Int=0,
    var timeInMilliSecond:Long=0L,
){
    @PrimaryKey(autoGenerate = true)
    var id:Int?=null
}
