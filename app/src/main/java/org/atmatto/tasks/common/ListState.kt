package org.atmatto.tasks.common

sealed class ListState<out T> {
	data object Loading : ListState<Nothing>()
	data object Empty : ListState<Nothing>()
	data object AllFiltered : ListState<Nothing>()
	data class Some<T>(val items: List<T>) : ListState<T>()
}
