package com.borodulin.moneytransferservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.math.BigInteger;


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
@AllArgsConstructor
public class Transfer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne
    private Card cardFrom;
    @OneToOne
    private Card cardTo;
    @Embedded
    private PaymentAmount amount;
    @Nullable
    @Size(max = 4)
    private String code;
    @Nullable
    private BigInteger commission;
}
