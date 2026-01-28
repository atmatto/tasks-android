package org.atmatto.tasks.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import kotlinx.serialization.Serializable
import org.atmatto.tasks.ui.settings.SettingsScreen
import org.atmatto.tasks.ui.taskCreator.TaskCreator
import org.atmatto.tasks.ui.taskEditor.TaskEditor
import org.atmatto.tasks.ui.taskList.TaskList
import org.atmatto.tasks.ui.taskViewer.TaskViewer

@Composable
fun TasksNavigation(
	deepLinkViewModel: DeepLinkViewModel
) {
	val backStack = rememberNavBackStack(Screen.TaskList)

	LaunchedEffect(Unit) {
		deepLinkViewModel.event.collect { event ->
			when (event) {
				DeepLink.Settings ->
					if (backStack.lastOrNull() !is Screen.Settings) {
						backStack.removeIf { it is Screen.Settings } // No need to have multiple settings screens in history
						backStack.add(Screen.Settings)
					}
				is DeepLink.Task ->
					when (backStack.lastOrNull()) {
						Screen.TaskEditor(event.taskId),
						Screen.TaskViewer(event.taskId) -> {}
						else -> backStack.add(Screen.TaskViewer(event.taskId))
					}
			}
		}
	}

	NavDisplay(
		backStack = backStack,
		entryDecorators = listOf(
			rememberSaveableStateHolderNavEntryDecorator(),
			rememberViewModelStoreNavEntryDecorator()
		),
		onBack = { backStack.removeLastOrNull() },
		entryProvider = { key ->
			if (key is Screen) {
				when (key) {
					is Screen.TaskList -> NavEntry(key) {
						TaskList(
							onClickCreateNewTask = { backStack.add(Screen.TaskCreator) },
							onClickViewTask = { id -> backStack.add(Screen.TaskViewer(id)) },
							onClickSettings = { backStack.add(Screen.Settings) },
						)
					}
					is Screen.TaskCreator -> NavEntry(key) {
						TaskCreator(
							onClose = { backStack.removeLastOrNull() }
						)
					}
					is Screen.TaskViewer -> NavEntry(key) {
						TaskViewer(
							taskId = key.id,
							onClose = { backStack.removeLastOrNull() },
							onClickEdit = { backStack.add(Screen.TaskEditor(key.id)) }
						)
					}
					is Screen.TaskEditor -> NavEntry(key) {
						TaskEditor(
							taskId = key.id,
							onClose = { backStack.removeLastOrNull() }
						)
					}
					is Screen.Settings -> NavEntry(key) {
						SettingsScreen(
							onClose = { backStack.removeLastOrNull() }
						)
					}
				}
			} else {
				throw IllegalStateException("NavKey is not Screen")
			}
		}
	)
}

// TODO: Faster animations
// TODO: Sanitize backStack (e.g. it should be impossible to have two TaskViewers on the stack)

@Serializable
sealed class Screen : NavKey {
	@Serializable
	data object Settings : Screen()
	@Serializable
	data object TaskList : Screen()
	@Serializable
	data class TaskViewer(val id: Long) : Screen()
	@Serializable
	data class TaskEditor(val id: Long) : Screen()
	@Serializable
	data object TaskCreator : Screen()
}
