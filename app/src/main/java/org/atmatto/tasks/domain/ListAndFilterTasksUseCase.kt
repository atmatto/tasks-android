package org.atmatto.tasks.domain

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import org.atmatto.tasks.common.ListState
import org.atmatto.tasks.data.TaskRepository
import org.atmatto.tasks.data.room.Task
import javax.inject.Inject

class ListAndFilterTasksUseCase @Inject constructor(
	private val taskRepository: TaskRepository,
	private val listTasksUseCase: ListTasksUseCase,
) {
	operator fun invoke(
		searchQuery: Flow<String>,
		chosenCategories: Flow<Set<String>>,
		showCompleted: Flow<Boolean>,
		stateFlowScope: CoroutineScope,
		stateFlowStarted: SharingStarted,
	): StateFlow<ListState<Task>> {
		return combine(
			listTasksUseCase(),
			searchQuery,
			chosenCategories,
			showCompleted,
			::constructTaskListData
		).stateIn(
			stateFlowScope,
			stateFlowStarted,
			ListState.Loading
		)
	}
}

private suspend fun constructTaskListData(
	rawList: List<Task>,
	query: String,
	categories: Set<String>,
	showCompleted: Boolean
): ListState<Task> {
	var list = rawList

	if (!showCompleted) {
		list = list.filter { !it.isCompleted }
	}

	if (list.isEmpty()) {
		return ListState.Empty
	}

	if (query.isNotBlank()) {
		list = list.filter { it.title.contains(query, true) }
	}

	if (categories.isNotEmpty()) {
		list = list.filter { it.category in categories }
	}

	return if (list.isEmpty()) {
		ListState.AllFiltered
	} else {
		ListState.Some(list)
	}
}
