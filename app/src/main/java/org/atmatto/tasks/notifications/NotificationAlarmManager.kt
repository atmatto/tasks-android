package org.atmatto.tasks.notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.Instant
import java.time.ZoneId
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "NotificationAlarmManager"

@Singleton
class NotificationAlarmManager @Inject constructor(
	@param:ApplicationContext private val context: Context
) {
	private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

	fun scheduleTestNotification(
		instant: Instant
	) {
		schedule(0, instant)
	}

	fun schedule(
		taskId: Long,
		instant: Instant
	) {
		Log.v(TAG, "Received request to schedule notification for task $taskId at ${instant.atZone(ZoneId.systemDefault())}")

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
			val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
			if (!alarmManager.canScheduleExactAlarms()) {
				// Redirect to settings
				val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
				context.startActivity(intent)
			}
		}

		cancel(taskId)

		val intent = Intent(context, NotificationAlarmReceiver::class.java).apply {
			putExtra(NotificationAlarmReceiver.EXTRA_TASK_ID, taskId)
		}

		val pendingIntent = PendingIntent.getBroadcast(
			context,
			taskId.toInt(),
			intent,
			PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
		)

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
			if (alarmManager.canScheduleExactAlarms()) {
				alarmManager.setExactAndAllowWhileIdle(
					AlarmManager.RTC_WAKEUP,
					instant.toEpochMilli(),
					pendingIntent
				)
			}
		} else {
			alarmManager.setExactAndAllowWhileIdle(
				AlarmManager.RTC_WAKEUP,
				instant.toEpochMilli(),
				pendingIntent
			)
		}

		Log.v(TAG, "Scheduled notification for task $taskId at ${instant.atZone(ZoneId.systemDefault())}")
	}

	fun cancel(taskId: Long) {
		Log.v(TAG, "Received request to cancel notification for task $taskId")

		val intent = Intent(context, NotificationAlarmReceiver::class.java)
		val pendingIntent = PendingIntent.getBroadcast(
			context,
			taskId.toInt(),
			intent,
			PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
		)

		alarmManager.cancel(pendingIntent)
		pendingIntent.cancel()
	}
}
