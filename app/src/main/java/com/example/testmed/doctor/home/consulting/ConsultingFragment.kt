package com.example.testmed.doctor.home.consulting

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.core.os.bundleOf
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.setFragmentResult
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.example.testmed.*
import com.example.testmed.base.BaseFragmentDoctor
import com.example.testmed.databinding.ConsultingFragmentBinding
import com.example.testmed.model.AllClinicData
import com.example.testmed.model.CommonPatientData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.*

class ConsultingFragment :
    BaseFragmentDoctor<ConsultingFragmentBinding>(ConsultingFragmentBinding::inflate) {

    private lateinit var adapter: ConsultingDoctorAdapter
    private lateinit var mRecyclerView: RecyclerView
    private val refConsulting = DB.reference.child("main_consulting_doctors").child(UID())
    private lateinit var valueEventListener: ValueEventListener
    private val list = mutableListOf<CommonPatientData>()
    private val searchList = mutableListOf<CommonPatientData>()

    private var tempTime: String = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val time = Date()
        val timeFormat = SimpleDateFormat("MMdd")
        Log.d("TAG", "${timeFormat.format(time)}")
        tempTime = timeFormat.format(time)
        setAdapter()
        setConsultingDate()
        searchPatient()
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
                                list.forEach {
                                    Log.d("TMP", it.toString())
                                }
                                list.removeIf {
                                    val date = manipulateString(it.date)
                                    it.statusConsulting == "inactive" ||
                                            tempTime.toInt() > date.toInt()
                                }
                            }
                        }
                    }
                    if(list.isEmpty()){
                        binding.apply {
                            recyclerView.isVisible = false
                            textview.isVisible = false
                            emptyMessages.isVisible = true
                        }
                    }else{
                        binding.apply {
                            recyclerView.isVisible = true
                            textview.isVisible = true
                            emptyMessages.isVisible = false
                        }
                    }
                    adapter.updateList(list)
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
    }

    private fun searchPatient() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                binding.searchView.clearFocus()
                if (query?.length!! > 2) {
                    list.forEach {
                        if (!searchList.contains(it)) {
                            if (it.fullNamePatient.contains(query.toString())) {
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
                            if (it.fullNamePatient.contains(newText.toString(), ignoreCase = true)) {
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

    fun manipulateString(str: String): String {
        val zero = str.replace("-", "")
        val first = zero.substring(0, str.length - 6)
        val sec = first.substring(0, 2)
        val sec1 = first.substring(2, first.length)
        return "$sec1$sec"
    }

    private fun setAdapter() {
        adapter = ConsultingDoctorAdapter {
            setFragmentResult("requestKeyConsulting", bundleOf("key" to it.idPatient))
        }
        mRecyclerView = binding.recyclerView
        mRecyclerView.adapter = adapter
        mRecyclerView.addItemDecoration(DividerItemDecoration(mRecyclerView.context,
            DividerItemDecoration.VERTICAL))
    }
}