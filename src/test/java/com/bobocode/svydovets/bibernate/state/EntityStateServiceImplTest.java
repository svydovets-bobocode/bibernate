package com.bobocode.svydovets.bibernate.state;

import static com.bobocode.svydovets.bibernate.state.EntityState.DETACHED;
import static com.bobocode.svydovets.bibernate.state.EntityState.MANAGED;
import static com.bobocode.svydovets.bibernate.state.EntityState.REMOVED;
import static com.bobocode.svydovets.bibernate.state.EntityState.TRANSIENT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.bobocode.svydovets.bibernate.action.key.EntityKey;
import com.bobocode.svydovets.bibernate.exception.EntityStateValidationException;
import com.bobocode.svydovets.bibernate.testdata.entity.Person;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
class EntityStateServiceImplTest {

    EntityStateService entityStateService = EntityStateServiceImpl.getInstance();

    @Test
    @DisplayName("Newly created entity should be in TRANSIENT state after creation.")
    void theNewlyCreatedEntityShouldBeInTransientState() {
        Person person = new Person(2L, "reallyFirstName", "justLastName");
        assertEquals(TRANSIENT, entityStateService.getEntityState(person));
    }

    @Test
    @DisplayName("Newly created entity should pass validation to MANAGED state.")
    void theNewlyCreatedEntityCanBeInManagedState() {
        Person person = new Person(4L, "reallyFirstName", "justLastName");
        entityStateService.validate(person, MANAGED);
        assertEquals(TRANSIENT, entityStateService.getEntityState(person));
    }

    @Test
    @DisplayName("Newly created entity should pass validation to MANAGED state by EntityKey.")
    void theNewlyCreatedEntityCanBeInManagedStateValidateWithEntityKey() {
        Person person = new Person(5L, "reallyFirstName", "justLastName");
        entityStateService.validate(EntityKey.valueOf(person), MANAGED);
        assertEquals(TRANSIENT, entityStateService.getEntityState(person));
    }

    @Test
    @DisplayName("Newly created entity should be in MANAGED state after setting the state.")
    void theNewlyCreatedEntityShouldBeInManagedState() {
        Person person = new Person(6L, "reallyFirstName", "justLastName");
        entityStateService.setEntityState(person, MANAGED);
        assertEquals(MANAGED, entityStateService.getEntityState(person));
    }

    @Test
    @DisplayName(
            "Newly created entity should be in MANAGED state after setting the state with EntityKey.")
    void theNewlyCreatedEntityShouldBeInManagedStateWithEntityKey() {
        Person person = new Person(7L, "reallyFirstName", "justLastName");
        entityStateService.setEntityState(EntityKey.valueOf(person), MANAGED);
        assertEquals(MANAGED, entityStateService.getEntityState(person));
    }

    @Test
    @DisplayName("Newly created entity should NOT be changed to REMOVED state.")
    void theNewlyCreatedEntityShouldNotBeUpdatedToRemovedState() {
        Person person = new Person(8L, "reallyFirstName", "justLastName");
        assertEquals(TRANSIENT, entityStateService.getEntityState(person));
        assertThrows(
                EntityStateValidationException.class,
                () -> entityStateService.setEntityState(person, REMOVED),
                "Can't change entity state from TRANSIENT to REMOVED");
    }

    @Test
    @DisplayName("Newly created entity should NOT be changed to DETACHED state.")
    void theNewlyCreatedEntityShouldNotBeUpdatedToDetachedState() {
        Person person = new Person(9L, "reallyFirstName", "justLastName");
        assertEquals(TRANSIENT, entityStateService.getEntityState(person));
        assertThrows(
                EntityStateValidationException.class,
                () -> entityStateService.setEntityState(person, DETACHED),
                "Can't change entity state from TRANSIENT to DETACHED");
    }

    @Test
    @DisplayName("Entity in MANAGED state should be changed to REMOVED state.")
    void theEntityInManagedStateShouldBeChangedToRemovedState() {
        Person person = new Person(10L, "reallyFirstName", "justLastName");
        entityStateService.setEntityState(person, MANAGED);
        entityStateService.validate(person, REMOVED);
    }

    @Test
    @DisplayName("Entity in MANAGED state should be changed to DETACHED state.")
    void theEntityInManagedStateShouldBeChangedToDetachedState() {
        Person person = new Person(11L, "reallyFirstName", "justLastName");
        entityStateService.setEntityState(person, MANAGED);
        entityStateService.validate(person, DETACHED);
    }

