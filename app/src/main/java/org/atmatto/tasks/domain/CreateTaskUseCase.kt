package org.atmatto.tasks.domain

import org.atmatto.tasks.data.TaskRepository
import org.atmatto.tasks.data.room.Task
import javax.inject.Inject

class CreateTaskUseCase @Inject constructor(
	private val taskRepository: TaskRepository,
	private val updateNotificationForTaskUseCase: UpdateNotificationForTaskUseCase,
) {
	suspend operator fun invoke(task: Task) {
		taskRepository.create(task)
		updateNotificationForTaskUseCase(task)
	}
}
