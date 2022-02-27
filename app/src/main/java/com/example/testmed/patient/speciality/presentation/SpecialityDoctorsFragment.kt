package com.example.testmed.patient.speciality.presentation

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.testmed.*
import com.example.testmed.base.BaseFragment
import com.example.testmed.databinding.FragmentHomeBinding
import com.example.testmed.model.DoctorData
import com.example.testmed.model.SpecialityData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener

class SpecialityDoctorsFragment : BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate) {

    private lateinit var specialityDoctorsViewModel: SpecialityDoctorsViewModel
    private lateinit var valueEventListener: ValueEventListener
    private lateinit var rdbRef: DatabaseReference
    private lateinit var adapter : SpecialityAdapter
    private val list = mutableSetOf<SpecialityData>()
    var idSpeciality = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setAdapter()
        rdbRef = DB.reference
            .child("speciality")
        valueEventListener = rdbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    snapshot.children.forEach {
                        Log.d("children", it.value.toString())
                        val data: SpecialityData? = it.getValue(SpecialityData::class.java)
                        if (data!=null){
                            list.removeIf {
                                it.id == data.id
                            }
                            list.add(data)
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
//        setDoctorData()
    }





    private fun setDoctorData() {
        val doctor = DoctorData(
             id = "S8VsEJuyDJWGEf5e6UFRoZjxPQE3",
         idSpeciality = "-Mw5CaxfdS6zKHVevpeg",
         iin = "123456789088",
         name = "Ахмед",
         surname = "Адимов",
         patronymic = "Талгатович",
         speciality= "Терапевт",
         specialization = "Мы практикуем диагностику и лечение целого комплекса заболеваний внутренних органов. Специализация включает лечение установленного набора заболеваний. К ним в первую очередь относятся простуды и респираторно-вирусные инфекции (грипп, ОРЗ, насморк, другие заболевания)." ,
         experience =  "15 лет",
         costOfConsultation = "3000",
         education = "В 2009 году окончил Московский государственный медико-стоматологический университет. В 2010 году - обучение в клинической интернатуре по специальности «Стоматология общей практики» на базе Института повышения квалификации МО РФ. В 2010 - 2012 г. – ординатура на кафедре ЧЛХ «НМХЦ им. Н.И. Пирогова».",
         address = "ул Попова 25",
         birthday = "12.12.1979",
         gender = "Мужской",
         login = "terapevt2@gmail.com",
         password = "123456",
         phoneNumber = "+16505551289",
         photoUrl = "",
            state = "",
            stateTo = "",
            token = ""
        )
        DB.reference.child("doctors").child(doctor.id).setValue(doctor)
    }

    private fun setAdapter() {
        adapter = SpecialityAdapter{
            val action = SpecialityDoctorsFragmentDirections.actionNavigationHomeToDoctorsFragment(it.id)
            findNavController().navigate(action)
        }
        binding.recyclerView.adapter = adapter
    }
}