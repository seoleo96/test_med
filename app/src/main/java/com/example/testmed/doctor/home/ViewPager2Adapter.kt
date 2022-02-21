package com.example.testmed.doctor.home

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.testmed.doctor.home.archive.ArchiveFragment
import com.example.testmed.doctor.home.chats.PatientsFragment
import com.example.testmed.doctor.home.consulting.ConsultingFragment
import com.example.testmed.model.CommonPatientData

class ViewPager2Adapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle
) :
    FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> {
                PatientsFragment()
            }
            1 -> {
                ConsultingFragment()
            }
            else -> {
                ArchiveFragment()
            }
        }
    }
}