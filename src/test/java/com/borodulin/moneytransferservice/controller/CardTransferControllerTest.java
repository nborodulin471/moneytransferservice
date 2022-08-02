package com.borodulin.moneytransferservice.controller;

import com.borodulin.moneytransferservice.model.Card;
import com.borodulin.moneytransferservice.model.Confirm;
import com.borodulin.moneytransferservice.model.PaymentAmount;
import com.borodulin.moneytransferservice.model.Transfer;
import com.borodulin.moneytransferservice.service.CardTransferService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(CardTransferController.class)
class CardTransferControllerTest {
    public static final SimpleDateFormat FORMATTER = new SimpleDateFormat("yyyy-MM-dd");
    private final static String ID = "123";
    private final static String CARD_FROM_NUMBER = "123412341324";
    private final static Date DATE = Date.from(Instant.now());
    private final static int CARD_CVV = 123;
    private final static String CARD_TO_NUMBER = "8888888888";
    private final static String CURRENCY = "RUR";
    private final static String CODE_OTP = "0000";

    @MockBean
    CardTransferService cardTransferService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void transfer() throws Exception {
        Card cardFrom = new Card(CARD_FROM_NUMBER, DATE, BigInteger.TEN, CARD_CVV);
        Card cardTo = new Card(CARD_TO_NUMBER, DATE, BigInteger.ZERO, CARD_CVV);
        PaymentAmount paymentAmount = new PaymentAmount();
        paymentAmount.setValue(100);
        paymentAmount.setCurrency(CURRENCY);
        Transfer transfer = new Transfer();
        transfer.setCardFrom(cardFrom);
        transfer.setCardTo(cardTo);
        transfer.setAmount(paymentAmount);
        String excepted = "{" +
                "\"operationId\":\"" + ID + "\"" +
                "}";
        when(cardTransferService.transfer(any())).thenReturn(ID);

        MvcResult result = mockMvc.perform(post("/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{" +
                                "\"cardFromNumber\":\"" + CARD_FROM_NUMBER + "\"," +
                                "\"cardFromValidTill\":\"" + FORMATTER.format(DATE) + "\"," +
                                "\"cardFromCVV\":\"" + CARD_CVV + "\"," +
                                "\"cardToNumber\":\"" + CARD_TO_NUMBER + "\"," +
                                "\"amount\": {" +
                                "\"value\":" + paymentAmount.getValue() + "," +
                                "\"currency\":\"" + paymentAmount.getCurrency() + "\"" +
                                "}" +
                                "}"))
                .andExpect(status().isOk())
                .andReturn();

        assertEquals(excepted, result.getResponse().getContentAsString());
        verify(cardTransferService).transfer(transfer);
        verifyNoMoreInteractions(cardTransferService);
    }

    @Test
    void confirmOperation() throws Exception {
        Confirm confirm = new Confirm(1L, CODE_OTP);
        String excepted = "{" +
                "\"operationId\":\"" + ID + "\"" +
                "}";
        when(cardTransferService.confirm(any())).thenReturn(ID);

        MvcResult result = mockMvc.perform(post("/confirmOperation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{" +
                                "\"operationId\":\"" + confirm.getOperationId().toString() + "\"," +
                                "\"code\":\"" + confirm.getCode() + "\"" +
                                "}"))

                .andExpect(status().isOk())
                .andReturn();
        assertEquals(excepted, result.getResponse().getContentAsString());
        verify(cardTransferService).confirm(confirm);
        verifyNoMoreInteractions(cardTransferService);
    }

    @Test
    void transfer_handleIllegalArgumentException() throws Exception {
        PaymentAmount paymentAmount = new PaymentAmount();
        Transfer transfer = new Transfer();
        transfer.setAmount(paymentAmount);
        when(cardTransferService.transfer(any())).thenThrow(new IllegalArgumentException("test"));

        MvcResult result = mockMvc.perform(post("/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{" +
                                "\"cardFromNumber\":\"" + CARD_FROM_NUMBER + "\"," +
                                "\"cardFromValidTill\":\"" + FORMATTER.format(DATE) + "\"," +
                                "\"cardFromCVV\":\"" + CARD_CVV + "\"," +
                                "\"cardToNumber\":\"" + CARD_TO_NUMBER + "\"," +
                                "\"amount\": {" +
                                "\"value\":" + paymentAmount.getValue() + "," +
                                "\"currency\":\"" + paymentAmount.getCurrency() + "\"" +
                                "}" +
                                "}"))
                .andExpect(status().is4xxClientError())
                .andReturn();

        assertFalse(result.getResponse().getContentAsString().isEmpty());
    }

    @Test
    void confirm_handleIllegalArgumentException() throws Exception {
        Confirm confirm = new Confirm(Long.parseLong(ID), CODE_OTP);
        when(cardTransferService.confirm(any())).thenThrow(new IllegalArgumentException("test"));

        MvcResult result = mockMvc.perform(post("/confirmOperation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{" +
                                "\"operationId\":\"" + confirm.getOperationId() + "\"," +
                                "\"code\":\"" + confirm.getCode() + "\"" +
                                "}"))
                .andExpect(status().is4xxClientError())
                .andReturn();

        assertFalse(result.getResponse().getContentAsString().isEmpty());
    }

    @Test
    void handleValidationExceptions() throws Exception {
        mockMvc.perform(post("/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{" +
                                "\"cardFromNumber\":\"" + null + "\"," +
                                "\"cardFromCVV\":\"" + null + "\"," +
                                "\"cardToNumber\":\"" + null + "\"," +
                                "}"))
                .andExpect(status().is4xxClientError());
    }
}