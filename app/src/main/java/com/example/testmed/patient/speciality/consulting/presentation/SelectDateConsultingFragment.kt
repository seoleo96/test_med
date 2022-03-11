package com.example.testmed.patient.speciality.consulting.presentation

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.testmed.DB
import com.example.testmed.R
import com.example.testmed.UID
import com.example.testmed.base.BaseFragment
import com.example.testmed.databinding.SelectDateConsultingFragmentBinding
import com.example.testmed.model.CommonPatientData
import com.example.testmed.model.DoctorData
import com.example.testmed.showSnackbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
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
    private lateinit var costOfConsulting: String
    private lateinit var timeConsulting: String
    private lateinit var phoneNumberDoctor: String
    private lateinit var phoneNumberPatient: String
    private lateinit var photoUrl: String
    private lateinit var photoUrlPatient: String
    private lateinit var idNotification: String
    private lateinit var speciality: String
    private lateinit var dateToDB: String
    private lateinit var adapter: ScheduleAdapter
    private lateinit var refDoctorData: DatabaseReference
    private lateinit var refPatientsData: DatabaseReference
    private lateinit var refConsulting: DatabaseReference
    private lateinit var refConsultingDocId: DatabaseReference
    private lateinit var refSetConsultingDate: DatabaseReference
    private lateinit var valueEventListener: ValueEventListener


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setDatabaseRefs()
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

    private fun setDatabaseRefs() {
        refConsulting = DB.reference.child("consulting")
    }

    private fun toConfirmConsultingDate() {
        val action = SelectDateConsultingFragmentDirections
            .actionSelectDateConsultingFragmentToConfirmConsultingDateFragment(
                fullNamePatient,
                fullNameDoctor,
                dateToDB,
                timeConsulting,
                phoneNumberDoctor,
                tokenDoctor,
                idDoctor,
                idPatient,
                idNotification,
                costOfConsulting,
                speciality,
                photoUrl,
                phoneNumberPatient,
                photoUrlPatient
            )
        findNavController().navigate(action)
    }

    private fun setDoctorData() {
        refDoctorData = DB.reference
            .child("doctors")
            .child(idDoctor)
        valueEventListener = refDoctorData.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    snapshot.getValue(DoctorData::class.java)
                        .also { it ->
                            if (it != null) {
                                fullNameDoctor =
                                    "${it.name} ${it.surname} ${it.patronymic}"
                                tokenDoctor = it.token
                                costOfConsulting = it.costOfConsultation
                                phoneNumberDoctor = it.phoneNumber
                                speciality = it.speciality
                                photoUrl = it.photoUrl
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
        refPatientsData = DB.reference
            .child("patients")
            .child(UID())
        valueEventListener = refPatientsData.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    snapshot.getValue(CommonPatientData::class.java)
                        .also { it ->
                            commonPatientData = it!!
                            fullNamePatient =
                                "${commonPatientData.name} ${commonPatientData.surname} ${commonPatientData.patronymic}"
                            idNotification =
                                ((it.iin.toLong() - 999900000000).toInt()).toString()
                            idPatient = it.id
                            phoneNumberPatient = it.phoneNumber
                            photoUrlPatient = it.photoUrl.toString()
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
        refSetConsultingDate = DB.reference.child("consulting").child(idDoctor)
        binding.etBirthday.setText(date)

        valueEventListener = refConsulting.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    checkValueDoctor()
                } else {
                    val consultingData = ConsultingData(
                        fullName = "Ali Alixanov Batrbek",
                        idPatient = "182736178238172689",
                        idNotification = "128712367",
                        time = "09-00",
                        statusConsulting = "inactive"
                    )
                    refConsulting
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
        val statusConsulting: String,
    ){
        constructor() : this("" ,"","","","")
    }

    private fun checkValueDoctor() {
        refConsultingDocId = DB.reference.child("consulting").child(idDoctor)
        valueEventListener = refConsultingDocId.addValueEventListener(object : ValueEventListener {
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

    override fun onStop() {
        super.onStop()
        refConsulting.removeEventListener(valueEventListener)
        refConsultingDocId.removeEventListener(valueEventListener)
        refDoctorData.removeEventListener(valueEventListener)
        refPatientsData.removeEventListener(valueEventListener)
        refSetConsultingDate.removeEventListener(valueEventListener)
    }

    private fun checkValueDate() {
        valueEventListener = refSetConsultingDate.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    lifecycleScope.launch(Dispatchers.IO) {
                        snapshot.children.forEach { snap ->
                            Log.d("CHILDREN", snap.toString())
                            val data = snap.getValue(ConsultingData::class.java)
                            mutableList.removeIf {
                                it == data?.time && data.statusConsulting == "active"
                            }
                        }
                        withContext(Dispatchers.Main) {
                            adapter.updateList(mutableList)
                            view?.findViewById<RecyclerView>(R.id.recycler_schedule)?.isVisible = true
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

    private fun createConsulting() {
        view?.findViewById<RecyclerView>(R.id.recycler_schedule)?.isVisible = true
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