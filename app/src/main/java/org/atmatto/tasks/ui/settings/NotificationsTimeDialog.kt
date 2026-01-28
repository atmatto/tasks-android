package org.atmatto.tasks.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@Composable
fun NotificationsTimeDialog(
	notificationMinutes: Int,
	onClose: () -> Unit,
	setNotificationMinutes: (Int) -> Unit
) {
	var sliderPosition by remember {
		mutableFloatStateOf(
			mapMinutesToSliderPosition(notificationMinutes)
		)
	}
	Dialog(
		onDismissRequest = onClose,
		content = {
			Card(
				modifier = Modifier.fillMaxWidth(),
				shape = RoundedCornerShape(16.dp),
				colors = CardDefaults.cardColors(
					containerColor = MaterialTheme.colorScheme.surfaceContainer
				)
			) {
				Column(
					modifier = Modifier.padding(16.dp)
				) {
					Column(
						modifier = Modifier.padding(horizontal = 6.dp),
					) {
						Text(
							modifier = Modifier.padding(top = 16.dp),
							text = "Notification time",
							style = MaterialTheme.typography.headlineSmall
						)
						Spacer(Modifier.height(24.dp))
						Slider(
							value = sliderPosition,
							onValueChange = { sliderPosition = it },
							steps = 11,
							valueRange = 0f..60f
						)
						Spacer(Modifier.height(18.dp))
						Text(
							text = "${mapSliderPositionToMinutes(sliderPosition)} minutes before due time"
						)
					}
					Spacer(Modifier.height(24.dp))
					Row(
						modifier = Modifier.fillMaxWidth(),
						horizontalArrangement = Arrangement.End
					) {
						TextButton(onClick = onClose) {
							Text("Dismiss")
						}
						TextButton(
							onClick = {
								setNotificationMinutes(mapSliderPositionToMinutes(sliderPosition))
								onClose()
							}
						) {
							Text("Confirm")
						}
					}
				}
			}
		}
	)
}

private val minutePresets = listOf(1, 2, 5, 10, 15, 20, 30, 40, 50, 60, 120, 180, 300)

private fun mapSliderPositionToMinutes(position: Float): Int {
	// Adding 1 to deal with floating point inaccuracies
	return minutePresets.getOrElse(((position + 1) / 5).toInt(), { 1 })
}

private fun mapMinutesToSliderPosition(minutes: Int): Float {
	val index =  minutePresets.indexOf(minutes)
	return if (index == -1) 0f else index * 5f
}
