package com.example.testmed


import android.content.Context
import android.graphics.Rect
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ProgressBar
import android.widget.Toast
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


fun AUTH(): FirebaseAuth {
    return FirebaseAuth.getInstance()
}

fun UID(): String {
    return AUTH().currentUser?.uid!!
}

val DB: FirebaseDatabase by lazy {
    FirebaseDatabase.getInstance()
}

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

fun String.asDate(): String {
    val time = Date(this.toLong())
    val timeFormat = SimpleDateFormat("dd.LLLL.yyyy", Locale.getDefault())
    val ret: String = timeFormat.format(time)
    return ret
}