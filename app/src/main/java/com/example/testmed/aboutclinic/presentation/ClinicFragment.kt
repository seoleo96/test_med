package com.example.testmed.aboutclinic.presentation

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import com.example.testmed.DB
import com.example.testmed.R
import com.example.testmed.aboutclinic.ClinicDataResult
import com.example.testmed.aboutclinic.data.ClinicDataRepository
import com.example.testmed.base.BaseFragment
import com.example.testmed.databinding.FragmentClinicBinding
import com.example.testmed.model.ClinicData
import com.example.testmed.showSnackbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener

class ClinicFragment : BaseFragment<FragmentClinicBinding>(FragmentClinicBinding::inflate) {

    private lateinit var valueEventListener: ValueEventListener
    private lateinit var rdbRef: DatabaseReference
    private lateinit var viewModel: ClinicDataViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var imgs = listOf<Int>(R.drawable.c2, R.drawable.c4, R.drawable.c5, R.drawable.c6)
        val clinicDataRepository = ClinicDataRepository()
        viewModel = ClinicDataViewModel(clinicDataRepository)
        var adapter: ViewPagerAdapter = ViewPagerAdapter()
        adapter.setList(imgs)
        binding.viewPager.adapter = adapter
        setUpData()
    }

    private fun setUpData() {
        viewModel.clinicDataLiveData.observe(viewLifecycleOwner, { data ->
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
                        binding.clinicName.text = name
                        binding.email.text = email
                        binding.link.text = link
                        binding.phoneNumber.text = phoneNumber
                        binding.address.text = address
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

//    override fun onPause() {
//        super.onPause()
//        rdbRef.removeEventListener(valueEventListener)
//    }
}