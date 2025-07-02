package com.example.myapplication.Adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.anytimeshoot.anytimeshoot.models.MessageModel
import com.example.myapplication.databinding.ItemReceiverChatBinding
import com.example.myapplication.databinding.ItemSenderChatBinding
import com.example.myapplication.fragments.MessageFragment
import java.text.SimpleDateFormat
import java.util.*

class MessagesAdapter(
    private val fragment: MessageFragment,
    val callback: (item: MessageModel) -> Unit
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == 0) {
            SenderViewHolder(
                ItemSenderChatBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        } else {
            ReceiverViewHolder(
                ItemReceiverChatBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (differ.currentList[position].senderId == fragment.senderId) {
            (holder as SenderViewHolder).bindView(differ.currentList[position])
            holder.binding.timeTv.text = convertLongToTime(differ.currentList[position].timeStamp)


        } else {
            (holder as ReceiverViewHolder).bindView(differ.currentList[position])
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (differ.currentList[position].senderId == fragment.senderId) {
            0
        } else {
            1
        }
    }

    inner class SenderViewHolder(val binding: ItemSenderChatBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindView(item: MessageModel) {

            binding.msgTv.text=item.msg
            binding.timeTv.text= convertLongToTime(differ.currentList[position].timeStamp)

        }

    }

    inner class ReceiverViewHolder(private val binding: ItemReceiverChatBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindView(item: MessageModel) {
            binding.msgTv.text=item.msg
            binding.timeTv.text= convertLongToTime(differ.currentList[position].timeStamp)

        }

    }


    private val diffCallBack = object : DiffUtil.ItemCallback<MessageModel>() {
        override fun areItemsTheSame(oldItem: MessageModel, newItem: MessageModel): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: MessageModel, newItem: MessageModel): Boolean {
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