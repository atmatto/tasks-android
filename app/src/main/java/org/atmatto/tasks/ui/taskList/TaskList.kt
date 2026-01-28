package org.atmatto.tasks.ui.taskList

import android.text.format.DateUtils
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.foundation.text.input.delete
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SegmentedListItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import kotlinx.coroutines.flow.map
import org.atmatto.tasks.R
import org.atmatto.tasks.common.ListState
import org.atmatto.tasks.data.room.Task
import org.atmatto.tasks.ui.common.ScaffoldWrapper
import java.time.Duration
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import kotlin.math.absoluteValue

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun TaskList(
	onClickCreateNewTask: () -> Unit,
	onClickViewTask: (id: Long) -> Unit,
	onClickSettings: () -> Unit,
) {
	val viewModel: TaskListViewModel = hiltViewModel()

	val data by viewModel.data.collectAsState()

	var showCategorySheet by remember { mutableStateOf(false) }
	val showCategorySheetState = rememberModalBottomSheetState()

	ScaffoldWrapper(
		title = {
			Text(
				modifier = Modifier.padding(horizontal = 8.dp),
				text = "Tasks",
			)
		},
		scrollable = true,
		searchable = true,
		searchTextFieldState = viewModel.searchState,
		bottomBar = {
			BottomAppBar(
				actions = {
					IconButton(
						onClick = onClickSettings
					) {
						Icon(painterResource(R.drawable.settings_24px), "Settings")
					}
					IconButton(
						onClick = { showCategorySheet = true }
					) {
						Icon(painterResource(R.drawable.filter_list_24px), "Filter")
					}
				},
				floatingActionButton = {
					FloatingActionButton(
						onClick = onClickCreateNewTask
					) {
						Icon(painterResource(R.drawable.add_24px), "Create")
					}
				},
				containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
				contentColor = MaterialTheme.colorScheme.onSurface,
			)
		}
	) { innerPadding ->
		when (data) {
			ListState.AllFiltered -> {
				Box(
					modifier = Modifier
						.fillMaxSize()
						.padding(innerPadding)
						.verticalScroll(rememberScrollState())
						.padding(32.dp),
					contentAlignment = Alignment.Center
				) {
					Column(
						horizontalAlignment = Alignment.CenterHorizontally
					) {
						Icon(
							modifier = Modifier.size(64.dp),
							painter = painterResource(R.drawable.search_off_24px),
							contentDescription = "No results"
						)
						Spacer(Modifier.height(8.dp))
						Text(
							text = "No results",
							style = MaterialTheme.typography.headlineMedium
						)
						Spacer(Modifier.height(12.dp))
						Text(
							text = "No tasks match your criteria",
							style = MaterialTheme.typography.bodyLarge,
							color = MaterialTheme.colorScheme.onSurfaceVariant
						)
						Spacer(Modifier.height(32.dp))
						OutlinedButton(
							onClick = {
								viewModel.searchState.edit {
									delete(0, length)
								}
								viewModel.chosenCategories.clear()
							}
						) {
							Text(
								text = "See all tasks",
								style = MaterialTheme.typography.titleMedium
							)
						}
					}
				}
			}

			ListState.Empty -> {
				Box(
					modifier = Modifier
						.fillMaxSize()
						.padding(innerPadding)
						.verticalScroll(rememberScrollState())
						.padding(32.dp),
					contentAlignment = Alignment.Center
				) {
					Column(
						horizontalAlignment = Alignment.CenterHorizontally
					) {
						Icon(
							modifier = Modifier.size(64.dp),
							painter = painterResource(R.drawable.inventory_24px),
							contentDescription = "No tasks"
						)
						Spacer(Modifier.height(8.dp))
						Text(
							text = "Empty",
							style = MaterialTheme.typography.headlineMedium
						)
						Spacer(Modifier.height(12.dp))
						Text(
							text = "You don't have any tasks",
							style = MaterialTheme.typography.bodyLarge,
							color = MaterialTheme.colorScheme.onSurfaceVariant
						)
					}
				}
			}

			ListState.Loading -> {
				Box(
					modifier = Modifier
						.fillMaxSize()
						.padding(innerPadding),
					contentAlignment = Alignment.Center
				) {
					LoadingIndicator()
				}
			}

			is ListState.Some -> {
				val list = (data as ListState.Some).items
				LazyColumn(
					modifier = Modifier.padding(innerPadding)
						.padding(horizontal = 16.dp),
					verticalArrangement = Arrangement.spacedBy(ListItemDefaults.SegmentedGap),
				) {
					item {
						Spacer(Modifier.height(32.dp))
					}
					itemsIndexed(list) { index, task ->
						val attachmentsCount by viewModel.getAttachments(task.id) // TODO: Ugly
							.map { it.size }
							.collectAsState(0)
						TaskListEntry(
							task = task,
							onClickViewTask = onClickViewTask,
							index = index,
							attachmentCount = attachmentsCount,
							count = list.size,
						)
					}
					item {
						Spacer(Modifier.height(16.dp))
					}
				}
			}
		}
		if (showCategorySheet) {
			ModalBottomSheet(
				onDismissRequest = {
					showCategorySheet = false
				},
				sheetState = showCategorySheetState
			) {
				Column(
					modifier = Modifier.fillMaxSize()
						.padding(horizontal = 16.dp),
				) {
					Row(
						modifier = Modifier.padding(horizontal = 8.dp)
							.fillMaxWidth(),
						horizontalArrangement = Arrangement.SpaceBetween,
						verticalAlignment = Alignment.CenterVertically
					) {
						Column {
							Text(
								text = "Select categories to show",
								style = MaterialTheme.typography.titleLargeEmphasized,
							)
							Spacer(Modifier.height(4.dp))
							val selected = viewModel.chosenCategories.size
							Text(
								text = if (selected == 0) "All categories" else "$selected categories selected",
								color = MaterialTheme.colorScheme.onSurfaceVariant,
								style = MaterialTheme.typography.bodyLarge,
							)
						}
						if (viewModel.chosenCategories.isNotEmpty()) {
							IconButton(
								onClick = { viewModel.chosenCategories.clear() }
							) {
								Icon(
									painterResource(R.drawable.filter_list_off_24px),
									"Clear filters"
								)
							}
						}
					}
					Spacer(Modifier.height(16.dp))
					val colors =
						ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.surfaceBright)
					val categories by viewModel.categories.collectAsState()
					val count = categories.size
					LazyColumn(
						modifier = Modifier.fillMaxHeight(),
						verticalArrangement = Arrangement.spacedBy(ListItemDefaults.SegmentedGap),
					) {
						itemsIndexed(categories) { index, category ->
							SegmentedListItem(
								checked = category.name in viewModel.chosenCategories,
								onCheckedChange = {
									if (viewModel.chosenCategories.contains(category.name)) {
										viewModel.chosenCategories.remove(category.name)
									} else {
										viewModel.chosenCategories.add(category.name)
									}
								},
								shapes = ListItemDefaults.segmentedShapes(index, count),
								colors = colors,
								contentPadding = PaddingValues(
									horizontal = 18.dp,
									vertical = 18.dp
								),
								content = {
									Text(
										text = category.name.ifBlank { "Tasks with no category" },
										style = MaterialTheme.typography.titleMedium,
									)
								},
								trailingContent = {
									Text(
										text = "${category.count}",
										style = MaterialTheme.typography.titleMedium,
									)
								}
							)
						}
						item {
							Spacer(Modifier.height(32.dp))
						}
					}
				}

//				Button(onClick = {
//					scope.launch { sheetState.hide() }.invokeOnCompletion {
//						if (!sheetState.isVisible) {
//							showBottomSheet = false
//						}
//					}
//				}) {
//					Text("Hide bottom sheet")
//				}
			}
		}
	}
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun TaskListEntry(
	task: Task,
	onClickViewTask: (id: Long) -> Unit,
	index: Int,
	count: Int,
	attachmentCount: Int,
	modifier: Modifier = Modifier,
) {
	SegmentedListItem(
		shapes = ListItemDefaults.segmentedShapes(index, count),
		colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.surfaceBright),
		contentPadding = PaddingValues(16.dp),
		onClick = { onClickViewTask(task.id) },
		modifier = Modifier.let { if (task.isCompleted) it.alpha(0.5f) else it }
	) {
		Column(
			modifier = Modifier
				.fillMaxWidth()
		) {
			Text(
				text = task.title,
				maxLines = 2,
				overflow = TextOverflow.Ellipsis,
				style = MaterialTheme.typography.titleLarge,
				textDecoration = if (task.isCompleted) TextDecoration.LineThrough else null
			)
			Spacer(Modifier.height(12.dp))
			if (task.description.isNotBlank()) {
				Text(
					text = task.description,
					maxLines = 2,
					overflow = TextOverflow.Ellipsis,
					style = MaterialTheme.typography.bodyLarge,
					color = MaterialTheme.colorScheme.onSurfaceVariant
				)
				Spacer(Modifier.height(12.dp))
			}
			Row(
				verticalAlignment = Alignment.CenterVertically
			) {
				if (task.category.isNotBlank()) {
					CategoryChip(
						category = task.category
					)
				}
				Spacer(Modifier.weight(1.0f))
				if (attachmentCount > 0) {
					AttachmentsIcon(attachmentCount)
					Spacer(Modifier.width(8.dp))
				}
				DateTimeText(task.dueAt, task.isNotificationEnabled)
			}
		}
	}

