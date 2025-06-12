package com.team573.gongguri.domain.grouppurchase.entity

enum class PurchaseFilter {
    ALL,
    ONGOING,
    COMPLETED;

    // 상태 조건 분리
    fun toStatuses(): List<ProgressStatus> {
        return when (this) {
            ONGOING -> listOf(ProgressStatus.RECRUITING, ProgressStatus.CLOSED)
            COMPLETED -> listOf(ProgressStatus.COMPLETED)
            else -> listOf(ProgressStatus.RECRUITING, ProgressStatus.CLOSED, ProgressStatus.COMPLETED)
        }
    }
}