    @Test
    @DisplayName("Entity in MANAGED state should be changed to MANAGED state.")
    void theEntityInManagedStateShouldBeChangedToManagedState() {
        Person person = new Person(12L, "reallyFirstName", "justLastName");
        entityStateService.setEntityState(person, MANAGED);
        entityStateService.validate(person, MANAGED);
    }

    @Test
    @DisplayName("Entity in MANAGED state should NOT be MANUALLY changed to TRANSIENT state.")
    void theEntityInManagedStateShouldNotBeChangedToTransientState() {
        Person person = new Person(12L, "reallyFirstName", "justLastName");
        entityStateService.setEntityState(person, MANAGED);
        assertThrows(
                EntityStateValidationException.class,
                () -> entityStateService.validate(person, TRANSIENT),
                "Can't change entity state from MANAGED to TRANSIENT");
    }

    @Test
    @DisplayName("Entity in REMOVED state should be changed to MANAGED state.")
    void theEntityInRemovedStateShouldBeChangedToManagedState() {
        Person person = new Person(14L, "reallyFirstName", "justLastName");
        entityStateService.setEntityState(person, MANAGED);
        entityStateService.setEntityState(person, REMOVED);
        entityStateService.setEntityState(person, MANAGED);
    }

    @Test
    @DisplayName("Entity in REMOVED state should NOT be changed to DETACHED state.")
    void theEntityInRemovedStateShouldNotBeChangedToDetachedState() {
        Person person = new Person(15L, "reallyFirstName", "justLastName");
        entityStateService.setEntityState(person, MANAGED);
        entityStateService.setEntityState(person, REMOVED);
        assertThrows(
                EntityStateValidationException.class,
                () -> entityStateService.setEntityState(person, DETACHED),
                "Can't change entity state from REMOVED to DETACHED");
    }

    @Test
    @DisplayName("Entity in REMOVED state should NOT be changed to TRANSIENT state.")
    void theEntityInRemovedStateShouldNotBeChangedToTransientState() {
        Person person = new Person(16L, "reallyFirstName", "justLastName");
        entityStateService.setEntityState(person, MANAGED);
        entityStateService.setEntityState(person, REMOVED);
        assertThrows(
                EntityStateValidationException.class,
                () -> entityStateService.setEntityState(person, TRANSIENT),
                "Can't change entity state from REMOVED to TRANSIENT");
    }

    @Test
    @DisplayName("Entity in REMOVED state should NOT be changed to REMOVED state.")
    void theEntityInRemovedStateShouldNotBeChangedToRemovedState() {
        Person person = new Person(17L, "reallyFirstName", "justLastName");
        entityStateService.setEntityState(person, MANAGED);
        entityStateService.setEntityState(person, REMOVED);
        assertThrows(
                EntityStateValidationException.class,
                () -> entityStateService.setEntityState(person, REMOVED),
                "Can't change entity state from REMOVED to TRANSIENT");
    }

    @Test
    @DisplayName("Entity in DETACHED state should be changed to MANAGED state.")
    void theEntityInDetachedStateShouldBeChangedToManagedState() {
        Person person = new Person(18L, "reallyFirstName", "justLastName");
        entityStateService.setEntityState(person, MANAGED);
        entityStateService.setEntityState(person, DETACHED);
        entityStateService.setEntityState(person, MANAGED);
    }

    @Test
    @DisplayName("Entity in DETACHED state should NOT be changed to REMOVED state.")
    void theEntityInDetachedStateShouldNotBeChangedToRemovedState() {
        Person person = new Person(19L, "reallyFirstName", "justLastName");
        entityStateService.setEntityState(person, MANAGED);
        entityStateService.setEntityState(person, DETACHED);
        assertThrows(
                EntityStateValidationException.class,
                () -> entityStateService.setEntityState(person, REMOVED),
                "Can't change entity state from REMOVED to TRANSIENT");
    }

    @Test
    @DisplayName("Entity in DETACHED state should NOT be changed to TRANSIENT state.")
    void theEntityInDetachedStateShouldNotBeChangedToTransientState() {
        Person person = new Person(20L, "reallyFirstName", "justLastName");
        entityStateService.setEntityState(person, MANAGED);
        entityStateService.setEntityState(person, DETACHED);
        assertThrows(
                EntityStateValidationException.class,
                () -> entityStateService.setEntityState(person, TRANSIENT),
                "Can't change entity state from REMOVED to TRANSIENT");
    }
}
