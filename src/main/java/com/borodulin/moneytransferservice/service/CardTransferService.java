package com.borodulin.moneytransferservice.service;

import com.borodulin.moneytransferservice.dao.CardTransferDao;
import com.borodulin.moneytransferservice.model.Confirm;
import com.borodulin.moneytransferservice.model.Transfer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
@RequiredArgsConstructor
public class CardTransferService {
    private final CardTransferDao cardTransferDao;

    @Transactional
    public String transfer(Transfer inTransfer) {
        Transfer transfer = cardTransferDao.save(inTransfer);

        log.info(String.format("Перевод с идентификатором %s от %s с карты %s на карту %s был на сумму %s зарегистрирован",
                transfer.getId(),
                LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                transfer.getCardFromNumber(),
                transfer.getCardToNumber(),
                transfer.getAmount()));

        return transfer
                .getId()
                .toString();
    }

    @Transactional
    public String confirm(Confirm confirm) {
        Transfer transfer = cardTransferDao.findById(confirm.getOperationId())
                .orElseThrow(() -> new IllegalArgumentException("Перевод не найден"));

        transfer.setCode(confirm.getCode());

        log.info(String.format("Перевод с идентификатором %s был подтвержден", transfer.getId()));

        return transfer
                .getId()
                .toString();
    }
}
