package com.example.sealnote.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.sealnote.R
import com.example.sealnote.databinding.SignupPageBinding
import com.example.sealnote.viewmodel.AuthViewModel

class SignupFragment : Fragment() {
    private var _binding: SignupPageBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = SignupPageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Changed from btnSignup to signUpButton
        binding.signUpButton.setOnClickListener {
            // Changed from etUsername to fullNameInput
            val username = binding.fullNameInput.text.toString().trim()
            // Changed from etPassword to passwordInput
            val password = binding.passwordInput.text.toString().trim()
            // Changed from etConfirmPassword to confirmPasswordInput
            val confirmPassword = binding.confirmPasswordInput.text.toString().trim()

            // Added password confirmation check
            if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(requireContext(), "Please fill in all fields!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                Toast.makeText(requireContext(), "Passwords do not match!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewModel.signup(username, password)
            Toast.makeText(requireContext(), "Account created successfully!", Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.action_stealthCalculator_to_sealNoteLogin)
        }

        // Changed from tvLogin to logInText
        binding.logInText.setOnClickListener {
            findNavController().navigate(R.id.action_signup_to_login)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}