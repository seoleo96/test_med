package com.example.testmed.patient.aboutclinic.presentation

import android.graphics.drawable.Drawable
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
import com.example.testmed.R
import com.example.testmed.UID
import com.example.testmed.asDate
import com.example.testmed.asTime
import com.example.testmed.databinding.CommentsItemBinding
import com.example.testmed.databinding.MessageItemBinding
import com.example.testmed.model.CommentsData
import com.example.testmed.model.MessageData
import com.google.android.material.snackbar.Snackbar

//private val adapterOnClick: (MessageData, View) -> Unit
class CommentsAdapter() : RecyclerView.Adapter<CommentsAdapter.CommentsHolder>() {
    private val list = arrayListOf<CommentsData>()
    fun updateList(data: List<CommentsData>) {
        this.list.clear()
        this.list.addAll(data)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): CommentsHolder {
        return CommentsHolder(makeView(parent))
    }

    private fun makeView(parent: ViewGroup) =
        CommentsItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)

    override fun onBindViewHolder(holder: CommentsHolder, position: Int) {
        val bind: CommentsData = list[position]
        var bool = false
        bool = if (position > 0) {
            val dateNow = bind.timestamp.toString().asDate()
            val date1 = list[position - 1].timestamp.toString().asDate()
            dateNow == date1
        } else {
            false
        }
        holder.bindDoctorComments(bind, bool)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class CommentsHolder(
        private val binding: CommentsItemBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bindDoctorComments(message: CommentsData, bool: Boolean) {
            message.apply {
                val date = message.timestamp.toString().asDate()
                if (bool) {
                    binding.commentsDate.isGone = true
                } else {
                    binding.commentsDate.text = date
                    binding.commentsDate.isVisible = true
                }
                binding.username.text = message.username
                binding.commentTime.text = message.timestamp.toString().asTime()
                binding.comment.text = message.message
            }
        }
    }
}