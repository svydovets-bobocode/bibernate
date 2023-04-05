package com.bobocode.svydovets.bibernate.integration.id.resolver;

import static com.bobocode.svydovets.bibernate.testdata.factory.TestCustomerFactory.newDefaultInvalidCustomer;
import static com.bobocode.svydovets.bibernate.testdata.factory.TestCustomerFactory.newDefaultValidCustomer;
import static com.bobocode.svydovets.bibernate.testdata.factory.TestEmployeesFactory.newDefaultValidEmployee;
import static com.bobocode.svydovets.bibernate.testdata.factory.TestPersonFactory.newDefaultInvalidPerson;
import static com.bobocode.svydovets.bibernate.testdata.factory.TestPersonFactory.newDefaultPerson;
import static com.bobocode.svydovets.bibernate.testdata.factory.TestUserFactory.newDefaultValidUser;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.bobocode.svydovets.bibernate.AbstractIntegrationTest;
import com.bobocode.svydovets.bibernate.exception.EntityValidationException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class IdResolverServiceIntegrationTest extends AbstractIntegrationTest {

    @Nested
    class SequenceGenerationType {
        @Test
        void retrieveIdForCustomerFromDefaultSequence() {
            var customer = newDefaultValidCustomer();
            idResolverService.resolveIdValue(connection, customer);

            assertNotNull(customer.getId());
            assertEquals(1, customer.getId());
        }

        @Test
        void retrieveIdForCustomerFailsIfAlreadyProvided() {
            var customer = newDefaultInvalidCustomer();
            assertThrows(
                    EntityValidationException.class,
                    () -> idResolverService.resolveIdValue(connection, customer));
        }

        @Test
        void retrieveIdForEmployeeFromCustomSequence() {
            var employee = newDefaultValidEmployee();
            idResolverService.resolveIdValue(connection, employee);
            assertNotNull(employee.getId());
            assertEquals(1, employee.getId());
        }

        @Test
        void retrieveIdFromCashForAllocationSizeRange() {
            var firstEmployee = newDefaultValidEmployee();
            var secondEmployee = newDefaultValidEmployee();
            var thirdEmployee = newDefaultValidEmployee();

            idResolverService.resolveIdValue(connection, firstEmployee);
            idResolverService.resolveIdValue(connection, secondEmployee);
            idResolverService.resolveIdValue(connection, thirdEmployee);

            assertEquals(1, firstEmployee.getId());
            assertEquals(2, secondEmployee.getId());
            assertEquals(51, thirdEmployee.getId());
        }
    }

    @Nested
    class IdentityGenerationType {
        @Test
        void retrieveIdFromDbForUser() {
            var user = newDefaultValidUser();
            idResolverService.resolveIdValue(connection, user);

            assertNotNull(user.getId());
            assertEquals(1, user.getId());
        }

        @Test
        void retrieveIdForUserFailsIfAlreadyProvided() {
            var customer = newDefaultInvalidCustomer();
            assertThrows(
                    EntityValidationException.class,
                    () -> idResolverService.resolveIdValue(connection, customer));
        }
    }

    @Nested
    class ManualGenerationType {
        @Test
        void checkIfIdIsAlreadyProvided() {
            var person = newDefaultPerson();
            person.setId(100L);
            assertDoesNotThrow(() -> idResolverService.resolveIdValue(connection, person));

            assertNotNull(person.getId());
            assertEquals(100L, person.getId());
        }

        @Test
        void failsIdCheckIfIdIsNull() {
            var person = newDefaultInvalidPerson();
            assertThrows(
                    EntityValidationException.class,
                    () -> idResolverService.resolveIdValue(connection, person));
        }
    }
}
