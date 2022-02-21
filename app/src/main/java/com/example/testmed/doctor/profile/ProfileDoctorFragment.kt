package com.example.testmed.doctor.profile

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.testmed.*
import com.example.testmed.base.BaseFragment
import com.example.testmed.databinding.FragmentProfileDoctorBinding
import com.example.testmed.model.PatientData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener

class ProfileDoctorFragment
    : BaseFragment<FragmentProfileDoctorBinding>(FragmentProfileDoctorBinding::inflate) {

    private lateinit var valueEventListener: ValueEventListener
    private lateinit var rdbRef: DatabaseReference

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        signOut()
        setProfileData()
    }

    private fun signOut() {
        binding.signOutApp.setOnClickListener {
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


    private fun setProfileData() {
        rdbRef = DB.reference
            .child("doctors")
            .child(UID())
        valueEventListener = rdbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val data = snapshot.getValue(PatientData::class.java)
                    binding.apply {
                        iin.text = data?.iin
                        fullName.text = "${data?.name} ${data?.surname} ${data?.patronymic}"
                        address.text = data?.address
                        phoneNumber.text = data?.phoneNumber
                        birthday.text = data?.birthday
                        login.text = data?.login
                        password.text = data?.password
                        gender.text = data?.gender
                        if (data?.photoUrl?.isNotEmpty()!!){
                            Glide
                                .with(requireContext())
                                .load(data.photoUrl)
                                .centerCrop()
                                .into(imageView);
                        }
                    }
                }else {
                    findNavController().navigate(R.id.navigation_login_doctor)
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    override fun onPause() {
        super.onPause()
        rdbRef.removeEventListener(valueEventListener)
    }

}