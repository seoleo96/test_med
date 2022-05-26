package com.example.testmed.patient.aboutclinic.presentation

import android.content.Intent
import android.os.Bundle
import android.text.util.Linkify
import android.util.Log
import android.view.View
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.testmed.R
import com.example.testmed.patient.aboutclinic.ClinicDataResult
import com.example.testmed.patient.aboutclinic.data.ClinicDataRepository
import com.example.testmed.base.BaseFragment
import com.example.testmed.databinding.FragmentClinicBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener

class ClinicFragment : BaseFragment<FragmentClinicBinding>(FragmentClinicBinding::inflate) {

    private lateinit var viewModel: ClinicDataViewModel
    private val navArgs: ClinicFragmentArgs by navArgs()
    private var clinicName = ""


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val clinicDataRepository = ClinicDataRepository()
        viewModel = ClinicDataViewModel(clinicDataRepository)
        setUpData()

        binding.comments.setOnClickListener {
            val action =
                ClinicFragmentDirections.actionNavigationClinicToCommentsClinicsFragment(navArgs.idClinic)
            findNavController().navigate(action)
        }


    }

    private fun setUpData() {
        viewModel.getClinicDate(navArgs.idClinic)
        viewModel.clinicDataLiveData.observe(viewLifecycleOwner) { data ->
            when (data) {
                is ClinicDataResult.Loading -> {
                    binding.apply {
                        progressBar.isVisible = true
                        contentAllView.isVisible = false
                    }
                }
                is ClinicDataResult.Error -> {
                    visibleContent()
                    binding.textRegister.text = data.errorMessage
                }
                is ClinicDataResult.Success -> {
                    visibleContent()
                    with(data.data) {
                        val an = lat
                        val on = lon
                        val clinicName = name
                        binding.clinicName.text = name
                        binding.email.text = email
                        binding.link.text = link
                        Linkify.addLinks(binding.link, Linkify.WEB_URLS)
                        binding.phoneNumber.text = phoneNumber
                        binding.address.text = address
                        binding.startEndTime.text = startEndTime
                        binding.bank.text = bank
                        binding.bik.text = bik
                        binding.bin.text = bin
                        binding.city.text = city
                        binding.iik.text = iik
                        Glide
                            .with(binding.viewPager.context)
                            .load(imageUrl)
                            .fitCenter()
                            .into(binding.viewPager)
                        binding.map.setOnClickListener {

                            val action = ClinicFragmentDirections.actionNavigationClinicToMapsFragment(clinicName,
                                an,
                                on)
                            findNavController().navigate(action)
                        }
                    }

                    binding.sendUsersDataButton.setOnClickListener {
                        val action =
                            ClinicFragmentDirections.actionNavigationClinicToSpecialitiesFragment()
                        action.idClinic = data.data.id
                        findNavController().navigate(action)
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