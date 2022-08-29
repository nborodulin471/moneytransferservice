package com.borodulin.moneytransferservice.controller;

import com.borodulin.moneytransferservice.model.Confirm;
import com.borodulin.moneytransferservice.model.TransferDto;
import com.borodulin.moneytransferservice.service.TransferService;
import com.borodulin.moneytransferservice.utils.TransferMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class CardTransferController {
    private final TransferService transferService;
    private final TransferMapper transferMapper;

    @PostMapping("/transfer")
    public ResponseEntity<Map<String, String>> transfer(@RequestBody @Valid TransferDto transfer) {
        return ResponseEntity.ok(
                Map.of("operationId", transferService.transfer(transferMapper.mapToTransfer(transfer)))
        );
    }

    @PostMapping("/confirmOperation")
    public ResponseEntity<Map<String, String>> confirmOperation(@RequestBody @Valid Confirm confirm) {
        return ResponseEntity.ok(
                Map.of("operationId", transferService.confirm(confirm))
        );
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(IllegalArgumentException.class)
    public Map<String, String> handleIllegalArgumentException(IllegalArgumentException e) {
        log.error(e.getLocalizedMessage());
        return Map.of(
                "message", e.getLocalizedMessage(),
                "id", UUID.randomUUID().toString()
        );
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException e) {
        log.error(e.getLocalizedMessage());
        return Map.of(
                "message", e.getLocalizedMessage(),
                "id", UUID.randomUUID().toString()
        );
    }
}
