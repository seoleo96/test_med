package com.example.testmed.registeruser.presentation.fragment

import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.core.widget.doOnTextChanged
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.testmed.R
import com.example.testmed.TestMedApp
import com.example.testmed.base.BaseFragment
import com.example.testmed.databinding.FragmentEnterCodeBinding
import com.example.testmed.hideKeyboard
import com.example.testmed.registeruser.domain.UIState
import com.example.testmed.registeruser.domain.usecase.UIValidationState
import com.example.testmed.registeruser.presentation.viewmodel.EnterCodeViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel


class EnterCodeFragment :
    BaseFragment<FragmentEnterCodeBinding>(FragmentEnterCodeBinding::inflate) {

    private val args: EnterCodeFragmentArgs by navArgs()
//    private val enterCodeViewModel: EnterCodeViewModel by viewModel()
    private lateinit var enterCodeViewModel: EnterCodeViewModel
    private var START_MILLI_SECONDS = 60000L
    private lateinit var countdown_timer: CountDownTimer
    private var isRunning: Boolean = false;
    private var time_in_milli_seconds = 0L

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        enterCodeViewModel = (activity?.application as TestMedApp).enterCodeViewModel
        setButtonOptions()
        editTexts()
        sendButtonClick()
    }

    private fun sendButtonClick() {
        binding.buttonSend.setOnClickListener {
            val code = lengthCode()
            enterCodeViewModel.validate(code)
            enterCodeViewModel.uiCodeValidateLiveData.observe(viewLifecycleOwner) { codeValidationState ->
                when (codeValidationState) {
                    is UIValidationState.Loading -> {
                        loading()
                    }

                    is UIValidationState.IsEmpty -> {
                        setButtonOptions()
                    }

                    is UIValidationState.PhoneNumberLess -> {
                        notLoading()
                        binding.errorOtpCode.text = codeValidationState.errorMessage
                        binding.otp1.requestFocus()
                    }

                    else -> {
                        enterCode(code)
                    }
                }
            }
        }
    }

    private fun setButtonOptions() {
        binding.buttonSend.isClickable = false
        binding.buttonSend.isEnabled = false
        binding.buttonSend.background.setTint(Color.parseColor("#F3F2F6"))
        timer()
    }

    private fun timer() {
        countdown_timer = object : CountDownTimer(START_MILLI_SECONDS, 1000) {
            override fun onFinish() {
                updateButtonOptions()
            }

            override fun onTick(p0: Long) {
                time_in_milli_seconds = p0
                if (isRunning) {
                    updateTextUI()
                }
            }
        }
        countdown_timer.start()
        isRunning = true
    }

    private fun updateButtonOptions() {
        binding.buttonSend.isClickable = true
        binding.buttonSend.isEnabled = true
        binding.buttonSend.background.setTint(Color.parseColor("#FF2E58AB"))
        binding.buttonSend.text = "Отправить повторно"
    }

    private fun updateTextUI() {
        val minute = (time_in_milli_seconds / 1000) / 60
        val seconds = (time_in_milli_seconds / 1000) % 60

        binding.buttonSend.text = "Отправить повторно · $minute:$seconds"
    }

    private fun editTexts() {
        binding.apply {
            otp1.apply {
                requestFocus()
                doOnTextChanged { text, _, _, _ ->
                    if (text.toString().trim().isNotEmpty()) {
                        otp2.requestFocus()
                        otp2.isCursorVisible = true
                    }
                }
            }
            otp2.doOnTextChanged { text, _, _, _ ->
                if (text.toString().trim().isNotEmpty()) {
                    otp3.requestFocus()
                    otp3.isCursorVisible = true
                }
            }
            otp3.doOnTextChanged { text, _, _, _ ->
                if (text.toString().trim().isNotEmpty()) {
                    otp4.requestFocus()
                    otp4.isCursorVisible = true
                }
            }
            otp4.doOnTextChanged { text, _, _, _ ->
                if (text.toString().trim().isNotEmpty()) {
                    otp5.requestFocus()
                    otp5.isCursorVisible = true
                }
            }
            otp5.doOnTextChanged { text, _, _, _ ->
                if (text.toString().trim().isNotEmpty()) {
                    otp6.requestFocus()
                    otp6.isCursorVisible = true
                }
            }
            otp6.apply {
                doAfterTextChanged {
                    if (lengthCode().length == 6) {
                        view?.hideKeyboard()
                        binding.buttonSend.isClickable = true
                        binding.buttonSend.isEnabled = true
                        binding.buttonSend.background.setTint(Color.parseColor("#FF2E58AB"))
                        binding.buttonSend.text = "Отправить"
                        pauseTimer()
                    } else {
                        binding.buttonSend.isClickable = false
                        binding.buttonSend.isEnabled = false
                    }
                }
            }
        }
    }

    private fun pauseTimer() {
        countdown_timer.cancel()
        isRunning = false
    }

    private fun lengthCode(): String {
        var code = ""
        binding.apply {
            code =
                otp1.text.toString() + otp2.text.toString() + otp3.text.toString() + otp4.text.toString() + otp5.text.toString() + otp6.text.toString()
        }
        return code
    }

    private fun enterCode(code: String) {
        val id = args.code
        enterCodeViewModel.enterCode(code, id)
        enterCodeViewModel.sendCodeLiveData.observe(viewLifecycleOwner) { uiState ->
            when (uiState) {
                is UIState.Loading -> {
                    loading()
                }

                is UIState.Error -> {
                    notLoading()
                    binding.errorOtpCode.text = uiState.errorMessage
                    binding.errorOtpCode.isVisible = true
                    binding.buttonSend.isVisible = true
                    editTextsNull()
                    binding.otp1.requestFocus()
                    setButtonOptions()
                }

                else -> {
                    pauseTimer()
                    hideContent()
                    val action =
                        findNavController().navigate(R.id.navigation_userDataSignOutFragment)
                }
            }
        }
    }

    private fun editTextsNull() {
        binding.apply {
            otp1.setText("")
            otp2.setText("")
            otp3.setText("")
            otp4.setText("")
            otp5.setText("")
            otp6.setText("")
        }
    }

    private fun loading() {
        binding.progressBar.isVisible = true
        binding.content.isVisible = false
        binding.buttonSend.isVisible = false
    }

    private fun notLoading() {
        binding.progressBar.isVisible = false
        binding.content.isVisible = true
    }

    private fun hideContent() {
        binding.progressBar.isVisible = false
        binding.content.isVisible = false
    }

    override fun onDestroyView() {
        super.onDestroyView()
        pauseTimer()
    }
}