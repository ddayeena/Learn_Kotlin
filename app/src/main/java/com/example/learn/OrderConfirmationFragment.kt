package com.example.learn

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController

class OrderConfirmationFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_order_confirmation, container, false)
        val toMainPageButton = view.findViewById<ImageView>(R.id.goToHomeButton)
        val toOrderHistory = view.findViewById<ImageView>(R.id.goToOrderHistoryButton)
        toMainPageButton.setOnClickListener {
            findNavController().navigate(R.id.action_orderConfirmationFragment_to_mainPageFragment)
        }
        toOrderHistory.setOnClickListener {
            findNavController().navigate(R.id.action_orderConfirmationFragment_to_orderHistoryFragment)
        }
        return view
    }
}