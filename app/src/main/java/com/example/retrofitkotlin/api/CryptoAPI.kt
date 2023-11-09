package com.example.retrofitkotlin.api

import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

interface CryptoAPI {
    @GET("/coins")
    fun getAllData(
        @Query("X-API-KEY") apiKey: String,
        @Query("limit") limit: Int,
        @Query("currency") currency: String
    ): Observable<CryptoResponse>
}