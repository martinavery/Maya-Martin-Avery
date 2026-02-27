package com.example.maya_exam_martin_avery.domain.model

/**
 * Wallet data exposed to the app.
 *
 * Modeled as 1:1 with a user (keyed by userId).
 */
data class WalletDomain(
    val userId: Long,
    val balance: Double,
)

