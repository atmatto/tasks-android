package org.atmatto.tasks.notifications

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

private const val TAG = "requestNotificationsPermission"

// Returns true if permission has been requested
fun requestNotificationsPermission(context: Activity): Boolean {
	if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
		Log.v(TAG, "Checking POST_NOTIFICATIONS permission")
		if (ContextCompat.checkSelfPermission(
				context,
				Manifest.permission.POST_NOTIFICATIONS
			) != PackageManager.PERMISSION_GRANTED
		) {
			Log.v(TAG, "Will request POST_NOTIFICATIONS permission")
			ActivityCompat.requestPermissions(
				context,
				arrayOf(Manifest.permission.POST_NOTIFICATIONS),
				1
			)
			return true
		}
	}
	return false
}

/*
Commands to restore to pristine state for testing:

adb shell pm revoke org.atmatto.tasks android.permission.POST_NOTIFICATIONS
adb shell pm clear-permission-flags org.atmatto.tasks android.permission.POST_NOTIFICATIONS user-set
adb shell pm clear-permission-flags org.atmatto.tasks android.permission.POST_NOTIFICATIONS user-fixed
*/
