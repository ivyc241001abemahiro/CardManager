package com.example.cardmanager.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.cardmanager.R
import com.example.cardmanager.database.Deck

class DeckAdapter(
    private val deckList: List<Deck>,
    private val onClick: (Deck) -> Unit,
    private val onLongClick: (Deck) -> Unit
) : RecyclerView.Adapter<DeckAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val deckName: TextView = view.findViewById(R.id.txtDeckName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_deck, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val deck = deckList[position]

        holder.deckName.text = deck.name

        // タップ
        holder.itemView.setOnClickListener {
            onClick(deck)
        }

        // 長押し
        holder.itemView.setOnLongClickListener {
            onLongClick(deck)
            true
        }
    }

    override fun getItemCount(): Int = deckList.size
}