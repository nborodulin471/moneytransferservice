package com.borodulin.moneytransferservice.model;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class Confirm {
    @NotNull
    private final Long operationId;
    @NotBlank
    private final String code;
}
