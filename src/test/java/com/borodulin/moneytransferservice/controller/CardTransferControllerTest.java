package com.borodulin.moneytransferservice.controller;

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
    private final static BigInteger CARD_FROM_NUMBER = BigInteger.TEN;
    private final static Date CARD_FROM_VALID_TILL = Date.from(Instant.EPOCH);
    private final static Integer CARD_FROM_CVV = 123;
    private final static BigInteger CARD_TO_NUMBER = BigInteger.TWO;
    private final static String CURRENCY = "RUR";

    @MockBean
    CardTransferService cardTransferService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void transfer() throws Exception {
        PaymentAmount paymentAmount = new PaymentAmount();
        paymentAmount.setValue(100);
        paymentAmount.setCurrency(CURRENCY);
        Transfer transfer = new Transfer();
        transfer.setCardFromNumber(CARD_FROM_NUMBER);
        transfer.setCardFromValidTill(CARD_FROM_VALID_TILL);
        transfer.setCardFromCVV(CARD_FROM_CVV);
        transfer.setCardToNumber(CARD_TO_NUMBER);
        transfer.setAmount(paymentAmount);
        String excepted = "{" +
                "\"operationId\":\"" + ID + "\"" +
                "}";
        when(cardTransferService.transfer(any())).thenReturn(ID);

        MvcResult result = mockMvc.perform(post("/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{" +
                                "\"cardFromNumber\":\"" + CARD_FROM_NUMBER + "\"," +
                                "\"cardFromValidTill\":\"" + FORMATTER.format(CARD_FROM_VALID_TILL) + "\"," +
                                "\"cardFromCVV\":\"" + CARD_FROM_CVV + "\"," +
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
        Confirm confirm = new Confirm(1L, "успех");
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
                                "\"cardFromValidTill\":\"" + FORMATTER.format(CARD_FROM_VALID_TILL) + "\"," +
                                "\"cardFromCVV\":\"" + CARD_FROM_CVV + "\"," +
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
        Confirm confirm = new Confirm(Long.parseLong(ID), "123");
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