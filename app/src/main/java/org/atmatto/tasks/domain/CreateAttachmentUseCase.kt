package org.atmatto.tasks.domain

import android.net.Uri
import org.atmatto.tasks.data.AttachmentRepository
import javax.inject.Inject

class CreateAttachmentUseCase @Inject constructor(
	private val attachmentRepository: AttachmentRepository,
) {
	suspend operator fun invoke(taskId: Long, uri: Uri) {
		attachmentRepository.createAttachment(taskId, uri)
	}
}
