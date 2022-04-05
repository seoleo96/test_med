package com.example.testmed.doctor.home.archive

import android.app.DatePickerDialog
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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.testmed.DB
import com.example.testmed.R
import com.example.testmed.UID
import com.example.testmed.base.BaseFragmentDoctor
import com.example.testmed.databinding.ArchiveFragmentBinding
import com.example.testmed.doctor.home.consulting.ConsultingDoctorAdapter
import com.example.testmed.model.CommonPatientData
import com.example.testmed.showSnackbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import java.util.*

class ArchiveFragment : BaseFragmentDoctor<ArchiveFragmentBinding>(ArchiveFragmentBinding::inflate) {

    private lateinit var adapter: ArchiveAdapter
    private lateinit var mRecyclerView: RecyclerView
    private val refConsulting = DB.reference.child("main_consulting_doctors").child(UID())
    private lateinit var valueEventListener: ValueEventListener
    private val list = mutableListOf<CommonPatientData>()
    private val searchList = mutableListOf<CommonPatientData>()
    private val filter = mutableListOf<CommonPatientData>()
    private var cal = Calendar.getInstance()
    private var birthday = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setAdapter()
        setConsultingDate()
        searchPatient()
            setDatePicker()

    }

    private fun setDatePicker() {
        binding.floatingActionButton.setOnClickListener {

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
                    val mont = 1+monthOfYear
                    val month = if (mont.toString().length == 2) mont else "0$mont"
                    birthday = "$dayOfMonth-${month}-$year"
                    setFilteredData(birthday)

                }, y, m, d)

            datePickerDialog.show()
        }
    }

    private fun setFilteredData(birthday: String) {
        Log.d("birthday", birthday)
        if (birthday.isNotEmpty()) {
            list.forEach {
                if (!filter.contains(it)) {
                    if (it.date == birthday) {
                        filter.add(it)
                    }
                }
            }
            adapter.updateList(filter)
        } else {
            adapter.updateList(list)
            filter.clear()
        }
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
                                try {
                                    binding.recyclerView.isVisible = true
                                    binding.emptyMessages.isGone = true
                                }catch (e : Exception){

                                }
                                list.add(data)
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

    private fun setAdapter() {
        adapter = ArchiveAdapter {
            setFragmentResult("requestKeyConsulting", bundleOf("key" to it.idPatient))
        }
        mRecyclerView = binding.recyclerView
        mRecyclerView.adapter = adapter
        mRecyclerView.addItemDecoration(DividerItemDecoration(mRecyclerView.context,
            DividerItemDecoration.VERTICAL))

        mRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val positionView = (mRecyclerView.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()

                if (positionView > 0) {
                    if(!binding.floatingActionButton.isShown) {
                        binding.floatingActionButton.show();
                    }
                } else  {
                    if(binding.floatingActionButton.isShown) {
                        binding.floatingActionButton.hide();
                    }
                }
            }
        })
    }

}