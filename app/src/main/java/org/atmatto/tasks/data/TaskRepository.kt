package org.atmatto.tasks.data

import org.atmatto.tasks.data.room.Task
import org.atmatto.tasks.data.room.TaskDao
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TaskRepository @Inject constructor(
	private val taskDao: TaskDao
) {
	fun getAllStartingFromMostUrgent() = taskDao.getAllStartingFromMostUrgent()

	fun getById(id: Long) = taskDao.getById(id)

	suspend fun create(task: Task) = taskDao.insert(task)

	suspend fun update(task: Task) = taskDao.update(task)

	suspend fun setState(id: Long, isCompleted: Boolean) = taskDao.setState(id, isCompleted)

	suspend fun delete(task: Task) {
		taskDao.delete(task)
	}

	fun getAllCategories(includeCompleted: Boolean) =
		if (includeCompleted)
			taskDao.getAllCategories()
		else
			taskDao.getAllCategoriesExcludingCompleted()
}
