package org.atmatto.tasks.domain

import org.atmatto.tasks.data.AttachmentRepository
import javax.inject.Inject

class DeleteAttachmentUseCase @Inject constructor(
	private val attachmentRepository: AttachmentRepository,
) {
	suspend operator fun invoke(attachmentId: String) = attachmentRepository.deleteAttachment(attachmentId)
}
