package com.example.testmed

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.example.testmed.base.BaseFragment
import com.example.testmed.databinding.ConsultationInfoFragmentBinding
import com.example.testmed.model.CommonPatientData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class ConsultationInfoFragment :
    BaseFragment<ConsultationInfoFragmentBinding>(ConsultationInfoFragmentBinding::inflate) {
    private lateinit var adapter: ConsultingAdapter
    private lateinit var mRecyclerView: RecyclerView
    private val refPatientConsulting = DB.reference.child("main_consulting_patients").child(UID())
    private lateinit var valueEventListener: ValueEventListener
    private val list = mutableListOf<CommonPatientData>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setAdapter()
        setConsultingDate()
        if (list.isEmpty()) {
            binding.emptyMessages.isVisible = true
            binding.recyclerView.isVisible = false
        }
    }

    private fun setAdapter() {
        adapter = ConsultingAdapter{
            showDialog(it)
        }
        mRecyclerView = binding.recyclerView
        mRecyclerView.adapter = adapter
    }

    private fun showDialog(patientData: CommonPatientData) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Запись будет удалена")
            .setIcon(R.drawable.ic_baseline_warning_24)
            .setMessage("Продолжить выпольнение операции?")
            .setPositiveButton("Да") { _, _ ->
                removeAllData(patientData)
            }
            .setNegativeButton("Нет"){ dialog, _ ->
                dialog.cancel()
            }
        builder.create()
        builder.show()
    }

    private fun removeAllData(patientData: CommonPatientData) {
        val dateTime = "${patientData.date} ${patientData.time}"
        val refCostOfConsulting = DB.reference
            .child("cost_of_consulting")
            .child(patientData.idTransaction)
            .child("statusConsulting")
        val refConsulting = DB.reference
            .child("consulting")
            .child(patientData.idDoctor)
            .child(dateTime)
            .child("statusConsulting")
        val refMainListConsulting = DB.reference
            .child("main_consulting_patients")
            .child(patientData.idPatient)
            .child(dateTime)
            .child("statusConsulting")
        val refMainListConsultingDoc = DB.reference
            .child("main_consulting_doctors")
            .child(patientData.idDoctor)
            .child(dateTime)
            .child("statusConsulting")

        lifecycleScope.launch(Dispatchers.IO) {
            refConsulting.setValue("inactive")
            refCostOfConsulting.setValue("inactive")
            refMainListConsulting.setValue("inactive")
            refMainListConsultingDoc.setValue("inactive")
        }
        list.clear()
        adapter.updateList(list)
    }

    override fun onPause() {
        super.onPause()
        refPatientConsulting.removeEventListener(valueEventListener)
    }

    private fun setConsultingDate() {
        valueEventListener =
            refPatientConsulting.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
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
                            Log.d("CONSUL",
                                dataSnapshot.getValue(CommonPatientData::class.java).toString())
                        }
                    }
                    adapter.updateList(list)
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
    }
}