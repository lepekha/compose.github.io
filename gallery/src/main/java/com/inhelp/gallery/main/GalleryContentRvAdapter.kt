package com.inhelp.gallery.main

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.inhelp.gallery.main.content.FragmentGalleryContent

class GalleryContentRvAdapter(private val fm: FragmentManager, private val items: MutableList<ImageFolder>) : FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT){

    override fun getCount(): Int {
        return items.size
    }

    override fun getItem(position: Int): Fragment {
        return FragmentGalleryContent.newInstance(position)
    }

    override fun getPageTitle(position: Int): CharSequence {
        return items[position].name
    }
}