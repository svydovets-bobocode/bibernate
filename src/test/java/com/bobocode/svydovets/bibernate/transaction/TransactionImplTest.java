package com.bobocode.svydovets.bibernate.transaction;

import static com.bobocode.svydovets.bibernate.constant.ErrorMessage.TRANSACTION_IS_ALREADY_STARTED;
import static com.bobocode.svydovets.bibernate.constant.ErrorMessage.TRANSACTION_NOT_STARTED_OR_ALREADY_COMMITTED;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.mock;

import com.bobocode.svydovets.bibernate.exception.BibernateException;
import java.sql.Connection;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class TransactionImplTest {
    private final Connection connection = mock(Connection.class);
    private Transaction transaction;

    @BeforeEach
    void beforeEach() {
        transaction = new TransactionImpl(connection);
    }

    @Test
    @DisplayName("Begin and begin")
    void beginAndBegin() {
        transaction.begin();

        assertThatExceptionOfType(BibernateException.class)
                .isThrownBy(transaction::begin)
                .withMessage(TRANSACTION_IS_ALREADY_STARTED);
    }

    @Test
    @DisplayName("Commit without begin")
    @SneakyThrows
    void commitWithoutBegin() {
        assertThatExceptionOfType(BibernateException.class)
                .isThrownBy(transaction::commit)
                .withMessage(TRANSACTION_NOT_STARTED_OR_ALREADY_COMMITTED);
    }
}
