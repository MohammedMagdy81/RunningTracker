package com.magdy.runningapp.ui

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.view.*
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
import com.magdy.runningapp.databinding.FragmentTrackingBinding
import com.magdy.runningapp.db.Run
import com.magdy.runningapp.services.TrackingService
import com.magdy.runningapp.services.polyline
import com.magdy.runningapp.utils.Constant.ACTION_PAUSE_SERVICE
import com.magdy.runningapp.utils.Constant.ACTION_START_OR_RESUME_SERVICE
import com.magdy.runningapp.utils.Constant.ACTION_STOP_SERVICE
import com.magdy.runningapp.utils.Constant.CANCEL_DIALOG_TAG
import com.magdy.runningapp.utils.Constant.KEY_LENGTH
import com.magdy.runningapp.utils.Constant.KEY_WEIGHT
import com.magdy.runningapp.utils.TrackingUtility
import com.magdy.runningapp.viewModels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import javax.inject.Inject
import kotlin.math.round


@AndroidEntryPoint
class TrackingFragment : Fragment() {
    private val viewModel: MainViewModel by viewModels()
    private var isTracking = false
    private var pathPoints = mutableListOf<polyline>()

    private var googleMap: GoogleMap? = null
    var timeInMillis = 0L


    var weight: Float = 0f
    var length: Float = 0f

    lateinit var binding: FragmentTrackingBinding

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    private var menu: Menu? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTrackingBinding.inflate(inflater)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.mapView.onCreate(savedInstanceState)
        binding.btnToggleRun.setOnClickListener {
            toggleRun()
        }

        if (savedInstanceState !=null){
            val cancelDialog=parentFragmentManager.findFragmentByTag(CANCEL_DIALOG_TAG) as CancelDialog?
            cancelDialog?.setYesListener {
                stopRun()
            }

        }

        binding.btnFinishRun.setOnClickListener {
            zoomToSeeWholeTrack()
            endRunAndSaveInDB()
        }
        binding.mapView.getMapAsync {
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
            binding.tvTimer.text = formattedTime
        }
    }

    private fun updateTracking(isTracking: Boolean) {
        this.isTracking = isTracking
        if (!isTracking && timeInMillis > 0L) {
            binding.btnToggleRun.text = "Start"
            binding.btnFinishRun.visibility = View.VISIBLE
        } else if (isTracking) {
            this.menu?.get(0)?.isVisible = true
            binding.btnToggleRun.text = "Stop"
            binding.btnFinishRun.visibility = View.GONE
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
                binding.mapView.width,
                binding.mapView.height,
                (Math.min(binding.mapView.width, binding.mapView.height) / 20)

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
                ((distanceInMeter / 1000f) / (timeInMillis / 1000f / 60 / 60) * 10) / 10f
            // 3 --
            val dateTimeStamp = Calendar.getInstance().timeInMillis

            weight = sharedPreferences.getFloat(KEY_WEIGHT, 0f)
            length = sharedPreferences.getFloat(KEY_LENGTH, 0f)

            // 4 -- CaloriesBurned
            val caloriesBurned = ((distanceInMeter / 1000f) * weight).toInt()

            val run = Run(
                img = bitmap, timeStamp = dateTimeStamp,
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
        CancelDialog().apply {
            setYesListener{
                stopRun()
            }
        }.show(parentFragmentManager,CANCEL_DIALOG_TAG)
    }

    private fun stopRun() {
        binding.tvTimer.text="00:00:00:00"
        sendIntentWithAction(ACTION_STOP_SERVICE)
        findNavController().navigate(R.id.action_trackingFragment_to_runFragment)
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        binding.mapView.onSaveInstanceState(outState)
    }

    override fun onPause() {
        super.onPause()
        binding.mapView.onPause()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding.mapView.onLowMemory()
    }

    override fun onResume() {
        super.onResume()
        binding.mapView.onResume()
    }

    override fun onStop() {
        super.onStop()
        binding.mapView.onStop()
    }

    override fun onStart() {
        super.onStart()
        binding.mapView.onStart()
    }
}