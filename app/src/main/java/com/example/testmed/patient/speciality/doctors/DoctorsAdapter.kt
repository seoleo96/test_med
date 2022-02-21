package com.example.testmed.patient.speciality.doctors

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.testmed.R
import com.example.testmed.databinding.DoctorsItemLayoutBinding
import com.example.testmed.model.DoctorData

class DoctorsAdapter(private val adapterOnClick: (DoctorData) -> Unit) :
    RecyclerView.Adapter<DoctorsAdapter.DoctorViewHolder>() {


    private val list = arrayListOf<DoctorData>()
    fun updateList(list: List<DoctorData>) {
        this.list.clear()
        this.list.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): DoctorViewHolder {
        return DoctorViewHolder(makeView(parent), adapterOnClick)
    }

    private fun makeView(parent: ViewGroup) =
        DoctorsItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)

    override fun onBindViewHolder(holder: DoctorViewHolder, position: Int) {
        val bind: DoctorData = list[position]
        if (position == list.size-1)
            holder.bind(bind, true)
        holder.bind(bind, false)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class DoctorViewHolder(
        private val binding: DoctorsItemLayoutBinding,
        private val adapterOnClick: (DoctorData) -> Unit,
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(doctor: DoctorData, hideline: Boolean) {
            doctor.apply {
                if (hideline) {
                    binding.line4.setBackgroundResource(R.drawable.round_fone_recycler)
                }
                binding.fio.text = "$name $surname $patronymic"
                binding.experience.text = experience
                Glide
                    .with(binding.root.context)
                    .load(photoUrl)
                    .centerCrop()
                    .into(binding.profileImage)

            }

            binding.itemRoot.setOnClickListener { adapterOnClick.invoke(doctor) }
        }
    }
}