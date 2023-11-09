package com.example.retrofitkotlin.api

import com.example.retrofitkotlin.model.CryptoModel

data class CryptoResponse(
    val result: List<CryptoModel>
)

