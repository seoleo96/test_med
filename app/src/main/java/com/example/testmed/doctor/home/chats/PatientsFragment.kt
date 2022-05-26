package com.example.testmed.doctor.home.chats

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.example.testmed.UID
import com.example.testmed.base.*
import com.example.testmed.databinding.ChatsWithPatientFragmentBinding
import com.example.testmed.doctor.home.PatientsAdapter
import com.example.testmed.doctor.home.chats.data.ReceivingPatientsDataRepository
import com.example.testmed.model.CommonPatientData
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener


class PatientsFragment
    :
    BaseFragmentDoctor<ChatsWithPatientFragmentBinding>(ChatsWithPatientFragmentBinding::inflate) {
    private lateinit var adapter: PatientsAdapter
    private lateinit var viewModel: PatientViewModel
    private lateinit var mRecyclerView: RecyclerView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val repo = ReceivingPatientsDataRepository()
        viewModel = PatientViewModel(repo)
        Log.d("uid", UID())
        setAdapter()
        setPatients()
    }


    fun seenMessages(data: List<CommonPatientData>) {
//        data.forEach { allPatients ->
//            var notReadMessage = 0
//            lifecycleScope.launch(Dispatchers.IO) {
//                refDoctorMEssages = DB.reference.child("message")
//                    .child(UID())
//                    .child(allPatients.id)
//                valueEventListener =
//                    refDoctorMEssages.addValueEventListener(object : ValueEventListener {
//                        override fun onDataChange(snapshot: DataSnapshot) {
//                            if (snapshot.exists()) {
//                                snapshot.children.forEach { messages ->
//                                    val data = messages.getValue(MessageData::class.java)!!
//                                    if (data.seen == "0") {
//                                       notReadMessage++
//                                    }
//                                }
//                                lifecycleScope.launch {
//                                    Log.d("COUNT", "COUNT--$notReadMessage")
//                                    notReadMessage = 0
//                                }
//                            }
//                        }
//                        override fun onCancelled(error: DatabaseError) = Unit
//                    })
//            }
//        }
    }


    private fun setPatients() {
        viewModel.usersFlow.observe(viewLifecycleOwner, Observer { data ->
            when (data) {
                is ReceivingUsersResult.Loading -> {
                    binding.apply {
                        progressBar.isVisible = true
                        textview.isVisible = false
                        recyclerView.isVisible = false
                    }
                }

                is ReceivingUsersResult.Error -> {
                    binding.apply {
                        progressBar.isVisible = false
                        textview.isVisible = true
                        textview.text = data.errorMessage
                        recyclerView.isVisible = false
                    }
                }

                is ReceivingUsersResult.Empty -> {
                    binding.apply {
                        progressBar.isVisible = false
                        textview.isVisible = true
                        textview.text = "Нет сообщений."
                        adapter.clearList()
                        recyclerView.isVisible = false
                    }
                }

                is ReceivingUsersResult.SuccessList -> {
                    adapter.updateList(data.data)
//                    data.data.forEach {
//                        Log.d("MAP", it.toString())
//                    }
                    binding.apply {
                        progressBar.isVisible = false
                        textview.isVisible = true
                        textview.text = "Пациенты"
                        recyclerView.isVisible = true
                    }
                }
            }
        })
    }

    private fun setAdapter() {
        adapter = PatientsAdapter {
            setFragmentResult("requestKey", bundleOf("bundleKey" to it.id))
        }
        mRecyclerView = binding.recyclerView
        mRecyclerView.adapter = adapter
        mRecyclerView.addItemDecoration(DividerItemDecoration(mRecyclerView.context,
            DividerItemDecoration.VERTICAL))
    }
}