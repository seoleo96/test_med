package com.example.testmed.doctor.home

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.example.testmed.base.BaseFragment
import com.example.testmed.databinding.FragmentHomeDoctorBinding
import com.google.android.material.tabs.TabLayoutMediator


class HomeDoctorFragment :
    BaseFragment<FragmentHomeDoctorBinding>(FragmentHomeDoctorBinding::inflate) {

    private lateinit var viewpager: ViewPager2
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getDataFromChildFragment()
        setTablayout()
    }

    private fun getDataFromChildFragment() {
        childFragmentManager.setFragmentResultListener("requestKey",
            viewLifecycleOwner) { key, bundle ->
            val result: String? = bundle.getString("bundleKey")
            if (result != null) {
                val action = HomeDoctorFragmentDirections.actionNavigationHomeDoctorToNavigationChatWithPatientFragment(
                        result)
                findNavController().navigate(action)
            }
        }
    }

    private fun setTablayout() {
        val adapter = ViewPager2Adapter(childFragmentManager, lifecycle)
        viewpager = binding.viewPager
        viewpager.adapter = adapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            when (position) {
                0 -> {
                    tab.text = "Чаты"
                }
                1 -> {
                    tab.text = "Консультация"
                }
                2 -> {
                    tab.text = "Архив"
                }
            }
        }.attach()
    }
}