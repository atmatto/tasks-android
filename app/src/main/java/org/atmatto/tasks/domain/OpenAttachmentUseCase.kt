package org.atmatto.tasks.domain

import org.atmatto.tasks.data.AttachmentRepository
import javax.inject.Inject

class OpenAttachmentUseCase @Inject constructor(
	private val attachmentRepository: AttachmentRepository,
) {
	operator fun invoke(attachmentId: String, mimeType: String) = attachmentRepository.openAttachment(attachmentId, mimeType)
}
