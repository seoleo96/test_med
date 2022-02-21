package com.example.testmed.patient.speciality.doctors

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.testmed.DB
import com.example.testmed.base.BaseFragment
import com.example.testmed.databinding.FragmentDoctorsBinding
import com.example.testmed.model.DoctorData
import com.example.testmed.showSnackbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class DoctorsFragment :
    BaseFragment<FragmentDoctorsBinding>(FragmentDoctorsBinding::inflate) {

    private val args: DoctorsFragmentArgs by navArgs()
    private lateinit var adapter: DoctorsAdapter
    private val list = mutableListOf<DoctorData>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setAdapter()
        DB.reference
            .child("doctors")
            .orderByChild("idSpeciality")
            .equalTo(args.idDoctor)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        snapshot.children.forEach {
                            val data: DoctorData? = it.getValue(DoctorData::class.java)
                            if (data != null) {
                                list.removeIf {
                                    it.id == data.id
                                }
                                list.add(data)
                            }
                        }
                    }

                    list.forEach {
                        Log.d("doctors", it.toString())
                    }
                    adapter.updateList(list.toMutableList())
                }

                override fun onCancelled(error: DatabaseError) = Unit
            })
    }

    private fun setAdapter() {
        adapter = DoctorsAdapter {
            showSnackbar(it.toString())
            val action = DoctorsFragmentDirections.actionDoctorsFragmentToDoctorsDataFragment(it.id)
            findNavController().navigate(action)
        }
        binding.recyclerView.adapter = adapter
    }

}