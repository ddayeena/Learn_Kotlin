package com.example.learn

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import android.widget.Button

class FirstFragment : Fragment() {
 override fun onCreateView(
  inflater: LayoutInflater, container: ViewGroup?,
  savedInstanceState: Bundle?
 ): View? {
  val view = inflater.inflate(R.layout.first_fragment, container, false)

  view.findViewById<Button>(R.id.second_button).setOnClickListener {
   findNavController().navigate(R.id.action_firstFragment_to_secondFragment)
  }

  return view
 }
}
