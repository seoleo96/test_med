package com.example.testmed.doctor.home.consulting

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.setFragmentResult
import androidx.recyclerview.widget.RecyclerView
import com.example.testmed.*
import com.example.testmed.base.BaseFragmentDoctor
import com.example.testmed.databinding.ConsultingFragmentBinding
import com.example.testmed.model.CommonPatientData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class ConsultingFragment :
    BaseFragmentDoctor<ConsultingFragmentBinding>(ConsultingFragmentBinding::inflate) {

    private lateinit var adapter: ConsultingDoctorAdapter
    private lateinit var mRecyclerView: RecyclerView
    private val refConsulting = DB.reference.child("main_consulting_doctors").child(UID())
    private lateinit var valueEventListener: ValueEventListener
    private val list = mutableListOf<CommonPatientData>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setAdapter()
        setConsultingDate()
    }

    override fun onPause() {
        super.onPause()
        refConsulting.removeEventListener(valueEventListener)
    }

    private fun setConsultingDate() {
        valueEventListener =
            refConsulting.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        list.clear()
                        snapshot.children.forEach { dataSnapshot ->
                            val data = dataSnapshot.getValue(CommonPatientData::class.java)
                            if (data != null) {
                                binding.recyclerView.isVisible = true
                                binding.emptyMessages.isGone = true
                                list.add(data)
                                list.removeIf {
                                    it.statusConsulting == "inactive"
                                }
                            }
                        }
                    }
                    adapter.updateList(list)
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
    }

    private fun setAdapter() {
        adapter = ConsultingDoctorAdapter{
            setFragmentResult("requestKeyConsulting", bundleOf("key" to it.idPatient))
        }
        mRecyclerView = binding.recyclerView
        mRecyclerView.adapter = adapter
    }
}