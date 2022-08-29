package com.borodulin.moneytransferservice.controller;

import com.borodulin.moneytransferservice.model.*;
import com.borodulin.moneytransferservice.service.TransferService;
import com.borodulin.moneytransferservice.utils.TransferMapper;
import org.junit.jupiter.api.BeforeEach;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;

import static java.math.BigInteger.TEN;
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
    private final static String CARD_FROM_NUMBER = "20200222222222";
    private final static String CARD_CVV = "123";
    private final static String DATE = "10-10-2022";
    private final static String CARD_TO_NUMBER = "20204444444444";
    private final static String CURRENCY = "RUR";
    private final static String CODE_OTP = "0000";

    @MockBean
    TransferService transferService;
    @MockBean
    TransferMapper transferMapper;

    @Autowired
    private MockMvc mockMvc;

    private Transfer transfer;
    private TransferDto dto;
    private PaymentAmount paymentAmount;

    @BeforeEach
    void init() throws ParseException {
        Card cardFrom = new Card(CARD_FROM_NUMBER, FORMATTER.parse(DATE), TEN, CARD_CVV);
        Card cardTo = new Card(CARD_TO_NUMBER, FORMATTER.parse(DATE), BigInteger.ZERO, CARD_CVV);
        paymentAmount = new PaymentAmount();
        paymentAmount.setValue(TEN);
        paymentAmount.setCurrency(CURRENCY);
        dto = new TransferDto();
        dto.setCardFromNumber(CARD_FROM_NUMBER);
        dto.setCardFromValidTill(DATE);
        dto.setCardFromCVV(CARD_CVV);
        dto.setCardToNumber(CARD_TO_NUMBER);
        dto.setAmount(paymentAmount);
        transfer = new Transfer();
        transfer.setCardFrom(cardFrom);
        transfer.setCardTo(cardTo);
        transfer.setAmount(paymentAmount);
    }

    @Test
    void transfer() throws Exception {
        String excepted = "{" +
                "\"operationId\":\"" + ID + "\"" +
                "}";
        when(transferService.transfer(any())).thenReturn(ID);
        when(transferMapper.mapToTransfer(any())).thenReturn(transfer);

        MvcResult result = mockMvc.perform(post("/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{" +
                                "\"cardFromNumber\":\"" + CARD_FROM_NUMBER + "\"," +
                                "\"cardFromValidTill\":\"" + DATE + "\"," +
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
        verify(transferMapper).mapToTransfer(any(TransferDto.class));
        verify(transferService).transfer(transfer);
        verifyNoMoreInteractions(transferService, transferMapper);
    }

    @Test
    void confirmOperation() throws Exception {
        Confirm confirm = new Confirm(1L, CODE_OTP);
        String excepted = "{" +
                "\"operationId\":\"" + ID + "\"" +
                "}";
        when(transferService.confirm(any())).thenReturn(ID);

        MvcResult result = mockMvc.perform(post("/confirmOperation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{" +
                                "\"operationId\":\"" + confirm.getOperationId().toString() + "\"," +
                                "\"code\":\"" + confirm.getCode() + "\"" +
                                "}"))

                .andExpect(status().isOk())
                .andReturn();
        assertEquals(excepted, result.getResponse().getContentAsString());
        verify(transferService).confirm(confirm);
        verifyNoMoreInteractions(transferService);
    }

    @Test
    void transfer_handleIllegalArgumentException() throws Exception {
        PaymentAmount paymentAmount = new PaymentAmount();
        Transfer transfer = new Transfer();
        transfer.setAmount(paymentAmount);
        when(transferService.transfer(any())).thenThrow(new IllegalArgumentException("test"));

        MvcResult result = mockMvc.perform(post("/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{" +
                                "\"cardFromNumber\":\"" + CARD_FROM_NUMBER + "\"," +
                                "\"cardFromValidTill\":\"" + DATE + "\"," +
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
        when(transferService.confirm(any())).thenThrow(new IllegalArgumentException("test"));

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