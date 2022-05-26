package com.example.testmed.doctor.chatwithpatient.patientprofile

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.testmed.R
import com.example.testmed.base.BaseFragmentDoctor
import com.example.testmed.databinding.PatientProfileFragmentBinding
import com.example.testmed.patient.profile.profilepatient.PatientDataResult
import com.example.testmed.patient.profile.profilepatient.presentation.ProfileDataViewModel

class PatientProfileFragment :
    BaseFragmentDoctor<PatientProfileFragmentBinding>(PatientProfileFragmentBinding::inflate) {

    private lateinit var patientProfileViewModel: PatientProfileViewModel
    private val args: PatientProfileFragmentArgs by navArgs()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        patientProfileViewModel = ViewModelProvider(this)[PatientProfileViewModel::class.java]
        setPatientData()
    }

    private fun setPatientData() {
        patientProfileViewModel.fetchPatientData(args.patientId)
        patientProfileViewModel.profileLiveData.observe(viewLifecycleOwner) { data ->
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

                is PatientDataResult.Success -> {
                    visibleContent()
                    with(data.data) {
                        binding.iin.text = iin
                        val fio = "$surname $name $patronymic"
                        binding.fullName.text = fio
                        binding.address.text = address
                        binding.phoneNumber.text = phoneNumber
                        binding.birthday.text = birthday
                        binding.gender.text = gender
                        if (photoUrl.isNotEmpty()) {
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