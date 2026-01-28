package org.atmatto.tasks

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import dagger.hilt.android.AndroidEntryPoint
import org.atmatto.tasks.ui.DeepLinkViewModel
import org.atmatto.tasks.ui.TasksNavigation
import org.atmatto.tasks.ui.theme.TasksTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
	private val deepLinkViewModel: DeepLinkViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

		intent?.let { deepLinkViewModel.handleIntent(intent) }

        setContent {
            TasksTheme {
				TasksNavigation(deepLinkViewModel)
            }
        }
    }

	override fun onNewIntent(intent: Intent) {
		super.onNewIntent(intent)
		setIntent(intent)

		deepLinkViewModel.handleIntent(intent)
	}
}
