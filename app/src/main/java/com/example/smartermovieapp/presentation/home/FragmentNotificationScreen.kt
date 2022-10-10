package com.example.smartermovieapp.presentation.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuInflater
import android.view.View
import android.view.View.inflate
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.TextView
import androidx.core.content.res.ColorStateListInflaterCompat.inflate
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.smartermovieapp.R
import com.example.smartermovieapp.data.DummyData
import com.example.smartermovieapp.presentation.NotificationAdapter
import kotlinx.coroutines.NonDisposableHandle.parent


class FragmentNotificationScreen : Fragment() {


   /* override fun onAttach(context: Context) {
        super.onAttach(context)
        AndroidSupportInjection.inject(this)
    }*/


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (activity as NavigationBarVisibilityHandler).hideNavigationBarForFragmentCreation()
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_notification_screen, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val myList = mutableListOf<DummyData>(DummyData("idk","idk","idk","idk"),
            DummyData("idk","idk","idk","idk"),
            DummyData("idk","idk","idk","idk"),
            DummyData("idk","idk","idk","idk"),
            DummyData("idk","idk","idk","idk"),
            DummyData("idk","idk","idk","idk"),
            DummyData("idk","idk","idk","idk")
        )
        val adapter = NotificationAdapter(myList)
        val myRecyclerView:RecyclerView = view.findViewById(R.id.noti_recyclerView)
        //val viewFav:View = View.inflate(context, R.layout.items_favourite,null)
        myRecyclerView.adapter = adapter
        myRecyclerView.layoutManager = LinearLayoutManager(context)


        /*val dots: TextView = view.findViewById(R.id.txt_dots)
        dots.setOnClickListener {
            showPopup(it)
        }*/

    }

    override fun onDestroyView() {
        (activity as NavigationBarVisibilityHandler).showNavigationBarForFragmentDestroy()
        super.onDestroyView()
    }
    private fun showPopup(v:View){
        val popup = PopupMenu(context, v)
        val inflater: MenuInflater = popup.menuInflater
        inflater.inflate(R.menu.notification_menu, popup.menu)
        popup.show()
    }
}


