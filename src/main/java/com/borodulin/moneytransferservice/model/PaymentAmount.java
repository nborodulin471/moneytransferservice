package com.borodulin.moneytransferservice.model;

import lombok.Data;

import javax.persistence.Embeddable;

@Data
@Embeddable
public class PaymentAmount {
    private int value;
    private String currency;
}
