package com.example.sealnote.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.example.sealnote.R
import com.example.sealnote.adapter.NotesAdapter
import com.example.sealnote.databinding.BookmarksPageBinding
import com.example.sealnote.viewmodel.BookmarksViewModel

class BookmarksFragment : Fragment() {
    private var _binding: BookmarksPageBinding? = null
    private val binding get() = _binding!!
    private val viewModel: BookmarksViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BookmarksPageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)

        viewModel.bookmarkedNotes.observe(viewLifecycleOwner) { notes ->
            binding.recyclerView.adapter = NotesAdapter(notes)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
