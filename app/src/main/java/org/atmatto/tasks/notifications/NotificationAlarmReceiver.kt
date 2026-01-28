package org.atmatto.tasks.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.atmatto.tasks.data.TaskRepository
import org.atmatto.tasks.data.room.Task
import java.time.Instant
import javax.inject.Inject

private const val TAG = "NotificationAlarmReceiver"

@AndroidEntryPoint
class NotificationAlarmReceiver : BroadcastReceiver() {
	@Inject
	lateinit var taskRepository: TaskRepository

	companion object {
		const val EXTRA_TASK_ID = "task_id"
	}

	override fun onReceive(context: Context, intent: Intent) {
		val pendingResult = goAsync()
		val taskId = intent.getLongExtra(EXTRA_TASK_ID, 0L)

		Log.v(TAG, "Received broadcast for task $taskId")

		CoroutineScope(Dispatchers.IO).launch {
			try {
				val task = if (taskId != 0L) {
					taskRepository.getById(taskId).first()
				} else { // Test notification
					Task(
						id = 0,
						title = "Send a test notification",
						description = "Remember to send a reminder before task is due.",
						createdAt = Instant.now(),
						dueAt = Instant.now().plusSeconds(3600),
						isCompleted = false,
						isNotificationEnabled = true,
						category = "Test"
					)
				}
				if (task != null && !task.isCompleted && task.isNotificationEnabled) {
					ReminderNotification.showNotificationForTask(context, task)
				} else {
					Log.v(TAG, "Could not load task $taskId")
				}
			} finally {
			    pendingResult.finish()
			}
		}
	}
}
