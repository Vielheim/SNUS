package com.orbital.snus.profile.MainTimeline

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.orbital.snus.data.TimeLinePost
import com.orbital.snus.data.UserData
import com.orbital.snus.data.UserFriendRequest
import java.util.*
import kotlin.collections.ArrayList

class MainFriendsViewModel(val user: UserData) : ViewModel() {

    private val db = FirebaseFirestore.getInstance()

    // all users in db
    private val _users = MutableLiveData<List<UserData>>()
    val users: LiveData<List<UserData>>
        get() = _users

    // for search
    private val _filteredUsers = MutableLiveData<List<UserData>>()
    val filteredUsers: LiveData<List<UserData>>
        get() = _filteredUsers

    // for friends
    private val _friends = MutableLiveData<List<UserData>>()
    val friends: LiveData<List<UserData>>
        get() = _friends

    // for requests
    private val _requests = MutableLiveData<List<UserData>>()
    val requests: LiveData<List<UserData>>
        get() = _requests

    // for search
    fun filterUsers(username: String) {
        val filteredList = ArrayList<UserData>()

        db.collection("users")
            .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                if (firebaseFirestoreException != null) {
                    Log.w("MainFriendsViewModel", firebaseFirestoreException.toString())
                    return@addSnapshotListener
                }
                if (querySnapshot != null) {
                    val documents = querySnapshot.documents
                    documents.forEach {
                        val eachUser = it.toObject(UserData::class.java)
                        if (eachUser != null) {
                            if (eachUser.fullname!!.toUpperCase(Locale.ROOT).contains(username.toUpperCase(Locale.ROOT))) {
                                filteredList.add(eachUser)
                            }
                        }
                    }
                }
                _filteredUsers.value = filteredList
            }
    }

    fun loadRequests() {
        val requestList = ArrayList<UserData>()

        db.collection("requests")
            .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                if (firebaseFirestoreException != null) {
                    Log.w("MainFriendsViewModel", firebaseFirestoreException.toString())
                    return@addSnapshotListener
                }
                if (querySnapshot != null) {
                    val documents = querySnapshot.documents
                    documents.forEach {
                        val eachUser = it.toObject(UserFriendRequest::class.java)
                        if (eachUser != null) {
                            if (eachUser.to.equals(user.userID)) {
                                _users.value?.forEach {
                                    if (it.userID.equals(eachUser.from)) {
                                        requestList.add(it)
                                    }
                                }
                            }
                        }
                    }
                }
                _requests.value = requestList
            }
    }

    // for all users
    fun loadUsers() {
        val listUsers = ArrayList<UserData>()
        db.collection("users")
            .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                if (firebaseFirestoreException != null) {
                    Log.w("MainFriendsViewModel", firebaseFirestoreException.toString())
                    return@addSnapshotListener
                }
                if (querySnapshot != null) {
                    val documents = querySnapshot.documents
                    documents.forEach {
                        val eachUser = it.toObject(UserData::class.java)
                        if (eachUser != null) {
                            listUsers.add(eachUser)
                        }
                    }
                }
                _users.value = listUsers
            }

    }

    // ADD POSTS
    private val _sendSuccess = MutableLiveData<Boolean?>()
    val sendSuccess: LiveData<Boolean?>
        get() = _sendSuccess

    private val _sendFailure = MutableLiveData<Exception?>()
    val sendFailure: LiveData<Exception?>
        get() = _sendFailure

    fun sendRequest(userFriendRequest: UserFriendRequest) {
        // from -> add to into from's requested
        // to -> add from into to's requests
        db.collection("requests").document().set(userFriendRequest)
            .addOnSuccessListener {
                _sendSuccess.value = true
            }.addOnFailureListener {
                _sendFailure.value = it
            }
    }

    fun sendSuccessCompleted() {
        _sendSuccess.value = null
    }

    fun sendFailureCompleted() {
        _sendFailure.value = null
    }

    fun acceptRequest(userid: String) {

    }

    fun declineRequest(userid: String) {

    }

    val _userStatus = MutableLiveData<String>()
    val userStatus : LiveData<String>
        get() = _userStatus

    fun getUserStatus(userid: String) {
        // check if user is in friends list -> return "Friends"
        // check if user is in requested list -> return "Friend Request sent"
        // check if user is in requests list -> return "Friend Request sent to you!"
        val currentUserID = FirebaseAuth.getInstance().currentUser!!.uid
        _userStatus.value = "Add Friend"

        db.collection("requests")
            .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                if (firebaseFirestoreException != null) {
                    Log.w("MainFriendsViewModel", firebaseFirestoreException.toString())
                    return@addSnapshotListener
                }
                if (querySnapshot != null) {
                    val documents = querySnapshot.documents
                    documents.forEach {
                        val eachRequest = it.toObject(UserFriendRequest::class.java)
                        if (eachRequest != null) {
                            // check if currentUser is sender, and userID is receiver
                            if (eachRequest.from.equals(currentUserID) && eachRequest.to.equals(userid)) {
                                _userStatus.value = "Friend Request Sent!"
                            } else if (eachRequest.from.equals(userid) && eachRequest.to.equals(currentUserID)) {
                                _userStatus.value = "Friend Request Sent to You!"
                            }
                        }
                    }
                }
            }

        db.collection("users")
            .document(currentUserID)
            .collection("friends")
            .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                if (firebaseFirestoreException != null) {
                    Log.w("MainFriendsViewModel", firebaseFirestoreException.toString())
                    return@addSnapshotListener
                }
                if (querySnapshot != null) {
                    val documents = querySnapshot.documents
                    documents.forEach {
                        val eachFriend = it.toObject(String::class.java)
                        if (eachFriend != null) {
                            if (eachFriend.equals(userid)) {
                                _userStatus.value = "Friends"
                            }
                        }
                    }
                }
            }
    }
}