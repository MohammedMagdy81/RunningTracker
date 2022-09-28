package com.magdy.runningapp.ui

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.magdy.runningapp.R
import com.magdy.runningapp.databinding.FragmentStatisticsBinding
import com.magdy.runningapp.utils.CustomeMarkerView
import com.magdy.runningapp.utils.TrackingUtility
import com.magdy.runningapp.viewModels.StatisticsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.round
import kotlin.math.roundToInt

@AndroidEntryPoint
class StatisticsFragment : Fragment(R.layout.fragment_statistics) {

    private val viewModel: StatisticsViewModel by viewModels()
    lateinit var binding: FragmentStatisticsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStatisticsBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subscribeToObservers()
        setUpBarchart()
    }

    private fun setUpBarchart(){
        binding.barChart.xAxis.apply {
            position=XAxis.XAxisPosition.BOTTOM
            setDrawLabels(false)
            setDrawGridLines(false)
            axisLineColor= Color.BLUE
            textColor=Color.BLUE
        }
        binding.barChart.axisLeft.apply {
            setDrawGridLines(false)
            axisLineColor= Color.BLUE
            textColor=Color.BLUE
        }
        binding.barChart.axisRight.apply {
            setDrawGridLines(false)
            axisLineColor= Color.BLUE
            textColor=Color.BLUE
        }
        binding.barChart.apply {
            description.text="Avg Speed At All Time "
            legend.isEnabled=false
        }
    }


    private fun subscribeToObservers() {

        viewModel.totalTimes.observe(viewLifecycleOwner) {
            it?.let {
                val totalTime = TrackingUtility.getFormattedStopWitchTime(it)
                binding.tvTotalTime.text = totalTime
            }

        }
        viewModel.totalDistance.observe(viewLifecycleOwner) {
            it?.let {

                binding.tvTotalDistance.text = "$it m"
            }
        }
        viewModel.totalAveSpeed.observe(viewLifecycleOwner) {
            it?.let {
                val avgSpeed =(it * 10000.0).roundToInt() / 10000.0
                binding.tvAverageSpeed.text = "$avgSpeed Km/h"
            }
        }
        viewModel.totalCaloriesBurned.observe(viewLifecycleOwner) {
            it?.let {
                binding.tvTotalCalories.text="$it Kcal"
            }
        }

        viewModel.runsSortedByDate.observe(viewLifecycleOwner) {
            it?.let {
                val allAvgSpeed=it.indices.map { index->
                    BarEntry(index.toFloat(),it[index].aveSpeedInKMH)
                }
                val barDataSet=BarDataSet(allAvgSpeed,"Avg Speed over Time .").apply {
                    valueTextColor=Color.BLUE
                    color=Color.BLUE
                }
                binding.barChart.data= BarData(barDataSet)
                binding.barChart.marker=CustomeMarkerView(requireContext(),
                    it.reversed(),R.layout.marker_view)
                binding.barChart.invalidate()
            }
        }


    }


}






