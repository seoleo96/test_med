package com.example.testmed.patient.speciality.consulting.presentation

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.navArgs
import com.example.testmed.DB
import com.example.testmed.UID
import com.example.testmed.base.BaseFragment
import com.example.testmed.databinding.SelectDateConsultingFragmentBinding
import com.example.testmed.model.CommonPatientData
import com.example.testmed.showSnackbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import java.util.*

class SelectDateConsultingFragment
    :
    BaseFragment<SelectDateConsultingFragmentBinding>(SelectDateConsultingFragmentBinding::inflate),
    DatePickerDialog.OnDateSetListener {

    private val args: SelectDateConsultingFragmentArgs by navArgs()
    val calendar = Calendar.getInstance()
    val day = calendar.get(Calendar.DAY_OF_MONTH);
    private lateinit var idDoctor: String
    private lateinit var commonPatientData: CommonPatientData
    private lateinit var idPatient: String
    private lateinit var fullName: String
    private lateinit var time: String
    private lateinit var idNotification: String
    private lateinit var dateToDB: String

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setPatientData()
        setIdDoctor()
        setDatePicker()
        binding.textRegister.setOnClickListener {
            DB.reference
                .child("consulting")
                .child(idDoctor)
                .child("25-02-2022")
                .child("fullName")
                .setValue("asdasd")
        }
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
                            .also { commonPatientData = it!! }
                    }
                }

                override fun onCancelled(error: DatabaseError) = Unit
            })
    }


    override fun onDateSet(view: DatePickerDialog?, year1: Int, monthOfYear: Int, dayOfMonth: Int) {
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
                    refDB.setValue("consulting")
                }
            }

            override fun onCancelled(error: DatabaseError) = Unit
        })

    }

    private fun checkValueDoctor() {
        val refConsulting = DB.reference.child("consulting")
        refConsulting.child(idDoctor).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    checkValueDate()
                } else {
                    refConsulting.setValue(idDoctor)
                }
            }

            override fun onCancelled(error: DatabaseError) = Unit
        })
    }

    private fun checkValueDate() {
        val refConsulting = DB.reference.child("consulting").child(idDoctor)
        refConsulting.child(dateToDB).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    checkValueTime()
                } else {
                    refConsulting.setValue(dateToDB)
                }
            }

            override fun onCancelled(error: DatabaseError) = Unit
        })
    }

    private fun checkValueTime() {
        val refConsulting =
            DB.reference.child("consulting").child(idDoctor).child(dateToDB)
        refConsulting.child("09-00").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    showSnackbar("yes time reference")
                } else {
                    showSnackbar("no time reference")
                }
            }

            override fun onCancelled(error: DatabaseError) = Unit
        })
    }

    private fun getReadyDates(date: Int) =
        if (date.toString().length == 1) "0$date" else date.toString()


}