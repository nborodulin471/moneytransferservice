package com.borodulin.moneytransferservice.service;

import com.borodulin.moneytransferservice.dao.TransferDao;
import com.borodulin.moneytransferservice.model.Card;
import com.borodulin.moneytransferservice.model.Confirm;
import com.borodulin.moneytransferservice.model.Transfer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransferService {
    private static final String OTP_CODE = "0000";

    private final TransferDao transferDao;

    /**
     * Выполняет перевод между картами
     *
     * @param inTransfer сущность содержащая данные входящего перевода
     * @return возвращает идентификатор перевода сохраненного в БД
     * @implNote после проверки валидации и обновления баланса карт - данные карт автоматически обновляются в БД.
     */
    @Transactional
    public String transfer(Transfer inTransfer) {
        Card cardFrom = inTransfer.getCardFrom();
        Card cardTo = inTransfer.getCardTo();

        isTransferValid(cardFrom, cardTo);
        cardFrom.setBalance(
                cardFrom.getBalance().subtract(inTransfer.getAmount().getValue()) // вычесть баланс с карты отправителя
        );
        cardTo.setBalance(
                cardTo.getBalance().add(inTransfer.getAmount().getValue()) // прибавить на баланс получателя
        );
        Transfer transfer = transferDao.save(inTransfer);

        log.debug(String.format("Перевод с идентификатором %s от %s был зарегистрирован",
                transfer.getId(),
                LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)));

        return transfer
                .getId()
                .toString();
    }

    private void isTransferValid(Card cardFrom, Card cardTo) {
        if (cardFrom.getBalance().compareTo(BigInteger.valueOf(0)) == 0
                || cardFrom.getNumber().equals(cardTo.getNumber())) {
            throw new IllegalArgumentException("Транзакция не валидна");
        }
    }

    @Transactional
    public String confirm(Confirm confirm) {
        Transfer transfer = transferDao.findById(confirm.getOperationId())
                .orElseThrow(() -> new IllegalArgumentException("Перевод не найден"));

        if (!confirm.getCode().equals(OTP_CODE)) {
            throw new IllegalArgumentException("Подтверждающий код не прошел проверку");
        }

        transfer.setCode(confirm.getCode());

        log.info(String.format("Перевод с идентификатором %s был подтвержден", transfer.getId()));

        return transfer
                .getId()
                .toString();
    }
}
