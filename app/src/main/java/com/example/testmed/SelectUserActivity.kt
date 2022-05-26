package com.example.testmed

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.testmed.databinding.ActivitySelectUserBinding
import com.example.testmed.doctor.MainActivityDoctor
import com.itextpdf.text.*
import com.itextpdf.text.html.HtmlTags.FONT
import com.itextpdf.text.pdf.BarcodeQRCode
import com.itextpdf.text.pdf.BaseFont
import com.itextpdf.text.pdf.PdfWriter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.*


class SelectUserActivity : AppCompatActivity(R.layout.activity_select_user) {
    private val binding get() = _binding!!
    private var _binding: ActivitySelectUserBinding? = null
    private var pdfFile1: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivitySelectUserBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.apply {
            patient.setOnClickListener {
                val intent = Intent(this@SelectUserActivity, MainActivity::class.java)
                startActivity(intent)
            }
            doctor.setOnClickListener {
//                createPdf()
                val intent = Intent(this@SelectUserActivity, MainActivityDoctor::class.java)
                startActivity(intent)
            }
        }

    }


    private fun createPdf() {
        val values = ContentValues()
        values.put(MediaStore.MediaColumns.DISPLAY_NAME, "pdffiles")
        values.put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
        // If you you want to create folder just add String means folder name like this
        //values.put(MediaStore.MediaColumns.RELATIVE_PATH,Environment.DIRECTORY_DOCUMENTS + "/FolderName" )
        values.put(MediaStore.MediaColumns.RELATIVE_PATH, "${Environment.DIRECTORY_DOCUMENTS}/ConsultingApp/docs")
        pdfFile1 = contentResolver.insert(MediaStore.Files.getContentUri("external"), values)
//        val uri: Uri? = contentResolver.insert(pdfFile!!,values)
//        val uri: Uri? = pdfFile

        if (pdfFile1 != null) {
            val outputStream = contentResolver.openOutputStream(pdfFile1!!)
            val document = Document()
            PdfWriter.getInstance(document, outputStream)
            document.open()
            document.addAuthor("CodeLib")
            addDataIntoPDF(document)
            document.close()
            Toast.makeText(this, "PDF Created", Toast.LENGTH_LONG).show()
//            savePdf()
        }
    }

    fun addDataIntoPDF(document: Document) {
        val paraGraph = Paragraph()
        val fontCyrilic = "/assets/fonts/time.ttf"
        val bf = BaseFont.createFont(fontCyrilic, BaseFont.IDENTITY_H, BaseFont.EMBEDDED)
        val font14 = Font(bf, 14f, Font.NORMAL)
        val font30Bold = Font(bf, 30f, Font.BOLD)
        val paraGraph1 = Paragraph("Добромед", font30Bold)
        paraGraph1.alignment = Element.ALIGN_CENTER
        paraGraph.add(paraGraph1)
        addEmptyLines(paraGraph, 1)
        paraGraph.add(Paragraph("Петропавловск", font14))
        paraGraph.add(Paragraph("Петропавловск.com", font14))
        paraGraph.add(Paragraph("Петропавловск@gmail.com", font14))
        paraGraph.add(Paragraph("870555597335", font14))
        addEmptyLines(paraGraph, 1)
        paraGraph.add(Paragraph("sidhfaldsjkgdjkflsdajkfh"))
        document.add(paraGraph)
    }

    private fun savePdf() {
        lifecycleScope.launch(Dispatchers.IO) {
            val key = DB.reference.child("message").push().key.toString()
            val name = getFilenameFromUri(pdfFile1!!)
            val path =
                REF_STORAGE_ROOT.child("consulting_files").child(name)
            try {
                val fileUrl = path.putFile(pdfFile1!!)
                    .await().storage.downloadUrl.await().toString()
                //save to FRD

                Log.d("data", fileUrl)
                addDataIntoPDF1(fileUrl)
                val fileUrl1 = path.putFile(pdfFile1!!)
                    .await().storage.downloadUrl.await().toString()
                Log.d("data", fileUrl1)

            } catch (e: java.lang.Exception) {
            }
        }
    }

    @SuppressLint("Range")
    fun getFilenameFromUri(uri: Uri): String {
        var result = ""
        val cursor = this.contentResolver.query(uri, null, null, null, null)
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

    fun addDataIntoPDF1(fileUrl: String) {
        if (pdfFile1 != null) {
            val paraGraph = Paragraph()
            val outputStream = contentResolver.openOutputStream(pdfFile1!!)
            val document = Document()
            val fontCyrilic = "/assets/fonts/time.ttf"
            val bf = BaseFont.createFont(fontCyrilic, BaseFont.IDENTITY_H, BaseFont.EMBEDDED)
            val font = Font(bf, 30f, Font.NORMAL)
            PdfWriter.getInstance(document, outputStream)
            document.open()
            val paraGraph1 = Paragraph("Добромед", font)
            paraGraph1.alignment = Element.ALIGN_LEFT
            paraGraph.add(paraGraph1)
            addEmptyLines(paraGraph, 1)
            paraGraph.add(Paragraph("Петропавловск", font))
            paraGraph.add(Paragraph("Петропавловск.com", font))
            paraGraph.add(Paragraph("Петропавловск@gmail.com", font))
            paraGraph.add(Paragraph("870555597335", font))
            addEmptyLines(paraGraph, 1)
            document.add(paraGraph)
            val barcodeQRCode =
                BarcodeQRCode(fileUrl,
                    1000,
                    1000,
                    null)
            val codeQrImage: Image = barcodeQRCode.image
            codeQrImage.scaleAbsolute(100F, 100F)
            document.add(codeQrImage)
            document.close()
            Toast.makeText(this, "PDF Created", Toast.LENGTH_LONG).show()
        }
    }

    fun addEmptyLines(paragraph: Paragraph, lineCount: Int) {
        for (i in 0 until lineCount) {
            paragraph.add(Paragraph(""))
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}