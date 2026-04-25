package com.gaoacorp.microinternships.ui.tasklist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gaoacorp.microinternships.databinding.FragmentTaskListBinding
import com.gaoacorp.microinternships.ui.common.UiState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

/**
 * Pantalla de lista de micro tareas.
 *
 * Observa el StateFlow del ViewModel y renderiza visualmente
 * los cuatro estados: Loading, Success, Empty, Error.
 * Cumple el requisito: "Cada pantalla debe manejar y mostrar visualmente
 * los tres estados: Loading, Error y Empty".
 */
@AndroidEntryPoint
class TaskListFragment : Fragment() {

    private var _binding: FragmentTaskListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TaskListViewModel by viewModels()
    private lateinit var adapter: TaskListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTaskListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecycler()
        setupSwipeRefresh()
        setupRetryButton()
        observeUiState()
    }

    private fun setupRecycler() {
        adapter = TaskListAdapter { task ->
            val action = TaskListFragmentDirections
                .actionTaskListToTaskDetail(task.id)
            findNavController().navigate(action)
        }
        val layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerTasks.layoutManager = layoutManager
        binding.recyclerTasks.adapter = adapter

        // Detección de scroll al final → loadNextPage()
        binding.recyclerTasks.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(rv: RecyclerView, dx: Int, dy: Int) {
                val total = layoutManager.itemCount
                val lastVisible = layoutManager.findLastVisibleItemPosition()
                if (total > 0 && lastVisible >= total - 3) {
                    viewModel.loadNextPage()
                }
            }
        })
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.loadFirstPage(forceRefresh = true)
        }
    }

    private fun setupRetryButton() {
        binding.errorView.btnRetry.setOnClickListener { viewModel.retry() }
    }

    private fun observeUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.uiState.collect { state ->
                        renderState(state)
                    }
                }
                launch {
                    viewModel.isLoadingMore.collect { loading ->
                        binding.progressBottom.visibility =
                            if (loading) View.VISIBLE else View.GONE
                    }
                }
            }
        }
    }

    private fun renderState(state: UiState<*>) {
        // Oculta todos y luego muestra el que corresponda
        binding.swipeRefresh.isRefreshing = false
        binding.progressLoading.visibility = View.GONE
        binding.recyclerTasks.visibility = View.GONE
        binding.emptyView.root.visibility = View.GONE
        binding.errorView.root.visibility = View.GONE

        when (state) {
            is UiState.Loading -> {
                binding.progressLoading.visibility = View.VISIBLE
            }
            is UiState.Empty -> {
                binding.emptyView.root.visibility = View.VISIBLE
            }
            is UiState.Error -> {
                binding.errorView.root.visibility = View.VISIBLE
                binding.errorView.txtErrorTitle.text = errorTitle(state.type)
                binding.errorView.txtErrorMessage.text = state.message
            }
            is UiState.Success<*> -> {
                @Suppress("UNCHECKED_CAST")
                val tasks = state.data as List<com.gaoacorp.microinternships.domain.model.Task>
                binding.recyclerTasks.visibility = View.VISIBLE
                adapter.submitList(tasks)
            }
        }
    }

    private fun errorTitle(type: com.gaoacorp.microinternships.domain.model.ErrorType) = when (type) {
        com.gaoacorp.microinternships.domain.model.ErrorType.NETWORK -> "Sin conexión"
        com.gaoacorp.microinternships.domain.model.ErrorType.TIMEOUT -> "Tiempo agotado"
        com.gaoacorp.microinternships.domain.model.ErrorType.NOT_FOUND -> "Recurso no encontrado"
        com.gaoacorp.microinternships.domain.model.ErrorType.SERVER -> "Error del servidor"
        com.gaoacorp.microinternships.domain.model.ErrorType.UNAUTHORIZED -> "No autorizado"
        com.gaoacorp.microinternships.domain.model.ErrorType.PARSING -> "Datos inválidos"
        com.gaoacorp.microinternships.domain.model.ErrorType.UNKNOWN -> "Error inesperado"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
