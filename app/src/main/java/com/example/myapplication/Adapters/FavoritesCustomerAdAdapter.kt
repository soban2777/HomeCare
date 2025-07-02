package com.example.myapplication.Adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.DataClasses.customerAd
import com.example.myapplication.RoomDB.FavoriteAd
import com.example.myapplication.R
import com.example.myapplication.databinding.CustomerItemBinding
import com.google.firebase.firestore.FirebaseFirestore

class FavoritesCustomerAdAdapter(private val favorites: List<FavoriteAd>)
    : RecyclerView.Adapter<FavoritesCustomerAdAdapter.FavoriteAdViewHolder>() {
        lateinit var onFavoriteClick: (id: String) -> Unit
        var onItemClick: ((FavoriteAd) -> Unit)? = null



    class FavoriteAdViewHolder(val binding: CustomerItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun setData(ad: FavoriteAd) {
            val db = FirebaseFirestore.getInstance()
            Log.i("aszasas", "asetData: $ad")
            db.collection("customerAds").document(ad.adId)
                .get()
                .addOnSuccessListener {


                    if (it != null) {

                        val user: customerAd? = it.toObject(customerAd::class.java)
                        if (user != null) {
                            binding.logoiv.setImageResource(R.drawable.electrician)
                            binding.titletv.text = user.title
                            binding.datetv.text = user.date
                            binding.loctv.text = user.location

                        }

                    }
                }
        }


}
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteAdViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = CustomerItemBinding.inflate(layoutInflater, parent, false)
            return FavoriteAdViewHolder(binding)
        }

        override fun getItemCount(): Int {
            return favorites.size
        }

        override fun onBindViewHolder(holder: FavoriteAdViewHolder, position: Int) {
            val ad = favorites[position]
            val db = FirebaseFirestore.getInstance()
            Log.i("aszasas", "ss: $ad")
            holder.setData(ad)
            holder.itemView.setOnClickListener(){
                onItemClick?.invoke(ad)
            }
            holder.binding.faviv.setOnClickListener {
                onFavoriteClick(ad.adId)
                holder.binding.faviv.isChecked
            }


        }


    }
