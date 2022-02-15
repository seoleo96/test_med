package com.example.testmed.ui.home

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.testmed.*
import com.example.testmed.base.BaseFragment
import com.example.testmed.model.PatientData
import com.example.testmed.databinding.FragmentHomeBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener

class HomeFragment : BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate) {

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var valueEventListener: ValueEventListener
    private lateinit var rdbRef: DatabaseReference

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rdbRef = DB.reference
            .child("patients")
            .child(UID)
        valueEventListener = rdbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val data: PatientData? = snapshot.getValue(PatientData::class.java)
                    binding.textHome.text =
                        "${data?.phoneNumber ?: "null"}, ${data?.login ?: "null"}"
                } else {
                    findNavController().navigate(R.id.navigation_login,)
                }
            }

            override fun onCancelled(error: DatabaseError) {
//                    requireActivity().finish()
            }
        })
    }

    override fun onPause() {
        super.onPause()
        rdbRef.removeEventListener(valueEventListener)
    }
}