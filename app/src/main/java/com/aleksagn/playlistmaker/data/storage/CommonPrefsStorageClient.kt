package com.aleksagn.playlistmaker.data.storage

import android.content.Context
import android.content.SharedPreferences
import com.aleksagn.playlistmaker.data.StorageClient
import com.google.gson.Gson
import java.lang.reflect.Type

class CommonPrefsStorageClient<T>(
    private val prefs: SharedPreferences,
    private val gson: Gson,
    private val type: Type
) : StorageClient<T> {

    private val dataKey = "SEARCH_HISTORY_LIST_KEY"

    override fun storeData(data: T) {
        prefs.edit().putString(dataKey, gson.toJson(data, type)).apply()
    }

    override fun getData(): T? {
        val dataJson = prefs.getString(dataKey, null)
        if (dataJson == null) {
            return null
        } else {
            return gson.fromJson(dataJson, type)
        }
    }

    override fun clearData() {
        prefs.edit().remove(dataKey).apply()
    }
}
