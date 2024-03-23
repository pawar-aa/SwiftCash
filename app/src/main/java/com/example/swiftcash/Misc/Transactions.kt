package com.example.swiftcash.Misc

import java.util.Date

data class Transactions(
    val amount: Long,
    val receiverID: String,
    val senderID: String,
    val time: Date
)