//	Box(
//		modifier = modifier.fillMaxWidth()
//			.clickable(onClick = { onClickViewTask(task.id) })
//			.let { if (task.isCompleted) it.alpha(0.5f) else it }
//	) {
//		Column(
//			modifier = Modifier
//				.fillMaxWidth()
//				.padding(16.dp)
//		) {
//			Text(
//				text = task.title.ifBlank { "Unnamed task" },
//				maxLines = 2,
//				overflow = TextOverflow.Ellipsis,
//				style = MaterialTheme.typography.titleLarge,
//				textDecoration = if (task.isCompleted) TextDecoration.LineThrough else null
//			)
//			Spacer(Modifier.height(12.dp))
//			if (task.description.isNotBlank()) {
//				Text(
//					text = task.description,
//					maxLines = 2,
//					overflow = TextOverflow.Ellipsis,
//					style = MaterialTheme.typography.bodyLarge,
//					color = MaterialTheme.colorScheme.onSurfaceVariant
//				)
//				Spacer(Modifier.height(12.dp))
//			}
//			Row(
//				verticalAlignment = Alignment.CenterVertically
//			) {
//				if (task.category.isNotBlank()) {
//					CategoryChip(
//						category = task.category
//					)
//				}
//				Spacer(Modifier.weight(1.0f))
//				DateTimeText(task.dueAt, task.isNotificationEnabled)
//			}
//		}
//	}
//	HorizontalDivider(
//		thickness = 1.dp
//	)
}

