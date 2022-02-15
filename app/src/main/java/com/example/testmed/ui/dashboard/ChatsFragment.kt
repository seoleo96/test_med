package com.example.testmed.ui.dashboard

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.example.testmed.base.BaseFragment
import com.example.testmed.databinding.FragmentChatsBinding

class ChatsFragment : BaseFragment<FragmentChatsBinding>(FragmentChatsBinding::inflate) {

    private lateinit var vm: DashboardViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vm = ViewModelProvider(this)[DashboardViewModel::class.java]

    }

}