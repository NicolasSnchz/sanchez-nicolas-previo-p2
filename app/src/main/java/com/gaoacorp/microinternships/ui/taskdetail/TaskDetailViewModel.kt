package com.gaoacorp.microinternships.ui.taskdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gaoacorp.microinternships.domain.model.Result
import com.gaoacorp.microinternships.domain.model.TaskWithPublisher
import com.gaoacorp.microinternships.domain.repository.TaskRepository
import com.gaoacorp.microinternships.ui.common.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel de la pantalla de detalle.
 *
 * Recibe el taskId por medio de Safe Args y dispara la carga
 * combinada (tarea + publicador) al repositorio.
 */
@HiltViewModel
class TaskDetailViewModel @Inject constructor(
    private val repository: TaskRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<TaskWithPublisher>>(UiState.Loading)
    val uiState: StateFlow<UiState<TaskWithPublisher>> = _uiState.asStateFlow()

    private var currentTaskId: Int? = null

    fun loadTaskDetail(taskId: Int, forceRefresh: Boolean = false) {
        currentTaskId = taskId
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            when (val result = repository.getTaskDetail(taskId, forceRefresh)) {
                is Result.Success -> _uiState.value = UiState.Success(result.data)
                is Result.Error -> _uiState.value = UiState.Error(result.type, result.message)
                is Result.Loading -> _uiState.value = UiState.Loading
            }
        }
    }

    fun retry() {
        currentTaskId?.let { loadTaskDetail(it, forceRefresh = true) }
    }
}
