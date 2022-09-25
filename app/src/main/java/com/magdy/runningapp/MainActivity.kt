package com.magdy.runningapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.magdy.runningapp.databinding.ActivityMainBinding
import com.magdy.runningapp.db.RunDao
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding:ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        bottom_navigation.setupWithNavController(navHostFragmentt.findNavController())
        navHostFragmentt.findNavController().addOnDestinationChangedListener{_,destination,_->
            when(destination.id){
                R.id.runFragment ,R.id.settingsFragment,R.id.statisticsFragment->{
                    binding.bottomNavigation.visibility= View.VISIBLE

                }
                else->binding.bottomNavigation.visibility=View.GONE
            }
        }
//

    }
}