package com.example.testmed.patient.chats.chatwithdoctor

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.testmed.*
import com.example.testmed.base.BaseFragment
import com.example.testmed.databinding.FragmentChatWithDoctorBinding
import com.example.testmed.model.CommonPatientData
import com.example.testmed.model.DoctorData
import com.example.testmed.model.MessageData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ServerValue
import com.google.firebase.database.ValueEventListener
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ChatWithDoctorFragment :
    BaseFragment<FragmentChatWithDoctorBinding>(FragmentChatWithDoctorBinding::inflate) {

    private val args: ChatWithDoctorFragmentArgs by navArgs()
    private lateinit var mAdapter: ChatAdapter
    private lateinit var mRecyclerView: RecyclerView
    private var mListMessages = mutableListOf<MessageData>()
    private var doctorName = ""
    private var doctorImageUrl = ""
    private var tempTimestamp = ""
    private var uri: Uri? = null


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setAdapter()
        setToBackButton()
        setDoctorsData()
        setEditTextListener()
        sendMessage()
        setMessages()
        sendImage()
    }

    private fun setMessages() {
        val ref = DB.reference
            .child("message")
            .child(UID())
            .child(args.idDoctor)
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    snapshot.children.forEach {
                        val data = it.getValue(MessageData::class.java)
                        Log.d("TAG", data.toString())
                        if (data != null) {
                            mListMessages.removeIf {
                                it.idMessage == data.idMessage
                            }
                            tempTimestamp = data.timestamp.toString()
                            mListMessages.add(data)
                        }
                    }
                }
                updateAdapter(mListMessages)
            }

            override fun onCancelled(error: DatabaseError) = Unit
        })
    }

    private fun setAdapter() {
        mAdapter = ChatAdapter()
        mRecyclerView = binding.recyclerChat
        mRecyclerView.adapter = mAdapter
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

    private var launchSomeActivity =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK && result.data != null) {
                uri = CropImage.getActivityResult(result.data).uri
                savePhoto()
            }
        }

    private fun sendImage() {
        binding.addMedia.setOnClickListener {
            val intent = CropImage.activity()
                .setAspectRatio(1, 1)
                .setRequestedSize(900, 900)
                .getIntent(activity as MainActivity)
            launchSomeActivity.launch(intent)
        }
    }

    private fun updateAdapter(mListMessages: MutableList<MessageData>) {
        mAdapter.updateList(mListMessages)
        mRecyclerView.smoothScrollToPosition(mAdapter.itemCount)
    }

    private fun savePhoto() {
        lifecycleScope.launch(Dispatchers.IO) {
            val key = DB.reference.child("message").child(UID()).push().key.toString()
            val timestamp = ServerValue.TIMESTAMP
            val data = MessageData(
                message = "Фото отправляется...",
                idMessage = key,
                timestamp = tempTimestamp
            )
            withContext(Dispatchers.Main){
                mListMessages.add(data)
                updateAdapter(mListMessages)
            }
            val path = REF_STORAGE_ROOT
                    .child("message_images")
                    .child(key)
            path.putFile(uri!!)
                .addOnCompleteListener { task1 ->
                    if (task1.isSuccessful) {
                        path.downloadUrl.addOnCompleteListener { task2 ->
                            if (task2.isSuccessful) {
                                val photoUrl = task2.result.toString()
                                sendToDB(key, photoUrl, "image", timestamp)
                            }
                        }
                    }
                }
        }

    }

    private fun sendMessage() {
        binding.sendButton.setOnClickListener {
            val text = binding.messageEditText.text.toString()
            binding.messageEditText.setText("")
            val idMess = DB.reference.push().key.toString()
            val timestamp: MutableMap<String, String> = ServerValue.TIMESTAMP
            sendToDB(idMess, text, "message", timestamp)
        }
    }

    private fun sendToDB(
        idMess: String,
        text: String,
        type: String,
        timestamp: Any,
    ) {
        lifecycleScope.launch(Dispatchers.IO) {
            val message = MessageData(
                idMessage = idMess,
                idFrom = args.idPatient,
                idTo = args.idDoctor,
                message = text,
                timestamp = timestamp,
                type = type
            )
            var refPatient = ""
            var refDoctor = ""
            refPatient = "message/${UID()}/${args.idDoctor}"
            refDoctor = "message/${args.idDoctor}/${UID()}"

            DB.reference.child(refPatient).child(idMess).setValue(message)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        DB.reference.child(refDoctor)
                            .child(idMess)
                            .setValue(message)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    saveMainList(text, timestamp, type)
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

    private fun saveMainList(text: String, timestamp: Any, type: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            var refPatient = ""
            var refDoctor = ""
            refPatient = "main_list/${UID()}/${args.idDoctor}"
            refDoctor = "main_list/${args.idDoctor}/${UID()}"
            val childPatient = CommonPatientData(
                id = args.idDoctor,
                photoUrl = doctorImageUrl,
                message = text,
                timestamp = timestamp,
                type = type
            )
            val childDoctor = CommonPatientData(
                id = UID(),
                photoUrl = doctorImageUrl,
                message = text,
                timestamp = timestamp,
                type = type
            )

            DB.reference.child(refPatient).setValue(childPatient)
            DB.reference.child(refDoctor).setValue(childDoctor)
        }
    }

    private fun setDoctorsData() {
        lifecycleScope.launch {
            DB.reference
                .child("doctors").child(args.idDoctor)
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            snapshot.getValue(DoctorData::class.java)?.apply {
                                binding.fio.text = "${name} ${patronymic}"
                                doctorName = "$name $patronymic $surname"
                                doctorImageUrl = photoUrl ?: ""
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

                    override fun onCancelled(error: DatabaseError) = Unit
                })
        }
    }

    private fun setToBackButton() {
        binding.toBackButton.setOnClickListener {
            findNavController().popBackStack()
        }
    }

}