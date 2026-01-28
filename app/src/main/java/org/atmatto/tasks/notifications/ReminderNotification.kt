package org.atmatto.tasks.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.text.bold
import androidx.core.text.buildSpannedString
import androidx.core.text.italic
import org.atmatto.tasks.R
import org.atmatto.tasks.data.room.Task
import org.atmatto.tasks.ui.taskList.formatInstant

private const val TAG = "ReminderNotification"

object ReminderNotification {
	const val NOTIFICATION_CHANNEL = "task_reminders"

	fun showNotificationForTask(context: Context, task: Task) {
		Log.v(TAG, "Will show notification for task ${task.id}")

		val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

		val bigText = buildSpannedString {
			bold { append("Due") }
			append(": ${formatInstant(task.dueAt)}\n")

			task.category.takeIf { it.isNotBlank() }?.let {
				bold { append("Category") }
				append(": ")
				italic { append(it) }
				append("\n")
			}

			task.description.takeIf { it.isNotBlank() }?.let {
				append("\n")
				italic { append(it) }
			}
		}

		val path = if (task.id != 0L) "/tasks/${task.id}" else "/settings"
		val intent = Intent(
			Intent.ACTION_VIEW,
			Uri.parse("org.atmatto.tasks:$path")
		).apply {
			setPackage(context.packageName)
			flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
		}
		val pendingIntent = PendingIntent.getActivity(
			context,
			task.id.toInt(),
			intent,
			PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
		)

		val notification = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL)
			.setSmallIcon(R.drawable.check_circle_24px)
			.setContentTitle("Due soon: " + task.title.ifBlank { task.description.ifBlank { "Unnamed task" } })
			.setContentText(formatInstant(task.dueAt))
			.setStyle(NotificationCompat.BigTextStyle()
				.bigText(bigText))
			.setContentIntent(pendingIntent)
			.setAutoCancel(true)
			.setPriority(NotificationCompat.PRIORITY_HIGH)
			.build()

		notificationManager.notify(task.id.toInt(), notification)

		Log.v(TAG, "Shown notification for task ${task.id}")
	}

	fun createNotificationChannel(context: Context) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			Log.v(TAG, "Will create notification channel")

			val channel = NotificationChannel(
				NOTIFICATION_CHANNEL,
				"Task Reminders",
				NotificationManager.IMPORTANCE_HIGH
			).apply {
				description = "Notifications for task reminders"
				enableLights(true)
				enableVibration(true)
			}

			val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
			notificationManager.createNotificationChannel(channel)

			Log.v(TAG, "Created notification channel")
		}
	}
}
