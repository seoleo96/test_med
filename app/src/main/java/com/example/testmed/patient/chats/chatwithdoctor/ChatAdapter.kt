package com.example.testmed.patient.chats.chatwithdoctor

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.testmed.UID
import com.example.testmed.asDate
import com.example.testmed.asTime
import com.example.testmed.databinding.MessageItemBinding
import com.example.testmed.model.MessageData

class ChatAdapter
    : RecyclerView.Adapter<ChatAdapter.ChatHolder>() {
    private val list = arrayListOf<MessageData>()
    fun updateList(list: List<MessageData>) {
        this.list.clear()
        this.list.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ChatHolder {
        return ChatHolder(makeView(parent))
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
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindMessagePatient(message: MessageData, b: Boolean) {
            message.apply {
                val date = message.timestamp.toString().asDate()
                if (b){
                    binding.messagesDate.isGone = true
                }else{
                    binding.messagesDate.text = date
                    binding.messagesDate.isVisible = true
                }
                binding.blocUserMessage.isVisible = true
                binding.blocReceivedMessage.isVisible = false
                binding.chatUserMessage.text = message.message
                binding.chatUserMessageTime.text = message.timestamp.toString().asTime()
            }
        }

        fun bindMessageDoctor(message: MessageData, bool: Boolean) {
            val date = message.timestamp.toString().asDate()
            if (bool){
                binding.messagesDate.isVisible = false
            }else{
                binding.messagesDate.text = date
                binding.messagesDate.isVisible = true
            }
            message.apply {
                binding.blocUserMessage.isVisible = false
                binding.blocReceivedMessage.isVisible = true
                binding.chatReceivedMessage.text = message.message
                binding.chatReceivedMessageTime.text = message.timestamp.toString().asTime()
            }
        }
    }
}