package com.example.testmed.profile.presentation

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.testmed.*
import com.example.testmed.aboutclinic.ClinicDataResult
import com.example.testmed.base.BaseFragment
import com.example.testmed.databinding.FragmentProfileBinding
import com.example.testmed.model.PatientData
import com.example.testmed.profile.PatientDataResult
import com.example.testmed.profile.data.ProfileDataRepository
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener

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

    private fun setChangeData() {
        binding.changeData.setOnClickListener {
            findNavController().navigate(R.id.navigation_change_patient_data)
        }
    }

    private fun setProfileData() {
        profileDataViewModel.patientDataLiveData.observe(viewLifecycleOwner, { data ->
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
                        binding.fullName.text = "${name} ${surname} ${patronymic}"
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
        })
    }

    private fun visibleContent() {
        binding.apply {
            contentAllView.isVisible = true
            progressBar.isVisible = false
        }
    }

}