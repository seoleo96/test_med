package com.example.testmed.doctor.home.consulting.recommendation

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.testmed.DB
import com.example.testmed.model.PatientData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PatientRecViewModel : ViewModel() {
    // TODO: Implement the ViewModel

    val livedata: MutableLiveData<PatientData> = MutableLiveData()

    fun getPatientsData(idPatient: String) {
        viewModelScope.launch(Dispatchers.IO) {
            DB.reference
                .child("patients").child(idPatient)
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            val data = snapshot.getValue(PatientData::class.java)
                            viewModelScope.launch {
                                livedata.value = data!!
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) = Unit
                })
        }
    }
}