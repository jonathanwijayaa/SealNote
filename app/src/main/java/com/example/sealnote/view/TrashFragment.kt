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
import com.example.sealnote.databinding.TrashPageBinding
import com.example.sealnote.viewmodel.TrashViewModel

class TrashFragment : Fragment() {
    private var _binding: TrashPageBinding? = null
    private val binding get() = _binding!!
    private val viewModel: TrashViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = TrashPageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)

        viewModel.trashedNotes.observe(viewLifecycleOwner) { notes ->
            binding.recyclerView.adapter = NotesAdapter(notes)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

