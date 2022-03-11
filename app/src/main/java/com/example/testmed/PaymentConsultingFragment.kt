package com.example.testmed

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.testmed.base.BaseFragment
import com.example.testmed.databinding.PaymentConsultingFragmentBinding
import com.example.testmed.model.CommonPatientData
import com.example.testmed.model.TransactionData
import com.example.testmed.notification.NotifyData
import com.example.testmed.notification.RetrofitClient
import com.example.testmed.notification.Sender
import com.example.testmed.patient.speciality.consulting.presentation.SelectDateConsultingFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

class PaymentConsultingFragment :
    BaseFragment<PaymentConsultingFragmentBinding>(PaymentConsultingFragmentBinding::inflate) {

    private val navArgs: PaymentConsultingFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toPayButton()
    }

    private fun toPayButton() {
        binding.toPay.setOnClickListener {
            validateData()
        }
    }

    private fun validateData() {
        binding.apply {
            if (etFio.text.isEmpty() ||
                etCreditCard.text.isEmpty() ||
                etCvv.text.isEmpty() ||
                etMonth.text.isEmpty() ||
                etYear.text.isEmpty()
            ) {
                etFio.error = "Запольните всех полей"
                etFio.requestFocus()
            } else if (etCreditCard.text.length < 16) {
                etCreditCard.error = "Код состоится из 16 цифр"
                etCreditCard.requestFocus()
            } else if (etYear.text.length < 2) {
                etYear.error = "Год состоится из 2 цифр"
                etYear.requestFocus()
            } else if (etMonth.text.length < 2) {
                etMonth.error = "Месяц состоитяс из 2 цифр"
                etMonth.requestFocus()
            } else if (etCvv.text.length < 3) {
                etCvv.error = "CVV код состоитяс из 3 цифр"
                etCvv.requestFocus()
            } else {
                payToClinic()
            }
        }
    }

    private fun payToClinic() {
        val idTransaction = DB.reference.child("cost_of_consulting").push().key.toString()
        val transactionData = TransactionData(
            cardNumber = binding.etCreditCard.text.toString(),
            costOfConsultation = navArgs.costOfConsulting,
            date = navArgs.dateConsulting,
            idDoctor = navArgs.idDoctor,
            idPatient = navArgs.idPatient,
            time = navArgs.timeConsulting,
            idTransaction = idTransaction,
            recordingDate = asDate(),
            statusConsulting = "active"
        )

        val commonPatientData = CommonPatientData(
            cardNumber = binding.etCreditCard.text.toString(),
            costOfConsultation = navArgs.costOfConsulting,
            date = navArgs.dateConsulting,
            idDoctor = navArgs.idDoctor,
            idPatient = navArgs.idPatient,
            time = navArgs.timeConsulting,
            idTransaction = idTransaction,
            fullNamePatient = navArgs.fullNamePatient,
            fullNameDoctor = navArgs.fullNameDoctor,
            speciality = navArgs.speciality,
            photoUrl = navArgs.photoUrl,
            recordingDate = asDate(),
            statusConsulting = "active",
            phoneNumber = navArgs.phoneNumber,
            photoUrlPatient = navArgs.photoUrlPatient
        )
        val dateTime = "${navArgs.dateConsulting} ${navArgs.timeConsulting}"
        val ref = DB.reference.child("cost_of_consulting").child(idTransaction)
        val refPatientConsulting = DB.reference.child("main_consulting_patients")
            .child(navArgs.idPatient)
            .child(dateTime)
        val refDoctorConsulting = DB.reference.child("main_consulting_doctors")
            .child(navArgs.idDoctor)
            .child(dateTime)

        lifecycleScope.launch(Dispatchers.IO) {
            ref.setValue(transactionData)
            createConsultingData(dateTime)
            refPatientConsulting.setValue(commonPatientData)
            refDoctorConsulting.setValue(commonPatientData)
            sendNotificationData()
            withContext(Dispatchers.Main) {
                binding.constraintLayout.isVisible = false
                binding.progressBar.isVisible = true
                delay(2000)
                binding.progressBar.isGone = true
                val builder = AlertDialog.Builder(requireContext())
                builder.setTitle("Запись успешна!")
                    .setMessage("Если планы изменятся, то отмените запись, при отмене записи мы вам вернем ваши денги в течении дня.")
                    .setIcon(R.drawable.ic_double_check_black_24)
                    .setPositiveButton("Все понятно") { _, _ ->
                        findNavController().apply {
                            popBackStack(R.id.navigation_doctors_fragment, true)
                            popBackStack(R.id.navigation_doctors_data_fragment, true)
                            popBackStack(R.id.selectDateConsultingFragment, true)
                            popBackStack(R.id.confirmConsultingDateFragment, true)
                            popBackStack(R.id.paymentConsultingFragment, true)
                            navigate(R.id.consultationInfoFragment)
                        }
                    }
                builder.create()
                builder.show()
            }
        }

    }

    fun asDate(): String {
        val time = Date()
        val dateFormat = SimpleDateFormat("dd.MM.yyy HH:mm", Locale.US)
        return dateFormat.format(time)
    }

    private fun createConsultingData(dateTime: String) {
        val refConsulting =
            DB.reference.child("consulting")
                .child(navArgs.idDoctor)
                .child(dateTime)
        val patientInfo = SelectDateConsultingFragment.ConsultingData(
            navArgs.timeConsulting,
            navArgs.fullNamePatient,
            navArgs.idNotification,
            navArgs.idPatient,
            "active"
        )
        refConsulting.setValue(patientInfo)
    }

    private fun sendNotificationData() {
        val idNotify = (navArgs.idNotification.toLong() - 999900000000).toInt()
        lifecycleScope.launch(Dispatchers.IO) {
            val titleName = "${navArgs.fullNamePatient} вам в онлайн консультацию записан(-а)"
            val notifyData = NotifyData(
                fromId = navArgs.idPatient,
                title = titleName,
                icon = 2,
                body = "${navArgs.dateConsulting}, ${navArgs.timeConsulting}",
                idNotification = navArgs.idNotification.toInt(),
                fromWho = "0",
                toId = navArgs.idDoctor
            )
            val sender = Sender(notifyData, navArgs.tokenDoctor)
            sendNotification(sender)
        }
    }

    private fun sendNotification(notification: Sender) = lifecycleScope.launch(Dispatchers.IO) {
        try {
            val response = RetrofitClient.api.sendNotification(notification)
            if (response.isSuccessful) {
                val data: ResponseData = response.body()!!
                Log.d("TAG", "ResponseBodyLog: $data")
                Log.d("TAG", "Response: $data")
                Log.d("TAG", "Response: ${response.body().toString()}")
            } else {
                Log.e("TAG", response.errorBody().toString())
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("TAG", e.message.toString())
        }
    }

}