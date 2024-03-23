package com.example.swiftcash.UserAuth

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.swiftcash.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class Registration : AppCompatActivity() {
    private lateinit var etEmail: EditText
    private lateinit var etPass: EditText
    private lateinit var etOpenAccBal: EditText
    private lateinit var btnSignup: Button

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        etEmail = findViewById(R.id.etEmail)
        etPass = findViewById(R.id.etPass)
        btnSignup = findViewById(R.id.btnSignup)
        etOpenAccBal = findViewById(R.id.etOpenAccBal)

        btnSignup.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPass.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val uid = FirebaseAuth.getInstance().currentUser?.uid
                        uid?.let { userId ->
                            val db = FirebaseFirestore.getInstance()

                            val allUsersCollection = db.collection("AllUsers")

                            allUsersCollection.document("all_app_users").update("allemails", FieldValue.arrayUnion(email))

                            val userCollection = db.collection(email)

                            val mOpenBal: Int = etOpenAccBal.text.toString().toInt()

                            val userDetails = hashMapOf(
                                "userid" to email,
                                "username" to email.substringBefore('@'),
                                "accountbalance" to mOpenBal
                            )
                            userCollection.document("userDetails").set(userDetails)
                                .addOnSuccessListener {

                                    val transactionDetails = hashMapOf(
                                        "transactions" to arrayListOf<HashMap<String, Any>>(
                                            hashMapOf(
                                                "senderID" to "first deposit",
                                                "receiverID" to email,
                                                "amount" to mOpenBal,
                                                "time" to Date()
                                            )
                                        )
                                    )
                                    userCollection.document("userTransactions").set(transactionDetails)
                                        .addOnSuccessListener {
                                            Toast.makeText(this, "Registration Successful", Toast.LENGTH_SHORT).show()
                                            finish()
                                        }
                                        .addOnFailureListener { exception ->
                                            Toast.makeText(this, "Registration Failed: ${exception.message}", Toast.LENGTH_SHORT).show()
                                        }
                                }
                                .addOnFailureListener { exception ->
                                    Toast.makeText(this, "Registration Failed: ${exception.message}", Toast.LENGTH_SHORT).show()
                                }
                        } ?: run {
                            Toast.makeText(this, "Registration Failed: User ID is null", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this, "Registration Failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }
}
