package com.example.gymmanagement.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.gymmanagement.R
import com.example.gymmanagement.databinding.FragmentChangePasswordBinding


class FragmentChangePassword : Fragment() {


    private lateinit var binding: FragmentChangePasswordBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = FragmentChangePasswordBinding.inflate(inflater, container,false)
        return binding.root
    }


}