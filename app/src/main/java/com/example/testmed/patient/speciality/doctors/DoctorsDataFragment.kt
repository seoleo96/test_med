package com.example.testmed.patient.speciality.doctors

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.testmed.DB
import com.example.testmed.R
import com.example.testmed.UID
import com.example.testmed.base.BaseFragment
import com.example.testmed.databinding.FragmentDoctorsDataBinding
import com.example.testmed.model.DoctorData
import com.example.testmed.showSnackbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*


class DoctorsDataFragment :
    BaseFragment<FragmentDoctorsDataBinding>(FragmentDoctorsDataBinding::inflate) {

    private lateinit var refDocData: DatabaseReference
    private lateinit var valueEventListener: ValueEventListener
    private val args: DoctorsDataFragmentArgs by navArgs()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toConsulting()
        toComments()
        toChat()
        setDoctorsData()
    }

    private fun toComments() {
        binding.toComments.setOnClickListener {
            val action =
                DoctorsDataFragmentDirections.actionNavigationDoctorsDataFragmentToCommentsToDoctorFragment(
                    args.idDoctor, "1")
            findNavController().navigate(action)
        }
    }

    private fun toConsulting() {
        binding.toConsulting.setOnClickListener {
            val action =
                DoctorsDataFragmentDirections.actionNavigationDoctorsDataFragmentToSelectDateConsultingFragment(
                    args.idDoctor, args.idClinic)
            findNavController().navigate(action)
        }
    }

    private fun toChat() {
        binding.toChat.setOnClickListener {
            val action =
                DoctorsDataFragmentDirections.actionDoctorsDataFragmentToChatWithDoctorFragment()
            action.idDoctor = args.idDoctor
            findNavController().navigate(action)
        }
    }


    private fun setDoctorsData() {
        refDocData = DB.reference
            .child("doctors").child(args.idDoctor)
        valueEventListener = refDocData.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    snapshot.getValue(DoctorData::class.java)?.apply {
                        binding.fullName.text = "${name} ${surname} ${patronymic}"
                        binding.speciality.text = speciality
                        binding.specialization.text = specialization
                        binding.experience.text = experience
                        binding.costOfConsultation.text = costOfConsultation
                        binding.education.text = education
                        if (photoUrl?.isNotEmpty()!!) {
                            Glide
                                .with(requireContext())
                                .load(photoUrl)
                                .fitCenter()
                                .into(binding.imageView)
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    override fun onPause() {
        super.onPause()
        refDocData.removeEventListener(valueEventListener)
    }
}