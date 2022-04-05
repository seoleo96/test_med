package com.example.testmed.patient.chats

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.testmed.R
import com.example.testmed.databinding.PatientItemLayoutBinding
import com.example.testmed.model.CommonPatientData

class DoctorsAdapter(private val adapterOnClick: (CommonPatientData) -> Unit) :
    RecyclerView.Adapter<DoctorsAdapter.DoctorsViewHolder>() {

    private val list = mutableListOf<CommonPatientData>()
    fun updateList(data: List<CommonPatientData>) {
        clearList()
        this.list.addAll(data)
        this.list.forEach{
            Log.d("ADAPTERLIST", it.toString())
        }
        this.list.sortByDescending {
            Log.d("ADAPTER", it.timestamp.toString())
            it.timestamp.toString().toLong()
        }
        notifyDataSetChanged()
    }

    fun clearList() {
        this.list.clear()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): DoctorsViewHolder {
        return DoctorsViewHolder(makeView(parent), adapterOnClick)
    }

    private fun makeView(parent: ViewGroup) =
        PatientItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)

    override fun onBindViewHolder(holder: DoctorsViewHolder, position: Int) {
        val bind: CommonPatientData = list[position]
        if (position == list.size-1)
            holder.bind(bind, true)
        else
            holder.bind(bind, false)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class DoctorsViewHolder(
        private val binding: PatientItemLayoutBinding,
        private val adapterOnClick: (CommonPatientData) -> Unit,
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: CommonPatientData, hideLine: Boolean) {
            if(data.sizeNotReadingMessages != "0"){
                binding.sizeNotReadingMessages.isVisible = true
                binding.sizeNotReadingMessages.text = data.sizeNotReadingMessages
            }else{
                binding.sizeNotReadingMessages.isVisible = false
            }
            data.apply {
//                if (hideLine){
//                    binding.line4.setBackgroundResource(R.drawable.round_fone_recycler)
//                }
                binding.patientName.text = "$name $surname"
                binding.lastMessage.text = message
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
            binding.itemRoot.setOnClickListener { adapterOnClick.invoke(data) }
        }
    }
}