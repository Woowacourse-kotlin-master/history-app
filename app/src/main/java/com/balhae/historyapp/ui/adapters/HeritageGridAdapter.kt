package com.balhae.historyapp.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.balhae.historyapp.R
import com.balhae.historyapp.network.models.HeritageItem

class HeritageGridAdapter(
    private var items: List<HeritageItem>
) : RecyclerView.Adapter<HeritageGridAdapter.HeritageViewHolder>() {

    inner class HeritageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val heritageName: TextView = view.findViewById(R.id.tvHeritageName)
        val heritageDesc: TextView = view.findViewById(R.id.tvHeritageDesc)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HeritageViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_heritage, parent, false)
        return HeritageViewHolder(view)
    }

    override fun onBindViewHolder(holder: HeritageViewHolder, position: Int) {
        val item = items[position]
        holder.heritageName.text = item.name ?: "문화재"
        holder.heritageDesc.text = item.description ?: "설명이 없습니다."
    }

    override fun getItemCount(): Int = items.size

    fun updateItems(newItems: List<HeritageItem>) {
        items = newItems
        notifyDataSetChanged()
    }
}
