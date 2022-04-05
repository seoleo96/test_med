package com.example.testmed.patient.auth.login.presentation

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.dropbox.core.android.Auth
import com.example.testmed.*
import com.example.testmed.base.BaseFragment
import com.example.testmed.databinding.FragmentNewPasswordBinding
import com.example.testmed.model.PatientData
import com.example.testmed.patient.auth.registeruser.domain.usecase.UserRegisterState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.lang.Exception

class NewPasswordFragment :
    BaseFragment<FragmentNewPasswordBinding>(FragmentNewPasswordBinding::inflate) {
    private val list = mutableListOf<PatientData>()
    private lateinit var userData : PatientData

    private val args: NewPasswordFragmentArgs by navArgs()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        AUTH().signOut()
        lifecycleScope.launch {
            try {

            }catch (e:Exception){
                val temp: DataSnapshot =
                    DB.reference.child("patients")
                        .orderByChild("phoneNumber")
                        .equalTo(args.phoneNumber)
                        .get().await()
                Log.d("newpassword", temp.toString())
                Log.d("newpasswordkey", temp.key.toString())
                Log.d("newpasswordValue", temp.value.toString())
                temp.children.forEach {
                    userData = it.getValue(PatientData::class.java)!!
                }
                Log.d("newpasswordDATA", userData.toString())
            }
        }

        binding.sendUsersDataButton.setOnClickListener {
            if (binding.etPassword.text.toString().isEmpty()) {
                binding.etPassword.requestFocus()
                invisibleProgress()
                binding.etPassword.error = "Длина пароля должна быть более шести символов."
            } else if (binding.etPassword.text.toString().length < 6) {
                binding.etPassword.requestFocus()
                invisibleProgress()
                binding.etPassword.error = "Длина пароля должна быть более шести символов."
            } else {
                Log.d("newpasswordLOGIN", userData.login)
                Log.d("newpasswordLOGIN", userData.password)
                it.hideKeyboard()
                binding.progressBar.isVisible = true
                binding.allContent.isVisible = false
                val login = userData.login.trim().toString()
                val password = userData.password.trim().toString()
                AUTH().signInWithEmailAndPassword(login, password)
                    .addOnCompleteListener {task ->
                        if (task.isSuccessful) {
                            val currentUser = FirebaseAuth.getInstance().currentUser!!
                            Log.d("newpasswordUSER", currentUser.uid)
                            val newPassword = binding.etPassword.text.toString()
                            currentUser.updatePassword(newPassword).addOnCompleteListener {
                                if (it.isSuccessful) {
                                    DB.reference.child("patients").child(userData.id).child("password")
                                        .setValue(newPassword).addOnCompleteListener {
                                            if (it.isSuccessful) {
                                                showSnackbar("Пароль изменен")
                                                findNavController().navigate(R.id.action_newPasswordFragment_to_navigation_login)
                                            }
                                        }
                                }else{
                                    invisibleProgress()
                                }
                            }
                        }else{
                            invisibleProgress()
//                            showSnackbar("ERROR with sign in")
//                            Log.d("newpasswordUSER", task.exception.toString())
                        }
                    }

            }
        }

    }

    private fun invisibleProgress() {
        binding.progressBar.isVisible = false
        binding.allContent.isVisible = true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        AUTH().signOut()
    }

    private fun patientsData() {
        lifecycleScope.launch {
            val temp: DataSnapshot =
                DB.reference.child("patients")
                    .orderByChild("phoneNumber")
                    .equalTo(args.phoneNumber)
                    .get().await()
            Log.d("newpassword", temp.toString())
            Log.d("newpasswordkey", temp.key.toString())
            Log.d("newpasswordValue", temp.value.toString())
            userData = temp.getValue(PatientData::class.java)!!
            Log.d("newpasswordDATA", userData.toString())

        }
    }

}