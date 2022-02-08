package com.example.testmed.registeruser.presentation

import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.widget.EditText
import java.lang.StringBuilder

class PhoneTextFormatter(private val mEditText: EditText, private val mPattern: String) :
    TextWatcher {
    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        val phone = StringBuilder(s)
        if (count > 0 && !isValid(phone.toString())) {
            for (i in 0 until phone.length) {
                val c = mPattern[i]
                if (c != '#' && c != phone[i]) {
                    phone.insert(i, c)
                }
            }
            mEditText.setText(phone)
            mEditText.setSelection(mEditText.text.length)
        }
    }

    override fun afterTextChanged(s: Editable) {}
    private fun isValid(phone: String): Boolean {
        for (i in 0 until phone.length) {
            val c = mPattern[i]
            if (c == '#') continue
            if (c != phone[i]) {
                return false
            }
        }
        return true
    }

    init {
        val maxLength = mPattern.length
        mEditText.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(maxLength))
    }
}