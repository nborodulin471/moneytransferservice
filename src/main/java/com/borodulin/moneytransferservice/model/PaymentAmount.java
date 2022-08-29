package com.borodulin.moneytransferservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import java.math.BigInteger;

@Data
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class PaymentAmount {
    private BigInteger value;
    private String currency;
}
