package com.example.testmed.patient.profile.profilepatient.presentation

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.testmed.*
import com.example.testmed.base.BaseFragment
import com.example.testmed.databinding.FragmentProfileBinding
import com.example.testmed.patient.profile.profilepatient.PatientDataResult
import com.example.testmed.patient.profile.profilepatient.data.ProfileDataRepository
import com.google.firebase.database.ServerValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ProfileFragment : BaseFragment<FragmentProfileBinding>(FragmentProfileBinding::inflate) {

    private lateinit var profileDataViewModel: ProfileDataViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val profileDataRepository = ProfileDataRepository()
        profileDataViewModel = ProfileDataViewModel(profileDataRepository)
        setProfileData()
        setChangeData()
        signOut()
    }

    private fun signOut() {
        binding.signOutApp.setOnClickListener {
            val data = ServerValue.TIMESTAMP
            setStatusOffline(data)
            profileDataViewModel.signOut()
            PHONE_NUMBER = ""
            findNavController().apply {
                popBackStack(R.id.navigation_clinic, true)
                popBackStack(R.id.navigation_change_patient_data, true)
                popBackStack(R.id.navigation_profile, true)
                popBackStack(R.id.navigation_home, true)
                popBackStack(R.id.navigation_chats, true)
                navigate(R.id.navigation_login)
            }
        }
    }

    private fun setStatusOffline(date: Any) {
        val refState = DB.reference.child("patients").child(UID()).child("state")
        lifecycleScope.launch(Dispatchers.IO) {
            val data: String? = refState.get().await().getValue(String::class.java)
            if (data != null) {
                refState.setValue(date)
                    .addOnCompleteListener {
                        try{
                            lifecycleScope.launch(Dispatchers.IO){
                                val data = refState.get().await().getValue(Any::class.java)
                                val date = data.toString().asDate()
                                PATIENT_STATUS = date
                            }
                        }catch (e:Exception){}

                    }
            }
        }
    }

    private fun setChangeData() {
        binding.changeData.setOnClickListener {
            findNavController().navigate(R.id.navigation_change_patient_data)
        }
    }

    private fun setProfileData() {
        profileDataViewModel.patientDataLiveData.observe(viewLifecycleOwner) { data ->
            when (data) {
                is PatientDataResult.Loading -> {
                    binding.apply {
                        progressBar.isVisible = true
                        contentAllView.isVisible = false
                    }
                }

                is PatientDataResult.Error -> {
                    visibleContent()
                    binding.textRegister.text = data.errorMessage
                }

                is PatientDataResult.NavigateToLogin -> findNavController().navigate(R.id.navigation_login)

                is PatientDataResult.Success -> {
                    visibleContent()
                    with(data.data) {
                        binding.iin.text = iin
                        binding.fullName.text = "${surname} ${name} ${patronymic}"
                        binding.address.text = address
                        binding.phoneNumber.text = phoneNumber
                        binding.birthday.text = birthday
                        binding.login.text = login
                        binding.password.text = password
                        binding.gender.text = gender
                        if (photoUrl?.isNotEmpty()!!) {
                            Glide
                                .with(requireContext())
                                .load(photoUrl)
                                .centerCrop()
                                .into(binding.imageView)
                        }

                    }
                }
            }
        }
    }

    private fun visibleContent() {
        binding.apply {
            contentAllView.isVisible = true
            progressBar.isVisible = false
        }
    }

}