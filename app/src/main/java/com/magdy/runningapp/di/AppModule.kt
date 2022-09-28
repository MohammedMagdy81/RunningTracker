package com.magdy.runningapp.di

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import androidx.room.Room
import com.magdy.runningapp.db.RunsDatabase
import com.magdy.runningapp.utils.Constant
import com.magdy.runningapp.utils.Constant.KEY_FIRST_TIME_TOGGLE
import com.magdy.runningapp.utils.Constant.KEY_LENGTH
import com.magdy.runningapp.utils.Constant.KEY_NAME
import com.magdy.runningapp.utils.Constant.KEY_WEIGHT
import com.magdy.runningapp.utils.Constant.RUNNING_DATABASE_NAME
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideRunningDatabase(
        @ApplicationContext app: Context
    ) = Room.databaseBuilder(
        app,
        RunsDatabase::class.java,
        RUNNING_DATABASE_NAME
    ).build()

    @Singleton
    @Provides
    fun provideRunDao(db:RunsDatabase)=db.getRunsDao()

    @Singleton
    @Provides
    fun provideSharedPref(@ApplicationContext app:Context)=
        app.getSharedPreferences(Constant.SHARED_PREF_NAME,MODE_PRIVATE)

    @Singleton
    @Provides
    fun provideName(sharedPreferences: SharedPreferences)=sharedPreferences.getString(KEY_NAME,"")?:""

    @Singleton
    @Provides
    fun provideFirstShowToggle(sharedPreferences: SharedPreferences)=sharedPreferences.getBoolean(
        KEY_FIRST_TIME_TOGGLE,true)












}






















