package org.atmatto.tasks.domain

import org.atmatto.tasks.data.AttachmentRepository
import javax.inject.Inject

class DeleteAllAttachmentsOfTaskUseCase @Inject constructor(
	private val attachmentRepository: AttachmentRepository,
) {
	suspend operator fun invoke(taskId: Long) = attachmentRepository.deleteAllAttachmentsOfTask(taskId)
}
