package org.atmatto.tasks.domain

import org.atmatto.tasks.data.TaskRepository
import javax.inject.Inject

class SetTaskStateUseCase @Inject constructor(
	private val taskRepository: TaskRepository,
) {
	suspend operator fun invoke(id: Long, isCompleted: Boolean) = taskRepository.setState(id, isCompleted)
}
