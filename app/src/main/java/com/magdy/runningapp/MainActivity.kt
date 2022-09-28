package com.magdy.runningapp

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.magdy.runningapp.databinding.ActivityMainBinding
import com.magdy.runningapp.utils.Constant.ACTION_TRACKING_FRAGMENT_SHOW
import com.magdy.runningapp.utils.Constant.KEY_NAME
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding:ActivityMainBinding

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        binding.toolbar.title=sharedPreferences.getString(KEY_NAME,"")
        navigateToTrackingFragmentIfNeeded(intent)

        binding.bottomNavigationn.setupWithNavController(findNavController(R.id.navHostFragment))
        binding.bottomNavigationn.setOnNavigationItemReselectedListener {
            /* No Operation*/
        }

        findNavController(R.id.navHostFragment).addOnDestinationChangedListener{_,destination,_->

            when(destination.id){
                R.id.runFragment ,R.id.settingsFragment,R.id.statisticsFragment->{
                    binding.bottomNavigationn.visibility= View.VISIBLE
                }
                else->binding.bottomNavigationn.visibility=View.GONE
            }

        }
//        binding. navHostFragmentt.findNavController().addOnDestinationChangedListener{_,destination,_->
//            when(destination.id){
//                R.id.runFragment ,R.id.settingsFragment,R.id.statisticsFragment->{
//                    binding.bottomNavigationn.visibility= View.VISIBLE
//
//                }
//                else->binding.bottomNavigationn.visibility=View.GONE
//            }
//        }

    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        navigateToTrackingFragmentIfNeeded(intent)
    }

    private fun navigateToTrackingFragmentIfNeeded(intent: Intent?){
        if (intent?.action==ACTION_TRACKING_FRAGMENT_SHOW){
            findNavController(R.id.navHostFragment).navigate(R.id.action_global)
           // binding.navHostFragmentt.findNavController().navigate(R.id.action_global)
        }
    }
}