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
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.drawerlayout.widget.DrawerLayout

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

        setupToolbar()
        setupNavigationDrawer()
    }

    private fun setupToolbar() {
        val activity = requireActivity() as AppCompatActivity
        val toolbar = activity.findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)

        activity.setSupportActionBar(toolbar)
        activity.supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_menu) // Pastikan ini adalah ikon menu yang benar
        }
    }

    private fun setupNavigationDrawer() {
        val activity = requireActivity() as AppCompatActivity
        val drawerLayout = activity.findViewById<DrawerLayout>(R.id.drawer_layout)
        val navView = activity.findViewById<com.google.android.material.navigation.NavigationView>(R.id.nav_view)

        // Bersihkan menu lama dan muat menu yang sesuai
        navView.menu.clear()
        navView.inflateMenu(R.menu.notes_menu_drawer) // Pastikan ini adalah menu yang benar untuk Notes Mode

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                // Tidak menambahkan menu agar tiga titik tidak muncul
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    android.R.id.home -> {
                        // Membuka drawer saat tombol menu ditekan
                        drawerLayout.openDrawer(Gravity.START)
                        Log.d("Menu", "Drawer opened")
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