package com.example.sealnote.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.navigation.fragment.findNavController
import com.example.sealnote.databinding.HomePageBinding
import com.example.sealnote.adapter.NotesAdapter
import com.example.sealnote.R
import com.example.sealnote.viewmodel.HomepageViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton

class HomepageFragment : Fragment() {
    private var _binding: HomePageBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HomepageViewModel by viewModels() // Menggunakan ViewModel
    private lateinit var adapter: NotesAdapter // Adapter untuk RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = HomePageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        adapter = NotesAdapter(emptyList())
        binding.recyclerView.adapter = adapter
        // Observasi data dari ViewModel
        viewModel.notes.observe(viewLifecycleOwner) { notes ->
            adapter.updateData(notes) // Gunakan metode updateData() di adapter

        }
        viewModel.loadNotes() // Panggil loadNotes() agar data muncul

        val fabAddNote = view.findViewById<FloatingActionButton>(R.id.floatingActionButton)
        fabAddNote.setOnClickListener {
            findNavController().navigate(R.id.addNotesFragment)
        }
    }
}
