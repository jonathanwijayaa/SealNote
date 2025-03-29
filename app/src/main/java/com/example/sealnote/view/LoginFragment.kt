package com.example.sealnote.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import com.example.sealnote.R
import com.example.sealnote.databinding.LoginPageBinding
import com.example.sealnote.viewmodel.LoginViewModel

class LoginFragment : Fragment() {
    private var _binding: LoginPageBinding? = null
    private val binding get() = _binding!!
    private val viewModel: LoginViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = LoginPageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.loginButton.setOnClickListener {
            val username = binding.emailInput.text.toString().trim()
            val password = binding.passwordInput.text.toString().trim()
            viewModel.login(username, password, requireContext())
        }

        viewModel.loginResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is LoginViewModel.LoginResult.Success -> {
                    Toast.makeText(requireContext(), "Login sukses!", Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.homepageFragment)
                }
                is LoginViewModel.LoginResult.Error -> {
                    Toast.makeText(requireContext(), result.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}