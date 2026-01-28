package org.atmatto.tasks.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.atmatto.tasks.data.PreferencesRepository
import org.atmatto.tasks.domain.UpdateNotificationsForAllTasksUseCase
import org.atmatto.tasks.notifications.NotificationAlarmManager
import java.time.Instant
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
	private val preferencesRepository: PreferencesRepository,
	private val notificationAlarmManager: NotificationAlarmManager,
	private val updateNotificationsForAllTasksUseCase: UpdateNotificationsForAllTasksUseCase
) : ViewModel() {
	fun getNotificationMinutes() = preferencesRepository.getNotificationMinutes()

	fun setNotificationMinutes(value: Int) {
		viewModelScope.launch {
			preferencesRepository.setNotificationMinutes(value)
			updateNotificationsForAllTasksUseCase()
		}
	}

	fun getShowCompleted() = preferencesRepository.getShowCompleted()

	fun setShowCompleted(value: Boolean) {
		viewModelScope.launch {
			preferencesRepository.setShowCompleted(value)
		}
	}

	fun sendTestNotification() =
		notificationAlarmManager.scheduleTestNotification(Instant.now().plusSeconds(2))
}
