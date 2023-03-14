package com.bobocode.svydovets.bibernate.action.key;

public record EntityKey<T>(Class<T> type, Object id) {}
