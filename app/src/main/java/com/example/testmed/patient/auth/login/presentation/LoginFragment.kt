package com.example.testmed.patient.auth.login.presentation

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.testmed.*
import com.example.testmed.base.BaseFragment
import com.example.testmed.databinding.FragmentLoginBinding
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import java.util.*

class LoginFragment : BaseFragment<FragmentLoginBinding>(FragmentLoginBinding::inflate) {

    private val args: LoginFragmentArgs? by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setEditTexts()
        binding.tvToRegister.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_login_to_navigation_register)
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
        if (binding.etLogin.text.isNotEmpty()) {
            AUTH().signInWithEmailAndPassword(binding.etLogin.text.toString(),
                binding.etPassword.text.toString())
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        PHONE_NUMBER = ""
                        findNavController().navigate(R.id.action_navigation_login_to_navigation_home)
                    } else {
                        invisibleProgress()
                        try {
                            throw Objects.requireNonNull<Exception>(task.exception)
                        } catch (existEmail: FirebaseAuthUserCollisionException) {
                            existEmail.printStackTrace()
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

    private fun setEditTexts() {
        if (args?.login != "1") {
            binding.apply {
                etLogin.setText(args?.login)
                etPassword.setText(args?.password)
            }
        }
    }
}