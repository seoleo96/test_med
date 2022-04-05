package com.example.testmed.patient.auth.login.presentation

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.testmed.*
import com.example.testmed.base.BaseFragment
import com.example.testmed.base.BaseFragmentAuth
import com.example.testmed.databinding.FragmentLoginBinding
import com.example.testmed.model.PatientData
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.database.DataSnapshot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.*

class LoginFragment : BaseFragmentAuth<FragmentLoginBinding>(FragmentLoginBinding::inflate) {

    private val args: LoginFragmentArgs? by navArgs()
    private val list = mutableListOf<PatientData>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setEditTexts()
        binding.tvToRegister.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_login_to_navigation_register)
        }

        setPhoneNumbers()

        binding.apply {
            etLogin.setText("w1@gmail.com")
            etPassword.setText("123456")
        }
        binding.sendUsersDataButton.setOnClickListener {
           it.hideKeyboard()
            binding.progressBar.isVisible = true
            binding.allContent.isVisible = false
            signInWithEmail()
        }

        binding.tvChangePassword.setOnClickListener{
            findNavController().navigate(R.id.action_navigation_login_to_changePasswordFragment)
        }
    }

    private fun setPhoneNumbers() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val temp: DataSnapshot = DB.reference.child("patients")
                    .get().await()
                temp.children.forEach {
                    Log.d("CURRENTUSER", it.toString())
                    val data = it.getValue(PatientData::class.java)
                    if (data != null){
                        list.add(data)
                    }
                }
            }catch (e : java.lang.Exception){

            }
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
                        PHONE_NUMBER = ""
                        findNavController().navigate(R.id.action_navigation_login_to_clinicsFragment)
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