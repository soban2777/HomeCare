package com.example.myapplication.Adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.DataClasses.Workers
import com.example.myapplication.RoomDB.FavoriteAd
import com.example.myapplication.R
import com.example.myapplication.databinding.WorkerItemBinding
import com.google.firebase.firestore.FirebaseFirestore

class FavoritesWorkerAdAdapter(private val favorites: List<FavoriteAd>)
    : RecyclerView.Adapter<FavoritesWorkerAdAdapter.FavoriteAdViewHolder>() {
        var onItemClick : ((FavoriteAd) -> Unit)? = null
    lateinit var onFavoriteClick: (id: String) -> Unit


    class FavoriteAdViewHolder(val binding: WorkerItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun setData(ad: FavoriteAd) {
            val db = FirebaseFirestore.getInstance()
            Log.i("aszafsas", "asfetData: $ad")
            db.collection("workers").document(ad.adId)
                .get()
                .addOnSuccessListener {


                    if (it != null) {

                        val user: Workers? = it.toObject(Workers::class.java)
                        if (user != null) {
                            binding.workeradpfpiv.setImageResource(R.drawable.electrician)
                            binding.workernametv.text = user.name
                            binding.adtitletv.text = user.skill
                            binding.workeradloctv.text = user.location

                        }

                    }
                }
        }


}
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteAdViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = WorkerItemBinding.inflate(layoutInflater, parent, false)
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
            holder.itemView.setOnClickListener {
                onItemClick?.invoke(ad)
                Log.i("qsc", "RvWorkerAd: $ad")
            }
            holder.binding.faviv.setOnClickListener {
                onFavoriteClick(ad.adId)
                holder.binding.faviv.isChecked
            }

        }

    }
