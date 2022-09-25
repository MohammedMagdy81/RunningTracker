package com.magdy.runningapp.ui

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.magdy.runningapp.R
import com.magdy.runningapp.viewModels.StatisticsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StatisticsFragment : Fragment(R.layout.fragment_statistics) {

    private val viewModel: StatisticsViewModel by viewModels()
}