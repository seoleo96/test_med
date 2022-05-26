package com.example.testmed.doctor.profile.ui

import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.testmed.AUTH
import com.example.testmed.PHONE_NUMBER
import com.example.testmed.R
import com.example.testmed.UID
import com.example.testmed.base.BaseFragmentDoctor
import com.example.testmed.databinding.FragmentProfileDoctorBinding
import com.example.testmed.doctor.profile.data.DoctorInfo
import com.example.testmed.doctor.profile.data.net.ProfileDoctorCloudDataSource
import com.example.testmed.doctor.profile.model.DoctorInfoData
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.launch

class ProfileDoctorFragment
    : BaseFragmentDoctor<FragmentProfileDoctorBinding>(
    FragmentProfileDoctorBinding::inflate) {
    private lateinit var docInfoData : DoctorInfoData
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launch {
            initData()
        }
        signOut()
        val data = ProfileDoctorCloudDataSource()
        lifecycleScope.launch {
            data.fetchDoctorData(UID())
        }


    }

    private suspend fun initData() {
        val profileDoctorCloudDataSource = ProfileDoctorCloudDataSource()
        val docInfo: DoctorInfo.DoctorProfileMapper.Base = DoctorInfo.DoctorProfileMapper.Base()
        docInfoData = profileDoctorCloudDataSource.fetchDoctorData(UID()).map(docInfo)
        binding.toComments.setOnClickListener {
            val action = ProfileDoctorFragmentDirections.actionNavigationProfileDoctorToCommentsToDoctorFragment2(docInfoData.id, "0")
            findNavController().navigate(action)
        }
        setProfileData(docInfoData)
    }

    private fun signOut() {
        binding.changeData.setOnClickListener {
            AUTH().signOut()
            PHONE_NUMBER = ""
            findNavController().apply {
                popBackStack(R.id.navigation_home_doctor, true)
                popBackStack(R.id.navigation_chat_with_patient_fragment, true)
                popBackStack(R.id.navigation_home_doctor, true)
                navigate(R.id.navigation_login_doctor)
            }
        }
    }


    private fun setProfileData(data: DoctorInfoData) {

        binding.apply {
            iin.text = data.iin
            fullName.text = data.fio
            address.text = data.address
            phoneNumber.text = data.phoneNumber
            birthday.text = data.birthday
            login.text = data.login
            password.text = data.password
            gender.text = data.gender
            if (data.photoUrl.isNotEmpty()) {
                Glide
                    .with(requireContext())
                    .load(data.photoUrl)
                    .centerCrop()
                    .into(imageView);
            }
            binding.changePassword.setOnClickListener {
                val action =
                    ProfileDoctorFragmentDirections.actionNavigationProfileDoctorToChangePasswordDoctorFragment(
                        data.id,
                        data.password
                    )
                findNavController().navigate(action)
            }
        }
    }
}