package com.example.learn

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.learn.admin.AdminOrdersFragment
import com.example.learn.admin.AdminProductsFragment
import com.example.learn.admin.AdminProfileFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class AdminMainPageFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_admin_main_page, container, false)

        val bottomNavigationView = view.findViewById<BottomNavigationView>(R.id.bottomNavigationView)

        // Початковий фрагмент (Головна сторінка)
        childFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, AdminProfileFragment())
            .commit()

        // Обробка натискання на пункти меню
        bottomNavigationView.setOnItemSelectedListener { menuItem ->
            val selectedFragment = when (menuItem.itemId) {
                R.id.products -> AdminProductsFragment()
                R.id.orders -> AdminOrdersFragment()
                R.id.profile -> AdminProfileFragment()
                else -> AdminProfileFragment()
            }

            childFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, selectedFragment as Fragment)
                .commit()

            true
        }

        return view
    }
}
