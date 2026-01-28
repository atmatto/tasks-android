package org.atmatto.tasks.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.atmatto.tasks.data.TaskRepository
import org.atmatto.tasks.data.room.Task
import javax.inject.Inject

class ListTasksUseCase @Inject constructor(
	private val taskRepository: TaskRepository,
) {
	operator fun invoke(): Flow<List<Task>> =
		taskRepository.getAllStartingFromMostUrgent()
			.map { list -> list.map {
				if (it.title.isNotBlank()) it else it.copy(title = "Unnamed task")
			} }
}
