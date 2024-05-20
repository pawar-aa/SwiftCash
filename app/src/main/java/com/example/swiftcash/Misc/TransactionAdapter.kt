package com.example.swiftcash.Misc

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.swiftcash.R
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.*

class TransactionAdapter(private var transactions: List<Transactions>) : RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {

    inner class TransactionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewType: TextView = itemView.findViewById(R.id.textViewType)
        val textViewAmount: TextView = itemView.findViewById(R.id.textViewAmount)
        val textViewUserID: TextView = itemView.findViewById(R.id.textViewUserID)
        val textViewDate: TextView = itemView.findViewById(R.id.textViewDate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.transaction_item, parent, false)
        return TransactionViewHolder(view)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val transaction = transactions[position]
        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
        holder.textViewDate.text = dateFormat.format(transaction.time)
        holder.textViewAmount.text = "${transaction.amount}"

        if (transaction.senderID == FirebaseAuth.getInstance().currentUser?.email) {
            holder.textViewType.text = "Got"
            holder.textViewType.setTextColor(Color.parseColor("#1b263b"))
            holder.textViewUserID.text = transaction.receiverID
        } else {
            holder.textViewType.text = "Sent"
            holder.textViewType.setTextColor(Color.parseColor("#778da9"))
            holder.textViewUserID.text = transaction.senderID
        }
    }

    override fun getItemCount(): Int {
        return transactions.size
    }

    fun updateTransactions(newTransactions: MutableList<Transactions>) {
        transactions = newTransactions
        notifyDataSetChanged()
    }
}
