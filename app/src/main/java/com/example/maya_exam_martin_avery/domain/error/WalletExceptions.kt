package com.example.maya_exam_martin_avery.domain.error

/**
 * Expected wallet failure: no wallet row exists for the provided userId.
 */
class WalletNotFoundException(
    val userId: Long,
) : Exception("Wallet not found for userId=$userId")

/**
 * Unexpected wallet failure: wraps data-layer/runtime issues (e.g., DB crashes).
 */
class WalletAppException(cause: Throwable) : Exception("Wallet fetch failed", cause)

