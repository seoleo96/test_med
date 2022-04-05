package com.example.testmed.patient.auth.registeruser.presentation.fragment

import android.app.Activity
import android.app.DatePickerDialog
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.testmed.*
import com.example.testmed.base.BaseFragment
import com.example.testmed.base.BaseFragmentAuth
import com.example.testmed.databinding.FragmentUserDataSignOutBinding
import com.example.testmed.model.PatientData
import com.example.testmed.patient.auth.registeruser.domain.usecase.UserRegisterState
import com.example.testmed.patient.auth.registeruser.domain.usecase.UserRegistrationValidate
import com.example.testmed.patient.auth.registeruser.presentation.viewmodel.UserDataSignOutViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*


class UserDataSignOutFragment :
    BaseFragmentAuth<FragmentUserDataSignOutBinding>(FragmentUserDataSignOutBinding::inflate) {

    private lateinit var spinner: Spinner
    private var personNames = emptyArray<String>()
    private var birthday = ""
    private var gender = ""
    private var uri: Uri? = null
    private var cal = Calendar.getInstance()
    private lateinit var viewModel: UserDataSignOutViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val userRegistrationValidate = UserRegistrationValidate()
        viewModel = UserDataSignOutViewModel(userRegistrationValidate)
        setSpinnerGender()
        setDatePicker()
        listeners()
        changePhotoUser()
        setPhoneNumber()

    }

    private fun setPhoneNumber() {
        if (PHONE_NUMBER.isNotEmpty()) {
            binding.etPhoneNumber.setText(PHONE_NUMBER)
        }
    }

    private fun changePhotoUser() {
        binding.selectPhoto.setOnClickListener {
            val intent = CropImage.activity()
                .setAspectRatio(1, 1)
                .setRequestedSize(600, 600)
                .setCropShape(CropImageView.CropShape.OVAL)
                .getIntent(activity as MainActivity)
            launchSomeActivity.launch(intent)
        }
    }

    private var launchSomeActivity =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK && result.data != null) {
                uri = CropImage.getActivityResult(result.data).uri
                binding.selectPhoto.background = null
                binding.selectPhoto.setImageURI(uri)
            }
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
                    birthday = "$dayOfMonth.${(1+monthOfYear)}.$year"
                    val age = getAge(year, monthOfYear, dayOfMonth)
                    if (checkAge(age)) {
                        binding.etBirthday.setText(birthday)
                    } else {
                        showSnackbar("Пациенту нет 18.")
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
        binding.saveUsersData.setOnClickListener {
            requireView().hideKeyboard()
            usersData()
        }
    }

    private fun invisibleAllViews() {
        binding.contentAllView.isVisible = false
    }

    private fun visibleAllViews() {
        binding.contentAllView.isVisible = true
    }

    private fun invisibleProgress(progressBar: ProgressBar) {
        progressBar.isVisible = false
        visibleAllViews()
    }

    private fun usersData() {
        visibleProgress(binding.progressBar)
        invisibleAllViews()
        viewModel.validate(
            binding.etIin.text.toString(),
            binding.etBirthday.text.toString(),
            binding.etCity.text.toString(),
            binding.etFio.text.toString(),
            binding.etPhoneNumber.text.toString(),
            binding.etLogin.text.toString(),
            binding.etPassword.text.toString(),
            binding.etPatronymic.text.toString(),
            binding.etSurname.text.toString())
        viewModel.dataValidateLiveData.observe(viewLifecycleOwner) { userRegisterState ->
            when (userRegisterState) {
                is UserRegisterState.AllDataEmpty -> {
                    invisibleProgress(binding.progressBar)
                    showSnackbar(userRegisterState.errorMessage)
                    binding.etIin.requestFocus()
                }
                is UserRegisterState.IinEmpty -> {
                    invisibleProgress(binding.progressBar)
                    errorEditsTexts(binding.etIin)
                }
                is UserRegisterState.IinLengthLess -> {
                    invisibleProgress(binding.progressBar)
                    binding.etIin.error = userRegisterState.errorMessage
                    binding.etIin.requestFocus()
                }
                is UserRegisterState.FIOEmpty -> {
                    invisibleProgress(binding.progressBar)
                    errorEditsTexts(binding.etFio)
                }
                is UserRegisterState.SurnameEmpty -> {
                    invisibleProgress(binding.progressBar)
                    errorEditsTexts(binding.etSurname)
                }
                is UserRegisterState.PatronymicEmpty -> {
                    invisibleProgress(binding.progressBar)
                    errorEditsTexts(binding.etPatronymic)
                }
                is UserRegisterState.CitEmpty -> {
                    invisibleProgress(binding.progressBar)
                    errorEditsTexts(binding.etCity)
                }
                is UserRegisterState.BirthdayEmpty -> {
                    invisibleProgress(binding.progressBar)
                    errorEditsTexts(binding.etBirthday)
                }
                is UserRegisterState.PhoneNumberEmpty -> {
                    invisibleProgress(binding.progressBar)
                    errorEditsTexts(binding.etPhoneNumber)
                }
                is UserRegisterState.PhoneNumberLengthLess -> {
                    invisibleProgress(binding.progressBar)
                    binding.etPhoneNumber.error = userRegisterState.errorMessage
                    binding.etPhoneNumber.requestFocus()
                }
                is UserRegisterState.LoginValidate -> {
                    invisibleProgress(binding.progressBar)
                    binding.etLogin.requestFocus()
                    binding.etLogin.error = userRegisterState.errorMessage
                }
                is UserRegisterState.PasswordEmpty -> {
                    invisibleProgress(binding.progressBar)
                    errorEditsTexts(binding.etPassword)
                }
                is UserRegisterState.PasswordLengthLess -> {
                    invisibleProgress(binding.progressBar)
                    binding.etPassword.requestFocus()
                    binding.etPassword.error = userRegisterState.errorMessage
                }
                is UserRegisterState.Success -> {
                    invisibleAllViews()
                    visibleProgress(binding.progressBar)
                    lifecycleScope.launch(Dispatchers.Main) {
                        requireView().hideKeyboard()
                        createPatientWithEmail(binding.etLogin.text.toString(),
                            binding.etPassword.text.toString())
                    }
                }
            }
        }
    }

    private fun createPatientWithEmail(login: String, password: String) {
        AUTH().createUserWithEmailAndPassword(login, password).addOnCompleteListener { value ->
            if (value.isSuccessful) {
                invisibleAllViews()
                visibleProgress(binding.progressBar)
                saveUsersData()
            } else {
                try {
                    invisibleProgress(binding.progressBar)
                    try {
                        throw value.exception!!
                    } catch (existEmail: FirebaseAuthUserCollisionException) {
                        invisibleProgress(binding.progressBar)
                        binding.etLogin.error =
                            "Идентификатор электронной почты уже существует."
                        binding.etLogin.requestFocus()
                    } catch (e: Exception) {
                        invisibleProgress(binding.progressBar)
                        showSnackbar("Регистрация не удалась. Попробуйте снова.")
                    }
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }

            }
        }
    }

    private fun saveUsersData() {
        val id = AUTH().currentUser!!.uid
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
            photoUrl,
            online)
        DB.reference.child("patients").child(id).setValue(patient).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                if (uri == null) {
                    navigateToLogin(login, password)
                } else {
                    savePhoto(login, password)
                }
            }
        }
    }

    private fun navigateToLogin(login: String, password: String) {
        val action =
            UserDataSignOutFragmentDirections
                .actionNavigationUserDataSignOutFragmentToNavigationLogin()
        action.login = login
        action.password = password
        findNavController().navigate(action)
    }

    private fun savePhoto(login: String, password: String) {
        val path =
            REF_STORAGE_ROOT.child("images").child(DB.reference.push().key.toString())
        path.putFile(uri!!)
            .addOnCompleteListener { task1 ->
                if (task1.isSuccessful) {
                    path.downloadUrl.addOnCompleteListener { task2 ->
                        if (task2.isSuccessful) {
                            val photoUrl = task2.result.toString()
                            DB.getReference("/patients/${AUTH().currentUser?.uid}/photoUrl")
                                .setValue(photoUrl)
                                .addOnCompleteListener {
                                    if (it.isSuccessful) {
                                        navigateToLogin(login, password)
                                    } else {
                                        invisibleProgress(binding.progressBar)
                                        tryOne()
                                    }
                                }
                        } else {
                            invisibleProgress(binding.progressBar)
                            tryOne()
                        }
                    }
                } else {
                    invisibleProgress(binding.progressBar)
                    tryOne()
                }
            }
    }

    private fun tryOne() {
        Snackbar.make(binding.root,
            "Попробуйте еще раз.",
            Snackbar.LENGTH_SHORT).show()
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


