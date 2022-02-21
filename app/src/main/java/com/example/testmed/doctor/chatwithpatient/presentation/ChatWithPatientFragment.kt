package com.example.testmed.doctor.chatwithpatient.presentation

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.testmed.doctor.chatwithpatient.ChatAdapterForDoctor
import com.example.testmed.DB
import com.example.testmed.UID
import com.example.testmed.base.BaseFragment
import com.example.testmed.databinding.ChatWithPatientFragmentBinding
import com.example.testmed.doctor.chatwithpatient.MessagingResult
import com.example.testmed.doctor.chatwithpatient.data.PatientDataResult
import com.example.testmed.doctor.chatwithpatient.data.PatientsDataRepository
import com.example.testmed.model.CommonPatientData
import com.example.testmed.model.MessageData
import com.example.testmed.model.PatientData
import com.example.testmed.showSnackbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ServerValue
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.coroutines.resume

class ChatWithPatientFragment
    : BaseFragment<ChatWithPatientFragmentBinding>(ChatWithPatientFragmentBinding::inflate) {

    private lateinit var viewModel: ChatWithPatientViewModel
    private val args: ChatWithPatientFragmentArgs by navArgs()
    private lateinit var mAdapter: ChatAdapterForDoctor
    private lateinit var mRecyclerView: RecyclerView
    private var listMessages = mutableListOf<MessageData>()
    private var photoUrl = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val repo = PatientsDataRepository()
        viewModel = ChatWithPatientViewModel(repo)
        showSnackbar(args.id)
        setAdapter()
        setMessages()
        setEditTextListener()
        sendMessage()
        setPatientsData()
        setToBackButton()
    }

    private fun setAdapter() {
        mAdapter = ChatAdapterForDoctor()
        mRecyclerView = binding.recyclerChat
        mRecyclerView.adapter = mAdapter
    }

    private fun setMessages() {
        lifecycleScope.launch(Dispatchers.IO) {
            DB.reference
                .child("message")
                .child(UID())
                .child(args.id)
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            snapshot.children.forEach { messages ->
                                val data = messages.getValue(MessageData::class.java)
                                if (data != null) {
                                    listMessages.removeIf { listData ->
                                        listData.idMessage == data.idMessage
                                    }
                                    listMessages.add(data)
                                    mAdapter.updateList(listMessages)
                                    mRecyclerView.smoothScrollToPosition(mAdapter.itemCount)
                                }
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) = Unit
                })
        }
    }

    private fun setEditTextListener() {
        sendButtonNotEditable()
        binding.messageEditText.doAfterTextChanged {
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

    private fun sendMessage() {
        binding.sendButton.setOnClickListener {
            val text = binding.messageEditText.text.toString()
            binding.messageEditText.setText("")
            val idMess = DB.reference.push().key.toString()
            val timestamp: MutableMap<String, String> = ServerValue.TIMESTAMP

            val message = MessageData(
                idMessage = idMess,
                idFrom = UID(),
                idTo = args.id,
                message = text,
                timestamp = timestamp,
                type = "message"
            )
            var refPatient = ""
            var refDoctor = ""
            refPatient = "message/${args.id}/${UID()}"
            refDoctor = "message/${UID()}/${args.id}"
            lifecycleScope.launch(Dispatchers.IO) {
                DB.reference.child(refPatient).child(idMess).setValue(message)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            DB.reference.child(refDoctor)
                                .child(idMess)
                                .setValue(message)
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        saveMainList(text)
                                    }
                                }
                        } else {
                            lifecycleScope.launch {
                                showSnackbar("Попробуйте еще раз.")
                            }
                        }
                    }
            }
        }
    }

    private fun saveMainList(text: String) {
        var refPatient = ""
        var refDoctor = ""
        refPatient = "main_list/${args.id}/${UID()}"
        refDoctor = "main_list/${UID()}/${args.id}"
        val childPatient = CommonPatientData(
            id = UID(),
            photoUrl = photoUrl,
            message = text,
            timestamp = ServerValue.TIMESTAMP,
            type = "message"
        )
        val childDoctor = CommonPatientData(
            id = args.id,
            photoUrl = photoUrl,
            message = text,
            timestamp = ServerValue.TIMESTAMP,
            type = "message"
        )
        lifecycleScope.launch(Dispatchers.IO){
            DB.reference.child(refPatient).setValue(childPatient)
            DB.reference.child(refDoctor).setValue(childDoctor)
        }
    }

    private fun setPatientsData() {
        viewModel.getPatientData(args.id)
        viewModel.patientDataLiveData.observe(viewLifecycleOwner, { data ->
            when (data) {
                is PatientDataResult.NotExist -> findNavController().popBackStack()
                is PatientDataResult.Success -> {
                    data.data.apply {
                        binding.fio.text = "$name $patronymic"
                        this@ChatWithPatientFragment.photoUrl = photoUrl ?: ""
                        if (photoUrl?.isNotEmpty()!!) {
                            Glide
                                .with(requireContext())
                                .load(photoUrl)
                                .centerCrop()
                                .into(binding.profileImage)
                        }
                    }
                }
            }
        })
    }

    private fun setToBackButton() {
        binding.toBackButton.setOnClickListener {
            findNavController().popBackStack()
        }
    }


}