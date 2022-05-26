package com.example.testmed.patient.speciality.doctors

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import com.example.testmed.DB
import com.example.testmed.R
import com.example.testmed.UID
import com.example.testmed.base.BaseFragment
import com.example.testmed.databinding.CommentsToDoctorFragmentBinding
import com.example.testmed.databinding.FragmentDoctorsDataBinding
import com.example.testmed.doctor.chatwithpatient.ChatAdapterForDoctor
import com.example.testmed.model.CommentsData
import com.example.testmed.model.DoctorData
import com.example.testmed.model.MessageData
import com.example.testmed.model.PatientData
import com.example.testmed.online
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ServerValue
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class CommentsToDoctorFragment :
    BaseFragment<CommentsToDoctorFragmentBinding>(CommentsToDoctorFragmentBinding::inflate) {

    private val args: CommentsToDoctorFragmentArgs by navArgs()
    private lateinit var username: String
    private lateinit var mAdapter: CommentsAdapter
    private lateinit var mRecyclerView: RecyclerView
    private var listComments = mutableListOf<CommentsData>()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUserName()
        sendButtonNotEditable()
        setEditTextListener()
        sendMessage()
        setDoctorsData()
        setDoctorsData()
        setAdapter()
        setComments()
    }

    private fun setComments() {
        val ref = DB.reference
            .child("comments")
            .child(args.idDoctor)
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    snapshot.children.forEach { comments ->
                        val data = comments.getValue(CommentsData::class.java)
                        if (data != null) {
                            listComments.removeIf { listData ->
                                listData.idMessage == data.idMessage
                            }
                            listComments.add(data)
                            mAdapter.updateList(listComments)
                        }
                    }
                }

            }

            override fun onCancelled(error: DatabaseError) = Unit
        })
    }

    private fun setAdapter() {
        mAdapter = CommentsAdapter()
        mRecyclerView = binding.recyclerComments
        mRecyclerView.adapter = mAdapter
    }

    private fun setUserName() {
        lifecycleScope.launch(Dispatchers.IO) {
            val name = if(args.type == "0") "doctors" else "patients"
            username = DB.reference.child(name)
                .child(UID())
                .child("name")
                .get().await().getValue(String::class.java).toString()
        }
    }

    private fun setDoctorsData() {
        lifecycleScope.launch(Dispatchers.IO) {
            DB.reference
                .child("doctors").child(args.idDoctor)
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            val data = snapshot.getValue(DoctorData::class.java)!!
                            lifecycleScope.launch {
                                binding.apply {
                                    val doctorName = "${data.name} ${data.surname}"
                                    fio.text = doctorName
                                    setImage(data.photoUrl, binding.profileImage)
                                }
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) = Unit
                })
        }
    }

    private fun sendMessage() {
        binding.sendButton.setOnClickListener {
            val text = binding.sendMessageEditText.text.toString()
            binding.sendMessageEditText.setText("")
            val idMess = DB.reference.push().key.toString()
            val timestamp: MutableMap<String, String> = ServerValue.TIMESTAMP
            lifecycleScope.launch(Dispatchers.IO) {
                val message = CommentsData(
                    idMessage = idMess,
                    idFrom = UID(),
                    idTo = args.idDoctor,
                    message = text,
                    timestamp = timestamp,
                    username = username
                )
                val refComments = "comments/${args.idDoctor}"

                DB.reference.child(refComments).child(idMess).setValue(message)
            }
        }
    }

    private fun setEditTextListener() {
        sendButtonNotEditable()
        binding.sendMessageEditText.doAfterTextChanged {
            if (it?.length == 0 || it?.trim()!!.isEmpty()) {
                sendButtonNotEditable()
            } else {
                binding.apply {
                    sendButton.isEnabled = true
                }
            }

        }
    }

    private fun sendButtonNotEditable() {
        binding.apply {
            sendButton.isEnabled = false
        }
    }
}