package com.cornellappdev.coffee_chats_android.models

import android.content.Context
import java.io.*

/*
    Utility class that provides 2 methods,
    one for storing objects to internal storage,
    and another for retrieving objects from internal storage
    https://androidresearch.wordpress.com/2013/04/07/caching-objects-in-android-internal-storage/
 */

object InternalStorage {
    @Throws(IOException::class)
    fun writeObject(context: Context, key: String, `object`: Object) {
        val fos: FileOutputStream = context.openFileOutput(key, Context.MODE_PRIVATE)
        val oos = ObjectOutputStream(fos)
        oos.writeObject(`object`)
        oos.close()
        fos.close()
    }

    @Throws(IOException::class, ClassNotFoundException::class)
    fun readObject(context: Context, key: String): Any {
        val fis: FileInputStream = context.openFileInput(key)
        val ois = ObjectInputStream(fis)
        return ois.readObject()
    }
}