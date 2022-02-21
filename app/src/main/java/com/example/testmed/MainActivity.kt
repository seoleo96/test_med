package com.example.testmed

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.testmed.databinding.ActivityMainBinding
import com.example.testmed.databinding.ActivityMainDoctorBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private val binding get() = _binding!!
    private var _binding: ActivityMainBinding? = null

    private val navView by lazy(LazyThreadSafetyMode.NONE) {
        binding.navView
    }

    private val navController : NavController by lazy(LazyThreadSafetyMode.NONE) {
        findNavController(R.id.nav_host_fragment_activity_main)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val appBarConfiguration = AppBarConfiguration(setOf(
            R.id.navigation_home,
            R.id.navigation_chats,
            R.id.navigation_profile,
            R.id.navigation_clinic))
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        hideActionBarOnDestinationChange()
    }

    private fun hideActionBarOnDestinationChange() {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.navigation_register,
                R.id.navigation_enter,
                R.id.navigation_userDataSignOutFragment,
                R.id.navigation_login,
                R.id.navigation_change_patient_data,
                R.id.navigation_chat_with_doctor
                -> {
                    supportActionBar?.hide()
                    navView.isVisible = false
                }
                R.id.navigation_doctors_data_fragment,
                R.id.navigation_doctors_fragment,
                R.id.navigation_home,
                R.id.navigation_chats,
                R.id.navigation_profile,
                R.id.navigation_clinic,
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
}