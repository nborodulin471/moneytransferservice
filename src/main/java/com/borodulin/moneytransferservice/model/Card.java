package com.borodulin.moneytransferservice.model;

import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.math.BigInteger;
import java.util.Date;
import java.util.Objects;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Card {
    @Id
    private String number;
    private Date validTill;
    private BigInteger balance; // минимальные денежные единицы
    private String cvv;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Card card = (Card) o;
        return number != null && Objects.equals(number, card.number);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
