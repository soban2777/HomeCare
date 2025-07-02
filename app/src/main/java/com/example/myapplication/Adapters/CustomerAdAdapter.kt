package com.example.myapplication.Adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.DataClasses.CustomerPostAd
import com.example.myapplication.R

class CustomerAdAdapter(private val customerAdList: ArrayList<CustomerPostAd>)

    :RecyclerView.Adapter<CustomerAdAdapter.AdViewHolder>(){
        lateinit var onFavoriteClick: (id: String) -> Unit
    var onItemClick : ((CustomerPostAd) -> Unit)? = null
    class AdViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val imageView: ImageView = itemView.findViewById(R.id.logoiv)
        val title: TextView = itemView.findViewById(R.id.titletv)
        val location: TextView = itemView.findViewById(R.id.loctv)
        val date: TextView = itemView.findViewById(R.id.datetv)
        val favorite: CheckBox = itemView.findViewById(R.id.faviv)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdViewHolder {
       val view = LayoutInflater.from(parent.context).inflate(R.layout.customer_item, parent, false)
        return AdViewHolder(view)
    }

    override fun getItemCount(): Int {
        return customerAdList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: AdViewHolder, position: Int) {
        val customerAd = customerAdList[position]
        holder.title.text = customerAd.title
        holder.location.text = customerAd.location
        holder.date.text = customerAd.date
        holder.itemView.setOnClickListener {
            onItemClick?.invoke(customerAd)
        }

        holder.favorite.setOnClickListener {
            onFavoriteClick(customerAd.id)
            holder.favorite.isChecked
            notifyDataSetChanged()

          //  Toast.makeText(holder.itemView.context, "Added to favorites", Toast.LENGTH_SHORT).show()
        }


        }

    }

