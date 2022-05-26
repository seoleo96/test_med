package com.example.testmed

import android.annotation.SuppressLint
import android.content.ContentValues
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import android.view.View
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.testmed.base.BaseFragment
import com.example.testmed.databinding.FragmentRecommendationBinding
import com.example.testmed.doctor.chatwithpatient.presentation.ChatWithPatientViewModel
import com.example.testmed.model.AllClinicData
import com.example.testmed.model.RecommendationData
import com.google.firebase.database.*
import com.google.firebase.storage.UploadTask
import com.itextpdf.text.*
import com.itextpdf.text.pdf.BarcodeQRCode
import com.itextpdf.text.pdf.BaseFont
import com.itextpdf.text.pdf.PdfWriter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class RecommendationFragment :
    BaseFragment<FragmentRecommendationBinding>(FragmentRecommendationBinding::inflate) {

    private lateinit var viewModel: ChatWithPatientViewModel
    private val args: RecommendationFragmentArgs by navArgs()

    private lateinit var clinicName: String
    private lateinit var clinicAddress: String
    private lateinit var clinicNumber: String
    private lateinit var clinicCity: String
    private lateinit var clinicLinc: String
    private lateinit var clinicEmail: String
    private var pdfFile1: Uri? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[ChatWithPatientViewModel::class.java]
        setPatientName()
        getClinicData()
    }

    private fun hideAllContent() {
        binding.contentAllView.isVisible = false
        binding.progressBar.isVisible = true
    }

    private fun showAllContent() {
        binding.contentAllView.isVisible = true
        binding.progressBar.isVisible = false
    }


    fun getClinicData() {
        lifecycleScope.launch(Dispatchers.IO) {
            DB.reference
                .child("clinics").child(args.idClinic)
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            val data = snapshot.getValue(AllClinicData::class.java)
                            data?.apply {
                                this@RecommendationFragment.clinicName = name
                                clinicEmail = email
                                clinicLinc = link
                                clinicNumber = phoneNumber
                                clinicAddress = address
                                clinicCity = city
                                binding.saveUsersData.setOnClickListener {
                                    if (binding.etSymptom.text.isEmpty() || binding.etRecommendation.text.isEmpty()) {
                                        showSnackbar("Пустое поле")
                                    } else {
                                        hideAllContent()
                                        createPdf(
                                            this@RecommendationFragment.clinicName,
                                            clinicEmail,
                                            clinicLinc,
                                            clinicNumber,
                                            clinicAddress,
                                            clinicCity
                                        )
                                    }
                                }
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) = Unit
                })
        }
    }

    private fun savePdf(
        document: Document,
        clinicName: String,
        clinicEmail: String,
        clinicLinc: String,
        clinicNumber: String,
        clinicAddress: String,
        clinicCity: String,
    ) {
        lifecycleScope.launch(Dispatchers.IO) {
            val name = getFilenameFromUri(pdfFile1!!)
            val path =
                REF_STORAGE_ROOT.child("consulting_files").child(name)
            try {
                val fileUrl = path.putFile(pdfFile1!!)
                    .await().storage.downloadUrl.await().toString()
                addDataIntoPDF1(fileUrl, document,
                    clinicName,
                    clinicEmail,
                    clinicLinc,
                    clinicNumber,
                    clinicAddress,
                    clinicCity)
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun createPdf(
        clinicName: String,
        clinicEmail: String,
        clinicLinc: String,
        clinicNumber: String,
        clinicAddress: String,
        clinicCity: String,
    ) {
        val values = ContentValues()
        val timestamp = System.currentTimeMillis().toString()
        values.put(MediaStore.MediaColumns.DISPLAY_NAME, timestamp)
        values.put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
        values.put(MediaStore.MediaColumns.RELATIVE_PATH,
            "${Environment.DIRECTORY_DOCUMENTS}/ConsultingApp/docs")
        pdfFile1 =
            requireActivity().contentResolver.insert(MediaStore.Files.getContentUri("external"),
                values)

        if (pdfFile1 != null) {
            val outputStream = requireActivity().contentResolver.openOutputStream(pdfFile1!!)
            val document = Document()
            PdfWriter.getInstance(document, outputStream)
            document.open()
            document.addAuthor(args.doctorFio)
            addDataIntoPDF(
                document,
                clinicName,
                clinicEmail,
                clinicLinc,
                clinicNumber,
                clinicAddress,
                clinicCity
            )
            document.close()
            savePdf(document,
                clinicName,
                clinicEmail,
                clinicLinc,
                clinicNumber,
                clinicAddress,
                clinicCity)
        }
    }

    fun addDataIntoPDF(
        document: Document,
        clinicName: String,
        clinicEmail: String,
        clinicLinc: String,
        clinicNumber: String,
        clinicAddress: String,
        clinicCity: String,
    ) {
        val paraGraph = Paragraph()
        val fontCyrilic = "/assets/fonts/time.ttf"
        val bf = BaseFont.createFont(fontCyrilic, BaseFont.IDENTITY_H, BaseFont.EMBEDDED)
        val font14 = Font(bf, 14f, Font.NORMAL)
        val font10 = Font(bf, 10f, Font.NORMAL)
        val font18Bold = Font(bf, 18f, Font.NORMAL)
        val font30Bold = Font(bf, 30f, Font.NORMAL)
        val paraGraphTitle = Paragraph(clinicName, font30Bold)
        paraGraphTitle.alignment = Element.ALIGN_CENTER
        paraGraph.add(paraGraphTitle)
        addEmptyLines(paraGraph, 1)
        paraGraph.add(Paragraph(clinicCity, font10))
        paraGraph.add(Paragraph(clinicLinc, font10))
        paraGraph.add(Paragraph(clinicEmail, font10))
        paraGraph.add(Paragraph(clinicNumber, font10))
        paraGraph.add(Paragraph(clinicAddress, font10))
        val paraGraphSymptoms = Paragraph(clinicName, font18Bold)
        paraGraphSymptoms.alignment = Element.ALIGN_LEFT
        paraGraph.add(paraGraphSymptoms)
        addEmptyLines(paraGraph, 1)
        paraGraph.add(Paragraph(binding.etSymptom.text.toString(), font10))
        addEmptyLines(paraGraph, 1)
        val paraGraphRecommendation = Paragraph(clinicName, font18Bold)
        paraGraphRecommendation.alignment = Element.ALIGN_LEFT
        paraGraph.add(paraGraphRecommendation)
        addEmptyLines(paraGraph, 1)
        paraGraph.add(Paragraph(binding.etRecommendation.text.toString(), font10))
        addEmptyLines(paraGraph, 1)
        addEmptyLines(paraGraph, 1)
        paraGraph.add(Paragraph("Врач, Специальность - ${args.doctorFio}", font14))
        val timestamp = System.currentTimeMillis().toString().asTimeStatus()
        paraGraph.add(Paragraph("Дата и время - $timestamp", font14))
        document.add(paraGraph)
    }

    suspend fun addDataIntoPDF1(
        fileUrl: String,
        document: Document,
        clinicName: String,
        clinicEmail: String,
        clinicLinc: String,
        clinicNumber: String,
        clinicAddress: String,
        clinicCity: String,
    ) {
        if (pdfFile1 != null) {
            val paraGraph = Paragraph()
            val document = Document()
            val outputStream = requireActivity().contentResolver.openOutputStream(pdfFile1!!)
            val fontCyrilic = "/assets/fonts/time.ttf"
            val bf = BaseFont.createFont(fontCyrilic, BaseFont.IDENTITY_H, BaseFont.EMBEDDED)
            val font12 = Font(bf, 12f, Font.NORMAL)
            val font14BOLD = Font(bf, 14f, Font.BOLD)
            val font10 = Font(bf, 10f, Font.NORMAL)
            val font18Bold = Font(bf, 18f, Font.NORMAL)
            val font30Bold = Font(bf, 24f, Font.NORMAL)
            PdfWriter.getInstance(document, outputStream)
            document.open()
            val paraGraph1 = Paragraph(clinicName, font30Bold)
            paraGraph1.alignment = Element.ALIGN_CENTER
            paraGraph.add(paraGraph1)
            addEmptyLines(paraGraph, 1)
            addEmptyLines(paraGraph, 1)
            paraGraph.add(Paragraph("Адрес: $clinicAddress", font10))
            paraGraph.add(Paragraph("Город: $clinicCity", font10))
            paraGraph.add(Paragraph("Сайт клиники: $clinicLinc", font10))
            paraGraph.add(Paragraph("Почта: $clinicEmail", font10))
            paraGraph.add(Paragraph("Номер телефона: $clinicNumber", font10))
            paraGraph.add(Paragraph("Пациент", font14BOLD))
            paraGraph.add(Paragraph("${args.patientFio}", font12))
            paraGraph.add(Paragraph("Дата рождения пациента", font14BOLD))
            paraGraph.add(Paragraph("${args.patientBirthday}", font12))
            paraGraph.add(Paragraph("Адрес пациента", font14BOLD))
            paraGraph.add(Paragraph("${args.patientAddress}", font12))
            val paraGraphSymptoms = Paragraph("Симптомы", font14BOLD)
            paraGraphSymptoms.alignment = Element.ALIGN_LEFT
            paraGraph.add(paraGraphSymptoms)
            addEmptyLines(paraGraph, 1)
            paraGraph.add(Paragraph(binding.etSymptom.text.toString(), font12))
            addEmptyLines(paraGraph, 1)
            val paraGraphRecommendation = Paragraph("Рекомендация", font14BOLD)
            paraGraphRecommendation.alignment = Element.ALIGN_LEFT
            paraGraph.add(paraGraphRecommendation)
            addEmptyLines(paraGraph, 1)
            paraGraph.add(Paragraph(binding.etRecommendation.text.toString(), font12))
            addEmptyLines(paraGraph, 1)
            addEmptyLines(paraGraph, 1)
            val timestamp = System.currentTimeMillis().toString().asTimeStatus()
            paraGraph.add(Paragraph("Врач, Специальность", font14BOLD))
            paraGraph.add(Paragraph("${args.doctorFio}, ${args.specialityDoc}, дата и время: $timestamp.",
                font12))
            paraGraph.add(Paragraph("Время консультации", font14BOLD))
            paraGraph.add(Paragraph("${binding.etStartTime.text} - ${binding.etEndTime.text}.",
                font12))
            val barcodeQRCode =
                BarcodeQRCode(fileUrl,
                    1000,
                    1000,
                    null)
            val codeQrImage: Image = barcodeQRCode.image
            codeQrImage.scaleAbsolute(100F, 100F)
            paraGraph.add(codeQrImage)
            paraGraph.add(Paragraph(fileUrl, font10))
            paraGraph.add(Paragraph("Для проверки копии электронного документа перейдите по короткой ссылке или считайте QR код. Данный документ согласно пункту 1 статьи 7 ЗРК от 7 января 2003 года «Об электронном документе и электронной цифровой подписи» равнозначен документу на бумажном носителе.",
                font10))
            document.add(paraGraph)
            document.close()
            showSnackbar("PDF is Created")
            val name = getFilenameFromUri(pdfFile1!!)
            val key = DB.reference.child("message").push().key.toString()
            val path =
                REF_STORAGE_ROOT.child("consulting_files").child(name)
            try {
                val putFile: UploadTask.TaskSnapshot = path.putFile(pdfFile1!!).await()
                putFile.task
                    .addOnSuccessListener {
                        try {
                            lifecycleScope.launch(Dispatchers.Default) {
                                val fileUrlAwait = putFile.storage.downloadUrl.await().toString()
                                val timestamp = ServerValue.TIMESTAMP
                                sendToDB(key, fileUrlAwait, name, timestamp)
                            }
                        } catch (e: java.lang.Exception) {
                            e.printStackTrace()
                        }
                    }
                    .addOnFailureListener {
                        showSnackbar(it.toString())
                    }

            } catch (e: java.lang.Exception) {
            }
        }
    }

    private fun sendToDB(idMess: String, fileUrl: String, type: String, timestamp: Any) {
        viewModel.sendNotificationData(args.patientId, fileUrl, args.idNotification, type)
        viewModel.sendMessage(idMess, args.patientId, fileUrl, timestamp, type, args.idNotification)
        viewModel.saveMainList(args.patientId, args.patientPhotoUrl, fileUrl, timestamp, type)
        viewModel.sendRecommendation(
            idDoctor = args.idDoctor,
            patientId = args.patientId,
            doctorFio = args.doctorFio,
            doctorSpeciality = args.specialityDoc,
            patientFio = args.patientFio,
            timestamp = timestamp,
            recommendationUrl = fileUrl,
            idRecommendation = idMess,
        )
        lifecycleScope.launch{
            showAllContent()
            showSnackbar("Рекомендация отправлена.")
            findNavController().popBackStack()
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
        } finally {
            cursor?.close()
            return result
        }
    }

    fun addEmptyLines(paragraph: Paragraph, lineCount: Int) {
        for (i in 0 until lineCount) {
            paragraph.add(Paragraph(""))
        }
    }


    private fun setPatientName() {
        val timestamp = System.currentTimeMillis().toString().asTimeStatus()
        binding.etStartTime.setText(timestamp)
        binding.etEndTime.setText(timestamp)
        binding.etFioo.setText(args.patientFio)
    }

}