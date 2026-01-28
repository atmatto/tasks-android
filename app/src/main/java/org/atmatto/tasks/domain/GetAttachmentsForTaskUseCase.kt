package org.atmatto.tasks.domain

import org.atmatto.tasks.data.AttachmentRepository
import javax.inject.Inject

class GetAttachmentsForTaskUseCase @Inject constructor(
	private val attachmentRepository: AttachmentRepository,
) {
	operator fun invoke(taskId: Long) = attachmentRepository.getAttachmentsForTask(taskId)
}
