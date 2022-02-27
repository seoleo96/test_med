package com.example.testmed.patient.speciality.consulting.presentation

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import com.example.testmed.DB
import com.example.testmed.UID
import com.example.testmed.base.BaseFragment
import com.example.testmed.databinding.SelectDateConsultingFragmentBinding
import com.example.testmed.model.CommonPatientData
import com.example.testmed.model.DoctorData
import com.example.testmed.showSnackbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.*


class SelectDateConsultingFragment :
    BaseFragment<SelectDateConsultingFragmentBinding>(SelectDateConsultingFragmentBinding::inflate),
    DatePickerDialog.OnDateSetListener {

    private val args: SelectDateConsultingFragmentArgs by navArgs()
    val calendar = Calendar.getInstance()
    val day = calendar.get(Calendar.DAY_OF_MONTH);
    private lateinit var idDoctor: String
    private lateinit var commonPatientData: CommonPatientData
    private lateinit var idPatient: String
    private lateinit var fullNamePatient: String
    private lateinit var fullNameDoctor: String
    private lateinit var tokenDoctor: String
    private lateinit var timeConsulting: String
    private lateinit var idNotification: String
    private lateinit var dateToDB: String
    private lateinit var adapter: ScheduleAdapter


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setPatientData()
        setIdDoctor()
        setDoctorData()
        setAdapter()
        setDatePicker()
        createList()
        binding.textRegister.setOnClickListener {
            DB.reference
                .child("consulting")
                .child(idDoctor)
                .child("25-02-2022")
                .child("fullName")
                .setValue("asdasd")
        }

        binding.nextButton.isEnabled = false
        binding.nextButton.setOnClickListener {
            toConfirmConsultingDate()
        }
    }

    private fun toConfirmConsultingDate() {
        val action = SelectDateConsultingFragmentDirections
            .actionSelectDateConsultingFragmentToConfirmConsultingDateFragment(
                fullNamePatient,
                fullNameDoctor,
                dateToDB,
                timeConsulting
            )
        findNavController().navigate(action)
    }

    private fun setDoctorData() {
        DB.reference
            .child("doctors")
            .child(idDoctor)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        snapshot.getValue(DoctorData::class.java)
                            .also {it->
                                if (it != null) {
                                    fullNameDoctor =
                                        "${it.name} ${it.surname} ${it.patronymic}"
                                    tokenDoctor = it.token
                                }
                                Log.d("TAGDOC", fullNameDoctor)
                                Log.d("TAGDOC", tokenDoctor)
                            }
                    }
                }

                override fun onCancelled(error: DatabaseError) = Unit
            })

    }

    private fun setDatePicker() {
        binding.etBirthday.setOnClickListener {
            showDatePicker()
        }
    }

    private fun setIdDoctor() {
        idDoctor = args.idDoctor
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()

        val dpd = DatePickerDialog.newInstance(
            this,
            calendar[Calendar.YEAR],
            calendar[Calendar.MONTH],
            calendar[Calendar.DAY_OF_MONTH]
        )

        val min_date_c = Calendar.getInstance()
        dpd.minDate = min_date_c
        val max_date_c = Calendar.getInstance()
        max_date_c[Calendar.DAY_OF_MONTH] = day + 4

        var loopdate: Calendar = min_date_c
        while (min_date_c.before(max_date_c)) {
            val dayOfWeek: Int = loopdate[Calendar.DAY_OF_WEEK]
            if (dayOfWeek == Calendar.SUNDAY || dayOfWeek == Calendar.SATURDAY) {
                val disabledDays: Array<Calendar?> = arrayOfNulls<Calendar>(1)
                disabledDays[0] = loopdate
                dpd.disabledDays = disabledDays
            }
            min_date_c.add(Calendar.DATE, 1)
            loopdate = min_date_c
        }
        dpd.maxDate = max_date_c
        dpd.show(childFragmentManager, "TAG")
    }

    private fun setPatientData() {
        DB.reference
            .child("patients")
            .child(UID())
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        snapshot.getValue(CommonPatientData::class.java)
                            .also {it->
                                commonPatientData = it!!
                                fullNamePatient =
                                    "${commonPatientData.name} ${commonPatientData.surname} ${commonPatientData.patronymic}"
                                idNotification =
                                    ((it.iin.toLong() - 999900000000).toInt()).toString()
                                idPatient = it.id
                            }
                    }
                }

                override fun onCancelled(error: DatabaseError) = Unit
            })

    }


    override fun onDateSet(view: DatePickerDialog?, year1: Int, monthOfYear: Int, dayOfMonth: Int) {
        binding.nextButton.isEnabled = false
        val day = getReadyDates(dayOfMonth)
        val month = getReadyDates(1 + monthOfYear)
        val year = getReadyDates(year1)
        val date = "$day.${month}.$year"
        dateToDB = "$day-${month}-$year"
        binding.etBirthday.setText(date)
        val refDB = DB.reference.child("consulting")
        refDB.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    checkValueDoctor()
                } else {
                    val consultingData = ConsultingData(
                        fullName = "Ali Alixanov Batrbek",
                        idPatient = "182736178238172689",
                        idNotification = "128712367",
                        time = "09-00"
                    )
                    refDB
                        .child("1234567890")
                        .child("01-01-2022")
                        .child("09:00")
                        .setValue(consultingData)
                }
            }

            override fun onCancelled(error: DatabaseError) = Unit
        })
    }

    data class ConsultingData(
        val time: String,
        val fullName: String,
        val idNotification: String,
        val idPatient: String,
    )

    private fun checkValueDoctor() {
        val refConsulting = DB.reference.child("consulting").child(idDoctor)
        refConsulting.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    checkValueDate()
                } else {
                    createConsulting()
                }
            }

            override fun onCancelled(error: DatabaseError) = Unit
        })
    }

    private fun checkValueDate() {
        val refConsultingDate = DB.reference.child("consulting").child(idDoctor).child(dateToDB)
        refConsultingDate.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    lifecycleScope.launch(Dispatchers.IO) {
                        snapshot.children.forEach { time ->
                            mutableList.removeIf { it == time.key.toString() }
                        }
                        withContext(Dispatchers.Main) {
                            adapter.updateList(mutableList)
                            binding.recyclerSchedule.isVisible = true
                        }
                    }
                } else {
                    createConsulting()
                }
            }

            override fun onCancelled(error: DatabaseError) = Unit
        })
    }

    val mutableList = mutableListOf<String>()

    private fun checkValueTime() {
        val refConsultingDate = DB.reference.child("consulting").child(idDoctor).child(dateToDB)
        refConsultingDate.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach {
                    val time = it.key.toString()
                    mutableList.removeIf { it == time }
                    adapter.updateList(mutableList)
                }
                binding.recyclerSchedule.isVisible = true
            }

            override fun onCancelled(error: DatabaseError) = Unit
        })
    }

    private fun createConsulting() {
        binding.recyclerSchedule.isVisible = true
        createList()
        adapter.updateList(mutableList)
    }


    private fun setAdapter() {
        adapter = ScheduleAdapter {
            binding.nextButton.isEnabled = true
            timeConsulting = it
        }
        binding.recyclerSchedule.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.recyclerSchedule.adapter = adapter

    }

    private fun createConsultingData(time: String) {
        val refConsulting =
            DB.reference.child("consulting").child(idDoctor).child(dateToDB).child(time)
        val patientInfo = ConsultingData(time, fullNamePatient, idNotification, idPatient)
        val bool = refConsulting.setValue(patientInfo)
        if (bool.isSuccessful) {
            showSnackbar("Вы записаны в консультацию")
        } else {
            showSnackbar("Вы уже записаны в консультацию")
        }
    }


    private fun createList() {
        mutableList.clear()
        mutableList.add("09-00")
        mutableList.add("10-00")
        mutableList.add("11-00")
        mutableList.add("12-00")
        mutableList.add("14-00")
        mutableList.add("15-00")
        mutableList.add("16-00")
        mutableList.add("17-00")
    }


    private fun getReadyDates(date: Int) =
        if (date.toString().length == 1) "0$date" else date.toString()


}