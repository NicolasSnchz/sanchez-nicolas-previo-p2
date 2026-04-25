package com.gaoacorp.microinternships.ui.tasklist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gaoacorp.microinternships.domain.model.Result
import com.gaoacorp.microinternships.domain.model.Task
import com.gaoacorp.microinternships.domain.repository.TaskRepository
import com.gaoacorp.microinternships.ui.common.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel de la pantalla de lista de tareas.
 *
 * Responsabilidades:
 *  - Exponer StateFlow<UiState<List<Task>>> que la UI observa.
 *  - Manejar paginación manual (offset / page).
 *  - Distinguir entre "cargar primera página" y "cargar más".
 *  - Soportar pull-to-refresh (forceRefresh = true).
 *
 * No conoce Retrofit ni Room — solo habla con el repositorio.
 */
@HiltViewModel
class TaskListViewModel @Inject constructor(
    private val repository: TaskRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<List<Task>>>(UiState.Loading)
    val uiState: StateFlow<UiState<List<Task>>> = _uiState.asStateFlow()

    private val _isLoadingMore = MutableStateFlow(false)
    val isLoadingMore: StateFlow<Boolean> = _isLoadingMore.asStateFlow()

    private var currentPage = 1
    private var isLastPage = false

    init {
        loadFirstPage()
    }

    fun loadFirstPage(forceRefresh: Boolean = false) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            currentPage = 1
            isLastPage = false

            when (val result = repository.getTasks(page = currentPage, forceRefresh = forceRefresh)) {
                is Result.Success -> {
                    _uiState.value = if (result.data.isEmpty()) {
                        UiState.Empty
                    } else {
                        UiState.Success(result.data)
                    }
                }
                is Result.Error -> {
                    _uiState.value = UiState.Error(result.type, result.message)
                }
                is Result.Loading -> {
                    _uiState.value = UiState.Loading
                }
            }
        }
    }

    /**
     * Carga la siguiente página cuando el usuario llega al final de la lista.
     * Cumple el requisito: "La UI debe mostrar indicador de 'cargando más' al hacer scroll".
     */
    fun loadNextPage() {
        if (isLastPage || _isLoadingMore.value) return
        val current = _uiState.value
        if (current !is UiState.Success) return

        viewModelScope.launch {
            _isLoadingMore.value = true
            val nextPage = currentPage + 1

            when (val result = repository.getTasks(page = nextPage, forceRefresh = false)) {
                is Result.Success -> {
                    if (result.data.size <= current.data.size) {
                        // la API no devolvió elementos nuevos → fin de datos
                        isLastPage = true
                    } else {
                        currentPage = nextPage
                        _uiState.value = UiState.Success(result.data)
                    }
                }
                is Result.Error -> {
                    // No se sobreescribe el estado — la lista actual se mantiene visible.
                    // La UI puede mostrar un Snackbar desde este canal lateral.
                }
                is Result.Loading -> Unit
            }
            _isLoadingMore.value = false
        }
    }

    fun retry() = loadFirstPage()
}
