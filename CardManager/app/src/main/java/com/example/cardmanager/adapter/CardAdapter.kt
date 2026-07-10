package com.example.cardmanager.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.cardmanager.R
import com.example.cardmanager.database.CardFace

class CardAdapter(
    private val list: List<CardFace>
) : RecyclerView.Adapter<CardAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtName: TextView = view.findViewById(R.id.txtCardName)
        val txtInfo: TextView = view.findViewById(R.id.txtInfo)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_card, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val card = list[position]

        holder.txtName.text = card.name

        holder.txtInfo.text =
            "${card.civilization}　コスト:${card.cost}\n${card.type}　${card.race}"

    }

    override fun getItemCount(): Int {
        return list.size
    }
}