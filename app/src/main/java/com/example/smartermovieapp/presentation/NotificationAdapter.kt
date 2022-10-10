package com.example.smartermovieapp.presentation

import android.graphics.Color
import android.view.*
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.PopupWindow
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.smartermovieapp.R
import com.example.smartermovieapp.data.DummyData
import com.google.android.material.snackbar.Snackbar
import java.lang.Float

class NotificationAdapter(

        var data: MutableList<DummyData>,
    ) : RecyclerView.Adapter<NotificationAdapter.ViewHolder>() {

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        }



        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.items_notification, parent, false)
            return ViewHolder(view)
        }


        override fun onBindViewHolder(holder: ViewHolder, position: Int) {

            val txt_title:TextView = holder.itemView.findViewById(R.id.txt_title)
            val txt_desciption:TextView = holder.itemView.findViewById(R.id.txt_description)
            val dots:ImageView = holder.itemView.findViewById(R.id.txt_dots)



            dots.setOnClickListener {
                val popup = PopupMenu(holder.itemView.context, holder.itemView.findViewById(R.id.txt_dots))
                popup.menuInflater.inflate(R.menu.notification_menu, popup.menu)
                popup.show()

            }

        }

        //counts how many items I have in the recycleView
        override fun getItemCount(): Int {
            return data.size
        }
  /*  override fun onMenuItemClick(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.archive -> {
                archive(item)
                true
            }
            R.id.delete -> {
                delete(item)
                true
            }
            else -> false
        }
    }*/


    }
