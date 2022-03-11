package com.example.testmed

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.testmed.databinding.ConsultingItemLayoutBinding
import com.example.testmed.model.CommonPatientData
//private val adapterOnClick: (CommonPatientData) -> Unit
class ConsultingAdapter(private val adapterOnClick: (CommonPatientData) -> Unit) :
    RecyclerView.Adapter<ConsultingAdapter.ConsultingViewHolder>() {

    private val list = mutableListOf<CommonPatientData>()
    fun updateList(data: List<CommonPatientData>) {
        this.list.clear()
        this.list.addAll(data)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ConsultingViewHolder {
        return ConsultingViewHolder(makeView(parent), adapterOnClick)
    }

    private fun makeView(parent: ViewGroup) =
        ConsultingItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)

    override fun onBindViewHolder(holder: ConsultingViewHolder, position: Int) {
        val bind: CommonPatientData = list[position]
        if (position == list.size-1)
            holder.bind(bind, true)
        else
            holder.bind(bind, false)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ConsultingViewHolder(
        private val binding: ConsultingItemLayoutBinding,
        private val adapterOnClick: (CommonPatientData) -> Unit,
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: CommonPatientData, hideLine: Boolean) {
            data.apply {
                if (hideLine){
                    binding.line4.setBackgroundResource(R.drawable.round_fone_recycler)
                }
                binding.apply {
                    speciality.text = data.speciality
                    fullNameDoctor.text = data.fullNameDoctor
                    dateTime.text = "${data.date} ${data.time}"
                }
                if (photoUrl != null) {
                    if (photoUrl.isNotEmpty()) {
                        Glide
                            .with(binding.root.context)
                            .load(photoUrl)
                            .centerCrop()
                            .into(binding.profileImage)
                    } else {
                        binding.profileImage.setImageResource(R.drawable.ic_profile)
                    }
                }
            }
            binding.cancelConsulting.setOnClickListener { adapterOnClick.invoke(data) }
            binding.profileImage.setOnClickListener { adapterOnClick.invoke(data) }
        }
    }
}