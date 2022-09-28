package com.magdy.runningapp.utils

import android.Manifest

object Constant {

    const val RUNNING_DATABASE_NAME="running_db"
    const val RATIONAL_MESSAGE="You need to Access Location Permission to use app !"
    const val FINE_LOCATION= Manifest.permission.ACCESS_FINE_LOCATION
    const val COARSE_LOCATION= Manifest.permission.ACCESS_COARSE_LOCATION
    const val BACKGROUND_LOCATION= Manifest.permission.ACCESS_BACKGROUND_LOCATION

    const val REQUEST_CODE=200

    const val CANCEL_DIALOG_TAG="cancelDialog"

    const val  ACTION_START_OR_RESUME_SERVICE="ACTION_START_OR_RESUME_SERVICE"
    const val  ACTION_STOP_SERVICE="ACTION_STOP_SERVICE"
    const val  ACTION_PAUSE_SERVICE="ACTION_PAUSE_SERVICE"
    const val ACTION_TRACKING_FRAGMENT_SHOW="ACTION_TRACKING_FRAGMENT_SHOW"

    const val NOTIFICATION_CHANNEL_ID="tracking..user"
    const val NOTIFICATION_CHANNEL_NAME="Tracking"
    const val NOTIFICATION_ID=1

    const val SHARED_PREF_NAME="shredPref"

    const val KEY_FIRST_TIME_TOGGLE="KEY_FIRST_TIME_TOGGLE"
    const val KEY_NAME="KEY_NAME"
    const val KEY_WEIGHT="KEY_WEIGHT"
    const val KEY_LENGTH="KEY_LENGTH"
}