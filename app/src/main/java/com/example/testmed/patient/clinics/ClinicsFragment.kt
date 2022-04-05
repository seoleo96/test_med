package com.example.testmed.patient.clinics

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.navigation.fragment.findNavController
import com.example.testmed.DB
import com.example.testmed.R
import com.example.testmed.base.BaseFragment
import com.example.testmed.databinding.FragmentClinicsBinding
import com.example.testmed.model.AllClinicData
import com.example.testmed.model.ClinicData
import com.example.testmed.model.CommonPatientData
import com.example.testmed.model.SpecialityData
import com.example.testmed.patient.speciality.presentation.SpecialityAdapter
import com.example.testmed.patient.speciality.presentation.SpecialityDoctorsFragmentDirections
import com.example.testmed.patient.speciality.presentation.SpecialityDoctorsViewModel
import com.example.testmed.showSnackbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener

class ClinicsFragment : BaseFragment<FragmentClinicsBinding>(FragmentClinicsBinding::inflate) {

    private lateinit var valueEventListener: ValueEventListener
    private lateinit var rdbRef: DatabaseReference
    private lateinit var adapter : ClinicsAdapter
    private val list = mutableListOf<AllClinicData>()
    private val searchList = mutableListOf<AllClinicData>()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setAdapter()
        searchDoctor()
        setAllClinics()

    }

    private fun setAllClinics() {
        rdbRef = DB.reference
            .child("clinics")
        valueEventListener = rdbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    snapshot.children.forEach {
                        Log.d("children", it.value.toString())
                        val data: AllClinicData? = it.getValue(AllClinicData::class.java)
                        if (data!=null){
                            list.removeIf {
                                it.id == data.id
                            }
                            list.add(data)
                        }
                    }
                } else {
                    showSnackbar("Нет клиники.")
                }
                adapter.updateList(list.toMutableList())

            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    private fun searchDoctor() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                binding.searchView.clearFocus()
                if (query?.length!! > 2) {
                    list.forEach {
                        if (!searchList.contains(it)) {
                            if (it.name.contains(query.toString())) {
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
                            if (it.name.contains(newText.toString(), ignoreCase = true)) {
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
        adapter = ClinicsAdapter{
            if(it.specialities != "0"){
                val action =
                    ClinicsFragmentDirections.actionClinicsFragmentToNavigationClinic()
                action.idClinic = it.id
                findNavController().navigate(action)
                Log.d("Clinics", it.name)
            }else{
                showSnackbar("Клиника в разработке, скоро появятся данные.")
                Log.d("Clinics", "В разработке этот процес")
            }

        }
        binding.recyclerView.adapter = adapter
    }


    fun setClinics(){
        val uid = DB.reference.child("clinics").push().key.toString()
        val clinicData = AllClinicData(
            id = uid,
            name = "Клиника Тест Мед",
            address = "Интернационал 123",
            email = "testmed@gmail.com",
            link = "testmed.kz",
            phoneNumber = "87055597335"
        )
        DB.reference.child("clinics").child(uid).setValue(clinicData)
    }

}