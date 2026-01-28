package org.atmatto.tasks.domain

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flatMapLatest
import org.atmatto.tasks.data.PreferencesRepository
import org.atmatto.tasks.data.TaskRepository
import javax.inject.Inject

class GetAllVisibleCategoriesUseCase @Inject constructor(
	private val taskRepository: TaskRepository,
	private val preferencesRepository: PreferencesRepository,
) {
	private fun includeCompleted() = preferencesRepository.getShowCompleted()

	@OptIn(ExperimentalCoroutinesApi::class)
	operator fun invoke() = includeCompleted().flatMapLatest { taskRepository.getAllCategories(it) }
}
