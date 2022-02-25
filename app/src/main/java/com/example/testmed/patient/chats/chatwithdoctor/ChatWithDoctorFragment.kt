package com.example.testmed.patient.chats.chatwithdoctor

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.doAfterTextChanged
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import com.example.testmed.*
import com.example.testmed.base.BaseFragment
import com.example.testmed.databinding.FragmentChatWithDoctorBinding
import com.example.testmed.model.MessageData
import com.google.firebase.database.ServerValue
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class ChatWithDoctorFragment :
    BaseFragment<FragmentChatWithDoctorBinding>(FragmentChatWithDoctorBinding::inflate) {

    private val argsNav: ChatWithDoctorFragmentArgs by navArgs()
    private lateinit var idDoctor: String
    private lateinit var idPatient: String
    private var idNotification: Int = 0
    private lateinit var mAdapter: ChatAdapter
    private lateinit var mRecyclerView: RecyclerView
    private var mListMessages = mutableListOf<MessageData>()
    private var doctorName = ""
    private var doctorImageUrl = ""
    private var tempTimestamp = ""
    private var uri: Uri? = null
    private var imageUriprofile: Uri? = null
    private lateinit var chatWithDoctorViewModel: ChatWithDoctorViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        chatWithDoctorViewModel = ViewModelProvider(this)[ChatWithDoctorViewModel::class.java]
        idPatient = UID()
        idDoctor =
            if (argsNav.idDoctor == "0") arguments!![ID_DOCTOR].toString() else argsNav.idDoctor

        setAdapter()
        setToBackButton()
        setDoctorsData()
        setEditTextListener()
        sendMessage()
        setMessages()
        sendImage()
        updateToken()
    }

    override fun onResume() {
        super.onResume()
        chatWithDoctorViewModel.seenMessages(idDoctor, idPatient)
    }


    private fun updateToken() {
        chatWithDoctorViewModel.updateToken()
    }

    private fun setMessages() {
        chatWithDoctorViewModel.getMessages(idDoctor)
        chatWithDoctorViewModel.messLiveData.observe(viewLifecycleOwner) { data ->
            mListMessages.removeIf {
                it.idMessage == data.idMessage
            }
            tempTimestamp = data.timestamp.toString()
            mListMessages.add(data)
            updateAdapter(mListMessages)
        }
    }

//    private fun setMessages() {
//        val ref = DB.reference
//            .child("message")
//            .child(UID())
//            .child(args.idDoctor)
//        ref.addValueEventListener(object : ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                if (snapshot.exists()) {
//                    snapshot.children.forEach {
//                        val data = it.getValue(MessageData::class.java)
//                        if (data != null) {
//                            mListMessages.removeIf {
//                                it.idMessage == data.idMessage
//                            }
//                            tempTimestamp = data.timestamp.toString()
//                            mListMessages.add(data)
//                        }
//                    }
//                }
//                updateAdapter(mListMessages)
//            }
//
//            override fun onCancelled(error: DatabaseError) = Unit
//        })
//    }

    private fun setAdapter() {
        mAdapter = ChatAdapter { messageData, view ->
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
        binding.messageEditText.doOnTextChanged { text, start, before, count ->
            if (start >= 1) {
                chatWithDoctorViewModel.updateTyping(idDoctor)
            }
            if (start == 0) {
                chatWithDoctorViewModel.updateStateTo("1")
                super.updateStatePatient(online)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        chatWithDoctorViewModel.updateStateTo("1")
        chatWithDoctorViewModel.removeListener()
        chatWithDoctorViewModel.removeListenerMess()
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
            val key = DB.reference.child("message").child(idPatient).push().key.toString()
            val path = REF_STORAGE_ROOT.child("message_images").child(key)
            val timestamp = ServerValue.TIMESTAMP
            val data = MessageData(idMessage = key,
                timestamp = "1645637136528",
                idFrom = idPatient,
                message = "Отправляется...",
                type = "message")
            withContext(Dispatchers.Main) {
                mListMessages.add(data)
                updateAdapter(mListMessages)
            }
            val photoUrl = path.putFile(uri!!).await().storage.downloadUrl.await().toString()
            sendToDB(key, photoUrl, "image", timestamp)
        }
    }

    private fun sendMessage() {
        binding.sendButton.setOnClickListener {
            lifecycleScope.launch(Dispatchers.IO) {
                super.updateStatePatient(online)
            }
            val text = binding.messageEditText.text.toString()
            binding.messageEditText.setText("")
            val idMess = DB.reference.push().key.toString()
            val timestamp: MutableMap<String, String> = ServerValue.TIMESTAMP
            sendToDB(idMess, text, "message", timestamp)
        }
    }

    private fun sendToDB(idMess: String, text: String, type: String, timestamp: Any) {
        chatWithDoctorViewModel.sendNotificationData(idDoctor, idPatient, text, idNotification)
        chatWithDoctorViewModel.sendMessage(idMess, text, type, timestamp, idDoctor, idNotification)
        chatWithDoctorViewModel.saveMainList(text, timestamp, type, idDoctor, doctorImageUrl)

    }

    private fun setDoctorsData() {
        chatWithDoctorViewModel.getDoctorsData(idDoctor)
        chatWithDoctorViewModel.livedata.observe(viewLifecycleOwner) {
            it?.apply {
                view?.findViewById<TextView>(R.id.fio)?.text = "${name} ${patronymic}"
                doctorName = "$name $patronymic $surname"
                if (stateTo == idPatient) {
                    binding.experience.text = state.toString()
                } else {
                    try {
                        binding.experience.text = state.toString().asTimeStatus()
                    } catch (e: Exception) {
                        binding.experience.text = state.toString()
                    }
                }
                doctorImageUrl = photoUrl
                idNotification = (iin.toLong() - 999900000000).toInt()
                if (photoUrl.isNotEmpty()) {
                    super.setImage(photoUrl, binding.profileImage)
                    lifecycleScope.launch(Dispatchers.IO) {
                        imageUriprofile = super.getCachedPhotoURI(photoUrl)
                    }
                }
            }
        }
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

    private fun setToBackButton() {
        binding.toBackButton.setOnClickListener {
            findNavController().popBackStack()
        }
    }
}