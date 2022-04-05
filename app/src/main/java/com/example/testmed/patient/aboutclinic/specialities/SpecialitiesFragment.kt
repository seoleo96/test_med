package com.example.testmed.patient.aboutclinic.specialities

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.testmed.DB
import com.example.testmed.R
import com.example.testmed.base.BaseFragment
import com.example.testmed.databinding.FragmentSpecialitiesBinding
import com.example.testmed.model.DoctorData
import com.example.testmed.model.SpecialityData
import com.example.testmed.showSnackbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener

class SpecialitiesFragment :
    BaseFragment<FragmentSpecialitiesBinding>(FragmentSpecialitiesBinding::inflate) {

    private val args: SpecialitiesFragmentArgs by navArgs()
    private lateinit var valueEventListener: ValueEventListener
    private lateinit var rdbRef: DatabaseReference
    private lateinit var adapter: SpecialityAdapter
    private val list = mutableSetOf<SpecialityData>()
    var idSpeciality = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setAdapter()
        setSpeciality()
//        setDoctorData()
    }

    private fun setSpeciality() {
        rdbRef = DB.reference
            .child("speciality")
        valueEventListener = rdbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    snapshot.children.forEach {
                        Log.d("children", it.value.toString())
                        val data: SpecialityData? = it.getValue(SpecialityData::class.java)
                        if (data != null) {
                            list.removeIf {
                                it.id == data.id
                            }
                            if (data.idClinic == args.idClinic) {
                                Log.d("children", "true")
                                list.add(data)
                            }
                        }
                    }
                } else {
                    showSnackbar("Нет врачей.")
                    findNavController().popBackStack()
                }
                adapter.updateList(list.toMutableList())

            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    private fun setAdapter() {
        adapter = SpecialityAdapter {
            val action = SpecialitiesFragmentDirections.actionSpecialitiesFragmentToNavigationDoctorsFragment(it.id)
            action.idClinic = it.idClinic
            findNavController().navigate(action)
        }
        binding.recyclerView.adapter = adapter
    }
}