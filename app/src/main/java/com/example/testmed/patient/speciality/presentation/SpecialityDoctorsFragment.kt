package com.example.testmed.patient.speciality.presentation

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.testmed.*
import com.example.testmed.base.BaseFragment
import com.example.testmed.databinding.FragmentHomeBinding
import com.example.testmed.model.DoctorData
import com.example.testmed.model.SpecialityData
import com.example.testmed.patient.speciality.doctors.DoctorsFragmentArgs
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener

class SpecialityDoctorsFragment : BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate) {

    private val args: SpecialityDoctorsFragmentArgs by navArgs()
    private lateinit var specialityDoctorsViewModel: SpecialityDoctorsViewModel
    private lateinit var valueEventListener: ValueEventListener
    private lateinit var rdbRef: DatabaseReference
    private lateinit var adapter : SpecialityAdapter
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
    }


    private fun setDoctorData() {
        val doctor = DoctorData(
             id = "O6H2YhHQwZcUheh4v7eEvnCoeqo2",
         idSpeciality = "-Mw5Fr-9v0c3wn_towEM",
         iin = "13218956789088",
         name = "Арай",
         surname = "Султанова",
         patronymic = "Аяз",
         speciality= "Психолог",
         specialization = "Гештальтерапия, символдрама, интегративная психотерапия и прочее — для коллег и любителей рационализаций психологических состояний.",
         experience =  "10 лет",
         costOfConsultation = "4000тг",
         education = "Медицинский университет Астана, город Нурсултан",
         address = "ул Попова 25",
         birthday = "12.12.1982",
         gender = "Женской",
         login = "psixolog1@gmail.com",
         password = "123456",
         phoneNumber = "+1650555128765",
         photoUrl = "https://img.freepik.com/free-photo/beautiful-doctor-pointing-fingers_1258-16474.jpg?size=626&ext=jpg&ga=GA1.2.1450975410.1639094400",
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