package com.borodulin.moneytransferservice.service;

import com.borodulin.moneytransferservice.dao.CardTransferDao;
import com.borodulin.moneytransferservice.model.Confirm;
import com.borodulin.moneytransferservice.model.Transfer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
class CardTransferServiceTest {
    private final static Long ID = 100L;
    private final static Transfer TRANSFER = new Transfer();
    private final static Confirm CONFIRM = new Confirm(ID, "Успех");

    @Mock
    CardTransferDao cardTransferDao;

    @InjectMocks
    CardTransferService sut;

    @BeforeEach
    void init() {
        TRANSFER.setId(ID);
    }

    @Test
    void transfer_ok() {
        when(cardTransferDao.save(any()))
                .thenReturn(TRANSFER);

        String actual = sut.transfer(TRANSFER);

        assertEquals(ID.toString(), actual);
        verify(cardTransferDao).save(TRANSFER);
        verifyNoMoreInteractions(cardTransferDao);
    }

    @Test
    void transfer_IllegalArgumentException() {
        when(cardTransferDao.save(any()))
                .thenThrow(new IllegalArgumentException());

        assertThrows(IllegalArgumentException.class, () -> sut.transfer(TRANSFER));
    }

    @Test
    void confirm_ok() {
        when(cardTransferDao.findById(any()))
                .thenReturn(Optional.of(TRANSFER));

        String actual = sut.confirm(CONFIRM);

        assertEquals(ID.toString(), actual);
        verify(cardTransferDao).findById(CONFIRM.getOperationId());
        verifyNoMoreInteractions(cardTransferDao);
    }

    @Test
    void confirm_IllegalArgumentException() {
        when(cardTransferDao.findById(any()))
                .thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class, () -> sut.confirm(CONFIRM));

        assertEquals("Перевод не найден", exception.getLocalizedMessage());
    }
}