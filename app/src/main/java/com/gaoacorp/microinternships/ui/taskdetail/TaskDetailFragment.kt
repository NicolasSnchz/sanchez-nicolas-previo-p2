package com.gaoacorp.microinternships.ui.taskdetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.gaoacorp.microinternships.databinding.FragmentTaskDetailBinding
import com.gaoacorp.microinternships.domain.model.TaskStatus
import com.gaoacorp.microinternships.domain.model.TaskWithPublisher
import com.gaoacorp.microinternships.ui.common.UiState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

/**
 * Pantalla de detalle.
 * Recibe el taskId por Safe Args (ver nav_graph.xml).
 */
@AndroidEntryPoint
class TaskDetailFragment : Fragment() {

    private var _binding: FragmentTaskDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TaskDetailViewModel by viewModels()
    private val args: TaskDetailFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTaskDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.errorView.btnRetry.setOnClickListener { viewModel.retry() }

        // Carga inicial
        if (savedInstanceState == null) {
            viewModel.loadTaskDetail(args.taskId)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { render(it) }
            }
        }
    }

    private fun render(state: UiState<TaskWithPublisher>) {
        binding.progressLoading.visibility = View.GONE
        binding.content.visibility = View.GONE
        binding.errorView.root.visibility = View.GONE

        when (state) {
            is UiState.Loading -> binding.progressLoading.visibility = View.VISIBLE
            is UiState.Empty -> { /* no aplica en esta pantalla */ }
            is UiState.Error -> {
                binding.errorView.root.visibility = View.VISIBLE
                binding.errorView.txtErrorMessage.text = state.message
            }
            is UiState.Success -> {
                binding.content.visibility = View.VISIBLE
                val (task, publisher) = state.data
                binding.txtTitle.text = task.title
                binding.txtCategory.text = task.category
                binding.txtDescription.text = task.description
                binding.txtReward.text = String.format("Recompensa: $%.2f USD", task.rewardUsd)
                binding.txtStatus.text = when (task.status) {
                    TaskStatus.OPEN -> "Estado: Abierta"
                    TaskStatus.IN_PROGRESS -> "Estado: En curso"
                    TaskStatus.COMPLETED -> "Estado: Completada"
                }
                binding.txtPublisherName.text = publisher.fullName
                binding.txtPublisherEmail.text = publisher.email
                binding.txtPublisherLocation.text = "${publisher.city}, ${publisher.country}"

                Glide.with(this)
                    .load(publisher.avatarUrl)
                    .circleCrop()
                    .into(binding.imgPublisher)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
