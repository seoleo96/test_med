package com.example.testmed

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.SearchView
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.example.testmed.base.BaseFragment
import com.example.testmed.databinding.ConsultationInfoFragmentBinding
import com.example.testmed.model.CommonPatientData
import com.example.testmed.model.DoctorData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception


class ConsultationInfoFragment :
    BaseFragment<ConsultationInfoFragmentBinding>(ConsultationInfoFragmentBinding::inflate) {
    private lateinit var adapter: ConsultingAdapter
    private lateinit var mRecyclerView: RecyclerView
    private val refPatientConsulting = DB.reference.child("main_consulting_patients").child(UID())
    private lateinit var valueEventListener: ValueEventListener
    private val list = mutableListOf<CommonPatientData>()
    private val searchList = mutableListOf<CommonPatientData>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setAdapter()
        setConsultingDate()
        searchDoctor()
        if (list.isEmpty()) {
            binding.emptyMessages.isVisible = true
            binding.recyclerView.isVisible = false
        }
    }

    private fun setAdapter() {
        adapter = ConsultingAdapter {data, type ->
            if (type == 0){
                showDialog(data)
            }else{
                showDialogForConfirm(data)
            }
        }
        mRecyclerView = binding.recyclerView
        mRecyclerView.adapter = adapter
        mRecyclerView.addItemDecoration(DividerItemDecoration(mRecyclerView.context,
            DividerItemDecoration.VERTICAL))
    }

    private fun showDialogForConfirm(patientData: CommonPatientData) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Подтвердите, что получили консультацию?")
            .setIcon(R.drawable.ic_baseline_warning_24)
            .setMessage("Продолжить выпольнение операции?")
            .setPositiveButton("Да") { _, _ ->
                changeAllData(patientData)
            }
            .setNegativeButton("Нет") { dialog, _ ->
                dialog.cancel()
            }
        builder.create()
        builder.show()
    }

    private fun changeAllData(patientData: CommonPatientData) {
        val dateTime = "${patientData.date} ${patientData.time}"
        val refCostOfConsulting = DB.reference
            .child("cost_of_consulting")
            .child(patientData.idTransaction)
            .child("confirmation")
        val refMainListConsulting = DB.reference
            .child("main_consulting_patients")
            .child(patientData.idPatient)
            .child(dateTime)
            .child("confirmation")
        val refMainListConsultingDoc = DB.reference
            .child("main_consulting_doctors")
            .child(patientData.idDoctor)
            .child(dateTime)
            .child("confirmation")

        lifecycleScope.launch(Dispatchers.IO) {
            refCostOfConsulting.setValue("1")
            refMainListConsulting.setValue("1")
            refMainListConsultingDoc.setValue("1")
        }
        list.clear()
        adapter.updateList(list)
    }

    private fun showDialog(patientData: CommonPatientData) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Запись будет удалена")
            .setIcon(R.drawable.ic_baseline_warning_24)
            .setMessage("Продолжить выпольнение операции?")
            .setPositiveButton("Да") { _, _ ->
                removeAllData(patientData)
            }
            .setNegativeButton("Нет") { dialog, _ ->
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
                               try {
                                   binding.recyclerView.isVisible = true
                                   binding.emptyMessages.isGone = true
                                   list.add(data)
                                   list.removeIf {
                                       it.statusConsulting == "inactive"
                                   }
                               }catch (e : Exception){

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

    private fun searchDoctor() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                binding.searchView.clearFocus()
                if (query?.length!! > 2) {
                    list.forEach {
                        if (!searchList.contains(it)) {
                            if (it.fullNameDoctor.contains(query.toString())) {
                                searchList.add(it)
                            }
                        }
                    }
                    adapter.updateList(searchList)
                } else {
                    adapter.updateList(list)
                    searchList.clear()
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText?.length!! > 2) {
                    list.forEach {
                        if (!searchList.contains(it)) {
                            if (it.fullNameDoctor.contains(newText.toString(), ignoreCase = true)) {
                                searchList.add(it)
                            }
                        }
                    }
                    adapter.updateList(searchList)
                } else {
                    adapter.updateList(list)
                    searchList.clear()
                }
                return false
            }


        })
    }
}