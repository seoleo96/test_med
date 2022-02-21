package com.example.testmed.patient.speciality.doctors

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.testmed.DB
import com.example.testmed.UID
import com.example.testmed.base.BaseFragment
import com.example.testmed.databinding.FragmentDoctorsDataBinding
import com.example.testmed.model.DoctorData
import com.example.testmed.showSnackbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*


class DoctorsDataFragment :
    BaseFragment<FragmentDoctorsDataBinding>(FragmentDoctorsDataBinding::inflate),
    DatePickerDialog.OnDateSetListener {

    private val args: DoctorsDataFragmentArgs by navArgs()
    private var mCalendar = Calendar.getInstance()
    private var birthday = ""
    val calendar = Calendar.getInstance();

    val Year = calendar.get(Calendar.YEAR);
    val Month = calendar.get(Calendar.MONTH);
    val Day = calendar.get(Calendar.DAY_OF_MONTH);

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toChat()
        setDoctorsData()
//        setDatePicker()
//        showDatePicker()
    }

    private fun toChat() {
        binding.toChat.setOnClickListener {
            val action =
                DoctorsDataFragmentDirections.actionDoctorsDataFragmentToChatWithDoctorFragment(
                    UID(),
                    args.idDoctor)
            findNavController().navigate(action)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun showDatePicker() {
        var calendar = Calendar.getInstance()
        val dpd = DatePickerDialog.newInstance(
            this,
            calendar[Calendar.YEAR],
            calendar[Calendar.MONTH],
            calendar[Calendar.DAY_OF_MONTH]
        )

        val sdf = SimpleDateFormat("dd-MM-yyyy")
        val date1: Date = sdf.parse(LocalDate.now().toString())
        showSnackbar(date1.toString())
        println(date1)
        val call: Calendar = dateToCalendar(date1)
        println(call?.time)
        val a = "26-07-2017";
        var date : Date? = null;
        try {
            date = sdf.parse(a);
            calendar = dateToCalendar(date);
            println(calendar.time)
        } catch ( e : ParseException) {
            e.printStackTrace();
        }

//        List<Calendar> dates = new ArrayList<>();
//        dates.add(calendar);
//        Calendar[] disabledDays1 = dates.toArray(new Calendar[dates.size()]);
//        dpd.setDisabledDays(disabledDays1);

        val min_date_c = Calendar.getInstance()
        dpd.minDate = min_date_c
        val max_date_c = Calendar.getInstance()
        max_date_c[Calendar.DAY_OF_MONTH] = Day +3

//        val dayOfWeek: Int = mCalendar.get(Calendar.DAY_OF_WEEK)+3
//        val dayOf: Int = Calendar.SATURDAY
//        Log.d("TAG", "$dayOf - $dayOfWeek")
//        if (dayOfWeek == Calendar.SATURDAY){
//            showSnackbar("true")
//        }

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

    private fun dateToCalendar(date: Date): Calendar {
        val calendar = Calendar.getInstance()
        calendar.time = date
        return calendar
    }

    private fun checkAge(age: Int): Boolean {
        if (age >= 18)
            return true
        return false
    }

    private fun getAge(year: Int, month: Int, day: Int): Int {
        val dob: Calendar = Calendar.getInstance()
        val today = Calendar.getInstance()
        dob[year, month] = day
        var age = today[Calendar.YEAR] - dob[Calendar.YEAR]
        if (today[Calendar.DAY_OF_YEAR] < dob[Calendar.DAY_OF_YEAR]) {
            age--
        }
        return age
    }

    private fun setDoctorsData() {
        DB.reference
            .child("doctors").child(args.idDoctor)
            .addValueEventListener(object : ValueEventListener {
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
                                    .centerCrop()
                                    .into(binding.imageView)
                            }
                        }
                    } else {

                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
    }

    override fun onDateSet(view: DatePickerDialog?, year: Int, monthOfYear: Int, dayOfMonth: Int) {
        TODO("Not yet implemented")
    }


}