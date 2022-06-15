package com.example.testmed.doctor.home.consulting.recommendation

import android.content.Intent
import android.net.Uri
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import com.example.testmed.DB
import com.example.testmed.R
import com.example.testmed.UID
import com.example.testmed.asTimeStatus
import com.example.testmed.base.BaseFragmentDoctor
import com.example.testmed.databinding.PatientRecFragmentBinding
import com.example.testmed.doctor.chatwithpatient.ChatAdapterForDoctor
import com.example.testmed.model.MessageData
import com.example.testmed.model.RecommendationData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception

class PatientRecFragment :
    BaseFragmentDoctor<PatientRecFragmentBinding>(PatientRecFragmentBinding::inflate) {

    private lateinit var viewModel: PatientRecViewModel
    private lateinit var mAdapter: RecPatientAdapter
    private lateinit var mRecyclerView: RecyclerView
    private var listRec = mutableListOf<RecommendationData>()
    private val args: PatientRecFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[PatientRecViewModel::class.java]
        setAdapter()
        setMessages()
        setPatientsData()
        binding.toBackButton.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.toChat.setOnClickListener {
            val action =
                PatientRecFragmentDirections.actionPatientRecFragmentToNavigationChatWithPatientFragment()
            action.id = args.idPatient
            findNavController().navigate(action)
        }
    }

    private fun setPatientsData() {
        viewModel.getPatientsData(args.idPatient)
        viewModel.livedata.observe(viewLifecycleOwner) {
            it?.apply {
                binding.fio.text = "$surname $name"
                super.setImage(photoUrl, binding.profileImage)
            }
        }
    }

    private fun setAdapter() {
        mAdapter = RecPatientAdapter { recUrl ->
            viewPdf(recUrl)
        }
        mRecyclerView = binding.recyclerChat
        mRecyclerView.adapter = mAdapter
    }

    private fun setMessages() {
        val ref = DB.reference
            .child("recommendation")
            .child(args.idPatient)
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    snapshot.children.forEach { rec ->
                        val data = rec.getValue(RecommendationData::class.java)
                        if (data != null) {
                            listRec.removeIf { listData ->
                                listData.idRecommendation == data.idRecommendation
                            }
                            listRec.add(data)
                            mAdapter.updateList(listRec)
                        }
                    }
                }

            }

            override fun onCancelled(error: DatabaseError) = Unit
        })
    }

    private fun viewPdf(recUrl: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(recUrl))
        startActivity(intent)
    }

}