package com.example.learn

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class ListFragment : Fragment() {

    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager2
    private lateinit var adapter: CategoryPagerAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_list, container, false)

        tabLayout = view.findViewById(R.id.tabLayout)
        viewPager = view.findViewById(R.id.viewPager)

        val categories = listOf("Солоні снеки", "Горішки", "Здорові перекуси", "Солодощі")

        adapter = CategoryPagerAdapter(this, categories)
        viewPager.adapter = adapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = categories[position]
        }.attach()

        return view
    }
}
