package com.example.smartermovieapp.presentation

import android.annotation.SuppressLint
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.smartermovieapp.R
import com.example.smartermovieapp.data.DummyData
import com.google.android.material.snackbar.Snackbar

class AdapterFavorite(
    var data: MutableList<DummyData>,
    ) : RecyclerView.Adapter<AdapterFavorite.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    val myPosition:Int = 0


    /* fun setOnItemClickListener(listener:OnItemClickListener){
         myListener = listener
     }

     */

    // called when the RecycleView needs a new viewHolder, if the user starts scrolling and
    // we need to make a new item visible
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.items_favourite, parent, false)
        return ViewHolder(view)
    }


    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
      //  var image: ImageView = holder.itemView.findViewById(R.id.iv_profile)
        var textDescription: TextView = holder.itemView.findViewById(R.id.txt_length)
        var textName: TextView = holder.itemView.findViewById(R.id.txt_type)
        //var cardDelete: CardView= holder.itemView.findViewById(R.id.textViewDelete)
        var deleteIcon: ImageView = holder.itemView.findViewById(R.id.iv_delete)
        var deleteText: TextView = holder.itemView.findViewById(R.id.txt_delete)
        val cv_popular: CardView = holder.itemView.findViewById(R.id.cv_popular)

        cv_popular.setOnTouchListener(
            View.OnTouchListener { view, event ->

                // variables to store current configuration of quote card.
                val displayMetrics = view.resources.displayMetrics
                val cardWidth = cv_popular.width
                val cardStart = (displayMetrics.widthPixels.toFloat() / 2) - (cardWidth / 2)
                when (event.action) {
                    MotionEvent.ACTION_MOVE -> {

                        val newX = event.rawX
                        if (newX - cardWidth < cardStart) { // or newX < cardStart + cardWidth
                            cv_popular.animate().x(
                                java.lang.Float.min(cardStart, newX - (cardWidth / 5.5f))
                            )

                                .setDuration(0)
                                .start()

                        }
                    }
                    MotionEvent.ACTION_UP -> {
                        data.removeAt(holder.adapterPosition)
                        notifyItemRemoved(holder.adapterPosition)
                        notifyItemRangeChanged(holder.adapterPosition, data.size)

                        val snack = Snackbar.make(holder.itemView,"Deleting movie ...", Snackbar.LENGTH_LONG)
                            .setAction("UNDO", View.OnClickListener {
                                data.add(myPosition, DummyData("new", "new", "new","new"))
                                notifyItemInserted(myPosition)
                                notifyItemRangeChanged(myPosition, data.size)
                                val snack2 = Snackbar.make(it,"Movie added back", Snackbar.LENGTH_LONG)
                                snack2.show()
                            }).setActionTextColor(Color.parseColor("#01feff"))
                        snack.show()
                    }
                }

                // required to by-pass lint warning
                view.performClick()
                return@OnTouchListener true
            }
        )



        /*deleteIcon.setOnClickListener {

            Log.d("tag", "Delete icon touched")
            val dialogBuilder = AlertDialog.Builder(holder.itemView.context)
            dialogBuilder.setMessage("DO you want to delete this item?")

                .setTitle("Deleting an Item")
                .setCancelable(false)
                .setPositiveButton("Ok", DialogInterface.OnClickListener { dialog, id ->


                    data.removeAt(holder.adapterPosition)
                    notifyItemRemoved(holder.adapterPosition)
                    notifyItemRangeChanged(holder.adapterPosition, data.size)

                })
                .setNegativeButton("No", DialogInterface.OnClickListener { dialog, id ->
                    dialog.dismiss()
                })*/

        /*      val alert = dialogBuilder.create()
            alert.setTitle("Test")
            alert.show()
*/

        //}

        /*holder.itemView.setOnClickListener {
            val snack = Snackbar.make(it,"${textName.text}", Snackbar.LENGTH_LONG)
            snack.show()

        }*/
        holder.itemView.apply {
            textDescription.text = data[position].length
            textName.text = data[position].genre





    }}

    //counts how many items I have in the recycleView
    override fun getItemCount(): Int {
        return data.size
    }
}