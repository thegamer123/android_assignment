package com.bouraoui.ocrtest.ui.fragment.home.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.bouraoui.ocrtest.ui.fragment.capture.CaptureFragment
import com.bouraoui.ocrtest.ui.fragment.library.LibraryFragment

class HomeFragmentsAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {


    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> {
                CaptureFragment.newInstance()
            }
            else -> {
                LibraryFragment.newInstance()
            }
        }
    }
}