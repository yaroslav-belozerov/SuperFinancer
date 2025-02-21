package com.yaabelozerov.superfinancer.finance

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.yaabelozerov.superfinancer.common.Module
import com.yaabelozerov.superfinancer.finance.data.FinanceDb

class FinanceModule: Module() {
    override fun onCreate(application: Application) {
        app = application
    }

    companion object {
        private lateinit var app: Application

        internal val financeDb by lazy {
            Room.databaseBuilder(app.applicationContext, FinanceDb::class.java, "finance.db").build()
        }
        internal val financeDao by lazy { financeDb.dao() }
        internal val statsDao by lazy { financeDb.statsDao() }
    }
}