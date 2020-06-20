package com.orbital.snus.modules.Forum.MainPage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.orbital.snus.R
import com.orbital.snus.dashboard.Today.TodayEventAdapter
import com.orbital.snus.dashboard.Today.TodayViewModel
import com.orbital.snus.dashboard.Today.TodayViewModelFactory
import com.orbital.snus.data.UserEvent
import com.orbital.snus.databinding.FragmentDashboardTodayBinding
import com.orbital.snus.databinding.ModuleForumMainPageBinding
import java.util.ArrayList

class MainPageFragment : Fragment() {
    private lateinit var firebaseAuth: FirebaseAuth

//    private lateinit var recyclerView: RecyclerView
//    private lateinit var viewAdapter: RecyclerView.Adapter<*>
//    private lateinit var viewManager: RecyclerView.LayoutManager

//    val factory = TodayViewModelFactory()
//    private lateinit var viewModel: TodayViewModel
//    private val events = ArrayList<UserEvent>() // holder to store events and for RecyclerViewAdapter to observe

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {

        val binding: ModuleForumMainPageBinding = DataBindingUtil.inflate(
            inflater, R.layout.module_forum_main_page, container, false
        )

        binding.Dummy.setOnClickListener {
            view: View -> view.findNavController().navigate(R.id.action_mainPageFragment_to_individualModuleFragment)
        }

        return binding.root
    }


}