package com.magdy.runningapp.ui

import android.Manifest
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.fragment.app.Fragment

import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.magdy.runningapp.R
import com.magdy.runningapp.adapter.RunAdapter
import com.magdy.runningapp.databinding.FragmentRunBinding
import com.magdy.runningapp.utils.Constant.BACKGROUND_LOCATION
import com.magdy.runningapp.utils.Constant.COARSE_LOCATION
import com.magdy.runningapp.utils.Constant.FINE_LOCATION
import com.magdy.runningapp.utils.Constant.KEY_NAME
import com.magdy.runningapp.utils.Constant.RATIONAL_MESSAGE
import com.magdy.runningapp.utils.Constant.REQUEST_CODE
import com.magdy.runningapp.utils.SortType
import com.magdy.runningapp.utils.TrackingUtility
import com.magdy.runningapp.viewModels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import pub.devrel.easypermissions.RationaleDialogFragment
import javax.inject.Inject

@AndroidEntryPoint
class RunFragment : Fragment(), EasyPermissions.PermissionCallbacks {

    private val viewModel: MainViewModel by viewModels()
    private lateinit var runAdapter: RunAdapter
    lateinit var binding:FragmentRunBinding

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentRunBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requestPermissions()
        setUpRecyclerView()
        when(viewModel.sortType){
            SortType.DATE->binding.spFilter.setSelection(0)
            SortType.TIME_RUNNING->binding.spFilter.setSelection(1)
            SortType.DISTANCE->binding.spFilter.setSelection(2)
            SortType.AVE_SPEED->binding.spFilter.setSelection(3)
            SortType.CALORIES_BURNED->binding.spFilter.setSelection(4)
        }

        binding.spFilter.onItemSelectedListener=object :AdapterView.OnItemSelectedListener{
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

        viewModel.runs.observe(viewLifecycleOwner) {list->
            runAdapter.submitList(list)
        }

        binding.fab.setOnClickListener {
            findNavController().navigate(R.id.action_runFragment_to_trackingFragment)
        }

    }

    private fun setUpRecyclerView()=binding.rvRuns.apply {
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







