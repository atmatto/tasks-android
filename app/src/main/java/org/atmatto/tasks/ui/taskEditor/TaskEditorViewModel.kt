package org.atmatto.tasks.ui.taskEditor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import org.atmatto.tasks.data.PreferencesRepository
import org.atmatto.tasks.data.room.Task
import org.atmatto.tasks.domain.GetTaskUseCase
import org.atmatto.tasks.domain.UpdateTaskUseCase
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import javax.inject.Inject

@HiltViewModel
class TaskEditorViewModel @Inject constructor(
	private val getTaskUseCase: GetTaskUseCase,
	private val updateTaskUseCase: UpdateTaskUseCase,
	private val preferencesRepository: PreferencesRepository,
) : ViewModel() {
	fun load(taskId: Long): Flow<Task> = getTaskUseCase(taskId)
		.filterNotNull().take(1) // Unsubscribe from updates

	fun update(
		task: Task,
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
			updateTaskUseCase(
				Task(
					id = task.id,
					title = title,
					description = description,
					createdAt = task.createdAt,
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
