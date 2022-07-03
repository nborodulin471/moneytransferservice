package com.borodulin.moneytransferservice.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigInteger;
import java.util.Date;

/**
 * Модель с описанием сущности перевода
 *
 * @implNote поля с чувствительной информацией типо cvv, номера карты и т.д
 * должны приходить зашифрованными, а мы на своей стороне должны это обрабатывать,
 * но так как это не требуется условиями задачи, то это не учтено в данной реализации.
 * Так же данная информация не должна логироваться, но это проигнорировано, т.к нужен кастомный логер
 */
@Data
@Entity
@NoArgsConstructor
public class Transfer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    private BigInteger cardFromNumber;
    @NotNull
    private Date cardFromValidTill;
    @NotNull
    private Integer cardFromCVV;
    @NotNull
    private BigInteger cardToNumber;
    @Embedded
    @NotNull
    private PaymentAmount amount;
    @Nullable
    private String code;
    @Nullable
    private BigInteger commission;
}
