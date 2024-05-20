package com.example.swiftcash.UserAuth
import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.swiftcash.R
import com.example.swiftcash.UserActivity.UserActivity
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val metEmail = findViewById<EditText>(R.id.etEmail)
        val metPass = findViewById<EditText>(R.id.etPass)
        val mbtnLogin = findViewById<Button>(R.id.btnLogin)
        val mtxtNewUser = findViewById<TextView>(R.id.txtCreateUser)

        mtxtNewUser.setOnClickListener {
            startActivity(Intent(this, Registration::class.java))
        }

        mbtnLogin.setOnClickListener {
            val email = metEmail.text.toString().trim()
            val password = metPass.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
        }

//        FirebaseAuth.getInstance().signInWithEmailAndPassword("aashay@sc.com", "aashay")
//            .addOnCompleteListener { task ->
//                if (task.isSuccessful) {
//                    Toast.makeText(this, "Login Success", Toast.LENGTH_LONG).show()
//                    startActivity(Intent(this, UserActivity::class.java))
//                } else {
//                    Toast.makeText(this, "Login Failed", Toast.LENGTH_LONG).show()
//                    Log.e("Login", "signInWithEmailAndPassword:failure", task.exception)
//                }
//            }

    }
}
