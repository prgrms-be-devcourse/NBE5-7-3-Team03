package com.team573.gongguri.domain.grouppurchase.entity;

import java.util.List;

public enum PurchaseFilter {
    ALL,
    ONGOING,
    COMPLETED;

    // 상태 조건 분리
    public List<ProgressStatus> toStatuses() {
        return switch (this) {
            case ONGOING -> List.of(ProgressStatus.RECRUITING, ProgressStatus.CLOSED);
            case COMPLETED -> List.of(ProgressStatus.COMPLETED);
            default -> List.of(ProgressStatus.RECRUITING, ProgressStatus.CLOSED, ProgressStatus.COMPLETED);
        };
    }
}
