package com.gaoacorp.microinternships.ui.tasklist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.gaoacorp.microinternships.databinding.ItemTaskBinding
import com.gaoacorp.microinternships.domain.model.Task

/**
 * Adapter eficiente para la lista de tareas usando ListAdapter + DiffUtil.
 */
class TaskListAdapter(
    private val onClick: (Task) -> Unit
) : ListAdapter<Task, TaskListAdapter.VH>(DIFF) {

    inner class VH(private val binding: ItemTaskBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(task: Task) {
            binding.txtTitle.text = task.title
            binding.txtCategory.text = task.category
            binding.txtReward.text = String.format("$%.2f USD", task.rewardUsd)
            binding.txtStatus.text = when (task.status) {
                com.gaoacorp.microinternships.domain.model.TaskStatus.OPEN -> "Abierta"
                com.gaoacorp.microinternships.domain.model.TaskStatus.IN_PROGRESS -> "En curso"
                com.gaoacorp.microinternships.domain.model.TaskStatus.COMPLETED -> "Completada"
            }
            binding.root.setOnClickListener { onClick(task) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemTaskBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<Task>() {
            override fun areItemsTheSame(a: Task, b: Task) = a.id == b.id
            override fun areContentsTheSame(a: Task, b: Task) = a == b
        }
    }
}
