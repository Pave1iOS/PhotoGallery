package com.example.photogallery.data

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

object FlickrDataStore {
    private const val TAG = "FlickrDataStore"
    private const val MODULE_NAME = "DATA STORE ->"

    private val Context.dataStore: androidx.datastore.core.DataStore<Preferences> by preferencesDataStore("settings")
    private val QUERY_KEY = stringPreferencesKey("stored_query")

    fun getStoredQuery(context: Context): Flow<String?> {
        return context.dataStore.data.map { preferences ->
            Log.i(TAG, "$MODULE_NAME getStoredQuery = ${preferences[QUERY_KEY]}")

            preferences[QUERY_KEY]
        }
    }

    suspend fun setStoredQuery(context: Context, query: String) {
        context.dataStore.edit { preferences ->
            Log.i(TAG, "$MODULE_NAME setStoredQuery = ${preferences[QUERY_KEY]}")

            preferences[QUERY_KEY] = query
        }
    }
}