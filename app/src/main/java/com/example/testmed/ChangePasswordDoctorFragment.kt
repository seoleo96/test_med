package com.example.testmed

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
import com.example.testmed.base.BaseFragment
import com.example.testmed.databinding.FragmentChangePasswordDoctorBinding
import kotlinx.coroutines.launch

class ChangePasswordDoctorFragment :
    BaseFragment<FragmentChangePasswordDoctorBinding>(FragmentChangePasswordDoctorBinding::inflate) {

    private val args: ChangePasswordDoctorFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
       binding.sendUsersDataButton.setOnClickListener {
           visibleProgress()
           if (binding.etPassword.text.toString().isEmpty()) {
               binding.etPassword.requestFocus()
               invisibleProgress()
               binding.etPassword.error = "Длина пароля должна быть более шести символов."
           } else if (binding.etPassword.text.toString().length < 6) {
               binding.etPassword.requestFocus()
               invisibleProgress()
               binding.etPassword.error = "Длина пароля должна быть более шести символов."
           } else {
               lifecycleScope.launch {
                   val user = AUTH().currentUser
                   if (user != null){
                       user.updatePassword(binding.etPassword.text.toString()).addOnCompleteListener {
                           if (it.isSuccessful){
                               DB.reference.child("doctors").child(args.id).child("password")
                                   .setValue(binding.etPassword.text.toString()).addOnCompleteListener {
                                       if (it.isSuccessful) {
                                           invisibleProgress()
                                           showSnackbar("Пароль изменен")
                                           findNavController().navigate(R.id.action_changePasswordDoctorFragment_to_navigation_profile_doctor)
                                       }
                                   }
                           }else{
                               showSnackbar("Проверьте интернет подключение.")
                           }
                       }
                   }else{
                       showSnackbar("Что то пошло не так, заново авторизуйтесь.")
                   }
               }
           }
       }
    }

    private fun invisibleProgress() {
        binding.progressBar.isVisible = false
        binding.allContent.isVisible = true
    }
    private fun visibleProgress() {
        binding.progressBar.isVisible = true
        binding.allContent.isVisible = false
    }
}