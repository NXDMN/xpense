package com.nxdmn.xpense

import android.app.Application
import com.nxdmn.xpense.data.dataSources.room.AppDatabase

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        AppDatabase.getDatabase(this)
    }
}