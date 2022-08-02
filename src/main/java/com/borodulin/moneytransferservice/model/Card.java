package com.borodulin.moneytransferservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.math.BigInteger;
import java.util.Date;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Card {
    @Id
    private String number;
    private Date validTill;
    private BigInteger balance; // минимальные денежные единицы
    private int cvv;
}
