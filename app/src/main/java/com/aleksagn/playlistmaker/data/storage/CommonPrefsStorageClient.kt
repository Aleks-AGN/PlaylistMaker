package com.aleksagn.playlistmaker.data.storage

import android.content.Context
import android.content.SharedPreferences
import com.aleksagn.playlistmaker.data.StorageClient
import com.google.gson.Gson
import java.lang.reflect.Type

class CommonPrefsStorageClient<T>(
    private val context: Context,
    private val dataKey: String,
    private val type: Type
) : StorageClient<T> {

    private val prefs: SharedPreferences = context.getSharedPreferences("PLAYLIST_MAKER", Context.MODE_PRIVATE)
    private val gson = Gson()

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
