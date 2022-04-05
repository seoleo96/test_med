package com.example.testmed

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.testmed.base.BaseFragment
import com.example.testmed.databinding.ConfirmConsultingDateFragmentBinding
import com.example.testmed.patient.speciality.consulting.presentation.SelectDateConsultingFragmentDirections

class ConfirmConsultingDateFragment :
    BaseFragment<ConfirmConsultingDateFragmentBinding>
        (ConfirmConsultingDateFragmentBinding::inflate) {
    private val navArgs: ConfirmConsultingDateFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setConsultingData()
        navigateToPayFragment()
    }

    private fun navigateToPayFragment() {
        binding.toConfirm.setOnClickListener {
            val action =
                ConfirmConsultingDateFragmentDirections.actionConfirmConsultingDateFragmentToPaymentConsultingFragment(
                    navArgs.fullNamePatient,
                    navArgs.fullNameDoctor,
                    navArgs.dateConsulting,
                    navArgs.timeConsulting,
                    navArgs.phoneNumberDoctor,
                    navArgs.tokenDoctor,
                    navArgs.idDoctor,
                    navArgs.idPatient,
                    navArgs.idNotification,
                    navArgs.costOfConsulting,
                    navArgs.speciality,
                    navArgs.photoUrl,
                    navArgs.phoneNumber,
                    navArgs.photoUrlPatient,
                    navArgs.idClinic
                )
            findNavController().navigate(action)
        }
    }

    private fun setConsultingData() {
        val date = navArgs.dateConsulting.replace("-", ".")
        val time = "$date ${navArgs.timeConsulting}"
        val fullTextPatient = "Вы хотите записать ${navArgs.fullNamePatient} в онлайн консультацию к врачу"
        binding.apply {
            textViewPatient.text = fullTextPatient
            fullNameDoctor.text = navArgs.fullNameDoctor
            phoneNumber.text = navArgs.phoneNumberDoctor
            dateTime.text = time
        }
    }


}