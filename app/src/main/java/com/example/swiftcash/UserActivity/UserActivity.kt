package com.example.swiftcash.UserActivity

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.swiftcash.R
import com.example.swiftcash.Misc.TransactionAdapter
import com.example.swiftcash.Misc.Transactions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class UserActivity : AppCompatActivity() {
    private lateinit var userBalanceTextView: TextView
    private lateinit var userTransactions: TextView
    private lateinit var recyclerViewTransactions: RecyclerView
    private lateinit var transactionAdapter: TransactionAdapter
    private lateinit var autoCompleteTextView: AutoCompleteTextView

    private val items = mutableListOf<String>()

    override fun onResume() {
        super.onResume()
        fetchUserDetails()
        fetchTransactions()
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        AlertDialog.Builder(this)
            .setTitle("Logout")
            .setMessage("Are you sure you want to logout?")
            .setPositiveButton("Yes") { _: DialogInterface, _: Int ->
                FirebaseAuth.getInstance().signOut()
                super.onBackPressed()
            }
            .setNegativeButton("No") { dialogInterface: DialogInterface, i: Int ->
                dialogInterface.dismiss()
            }
            .show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)

        autoCompleteTextView = findViewById(R.id.autoCompleteTextView)
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, items)
        autoCompleteTextView.setAdapter(adapter)

        userBalanceTextView = findViewById(R.id.user_balance)
        userTransactions = findViewById(R.id.user_trans)
        recyclerViewTransactions = findViewById(R.id.recyclerViewTransactions)

        recyclerViewTransactions.layoutManager = LinearLayoutManager(this)
        transactionAdapter = TransactionAdapter(emptyList())
        recyclerViewTransactions.adapter = transactionAdapter

        fetchUserDetails()
        fetchTransactions()
        fetchAllEmails()

        autoCompleteTextView.setOnItemClickListener { parent, _, position, _ ->
            val selectedEmail = parent.getItemAtPosition(position) as String
            val intent = Intent(this, SendActivity::class.java)
            intent.putExtra("selectedEmail", selectedEmail)
            startActivity(intent)
        }
    }

    private fun fetchAllEmails() {
        val db = FirebaseFirestore.getInstance()

        db.collection("AllUsers").document("all_app_users")
            .get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val allEmails = documentSnapshot.get("allemails") as? List<String>
                    if (allEmails != null) {
                        for (email in allEmails) {
                            items.add(email)
                        }
                    }
                }
            }
            .addOnFailureListener { exception ->
            }
    }

    @SuppressLint("SetTextI18n")
    private fun fetchUserDetails() {
        val userEmail = FirebaseAuth.getInstance().currentUser?.email

        userEmail?.let { email ->
            val db = FirebaseFirestore.getInstance()

            db.collection(email).document("userDetails").get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        val userName = documentSnapshot.getString("username") ?: "N/A"
                        val userBalance = documentSnapshot.getLong("accountbalance") ?: 0
                        userBalanceTextView.text = "$$userBalance"
                    }
                }
                .addOnFailureListener { exception ->
                }
        }
    }

    private fun fetchTransactions() {
        val userEmail = FirebaseAuth.getInstance().currentUser?.email

        userEmail?.let { email ->
            val db = FirebaseFirestore.getInstance()

            db.collection(email).document("userTransactions").get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        val transactions = documentSnapshot.get("transactions") as? List<Map<String, Any>>

                        val transactionList = mutableListOf<Transactions>()
                        transactions?.let {
                            for (transactionMap in it) {
                                val amount = transactionMap["amount"] as Long
                                val receiverID = transactionMap["receiverID"] as String
                                val senderID = transactionMap["senderID"] as String
                                val time = transactionMap["time"] as com.google.firebase.Timestamp

                                val transaction = Transactions(amount, senderID, receiverID, time.toDate())
                                transactionList.add(transaction)
                            }
                            transactionAdapter.updateTransactions(transactionList)
                        }
                        userTransactions.text = transactionList.size.toString()
                    }
                }
                .addOnFailureListener { exception ->
                }
        }
    }
}
