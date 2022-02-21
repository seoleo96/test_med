package com.example.testmed.doctor

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.testmed.R
import com.example.testmed.databinding.ActivityMainDoctorBinding

class MainActivityDoctor : AppCompatActivity() {

    private val binding get() = _binding!!
    private var _binding: ActivityMainDoctorBinding? = null
    private val navController: NavController by lazy {
        findNavController(R.id.nav_host_fragment_activity_main_doctor)
    }
    private val navView by lazy(LazyThreadSafetyMode.NONE) {
        binding.navViewDoctor
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainDoctorBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val appBarConfiguration = AppBarConfiguration(setOf(
            R.id.navigation_home_doctor,
            R.id.navigation_profile_doctor))
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        hideActionBarOnDestinationChange()
    }

    private fun hideActionBarOnDestinationChange() {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.navigation_login_doctor,
                R.id.navigation_chat_with_patient_fragment,
                -> {
                    supportActionBar?.hide()
                    navView.isVisible = false
                }

                R.id.navigation_home_doctor,
                R.id.navigation_profile_doctor,
                -> {
                    supportActionBar?.hide()
                    navView.isVisible = true
                }
                else -> {
                    supportActionBar?.show()
                    navView.isVisible = true
                }
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}