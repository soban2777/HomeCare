package com.example.myapplication.Adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.anytimeshoot.anytimeshoot.models.ChatModel
import com.anytimeshoot.anytimeshoot.models.MessageModel
import com.example.myapplication.DataClasses.userType
import com.example.myapplication.databinding.ItemUserChatBinding
import com.example.myapplication.fragments.ChatFragment

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class ChatsAdapter(
    private val fragment: ChatFragment, val callback: (item: ChatModel) -> Unit
) : RecyclerView.Adapter<ChatsAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemUserChatBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindView(differ.currentList[position])
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    inner class ViewHolder(private val binding: ItemUserChatBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bindView(item: ChatModel) {
            val db= FirebaseFirestore.getInstance()
            val auth = FirebaseAuth.getInstance()

            db.collection("users").document(item.userId).get()
                .addOnSuccessListener { doc ->
                    Log.i("uuuuuu", "bindView: ${doc.id}")
                    val user = doc.toObject(userType::class.java)
                    user?.let {
                        binding.userName.text = user.name

//                        if (user.img.isNotEmpty()) {
//                            Picasso.get().load(user.img).placeholder(ShimmerDrawable().apply {
//                                setShimmer(
//                                    Shimmer.AlphaHighlightBuilder().setDuration(800)
//                                        .setBaseAlpha(0.97f).setHighlightAlpha(0.9f)
//                                        .setDirection(Shimmer.Direction.LEFT_TO_RIGHT)
//                                        .setAutoStart(true).build()
//                                )
//                            }).into(binding.userImg)
//                        }
                        getLastMsg(
                            auth.currentUser?.uid.toString(),
                            doc.id,
                            binding.lasMsgTv,
                            binding.msgTime

                        )
                    }
                }
            binding.root.setOnClickListener {
                callback(item)


            }

        }

        private fun getLastMsg(
            senderId: String, receiverId: String, lastMsg: TextView, msgTime: TextView
        ) {
            val db= FirebaseFirestore.getInstance()
            val auth = FirebaseAuth.getInstance()
           db.collection("chats")
                .document(auth.currentUser?.uid.toString())
                .collection(senderId + receiverId).orderBy("timeStamp").limit(1).get()
                .addOnSuccessListener {
                    it.documents.forEach { doc ->
                        val msg = doc.toObject(MessageModel::class.java)
                        msg?.let { model ->
                            lastMsg.text = model.msg
                            msgTime.text = convertLongToTime(model.timeStamp)
                        }
                    }
                }
        }


    }

    private val diffCallBack = object : DiffUtil.ItemCallback<ChatModel>() {
        override fun areItemsTheSame(oldItem: ChatModel, newItem: ChatModel): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: ChatModel, newItem: ChatModel): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, diffCallBack)
    fun convertLongToTime(time: Long): String {
        val date = Date(time)
        val format = SimpleDateFormat("HH:mm", Locale.getDefault())
        return format.format(date)
    }


}