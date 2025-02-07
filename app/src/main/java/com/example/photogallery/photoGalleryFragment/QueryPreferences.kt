package com.example.photogallery.photoGalleryFragment

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

object QueryPreferences {

    private val Context.dataStore: androidx.datastore.core.DataStore<Preferences> by preferencesDataStore("settings")
    private val QUERY_KEY = stringPreferencesKey("stored_query")

    fun getStoredQuery(context: Context): Flow<String?> {
        return context.dataStore.data.map { preferences ->
            preferences[QUERY_KEY]
        }
    }

    suspend fun setStoredQuery(context: Context, query: String) {
        context.dataStore.edit { preferences ->
            preferences[QUERY_KEY] = query
        }
    }
}