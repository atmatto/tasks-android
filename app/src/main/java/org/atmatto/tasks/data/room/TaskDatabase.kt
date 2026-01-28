package org.atmatto.tasks.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import java.time.Instant

@Database(
	version = 2,
	entities = [Task::class, Attachment::class]
)
@TypeConverters(Converters::class)
abstract class TaskDatabase : RoomDatabase() {
	abstract fun taskDao(): TaskDao
	abstract fun attachmentDao(): AttachmentDao

	companion object {
		val MIGRATION_1_2 = object : Migration(1, 2) {
			override fun migrate(database: SupportSQLiteDatabase) {
				database.beginTransaction()
				try {
					database.execSQL("CREATE TABLE Attachment2 (`id` TEXT NOT NULL, `taskId` INTEGER NOT NULL, `originalName` TEXT NOT NULL, `mimeType` TEXT NOT NULL, PRIMARY KEY(`id`))")
					database.execSQL("INSERT INTO Attachment2 SELECT id, taskId, originalName, 'image/jpeg' FROM Attachment")
					database.execSQL("DROP TABLE Attachment")
					database.execSQL("ALTER TABLE Attachment2 RENAME TO Attachment")
					database.setTransactionSuccessful()
				} finally {
					database.endTransaction()
				}
			}
		}
	}
}

class Converters {
	@TypeConverter
	fun instantToLong(value: Instant): Long {
		return value.toEpochMilli()
	}

	@TypeConverter
	fun longToInstant(value: Long): Instant {
		return Instant.ofEpochMilli(value)
	}
}
