package com.magdy.runningapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.magdy.runningapp.databinding.ActivityMainBinding
import com.magdy.runningapp.utils.Constant.ACTION_TRACKING_FRAGMENT_SHOW
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding:ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        navigateToTrackingFragmentIfNeeded(intent)

        bottom_navigation.setupWithNavController(navHostFragmentt.findNavController())
        navHostFragmentt.findNavController().addOnDestinationChangedListener{_,destination,_->
            when(destination.id){
                R.id.runFragment ,R.id.settingsFragment,R.id.statisticsFragment->{
                    binding.bottomNavigation.visibility= View.VISIBLE

                }
                else->binding.bottomNavigation.visibility=View.GONE
            }
        }

    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        navigateToTrackingFragmentIfNeeded(intent)
    }

    private fun navigateToTrackingFragmentIfNeeded(intent: Intent?){
        if (intent?.action==ACTION_TRACKING_FRAGMENT_SHOW){
            navHostFragmentt.findNavController().navigate(R.id.action_global)
        }
    }
}