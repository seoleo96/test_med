package com.example.testmed


import android.content.Context
import android.graphics.Rect
import android.media.MediaPlayer
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.text.DateFormatSymbols
import java.text.SimpleDateFormat
import java.util.*


fun View.hideKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)
}

inline fun Context.toast(message: () -> String) {
    Toast.makeText(this, message(), Toast.LENGTH_LONG).show()
}

inline fun Fragment.showSnackbar(msg: String) {
    Snackbar.make(requireView(),
        msg,
        Snackbar.LENGTH_SHORT).show()
}

inline fun Fragment.showSnackbarLong(msg: String) {
    Snackbar.make(requireView(),
        msg,
        Snackbar.LENGTH_LONG).show()

}


fun AUTH(): FirebaseAuth {
    return FirebaseAuth.getInstance()
}

fun UID(): String {
    return AUTH().currentUser?.uid!!
}

val DB: FirebaseDatabase by lazy {
    FirebaseDatabase.getInstance()
}

val online = "в сети"
val typing = "печатает..."
var PATIENT_STATUS = ""
var DOCTOR_STATUS = ""
const val SERVER_KEY = "AAAAiApd1PA:APA91bG0aTplmaQeeDA1R2o0eG6TfL0nozI8VgVXulHLvlA07FRvZ79IjiU2FQKbegOIdtvhnF6jwNGWoUa5AmY9biEfaF2KuzI6eTj-90-dZldX7DGMZOCy_bi4LI03gw5EEGiS80gk\t\n"
const val CONTENT_TYPE = "application/json"
const val ID_PATIENT = "ID_PATIENT"
const val ID_DOCTOR = "ID_DOCTOR"
var CONSULTING = "CONSULTING"

val REF_STORAGE_ROOT: StorageReference by lazy {
    FirebaseStorage.getInstance().reference
}

var PHONE_NUMBER = ""

fun Fragment.visibleProgress(progressBar: ProgressBar) {
    progressBar.isVisible = true
}

fun Fragment.invisibleProgress(progressBar: ProgressBar) {
    progressBar.isVisible = false
}

fun String.asTime(): String {
    val time = Date(this.toLong())
    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    return timeFormat.format(time)
}

fun String.asTimeStatus(): String {
    val time = Date(this.toLong())
    val timeFormat = SimpleDateFormat("HH:mm dd.MM.yy", myDateFormatSymbols)
    return "${timeFormat.format(time)} г"
}

fun String.asDate2(): String {
    val time = Date(this.toLong())
    val timeFormat = SimpleDateFormat("dd.LLLL.yyyy", Locale.getDefault())
    val ret: String = timeFormat.format(time)
    return ret
}

fun String.asDate() : String {
    val time = Date(this.toLong())
    var dateFormat = SimpleDateFormat("dd MMMM yyyy", myDateFormatSymbols)
    return "${dateFormat.format(time)} г"
}

private val myDateFormatSymbols: DateFormatSymbols = object : DateFormatSymbols() {
    override fun getMonths(): Array<String> {
        return arrayOf("января", "февраля", "марта", "апреля", "мая", "июня",
            "июля", "августа", "сентября", "октября", "ноября", "декабря")
    }
}
var  mediaPlayer = MediaPlayer()
fun playAudio(context: Context, s : Int) {
    mediaPlayer.stop()
    mediaPlayer = MediaPlayer.create(context, s)
    mediaPlayer.start()

}

fun stopAudio() {
    if (mediaPlayer.isPlaying) {
        mediaPlayer.stop()
    }
}

