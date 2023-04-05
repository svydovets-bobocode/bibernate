package com.bobocode.svydovets.bibernate.testdata.factory;

import com.bobocode.svydovets.bibernate.testdata.entity.User;

public class TestUserFactory {
    public static final String DEFAULT_NAME = "John";
    public static final String DEFAULT_PHONE = "937992";

    public static User newDefaultValidUser() {
        var user = new User();
        user.setName(DEFAULT_NAME);
        user.setPhone(DEFAULT_PHONE);
        return user;
    }
}
