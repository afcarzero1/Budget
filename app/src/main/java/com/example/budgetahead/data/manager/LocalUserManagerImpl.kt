package com.example.budgetahead.data.manager

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import com.example.budgetahead.data.currencies.OnlineCurrenciesRepository
import java.io.IOException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

class LocalUserManagerImpl(private val dataStore: DataStore<Preferences>) : LocalUserManager {
    override suspend fun saveAppEntry() {
        dataStore.edit { settings ->
            settings[PreferencesKeys.APP_ENTRY] = true
        }
    }

    override fun readAppEntry(): Flow<Boolean> = dataStore.data
        .catch {
            if (it is IOException) {
                Log.e(OnlineCurrenciesRepository.TAG, "Error reading preferences.", it)
                emit(emptyPreferences())
            } else {
                throw it
            }
        }.map { preferences ->
            preferences[PreferencesKeys.APP_ENTRY] ?: false
        }
}

private object PreferencesKeys {
    val APP_ENTRY = booleanPreferencesKey("APP_ENTRY")
}
