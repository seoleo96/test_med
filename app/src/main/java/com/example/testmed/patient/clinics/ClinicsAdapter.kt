package com.example.testmed.patient.clinics

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.testmed.R
import com.example.testmed.databinding.ClinicsItemLayoutBinding
import com.example.testmed.databinding.SpecialityItemLayoutBinding
import com.example.testmed.model.AllClinicData
import com.example.testmed.model.SpecialityData
import com.example.testmed.patient.speciality.presentation.SpecialityAdapter

class ClinicsAdapter(private val adapterOnClick : (AllClinicData) -> Unit) :
    RecyclerView.Adapter<ClinicsAdapter.ClinicsViewHolder>() {


    private val list = arrayListOf<AllClinicData>()
    fun updateList(list: List<AllClinicData>) {
        this.list.clear()
        this.list.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ClinicsViewHolder {
        return ClinicsViewHolder(makeView(parent), adapterOnClick)
    }

    private fun makeView(parent: ViewGroup) =
        ClinicsItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)

    override fun onBindViewHolder(holder: ClinicsViewHolder, position: Int) {
        val bind: AllClinicData = list[position]
        if (position == list.size-1){
            holder.bind(bind, true)
        }else{
            holder.bind(bind, false)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ClinicsViewHolder(
        private val binding: ClinicsItemLayoutBinding,
        private val adapterOnClick: (AllClinicData) -> Unit
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(species: AllClinicData, hideLine: Boolean) {
            species.apply {
                if (hideLine){
                    binding.line4.setBackgroundResource(R.drawable.round_fone_recycler)
                }
                binding.speciality.text = name
                binding.purpose.text = address
                Glide
                    .with(binding.root.context)
                    .load(imageUrl)
                    .fitCenter()
                    .into(binding.profileImage)

            }

            binding.itemRoot.setOnClickListener { adapterOnClick.invoke(species) }
        }
    }
}