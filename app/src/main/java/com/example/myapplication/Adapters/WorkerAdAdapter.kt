package com.example.myapplication.Adapters

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.DataClasses.Reviews
import com.example.myapplication.DataClasses.Workers
import com.example.myapplication.R
import com.example.myapplication.databinding.WorkerItemBinding
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.properties.Delegates

class WorkerAdAdapter(private val workerAdList: ArrayList<Workers>)
    : RecyclerView.Adapter<WorkerAdAdapter.AdViewHolder>(){
    lateinit var onFavoriteClick: (id: String) -> Unit
    var onItemClick : ((Workers) -> Unit)? = null

    class AdViewHolder(val binding: WorkerItemBinding) : RecyclerView.ViewHolder(binding.root){
        private lateinit var reviewList: ArrayList<Reviews>

        val workername: TextView = itemView.findViewById(R.id.workernametv)
        val title: TextView = itemView.findViewById(R.id.adtitletv)
        val location: TextView = itemView.findViewById(R.id.workeradloctv)
        val favorite: CheckBox = itemView.findViewById(R.id.faviv)



        fun reviewSize(workers: Workers) {

            val db = FirebaseFirestore.getInstance()
        reviewList = ArrayList()
        db.collection("reviews").whereEqualTo("workerId",workers.id ).get()
        .addOnSuccessListener {
            if (!it.isEmpty) {
                for (data in it.documents) {
                    Log.i("jhvjfdfx", "onViewCreated: $data")
                    val review: Reviews? = data.toObject(Reviews::class.java)
                    if (review != null) {
                        reviewList.add(review)
                        Log.i("qwwewer", "onViewCreated: $review")
                    }
                    reviewList.size
                    binding.tvNumberOfReviews.text = "(${reviewList.size} reviews)"



                }

            }
        }

    }
        fun averageRating(workers: Workers) {
            val db = FirebaseFirestore.getInstance()
            reviewList.clear()
            reviewList = ArrayList()
            db.collection("reviews").whereEqualTo("workerId",workers.id ).get()
                .addOnSuccessListener {
                    if (!it.isEmpty) {
                        for (data in it.documents) {
                            Log.i("jhvjfdfx", "onViewCreated: $data")
                            val review: Reviews? = data.toObject(Reviews::class.java)
                            if (review != null) {
                                reviewList.add(review)
                                Log.i("qwwewer", "onViewCreated: $review")
                            }
                            reviewList.size
                            binding.tvAverageRating.text = calculateAverageRating().toString()
                        }


                        }
                }

        }
        fun calculateAverageRating(): Float {

            Log.i("jhfvjfdfx", "onViewCreated: $reviewList")
            return if (reviewList.size > 0) {
                var totalRatingSum = 0.0f
                for (rating in reviewList) {
                    totalRatingSum += rating.rating
                }
                totalRatingSum / reviewList.size
            } else {
                0.0f
            }
        }
    }

    @SuppressLint("SuspiciousIndentation")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdViewHolder {
     val view = LayoutInflater.from(parent.context).inflate(R.layout.worker_item, parent, false)
        return AdViewHolder(binding = WorkerItemBinding.bind(view))
    }

    override fun getItemCount(): Int {
        return workerAdList.size
    }

    @SuppressLint("NotifyDataSetChanged", "SetTextI18n")
    override fun onBindViewHolder(holder: AdViewHolder, position: Int) {
        val Workers = workerAdList[position]
        holder.title.text = Workers.skill
        Log.i("qwer", "onBindViewHolder: ${Workers.skill}")
        holder.workername.text = Workers.name
        holder.location.text = Workers.location
        holder.reviewSize(Workers)
        holder.averageRating(Workers)

        // Check if the ad is a favorite and update the checkbox
        holder.itemView.setOnClickListener {
            onItemClick?.invoke(Workers)
            Log.i("qsc", "RvWorkerAd: $Workers")
        }
        holder.favorite.setOnClickListener {
            onFavoriteClick(Workers.id)
            holder.favorite.isChecked
            notifyDataSetChanged()

            //  Toast.makeText(holder.itemView.context, "Added to favorites", Toast.LENGTH_SHORT).show()
        }
        return
    }







}