package com.example.testmed.registeruser.presentation.fragment

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.core.widget.doAfterTextChanged
import androidx.navigation.fragment.findNavController
import com.example.testmed.*
import com.example.testmed.base.BaseFragment
import com.example.testmed.databinding.FragmentUserDataSignOutBinding
import com.example.testmed.model.PatientData
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import java.util.*


class UserDataSignOutFragment :
    BaseFragment<FragmentUserDataSignOutBinding>(FragmentUserDataSignOutBinding::inflate) {

    private lateinit var spinner: Spinner
    private var personNames = emptyArray<String>()
    private var birthday = ""
    private var gender = ""
    private var cal = Calendar.getInstance()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setSpinnerGender()
        setDatePicker()
        listeners()
    }

    private fun setDatePicker() {
        binding.etBirthday.setOnClickListener {
            val y = cal.get(Calendar.YEAR)
            val m = cal.get(Calendar.MONTH)
            val d = cal.get(Calendar.DAY_OF_MONTH)
            val datePickerDialog =
                DatePickerDialog(requireContext(), { _, year, monthOfYear, dayOfMonth ->
                    cal.set(Calendar.YEAR, year)
                    cal.set(Calendar.MONTH, monthOfYear)
                    cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    cal.get(Calendar.YEAR)
                    cal.get(Calendar.MONTH)
                    cal.get(Calendar.DAY_OF_MONTH)
                    birthday = "$dayOfMonth.${monthOfYear}.$year"
                    val age = getAge(year, monthOfYear, dayOfMonth)
                    if (checkAge(age)) {
                        binding.etBirthday.setText(birthday)
                    } else {
                        binding.etBirthday.error = "Пациенту нет 18."
                    }

                }, y, m, d)

            datePickerDialog.show()
        }
    }

    private fun checkAge(age: Int): Boolean {
        if (age >= 18)
            return true
        return false
    }

    private fun getAge(year: Int, month: Int, day: Int): Int {
        val dob: Calendar = Calendar.getInstance()
        val today = Calendar.getInstance()
        dob[year, month] = day
        var age = today[Calendar.YEAR] - dob[Calendar.YEAR]
        if (today[Calendar.DAY_OF_YEAR] < dob[Calendar.DAY_OF_YEAR]) {
            age--
        }
        return age
    }

    private fun setSpinnerGender() {
        spinner = binding.etGender
        personNames = arrayOf("Мужской", "Женской")
        val arrayAdapter =
            ArrayAdapter(requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                personNames)
        spinner.adapter = arrayAdapter
        setGender()
    }

    private fun listeners() {
        binding.sendUsersDataButton.setOnClickListener {
            usersData()
        }
//        binding.selectPhoto.setOnClickListener {
//            val currentUser = AUTH.currentUser!!.uid
//            requireView().showSnackbar(currentUser)
//        }

//        binding.selectPhoto.setOnLongClickListener {
//            val email = "seoleo96@gmail.com"
//            val password = "123456"
//            AUTH.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
//                if (task.isSuccessful) {
//                    val id = AUTH.currentUser!!.uid
//                    requireView().showSnackbar(id)
//                    lifecycleScope.launch { delay(1000) }
//
//                    DB.getReference("/patients/$id")
//                        .addValueEventListener(object : ValueEventListener {
//                            override fun onDataChange(snapshot: DataSnapshot) {
//
//                                val number = snapshot.getValue(PatientData::class.java)
//                                requireView().showSnackbar(number.toString())
//                                DB.reference.child("current_user").setValue(number)
//                            }
//
//                            override fun onCancelled(error: DatabaseError) {
//                                TODO("Not yet implemented")
//                            }
//                        })
//                }
//            }
//            true
//        }
    }


    private fun usersData() {
        visibleProgress(binding.progressBar)
        binding.apply {
            if (etIin.text.isEmpty() && etBirthday.text.isEmpty() && etCity.text.isEmpty() &&
                etFio.text.isEmpty() && etLogin.text.isEmpty() &&
                etPassword.text.isEmpty() && etPatronymic.text.isEmpty() && etSurname.text.isEmpty()
            ) {
                invisibleProgress(binding.progressBar)
                showSnackbar("Запольните всех полей")
                etIin.requestFocus()
            } else if (etIin.text.isEmpty()) {
                invisibleProgress(binding.progressBar)
                errorEditsTexts(etIin)
            } else if (etIin.text.length < 12) {
                invisibleProgress(binding.progressBar)
                etIin.error = "ИИН 12 цифр, попробуйте еще раз"
            } else if (etFio.text.isEmpty()) {
                invisibleProgress(binding.progressBar)
                errorEditsTexts(etFio)
            } else if (etSurname.text.isEmpty()) {
                invisibleProgress(binding.progressBar)
                errorEditsTexts(etSurname)
            } else if (etPatronymic.text.isEmpty()) {
                invisibleProgress(binding.progressBar)
                errorEditsTexts(etPatronymic)
            } else if (etCity.text.isEmpty()) {
                invisibleProgress(binding.progressBar)
                errorEditsTexts(etCity)
            } else if (etBirthday.text.isEmpty()) {
                invisibleProgress(binding.progressBar)
                errorEditsTexts(etBirthday)
            } else if (etPhoneNumber.text.isEmpty()) {
                invisibleProgress(binding.progressBar)
                errorEditsTexts(etPhoneNumber)
            } else if (etPhoneNumber.text.length < 12) {
                invisibleProgress(binding.progressBar)
                etPhoneNumber.error = "Номер телефона не соответсвует, попробуйте еще раз"
            } else if (etLogin.text.isEmpty()) {
                invisibleProgress(binding.progressBar)
                errorEditsTexts(etLogin)
            } else if (etPassword.text.isEmpty()) {
                invisibleProgress(binding.progressBar)
                errorEditsTexts(etPassword)
            } else {
                requireView().hideKeyboard()
                createPatientWithEmail(etLogin.text.toString(),
                    etPassword.text.toString())
            }
        }
    }

    private fun createPatientWithEmail(login: String, password: String) {
        AUTH.createUserWithEmailAndPassword(login, password).addOnCompleteListener { value ->
            if (value.isSuccessful) {
                saveUsersData()
            } else {
                invisibleProgress(binding.progressBar)
                try {
                    throw Objects.requireNonNull<Exception>(value.exception)
                } catch (existEmail: FirebaseAuthUserCollisionException) {
                    binding.etLogin.error = "Идентификатор электронной почты уже существует."
                    binding.etLogin.requestFocus()
                } catch (weakPassword: FirebaseAuthWeakPasswordException) {
                    binding.etPassword.error = "Длина пароля должна быть более шести символов."
                    binding.etPassword.requestFocus()
                } catch (malformedEmail: FirebaseAuthInvalidCredentialsException) {
                    binding.etLogin.requestFocus()
                    binding.etLogin.error = "Недействительные учетные данные, попробуйте еще раз."
                } catch (e: Exception) {
                    binding.etIin.requestFocus()
                    binding.etIin.error = "Регистрация не удалась. Попробуйте снова."
                }
            }
        }
    }

    private fun saveUsersData() {
        val id = AUTH.currentUser!!.uid
        val iin = binding.etIin.text.toString()
        val name = binding.etFio.text.toString()
        val surname = binding.etSurname.text.toString()
        val patronymic = binding.etPatronymic.text.toString()
        val address = binding.etCity.text.toString()
        val login = binding.etLogin.text.toString()
        val password = binding.etPassword.text.toString()
        PHONE_NUMBER =
            if (PHONE_NUMBER.isEmpty()) binding.etPhoneNumber.text.toString() else PHONE_NUMBER
        val photoUrl = ""
        val patient = PatientData(
            id,
            iin,
            name,
            surname,
            patronymic,
            address,
            birthday,
            gender,
            login,
            password,
            PHONE_NUMBER,
            photoUrl)
        DB.reference.child("patients").child(id).setValue(patient).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                invisibleProgress(binding.progressBar)
                val action =
                    UserDataSignOutFragmentDirections
                        .actionNavigationUserDataSignOutFragmentToNavigationLogin()
                action.login = login
                action.password = password
                findNavController().navigate(action)
            }
        }
    }


    private fun setGender() {

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                gender = personNames[p2]
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }
    }

    private fun errorEditsTexts(et: EditText) {
        et.apply {
            requestFocus()
            error = "Это обезятельное поле"
        }
    }
}


