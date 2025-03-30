package com.example.sealnote.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.sealnote.R

class SecretNotesLockedFragment: Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.secret_note_locked_page, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val toolbar = requireActivity().findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)

        if (toolbar != null) {
            (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)
            (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)

            toolbar.setNavigationIcon(R.drawable.ic_back) // Pastikan ic_back ada di drawable
            toolbar.setNavigationOnClickListener {
                findNavController().navigateUp()
            }
        } else {
            throw NullPointerException("Toolbar tidak ditemukan di MainActivity")
        }
        view.setOnClickListener{
            findNavController().navigate(R.id.action_secretNoteLocked_to_auth)
        }
    }
}