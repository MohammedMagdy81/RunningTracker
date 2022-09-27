package com.magdy.runningapp.ui

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import androidx.fragment.app.Fragment

import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.magdy.runningapp.R
import com.magdy.runningapp.adapter.RunAdapter
import com.magdy.runningapp.utils.Constant.BACKGROUND_LOCATION
import com.magdy.runningapp.utils.Constant.COARSE_LOCATION
import com.magdy.runningapp.utils.Constant.FINE_LOCATION
import com.magdy.runningapp.utils.Constant.RATIONAL_MESSAGE
import com.magdy.runningapp.utils.Constant.REQUEST_CODE
import com.magdy.runningapp.utils.SortType
import com.magdy.runningapp.utils.TrackingUtility
import com.magdy.runningapp.viewModels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_run.*
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import pub.devrel.easypermissions.RationaleDialogFragment

@AndroidEntryPoint
class RunFragment : Fragment(R.layout.fragment_run), EasyPermissions.PermissionCallbacks {

    private val viewModel: MainViewModel by viewModels()
    private lateinit var runAdapter: RunAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requestPermissions()
        setUpRecyclerView()

        when(viewModel.sortType){
            SortType.DATE-> spFilter.setSelection(0)
            SortType.TIME_RUNNING->spFilter.setSelection(1)
            SortType.DISTANCE->spFilter.setSelection(2)
            SortType.AVE_SPEED->spFilter.setSelection(3)
            SortType.CALORIES_BURNED->spFilter.setSelection(4)
        }

        spFilter.onItemSelectedListener=object :AdapterView.OnItemSelectedListener{
            override fun onItemSelected(adapterView: AdapterView<*>?, view: View?, position: Int, id: Long) {
                when(position){
                 0->viewModel.sortType(SortType.DATE)
                 1->viewModel.sortType(SortType.TIME_RUNNING)
                 2->viewModel.sortType(SortType.DISTANCE)
                 3->viewModel.sortType(SortType.AVE_SPEED)
                 4->viewModel.sortType(SortType.CALORIES_BURNED)
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {}

        }

        viewModel.runs.observe(viewLifecycleOwner) {
            runAdapter.submitList(it)
        }

        fab.setOnClickListener {
            findNavController().navigate(R.id.action_runFragment_to_trackingFragment)
        }

    }

    private fun setUpRecyclerView()=rvRuns.apply {
        runAdapter=RunAdapter()
        setHasFixedSize(true)
        adapter=runAdapter
        layoutManager=LinearLayoutManager(requireContext())
    }

    private fun requestPermissions() {
        if (TrackingUtility.hasLocationPermission(requireContext())) {
            return
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            EasyPermissions.requestPermissions(
                this,
                RATIONAL_MESSAGE,
                REQUEST_CODE,
                FINE_LOCATION,
                COARSE_LOCATION
            )
        } else {
            EasyPermissions.requestPermissions(
                this,
                RATIONAL_MESSAGE,
                REQUEST_CODE,
                FINE_LOCATION,
                COARSE_LOCATION,
                BACKGROUND_LOCATION
            )
        }
    }


    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {

    }

    // called when user denied Mandatory Permission
    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            // dialog to lead user to the Setting
            AppSettingsDialog.Builder(this).build().show()
        }else{
            requestPermissions()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode,permissions,grantResults,this)
    }

}







