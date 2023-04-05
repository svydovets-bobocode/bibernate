package com.bobocode.svydovets.bibernate.testdata.factory;

import com.bobocode.svydovets.bibernate.testdata.entity.Customer;

public class TestCustomerFactory {
    public static final String NAME = "John";
    public static final String EMAIL = "john@gmail.com";

    public static Customer newDefaultValidCustomer() {
        Customer customer = new Customer();
        customer.setName(NAME);
        customer.setEmail(EMAIL);
        return customer;
    }

    public static Customer newDefaultInvalidCustomer() {
        Customer customer = new Customer();
        customer.setId(1L);
        customer.setName(NAME);
        customer.setEmail(EMAIL);
        return customer;
    }
}
