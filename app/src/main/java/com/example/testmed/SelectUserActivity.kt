package com.example.testmed

import android.content.Intent
import android.os.Bundle
import android.util.JsonToken
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.testmed.databinding.ActivitySelectUserBinding
import com.example.testmed.doctor.MainActivityDoctor
import com.google.firebase.database.ServerValue.TIMESTAMP

class SelectUserActivity : AppCompatActivity(R.layout.activity_select_user) {
    private val binding get() = _binding!!
    private var _binding: ActivitySelectUserBinding? = null
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
                val intent = Intent(this@SelectUserActivity, MainActivityDoctor::class.java)
                startActivity(intent)
            }

        }

        val timestamp: MutableMap<String, String> = TIMESTAMP
        Log.d("timestamp", timestamp.toString())

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}