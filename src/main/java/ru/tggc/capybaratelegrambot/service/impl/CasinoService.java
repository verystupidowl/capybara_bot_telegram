package ru.tggc.capybaratelegrambot.service.impl;

import com.pengrad.telegrambot.model.Message;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import ru.tggc.capybaratelegrambot.domain.dto.CapybaraContext;
import ru.tggc.capybaratelegrambot.domain.model.Capybara;
import ru.tggc.capybaratelegrambot.exceptions.CapybaraException;
import ru.tggc.capybaratelegrambot.exceptions.CapybaraHasNoMoneyException;
import ru.tggc.capybaratelegrambot.utils.CasinoTargetType;
import ru.tggc.capybaratelegrambot.utils.HistoryType;
import ru.tggc.capybaratelegrambot.utils.RandomUtils;
import ru.tggc.capybaratelegrambot.utils.SlotResult;
import ru.tggc.capybaratelegrambot.utils.SlotType;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class CasinoService {
    private final HistoryService historyService;
    private final CapybaraService capybaraService;

    private final Map<CapybaraContext, CasinoCtx> map = new ConcurrentHashMap<>();

    public void startCasino(CapybaraContext ctx) {
        historyService.setHistory(ctx, HistoryType.CASINO_SET_BET);
    }

    public void setBet(CapybaraContext historyDto, String bet) {
        CasinoCtx ctx = CasinoCtx.builder()
                .bet(Long.valueOf(bet))
                .build();
        if (map.containsKey(historyDto)) {
            throw new CapybaraException("");
        }
        map.put(historyDto, ctx);
    }

    public String casino(CapybaraContext historyDto, CasinoTargetType type) {
        String chatId = historyDto.chatId();
        String userId = historyDto.userId();
        Capybara capybara = capybaraService.getCapybara(Long.valueOf(userId), chatId);
        if (!map.containsKey(historyDto)) {
            throw new CapybaraException("");
        }
        CasinoCtx ctx = map.get(historyDto);
        Long betAmount = ctx.getBet();

        if (capybara.getCurrency() <= betAmount) {
            throw new CapybaraHasNoMoneyException("", chatId);
        }
        if (betAmount < (capybara.getLevel().getValue() / 10) * 25L) {
            throw new CapybaraException("ur min bet amount is ");
        }
        CasinoTargetType wonType = RandomUtils.randomWeighted();
        String response;

        if (wonType == type) {
            Long winAmount = type.getCalculateWin().apply(betAmount);
            capybara.setCurrency(capybara.getCurrency() + winAmount);
            response = "Вау! Вот это везение! Твоя капибара выиграла " + winAmount;
        } else {
            capybara.setCurrency(capybara.getCurrency() - betAmount);
            response = "Твоя капибара была близка, но проиграла " + betAmount;
        }

        map.remove(historyDto);

        capybaraService.save(capybara);
        return response;
    }

    public String slots(CapybaraContext historyDto, Message response) {
        Capybara capybara = capybaraService.getCapybaraByUserId(historyDto.userId(), historyDto.chatId());
        if (!map.containsKey(historyDto)) {
            throw new CapybaraException("");
        }
        Long bet = map.get(historyDto).getBet();
        int diceValue = response.dice().value() - 1;
        List<SlotType> result = IntStream.range(0, 3)
                .mapToObj(i -> {
                    int index = (diceValue - 1) / (int) Math.pow(4, i) % 4;
                    return SlotType.fromIndex(index);
                })
                .toList();

        SlotResult slotResult = getResult(result);

        long win = (long) (bet * slotResult.multiplier());
        capybara.setCurrency(capybara.getCurrency() + win);
        capybaraService.save(capybara);
        map.remove(historyDto);
        if (slotResult == SlotResult.LOSE) {
            return "Не повезло( Твоя капибара проиграла " + bet;
        }
        return "Твоя капибара выиграла " + win;
    }

    @NotNull
    private static SlotResult getResult(List<SlotType> result) {
        if (result.stream().allMatch(s -> s == SlotType.SEVEN)) {
            return SlotResult.JACKPOT;
        } else if (result.stream().distinct().count() == 1) {
            return SlotResult.TRIPLE;
        } else if (result.getFirst() == result.get(1)) {
            return SlotResult.DOUBLE;
        } else {
            return SlotResult.LOSE;
        }
    }

    @Builder
    @Data
    static class CasinoCtx {
        private Long bet;
        private CasinoTargetType target;
    }
}
