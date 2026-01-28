package org.atmatto.tasks.data.room

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Instant

@Entity
data class Task(
	@PrimaryKey(autoGenerate = true) val id: Long,
	val title: String,
	val description: String,
	val createdAt: Instant,
	val dueAt: Instant,
	val isCompleted: Boolean,
	val isNotificationEnabled: Boolean,
	val category: String,
)