@Composable
fun AttachmentsIcon(
	attachmentCount: Int,
	modifier: Modifier = Modifier
) {
	val text = buildAnnotatedString {
		appendInlineContent("icon", "[icon]")
//		append(" ")
		append(attachmentCount.toString())
	}
	val inlineContent = mapOf(
		"icon" to InlineTextContent(
			Placeholder(
				width = 20.sp,
				height = 20.sp,
				placeholderVerticalAlign = PlaceholderVerticalAlign.TextCenter
			)
		) {
			Icon(
				painter = painterResource(R.drawable.attach_file_24px),
				contentDescription = "Has attachments",
				modifier = Modifier.fillMaxSize()
			)
		}
	)
	Text(
		modifier = modifier,
		text = text,
		inlineContent = inlineContent
	)
}

@Composable
fun DateTimeText(instant: Instant, notificationEnabled: Boolean, modifier: Modifier = Modifier) {
	val text = buildAnnotatedString {
		if (notificationEnabled) {
			appendInlineContent("notification", "[notification]")
			append(" ")
		}
		appendInlineContent("icon", "[icon]")
		append(" ")
		append(formatInstant(instant))
	}
	val inlineContent = mapOf(
		"notification" to InlineTextContent(
			Placeholder(
				width = 20.sp,
				height = 20.sp,
				placeholderVerticalAlign = PlaceholderVerticalAlign.TextCenter
			)
		) {
			Icon(
				painter = painterResource(R.drawable.notifications_active_24px),
				contentDescription = "Due at",
				modifier = Modifier.fillMaxSize()
			)
		},
		"icon" to InlineTextContent(
			Placeholder(
				width = 20.sp,
				height = 20.sp,
				placeholderVerticalAlign = PlaceholderVerticalAlign.TextCenter
			)
		) {
			Icon(
				painter = painterResource(R.drawable.date_range_24px),
				contentDescription = "Due at",
				modifier = Modifier.fillMaxSize()
			)
		}
	)
	Text(
		modifier = modifier,
		text = text,
		inlineContent = inlineContent
	)
}

@Composable
fun CategoryChip(modifier: Modifier = Modifier, category: String) {
	Surface(
		modifier = modifier.defaultMinSize(minHeight = 32.dp),
		shape = MaterialTheme.shapes.small,
		contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
		color = MaterialTheme.colorScheme.secondaryContainer
	) {
		Row(
			modifier = Modifier.padding(horizontal = 12.dp),
			verticalAlignment = Alignment.CenterVertically,
		) {
			Text(
				text = category,
				style = MaterialTheme.typography.labelLarge
			)
		}
	}
}

fun formatInstant(instant: Instant): String {
	val now = Instant.now()
	return if (instant.atZone(ZoneId.systemDefault()).toLocalDate() == now.atZone(ZoneId.systemDefault()).toLocalDate()) {
		instant.atZone(ZoneId.systemDefault())
			.format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT))
	} else if (Duration.between(instant, now).toDays().absoluteValue <= 7) {
		DateUtils.getRelativeTimeSpanString(
			instant.toEpochMilli(),
			now.toEpochMilli(),
			DateUtils.MINUTE_IN_MILLIS
		).toString()
	} else {
		instant.atZone(ZoneId.systemDefault())
			.format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM))
	}
}
