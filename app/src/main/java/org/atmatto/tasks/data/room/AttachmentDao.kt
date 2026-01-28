package org.atmatto.tasks.data.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AttachmentDao {
	@Insert
	suspend fun insert(attachment: Attachment)

	@Query("SELECT * FROM Attachment WHERE taskId = :taskId")
	fun getAttachmentsForTask(taskId: Long): Flow<List<Attachment>>

	@Query("DELETE FROM Attachment WHERE id = :id")
	suspend fun deleteById(id: String)

//	@Query("DELETE FROM Attachment WHERE taskId = :taskId")
//	suspend fun deleteAllForTask(taskId: String)
}
