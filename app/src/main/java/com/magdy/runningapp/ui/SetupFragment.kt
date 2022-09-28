package com.magdy.runningapp.ui

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.magdy.runningapp.R
import com.magdy.runningapp.databinding.FragmentSetupBinding
import com.magdy.runningapp.utils.Constant.KEY_FIRST_TIME_TOGGLE
import com.magdy.runningapp.utils.Constant.KEY_LENGTH
import com.magdy.runningapp.utils.Constant.KEY_NAME
import com.magdy.runningapp.utils.Constant.KEY_WEIGHT
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SetupFragment : Fragment() {


    @Inject
    lateinit var sharedPref: SharedPreferences
    lateinit var binding: FragmentSetupBinding
    @set:Inject
    var isFirstAppOpen = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentSetupBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (!isFirstAppOpen) {
            val navOption = NavOptions.Builder()
                .setPopUpTo(R.id.setupFragment, true)
                .build()
            findNavController().navigate(
                R.id.action_setupFragment_to_runFragment,
                savedInstanceState,
                navOption
            )
        }
        binding.btnContinue.setOnClickListener {
            val success = writePersonalDataToSharedPref()
            if (success) {
                findNavController().navigate(R.id.action_setupFragment_to_runFragment)
            } else {
                Snackbar.make(requireView(), "Please Enter All Data", Snackbar.LENGTH_LONG).show()
            }

        }
    }

    private fun writePersonalDataToSharedPref(): Boolean {
        val name = binding.etName.text.toString()
        val weight = binding.etWeight.text.toString()
        val length = binding.etLength.text.toString()

        if (name.isEmpty() || weight.isEmpty() || length.isEmpty()) {
            return false
        }
        sharedPref.edit()
            .putString(KEY_NAME, name)
            .putFloat(KEY_WEIGHT, weight.toFloat())
            .putFloat(KEY_LENGTH, length.toFloat())
            .putBoolean(KEY_FIRST_TIME_TOGGLE, false)
            .apply()
        return true

    }
}