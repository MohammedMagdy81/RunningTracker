package com.magdy.runningapp.di

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.magdy.runningapp.MainActivity
import com.magdy.runningapp.R
import com.magdy.runningapp.utils.Constant
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ServiceScoped
import javax.inject.Singleton

@Module
@InstallIn(ServiceComponent::class)
object ServiceModule {

    @Provides
    @ServiceScoped
    fun provideFusedLocationProviderClient(@ApplicationContext app: Context) =
      FusedLocationProviderClient(app)

    @Provides
    @ServiceScoped
    fun provideMainActivityPendingIntent(@ApplicationContext app: Context) =
        PendingIntent.getActivity(
             app,
            0,
            Intent(app, MainActivity::class.java).also { it.action = Constant.ACTION_TRACKING_FRAGMENT_SHOW },
            PendingIntent.FLAG_UPDATE_CURRENT
        )
    @Provides
    @ServiceScoped
    fun provideLocationRequest()= LocationRequest.create()

    @Provides
    @ServiceScoped
    fun provideBaseNotificationBuilder(@ApplicationContext app: Context,pendingIntent: PendingIntent)=
        NotificationCompat.Builder(app, Constant.NOTIFICATION_CHANNEL_ID).apply {
            setAutoCancel(false)
            setOngoing(true)
            setContentTitle("Running App")
            setContentText("00:00:00")
            setSmallIcon(R.drawable.ic_run)
            setContentIntent(pendingIntent)
        }

    @Provides
    @ServiceScoped
    fun provideNotificationChanel()= NotificationChannel(
        Constant.NOTIFICATION_CHANNEL_ID,
        Constant.NOTIFICATION_CHANNEL_NAME,
        NotificationManager.IMPORTANCE_LOW
    )




}













