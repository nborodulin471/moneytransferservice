package com.borodulin.moneytransferservice.utils;

import com.borodulin.moneytransferservice.dao.CardDao;
import com.borodulin.moneytransferservice.model.Card;
import com.borodulin.moneytransferservice.model.Transfer;
import com.borodulin.moneytransferservice.model.TransferDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


@Component
@RequiredArgsConstructor
public class TransferMapper {
    public static final SimpleDateFormat FORMATTER = new SimpleDateFormat("MM-dd-yyyy");

    private final CardDao cardDao;

    public Transfer mapToTransfer(TransferDto transferDto) {
        Card cardFrom = cardDao.findById(transferDto.getCardFromNumber())
                .orElseThrow(() -> new IllegalArgumentException("Не удалось найти карту отправителя по переданному номеру"));

        try {
            if (!cardFrom.getCvv().equals(transferDto.getCardFromCVV())
                    || !(new Date(cardFrom.getValidTill().getTime()).compareTo(FORMATTER.parse(transferDto.getCardFromValidTill())) == 0)) {
                throw new IllegalArgumentException("Переданные данные карты отправителя не соответствуют сохраненным");
            }
        } catch (ParseException e) {
            throw new IllegalArgumentException("Не удалось распознать дату срока действия карты отправителя");
        }

        return new Transfer(
                cardFrom,
                cardDao.findById(transferDto.getCardToNumber())
                        .orElseThrow(() -> new IllegalArgumentException("Не удалось найти карту получателя по переданному номеру")),
                transferDto.getAmount());
    }
}
