package com.example.testmed

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.testmed.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ServerValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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
            R.id.consultationInfoFragment,
            R.id.navigation_profile,
            R.id.clinicsFragment))
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        hideActionBarOnDestinationChange()
    }

    private fun updateStatePatientOffline(time: Any) {
        val uid = FirebaseAuth.getInstance().uid
        if (uid != null) {
            val refState = DB.reference.child("patients").child(uid).child("state")
            lifecycleScope.launch(Dispatchers.IO) {
                refState.setValue(time)
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
                R.id.selectDateConsultingFragment,
                R.id.confirmConsultingDateFragment,
                R.id.paymentConsultingFragment,
                R.id.commentsToDoctorFragment,
                R.id.navigation_doctors_data_fragment,
                R.id.navigation_clinic,
                R.id.specialitiesFragment,
                R.id.changePasswordFragment,
                R.id.newPasswordFragment,
                R.id.commentsClinicsFragment,
                R.id.mapsFragment,
                -> {
                    supportActionBar?.hide()
                    navView.isVisible = false
                }
                R.id.navigation_doctors_fragment,
                R.id.navigation_home,
                R.id.navigation_chats,
                R.id.navigation_profile,
                R.id.consultationInfoFragment,
                R.id.clinicsFragment,
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