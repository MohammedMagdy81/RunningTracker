package com.magdy.runningapp.utils

import android.Manifest
import android.content.Context
import android.location.Location
import android.os.Build
import com.magdy.runningapp.services.polyline
import pub.devrel.easypermissions.EasyPermissions
import java.util.concurrent.TimeUnit

object TrackingUtility {
    fun hasLocationPermission(context: Context) =
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            EasyPermissions.hasPermissions(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        } else {
            EasyPermissions.hasPermissions(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            )
        }


    fun getFormattedStopWitchTime(ms: Long, includingMillis: Boolean = false): String {
        var millis = ms

        val hours = TimeUnit.MILLISECONDS.toHours(millis)
        millis -= TimeUnit.HOURS.toMillis(hours)

        val minutes = TimeUnit.MILLISECONDS.toMinutes(millis)
        millis -= TimeUnit.MINUTES.toMillis(minutes)

        val seconds = TimeUnit.MILLISECONDS.toSeconds(millis)

        if (!includingMillis) {
            return "${if (hours < 10) "0" else ""} $hours:" +
                    "${if (minutes < 10) "0" else ""} $minutes:" +
                    "${if (seconds < 10) "0" else ""} $seconds:"
        }
        millis -= TimeUnit.SECONDS.toMillis(seconds)
        millis/=10
        return "${if (hours < 10) "0" else ""} $hours:" +
                "${if (minutes < 10) "0" else ""} $minutes:" +
                "${if (seconds < 10) "0" else ""} $seconds:"+
                "${if (millis < 10) "0" else ""} $millis"

    }

    // this function to calculate distance
    fun calculatePolylineLength(polyline: polyline):Float{
        var distance=0f
        for (i in 0..polyline.size-2){
            val pos1= polyline[i]
            val pos2= polyline[i+1]
            val result= FloatArray(1)
            Location.distanceBetween(pos1.latitude,pos1.longitude,pos2.latitude,pos2.longitude,result)
            distance+=result[0]
        }
        return distance
    }
}

















