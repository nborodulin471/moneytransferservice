package com.borodulin.moneytransferservice.model;

import javax.validation.constraints.NotNull;
import java.math.BigInteger;

public class TransferDto {
    @NotNull
    private Card cardFromNumber;
    @NotNull
    private Card cardToNumber;
    @NotNull
    private PaymentAmount amount;
    private String code;
    private BigInteger commission;

    public Transfer mapToTransfer() {
        return new Transfer(
                null,
                cardFromNumber,
                cardToNumber,
                amount,
                code,
                commission);
    }

    ;
}
