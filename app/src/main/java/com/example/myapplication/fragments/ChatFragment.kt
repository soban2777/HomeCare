package com.example.myapplication.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.anytimeshoot.anytimeshoot.models.ChatModel
import com.example.myapplication.Adapters.ChatsAdapter
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentChatBinding
import com.example.myapplication.databinding.FragmentMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage


class ChatFragment : Fragment() {

    lateinit var binding: FragmentChatBinding
    private lateinit var chatAdapter: ChatsAdapter
    private val chatList: ArrayList<ChatModel> by lazy { arrayListOf() }
    private var mRootView: ViewGroup? = null
    private var mIsFirstLoad = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        if (mRootView == null) {
            binding = FragmentChatBinding.inflate(layoutInflater)
            mRootView = binding.root
            mIsFirstLoad = true
        } else {
            mIsFirstLoad = false
        }
        return mRootView as ViewGroup
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (mIsFirstLoad){
            setupAdapter()
            getAllChats()
            binding.str.setColorSchemeResources(R.color.yellow)
            binding.str.setOnRefreshListener {
                getAllChats()
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun getAllChats() {
        val db = FirebaseFirestore.getInstance()
        val auth = FirebaseAuth.getInstance()
        db.collection("users").document(auth.currentUser?.uid.toString())
            .collection("chatForAds").addSnapshotListener { query, error ->
                query?.let {
                    binding.str.isRefreshing = false
                    chatList.clear()
                    query.documents.forEach {
                        val chat = ChatModel(it.id, it.getString("chatAd") ?: "")
                        chatList.add(chat)
                    }
                    if (chatList.isEmpty()) {
                        binding.phView.visibility=View.VISIBLE
                        binding.chatsRv.visibility=View.GONE
                    } else {
                        binding.chatsRv.visibility=View.VISIBLE
                        binding.phView.visibility=View.GONE
                        chatAdapter.differ.submitList(chatList)
                        chatAdapter.notifyDataSetChanged()
                    }

                }


            }
    }

    private fun setupAdapter() {
        chatAdapter = ChatsAdapter(this) {
            Log.i("lkjhh", "setupAdapter: ${it.adId}")
            Log.i("lkdfjhh", "setupAdapter: ${it.userId}")
            findNavController().navigate(R.id.messageFragment, Bundle().apply {
                this.putString("userId", it.userId)
                this.putString("adId", it.adId)
            })
        }
        binding.chatsRv.adapter = chatAdapter
    }
}