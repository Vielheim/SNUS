package com.orbital.snus.dashboard

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.orbital.snus.data.TimeLinePost
import com.orbital.snus.data.UserEvent
import java.util.*

class DashboardDataViewModel : ViewModel() {
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    // FOR ADDING EVENTS
    private val _addSuccess = MutableLiveData<Boolean?>()
    val addSuccess : LiveData<Boolean?>
        get() = _addSuccess

    //for adding posts
//    private val _addSuccess2 = MutableLiveData<Boolean?>()
//    val addSucces2 : LiveData<Boolean?>
//        get() = _addSuccess2

    private val _addFailure = MutableLiveData<Exception?>()
    val addFailure : LiveData<Exception?>
        get() = _addFailure

//    private val _addFailure2 = MutableLiveData<Exception?>()
//    val addFailure2 : LiveData<Exception?>
//        get() = _addFailure2

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

//        if (event.addToTimeline!!) {
//            val timelinePost = TimeLinePost(null, event.eventName, Calendar.getInstance().time, false, event.eventDescription, null)
//            val id = db.collection("users")
//                .document(firebaseAuth.currentUser!!.uid)
//                .collection("timeline")
//                .document().id
//
//            timelinePost.id = id
//
//            db.collection("users")
//                .document(firebaseAuth.currentUser!!.uid)
//                .collection("timeline")
//                .document(id).set(timelinePost)
//                .addOnSuccessListener {
//                    _addSuccess2.value = true
//                }.addOnFailureListener {
//                    _addFailure2.value = it
//                }
//
//        }
    }

    fun addEventSuccessCompleted() {
        _addSuccess.value = null
    }

    fun addEventFailureCompleted() {
        _addFailure.value = null
    }

    // FOR DELETING EVENTS
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


    // FOR UPDATING EVENTS

    private val _updateSuccess = MutableLiveData<Boolean?>()
    val updateSuccess : LiveData<Boolean?>
        get() = _updateSuccess

    private val _updateFailure = MutableLiveData<Exception?>()
    val updateFailure : LiveData<Exception?>
        get() = _updateFailure

    fun updateEvent(event: UserEvent) {
        val id = event.id!!

        db.collection("users") // users collection
            .document(firebaseAuth.currentUser!!.uid) // current userId
            .collection("events") // user events collection
            .document(id).set(event)
            .addOnSuccessListener {
                _updateSuccess.value = true
            }.addOnFailureListener {
                _updateFailure.value = it
            }
    }

    fun updateEventSuccessCompleted() {
        _updateSuccess.value = null
    }

    fun updateEventFailureCompleted() {
        _updateFailure.value = null
    }
}