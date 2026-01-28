package org.atmatto.tasks.ui.taskList

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.mutableStateSetOf
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.stateIn
import org.atmatto.tasks.data.PreferencesRepository
import org.atmatto.tasks.data.room.Category
import org.atmatto.tasks.domain.GetAllVisibleCategoriesUseCase
import org.atmatto.tasks.domain.GetAttachmentsForTaskUseCase
import org.atmatto.tasks.domain.ListAndFilterTasksUseCase
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
class TaskListViewModel @Inject constructor(
	private val listAndFilterTasksUseCase: ListAndFilterTasksUseCase,
	private val getAllVisibleCategoriesUseCase: GetAllVisibleCategoriesUseCase,
	private val getAttachmentsForTaskUseCase: GetAttachmentsForTaskUseCase,
	private val preferencesRepository: PreferencesRepository,
) : ViewModel() {
	val searchState = TextFieldState()

	val chosenCategories = mutableStateSetOf<String>()

	val categories: StateFlow<List<Category>> = getAllVisibleCategoriesUseCase()
		.stateIn(
			scope = viewModelScope,
			started = SharingStarted.WhileSubscribed(5.seconds),
			initialValue = listOf()
		)

	val showCompleted = preferencesRepository.getShowCompleted()
		.stateIn(
			scope = viewModelScope,
			started = SharingStarted.WhileSubscribed(5.seconds),
			initialValue = false
		)

	val data = listAndFilterTasksUseCase(
		searchQuery = snapshotFlow { searchState.text.toString() },
		chosenCategories = snapshotFlow { chosenCategories.toSet() },
		showCompleted = showCompleted,
		stateFlowScope = viewModelScope,
		stateFlowStarted = SharingStarted.WhileSubscribed(5.seconds)
	)

	fun getAttachments(taskId: Long) = getAttachmentsForTaskUseCase(taskId)
}
