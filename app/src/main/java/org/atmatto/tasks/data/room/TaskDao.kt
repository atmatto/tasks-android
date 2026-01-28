package org.atmatto.tasks.data.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Embedded
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Relation
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
	@Query("SELECT * FROM Task ORDER BY dueAt ASC")
	fun getAllStartingFromMostUrgent(): Flow<List<Task>>

	@Query("SELECT * FROM Task WHERE id = :id")
	fun getById(id: Long): Flow<Task?>

	@Insert
	suspend fun insert(task: Task)

	@Update
	suspend fun update(task: Task)

	@Delete
	suspend fun delete(task: Task)

	@Query("UPDATE Task SET isCompleted = :isCompleted WHERE id = :id")
	suspend fun setState(id: Long, isCompleted: Boolean)

	@Query("""
		SELECT category AS name, COUNT(*) AS count FROM Task
		GROUP BY category
		ORDER BY
			CASE WHEN category = '' THEN 0 ELSE 1 END,
			COUNT(*) DESC
	""")
	fun getAllCategories(): Flow<List<Category>>

	@Query("""
		SELECT category AS name, COUNT(*) AS count FROM Task
		WHERE isCompleted = 0
		GROUP BY category
		ORDER BY
			CASE WHEN category = '' THEN 0 ELSE 1 END,
			COUNT(*) DESC
	""")
	fun getAllCategoriesExcludingCompleted(): Flow<List<Category>>
}

data class TaskWithAttachments(
	@Embedded val task: Task,
	@Relation(
		parentColumn = "id",
		entityColumn = "taskId"
	)
	val attachments: List<Attachment>
)

data class Category(
	val name: String,
	val count: Long
)
