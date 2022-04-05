package com.example.testmed.patient.speciality.consulting.presentation

import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.compose.ui.graphics.Color
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.testmed.R
import com.example.testmed.databinding.ScheduleItemLayoutBinding

//private val adapterOnClick: (DoctorData) -> Unit
class ScheduleAdapter(private val adapterOnClick: (String) -> Unit) :
    RecyclerView.Adapter<ScheduleAdapter.ScheduleViewHolder>() {

    private val list = arrayListOf<String>()
    fun updateList(list: List<String>) {
        this.list.clear()
        this.list.addAll(list)
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ScheduleViewHolder {
        return ScheduleViewHolder(makeView(parent), adapterOnClick)
    }

    private fun makeView(parent: ViewGroup) =
        ScheduleItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)

    override fun getItemCount(): Int {
        return list.size
    }

    var cardViewList = mutableListOf<CardView>()

    override fun onBindViewHolder(holder: ScheduleViewHolder, position: Int) {
        cardViewList.add(holder.cardView)
        val time =list[position]
        holder.textview.text = time
        holder.cardView.setOnClickListener { view ->
            cardViewList.forEach {
                it.setCardBackgroundColor(ContextCompat.getColor(it.context,
                    R.color.white))
            }
            holder.cardView.setCardBackgroundColor(ContextCompat.getColor(view.context,
                R.color.button_color))
            adapterOnClick.invoke(time)
        }

    }


    class ScheduleViewHolder(
        private val binding: ScheduleItemLayoutBinding,
        private val adapterOnClick: (String) -> Unit,
    ) : RecyclerView.ViewHolder(binding.root) {
        val cardView = binding.car1
        val textview = binding.firstTextView
    }
}