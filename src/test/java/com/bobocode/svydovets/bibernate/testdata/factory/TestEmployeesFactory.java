package com.bobocode.svydovets.bibernate.testdata.factory;

import com.bobocode.svydovets.bibernate.testdata.entity.Employee;

public class TestEmployeesFactory {

    public static Employee newDefaultValidEmployee() {
        Employee employee = new Employee();
        employee.setName("John");
        employee.setEmail("john@gmail.com");
        return employee;
    }
}
