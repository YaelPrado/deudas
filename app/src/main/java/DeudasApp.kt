package mx.edu.upchiapas.deudas

import android.app.Application
import mx.edu.upchiapas.deudas.di.AppContainer
import mx.edu.upchiapas.deudas.di.DefaultAppContainer

class DeudasApp : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(this)
    }
}
