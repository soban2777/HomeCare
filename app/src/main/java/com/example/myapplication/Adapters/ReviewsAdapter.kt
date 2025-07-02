package com.example.myapplication.Adapters

import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.Adapters.CatagoryAdapter.CatagoryViewHolder
import com.example.myapplication.DataClasses.Reviews
import com.example.myapplication.databinding.ReviewItemBinding
import java.util.Date

class ReviewsAdapter(private val reviewList:ArrayList<Reviews>):
    RecyclerView.Adapter<ReviewsAdapter.ReviewsViewHolder>() {
    class ReviewsViewHolder(val binding: ReviewItemBinding) :
        RecyclerView.ViewHolder(binding.root){


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewsViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ReviewItemBinding.inflate(layoutInflater, parent, false)
        return ReviewsViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return reviewList.size
    }

    override fun onBindViewHolder(holder: ReviewsViewHolder, position: Int) {
        val review = reviewList[position]
        holder.binding.reviewerName.text = review.reviewerName
        holder.binding.reviewText.text = review.reviewText
        holder.binding.ratingBarSmall.rating = review.rating.toFloat()
        holder.binding.reviewTime.text = getTimeAgo(review.reviewTime?.toDate())

    }

    private fun getTimeAgo(date: Date?): String {
        return DateUtils.getRelativeTimeSpanString(
            date?.time ?: 0L,
            System.currentTimeMillis(),
            DateUtils.MINUTE_IN_MILLIS
        ).toString()
    }
}


