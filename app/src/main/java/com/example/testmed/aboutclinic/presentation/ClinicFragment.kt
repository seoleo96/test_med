package com.example.testmed.aboutclinic.presentation

import android.os.Bundle
import android.view.View
import com.example.testmed.DB
import com.example.testmed.R
import com.example.testmed.base.BaseFragment
import com.example.testmed.databinding.FragmentClinicBinding
import com.example.testmed.model.ClinicData
import com.example.testmed.showSnackbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener

class ClinicFragment : BaseFragment<FragmentClinicBinding>(FragmentClinicBinding::inflate) {

    private lateinit var valueEventListener: ValueEventListener
    private lateinit var rdbRef: DatabaseReference

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var imgs = listOf<Int>(R.drawable.c2,R.drawable.c4,R.drawable.c5, R.drawable.c6)

        var adapter: ViewPagerAdapter = ViewPagerAdapter()
        adapter.setList(imgs)
        binding.viewPager.adapter = adapter
        setUpData()
    }

    private fun setUpData() {
        rdbRef = DB.reference
            .child("clinic")
            valueEventListener = rdbRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val data = snapshot.getValue(ClinicData::class.java)
                    binding.apply {
                        if (data != null){
                            clinicName.text = data.name
                            email.text = data.email
                            link.text = data.link
                            phoneNumber.text = data.phoneNumber
                            address.text = data.address
                        }else{
                            showSnackbar("Проверьте интернет подключение.")
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
    }

    override fun onPause() {
        super.onPause()
        rdbRef.removeEventListener(valueEventListener)
    }
}