package org.atmatto.tasks.domain

import org.atmatto.tasks.data.TaskRepository
import javax.inject.Inject

class GetTaskUseCase @Inject constructor(
	private val taskRepository: TaskRepository,
) {
	operator fun invoke(id: Long) = taskRepository.getById(id)
}
