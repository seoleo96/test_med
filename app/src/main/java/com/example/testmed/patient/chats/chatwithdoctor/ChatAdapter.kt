package com.example.testmed.patient.chats.chatwithdoctor

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
import com.example.testmed.databinding.MessageItemBinding
import com.example.testmed.model.MessageData
import com.google.android.material.snackbar.Snackbar

//
class ChatAdapter(private val adapterOnClick: (MessageData, View) -> Unit)
    : RecyclerView.Adapter<ChatAdapter.ChatHolder>() {
    private val list = arrayListOf<MessageData>()
    fun updateList(data: List<MessageData>) {
        this.list.clear()
        this.list.addAll(data)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ChatHolder {
        return ChatHolder(makeView(parent), adapterOnClick)
    }

    private fun makeView(parent: ViewGroup) =
        MessageItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)

    override fun onBindViewHolder(holder: ChatHolder, position: Int) {
        val bind: MessageData = list[position]
        var bool = false
            bool = if (position > 0){
                val dateNow = bind.timestamp.toString().asDate()
                val date1 = list[position-1].timestamp.toString().asDate()
                dateNow==date1
            }else{
                false
            }

        if (list[position].idFrom == UID()) {
            holder.bindMessagePatient(bind, bool)
        } else {
            holder.bindMessageDoctor(bind, bool)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ChatHolder(
        private val binding: MessageItemBinding,
        private val adapterOnClick: (MessageData, View) -> Unit,
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindMessagePatient(message: MessageData, b: Boolean) {
            if (message.type == "message"){
                message.apply {
                    val date = message.timestamp.toString().asDate()
                    if (b){
                        binding.messagesDate.isGone = true
                    }else{
                        binding.messagesDate.text = date
                        binding.messagesDate.isVisible = true
                    }
                    if (seen == "0"){
                        binding.chatUserMessageSeen.setBackgroundResource(R.drawable.ic_done_black_24)
                    }else{
                        binding.chatUserMessageSeen.setBackgroundResource(R.drawable.ic_double_check_black_24)
                    }
                    binding.blocUserMessage.isVisible = true
                    binding.blocUserMessageImage.isGone = true
                    binding.blocReceivedMessage.isGone = true
                    binding.blocReceivedMessageImage.isGone = true
                    binding.chatUserMessage.text = message.message
                    binding.chatUserMessageTime.text = message.timestamp.toString().asTime()
                }
            }else{
                message.apply {
                    val date = message.timestamp.toString().asDate()
                    if (b){
                        binding.messagesDate.isGone = true
                    }else{
                        binding.messagesDate.text = date
                        binding.messagesDate.isVisible = true
                    }
                    if (seen == "0"){
                        binding.chatUserMessageImageSeen.setBackgroundResource(R.drawable.ic_done_black_24)
                    }else{
                        binding.chatUserMessageImageSeen.setBackgroundResource(R.drawable.ic_double_check_black_24)
                    }

                    binding.blocUserMessage.isGone = true
                    binding.blocUserMessageImage.isVisible = true
                    binding.blocReceivedMessage.isVisible = false
                    binding.blocReceivedMessageImage.isGone = true
                    binding.chatUserImageTime.text = message.timestamp.toString().asTime()
                    binding.chatUserMessageImageProgress.isVisible = true
                    Glide
                        .with(binding.chatUserMessageImage.context)
                        .load(message.message)
                        .addListener(object : RequestListener<Drawable>{
                            override fun onLoadFailed(
                                e: GlideException?,
                                model: Any?,
                                target: Target<Drawable>?,
                                isFirstResource: Boolean,
                            ): Boolean {
                                binding.chatUserMessageImageProgress.isGone = true
                                return false
                            }

                            override fun onResourceReady(
                                resource: Drawable?,
                                model: Any?,
                                target: Target<Drawable>?,
                                dataSource: DataSource?,
                                isFirstResource: Boolean,
                            ): Boolean {
                                binding.chatUserMessageImageProgress.isGone = true
                                return false
                            }
                        })
                        .centerCrop()
                        .into(binding.chatUserMessageImage)
                }
                binding.chatUserMessageImage.setOnClickListener {
                    adapterOnClick.invoke(message, it)
                }
            }
        }

        fun bindMessageDoctor(message: MessageData, bool: Boolean) {
            if (message.type == "message"){
                message.apply {
                    val date = message.timestamp.toString().asDate()
                    if (bool){
                        binding.messagesDate.isGone = true
                    }else{
                        binding.messagesDate.text = date
                        binding.messagesDate.isVisible = true
                    }
                    binding.blocUserMessageImage.isGone = true
                    binding.blocUserMessage.isGone = true
                    binding.blocReceivedMessage.isVisible = true
                    binding.blocReceivedMessageImage.isGone = true
                    binding.chatReceivedMessage.text = message.message
                    binding.chatReceivedMessageTime.text = message.timestamp.toString().asTime()
                }
            }else{
                message.apply {
                    val date = message.timestamp.toString().asDate()
                    if (bool){
                        binding.messagesDate.isGone = true
                    }else{
                        binding.messagesDate.text = date
                        binding.messagesDate.isVisible = true
                    }
                    binding.blocUserMessage.isGone = true
                    binding.blocUserMessageImage.isGone = true
                    binding.blocReceivedMessageImage.isVisible = true
                    binding.blocReceivedMessage.isGone = true
                    binding.chatReceivedImageTime.text = message.timestamp.toString().asTime()
                    binding.chatReceivedMessageImageProgress.isVisible = true
                    Glide
                        .with(binding.chatReceivedMessageImage.context)
                        .load(message.message)
                        .addListener(object : RequestListener<Drawable>{
                            override fun onLoadFailed(
                                e: GlideException?,
                                model: Any?,
                                target: Target<Drawable>?,
                                isFirstResource: Boolean,
                            ): Boolean {
                                binding.chatReceivedMessageImageProgress.isGone = true
                                return false
                            }

                            override fun onResourceReady(
                                resource: Drawable?,
                                model: Any?,
                                target: Target<Drawable>?,
                                dataSource: DataSource?,
                                isFirstResource: Boolean,
                            ): Boolean {
                                binding.chatReceivedMessageImageProgress.isGone = true
                                return false
                            }
                        })
                        .centerCrop()
                        .into(binding.chatReceivedMessageImage)
                }
                binding.chatReceivedMessageImage.setOnClickListener {
                    adapterOnClick.invoke(message, it)
                }
            }
        }
    }
}