package com.personal.deudas

import android.app.Application
import com.personal.deudas.di.AppContainer
import com.personal.deudas.di.DefaultAppContainer

class DeudasApp : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(this)
    }
}
