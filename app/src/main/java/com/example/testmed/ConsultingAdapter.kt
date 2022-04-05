package com.example.testmed

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.testmed.databinding.ConsultingItemLayoutBinding
import com.example.testmed.model.CommonPatientData
import java.text.SimpleDateFormat

//private val adapterOnClick: (CommonPatientData) -> Unit
class ConsultingAdapter(private val adapterOnClick: (CommonPatientData, type : Int) -> Unit) :
    RecyclerView.Adapter<ConsultingAdapter.ConsultingViewHolder>() {

    private val list = mutableListOf<CommonPatientData>()
    private val timestamp = System.currentTimeMillis().toString().toLong()
    fun updateList(data: List<CommonPatientData>) {
        this.list.clear()
//        data.stream().sorted { commonPatientData, commonPatientData2 ->
//            val date1 = SimpleDateFormat("dd-MM-yyyy").parse(commonPatientData.date).time
//            val date2 = SimpleDateFormat("dd-MM-yyyy").parse(commonPatientData2.date).time
//            (date1-date2).toInt()
//        }
        this.list.addAll(data)

        this.list.sortByDescending {
            val time: Long = SimpleDateFormat("dd-MM-yyyy").parse(it.date).time
            time
        }
//        list.forEach {
//            Log.d("SORT", it.toString())
//        }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ConsultingViewHolder {
        return ConsultingViewHolder(makeView(parent), adapterOnClick, timestamp)
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
        private val adapterOnClick: (CommonPatientData, Int) -> Unit,
        private val timestamp: Long,
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: CommonPatientData, hideLine: Boolean) {

            Log.d("timestamp", timestamp.toString())
            val timestampFromDb = SimpleDateFormat("dd-MM-yyyy").parse(data.date).time
            if (timestamp > timestampFromDb){
                if (data.confirmation == "1"){
                    binding.cancelConsulting.isGone = true
                    binding.confirmConsulting.isGone = true
                }else{
                    binding.cancelConsulting.isGone = true
                    binding.confirmConsulting.isVisible = true
                }
            }else{
                if (data.confirmation == "1"){
                    binding.cancelConsulting.isGone = true
                    binding.confirmConsulting.isGone = true
                }else{
                    binding.cancelConsulting.isVisible = true
                    binding.confirmConsulting.isVisible = true
                }
            }
            data.apply {
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
            binding.cancelConsulting.setOnClickListener { adapterOnClick.invoke(data, 0) }
            binding.confirmConsulting.setOnClickListener { adapterOnClick.invoke(data, 1) }
        }
    }
}