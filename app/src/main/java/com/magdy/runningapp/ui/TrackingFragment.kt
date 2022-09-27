package com.magdy.runningapp.ui

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.magdy.runningapp.R
import com.magdy.runningapp.db.Run
import com.magdy.runningapp.services.TrackingService
import com.magdy.runningapp.services.polyline
import com.magdy.runningapp.utils.Constant.ACTION_PAUSE_SERVICE
import com.magdy.runningapp.utils.Constant.ACTION_START_OR_RESUME_SERVICE
import com.magdy.runningapp.utils.Constant.ACTION_STOP_SERVICE
import com.magdy.runningapp.utils.TrackingUtility
import com.magdy.runningapp.viewModels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_tracking.*
import java.util.*
import kotlin.math.round


@AndroidEntryPoint
class TrackingFragment : Fragment(R.layout.fragment_tracking) {
    private val viewModel: MainViewModel by viewModels()
    private var isTracking = false
    private var pathPoints = mutableListOf<polyline>()

    private var googleMap: GoogleMap? = null
    var timeInMillis = 0L

    private var menu: Menu? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mapView.onCreate(savedInstanceState)
        btnToggleRun.setOnClickListener {
            toggleRun()
        }
        btnFinishRun.setOnClickListener {
            zoomToSeeWholeTrack()
            endRunAndSaveInDB()
        }
        mapView.getMapAsync {
            googleMap = it
            addAllPolyLine()
        }

        observeToLiveData()
    }

    private fun observeToLiveData() {
        TrackingService.isTracking.observe(viewLifecycleOwner) {
            updateTracking(it)
        }
        TrackingService.pathPoints.observe(viewLifecycleOwner) {
            pathPoints = it
            addLatestPolyLine()
            addCameraToUser()
        }
        TrackingService.timeInMilliSecond.observe(viewLifecycleOwner) {
            timeInMillis = it

            val formattedTime = TrackingUtility.getFormattedStopWitchTime(timeInMillis, true)
            tvTimer.text = formattedTime
        }
    }

    private fun updateTracking(isTracking: Boolean) {
        this.isTracking = isTracking
        if (!isTracking) {
            btnToggleRun.text = "Start"
            btnFinishRun.visibility = View.VISIBLE
        } else {
            this.menu?.get(0)?.isVisible = true
            btnToggleRun.text = "Stop"
            btnFinishRun.visibility = View.GONE
        }
    }

    private fun toggleRun() {
        if (isTracking) {
            this.menu?.get(0)?.isVisible = true
            sendIntentWithAction(ACTION_PAUSE_SERVICE)
        } else {
            sendIntentWithAction(ACTION_START_OR_RESUME_SERVICE)
        }
    }

    // this function when we start tracking the map is zoomed
    private fun addCameraToUser() {
        if (pathPoints.isNotEmpty() && pathPoints.last().isNotEmpty()) {
            googleMap?.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    pathPoints.last().last(), 16f
                )
            )
        }
    }

    // this function to add all lines in map to show all tracking lines
    private fun addAllPolyLine() {
        for (polyline in pathPoints) {
            val polyLineOptions = PolylineOptions()
                .color(Color.GREEN)
                .width(8f)
                .addAll(polyline)
            googleMap?.addPolyline(polyLineOptions)
        }
    }

    private fun zoomToSeeWholeTrack() {
        val bounds = LatLngBounds.builder()
        for (polyline in pathPoints) {
            for (pos in polyline) {
                bounds.include(pos)
            }
        }
        // this to take screenshot for image that saved in DB
        googleMap?.moveCamera(
            CameraUpdateFactory.newLatLngBounds(
                bounds.build(),
                mapView.width,
                mapView.height,
                (mapView.width - .05f).toInt()
            )
        )
    }

    // called when we finished tracking
    private fun endRunAndSaveInDB() {
        googleMap?.snapshot { bitmap ->
            // 1-- total distance in meter
            var distanceInMeter = 0
            for (polyline in pathPoints) {
                distanceInMeter += TrackingUtility.calculatePolylineLength(polyline).toInt()
            }
            // 2 -- Average Speed
            val aveSpeed =
                round((distanceInMeter / 1000f) / (timeInMillis / 1000 / 60 / 60) * 10) / 10f
            // 3 --
            val dateTimeStamp = Calendar.getInstance().timeInMillis

            val weight = 80f
            // 4 -- CaloriesBurned
            val caloriesBurned = ((distanceInMeter / 1000f) * weight).toInt()

            val run = Run(
                img=bitmap,timeStamp = dateTimeStamp,
                aveSpeedInKMH = aveSpeed, distanceInMeter = distanceInMeter,
                caloriesBurned = caloriesBurned, timeInMilliSecond = timeInMillis
            )
            viewModel.insertRun(run)
            Snackbar.make(
                requireActivity().findViewById(R.id.rootView),
                "Run Saved Successfully",
                Snackbar.LENGTH_LONG
            ).show()
            stopRun()
        }
    }

    private fun addLatestPolyLine() {
        if (pathPoints.isNotEmpty() && pathPoints.last().size > 1) {
            val preLastLatLng = pathPoints.last()[pathPoints.last().size - 2]
            val lastLatLng = pathPoints.last().last()
            val polyLineOptions = PolylineOptions()
                .color(Color.GREEN)
                .width(8f)
                .add(preLastLatLng)
                .add(lastLatLng)
            googleMap?.addPolyline(polyLineOptions)
        }
    }

    private fun sendIntentWithAction(action: String) =
        Intent(requireContext(), TrackingService::class.java).also {
            it.action = action
            requireContext().startService(it)
        }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.tool_bar_menu, menu)
        this.menu = menu
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.icCancelTracking -> {
                showCancelTrackingDialog()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        if (timeInMillis > 0L) {
            this.menu?.get(0)?.isVisible = true
        }
    }

    private fun showCancelTrackingDialog() {
        val dialog = MaterialAlertDialogBuilder(requireContext(), R.style.AlertDialogTheme).apply {
            setIcon(R.drawable.ic_delete)
            setTitle("Cancel Run !")
            setMessage("Are you Want To Cancel Current Run ?")
            setPositiveButton("Yes") { _, _ ->
                stopRun()

            }
            setNegativeButton("No") { dialog, _ ->
                dialog.cancel()
            }
        }.create()
        dialog.show()
    }

    private fun stopRun() {
        sendIntentWithAction(ACTION_STOP_SERVICE)
        findNavController().navigate(R.id.action_trackingFragment_to_runFragment)
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }
}