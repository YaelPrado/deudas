package com.personal.deudas.data.remote

import com.personal.deudas.data.local.SessionStorage
import okhttp3.Interceptor
import okhttp3.Response

/** Agrega "Authorization: Bearer <token>" a cada petición cuando hay una sesión guardada. */
class AuthInterceptor(private val session: SessionStorage) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val token = session.getAccessToken()
        if (token == null || original.header("Authorization") != null) {
            return chain.proceed(original)
        }
        val conToken = original.newBuilder()
            .header("Authorization", "Bearer $token")
            .build()
        return chain.proceed(conToken)
    }
}
