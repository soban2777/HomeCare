package com.example.myapplication.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.PopupWindow
import android.widget.RelativeLayout
import androidx.activity.OnBackPressedCallback
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.anytimeshoot.anytimeshoot.models.MessageModel
import com.example.myapplication.Adapters.MessagesAdapter
import com.example.myapplication.DataClasses.userType
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentMessageBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


class MessageFragment : Fragment() {

    lateinit var binding: FragmentMessageBinding
    var senderId = ""
    private var receiverId = ""
    private var senderRoom = ""
    private var receiverRoom = ""
    private var adId: String = ""
    private lateinit var messageAdapter: MessagesAdapter
    private val msgList: ArrayList<MessageModel> by lazy { arrayListOf() }
    private lateinit var listener: ListenerRegistration
    private var receiverToken: String = ""
    private var senderName: String = ""
    private var db = FirebaseFirestore.getInstance()
    private var auth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentMessageBinding.inflate(layoutInflater)
        setStatusBarColorStart()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupAdapter()
        arguments?.getString("userId")?.let { id ->

            db.collection("users").document(id).get().addOnSuccessListener { doc ->
                Log.i("fdhs", "onViewCreated: $id")
                val user = doc.toObject(userType::class.java)
                user?.let {
//                    if (it.img.isNotEmpty()){
//                        Picasso.get().load(it.img).error(R.color.bg_gray).placeholder(R.color.gray).into(binding.userPic)
//                    }
                    binding.userNameTv.text = it.name
                }
            }
            receiverId = id

            auth.currentUser?.let {
                senderId = it.uid
                senderRoom = senderId + receiverId
                receiverRoom = receiverId + senderId
                getAllMessages()
                db.collection("users").document(it.uid).get().addOnSuccessListener { doc ->
                    val user = doc.toObject(userType::class.java)
                    user?.let {
                        senderName = user.name
                    }
                }
            }
        }
        arguments?.getString("adId")?.let {
            adId = it
        }

        binding.ivBack.setOnClickListener {
            setStatusBarColorEnd()
            findNavController().popBackStack()
        }


        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    setStatusBarColorEnd()
                    findNavController().popBackStack()
                }

            })

        binding.sendBtn.setOnClickListener {

                if (binding.messageEt.text.trim().isNotEmpty()) {


                    val msg = MessageModel(
                        binding.messageEt.text.toString(),
                        senderId,
                        receiverId,
                        System.currentTimeMillis()
                    )
                    msgList.add(msg)
                    messageAdapter.differ.submitList(msgList)
                    messageAdapter.notifyItemInserted(msgList.size - 1)
                    binding.messageEt.text.clear()
                    binding.messagesRv.smoothScrollToPosition(msgList.size - 1)
                    uploadMessageDatabase(msg)

                }

        }
    }



    private fun deleteChat() {
        deleteAllChatMsg()
        db.collection("users").document(senderId).collection("chatForAds").document(receiverId)
            .delete().addOnSuccessListener {
                setStatusBarColorEnd()
                findNavController().popBackStack()
            }


    }

    private fun deleteAllChatMsg() {
        CoroutineScope(Dispatchers.IO).launch {
            db.collection("chats").document(auth.currentUser?.uid.toString())
                .collection(senderId + receiverId).get().addOnSuccessListener { qs ->
                    qs?.let {
                        for (qds: QueryDocumentSnapshot in qs) {
                            val id = qds.id
                            db.collection("chats")
                                .document(auth.currentUser?.uid.toString())
                                .collection(senderId + receiverId).document(id).delete()
                        }
                    }
                }
        }
    }

    private fun setupAdapter() {
        messageAdapter = MessagesAdapter(this) {}
        binding.messagesRv.adapter = messageAdapter
        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
    }

    private fun uploadMessageDatabase(msg: MessageModel) {
        val ad = hashMapOf("chatAd" to adId)

        db.collection("users").document(receiverId)
            .collection("chatForAds").document(senderId)
            .set(ad).addOnSuccessListener {
                db.collection("users").document(senderId)
                    .collection("chatForAds").document(receiverId)
                    .set(ad).addOnSuccessListener {
                        db.collection("chats").document(senderId)
                            .collection(senderRoom).document()
                            .set(msg).addOnSuccessListener {
                                db.collection("chats").document(receiverId)
                                    .collection(receiverRoom).document().set(msg)
                            }
                    }
            }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun getAllMessages() {
        listener = db.collection("chats").document(senderId).collection(senderRoom)
         .addSnapshotListener { query, _ ->
                query?.let {
                    msgList.clear()
                    query.documents.forEach { doc ->
                        val msg = doc.toObject(MessageModel::class.java)
                        msg?.let {
                            msgList.add(it)
                        }
                    }
                    msgList.sortBy {
                        it.timeStamp
                    }
                    messageAdapter.differ.submitList(msgList)
                    messageAdapter.notifyDataSetChanged()
                    if (msgList.isNotEmpty()) {
                        binding.messagesRv.smoothScrollToPosition(msgList.size - 1)
                    }
                }

            }
    }

    override fun onDestroyView() {

        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        super.onDestroyView()
    }

    private fun setStatusBarColorStart() {
        activity?.apply {
            val window: Window = this.window
            window.statusBarColor = ResourcesCompat.getColor(resources, R.color.yellow, null)
            window.navigationBarColor = ResourcesCompat.getColor(resources, R.color.msgNav, null)
        }
    }

    private fun setStatusBarColorEnd() {

        activity?.apply {
            val window: Window = this.window
            window.statusBarColor = ResourcesCompat.getColor(resources, R.color.yellow, null)
            window.navigationBarColor = ResourcesCompat.getColor(resources, R.color.bottomNav, null)
        }
    }

}