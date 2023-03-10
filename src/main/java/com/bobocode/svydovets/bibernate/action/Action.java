package com.bobocode.svydovets.bibernate.action;

import com.bobocode.svydovets.bibernate.action.key.EntityKey;

public interface Action {

    <T> T execute(EntityKey<T> key);
}
