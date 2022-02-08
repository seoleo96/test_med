package com.example.testmed

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.testmed.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private val binding get() = _binding!!
    private var _binding : ActivityMainBinding? = null
    private val navController: NavController by lazy {
        findNavController(R.id.nav_host_fragment_activity_main)
    }
    private val navView by lazy(LazyThreadSafetyMode.NONE) {
        binding.navView
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val appBarConfiguration = AppBarConfiguration(setOf(
            R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications))
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        hideActionBarOnDestinationChange()
    }

    private fun hideActionBarOnDestinationChange() {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.navigation_register, R.id.navigation_enter, R.id.navigation_userDataSignOutFragment,
                R.id.navigation_login,
                -> {
                    supportActionBar?.hide()
                    navView.isVisible = false
                }
//                R.id.nav_home -> {
//                    navController.popBackStack(R.id.nav_home, false)
//                    supportActionBar?.show()
//                }
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