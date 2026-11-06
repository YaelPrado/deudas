package mx.edu.upchiapas.deudas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import mx.edu.upchiapas.deudas.factory.ViewModelFactory
import mx.edu.upchiapas.deudas.ui.navigation.AppNavHost
import mx.edu.upchiapas.deudas.ui.theme.DeudasTheme
import mx.edu.upchiapas.deudas.ui.viewmodels.MainViewModel

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
