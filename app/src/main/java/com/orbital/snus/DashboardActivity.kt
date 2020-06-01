package com.orbital.snus

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuth.AuthStateListener
import com.orbital.snus.opening.MainActivity

class DashboardActivity : AppCompatActivity() {

    private lateinit var logout_button: Button
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var authStateListener: AuthStateListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        firebaseAuth = FirebaseAuth.getInstance()

        // If no user logged in, transfer to opening screen
        if (firebaseAuth.currentUser == null) {
            startActivity(Intent(applicationContext, MainActivity::class.java))
        }

        setContentView(R.layout.activity_dashboard)

        logout_button = findViewById(R.id.logout_button)

        logout_button.setOnClickListener {
            firebaseAuth.signOut()
            startActivity(Intent(applicationContext, MainActivity::class.java))
            finish()
        }
    }
}
