package com.example.sealnote.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.sealnote.R
import com.example.sealnote.viewmodel.StealthCalculatorViewModel

class StealthCalculatorFragment : Fragment() {
    private val viewModel: StealthCalculatorViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.stealth_calculator, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view,savedInstanceState)

        val btnC = view.findViewById<Button>(R.id.button16) // Pastikan ID sesuai layout

        btnC.setOnClickListener {
            viewModel.onButtonClick {
                navigateToLogin()
            }
        }
    }

    private fun navigateToLogin() {
        findNavController().navigate(R.id.loginFragment)
    }
}