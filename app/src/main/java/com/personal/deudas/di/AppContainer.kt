package com.personal.deudas.di

import android.content.Context
import java.util.concurrent.TimeUnit
import kotlinx.serialization.json.Json
import com.personal.deudas.BuildConfig
import com.personal.deudas.data.local.SessionStorage
import com.personal.deudas.data.local.SharedPreferencesSessionStorage
import com.personal.deudas.data.remote.AuthInterceptor
import com.personal.deudas.data.remote.TokenAuthenticator
import com.personal.deudas.data.remote.api.AuthApi
import com.personal.deudas.data.remote.api.DeudasApi
import com.personal.deudas.data.repository.AuthRepository
import com.personal.deudas.data.repository.AuthRepositoryImpl
import com.personal.deudas.data.repository.DeudaRepository
import com.personal.deudas.data.repository.DeudaRepositoryImpl
import com.personal.deudas.data.repository.PersonaRepository
import com.personal.deudas.data.repository.PersonaRepositoryImpl
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

/** Contenedor de dependencias manual: lo único que los ViewModels (vía la fábrica) pueden pedir. */
interface AppContainer {
    val authRepository: AuthRepository
    val deudaRepository: DeudaRepository
    val personaRepository: PersonaRepository
}

class DefaultAppContainer(context: Context) : AppContainer {

    private val json = Json {
        ignoreUnknownKeys = true
        explicitNulls = false
        encodeDefaults = true
    }

    private val session: SessionStorage = SharedPreferencesSessionStorage(context)

    private fun baseClient(): OkHttpClient.Builder = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .writeTimeout(20, TimeUnit.SECONDS)

    private fun retrofit(client: OkHttpClient): Retrofit = Retrofit.Builder()
        .baseUrl(BuildConfig.BASE_URL)
        .client(client)
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .build()

    // Cliente "limpio" (sin token ni authenticator): login, registro, refresco y logout.
    private val authApi: AuthApi by lazy {
        retrofit(baseClient().build()).create(AuthApi::class.java)
    }

    private val authedClient: OkHttpClient by lazy {
        val builder = baseClient()
            .addInterceptor(AuthInterceptor(session))
            .authenticator(TokenAuthenticator(session, authApi, json))
        if (BuildConfig.DEBUG) {
            builder.addInterceptor(
                HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BASIC
                    redactHeader("Authorization")
                },
            )
        }
        builder.build()
    }

    private val deudasApi: DeudasApi by lazy {
        retrofit(authedClient).create(DeudasApi::class.java)
    }

    override val authRepository: AuthRepository by lazy { AuthRepositoryImpl(authApi, session, json) }
    override val deudaRepository: DeudaRepository by lazy { DeudaRepositoryImpl(deudasApi, json) }
    override val personaRepository: PersonaRepository by lazy { PersonaRepositoryImpl(deudasApi, json) }
}
