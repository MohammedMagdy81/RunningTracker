package com.magdy.runningapp.ui
import androidx.fragment.app.Fragment

import androidx.fragment.app.viewModels
import com.magdy.runningapp.R
import com.magdy.runningapp.viewModels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TrackingFragment : Fragment(R.layout.fragment_tracking) {

    private val viewModel: MainViewModel by viewModels()
}