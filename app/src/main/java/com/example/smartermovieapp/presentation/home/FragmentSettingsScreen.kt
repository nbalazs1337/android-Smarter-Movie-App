package com.example.smartermovieapp.presentation.home

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log.d
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.fragment.app.Fragment
import com.example.smartermovieapp.R


class FragmentSettingsScreen : Fragment() {


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
        // Inflate the layout for this fragment

        val view =  inflater.inflate(R.layout.fragment_settings_screen, container, false)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    /*    val notification_bell: ImageButton = view.findViewById(R.id.btn_notifications)
        notification_bell.setOnClickListener {
            findNavController().navigate(R.id.action_fragmentHomeScreen_to_fragmentNotificationScreen)
        }*/

        //val til:TextInputLayout = view.findViewById(R.id.txt_input)
        //til.setStartIconDrawable(R.drawable.custom_gradient_circle)


        val themes = resources.getStringArray(R.array.themes)
        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.drop_down_item, themes)
        val autoCompleteTxt :AutoCompleteTextView = view.findViewById(R.id.autoCompleteText)
        autoCompleteTxt.setDropDownBackgroundDrawable(resources.getDrawable(R.drawable.gradient_background))
        autoCompleteTxt.setAdapter(arrayAdapter)





    }


}
