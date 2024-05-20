package com.example.swiftcash.UserActivity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.example.swiftcash.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Date

class SendActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_send)

        val intent = intent
        val selectedEmail = intent.getStringExtra("selectedEmail")
        val sendUserTv = findViewById<TextView>(R.id.sendUser)
        val mSendAmt = findViewById<EditText>(R.id.sendAmount)
        val msendBtn = findViewById<ImageView>(R.id.sendButton)

        msendBtn.setOnClickListener {
            val userEmail = FirebaseAuth.getInstance().currentUser?.email
            val db = FirebaseFirestore.getInstance()

            val mSendAmtString = mSendAmt.text.toString()
            val mSendAmtInt: Int = mSendAmtString.toInt()

            val newTransaction = hashMapOf(
                "amount" to mSendAmtInt,
                "receiverID" to selectedEmail,
                "senderID" to userEmail,
                "time" to Date()
            )

            var userBalance: Long = 0
            userEmail?.let { email ->
                db.collection(email).document("userDetails").get()
                    .addOnSuccessListener { documentSnapshot ->
                        if (documentSnapshot.exists()) {
                            userBalance = documentSnapshot.getLong("accountbalance") ?: 0
                            if (userBalance >= mSendAmtInt) {
                                userBalance -= mSendAmtInt
                                db.collection(email).document("userDetails")
                                    .update("accountbalance", userBalance)
                                    .addOnSuccessListener {
                                    }
                                    .addOnFailureListener { exception ->
                                    }
                                db.collection(email).document("userTransactions")
                                    .update(
                                        "transactions",
                                        FieldValue.arrayUnion(newTransaction),
                                        "time",
                                        FieldValue.serverTimestamp()
                                    )
                                    .addOnSuccessListener {
                                        Toast.makeText(applicationContext, "Transaction Successful", Toast.LENGTH_LONG).show()
                                        finish()
                                    }
                                    .addOnFailureListener { exception ->
                                        Toast.makeText(applicationContext, "Transaction Failed", Toast.LENGTH_LONG).show()
                                    }
                                selectedEmail?.let { recipientEmail ->
                                    db.collection(recipientEmail).document("userTransactions")
                                        .update(
                                            "transactions",
                                            FieldValue.arrayUnion(newTransaction),
                                            "time",
                                            FieldValue.serverTimestamp()
                                        )
                                        .addOnSuccessListener {
                                        }
                                        .addOnFailureListener { exception ->
                                        }
                                }
                                var selectedUserBalance: Long = 0
                                selectedEmail?.let { recipientEmail ->
                                    db.collection(recipientEmail).document("userDetails").get()
                                        .addOnSuccessListener { documentSnapshot ->
                                            if (documentSnapshot.exists()) {
                                                selectedUserBalance = documentSnapshot.getLong("accountbalance") ?: 0
                                                selectedUserBalance += mSendAmtInt
                                                db.collection(recipientEmail).document("userDetails")
                                                    .update("accountbalance", selectedUserBalance)
                                                    .addOnSuccessListener {
                                                    }
                                                    .addOnFailureListener { exception ->
                                                    }
                                            }
                                        }
                                        .addOnFailureListener { exception ->
                                        }
                                }

                            } else {
                                Toast.makeText(applicationContext, "Insufficient Balance", Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                    .addOnFailureListener { exception ->
                    }
            }

        }

        sendUserTv.text = selectedEmail
    }
}
