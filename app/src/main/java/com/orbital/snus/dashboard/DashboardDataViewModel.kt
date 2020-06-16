package com.orbital.snus.dashboard

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.orbital.snus.data.UserEvent

class DashboardDataViewModel : ViewModel() {
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    private val _addSuccess = MutableLiveData<Boolean?>()
    val addSuccess : LiveData<Boolean?>
        get() = _addSuccess

    private val _addFailure = MutableLiveData<Exception?>()
    val addFailure : LiveData<Exception?>
        get() = _addFailure

    fun addEvent(event: UserEvent) {
        // start to add inside database
        val id = db.collection("users") // users collection
            .document(firebaseAuth.currentUser!!.uid) // current userId
            .collection("events") // user events collection
            .document().id // event document with auto-generated key

        event.id = id

        db.collection("users") // users collection
            .document(firebaseAuth.currentUser!!.uid) // current userId
            .collection("events") // user events collection
            .document(id).set(event)
            .addOnSuccessListener {
                _addSuccess.value = true
            }.addOnFailureListener {
                _addFailure.value = it
            }
    }

    fun addEventSuccessCompleted() {
        _addSuccess.value = null
    }

    fun addEventFailureCompleted() {
        _addFailure.value = null
    }

    private val _delSuccess = MutableLiveData<Boolean?>()
    val delSuccess : LiveData<Boolean?>
        get() = _delSuccess

    private val _delFailure = MutableLiveData<Exception?>()
    val delFailure : LiveData<Exception?>
        get() = _delFailure

    fun deleteEvent(ID: String) {
        db.collection("users") // users collection
            .document(firebaseAuth.currentUser!!.uid) // current userId
            .collection("events") // user events collection
            .document(ID).delete()
            .addOnSuccessListener {
                _delSuccess.value = true
                Log.d("Delete Event", "DocumentSnapshot successfully deleted!")
            }.addOnFailureListener {
                _delFailure.value = it
                Log.w("Delete Event", "Error deleting document", it)
            }
    }

    fun delEventSuccessCompleted() {
        _delSuccess.value = null
    }

    fun delEventFailureCompleted() {
        _delFailure.value = null
    }
}