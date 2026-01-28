package org.atmatto.tasks.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "RebootReceiver"

@AndroidEntryPoint
class RebootReceiver : BroadcastReceiver() {
	@Inject
	lateinit var notificationScheduler: NotificationScheduler

	override fun onReceive(context: Context, intent: Intent) {
		val pendingResult = goAsync()

		Log.v(TAG, "Received reboot broadcast")

		CoroutineScope(Dispatchers.IO).launch {
			try {
				notificationScheduler.updateAllNotifications()
				Log.v(TAG, "Finished updating all notifications after a reboot")
			} finally {
			    pendingResult.finish()
			}
		}
	}
}
