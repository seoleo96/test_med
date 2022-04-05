package com.example.testmed.patient.speciality.doctors

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.SearchView
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
    private val searchList = mutableListOf<DoctorData>()
    private val newList = mutableListOf<String>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setAdapter()
        Log.d("newspasswordIDClinicDoctorfrag", args.idClinic)
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
                                if (args.idClinic != "0" && data.idClinic == args.idClinic) {
                                    list.add(data)
                                }
                                if (args.idClinic == "0") {
                                    list.add(data)
                                }
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

        searchDoctor()
    }

    private fun searchDoctor() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                binding.searchView.clearFocus()
                if (query?.length!! > 2) {
                    list.forEach {
                        if (!searchList.contains(it)) {
                            if (it.name.contains(query.toString()) ||
                                it.surname.contains(query.toString()) ||
                                it.patronymic.contains(query.toString())
                            ) {
                                searchList.add(it)
                            }
                        }
                    }
                    adapter.updateList(searchList)
                } else {
                    adapter.updateList(list)
                    searchList.clear()
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText?.length!! > 2) {
                    list.forEach {
                        if (!searchList.contains(it)) {
                            if (it.name.contains(newText.toString(), ignoreCase = true) ||
                                it.surname.contains(newText.toString(), ignoreCase = true) ||
                                it.patronymic.contains(newText.toString(), ignoreCase = true)
                            ) {
                                searchList.add(it)
                            }
                        }
                    }
                    adapter.updateList(searchList)
                } else {
                    adapter.updateList(list)
                    searchList.clear()
                }
                return false
            }


        })
    }

    private fun setAdapter() {
        adapter = DoctorsAdapter {
            val action =
                DoctorsFragmentDirections.actionDoctorsFragmentToDoctorsDataFragment(it.id,
                    args.idClinic)
            findNavController().navigate(action)
        }
        binding.recyclerView.adapter = adapter
    }

}