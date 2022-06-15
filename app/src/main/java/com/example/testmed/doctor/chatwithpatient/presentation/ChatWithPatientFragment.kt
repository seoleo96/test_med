package com.example.testmed.doctor.chatwithpatient.presentation

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent

import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.NotificationManagerCompat
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import com.example.testmed.*
import com.example.testmed.R
import com.example.testmed.base.BaseFragmentDoctor
import com.example.testmed.databinding.ChatWithPatientFragmentBinding
import com.example.testmed.doctor.MainActivityDoctor
import com.example.testmed.doctor.chatwithpatient.ChatAdapterForDoctor
import com.example.testmed.doctor.chatwithpatient.calling.CallingToPatientActivity
import com.example.testmed.model.ConsultingData
import com.example.testmed.model.DoctorData
import com.example.testmed.model.MessageData
import com.google.android.material.bottomsheet.BottomSheetBehavior
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
    private lateinit var mRecyclerView: RecyclerView
    private var listMessages = mutableListOf<MessageData>()
    private lateinit var patientId: String
    private lateinit var idDoctor: String
    private lateinit var patientFio: String
    private lateinit var patientAddress: String
    private lateinit var patientBirthday: String
    private lateinit var doctorFio: String
    private lateinit var specialityDoc: String
    private lateinit var idClinicDoc: String
    private var photoUrl = ""
    private var uri: Uri? = null
    private var imageUriprofile: Uri? = null
    private var tempTimestamp = ""
    private lateinit var mBottomSheetBehavior: BottomSheetBehavior<*>
    private var pdfFile: Uri? = null


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[ChatWithPatientViewModel::class.java]
        try {
            lifecycleScope.launch(Dispatchers.IO) {
                val temp = DB.reference.child("doctors")
                    .child(UID())
                    .child("iin")
                    .get().await().getValue(String::class.java).toString()
                idNotification = (temp.toLong() - 999900000000).toInt()
            }
        } catch (e: Exception) {

        }
        val viewBottomSheetBehavior = view.findViewById<LinearLayout>(R.id.bottom_sheet_choice)
        mBottomSheetBehavior = BottomSheetBehavior.from(viewBottomSheetBehavior)
        mBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        setPatientId()
        setAdapter()
        setMessages()
        setEditTextListener()
        sendMessage()
        setPatientsData()
        getDoctorData()
        setToBackButton()
        sendImage()
        setCallingActivity()
        toRecommendation()
        toPatientProfile()
    }

    private fun toPatientProfile() {
        binding.fio.setOnClickListener {
            val action =
                ChatWithPatientFragmentDirections.actionNavigationChatWithPatientFragmentToPatientProfileFragment(patientId)
            findNavController().navigate(action)
        }
    }

    fun getDoctorData() {
        lifecycleScope.launch(Dispatchers.IO) {
            DB.reference
                .child("doctors").child(idDoctor)
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            val data = snapshot.getValue(DoctorData::class.java)
                            data?.apply {
                                doctorFio =
                                    if (patronymic != "") "$surname $name $patronymic" else "$surname $name"
                                specialityDoc = speciality
                                idClinicDoc = idClinic
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) = Unit
                })
        }
    }

    private fun toRecommendation() {
        getDoctorData()
        binding.toRecommendation.setOnClickListener {
            val action =
                ChatWithPatientFragmentDirections.actionNavigationChatWithPatientFragmentToRecommendationFragment(
                    patientFio,
                    idDoctor,
                    idClinicDoc,
                    doctorFio,
                    specialityDoc,
                    patientAddress,
                    patientBirthday,
                    patientId,
                    photoUrl,
                    idNotification
                )
            findNavController().navigate(action)
        }
    }

    private var pdfUploadActivity =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK && result.data != null) {
                pdfFile = result.data!!.data
                savePdf()
                mBottomSheetBehavior.state =
                    BottomSheetBehavior.STATE_HIDDEN
            }
        }

    private fun savePdf() {
        try {
            lifecycleScope.launch(Dispatchers.IO) {
                val key = DB.reference.child("message").child(idDoctor)
                    .push().key.toString()
                val path =
                    REF_STORAGE_ROOT.child("message_files").child(key)
                val timestamp = ServerValue.TIMESTAMP
                val data = MessageData(idMessage = key,
                    timestamp = tempTimestamp,
                    idFrom = idDoctor,
                    message = "Отправляется...",
                    type = "file")
                withContext(Dispatchers.Main) {
                    listMessages.add(data)
                    updateAdapter(listMessages)
                }
                val fileUrl = path.putFile(pdfFile!!)
                    .await().storage.downloadUrl.await().toString()
                val fileName = getFilenameFromUri(pdfFile!!)
                sendToDB(key, fileUrl, fileName, timestamp)
            }
        } catch (e: Exception) {
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
                attachFile()
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
            .getIntent(activity as MainActivityDoctor)
        launchSomeActivity.launch(intent)
    }


    private fun setCallingActivity() {
        binding.toConsulting.setOnClickListener {
            val consultingLinc = "https://meet.jit.si/$patientId"
            val message =
                "$consultingLinc \n \n Если хотите зайти в консультацию на компютере перейдиете по этой ссылке "
            val idMess = DB.reference.push().key.toString()
            val timestamp: MutableMap<String, String> = ServerValue.TIMESTAMP
            sendToDB(idMess, message, "message", timestamp)
            sendConsultingData(idMess, consultingLinc)
            val intent = Intent(requireActivity(), CallingToPatientActivity::class.java)
            intent.putExtra("consultingLinc", consultingLinc)
            intent.putExtra("patientId", patientId)
            startActivity(intent)
        }
    }

    private fun sendConsultingData(idMess: String, consultingLinc: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            val message = ConsultingData(
                idConsulting = idMess,
                idFrom = UID(),
                idTo = patientId,
                link = consultingLinc,
                status = "online"
            )
            val refConsulting = "online_consulting/${UID()}/${patientId}"
            DB.reference.child(refConsulting).setValue(message)
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
        mAdapter = ChatAdapterForDoctor { messageData, view, type ->
            if (messageData.message.isNotEmpty() && type == "image") {
                setAnimationView(messageData, view)
            } else {
                viewpdf(messageData)
            }
        }
        mRecyclerView = binding.recyclerChat
        mRecyclerView.adapter = mAdapter
    }

    private fun viewpdf(messageData: MessageData) {
        val value = messageData.message
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(value))
        startActivity(intent)
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
        viewModel.sendNotificationData(patientId, text, idNotification, type)
        viewModel.sendMessage(idMess, patientId, text, timestamp, type, idNotification)
        viewModel.saveMainList(patientId, photoUrl, text, timestamp, type)
    }


    private fun setPatientsData() {
        viewModel.getPatientsData(patientId)
        viewModel.livedata.observe(viewLifecycleOwner) {
            it?.apply {
                binding.fio.text = "$surname $name"
                patientFio =
                    if (patronymic != "") "$surname $name $patronymic" else "$name $surname"
                patientAddress = address
                patientBirthday = birthday
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