package com.example.testmed.doctor.chatwithpatient

import android.util.Log
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
import java.text.SimpleDateFormat
import java.util.*




class ChatAdapterForDoctor
    : RecyclerView.Adapter<ChatAdapterForDoctor.ChatHolder>() {
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
        if (position > 0){
            val dateNow = bind.timestamp.toString().asDate()
            val date1 = list[position-1].timestamp.toString().asDate()
            if (dateNow==date1){
                bool = true
            }else{
                bool = false
            }
        }else{
            bool = false
//            holder.bindMessagePatient(bind, false)
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
                if (b) {
                    binding.messagesDate.isGone = true
                } else {
                    binding.messagesDate.text = message.timestamp.toString().asDate()
                    binding.messagesDate.isVisible = true
                }
                binding.blocUserMessage.isVisible = true
                binding.blocReceivedMessage.isVisible = false
                binding.chatUserMessage.text = message.message
                binding.chatUserMessageTime.text = message.timestamp.toString().asTime()
            }
        }

        fun bindMessageDoctor(message: MessageData, bool: Boolean) {
            message.apply {
                val date = message.timestamp.toString().asDate()
//                val dateString = asDate(date)
                Log.d("date", "$date")
                if (bool) {
                    binding.messagesDate.isVisible = false
                } else {
                    binding.messagesDate.text = date
                    binding.messagesDate.isVisible = true
                }
                binding.blocUserMessage.isVisible = false
                binding.blocReceivedMessage.isVisible = true
                binding.chatReceivedMessage.text = message.message
                binding.chatReceivedMessageTime.text = message.timestamp.toString().asTime()
            }
        }
    }


}