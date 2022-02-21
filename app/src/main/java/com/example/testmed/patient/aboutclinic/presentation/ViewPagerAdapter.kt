package com.example.testmed.patient.aboutclinic.presentation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.viewpager.widget.PagerAdapter
import com.example.testmed.R

class ViewPagerAdapter : PagerAdapter() {

    private var list = listOf<Int>()

    fun setList(list: List<Int>) {
        this.list = list
    }

    override fun getCount(): Int {
        return list.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val view = LayoutInflater.from(container.context)
            .inflate(R.layout.item_view_pager, container, false)
        val img = view.findViewById<ImageView>(R.id.iv_item)
        img.setImageResource(list[position])
        container.addView(view, 0)
        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }
}