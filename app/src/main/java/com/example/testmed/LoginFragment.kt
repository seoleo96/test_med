package com.example.testmed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
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
            signInWithEmail()
        }
    }

    private fun signInWithEmail() {
        if (binding.etLogin.text.isNotEmpty()) {
            AUTH.signInWithEmailAndPassword(binding.etLogin.text.toString(),
                binding.etPassword.text.toString())
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        findNavController().navigate(R.id.action_navigation_login_to_navigation_home)
                    } else {
                        invisibleProgress(binding.progressBar)
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
                            showSnackbar("Регистрация не удалась. Попробуйте снова.")
                            binding.etLogin.requestFocus()
                        }
                    }
                }
        }else{
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