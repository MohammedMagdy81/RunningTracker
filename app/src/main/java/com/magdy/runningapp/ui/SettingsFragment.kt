package com.magdy.runningapp.ui

import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.snackbar.Snackbar
import com.magdy.runningapp.R
import com.magdy.runningapp.databinding.FragmentSettingsBinding
import com.magdy.runningapp.utils.Constant.KEY_LENGTH
import com.magdy.runningapp.utils.Constant.KEY_NAME
import com.magdy.runningapp.utils.Constant.KEY_WEIGHT
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SettingsFragment : Fragment() {

    @Inject
    lateinit var sharedPref: SharedPreferences
    lateinit var binding:FragmentSettingsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentSettingsBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadDataFromSharedPref()
        binding.btnApplyChanges.setOnClickListener {
            val success = applyChangeToSharedPref()
            if (success) {
                showSnackbar("Changes Applied Successfully . . ")
            } else {
                showSnackbar("Please Enter All Field !")

            }
        }
    }


    private fun showSnackbar(message: String) {
        Snackbar.make(requireView(), message, Snackbar.LENGTH_LONG).show()
    }

    private fun applyChangeToSharedPref(): Boolean {
        val name = binding.etNameSetting.text.toString()
        val weight = binding.etWeightSetting.text.toString()
        val length = binding.etLengthSetting.text.toString()

        if (name.isEmpty() || weight.isEmpty() || length.isEmpty()) {
            return false
        }
        sharedPref.edit()
            .putString(KEY_NAME, name)
            .putFloat(KEY_WEIGHT, weight.toFloat())
            .putFloat(KEY_LENGTH, length.toFloat())
            .apply()
        return true
    }

    private fun loadDataFromSharedPref() {
        val name = sharedPref.getString(KEY_NAME, "") ?: ""
        val weight = sharedPref.getFloat(KEY_WEIGHT, 0f)
        val length = sharedPref.getFloat(KEY_LENGTH, 0f)

        binding.etNameSetting.setText(name)
        binding.etWeightSetting.setText(weight.toString())
        binding.etLengthSetting.setText(length.toString())
    }

}