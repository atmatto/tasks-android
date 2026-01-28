package org.atmatto.tasks.notifications

import kotlinx.coroutines.flow.first
import org.atmatto.tasks.data.PreferencesRepository
import org.atmatto.tasks.data.TaskRepository
import org.atmatto.tasks.data.room.Task
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationScheduler @Inject constructor(
	private val taskRepository: TaskRepository,
	private val notificationAlarmManager: NotificationAlarmManager,
	private val preferencesRepository: PreferencesRepository,
) {
	suspend fun updateAllNotifications() {
		val tasks = taskRepository.getAllStartingFromMostUrgent().first()
		tasks.forEach { this.updateNotificationForTask(it) }
	}

	suspend fun updateNotificationForTask(task: Task) {
		val notificationMinutes = preferencesRepository.getNotificationMinutes().first()
		val notificationTime = task.dueAt.minusSeconds(60L * notificationMinutes)
		if (task.isNotificationEnabled && notificationTime.isAfter(Instant.now().plusSeconds(1))) {
			notificationAlarmManager.schedule(task.id, notificationTime)
		} else {
			notificationAlarmManager.cancel(task.id)
		}
	}
}
