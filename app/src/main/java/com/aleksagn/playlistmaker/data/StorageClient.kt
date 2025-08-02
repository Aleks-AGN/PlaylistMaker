package com.aleksagn.playlistmaker.data

interface StorageClient<T> {
    fun storeData(data: T)
    fun getData(): T?
    fun clearData()
}
