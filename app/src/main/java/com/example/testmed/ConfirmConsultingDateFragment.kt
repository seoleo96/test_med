package com.example.testmed

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.example.testmed.base.BaseFragment
import com.example.testmed.databinding.ConfirmConsultingDateFragmentBinding

class ConfirmConsultingDateFragment :
    BaseFragment<ConfirmConsultingDateFragmentBinding>
        (ConfirmConsultingDateFragmentBinding::inflate) {
            private val navArgs : ConfirmConsultingDateFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.textview.text = "${navArgs.dateConsulting} - ${navArgs.timeConsulting} - ${navArgs.fullNameDoctor} - ${navArgs.fullNamePatient}"
    }


}