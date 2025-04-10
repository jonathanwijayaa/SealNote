package com.example.sealnote.view

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.GridLayoutManager
import com.example.sealnote.R
import com.example.sealnote.adapter.NotesAdapter
import com.example.sealnote.databinding.TrashPageBinding
import com.example.sealnote.viewmodel.TrashViewModel

class TrashFragment : Fragment() {
    private var _binding: TrashPageBinding? = null
    private val binding get() = _binding!!
    private val viewModel: TrashViewModel by viewModels()
    private lateinit var adapter: NotesAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = TrashPageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val activity = requireActivity() as AppCompatActivity
        val toolbar = activity.findViewById<Toolbar>(R.id.toolbar)
        activity.setSupportActionBar(toolbar)

        activity.supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_menu)
        }

        setupMenu(toolbar)
        setupRecyclerView()
        setupNavigationDrawer()

        // Tetap mengamati data dari ViewModel tanpa filtering
        viewModel.trashedNotes.observe(viewLifecycleOwner) { notes ->
            adapter.updateData(notes)
        }
    }

    private fun setupMenu(toolbar: Toolbar) {
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_search, menu)

                val searchItem = menu.findItem(R.id.action_search)
                val searchView = searchItem.actionView as SearchView
                searchView.queryHint = "Search your trashed notes..."
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    android.R.id.home -> {
                        val drawerLayout =
                            requireActivity().findViewById<DrawerLayout>(R.id.drawer_layout)
                        drawerLayout.openDrawer(GravityCompat.START)
                        true
                    }

                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun setupRecyclerView() {
        binding.recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        adapter = NotesAdapter(emptyList())
        binding.recyclerView.adapter = adapter
    }

    private fun setupNavigationDrawer() {
        val drawerLayout = requireActivity().findViewById<DrawerLayout>(R.id.drawer_layout)
        val navView =
            requireActivity().findViewById<com.google.android.material.navigation.NavigationView>(R.id.nav_view)

        navView.menu.clear()
        navView.inflateMenu(R.menu.notes_menu_drawer)
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}