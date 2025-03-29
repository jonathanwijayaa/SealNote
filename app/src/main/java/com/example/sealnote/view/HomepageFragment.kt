package com.example.sealnote.view

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.navigation.fragment.findNavController
import com.example.sealnote.databinding.HomePageBinding
import com.example.sealnote.adapter.NotesAdapter
import com.example.sealnote.R
import com.example.sealnote.viewmodel.HomepageViewModel
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle

class HomepageFragment : Fragment() {
    private var _binding: HomePageBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HomepageViewModel by viewModels()
    private lateinit var adapter: NotesAdapter

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

        viewModel.notes.observe(viewLifecycleOwner) { notes ->
            adapter.updateData(notes)
        }
        viewModel.loadNotes()

        val fabAddNote = binding.floatingActionButton
        fabAddNote.setOnClickListener {
            findNavController().navigate(R.id.addNotesFragment)
        }

        setupMenuIcon() // Call setupMenuIcon without passing the toolbar
        addMenuProvider() // Add the MenuProvider
    }

    private fun setupMenuIcon() {
        val activity = activity as? AppCompatActivity
        val toolbar = activity?.findViewById<Toolbar>(R.id.toolbar) // Get the toolbar from the activity
        activity?.setSupportActionBar(toolbar)
        activity?.supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_menu) // Replace with your menu icon
        }
    }

    private fun addMenuProvider() {
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                // Not used in this case, as we're only handling the home button
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                // Handle the menu selection
                return when (menuItem.itemId) {
                    android.R.id.home -> {
                        // Handle menu icon click here
                        Log.d("Menu", "Menu icon clicked")
                        // Example: Open a drawer, show a menu, etc.
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}