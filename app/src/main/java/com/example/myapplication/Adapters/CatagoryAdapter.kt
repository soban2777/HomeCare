package com.example.myapplication.Adapters

import com.example.myapplication.DataClasses.Catagory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.databinding.CatagoryItemBinding

class CatagoryAdapter(
    private val catagorylist: ArrayList<Catagory>,
    private val onItemClick:(Catagory)-> Unit )
    : RecyclerView.Adapter<CatagoryAdapter.CatagoryViewHolder>() {
    class CatagoryViewHolder(val binding: CatagoryItemBinding) : RecyclerView.ViewHolder(binding.root) {




        val imageView: ImageView = itemView.findViewById(R.id.logoiv)
        val textView: TextView = itemView.findViewById(R.id.textiv)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CatagoryViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = CatagoryItemBinding.inflate(layoutInflater, parent, false)
        return CatagoryViewHolder(binding)

    }

    override fun getItemCount(): Int {
       return catagorylist.size
    }

    override fun onBindViewHolder(holder: CatagoryViewHolder, position: Int) {
        val catagory = catagorylist[position]
        holder.imageView.setImageResource(catagory.imageView)
        holder.textView.text = catagory.name
        holder.binding.root.setOnClickListener {
            onItemClick(catagory)

        }


        return
    }
}