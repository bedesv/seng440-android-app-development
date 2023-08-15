package com.bedesv.frugaltracker

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserPreferencesStore(private val context: Context) {
    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("app_preferences")
        private val USER_BUDGET_KEY = stringPreferencesKey("user_budget")
    }

    val getUserBudget: Flow<String> = context.dataStore.data.map { preferences ->
        Log.d("UserPreferencesStore", "fetching budget: ${preferences[USER_BUDGET_KEY]}")
        preferences[USER_BUDGET_KEY] ?: "500" // Give data
    }

    suspend fun saveBudget(budget: String) {
        Log.d("UserPreferencesStore", "Saving budget: $budget")
        context.dataStore.edit { preferences ->
            preferences[USER_BUDGET_KEY] = budget
        }
    }
}