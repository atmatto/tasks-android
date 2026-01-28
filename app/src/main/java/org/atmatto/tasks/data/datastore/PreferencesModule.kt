package org.atmatto.tasks.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PreferencesModule {
	@Singleton
	@Provides
	fun preferences(@ApplicationContext app: Context): DataStore<Preferences> =
		PreferenceDataStoreFactory.create(
			produceFile = { app.preferencesDataStoreFile("app_preferences") }
		)
}
