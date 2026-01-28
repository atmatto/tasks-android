package org.atmatto.tasks.data.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Attachment(
	@PrimaryKey val id: String = "",
	val taskId: Long = 0,
	val originalName: String = "",
	val mimeType: String = "",
)
