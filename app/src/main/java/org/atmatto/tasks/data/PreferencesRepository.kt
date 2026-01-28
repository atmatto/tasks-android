package org.atmatto.tasks.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PreferencesRepository @Inject constructor(
	private val dataStore: DataStore<Preferences>
) {
	private object Keys {
		val NOTIFICATION_MINUTES = intPreferencesKey("notification_minutes")
		val SHOW_COMPLETED = booleanPreferencesKey("show_completed")
	}

	fun getNotificationMinutes(): Flow<Int> =
		dataStore.data.map { it[Keys.NOTIFICATION_MINUTES] ?: 5 }

	suspend fun setNotificationMinutes(value: Int) =
		dataStore.edit { it[Keys.NOTIFICATION_MINUTES] = value }

	fun getShowCompleted(): Flow<Boolean> =
		dataStore.data.map { it[Keys.SHOW_COMPLETED] ?: true }

	suspend fun setShowCompleted(value: Boolean) =
		dataStore.edit { it[Keys.SHOW_COMPLETED] = value }
}
