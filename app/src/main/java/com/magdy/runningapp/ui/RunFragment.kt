package com.magdy.runningapp.ui

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment

import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.magdy.runningapp.R
import com.magdy.runningapp.utils.Constant.BACKGROUND_LOCATION
import com.magdy.runningapp.utils.Constant.COARSE_LOCATION
import com.magdy.runningapp.utils.Constant.FINE_LOCATION
import com.magdy.runningapp.utils.Constant.RATIONAL_MESSAGE
import com.magdy.runningapp.utils.Constant.REQUEST_CODE
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requestPermissions()
        fab.setOnClickListener {
            findNavController().navigate(R.id.action_runFragment_to_trackingFragment)
        }

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







