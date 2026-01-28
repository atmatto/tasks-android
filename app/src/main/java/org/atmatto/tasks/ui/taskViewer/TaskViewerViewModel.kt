package org.atmatto.tasks.ui.taskViewer

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.atmatto.tasks.data.PreferencesRepository
import org.atmatto.tasks.data.room.Task
import org.atmatto.tasks.domain.CreateAttachmentUseCase
import org.atmatto.tasks.domain.DeleteAttachmentUseCase
import org.atmatto.tasks.domain.DeleteTaskUseCase
import org.atmatto.tasks.domain.GetAttachmentsForTaskUseCase
import org.atmatto.tasks.domain.GetTaskUseCase
import org.atmatto.tasks.domain.OpenAttachmentUseCase
import org.atmatto.tasks.domain.SetTaskStateUseCase
import javax.inject.Inject

@HiltViewModel
class TaskViewerViewModel @Inject constructor(
	private val getTaskUseCase: GetTaskUseCase,
	private val deleteTaskUseCase: DeleteTaskUseCase,
	private val setTaskStateUseCase: SetTaskStateUseCase,
	private val createAttachmentUseCase: CreateAttachmentUseCase,
	private val getAttachmentsForTaskUseCase: GetAttachmentsForTaskUseCase,
	private val openAttachmentUseCase: OpenAttachmentUseCase,
	private val deleteAttachmentUseCase: DeleteAttachmentUseCase,
	private val preferencesRepository: PreferencesRepository,
) : ViewModel() {
	fun getTask(id: Long)  = getTaskUseCase(id)

	fun deleteTask(task: Task) = viewModelScope.launch { deleteTaskUseCase(task) }

	fun toggleCompleted(task: Task) = viewModelScope.launch { setTaskStateUseCase(task.id, !task.isCompleted) }

	fun getNotificationMinutes() = preferencesRepository.getNotificationMinutes()

	fun createAttachment(taskId: Long, uri: Uri) = viewModelScope.launch { createAttachmentUseCase(taskId, uri) }

	fun getAttachments(taskId: Long) = getAttachmentsForTaskUseCase(taskId)

	fun openAttachment(attachmentId: String, mimeType: String) = openAttachmentUseCase(attachmentId, mimeType)

	fun deleteAttachment(attachmentId: String) = viewModelScope.launch { deleteAttachmentUseCase(attachmentId) }
}
