package org.atmatto.tasks.ui.taskEditor

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import org.atmatto.tasks.R
import org.atmatto.tasks.common.getActivity
import org.atmatto.tasks.notifications.requestNotificationsPermission
import org.atmatto.tasks.ui.common.ScaffoldWrapper
import org.atmatto.tasks.ui.taskCreator.DatePickerModal
import org.atmatto.tasks.ui.taskCreator.TimePickerModal
import org.atmatto.tasks.ui.taskCreator.formatHourAndMinute
import org.atmatto.tasks.ui.taskCreator.formatLocalDate
import org.atmatto.tasks.ui.taskViewer.formatTaskNotificationDateTime
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId

// TODO: TaskEditor and TaskCreator: Allow scrolling when keyboard is expanded

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun TaskEditor(
	taskId: Long,
	onClose: () -> Unit,
) {
	val viewModel: TaskEditorViewModel = hiltViewModel()

	val maybeTask by viewModel.load(taskId).collectAsState(null)
	val notificationMinutes by viewModel.getNotificationMinutes().collectAsState(1)

	maybeTask?.let { task ->
		val titleState = rememberTextFieldState(task.title)
		val descriptionState = rememberTextFieldState(task.description)
		val categoryState = rememberTextFieldState(task.category)
		var isCompleted by remember { mutableStateOf(task.isCompleted) }
		var isNotificationEnabled by remember { mutableStateOf(task.isNotificationEnabled) }
		val dueAt = task.dueAt.atZone(ZoneId.systemDefault())
		var dueDate by remember { mutableStateOf(dueAt.toLocalDate()) }
		var showDueDateModal by remember { mutableStateOf(false) }
		var dueHour by remember { mutableStateOf(dueAt.hour) }
		var dueMinute by remember { mutableStateOf(dueAt.minute) }
		var showDueTimeModal by remember { mutableStateOf(false) }

		ScaffoldWrapper(
			title = { Text("Editing task") },
			scrollable = false,
			bottomBar = {
				BottomAppBar(
					contentPadding = PaddingValues(16.dp),
					content = {
						TextButton(
							onClick = onClose // TODO: Show a dialog
						) {
							Text("Cancel")
						}
						Spacer(
							modifier = Modifier.weight(1.0f)
						)
						Button(
							onClick = {
								viewModel.update(
									task,
									titleState.text.toString(),
									descriptionState.text.toString(),
									categoryState.text.toString(),
									isCompleted,
									isNotificationEnabled,
									dueDate,
									dueHour,
									dueMinute
								)
								onClose()
							}
						) {
							Text("Save")
						}
					}
				)
			}
		) { innerPadding ->
			Column(
				modifier = Modifier.padding(innerPadding)
					.verticalScroll(rememberScrollState())
					.padding(16.dp),
				verticalArrangement = Arrangement.spacedBy(16.dp)
			) {
				TextField(
					state = titleState,
					modifier = Modifier.fillMaxWidth(),
					label = { Text("Title") },
					lineLimits = TextFieldLineLimits.SingleLine
				)
				TextField(
					state = categoryState,
					modifier = Modifier.fillMaxWidth(),
					label = { Text("Category") },
					lineLimits = TextFieldLineLimits.SingleLine
				)
				TextField(
					state = descriptionState,
					modifier = Modifier.fillMaxWidth()
						.heightIn(max = 200.dp),
					label = { Text("Description") },
					lineLimits = TextFieldLineLimits.MultiLine()
				)
				Row(
					verticalAlignment = Alignment.CenterVertically,
					horizontalArrangement = Arrangement.spacedBy(16.dp)
				) {
					Switch(
						checked = isCompleted,
						onCheckedChange = { isCompleted = it }
					)
					Text("Completed")
				}
				TextField(
					value = formatLocalDate(dueDate),
					onValueChange = {},
					label = { Text("Due date") },
					trailingIcon = {
						Icon(
							painterResource(R.drawable.date_range_24px),
							contentDescription = "Select due date"
						)
					},
					readOnly = true,
					modifier = Modifier
						.fillMaxWidth()
						.pointerInput(showDueDateModal) {
							awaitEachGesture {
								awaitFirstDown(pass = PointerEventPass.Initial)
								val upEvent =
									waitForUpOrCancellation(pass = PointerEventPass.Initial)
								if (upEvent != null) {
									showDueDateModal = true
								}
							}
						}
				)
				if (showDueDateModal) {
					DatePickerModal(
						initialSelectedDate = dueDate,
						onDateSelected = { dueDate = it },
						onDismiss = { showDueDateModal = false }
					)
				}
				TextField(
					value = formatHourAndMinute(dueHour, dueMinute),
					onValueChange = {},
					label = { Text("Due time") },
					trailingIcon = {
						Icon(
							painterResource(R.drawable.schedule_24px),
							contentDescription = "Select due time"
						)
					},
					readOnly = true,
					modifier = Modifier
						.fillMaxWidth()
						.pointerInput(showDueTimeModal) {
							awaitEachGesture {
								awaitFirstDown(pass = PointerEventPass.Initial)
								val upEvent =
									waitForUpOrCancellation(pass = PointerEventPass.Initial)
								if (upEvent != null) {
									showDueTimeModal = true
								}
							}
						}
				)
				if (showDueTimeModal) {
					TimePickerModal(
						initialHour = dueHour,
						initialMinute = dueMinute,
						onConfirm = {
							dueHour = it.hour
							dueMinute = it.minute
						},
						onDismiss = { showDueTimeModal = false }
					)
				}
				Row(
					verticalAlignment = Alignment.CenterVertically,
					horizontalArrangement = Arrangement.spacedBy(16.dp)
				) {
					val localContext = LocalContext.current
					Switch(
						checked = isNotificationEnabled,
						onCheckedChange = {
							isNotificationEnabled = it
							if (isNotificationEnabled) {
								localContext.getActivity()
									?.let { context -> requestNotificationsPermission(context) }
							}
						}
					)
					Column {
						Text("Send notification")
						val timestampText = formatTaskNotificationDateTime(
							LocalDateTime.of(dueDate, LocalTime.of(dueHour, dueMinute))
								.atZone(ZoneId.systemDefault())
								.toInstant(),
							notificationMinutes
						)
						if (isNotificationEnabled) {
							Text("A notification will be sent at ${timestampText}")
						} else {
							Text("No notification will be sent")
						}
					}
				}
			}
		}
	} ?: ScaffoldWrapper(
		title = { Text("") },
		scrollable = false,
		bottomBar = {
			BottomAppBar(
				actions = {},
				floatingActionButton = {},
			)
		}
	) { innerPadding ->
		Box(
			modifier = Modifier
				.fillMaxSize()
				.padding(innerPadding),
			contentAlignment = Alignment.Center
		) {
			LoadingIndicator()
		}
	}
}
