package org.atmatto.tasks.ui.taskViewer

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import org.atmatto.tasks.R
import org.atmatto.tasks.ui.common.ScaffoldWrapper
import org.atmatto.tasks.ui.taskList.CategoryChip
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun TaskViewer(
	taskId: Long,
	onClose: () -> Unit,
	onClickEdit: () -> Unit
) {
	val viewModel: TaskViewerViewModel = hiltViewModel()

	val maybeTask by viewModel.getTask(taskId).collectAsState(null)
	val attachments by viewModel.getAttachments(taskId).collectAsState(emptyList())
	val notificationMinutes by viewModel.getNotificationMinutes().collectAsState(1)

	maybeTask?.let { task ->
		val filePicker = rememberLauncherForActivityResult(
			contract = ActivityResultContracts.GetContent()
		) { uri: Uri? ->
			uri?.let { viewModel.createAttachment(task.id, it) }
		}

		ScaffoldWrapper(
			title = {
				Text(
					text = task.title.ifBlank { "Unnamed task" },
					maxLines = 2,
					overflow = TextOverflow.Ellipsis,
					textDecoration = if (task.isCompleted) TextDecoration.LineThrough else null
				)
			},
			scrollable = false,
			bottomBar = {
				BottomAppBar(
					actions = {
						IconButton(
							onClick = onClickEdit
						) {
							Icon(
								painterResource(R.drawable.edit_24px),
								"Edit task"
							)
						}
						IconButton(
							onClick = {
								viewModel.deleteTask(task)
								onClose()
							}
						) {
							Icon(
								painterResource(R.drawable.delete_24px),
								"Delete task"
							)
						}
						IconButton(
							onClick = {
								filePicker.launch("*/*")
							}
						) {
							Icon(
								painterResource(R.drawable.attach_file_24px),
								"Attach"
							)
						}
					},
					floatingActionButton = {
						ExtendedFloatingActionButton(
							onClick = {
								viewModel.toggleCompleted(task)
							},
							icon = {
								Icon(
									painterResource(R.drawable.check_circle_24px),
									null
								)
							},
							text = { Text(if (task.isCompleted) "Mark as not done" else "Mark as done") }
						)
					},
				)
			}
		) { innerPadding ->
			Column(
				modifier = Modifier.padding(innerPadding)
					.verticalScroll(rememberScrollState())
					.padding(16.dp)
					.fillMaxWidth(),
				verticalArrangement = Arrangement.spacedBy(16.dp)
			) {
				if (task.category.isNotBlank()) {
//					Text(
//						text = "Category",
//						style = MaterialTheme.typography.titleLarge
//					)
					CategoryChip(
						category = task.category
					)
				}
				if (task.description.isNotBlank()) {
					Text(
						text = "Description",
						style = MaterialTheme.typography.titleLarge
					)
					Text(
						text = task.description,
						style = MaterialTheme.typography.bodyLarge,
						color = MaterialTheme.colorScheme.onSurfaceVariant
					)
				}
				Text(
					text = "Due at",
					style = MaterialTheme.typography.titleLarge
				)
				Text(
					text = task.dueAt.atZone(ZoneId.systemDefault())
						.format(
							DateTimeFormatter.ofLocalizedDateTime(
								FormatStyle.LONG,
								FormatStyle.SHORT
							)
						)
				)
				if (task.isNotificationEnabled) {
					Row(
						verticalAlignment = Alignment.CenterVertically,
						horizontalArrangement = Arrangement.spacedBy(12.dp)
					) {
						Icon(
							painterResource(R.drawable.notifications_active_24px),
							"Notification"
						)
						Text(
							text = formatTaskNotificationDateTime(task.dueAt, notificationMinutes)
						)
					}
				}
				Text(
					text = "Created at",
					style = MaterialTheme.typography.titleLarge
				)
				Text(
					text = task.createdAt.atZone(ZoneId.systemDefault())
						.format(
							DateTimeFormatter.ofLocalizedDateTime(
								FormatStyle.LONG,
								FormatStyle.SHORT
							)
						)
				)
				if (attachments.isNotEmpty()) {
					Text(
						text = "Attachments",
						style = MaterialTheme.typography.titleLarge
					)
					Column(
						modifier = Modifier
							.fillMaxWidth(),
						verticalArrangement = Arrangement.spacedBy(ListItemDefaults.SegmentedGap),
					) {
						val count = attachments.size
						attachments.forEachIndexed { index, attachment ->
							SegmentedListItem(
								onClick = {
									viewModel.openAttachment(attachment.id, attachment.mimeType)
								},
								shapes = ListItemDefaults.segmentedShapes(index, count),
								colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.surfaceBright),
								contentPadding = PaddingValues(horizontal = 18.dp, vertical = 18.dp),
								content = {
									Text(
										text = attachment.originalName,
										style = MaterialTheme.typography.titleMedium,
									)
								},
								trailingContent = {
									IconButton(
										onClick = {
											viewModel.deleteAttachment(attachment.id)
										}
									) {
										Icon(painterResource(R.drawable.delete_24px), "Delete attachment")
									}
								}
							)
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

fun formatTaskNotificationDateTime(
	dueAt: Instant,
	notificationMinutes: Int
): String = dueAt.atZone(ZoneId.systemDefault())
	.let { dueAt ->
		val notification = dueAt.minusMinutes(notificationMinutes.toLong())
		if (dueAt.dayOfMonth == notification.dayOfMonth) {
			notification.format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT))
		} else {
			notification.format(
				DateTimeFormatter.ofLocalizedDateTime(
					FormatStyle.LONG,
					FormatStyle.SHORT
				)
			)
		}
	}
