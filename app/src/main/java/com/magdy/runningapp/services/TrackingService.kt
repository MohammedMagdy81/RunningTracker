package com.magdy.runningapp.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_LOW
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import com.magdy.runningapp.MainActivity
import com.magdy.runningapp.R
import com.magdy.runningapp.utils.Constant
import com.magdy.runningapp.utils.Constant.ACTION_PAUSE_SERVICE
import com.magdy.runningapp.utils.Constant.ACTION_START_OR_RESUME_SERVICE
import com.magdy.runningapp.utils.Constant.ACTION_STOP_SERVICE
import com.magdy.runningapp.utils.Constant.ACTION_TRACKING_FRAGMENT_SHOW
import com.magdy.runningapp.utils.Constant.NOTIFICATION_CHANNEL_ID
import com.magdy.runningapp.utils.Constant.NOTIFICATION_CHANNEL_NAME
import com.magdy.runningapp.utils.Constant.NOTIFICATION_ID

class TrackingService : LifecycleService() {

    var isFirstRun= true

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            when (it.action) {
                ACTION_START_OR_RESUME_SERVICE -> {
                    if (isFirstRun){
                        startForegroundService()
                        isFirstRun=false
                    }else{
                        Log.d("Service", "resume Service !!!!!")
                    }

                }
                ACTION_PAUSE_SERVICE -> {
                    Log.d("Service ", " Service is Pause")
                }
                ACTION_STOP_SERVICE -> {
                    Log.d("Service ", " Service is Stop")
                }
                else -> Unit
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun startForegroundService() {
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(notificationManager)
        }
        val notificationBuilder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID).apply {
            setAutoCancel(false)
            setOngoing(true)
            setContentTitle("Running App")
            setContentText("00:00:00")
            setSmallIcon(R.drawable.ic_run)
            setContentIntent(getMainActivityPendingIntent())
        }
        startForeground(NOTIFICATION_ID,notificationBuilder.build())
    }

    private fun getMainActivityPendingIntent() = PendingIntent.getActivity(
        this,
        0,
        Intent(this, MainActivity::class.java).also {
            it.action = ACTION_TRACKING_FRAGMENT_SHOW
        }, FLAG_UPDATE_CURRENT
    )


    private fun createNotificationChannel(notificationManager: NotificationManager) {
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            NOTIFICATION_CHANNEL_NAME,
            IMPORTANCE_LOW
        )
        notificationManager.createNotificationChannel(channel)
    }
}