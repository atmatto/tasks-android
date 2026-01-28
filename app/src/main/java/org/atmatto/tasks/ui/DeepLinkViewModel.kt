package org.atmatto.tasks.ui

import android.content.Intent
import android.net.Uri
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject

// TODO: Best architecture would be if both in-app navigation and
//       deep links were handled by a single point in code
//       (a reducer) which takes current state and an action, and
//       produces the canonical and normalized back stack.

@HiltViewModel
class DeepLinkViewModel @Inject constructor() : ViewModel() {
	private val _event = MutableSharedFlow<DeepLink>(
		replay = 1, // This is required and DeepLinks must be handled idempotently
		onBufferOverflow = BufferOverflow.DROP_OLDEST
	)
	val event: SharedFlow<DeepLink> = _event.asSharedFlow()

	fun handleIntent(intent: Intent) =
		intent.data
			?.let { parseUri(it) }
			?.let { _event.tryEmit(it) }
}

private fun parseUri(uri: Uri): DeepLink? {
	if (uri.scheme.equals("org.atmatto.tasks", true)) {
		val segments = uri.pathSegments
		when (segments.getOrNull(0)?.lowercase()) {
			"settings" -> {
				if (segments.getOrNull(1) == null) {
					return DeepLink.Settings
				}
			}
			"tasks" -> {
				segments.getOrNull(1)?.toLongOrNull()?.let { taskId ->
					return DeepLink.Task(taskId)
				}
			}
		}
	}
	return null
}

sealed class DeepLink {
	data class Task(val taskId: Long) : DeepLink()
	data object Settings : DeepLink()
}

/*
Command for testing:

adb shell am start -W -a android.intent.action.VIEW -d org.atmatto.tasks:/settings org.atmatto.tasks
*/

