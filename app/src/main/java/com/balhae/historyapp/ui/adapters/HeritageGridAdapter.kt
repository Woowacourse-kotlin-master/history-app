package com.balhae.historyapp.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.balhae.historyapp.R
import com.balhae.historyapp.network.models.HeritageDto
import com.squareup.picasso.Picasso

class HeritageGridAdapter(
    private var items: List<HeritageDto>
) : RecyclerView.Adapter<HeritageGridAdapter.HeritageViewHolder>() {

    private var onItemClickListener: ((HeritageDto) -> Unit)? = null

    inner class HeritageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val heritageImage: ImageView = view.findViewById(R.id.ivHeritageImage)
        val heritageText: TextView = view.findViewById(R.id.tvHeritageDesc)

        init {
            view.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    onItemClickListener?.invoke(items[adapterPosition])
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HeritageViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_heritage, parent, false)
        return HeritageViewHolder(view)
    }

    override fun onBindViewHolder(holder: HeritageViewHolder, position: Int) {
        val item = items[position]

        // 이미지 로드
        Picasso.get()
            .load(item.heritageImage)
            .placeholder(R.drawable.ic_profile_placeholder)
            .error(R.drawable.ic_profile_placeholder)
            .into(holder.heritageImage)

        holder.heritageText.text = item.heritageText
    }

    override fun getItemCount(): Int = items.size

    fun updateData(newItems: List<HeritageDto>) {
        items = newItems
        notifyDataSetChanged()
    }

    fun setOnItemClickListener(listener: (HeritageDto) -> Unit) {
        onItemClickListener = listener
    }
}
