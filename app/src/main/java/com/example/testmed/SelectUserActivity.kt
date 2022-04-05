package com.example.testmed

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.testmed.databinding.ActivitySelectUserBinding
import com.example.testmed.doctor.MainActivityDoctor
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


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

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}