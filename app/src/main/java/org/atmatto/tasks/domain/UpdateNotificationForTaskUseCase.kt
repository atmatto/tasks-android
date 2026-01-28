package org.atmatto.tasks.domain

import org.atmatto.tasks.data.room.Task
import org.atmatto.tasks.notifications.NotificationScheduler
import javax.inject.Inject

class UpdateNotificationForTaskUseCase @Inject constructor(
	private val notificationScheduler: NotificationScheduler,
) {
	suspend operator fun invoke(task: Task) = notificationScheduler.updateNotificationForTask(task)
}
