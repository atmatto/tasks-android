package org.atmatto.tasks.data

import android.net.Uri
import kotlinx.coroutines.flow.first
import org.atmatto.tasks.data.fs.AttachmentFileStorage
import org.atmatto.tasks.data.room.Attachment
import org.atmatto.tasks.data.room.AttachmentDao
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AttachmentRepository @Inject constructor(
    private val attachmentFileStorage: AttachmentFileStorage,
    private val attachmentDao: AttachmentDao,
) {
	suspend fun createAttachment(
		taskId: Long,
		uri: Uri
	) {
		val attachmentId = UUID.randomUUID().toString()
		val fileDetails = attachmentFileStorage.save(attachmentId, uri)
		val metadata = Attachment(
            id = attachmentId,
            taskId = taskId,
            originalName = fileDetails.name,
			mimeType = fileDetails.mimeType,
        )
		attachmentDao.insert(metadata)
	}

	fun getAttachmentsForTask(taskId: Long) = attachmentDao.getAttachmentsForTask(taskId)

	fun openAttachment(attachmentId: String, mimeType: String) = attachmentFileStorage.open(attachmentId, mimeType)

	suspend fun deleteAttachment(
		attachmentId: String,
	) {
		attachmentFileStorage.delete(attachmentId)
		attachmentDao.deleteById(attachmentId)
	}

	suspend fun deleteAllAttachmentsOfTask(
		taskId: Long,
	) {
		val attachments = getAttachmentsForTask(taskId).first()
		attachments.forEach {
			deleteAttachment(it.id)
		}
	}
}
