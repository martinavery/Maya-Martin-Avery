package com.example.maya_exam_martin_avery.domain.model

/**
 * Transaction data exposed to the app UI.
 *
 * We model local (user-created) and remote (sample feed) items explicitly so the UI can merge them.
 */
sealed interface TransactionDomain {
    val id: String

    data class LocalSent(
        override val id: String,
        val userId: Long,
        val amount: Double,
        val description: String,
        val createdAtEpochMs: Long,
    ) : TransactionDomain

    data class RemoteSample(
        override val id: String,
        val userId: Long,
        val title: String,
        val body: String,
    ) : TransactionDomain
}

