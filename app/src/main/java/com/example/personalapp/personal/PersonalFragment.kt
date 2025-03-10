package com.example.personalapp.personal

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.personalapp.R

class PersonalFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_personal, container, false)

        view.findViewById<View>(R.id.btn_addPost).setOnClickListener {
            val intent = Intent(requireContext(), AddPostActivity::class.java)
            startActivity(intent)
        }

        return view
    }
}