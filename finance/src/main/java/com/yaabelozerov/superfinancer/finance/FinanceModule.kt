package com.yaabelozerov.superfinancer.finance

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.yaabelozerov.superfinancer.finance.data.FinanceDb

open class FinanceModule {
    fun onCreate(application: Application) {
        app = application
    }

    companion object {
        private lateinit var app: Application

        val financeDb by lazy {
            Room.databaseBuilder(app.applicationContext, FinanceDb::class.java, "finance.db").build()
        }
        val financeDao by lazy { financeDb.dao() }
    }
}