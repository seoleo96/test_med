package com.example.testmed.doctor.home.chats

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.example.testmed.base.BaseFragment
import com.example.testmed.databinding.ChatsWithPatientFragmentBinding
import com.example.testmed.doctor.home.PatientsAdapter
import com.example.testmed.doctor.home.chats.data.ReceivingPatientsDataRepository
import com.example.testmed.showSnackbar

class PatientsFragment
    : BaseFragment<ChatsWithPatientFragmentBinding>(ChatsWithPatientFragmentBinding::inflate) {

    private lateinit var adapter: PatientsAdapter
    private lateinit var viewModel: PatientViewModel
    private lateinit var mRecyclerView: RecyclerView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val repo = ReceivingPatientsDataRepository()
        viewModel = PatientViewModel(repo)
        setAdapter()
        setPatients()
    }

    private fun setPatients() {
        viewModel.usersFlow.observe(viewLifecycleOwner, Observer { data ->
            when (data) {
                is ReceivingUsersResult.Loading -> {
                    binding.apply {
                        progressBar.isVisible = true
                        textview.isVisible = false
                        constraintLayout.isVisible = false
                    }
                }

                is ReceivingUsersResult.Error -> {
                    binding.apply {
                        progressBar.isVisible = false
                        textview.isVisible = true
                        textview.text = data.errorMessage
                        constraintLayout.isVisible = false
                    }
                }

                is ReceivingUsersResult.Empty -> {
                    binding.apply {
                        progressBar.isVisible = false
                        textview.isVisible = true
                        textview.text = "Нет сообщений."
                        adapter.clearList()
                        constraintLayout.isVisible = false
                    }
                }

                is ReceivingUsersResult.Success -> {
                    adapter.updateList(data.data)
                    binding.apply {
                        progressBar.isVisible = false
                        textview.isVisible = true
                        textview.text = "Пациенты"
                        constraintLayout.isVisible = true
                    }
                }
            }
        })
    }

    private fun setAdapter() {
        adapter = PatientsAdapter {
            setFragmentResult("requestKey", bundleOf("bundleKey" to it.id))
            showSnackbar(it.message)
        }
        mRecyclerView = binding.recyclerView
        mRecyclerView.adapter = adapter
    }
}