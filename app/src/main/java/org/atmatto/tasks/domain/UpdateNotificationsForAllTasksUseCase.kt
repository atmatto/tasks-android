package org.atmatto.tasks.domain

import org.atmatto.tasks.notifications.NotificationScheduler
import javax.inject.Inject

class UpdateNotificationsForAllTasksUseCase @Inject constructor(
	private val notificationScheduler: NotificationScheduler,
) {
	suspend operator fun invoke() = notificationScheduler.updateAllNotifications()
}
