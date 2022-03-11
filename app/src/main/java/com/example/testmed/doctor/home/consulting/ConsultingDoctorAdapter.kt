package com.example.testmed.doctor.home.consulting

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.testmed.R
import com.example.testmed.databinding.ConsultingItemLayoutBinding
import com.example.testmed.databinding.ConsultingItemLayoutDoctorBinding
import com.example.testmed.model.CommonPatientData
//private val adapterOnClick: (CommonPatientData) -> Unit
class ConsultingDoctorAdapter(private val adapterOnClick: (CommonPatientData) -> Unit) :
    RecyclerView.Adapter<ConsultingDoctorAdapter.ConsultingDoctorViewHolder>() {

    private val list = mutableListOf<CommonPatientData>()
    fun updateList(data: List<CommonPatientData>) {
        this.list.clear()
        this.list.addAll(data)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ConsultingDoctorViewHolder {
        return ConsultingDoctorViewHolder(makeView(parent), adapterOnClick)
    }

    private fun makeView(parent: ViewGroup) =
        ConsultingItemLayoutDoctorBinding.inflate(LayoutInflater.from(parent.context), parent, false)

    override fun onBindViewHolder(holder: ConsultingDoctorViewHolder, position: Int) {
        val bind: CommonPatientData = list[position]
        if (position == list.size-1)
            holder.bind(bind, true)
        else
            holder.bind(bind, false)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ConsultingDoctorViewHolder(
        private val binding: ConsultingItemLayoutDoctorBinding,
        private val adapterOnClick: (CommonPatientData) -> Unit,
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: CommonPatientData, hideLine: Boolean) {
            data.apply {
                if (hideLine){
                    binding.line4.setBackgroundResource(R.drawable.round_fone_recycler)
                }
                binding.apply {
                    phoneNumber.text = data.phoneNumber
                    fullNamePatient.text = data.fullNamePatient
                    dateTime.text = "${data.date} ${data.time}"
                }
                if (photoUrl != null) {
                    if (photoUrl.isNotEmpty()) {
                        Glide
                            .with(binding.root.context)
                            .load(photoUrlPatient)
                            .centerCrop()
                            .into(binding.profileImage)
                    } else {
                        binding.profileImage.setImageResource(R.drawable.ic_profile)
                    }
                }else {
                    binding.profileImage.setImageResource(R.drawable.ic_profile)
                }
            }
            binding.itemRoot.setOnClickListener { adapterOnClick.invoke(data) }
        }
    }
}