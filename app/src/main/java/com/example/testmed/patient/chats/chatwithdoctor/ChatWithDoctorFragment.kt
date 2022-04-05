package com.example.testmed.patient.chats.chatwithdoctor

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.DownloadManager
import android.content.Context.DOWNLOAD_SERVICE
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.OpenableColumns
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import com.example.testmed.*
import com.example.testmed.base.BaseFragment
import com.example.testmed.databinding.FragmentChatWithDoctorBinding
import com.example.testmed.doctor.chatwithpatient.calling.CallingToPatientActivity
import com.example.testmed.model.MessageData
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ServerValue
import com.google.firebase.database.ValueEventListener
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.File


class ChatWithDoctorFragment :
    BaseFragment<FragmentChatWithDoctorBinding>(FragmentChatWithDoctorBinding::inflate) {

    private val argsNav: ChatWithDoctorFragmentArgs by navArgs()
    private lateinit var idDoctor: String
    private lateinit var idPatient: String
    private var idNotification: Int = 0
    private var idNotificationDoctor: Int = 0
    private lateinit var mAdapter: ChatAdapter
    private lateinit var mRecyclerView: RecyclerView
    private var mListMessages = mutableListOf<MessageData>()
    private var doctorName = ""
    private var doctorImageUrl = ""
    private var tempTimestamp = ""
    private var uri: Uri? = null
    private var pdfFile: Uri? = null
    private var imageUriprofile: Uri? = null
    private lateinit var chatWithDoctorViewModel: ChatWithDoctorViewModel
    private lateinit var mBottomSheetBehavior: BottomSheetBehavior<*>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        chatWithDoctorViewModel = ViewModelProvider(this)[ChatWithDoctorViewModel::class.java]
        idPatient = UID()
        idDoctor =
            if (argsNav.idDoctor == "0") requireArguments()[ID_DOCTOR].toString() else argsNav.idDoctor
        lifecycleScope.launch(Dispatchers.IO) {
            val temp = DB.reference.child("patients")
                .child(idPatient)
                .child("iin")
                .get().await().getValue(String::class.java).toString()
            idNotification = (temp.toLong() - 999900000000).toInt()
        }
        val viewBottomSheetBehavior = view.findViewById<LinearLayout>(R.id.bottom_sheet_choice)
        mBottomSheetBehavior = BottomSheetBehavior.from(viewBottomSheetBehavior)
        mBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        setAdapter()
        setToBackButton()
        setDoctorsData()
        setEditTextListener()
        sendMessage()
        setMessages()
        sendImage()
        updateToken()
        setConsultingData()
        setToConsulting()
    }

    private fun setToConsulting() {
        binding.toConsulting.setOnClickListener {
            val consultingLinc = "https://meet.jit.si/${UID()}"
            val intent = Intent(requireActivity(), CallingToPatientActivity::class.java)
            intent.putExtra("consultingLinc", consultingLinc)
            startActivity(intent)
        }
    }

    private fun setConsultingData() {
        val ref = "online_consulting/${idDoctor}/${UID()}/status"
        DB.reference.child(ref).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val data = snapshot.getValue(String::class.java)
                    if (data != null) {
                        if (data == "online") {
                            try {
                                binding.toConsulting.isVisible = true
                                showSnackbarLong("Консультация началась. Нажмите на кнопку (+) в углу.")
                            } catch (e: Exception) {
                            }
                        } else {
                            binding.toConsulting.isVisible = false
                        }
                    }

                }
            }

            override fun onCancelled(error: DatabaseError) = Unit
        })
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
            val temp1 = DB.reference.child("doctors")
                .child(idDoctor)
                .child("iin")
                .get().await().getValue(String::class.java).toString()
            idNotificationDoctor = (temp1.toLong() - 999900000000).toInt()
            NotificationManagerCompat.from(requireContext()).cancel(idNotificationDoctor);
        }
        chatWithDoctorViewModel.seenMessages(idDoctor, idPatient)

        if (requireArguments()[CONSULTING].toString() == "CONSULTING") {
            Log.d(CONSULTING, "CONSULTING")
            val consultingLinc = "https://meet.jit.si/${UID()}"
            val intent = Intent(requireActivity(), CallingToPatientActivity::class.java)
            intent.putExtra("consultingLinc", consultingLinc)
            startActivity(intent)
        }
    }


    private fun updateToken() {
        chatWithDoctorViewModel.updateToken()
    }

