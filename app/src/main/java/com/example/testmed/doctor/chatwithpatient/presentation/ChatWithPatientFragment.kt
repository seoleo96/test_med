package com.example.testmed.doctor.chatwithpatient.presentation

import android.app.Activity
import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import android.content.Intent

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.NotificationManagerCompat
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import com.example.testmed.*
import com.example.testmed.base.BaseFragmentDoctor
import com.example.testmed.databinding.ChatWithPatientFragmentBinding
import com.example.testmed.doctor.MainActivityDoctor
import com.example.testmed.doctor.chatwithpatient.ChatAdapterForDoctor
import com.example.testmed.doctor.chatwithpatient.calling.CallingToPatientActivity
import com.example.testmed.model.MessageData
import com.google.firebase.database.*
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.lang.Exception

class ChatWithPatientFragment
    : BaseFragmentDoctor<ChatWithPatientFragmentBinding>(ChatWithPatientFragmentBinding::inflate) {

    private lateinit var viewModel: ChatWithPatientViewModel
    private val argsNav: ChatWithPatientFragmentArgs by navArgs()
    private var idNotification: Int = 0
    private var idNotificationPatient: Int = 0
    private lateinit var mAdapter: ChatAdapterForDoctor
    private lateinit var patientId: String
    private lateinit var idDoctor: String
    private lateinit var mRecyclerView: RecyclerView
    private var listMessages = mutableListOf<MessageData>()
    private var photoUrl = ""
    private var uri: Uri? = null
    private var imageUriprofile: Uri? = null
    private var tempTimestamp = ""


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[ChatWithPatientViewModel::class.java]
        lifecycleScope.launch(Dispatchers.IO) {
            val temp = DB.reference.child("doctors")
                .child(UID())
                .child("iin")
                .get().await().getValue(String::class.java).toString()
            idNotification = (temp.toLong() - 999900000000).toInt()
        }
        setPatientId()
        setAdapter()
        setMessages()
        setEditTextListener()
        sendMessage()
        setPatientsData()
        setToBackButton()
        sendImage()
        setCallingActivity()
    }

    private fun setCallingActivity() {
        binding.toConsulting.setOnClickListener {
            val intent = Intent(requireActivity(), CallingToPatientActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setPatientId() {
        idDoctor = UID()
        patientId = if (argsNav.id == "0") {
            requireArguments()[ID_PATIENT].toString()
        } else {
            argsNav.id
        }
    }

    override fun onResume() {
        super.onResume()
        Log.d("TEMPIDNOT", idNotificationPatient.toString())
        lifecycleScope.launch {
            val temp1: String? = DB.reference.child("patients")
                .child(argsNav.id)
                .child("iin")
                .get().await().getValue(String::class.java)
            if (temp1 != null) {
                idNotificationPatient = (temp1.toLong() - 999900000000).toInt()
                NotificationManagerCompat.from(requireContext()).cancel(idNotificationPatient);
            }
        }
        viewModel.seenMessages(idDoctor, patientId)
    }

    private fun setAdapter() {
        mAdapter = ChatAdapterForDoctor { messageData, view ->
            if (messageData.message.isNotEmpty()) {
                setAnimationView(messageData, view)
            }
        }
        mRecyclerView = binding.recyclerChat
        mRecyclerView.adapter = mAdapter
    }

    private fun setAnimationView(messageData: MessageData, view: View) {
        lifecycleScope.launch(Dispatchers.IO) {
            val imageUri = super.getCachedPhotoURI(messageData.message)
            withContext(Dispatchers.Main) {
                super.zoomImageFromThumb(
                    view,
                    imageUri,
                    binding.expandedImage,
                    binding.container,
                    binding.containerSen,
                    binding.containerToolbar,
                    binding.recyclerChat
                )
            }
        }
    }

//    private fun setMessages() {
//        viewModel.getMessages(idDoctor, patientId)
//        viewModel.messLiveData.observe(viewLifecycleOwner) { data ->
//            listMessages.removeIf { listData ->
//                listData.idMessage == data.idMessage
//            }
//            tempTimestamp = data.timestamp.toString()
//            listMessages.add(data)
//            updateAdapter(listMessages)
//        }
//    }

    private fun setMessages() {
        val ref = DB.reference
            .child("message")
            .child(idDoctor)
            .child(patientId)
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    snapshot.children.forEach { messages ->
                        val data = messages.getValue(MessageData::class.java)
                        if (data != null) {
                            listMessages.removeIf { listData ->
                                listData.idMessage == data.idMessage
                            }
                            tempTimestamp = data.timestamp.toString()
                            listMessages.add(data)
                            updateAdapter(listMessages)
                        }
                    }
                }

            }

            override fun onCancelled(error: DatabaseError) = Unit
        })
    }

    private fun setEditTextListener() {
        sendButtonNotEditable()

        binding.sendMessageEditText.doAfterTextChanged {
            if (it?.length == 0 || it?.trim()!!.isEmpty()) {
                updateStateTo(idDoctor, "1")
                updateStateDoctor(online)
                sendButtonNotEditable()
            } else {
                if (it.isNotEmpty()) {
                    updateStateDoctorTyping(idDoctor)
                }
                binding.apply {
                    sendButton.isEnabled = true
                }
            }

        }
    }

    private fun updateStateTo(idDoctor: String, id: String) {
        val refStateTo = DB.reference.child("doctors").child(idDoctor).child("stateTo")
        lifecycleScope.launch(Dispatchers.IO) {
            refStateTo.setValue(id)
        }
    }

    private fun updateStateDoctorTyping(idDoctor: String) {
        val refState = DB.reference.child("doctors").child(idDoctor).child("state")
        lifecycleScope.launch(Dispatchers.IO) {
            refState.setValue(typing)
            updateStateTo(idDoctor, patientId)
        }
    }

    override fun onPause() {
        super.onPause()
        updateStateTo(idDoctor, "1")
        viewModel.removeListener()
//        viewModel.removeListenerMess()
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
                .setRequestedSize(400, 400)
                .getIntent(activity as MainActivityDoctor)
            launchSomeActivity.launch(intent)
        }
    }

    private fun updateAdapter(mListMessages: MutableList<MessageData>) {
        mAdapter.updateList(mListMessages)
        mRecyclerView.smoothScrollToPosition(mAdapter.itemCount)
    }

    private fun savePhoto() {
        lifecycleScope.launch(Dispatchers.IO) {
            val key = DB.reference.child("message").child(idDoctor).push().key.toString()
            val timestamp = ServerValue.TIMESTAMP
            val data = MessageData(
                message = "Фото отправляется...",
                idMessage = key,
                timestamp = tempTimestamp,
                idFrom = idDoctor,
                type = "message"
            )
            withContext(Dispatchers.Main) {
                listMessages.add(data)
                updateAdapter(listMessages)
            }
            val path = REF_STORAGE_ROOT.child("message_images").child(key)
            val photoUrl = path.putFile(uri!!).await().storage.downloadUrl.await().toString()
            sendToDB(key, photoUrl, "image", timestamp)
        }
    }

    private fun sendMessage() {
        binding.sendButton.setOnClickListener {
            lifecycleScope.launch(Dispatchers.IO) {
                super.updateStateDoctor(online)
                updateStateTo(idDoctor, "1")
            }
            val text = binding.sendMessageEditText.text.toString()
            binding.sendMessageEditText.setText("")
            val idMess = DB.reference.push().key.toString()
            val timestamp: MutableMap<String, String> = ServerValue.TIMESTAMP
            sendToDB(idMess, text, "message", timestamp)
        }
    }

    private fun sendToDB(idMess: String, text: String, type: String, timestamp: Any) {
        viewModel.sendNotificationData(patientId, text, idNotification)
        viewModel.sendMessage(idMess, patientId, text, timestamp, type, idNotification)
        viewModel.saveMainList(patientId, photoUrl, text, timestamp, type)
    }

    private fun setPatientsData() {
        viewModel.getPatientsData(patientId)
        viewModel.livedata.observe(viewLifecycleOwner) {
            it?.apply {
                binding.fio.text = "$name $patronymic"
                if (stateTo == UID()) {
                    binding.experience.text = state.toString()
                } else {
                    try {
                        binding.experience.text = state.toString().asTimeStatus()
                    } catch (e: Exception) {
                        binding.experience.text = state.toString()
                    }
                }
                this@ChatWithPatientFragment.photoUrl = photoUrl ?: ""
                if (photoUrl.isNotEmpty()) {
                    super.setImage(photoUrl, binding.profileImage)
                    lifecycleScope.launch(Dispatchers.IO) {
                        imageUriprofile = super.getCachedPhotoURI(photoUrl)
                    }
                }
            }
        }

        lifecycleScope.launch {
            binding.profileImage.setOnClickListener {
                if (imageUriprofile != null) {
                    super.zoomImageFromThumb(
                        it,
                        imageUriprofile!!,
                        binding.expandedImage,
                        binding.container,
                        binding.containerSen,
                        binding.containerToolbar,
                        binding.recyclerChat
                    )
                }
            }
        }
    }

    private fun setToBackButton() {
        binding.toBackButton.setOnClickListener {
            findNavController().popBackStack()
        }
    }

}