package com.magdy.runningapp.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_LOW
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Build
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.Task
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
import com.magdy.runningapp.utils.TrackingUtility
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

typealias polyline = MutableList<LatLng>
typealias polyLines = MutableList<polyline>

@AndroidEntryPoint
class TrackingService : LifecycleService() {

    var isFirstRun = true
    var serviceKilled = false

    @Inject
    lateinit var fusedLocationClient: FusedLocationProviderClient

    @Inject
    lateinit var locationRequest: LocationRequest

    @Inject
    lateinit var baseNotificationBuilder: NotificationCompat.Builder

    //  الأوبجكت دا  عشان اعمل تحديث للنوتفيكشن بالوقت الجديد لما التايمر يتغير
    private lateinit var curNotificationBuilder: NotificationCompat.Builder

    @Inject
    lateinit var notificationChannel: NotificationChannel

    private val timeInSecond = MutableLiveData<Long>()

    companion object {
        val timeInMilliSecond = MutableLiveData<Long>()
        val isTracking = MutableLiveData<Boolean>()
        val pathPoints = MutableLiveData<polyLines>()

    }

    override fun onCreate() {
        super.onCreate()
        postInitialValue()
        curNotificationBuilder = baseNotificationBuilder

        isTracking.observe(this) {
            updateUserTracking(it)
            updateNotificationTrackingState(it)

        }
    }

    private fun postInitialValue() {
        isTracking.value = false
        pathPoints.value = mutableListOf()
        timeInSecond.postValue(0L)
        timeInMilliSecond.postValue(0L)
    }

    // عشان اعمل update النوتفيكشن لما التايمر يشتغل واتخذ ااكشن معين سواء pause or resume
    private fun updateNotificationTrackingState(isTracking: Boolean) {
        val notificationActionText = if (isTracking) "Pause" else "Resume"

        val pendingIntent = if (isTracking) {
            val pauseIntent = Intent(this, TrackingService::class.java).apply {
                action = ACTION_PAUSE_SERVICE
            }

            PendingIntent.getService(this, 1, pauseIntent, FLAG_UPDATE_CURRENT)
        } else {
            val resumeIntent = Intent(this, TrackingService::class.java).apply {
                action = ACTION_START_OR_RESUME_SERVICE
            }
            PendingIntent.getService(this, 2, resumeIntent, FLAG_UPDATE_CURRENT)
        }

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // عشان احذف الاكشن القديم واضيف واحد جديد
        curNotificationBuilder.javaClass.getDeclaredField("mActions").apply {
            isAccessible = true
            set(curNotificationBuilder, ArrayList<NotificationCompat.Action>())
        }

        if (!serviceKilled) {
            curNotificationBuilder = baseNotificationBuilder
                .addAction(R.drawable.ic_baseline_pause, notificationActionText, pendingIntent)
            notificationManager.notify(NOTIFICATION_ID, curNotificationBuilder.build())
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            when (it.action) {
                ACTION_START_OR_RESUME_SERVICE -> {
                    if (isFirstRun) {
                        startForegroundService()
                        isFirstRun = false
                    } else {
                        Log.d("Service", "resume Service !!!!!")
                        startTimer()
                    }

                }
                ACTION_PAUSE_SERVICE -> {
                    pauseService()
                }
                ACTION_STOP_SERVICE -> {
                    killService()
                }
                else -> Unit
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private var isTimerEnable = false
    private var lapTime = 0L
    private var totalRunTime = 0L
    private var timeStarted = 0L
    private var lastSecondTimeStamp = 0L

    private fun startTimer() {
        addEmptyPolyLine()
        isTracking.postValue(true)
        isTimerEnable = true
        timeStarted = System.currentTimeMillis()

        CoroutineScope(Dispatchers.Main).launch {
            while (isTracking.value!!) {
                lapTime = System.currentTimeMillis() - timeStarted

                timeInMilliSecond.postValue(totalRunTime + lapTime)

                if (timeInMilliSecond.value!! >= lastSecondTimeStamp + 1000L) {
                    timeInSecond.postValue(timeInSecond.value!! + 1)
                    lastSecondTimeStamp += 1000L
                }
                delay(50)
            }
            totalRunTime += lapTime

        }
    }

    private fun pauseService() {
        isTracking.postValue(false)
        isTimerEnable = false
    }

    private fun killService() {
        serviceKilled = true
        isFirstRun = true
        pauseService()
        postInitialValue()
        stopForeground(true)
        stopSelf()
    }


    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            if (isTracking.value!!) {
                for (location in result.locations) {
                    addPathPoints(location)
                }
            }

        }
    }

    private fun updateUserTracking(isTracking: Boolean) {
        if (isTracking) {
            if (TrackingUtility.hasLocationPermission(this)) {
                locationRequest.apply {
                    interval = 5000L
                    fastestInterval = 3000
                    priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                }

                fusedLocationClient.requestLocationUpdates(
                    locationRequest,
                    locationCallback,
                    Looper.getMainLooper()
                )


            }
        } else {
            fusedLocationClient.removeLocationUpdates(locationCallback)
        }


    }


    private fun startForegroundService() {
        startTimer()
        isTracking.postValue(true)

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(notificationManager)
        }
        startForeground(NOTIFICATION_ID, baseNotificationBuilder.build())

        timeInSecond.observe(this) {
            if (!serviceKilled) {
                val contentText = TrackingUtility.getFormattedStopWitchTime(it * 1000)
                val notification = curNotificationBuilder.setContentText(contentText)
                notificationManager.notify(NOTIFICATION_ID, notification.build())
            }

        }
    }

    private fun createNotificationChannel(notificationManager: NotificationManager) {
        notificationManager.createNotificationChannel(notificationChannel)
    }

    private fun addEmptyPolyLine() = pathPoints.value?.apply {
        add(mutableListOf())
        pathPoints.postValue(this)
    } ?: pathPoints.postValue(mutableListOf(mutableListOf()))

    private fun addPathPoints(location: Location?) {
        location?.let {
            val pos = LatLng(location.latitude, location.longitude)
            pathPoints?.value?.apply {
                last().add(pos)
                pathPoints.postValue(this)
            }
        }
    }


}