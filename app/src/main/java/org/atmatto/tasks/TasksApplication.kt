package org.atmatto.tasks

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.atmatto.tasks.notifications.NotificationScheduler
import org.atmatto.tasks.notifications.ReminderNotification
import javax.inject.Inject

@HiltAndroidApp
class TasksApplication : Application() {
	@Inject
	lateinit var notificationScheduler: NotificationScheduler

	override fun onCreate() {
		super.onCreate()

		ReminderNotification.createNotificationChannel(this)

		CoroutineScope(Dispatchers.Default).launch {
			notificationScheduler.updateAllNotifications()
		}
	}
}
