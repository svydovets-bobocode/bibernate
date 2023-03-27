package com.bobocode.svydovets.bibernate.testdata.factory;

import com.bobocode.svydovets.bibernate.testdata.entity.User;

public class TestUserFactory {
    public static final Integer DEFAULT_ID = 1;
    public static final String DEFAULT_NAME = "John";
    public static final String DEFAULT_PHONE = "937992";

    public static User newDefaultUser() {
        var user = new User();
        user.setId(DEFAULT_ID);
        user.setName(DEFAULT_NAME);
        user.setPhone(DEFAULT_PHONE);
        return user;
    }
}
