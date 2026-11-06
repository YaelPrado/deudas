package com.personal.deudas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.personal.deudas.factory.ViewModelFactory
import com.personal.deudas.ui.navigation.AppNavHost
import com.personal.deudas.ui.theme.DeudasTheme
import com.personal.deudas.ui.viewmodels.MainViewModel

class MainActivity : ComponentActivity() {

    private val factory by lazy { ViewModelFactory((application as DeudasApp).container) }
    private val mainViewModel: MainViewModel by viewModels { factory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DeudasTheme {
                AppNavHost(factory = factory, mainViewModel = mainViewModel)
            }
        }
    }
}
