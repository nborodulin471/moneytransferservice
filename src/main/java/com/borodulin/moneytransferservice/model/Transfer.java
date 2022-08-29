package com.borodulin.moneytransferservice.model;

import lombok.*;
import org.hibernate.Hibernate;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.math.BigInteger;
import java.util.Objects;

/**
 * Модель с описанием сущности перевода
 *
 * @implNote поля с чувствительной информацией типо cvv, номера карты и т.д
 * должны приходить зашифрованными, а мы на своей стороне должны это обрабатывать,
 * но так как это не требуется условиями задачи, то это не учтено в данной реализации.
 * Так же данная информация не должна логироваться, но это проигнорировано, т.к нужен кастомный логер
 */
@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@RequiredArgsConstructor
public class Transfer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    @OneToOne
    private Card cardFrom;

    @NonNull
    @OneToOne
    private Card cardTo;

    @NonNull
    @Embedded
    private PaymentAmount amount;

    @Nullable
    @Size(max = 4)
    private String code;

    @Nullable
    private BigInteger commission;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Transfer transfer = (Transfer) o;
        return id != null && Objects.equals(id, transfer.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}

