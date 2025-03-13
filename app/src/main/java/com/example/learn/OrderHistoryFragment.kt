package com.example.learn

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController

class OrderHistoryFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_order_history, container, false)
        val backButton = view.findViewById<Button>(R.id.back_from_order_history_button)
        backButton.setOnClickListener {
            findNavController().navigate(R.id.action_orderHistoryFragment_to_mainPageFragment)
        }
        return view
    }
}