package com.example.testmed.doctor.home.consulting.recommendation

import android.graphics.drawable.Drawable
import android.text.util.Linkify
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.testmed.*
import com.example.testmed.databinding.MessageItemBinding
import com.example.testmed.databinding.RecommendationItemBinding
import com.example.testmed.model.MessageData
import com.example.testmed.model.RecommendationData

class RecPatientAdapter(private val adapterOnClick: (String) -> Unit) :
    RecyclerView.Adapter<RecPatientAdapter.RecHolder>() {

    private val list = arrayListOf<RecommendationData>()
    fun updateList(data: List<RecommendationData>) {
        this.list.clear()
        this.list.addAll(data)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): RecHolder {
        return RecHolder(makeView(parent), adapterOnClick)
    }

    private fun makeView(parent: ViewGroup) =
        RecommendationItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)

    override fun onBindViewHolder(holder: RecHolder, position: Int) {
        val bind: RecommendationData = list[position]
        holder.bindRecPatient(bind)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class RecHolder(
        private val binding: RecommendationItemBinding,
        private val adapterOnClick: (String) -> Unit,
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindRecPatient(recData: RecommendationData) {
            recData.apply {
                binding.fioDoctor.text = doctorFio
                binding.specialityDoctor.text = doctorSpeciality
                binding.fileNameSend.text = "Рекомендация"
                binding.chatUserFileTime.text = timestamp.toString().asTimeStatus()
            }
            binding.chatUserMessageFile.setOnClickListener {
                adapterOnClick.invoke(recData.recommendationUrl)
            }
        }
    }
}