package com.example.androidsofit.model

data class Btc (
    val data: Data,
    val channel: String,
    val event: String
)

data class Data (
    val id: Long,
    val timestamp: String,
    val amount: Double,
    val amountStr: String,
    val price: Double,
    val priceStr: String,
    val type: Long,
    val microtimestamp: String,
    val buyOrderID: Long,
    val sellOrderID: Long
)