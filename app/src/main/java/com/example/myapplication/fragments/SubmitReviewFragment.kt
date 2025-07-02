package com.example.myapplication.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.myapplication.DataClasses.Reviews
import com.example.myapplication.databinding.FragmentReviewBinding
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import java.util.UUID

class SubmitReviewFragment : Fragment() {
    private lateinit var binding: FragmentReviewBinding
    private var workerId: String? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentReviewBinding.inflate(layoutInflater)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.backbtn.setOnClickListener {
        findNavController().navigateUp()
        }
        requireActivity().onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigateUp()
            }
        })


        workerId = arguments?.getString("userId")
        Log.i("workeridd", "onViewCreated: $workerId")

        binding.btnsubmitreview.setOnClickListener {
            submitReview()
        }









    }

    private fun submitReview() {
        val reviewText = binding.reviewet.text.toString().trim()
        val rating = binding.ratingBar.rating

        if (reviewText.isEmpty() || rating == 0f) {
            Toast.makeText(context, "Please enter review and rating", Toast.LENGTH_SHORT).show()
            return
        }

        val reviewId = UUID.randomUUID().toString()
        val reviewerId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val reviewerName = FirebaseAuth.getInstance().currentUser?.displayName ?: ""

        val review = Reviews(reviewId = reviewId, reviewerId = reviewerId, reviewerName =reviewerName , rating = rating, reviewText = reviewText, reviewTime= Timestamp.now(), workerId = workerId.toString())

        val db = FirebaseFirestore.getInstance()
        db.collection("reviews")
            .document(reviewId)
            .set(review)
            .addOnSuccessListener {
                Toast.makeText(context, "Review submitted!", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Failed to submit: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}

