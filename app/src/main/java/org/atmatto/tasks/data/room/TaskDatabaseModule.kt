package org.atmatto.tasks.data.room

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object TaskDatabaseModule {
	@Singleton
	@Provides
	fun taskDatabase(@ApplicationContext app: Context): TaskDatabase =
		Room.databaseBuilder(app, TaskDatabase::class.java, "task_database")
			.addMigrations(TaskDatabase.MIGRATION_1_2)
			.build()

	@Singleton
	@Provides
	fun taskDao(taskDatabase: TaskDatabase) = taskDatabase.taskDao()

	@Singleton
	@Provides
	fun attachmentDao(taskDatabase: TaskDatabase) = taskDatabase.attachmentDao()
}
