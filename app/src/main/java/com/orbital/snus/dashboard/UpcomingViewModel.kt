package com.orbital.snus.dashboard

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.auth.User
import com.orbital.snus.data.UserEvent
import java.text.SimpleDateFormat
import java.time.DayOfWeek
import java.util.*
import kotlin.collections.ArrayList

class UpcomingViewModel : ViewModel() {
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    private val _events = MutableLiveData<List<UserEvent>>(ArrayList())
    val events : LiveData<List<UserEvent>>
        get() = _events

    private val _addSuccess = MutableLiveData<Boolean?>()
    val addSuccess : LiveData<Boolean?>
        get() = _addSuccess

    private val _addFailure = MutableLiveData<Exception?>()
    val addFailure : LiveData<Exception?>
        get() = _addFailure

    private val _eventSunday = MutableLiveData<List<UserEvent>>(ArrayList())
    val eventSunday : LiveData<List<UserEvent>>
        get() = _eventSunday

    private val _eventMonday = MutableLiveData<List<UserEvent>>(ArrayList())
    val eventMonday : LiveData<List<UserEvent>>
        get() = _eventMonday

    private val _eventTuesday = MutableLiveData<List<UserEvent>>(ArrayList())
    val eventTuesday : LiveData<List<UserEvent>>
        get() = _eventTuesday

    private val _eventWednesday = MutableLiveData<List<UserEvent>>(ArrayList())
    val eventWednesday : LiveData<List<UserEvent>>
        get() = _eventWednesday

    private val _eventThursday = MutableLiveData<List<UserEvent>>(ArrayList())
    val eventThursday : LiveData<List<UserEvent>>
        get() = _eventThursday

    private val _eventFriday = MutableLiveData<List<UserEvent>>(ArrayList())
    val eventFriday : LiveData<List<UserEvent>>
        get() = _eventFriday

    private val _eventSaturday = MutableLiveData<List<UserEvent>>(ArrayList())
    val eventSaturday : LiveData<List<UserEvent>>
        get() = _eventSaturday

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

    // fetching events from database
    fun loadUsers() {
        var eventList = ArrayList<UserEvent>()
        db.collection("users")
            .document(firebaseAuth.currentUser!!.uid)
            .collection("events")
            .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                if (firebaseFirestoreException != null) {
                    Log.w("EventViewModel", firebaseFirestoreException.toString())
                    return@addSnapshotListener
                }
                if (querySnapshot != null) {
                    val documents = querySnapshot.documents
                    documents.forEach {
                        val event = it.toObject(UserEvent::class.java)
                        if (event != null) {
                            if (isDateInCurrentWeek(event)) {
                                eventList.add(event)
                            }
                        }
                    }

                    // assuming the sorting is stable
                    eventList.sortWith(compareBy { it.endDate })
                    eventList.sortWith(compareBy { it.startDate })
                }
                _events.value = eventList
            }
    }

    // Will exclude events that has finished before current instance in time
    fun checkIfToday(event: UserEvent) : Boolean {
        // need to check if event.StartDate <= Today <= event.End
        val fmt = SimpleDateFormat("yyyyMMdd")
        val todayDate = Calendar.getInstance().time
        val startDate = event.startDate!!
        val endDate = event.endDate!!

        //either todaydate is in between current
        // the today date = start date or  today date = end date
        return (startDate.compareTo(todayDate) <= 0 && todayDate.compareTo(endDate) <= 0) || fmt.format(todayDate) == fmt.format(startDate) || fmt.format(todayDate) == fmt.format(endDate)
    }

    fun isDateInCurrentWeek(event: UserEvent): Boolean {
        val startDate = event.startDate!!
        val endDate = event.endDate!!

        val currentCalendar = Calendar.getInstance()
        val week = currentCalendar[Calendar.WEEK_OF_YEAR]
        val year = currentCalendar[Calendar.YEAR]
        val targetCalendarStartDate = Calendar.getInstance()
        val targetCalendarEndDate = Calendar.getInstance()

        targetCalendarStartDate.time = startDate
        targetCalendarEndDate.time = endDate

        val targetWeekStart = targetCalendarStartDate[Calendar.WEEK_OF_YEAR]
        val targetYearStart = targetCalendarStartDate[Calendar.YEAR]

        val targetWeekEnd = targetCalendarEndDate[Calendar.WEEK_OF_YEAR]
        val targetYearEnd = targetCalendarEndDate[Calendar.YEAR]

        // check if it is inside
        return (week >= targetWeekStart && year >= targetYearStart) && (week <= targetWeekEnd && year <= targetYearEnd)
    }

    fun isDateInCurrentDay(dayOfWeek: Int, event: UserEvent): Boolean {
        val startDate = event.startDate!!
        val endDate = event.endDate!!

        val currentCalendar = Calendar.getInstance()

        val targetCalendarStartDate = Calendar.getInstance()
        val targetCalendarEndDate = Calendar.getInstance()

        targetCalendarStartDate.time = startDate
        targetCalendarEndDate.time = endDate

        val targetDayStart = targetCalendarStartDate[Calendar.DAY_OF_WEEK]

        val targetDayEnd = targetCalendarEndDate[Calendar.DAY_OF_WEEK]

        // check if it is inside
        return dayOfWeek in targetDayStart..targetDayEnd
    }

    fun filterEvents() {
        val eventList: List<UserEvent> = _events.value!!

        val sunday = ArrayList<UserEvent>()
        val monday = ArrayList<UserEvent>()
        val tuesday = ArrayList<UserEvent>()
        val wednesday = ArrayList<UserEvent>()
        val thursday = ArrayList<UserEvent>()
        val friday = ArrayList<UserEvent>()
        val saturday = ArrayList<UserEvent>()

        eventList.forEach {
            for (day in 0 until 7) {
                if (isDateInCurrentDay(day, it)) {
                    when (day) {
                        0 -> sunday.add(it)
                        1 -> monday.add(it)
                        2 -> tuesday.add(it)
                        3 -> wednesday.add(it)
                        4 -> thursday.add(it)
                        5 -> friday.add(it)
                        else -> saturday.add(it)
                    }
                }
            }
        }

        _eventSunday.value = sunday
        _eventMonday.value = monday
        _eventTuesday.value = tuesday
        _eventWednesday.value = wednesday
        _eventThursday.value = thursday
        _eventFriday.value = friday
        _eventSaturday.value = saturday
    }
}

