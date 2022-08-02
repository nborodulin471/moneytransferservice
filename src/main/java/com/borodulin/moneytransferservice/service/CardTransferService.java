package com.borodulin.moneytransferservice.service;

import com.borodulin.moneytransferservice.dao.CardTransferDao;
import com.borodulin.moneytransferservice.model.Confirm;
import com.borodulin.moneytransferservice.model.Transfer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
@RequiredArgsConstructor
public class CardTransferService {
    private static final String OTP_CODE = "0000";

    private final CardTransferDao cardTransferDao;

    @Transactional
    public String transfer(Transfer inTransfer) {
        Transfer transfer = cardTransferDao.save(inTransfer);

        if(isTransferValid(transfer)){
             throw new IllegalArgumentException("Транзакция не валидна");
        }

        log.debug(String.format("Перевод с идентификатором %s от %s с карты %s на карту %s был на сумму %s зарегистрирован",
                transfer.getId(),
                LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                transfer.getCardFrom(),
                transfer.getCardTo(),
                transfer.getAmount()));

        return transfer
                .getId()
                .toString();
    }

    private boolean isTransferValid(Transfer transfer) {
        if (transfer.getCardFrom().getBalance().compareTo(BigInteger.valueOf(0)) > 0
                && transfer.getCardFrom().getNumber() != transfer.getCardTo().getNumber()){
            return false;
        }
        return true;
    }

    @Transactional
    public String confirm(Confirm confirm) {
        Transfer transfer = cardTransferDao.findById(confirm.getOperationId())
                .orElseThrow(() -> new IllegalArgumentException("Перевод не найден"));

        if (confirm.getCode().equals(OTP_CODE)){
            throw new IllegalArgumentException("Подтверждающий код не прошел проверку");
        }

        transfer.setCode(confirm.getCode());

        log.info(String.format("Перевод с идентификатором %s был подтвержден", transfer.getId()));

        return transfer
                .getId()
                .toString();
    }
}
