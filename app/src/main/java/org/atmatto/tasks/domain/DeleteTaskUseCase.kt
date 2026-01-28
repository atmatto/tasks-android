package org.atmatto.tasks.domain

import org.atmatto.tasks.data.TaskRepository
import org.atmatto.tasks.data.room.Task
import javax.inject.Inject

class DeleteTaskUseCase @Inject constructor(
	private val taskRepository: TaskRepository,
	private val deleteAllAttachmentsOfTaskUseCase: DeleteAllAttachmentsOfTaskUseCase,
	private val updateNotificationForTaskUseCase: UpdateNotificationForTaskUseCase,
) {
	suspend operator fun invoke(task: Task) {
		deleteAllAttachmentsOfTaskUseCase(task.id)
		updateNotificationForTaskUseCase(task.copy(isNotificationEnabled = false))
		taskRepository.delete(task)
	}
}
