package com.example.smartermovieapp.presentation

import android.content.Context
import androidx.fragment.app.Fragment
import com.example.smartermovieapp.R
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject

class LocationSearchFragment : Fragment(R.layout.fragment_location_search) {

    @Inject
    lateinit var vm: LocationSearchVM

    override fun onAttach(context: Context) {
        super.onAttach(context)
        AndroidSupportInjection.inject(this)
    }
}