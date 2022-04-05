package com.example.testmed.doctor.home.consulting

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.testmed.R
import com.example.testmed.databinding.ConsultingItemLayoutDoctorBinding
import com.example.testmed.model.CommonPatientData
import java.text.SimpleDateFormat

//private val adapterOnClick: (CommonPatientData) -> Unit
class ConsultingDoctorAdapter(private val adapterOnClick: (CommonPatientData) -> Unit) :
    RecyclerView.Adapter<ConsultingDoctorAdapter.ConsultingDoctorViewHolder>() {

    private val list = mutableListOf<CommonPatientData>()
    private val timestamp = System.currentTimeMillis().toString().toLong()

    fun updateList(data: List<CommonPatientData>) {
        this.list.clear()
        this.list.addAll(data)
        this.list.sortByDescending {
            val time: Long = SimpleDateFormat("dd-MM-yyyy").parse(it.date).time
            time
        }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ConsultingDoctorViewHolder {
        return ConsultingDoctorViewHolder(makeView(parent), adapterOnClick, timestamp)
    }

    private fun makeView(parent: ViewGroup) =
        ConsultingItemLayoutDoctorBinding.inflate(LayoutInflater.from(parent.context), parent, false)

    override fun onBindViewHolder(holder: ConsultingDoctorViewHolder, position: Int) {
        val bind: CommonPatientData = list[position]
        holder.bind(bind, true)

//        if (position == list.size-1)
//            holder.bind(bind, true)
//        else
//            holder.bind(bind, false)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ConsultingDoctorViewHolder(
        private val binding: ConsultingItemLayoutDoctorBinding,
        private val adapterOnClick: (CommonPatientData) -> Unit,
        private val timestamp: Long,
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: CommonPatientData, hideLine: Boolean) {
            val timestampFromDb = SimpleDateFormat("dd-MM-yyyy").parse(data.date).time
            if (timestamp > timestampFromDb){
                if (data.statusConsulting == "inactive"){
                    binding.dateTime.setTextColor(ContextCompat.getColor(binding.dateTime.context,R.color.green))
                }else{
                    binding.dateTime.setTextColor(ContextCompat.getColor(binding.dateTime.context,R.color.red))
                }
            }else{
                binding.dateTime.setTextColor(ContextCompat.getColor(binding.dateTime.context,R.color.button_color))
            }
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