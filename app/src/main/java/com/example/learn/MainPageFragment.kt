package com.example.learn


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainPageFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_main_page, container, false)

        val bottomNavigationView = view.findViewById<BottomNavigationView>(R.id.bottomNavigationView)

        childFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, HomeFragment())
            .commit()

        bottomNavigationView.setOnItemSelectedListener { menuItem ->
            val selectedFragment = when (menuItem.itemId) {
                R.id.shop -> HomeFragment()
                R.id.products -> ListFragment()
                R.id.cart -> MysteryFragment()
                R.id.profile -> ProfileFragment()
                else -> HomeFragment()
            }

            childFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, selectedFragment)
                .commit()

            true
        }

        return view
    }
}
