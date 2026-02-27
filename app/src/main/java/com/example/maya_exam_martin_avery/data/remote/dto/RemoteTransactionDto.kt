package com.example.maya_exam_martin_avery.data.remote.dto

/**
 * JSONPlaceholder `/posts` payload used as a fake \"transactions\" API.
 *
 * Note: JSONPlaceholder does not persist writes; POST is for demonstration only.
 */
data class RemoteTransactionDto(
    val id: Long,
    val userId: Long,
    val title: String,
    val body: String,
)

