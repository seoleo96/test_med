package com.example.testmed.patient.speciality.presentation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.testmed.R
import com.example.testmed.databinding.SpecialityItemLayoutBinding
import com.example.testmed.model.SpecialityData

class SpecialityAdapter(private val adapterOnClick : (SpecialityData) -> Unit) :
    RecyclerView.Adapter<SpecialityAdapter.SpecialityViewHolder>() {


    private val list = arrayListOf<SpecialityData>()
    fun updateList(list: List<SpecialityData>) {
        this.list.clear()
        this.list.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): SpecialityViewHolder {
        return SpecialityViewHolder(makeView(parent), adapterOnClick)
    }

    private fun makeView(parent: ViewGroup) =
        SpecialityItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)

    override fun onBindViewHolder(holder: SpecialityViewHolder, position: Int) {
        val bind: SpecialityData = list[position]
        if (position == list.size-1){
            holder.bind(bind, true)
        }else{
            holder.bind(bind, false)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class SpecialityViewHolder(
        private val binding: SpecialityItemLayoutBinding,
        private val adapterOnClick: (SpecialityData) -> Unit
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(species: SpecialityData, hideLine: Boolean) {
            species.apply {
                if (hideLine){
                    binding.line4.setBackgroundResource(R.drawable.round_fone_recycler)
                }
                binding.speciality.text = speciality
                binding.purpose.text = purpose
                Glide
                    .with(binding.root.context)
                    .load(imageUrl)
                    .centerCrop()
                    .into(binding.profileImage)

            }

            binding.itemRoot.setOnClickListener { adapterOnClick.invoke(species) }
        }
    }
}