//    private fun setMessages() {
//        chatWithDoctorViewModel.getMessages(idDoctor)
//        chatWithDoctorViewModel.messLiveData.observe(viewLifecycleOwner) { data ->
//            mListMessages.removeIf {
//                it.idMessage == data.idMessage
//            }
//            tempTimestamp = data.timestamp.toString()
//            mListMessages.add(data)
//            updateAdapter(mListMessages)
//        }
//    }

    private fun setMessages() {
        val ref = DB.reference
            .child("message")
            .child(idPatient)
            .child(idDoctor)
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                if (snapshot.exists()) {
                    snapshot.children.forEach {
                        Log.d("TAG", it.toString())

                        val data = it.getValue(MessageData::class.java)
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

    private fun downloadfile(data: MessageData) {
        val file = File(Environment.getExternalStorageDirectory(), "mypdffile.pdf")
        val downloadManager =
            requireActivity().getSystemService(DOWNLOAD_SERVICE) as DownloadManager?
        val request = DownloadManager.Request(data.message.toUri())
            .setTitle(data.type)
            .setDescription("This is file download demo")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
            .setDestinationUri(Uri.fromFile(file))
        downloadManager!!.enqueue(request)
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, "This is my text to send.")
            type = "text/pdf"
        }

        val shareIntent = Intent.createChooser(sendIntent, null)
        startActivity(shareIntent)
    }

    private fun getPermissions() {
        val externalReadPermission = Manifest.permission.READ_EXTERNAL_STORAGE
        val externalWritePermission = Manifest.permission.WRITE_EXTERNAL_STORAGE
        if (ContextCompat.checkSelfPermission(requireContext(),
                externalReadPermission) != PackageManager.PERMISSION_GRANTED
            && ContextCompat.checkSelfPermission(requireContext(),
                externalWritePermission) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(arrayOf(externalReadPermission, externalWritePermission),
                100)
        }
    }

    private fun setAdapter() {
        mAdapter = ChatAdapter { messageData, view, type ->
            if (messageData.message.isNotEmpty() && type == "image") {
                setAnimationView(messageData, view)
            } else {
//                getPermissions()
//                downloadfile(messageData)
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
                chatWithDoctorViewModel.updateStateTo("1")
                updateStatePatient(online)
                sendButtonNotEditable()
            } else {
                if (it.isNotEmpty()) {
                    chatWithDoctorViewModel.updateTyping(idDoctor)
                }
                binding.apply {
                    sendButton.isEnabled = true
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        chatWithDoctorViewModel.updateStateTo("1")
        chatWithDoctorViewModel.removeListener()
//        chatWithDoctorViewModel.removeListenerMess()
    }

    private fun sendButtonNotEditable() {
        binding.apply {
            sendButton.isEnabled = false
        }
    }

    private
    var launchSomeActivity =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK && result.data != null) {
                uri = CropImage.getActivityResult(result.data).uri
                savePhoto()
                mBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            }
        }

    private
    var pdfUploadActivity =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK && result.data != null) {
                pdfFile = result.data!!.data
                savePdf()
                mBottomSheetBehavior.state =
                    BottomSheetBehavior.STATE_HIDDEN
            }
        }

    private fun savePdf() {
        lifecycleScope.launch(Dispatchers.IO) {
            val key = DB.reference.child("message").child(idPatient)
                .push().key.toString()
            val path =
                REF_STORAGE_ROOT.child("message_files").child(key)
            val timestamp = ServerValue.TIMESTAMP
            val data = MessageData(idMessage = key,
                timestamp = tempTimestamp,
                idFrom = idPatient,
                message = "Отправляется...",
                type = "message")
            withContext(Dispatchers.Main) {
                mListMessages.add(data)
                updateAdapter(mListMessages)
            }
            val fileUrl = path.putFile(pdfFile!!)
                .await().storage.downloadUrl.await().toString()
            val fileName = getFilenameFromUri(pdfFile!!)
            sendToDB(key, fileUrl, fileName, timestamp)
        }
    }

    @SuppressLint("Range")
    fun getFilenameFromUri(uri: Uri): String {
        var result = ""
        val cursor = requireActivity().contentResolver.query(uri, null, null, null, null)
        try {
            if (cursor != null && cursor.moveToFirst()) {
                result =
                    cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
            }
        } catch (e: Exception) {
            showSnackbar(e.toString())
        } finally {
            cursor?.close()
            return result
        }
    }

    private fun sendImage() {
        binding.addMedia.setOnClickListener {
            mBottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            view?.findViewById<ImageView>(R.id.btn_attach_file)?.setOnClickListener {
//                attachFile()
            }
            view?.findViewById<ImageView>(R.id.btn_attach_image)
                ?.setOnClickListener { attachImage() }
        }

        binding.hidebtmsheet.setOnClickListener {
            mBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }
        binding.recyclerChat.setOnClickListener {
            mBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }
    }

    private fun attachFile() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "*/*"
        pdfUploadActivity.launch(intent)
    }

    private fun attachImage() {
        val intent = CropImage.activity()
            .setAspectRatio(1, 1)
            .setRequestedSize(400, 400)
            .getIntent(activity as MainActivity)
        launchSomeActivity.launch(intent)
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
                timestamp = tempTimestamp,
                idFrom = idPatient,
                message = "Отправляется...",
                type = "message")
            withContext(Dispatchers.Main) {
                mListMessages.add(data)
                updateAdapter(mListMessages)
            }
            val photoUrl =
                path.putFile(uri!!).await().storage.downloadUrl.await().toString()
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

    private fun sendToDB(
        idMess: String,
        text: String,
        type: String,
        timestamp: Any,
    ) {
        chatWithDoctorViewModel.sendNotificationData(idDoctor,
            idPatient,
            text,
            idNotification)
        chatWithDoctorViewModel.sendMessage(idMess,
            text,
            type,
            timestamp,
            idDoctor,
            idNotification)
        chatWithDoctorViewModel.saveMainList(text,
            timestamp,
            type,
            idDoctor,
            doctorImageUrl)
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

                if (photoUrl.isNotEmpty()) {
                    setImage(photoUrl, binding.profileImage)
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