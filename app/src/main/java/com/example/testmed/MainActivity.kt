package com.example.testmed

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.example.testmed.databinding.ActivityMainBinding
import com.example.testmed.databinding.ActivityMainDoctorBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.database.ServerValue
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class MainActivity : AppCompatActivity() {

    private val binding get() = _binding!!
    private var _binding: ActivityMainBinding? = null

    private val navView by lazy(LazyThreadSafetyMode.NONE) {
        binding.navView
    }

    private val navController: NavController by lazy(LazyThreadSafetyMode.NONE) {
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


    private fun updateStatePatientOffline(time: Any) {
        val refState = DB.reference.child("patients").child(UID()).child("state")
        lifecycleScope.launch(Dispatchers.IO) {
            val data: String? = refState.get().await().getValue(String::class.java)
            if (data != null) {
                refState.setValue(time)
                    .addOnCompleteListener {
                        lifecycleScope.launch(Dispatchers.IO){
                            val data = refState.get().await().getValue(Any::class.java)
                            val date = data.toString().asDate()
                            PATIENT_STATUS = date
                        }
                    }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        lifecycleScope.launch(Dispatchers.IO) {
            val data = ServerValue.TIMESTAMP
            updateStatePatientOffline(data)
        }
    }

    private fun hideActionBarOnDestinationChange() {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.navigation_register,
                R.id.navigation_enter,
                R.id.navigation_userDataSignOutFragment,
                R.id.navigation_login,
                R.id.navigation_change_patient_data,
                R.id.navigation_chat_with_doctor,
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