package org.atmatto.tasks.ui.taskCreator

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.atmatto.tasks.data.PreferencesRepository
import org.atmatto.tasks.data.room.Task
import org.atmatto.tasks.domain.CreateTaskUseCase
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import javax.inject.Inject

@HiltViewModel
class TaskCreatorViewModel @Inject constructor(
	private val createTaskUseCase: CreateTaskUseCase,
	private val preferencesRepository: PreferencesRepository,
) : ViewModel() {
	fun create(
		title: String,
		description: String,
		category: String,
		isCompleted: Boolean,
		isNotificationEnabled: Boolean,
		dueDate: LocalDate,
		dueHour: Int,
		dueMinute: Int
	) {
		val dueTime = LocalDateTime.of(dueDate, LocalTime.of(dueHour, dueMinute))
			.atZone(ZoneId.systemDefault())
			.toInstant()

		viewModelScope.launch {
			createTaskUseCase(
				Task(
					id = 0,
					title = title,
					description = description,
					createdAt = Instant.now(),
					dueAt = dueTime,
					isCompleted = isCompleted,
					isNotificationEnabled = isNotificationEnabled,
					category = category
				)
			)
		}
	}

	fun getNotificationMinutes() = preferencesRepository.getNotificationMinutes()
}
