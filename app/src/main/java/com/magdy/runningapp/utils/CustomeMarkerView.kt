package com.magdy.runningapp.utils

import android.content.Context
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import com.magdy.runningapp.db.Run
import kotlinx.android.synthetic.main.marker_view.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

class CustomeMarkerView(
    context: Context,
    val runs: List<Run>,
    layoutId: Int,
) : MarkerView(context, layoutId) {


    override fun getOffset(): MPPointF {
        return MPPointF(-width / 2f, -height / 2f)
    }

    override fun refreshContent(entry: Entry?, highlight: Highlight?) {
        super.refreshContent(entry, highlight)

        if (entry == null) {
            return
        }
        val curRunId = entry.x.toInt()
        val run = runs[curRunId]


        val calendar = Calendar.getInstance().apply {
            timeInMillis = run.timeStamp
        }
        val dateFormat = SimpleDateFormat("dd.MM.yy", Locale.ENGLISH)
        tvDateMarker.text = dateFormat.format(calendar.time)

        val avgSpeed = "${(run.aveSpeedInKMH * 10000.0).roundToInt() / 10000.0} km/h"
        tvAvgSpeedMarker.text = avgSpeed

        val distanceInKm = "${run.distanceInMeter / 1000f}km"
        tvDistanceMarker.text = distanceInKm

        tvDuration.text = TrackingUtility.getFormattedStopWitchTime(run.timeInMilliSecond)

        val caloriesBurned = "${run.caloriesBurned}kcal"
        tvCaloriesBurned.text = caloriesBurned
    }
}