package com.example.testmed.doctor.login

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.testmed.*
import com.example.testmed.base.BaseFragment
import com.example.testmed.base.BaseFragmentDoctor
import com.example.testmed.databinding.FragmentLoginDoctorBinding
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import java.util.*

class LoginDoctorFragment : Fragment(R.layout.fragment_login_doctor) {

    private var _binding: FragmentLoginDoctorBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentLoginDoctorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val testLogin = "terapevt1@gmail.com"
        val testPassword = "123456"
        binding.apply {
            etLogin.setText(testLogin)
            etPassword.setText(testPassword)
        }
        binding.sendUsersDataButton.setOnClickListener {
            requireView().hideKeyboard()
            binding.progressBar.isVisible = true
            binding.allContent.isVisible = false
            signInWithEmail()
        }

    }

    private fun invisibleProgress() {
        binding.progressBar.isVisible = false
        binding.allContent.isVisible = true
    }

    private fun signInWithEmail() {
        if (binding.etLogin.text.isNotEmpty() && binding.etPassword.text.isNotEmpty()) {
            AUTH().signInWithEmailAndPassword(binding.etLogin.text.toString(),
                binding.etPassword.text.toString())
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d("signInWithEmail - Doctor", UID() )
                        findNavController().navigate(R.id.action_navigation_login_doctor_to_navigation_home_doctor)
                    } else {
                        invisibleProgress()
                        try {
                            throw Objects.requireNonNull<Exception>(task.exception)
                        } catch (existEmail: FirebaseAuthUserCollisionException) {
                            showSnackbar("Идентификатор электронной почты уже существует.")
                            binding.etLogin.requestFocus()
                        } catch (weakPassword: FirebaseAuthWeakPasswordException) {
                            showSnackbar("Длина пароля должна быть более шести символов.")
                            binding.etPassword.requestFocus()
                        } catch (malformedEmail: FirebaseAuthInvalidCredentialsException) {
                            showSnackbar("Недействительные учетные данные, попробуйте еще раз.")
                            binding.etLogin.requestFocus()
                        } catch (e: Exception) {
                            showSnackbar("Недействительные учетные данные. Попробуйте снова.")
                            binding.etLogin.requestFocus()
                        }
                    }
                }
        } else {
            binding.etLogin.requestFocus()
        }
    }
}