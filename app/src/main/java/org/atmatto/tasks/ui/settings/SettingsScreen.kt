package org.atmatto.tasks.ui.settings

import android.widget.Toast
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedListItem
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import org.atmatto.tasks.R
import org.atmatto.tasks.notifications.requestNotificationsPermission
import org.atmatto.tasks.ui.common.ScaffoldWrapper
import org.atmatto.tasks.common.getActivity

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SettingsScreen(
	onClose: () -> Unit
) {
	val viewModel: SettingsViewModel = hiltViewModel()

	val showCompletedTasks by viewModel.getShowCompleted().collectAsState(true)
	val notificationMinutes by viewModel.getNotificationMinutes().collectAsState(5)

	var showNotificationsTimeDialog by remember { mutableStateOf(false) }

	ScaffoldWrapper(
		title = {
			Text(
				modifier = Modifier.padding(horizontal = 8.dp),
				text = "Settings",
			)
		},
		navigationIcon = {
			IconButton(
				modifier = Modifier.padding(start = 14.dp),
				onClick = onClose,
				colors = IconButtonDefaults.iconButtonColors(
					containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
					contentColor = MaterialTheme.colorScheme.onSurfaceVariant
				)
			) {
				Icon(
					painterResource(R.drawable.arrow_back_24px),
					"Back"
				)
			}
		},
		scrollable = true,
	) { innerPadding ->
		Column(
			modifier = Modifier
				.fillMaxHeight()
				.padding(innerPadding)
				.verticalScroll(rememberScrollState())
				.padding(top = 18.dp)
				.padding(16.dp),
			verticalArrangement = Arrangement.spacedBy(ListItemDefaults.SegmentedGap),
		) {
			val colors =
				ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.surfaceBright)
			Text(
				modifier = Modifier.padding(horizontal = 8.dp)
					.padding(bottom = 8.dp),
				text = "Notifications",
				style = MaterialTheme.typography.labelLarge,
				color = MaterialTheme.colorScheme.primary
			)
			SegmentedListItem(
				onClick = { showNotificationsTimeDialog = true },
				shapes = ListItemDefaults.segmentedShapes(0, 2),
				colors = colors,
				contentPadding = PaddingValues(horizontal = 18.dp, vertical = 18.dp),
				content = {
					Text(
						text = "Notify me before due time",
						style = MaterialTheme.typography.titleMedium,
					)
				},
				supportingContent = {
					Text(
						text = "$notificationMinutes minutes before", // TODO: Singular/plural here and in category filter
						color = MaterialTheme.colorScheme.onSurfaceVariant,
						style = MaterialTheme.typography.bodyLarge,
					)
				}
			)
			if (showNotificationsTimeDialog) {
				NotificationsTimeDialog(
					notificationMinutes = notificationMinutes,
					onClose = { showNotificationsTimeDialog = false },
					setNotificationMinutes = viewModel::setNotificationMinutes
				)
			}
			val localContext = LocalContext.current
			SegmentedListItem(
				onClick = {
					var requested = false
					localContext.getActivity()
						?.let { context -> requested = requestNotificationsPermission(context) }
					viewModel.sendTestNotification()
					if (requested) {
						Toast.makeText(
							localContext,
							"Enable notifications and send once more",
							Toast.LENGTH_LONG
						)
							.show()
					}
				},
				shapes = ListItemDefaults.segmentedShapes(1, 2),
				colors = colors,
				contentPadding = PaddingValues(horizontal = 18.dp, vertical = 18.dp),
				content = {
					Text(
						text = "Send test notification",
						style = MaterialTheme.typography.titleMedium,
					)
				}
			)
			Text(
				modifier = Modifier.padding(horizontal = 8.dp)
					.padding(top = 24.dp, bottom = 8.dp),
				text = "Filtering",
				style = MaterialTheme.typography.labelLarge,
				color = MaterialTheme.colorScheme.primary
			)
			val hideCompletedTasksInteractionSource = remember { MutableInteractionSource() }
			SegmentedListItem(
				checked = !showCompletedTasks,
				onCheckedChange = { viewModel.setShowCompleted(!it) },
				shapes = ListItemDefaults.segmentedShapes(
					0,
					1,
					ListItemDefaults.shapes(shape = ShapeDefaults.Large)
				),
				colors = colors,
				contentPadding = PaddingValues(horizontal = 18.dp, vertical = 18.dp),
				interactionSource = hideCompletedTasksInteractionSource,
				content = {
					Text(
						text = "Hide completed tasks",
						style = MaterialTheme.typography.titleMedium,
					)
				},
				trailingContent = {
					Switch(
						checked = !showCompletedTasks,
						onCheckedChange = null,
						interactionSource = hideCompletedTasksInteractionSource
					)
				}
			)
		}
	}
}
