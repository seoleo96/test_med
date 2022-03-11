package com.example.testmed.patient.profile.changepatientdata

import android.app.Activity
import android.app.DatePickerDialog
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.testmed.*
import com.example.testmed.base.BaseFragment
import com.example.testmed.databinding.FragmentChangePatientDataBinding
import com.example.testmed.model.PatientData
import com.example.testmed.patient.auth.registeruser.domain.usecase.UserRegisterState
import com.example.testmed.patient.auth.registeruser.domain.usecase.UserRegistrationValidate
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuth.AuthStateListener
import com.google.firebase.auth.FirebaseAuth.getInstance
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*


class ChangePatientDataFragment :
    BaseFragment<FragmentChangePatientDataBinding>(FragmentChangePatientDataBinding::inflate) {
    private lateinit var valueEventListener: ValueEventListener
    private lateinit var currentUser: FirebaseUser
    private lateinit var authStateListener: AuthStateListener
    private lateinit var auth: FirebaseAuth
    private lateinit var rdbRef: DatabaseReference
    private lateinit var viewModel: ChangePatientDataViewModel
    private var cal = Calendar.getInstance()
    private var birthday = ""
    private var uri: Uri? = null
    private lateinit var gender: String
    private lateinit var photoUrl: String
    private lateinit var login: String
    private lateinit var loginIsSame: String
    private lateinit var password: String


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val userRegistrationValidate = UserRegistrationValidate()
        viewModel = ChangePatientDataViewModel(userRegistrationValidate)
        auth = getInstance()
        authStateListener = AuthStateListener { firebaseAuth ->
            currentUser = firebaseAuth.currentUser!!
        }
        authStateListener.onAuthStateChanged(auth)
        setProfileData()
        saveData()
        setDatePicker()
        changePhotoUser()
        navigateBack()
    }

    override fun onResume() {
        super.onResume()
        authStateListener.onAuthStateChanged(auth)
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

    private fun navigateBack() {
        binding.toBackButton.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun saveData() {
        binding.saveUsersData.setOnClickListener {
            requireView().hideKeyboard()
            validateUserData()
        }
    }

    private fun validateUserData() {
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
                    lifecycleScope.launch(Dispatchers.Main) {
                        updateUserData(binding.etLogin.text.toString(),
                            binding.etPassword.text.toString())
                    }
                }
            }
        }
    }

    private fun updateUserData(login: String, password: String) {
        currentUser.updatePassword(password)
        currentUser.updateEmail(login).addOnCompleteListener {
            if (it.isSuccessful) {
                try {
                    saveUsersData(login, password)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else {
                try {
                    throw it.exception!!
                } catch (existEmail: FirebaseAuthUserCollisionException) {
                    invisibleProgress(binding.progressBar)
                    binding.etLogin.error =
                        "Идентификатор электронной почты уже существует."
                    binding.etLogin.requestFocus()
                }
            }
        }
    }

    private fun saveUsersData(
        login: String,
        password: String,
    ) {
        val id = currentUser.uid
        val iin = binding.etIin.text.toString()
        val name = binding.etFio.text.toString()
        val surname = binding.etSurname.text.toString()
        val patronymic = binding.etPatronymic.text.toString()
        val address = binding.etCity.text.toString()
        PHONE_NUMBER = binding.etPhoneNumber.text.toString()
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
        DB.reference.child("patients").child(id).setValue(patient)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    if (uri == null) {
                        navigateToProfile()
                    } else {
                        savePhoto()
                    }
                }
            }
    }

    private fun navigateToProfile() {
        showSnackbar("Профиль обновлен.")
        findNavController().navigate(R.id.action_navigation_change_patient_data_to_navigation_profile)
    }

    private fun savePhoto() {
        val path =
            REF_STORAGE_ROOT.child("images").child(DB.reference.push().key.toString())
        path.putFile(uri!!)
            .addOnCompleteListener { task1 ->
                if (task1.isSuccessful) {
                    path.downloadUrl.addOnCompleteListener { task2 ->
                        if (task2.isSuccessful) {
                            val photoUrl = task2.result.toString()
                            DB.getReference("/patients/${currentUser.uid}/photoUrl")
                                .setValue(photoUrl)
                                .addOnCompleteListener {
                                    if (it.isSuccessful) {
                                        navigateToProfile()
                                    } else {
                                        tryOne()
                                    }
                                }
                        } else {
                            tryOne()
                        }
                    }
                } else {
                    tryOne()
                }
            }
    }

    private fun tryOne() {
        Snackbar.make(binding.root,
            "Попробуйте еще раз.",
            Snackbar.LENGTH_SHORT).show()
    }


    private fun invisibleProgress(progressBar: ProgressBar) {
        progressBar.isVisible = false
        visibleAllViews()
    }

    private fun invisibleAllViews() {
        binding.contentAllView.isVisible = false
    }

    private fun visibleAllViews() {
        binding.contentAllView.isVisible = true
    }

    private fun errorEditsTexts(et: EditText) {
        et.apply {
            requestFocus()
            error = "Это обезятельное поле"
        }
    }

    private fun setProfileData() {
        rdbRef = DB.reference
            .child("patients")
            .child(currentUser.uid)
        valueEventListener = rdbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val data = snapshot.getValue(PatientData::class.java)
                    binding.apply {
                        if (data != null) {
                            gender = data.gender
                            etIin.setText(data.iin)
                            etFio.setText(data.name)
                            etSurname.setText(data.surname)
                            etPatronymic.setText(data.patronymic)
                            etBirthday.setText(data.birthday)
                            birthday = data.birthday
                            etCity.setText(data.address)
                            etLogin.setText(data.login)
                            etPassword.setText(data.password)
                            password = data.password
                            login = data.login
                            loginIsSame = data.login
                            etPhoneNumber.setText(data.phoneNumber)
                            photoUrl = data.photoUrl ?: ""
                            if (data.photoUrl?.isNotEmpty() == true) {
                                Glide
                                    .with(requireContext())
                                    .load(data.photoUrl)
                                    .centerCrop()
                                    .into(selectPhoto)
                            }
                        }
                    }
                } else {
                    findNavController().navigate(R.id.navigation_login)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
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
                    val age: Int = getAge(year, monthOfYear, dayOfMonth)
                    if (checkAge(age)) {
                        binding.etBirthday.setText(birthday)
                    } else {
                        showSnackbar("Пациенту нет 18.")
                    }
                }, y, m, d)

            datePickerDialog.show()
        }
    }

    private fun checkAge(age: Int) = age >= 18

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

    override fun onPause() {
        super.onPause()
        rdbRef.removeEventListener(valueEventListener)
    }

    override fun onStop() {
        super.onStop()
        auth.removeAuthStateListener(authStateListener)
    }

    override fun onStart() {
        super.onStart()
        auth.addAuthStateListener(authStateListener)
    }


}