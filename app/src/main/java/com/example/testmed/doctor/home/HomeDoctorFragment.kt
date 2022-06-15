package com.example.testmed.doctor.home

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.example.testmed.ID_PATIENT
import com.example.testmed.base.BaseFragmentDoctor
import com.example.testmed.databinding.FragmentHomeDoctorBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator


class HomeDoctorFragment :
    BaseFragmentDoctor<FragmentHomeDoctorBinding>(FragmentHomeDoctorBinding::inflate) {

    private lateinit var viewpager: ViewPager2
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getDataFromChildFragment()
        setTablayout()
        if (arguments?.get(ID_PATIENT) == "consulting"){
            requireActivity().runOnUiThread {
                val tab: TabLayout.Tab? = binding.tabLayout.getTabAt(1)
                tab?.select()
            }
        }
    }

    private fun getDataFromChildFragment() {
        childFragmentManager.setFragmentResultListener("requestKey",
            viewLifecycleOwner) { key, bundle ->
            val result: String? = bundle.getString("bundleKey")
            if (result != null) {
                val action =
                    HomeDoctorFragmentDirections
                        .actionNavigationHomeDoctorToNavigationChatWithPatientFragment()
                action.id = result
                findNavController().navigate(action)
            }
        }

        childFragmentManager.setFragmentResultListener("requestKeyConsulting",
            viewLifecycleOwner) { key, bundle ->
            val result: String? = bundle.getString("key")
            if (result != null) {
                val action =
                    HomeDoctorFragmentDirections
                        .actionNavigationHomeDoctorToNavigationChatWithPatientFragment()
                action.id = result
                findNavController().navigate(action)
            }
        }
        childFragmentManager.setFragmentResultListener("toRecommendation",
            viewLifecycleOwner) { _, bundle ->
            val result: String? = bundle.getString("idPatient")
            if (result != null) {
                val action =
                    HomeDoctorFragmentDirections.actionNavigationHomeDoctorToPatientRecFragment(result)
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
                    tab.text = "Чат"
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