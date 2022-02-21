package com.example.testmed.patient.chats

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.testmed.DB
import com.example.testmed.UID
import com.example.testmed.base.BaseFragment
import com.example.testmed.databinding.FragmentChatsBinding
import com.example.testmed.doctor.home.chats.ReceivingUsersResult
import com.example.testmed.model.SpecialityData
import com.example.testmed.patient.chats.data.ReceivingDoctorsDataRepository
import kotlinx.coroutines.launch

class ChatsFragment : BaseFragment<FragmentChatsBinding>(FragmentChatsBinding::inflate) {

    private lateinit var adapter: DoctorsAdapter
    private lateinit var viewModel: ChatsViewModel
    private lateinit var mRecyclerView: RecyclerView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val repo = ReceivingDoctorsDataRepository()
        viewModel = ChatsViewModel(repo)
        setAdapter()
        setDoctors()
    }

    private fun setDoctors() {
        viewModel.usersFlow.observe(viewLifecycleOwner, Observer { data ->
            when (data) {
                is ReceivingUsersResult.Loading -> {
                    binding.apply {
                        progressBar.isVisible = true
                        textview.isVisible = false
                        constraintLayout.isVisible = false
                        emptyMessages.isVisible = false
                    }
                }

                is ReceivingUsersResult.Error -> {
                    binding.apply {
                        progressBar.isVisible = false
                        textview.isVisible = false
                        constraintLayout.isVisible = false
                        emptyMessages.isVisible = true
                        emptyMessagesTv.text = data.errorMessage
                    }
                }

                is ReceivingUsersResult.Empty -> {
                    binding.apply {
                        progressBar.isVisible = false
                        textview.isVisible = false
                        emptyMessages.isVisible = true
                        emptyMessagesTv.text = "У вас нет сообщений"
                        constraintLayout.isVisible = false
                        adapter.clearList()
                    }
                }

                is ReceivingUsersResult.Success -> {
                    adapter.updateList(data.data)
                    data.data.forEach {
                        Log.d("message", it.message)
                    }
                    binding.apply {
                        progressBar.isVisible = false
                        textview.isVisible = true
                        textview.text = "Врачи"
                        constraintLayout.isVisible = true
                        emptyMessages.isVisible = false
                    }
                }
            }
        })
    }

    private fun toChat(id: String) {
        val action =
            ChatsFragmentDirections.actionNavigationChatsToNavigationChatWithDoctor(
                UID(),
                id)
        findNavController().navigate(action)
    }

    private fun setAdapter() {
        adapter = DoctorsAdapter {
            toChat(it.id)
        }
        mRecyclerView = binding.recyclerView
        mRecyclerView.adapter = adapter
    }

    private fun setSpeciality() {
        val idSpeciality = DB.reference.push().key.toString()
        val sp = SpecialityData(idSpeciality,
            "Терапевт",
            "https://png.pngtree.com/png-vector/20190215/ourlarge/pngtree-vector-doctor-icon-png-image_515568.jpg",
            "Специалист общего проиля")
        val sp1 = SpecialityData(idSpeciality,
            "Психолог",
            "https://png.pngtree.com/png-vector/20190215/ourlarge/pngtree-vector-doctor-icon-png-image_515568.jpg",
            "Специалист по психологическому благополучию")
        val sp2 = SpecialityData(idSpeciality,
            "Педиатр",
            "https://png.pngtree.com/png-vector/20190215/ourlarge/pngtree-vector-doctor-icon-png-image_515568.jpg",
            "Специалист для детей и подростков")
        DB.reference.child("speciality").child(idSpeciality).setValue(sp2)
    }

}