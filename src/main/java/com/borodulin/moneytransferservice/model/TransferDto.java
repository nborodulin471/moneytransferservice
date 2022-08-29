package com.borodulin.moneytransferservice.model;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class TransferDto {
    @NotBlank
    private String cardFromNumber;
    @NotBlank
    private String cardFromValidTill;
    @NotBlank
    private String cardFromCVV;
    @NotBlank
    private String cardToNumber;
    @NotNull
    private PaymentAmount amount;
}
