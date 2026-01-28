package org.atmatto.tasks.ui.common

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.delete
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import org.atmatto.tasks.R

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ScaffoldWrapper(
	title: @Composable () -> Unit,
	scrollable: Boolean,
	fab: @Composable () -> Unit = {},
	bottomBar: @Composable () -> Unit = {},
	navigationIcon: @Composable () -> Unit = {},
	searchable: Boolean = false,
	searchTextFieldState: TextFieldState = rememberTextFieldState(),
	content: @Composable (PaddingValues) -> Unit,
) {
	var searching by remember { mutableStateOf(searchTextFieldState.text.isNotEmpty()) }
	if (searching) {
		val focusRequester = FocusRequester()
		LaunchedEffect(Unit) {
			focusRequester.requestFocus()
		}

		Scaffold(
			modifier = Modifier.fillMaxSize(),
			containerColor = MaterialTheme.colorScheme.surfaceContainer,
			topBar = {
				TopAppBar(
					title = {
						TextField(
							modifier = Modifier.fillMaxWidth()
								.padding(end = 8.dp)
								.focusRequester(focusRequester),
							state = searchTextFieldState,
							lineLimits = TextFieldLineLimits.SingleLine,
							trailingIcon = {
								IconButton( // TODO: Back navigation should also cancel search
									onClick = {
										searchTextFieldState.edit {
											delete(0, length)
										}
										searching = false
									}
								) {
									Icon(painterResource(R.drawable.close_24px), "Cancel")
								}
							},
						)
					},
					colors = TopAppBarDefaults.topAppBarColors(
						containerColor = MaterialTheme.colorScheme.surfaceContainer,
						titleContentColor = MaterialTheme.colorScheme.onSurface,
						scrolledContainerColor = MaterialTheme.colorScheme.surfaceContainer,
					),
				)
			},
			floatingActionButton = fab,
			bottomBar = bottomBar,
			contentWindowInsets = WindowInsets.ime,
			content = { paddingValues ->
				val layoutDirection = LocalLayoutDirection.current
				val imeBottom = WindowInsets.ime.asPaddingValues().calculateBottomPadding()
				val scaffoldBottom = paddingValues.calculateBottomPadding()

				content(PaddingValues(
					top = paddingValues.calculateTopPadding(),
					start = paddingValues.calculateStartPadding(layoutDirection),
					end = paddingValues.calculateEndPadding(layoutDirection),
					bottom = maxOf(imeBottom, scaffoldBottom)
				))
			}
		)
	} else if (scrollable) {
		val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

		Scaffold(
			modifier = Modifier.fillMaxSize()
				.nestedScroll(scrollBehavior.nestedScrollConnection),
			containerColor = MaterialTheme.colorScheme.surfaceContainer,
			topBar = {
				MaterialTheme(
					typography = MaterialTheme.typography.copy(
						headlineMedium =  MaterialTheme.typography.displaySmallEmphasized,
						titleLarge =  MaterialTheme.typography.titleLargeEmphasized
					)
				) {
					LargeTopAppBar(
						title = title,
						colors = TopAppBarDefaults.topAppBarColors(
							containerColor = MaterialTheme.colorScheme.surfaceContainer,
							titleContentColor = MaterialTheme.colorScheme.onSurface,
							scrolledContainerColor = MaterialTheme.colorScheme.surfaceContainer,
						),
						scrollBehavior = scrollBehavior,
						navigationIcon = navigationIcon,
						expandedHeight = 176.dp,
						actions = {
							if (searchable) {
								IconButton(
									modifier = Modifier.padding(end = 8.dp),
									onClick = { searching = true }
								) {
									Icon(painterResource(R.drawable.search_24px), "Search")
								}
							}
						}
					)
				}
			},
			floatingActionButton = fab,
			bottomBar = bottomBar,
			content = { paddingValues ->
				val layoutDirection = LocalLayoutDirection.current
				val imeBottom = WindowInsets.ime.asPaddingValues().calculateBottomPadding()
				val scaffoldBottom = paddingValues.calculateBottomPadding()

				content(PaddingValues(
					top = paddingValues.calculateTopPadding(),
					start = paddingValues.calculateStartPadding(layoutDirection),
					end = paddingValues.calculateEndPadding(layoutDirection),
					bottom = maxOf(imeBottom, scaffoldBottom)
				))
			}
		)
	} else {
		Scaffold(
			modifier = Modifier.fillMaxSize(),
			topBar = {
				MediumTopAppBar(
					title = title,
					colors = TopAppBarDefaults.topAppBarColors(
						containerColor = MaterialTheme.colorScheme.surfaceContainer,
						titleContentColor = MaterialTheme.colorScheme.onSurface
					)
				)
			},
			floatingActionButton = fab,
			bottomBar = bottomBar,
			content = { paddingValues ->
				val layoutDirection = LocalLayoutDirection.current
				val imeBottom = WindowInsets.ime.asPaddingValues().calculateBottomPadding()
				val scaffoldBottom = paddingValues.calculateBottomPadding()

				content(PaddingValues(
					top = paddingValues.calculateTopPadding(),
					start = paddingValues.calculateStartPadding(layoutDirection),
					end = paddingValues.calculateEndPadding(layoutDirection),
					bottom = maxOf(imeBottom, scaffoldBottom)
				))
			}
		)
	}
}
