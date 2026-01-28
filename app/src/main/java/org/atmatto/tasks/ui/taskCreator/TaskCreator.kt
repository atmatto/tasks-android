package org.atmatto.tasks.ui.taskCreator

import android.icu.text.DateFormat
import android.icu.util.Calendar
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.getSelectedDate
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
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
import org.atmatto.tasks.ui.taskViewer.formatTaskNotificationDateTime
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import java.util.Date

@Composable
fun TaskCreator(
	onClose: () -> Unit,
) {
	val viewModel: TaskCreatorViewModel = hiltViewModel()

	val notificationMinutes by viewModel.getNotificationMinutes().collectAsState(1)

	val titleState = rememberTextFieldState()
	val descriptionState = rememberTextFieldState()
	val categoryState = rememberTextFieldState()
	var isCompleted by remember { mutableStateOf(false) }
	var isNotificationEnabled by remember { mutableStateOf(false) }
	var dueDate by remember { mutableStateOf(LocalDate.now().plus(1, ChronoUnit.DAYS)) }
	var showDueDateModal by remember { mutableStateOf(false) }
	var dueHour by remember { mutableStateOf(Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) }
	var dueMinute by remember { mutableStateOf(Calendar.getInstance().get(Calendar.MINUTE)) }
	var showDueTimeModal by remember { mutableStateOf(false) }

	ScaffoldWrapper(
		title = { Text("New task") },
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
							viewModel.create(
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
						Text("Create")
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
							val upEvent = waitForUpOrCancellation(pass = PointerEventPass.Initial)
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
							val upEvent = waitForUpOrCancellation(pass = PointerEventPass.Initial)
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
}

fun formatLocalDate(localDate: LocalDate): String {
	val date = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant())
	return DateFormat.getDateInstance(DateFormat.SHORT).format(date)
}

@Composable
fun DatePickerModal(
	initialSelectedDate: LocalDate,
	onDateSelected: (LocalDate) -> Unit,
	onDismiss: () -> Unit
) {
	val datePickerState = rememberDatePickerState(initialSelectedDate = initialSelectedDate)

	DatePickerDialog(
		onDismissRequest = onDismiss,
		confirmButton = {
			TextButton(onClick = {
				datePickerState.getSelectedDate()?.let { onDateSelected(it) }
				onDismiss()
			}) {
				Text("OK")
			}
		},
		dismissButton = {
			TextButton(onClick = onDismiss) {
				Text("Cancel")
			}
		}
	) {
		DatePicker(
			state = datePickerState,
			showModeToggle = false
		)
	}
}

fun formatHourAndMinute(hour: Int, minute: Int): String {
	val calendar = Calendar.getInstance().apply {
		set(Calendar.HOUR_OF_DAY, hour)
		set(Calendar.MINUTE, minute)
	}
	return DateFormat.getTimeInstance(DateFormat.SHORT).format(calendar.time)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerModal(
	initialHour: Int,
	initialMinute: Int,
	onConfirm: (TimePickerState) -> Unit,
	onDismiss: () -> Unit,
) {
	val timePickerState = rememberTimePickerState(
		initialHour = initialHour,
		initialMinute = initialMinute
	)

	TimePickerDialog(
		onDismiss = { onDismiss() },
		onConfirm = {
			onConfirm(timePickerState)
			onDismiss()
		}
	) {
		TimePicker(
			state = timePickerState,
		)
	}
}

@Composable
fun TimePickerDialog(
	onDismiss: () -> Unit,
	onConfirm: () -> Unit,
	content: @Composable () -> Unit
) {
	AlertDialog(
		onDismissRequest = onDismiss,
		dismissButton = {
			TextButton(onClick = { onDismiss() }) {
				Text("Cancel")
			}
		},
		confirmButton = {
			TextButton(onClick = { onConfirm() }) {
				Text("OK")
			}
		},
		text = { content() }
	)
}
