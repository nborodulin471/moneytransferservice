package com.borodulin.moneytransferservice.service;


import com.borodulin.moneytransferservice.dao.TransferDao;
import com.borodulin.moneytransferservice.model.Card;
import com.borodulin.moneytransferservice.model.Confirm;
import com.borodulin.moneytransferservice.model.PaymentAmount;
import com.borodulin.moneytransferservice.model.Transfer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigInteger;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;

import static java.math.BigInteger.*;
import static java.math.BigInteger.TEN;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = {TransferService.class})
class TransferServiceTest {
    private final static Long ID = 100L;
    private final static Transfer TRANSFER = new Transfer();
    private final static Confirm CONFIRM = new Confirm(ID, "Успех");
    private final static String CARD_FROM_NUMBER = "123412341324";
    private final static Date DATE = Date.from(Instant.now());
    private final static String CARD_CVV = "123";
    private final static String CARD_TO_NUMBER = "8888888888";

    @MockBean
    private TransferDao transferDao;

    @Autowired
    private TransferService sut;

    @BeforeEach
    void init() {
        Card cardFrom = new Card(CARD_FROM_NUMBER, DATE, TEN, CARD_CVV);
        Card cardTo = new Card(CARD_TO_NUMBER, DATE, ZERO, CARD_CVV);
        TRANSFER.setId(ID);
        TRANSFER.setCardFrom(cardFrom);
        TRANSFER.setCardTo(cardTo);
        TRANSFER.setAmount(new PaymentAmount(TEN, "RUR"));
    }

    @Test
    void transfer_ok() {
        when(transferDao.save(any())).thenReturn(TRANSFER);

        String actual = sut.transfer(TRANSFER);

        assertEquals(ID.toString(), actual);
        assertEquals(TRANSFER.getCardFrom().getBalance(), ZERO);
        assertEquals(TRANSFER.getCardTo().getBalance(), TEN);
        verify(transferDao).save(TRANSFER);
        verifyNoMoreInteractions(transferDao);
    }

    @Test
    void transfer_IllegalArgumentException() {
        when(transferDao.save(any()))
                .thenThrow(new IllegalArgumentException());

        assertThrows(IllegalArgumentException.class, () -> sut.transfer(TRANSFER));
    }

    @Test
    void confirm_ok() {
        when(transferDao.findById(any()))
                .thenReturn(Optional.of(TRANSFER));

        String actual = sut.confirm(CONFIRM);

        assertEquals(ID.toString(), actual);
        verify(transferDao).findById(CONFIRM.getOperationId());
        verifyNoMoreInteractions(transferDao);
    }

    @Test
    void confirm_IllegalArgumentException() {
        when(transferDao.findById(any()))
                .thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class, () -> sut.confirm(CONFIRM));

        assertEquals("Перевод не найден", exception.getLocalizedMessage());
    }
}