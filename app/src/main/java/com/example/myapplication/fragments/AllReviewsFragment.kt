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
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.Adapters.ReviewsAdapter
import com.example.myapplication.DataClasses.Reviews

import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentAllReviewsBinding

import com.google.firebase.firestore.FirebaseFirestore


class AllReviewsFragment : Fragment() {
    lateinit var binding:FragmentAllReviewsBinding
    private lateinit var reviewList: ArrayList<Reviews>


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAllReviewsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//
        val workerId = arguments?.getString("userId")
        Log.i("asqwasq", "onViewCreated: $workerId")

        binding.backbtn.setOnClickListener(){
            findNavController().navigateUp()
        }
        requireActivity().onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigateUp()
            }
        })

        binding.rvreviews.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)
        reviewList = ArrayList()

        val db= FirebaseFirestore.getInstance()
        db.collection("reviews").whereEqualTo("workerId",workerId)
            .get()
            .addOnSuccessListener {
                if(!it.isEmpty){
                    for(data in it.documents){
                        val user: Reviews? = data.toObject(Reviews::class.java)
                        if (user != null) {
                            reviewList.add(user)
                           val reviewsAdapter = ReviewsAdapter(reviewList)
                            binding.rvreviews.adapter = reviewsAdapter
                        }
                    }
                    }else
                        binding.rvreviews.visibility = View.GONE
                Toast.makeText(requireContext(), "No reviews yet", Toast.LENGTH_SHORT).show()

            }.addOnFailureListener(){
                Log.i("asfqwasq", "onViewCreated: ${it.message}")

            }



//
    }


}