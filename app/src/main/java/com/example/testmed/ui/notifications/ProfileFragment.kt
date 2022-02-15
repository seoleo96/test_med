package com.example.testmed.ui.notifications

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.testmed.*
import com.example.testmed.base.BaseFragment
import com.example.testmed.databinding.FragmentProfileBinding
import com.example.testmed.model.PatientData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener

class ProfileFragment : BaseFragment<FragmentProfileBinding>(FragmentProfileBinding::inflate) {

    private lateinit var notificationsViewModel: NotificationsViewModel
    private lateinit var valueEventListener: ValueEventListener
    private lateinit var rdbRef: DatabaseReference

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setProfileData()
        setChangeData()
        signOut()
    }

    private fun signOut() {
        binding.signOutApp.setOnClickListener {
            AUTH.signOut()
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

    private fun setChangeData(){
        binding.changeData.setOnClickListener {
            findNavController().navigate(R.id.navigation_change_patient_data)
        }
    }

    private fun setProfileData() {
        rdbRef = DB.reference
            .child("patients")
            .child(UID)
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
                    findNavController().navigate(R.id.navigation_login)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    override fun onPause() {
        super.onPause()
        rdbRef.removeEventListener(valueEventListener)
    }

}