package com.magdy.runningapp.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [Run::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class RunsDatabase :RoomDatabase(){

    abstract fun getRunsDao():RunDao
}