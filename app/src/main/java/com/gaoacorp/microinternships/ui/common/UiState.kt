package com.gaoacorp.microinternships.ui.common

import com.gaoacorp.microinternships.domain.model.ErrorType

/**
 * Estado de UI genérico expuesto por los ViewModels como StateFlow<UiState<T>>.
 * La UI reacciona de forma declarativa a cada estado.
 *
 * Cumple el requisito: "sealed class Result<T, E> o similar.
 * El ViewModel expone StateFlow<UiState> con estados Loading, Success y Error".
 * Se agrega Empty como cuarto estado para UX.
 */
sealed class UiState<out T> {
    data object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data object Empty : UiState<Nothing>()
    data class Error(val type: ErrorType, val message: String) : UiState<Nothing>()
}
