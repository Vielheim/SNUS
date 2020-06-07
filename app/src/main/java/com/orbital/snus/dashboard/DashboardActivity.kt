package com.orbital.snus.dashboard

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import androidx.databinding.DataBindingUtil
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuth.AuthStateListener
import com.orbital.snus.R
import com.orbital.snus.databinding.ActivityDashboardBinding
import com.orbital.snus.groups.GroupsActivity
import com.orbital.snus.messages.MessagesActivity
import com.orbital.snus.modules.ModulesActivity
import com.orbital.snus.opening.MainActivity
import com.orbital.snus.profile.ProfileActivity
import java.util.*

class DashboardActivity : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var binding: ActivityDashboardBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        firebaseAuth = FirebaseAuth.getInstance()
        // If no user logged in, transfer to opening screen
        if (firebaseAuth.currentUser == null) {
            startActivity(Intent(applicationContext, MainActivity::class.java))
        }
        binding = DataBindingUtil.setContentView<ActivityDashboardBinding>(this, R.layout.activity_dashboard)

        binding.bottomNavigationMenu.menu.findItem(R.id.ic_action_home).setChecked(true)
        binding.bottomNavigationMenu.setOnNavigationItemSelectedListener {
            menuItem: MenuItem ->
            when (menuItem.itemId) {
                R.id.ic_action_home -> {}
                R.id.ic_action_profile -> {
                    startActivity(Intent(applicationContext, ProfileActivity::class.java))
                    finish()
                }
                R.id.ic_action_messages -> {
                    startActivity(Intent(applicationContext, MessagesActivity::class.java))
                    finish()
                }
                R.id.ic_action_groups -> {
                    startActivity(Intent(applicationContext, GroupsActivity::class.java))
                    finish()
                }
                R.id.ic_action_modules -> {
                    startActivity(Intent(applicationContext, ModulesActivity::class.java))
                    finish()
                }
            }
            true
        }
    }

//    fun setDate(v: EditText) {
//        val c = Calendar.getInstance()
//        val mYear = c[Calendar.YEAR]
//        val mMonth = c[Calendar.MONTH]
//        val mDay = c[Calendar.DAY_OF_MONTH]
//        val datePickerDialog = DatePickerDialog(
//            this,
//            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
//                v.setText(
//                    dayOfMonth.toString() + "-" + (monthOfYear + 1) + "-" + year
//                )
//            }, mYear, mMonth, mDay
//        )
//        datePickerDialog.show()
//    }
//
//    fun setTime(v: EditText) {
//        // Get Current Time
//        val c = Calendar.getInstance()
//        val mHour = c[Calendar.HOUR_OF_DAY]
//        val mMinute = c[Calendar.MINUTE]
//        val timePickerDialog = TimePickerDialog(
//            this,
//            TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute -> v.setText("$hourOfDay:$minute") },
//            mHour,
//            mMinute,
//            false
//        )
//        timePickerDialog.show()
//
//    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }
}