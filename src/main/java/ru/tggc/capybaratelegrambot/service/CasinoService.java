package ru.tggc.capybaratelegrambot.service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.request.SendDice;
import com.pengrad.telegrambot.request.SendPhoto;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import ru.tggc.capybaratelegrambot.domain.dto.CapybaraContext;
import ru.tggc.capybaratelegrambot.domain.dto.PhotoDto;
import ru.tggc.capybaratelegrambot.domain.model.Capybara;
import ru.tggc.capybaratelegrambot.exceptions.CapybaraException;
import ru.tggc.capybaratelegrambot.exceptions.CapybaraHasNoMoneyException;
import ru.tggc.capybaratelegrambot.exceptions.CapybaraNotFoundException;
import ru.tggc.capybaratelegrambot.utils.CasinoTargetType;
import ru.tggc.capybaratelegrambot.utils.HistoryType;
import ru.tggc.capybaratelegrambot.utils.RandomUtils;
import ru.tggc.capybaratelegrambot.utils.SlotResult;
import ru.tggc.capybaratelegrambot.utils.SlotType;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class CasinoService {
    private final HistoryService historyService;
    private final CapybaraService capybaraService;

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(4);
    private final Map<CapybaraContext, CasinoCtx> map = new ConcurrentHashMap<>();

    private static final String WIN_PHOTO_URL = "https://vk.com/photo-209917797_457246197";
    private static final String LOSE_PHOTO_URL = "https://vk.com/photo-209917797_457246192";

    public void startCasino(CapybaraContext ctx) {
        historyService.setHistory(ctx, HistoryType.CASINO_SET_BET);
    }

    public void setBet(CapybaraContext historyDto, String bet) {
        Long longBet = Long.parseLong(bet);
        CasinoCtx ctx = CasinoCtx.builder()
                .bet(longBet)
                .build();
        if (map.containsKey(historyDto)) {
            throw new CapybaraException("ошибка!");
        }
        map.put(historyDto, ctx);
    }

    public PhotoDto casino(CapybaraContext historyDto, CasinoTargetType type) {
        String chatId = historyDto.chatId();
        Capybara capybara = capybaraService.findCapybara(historyDto)
                .orElseThrow(() -> {
                    map.remove(historyDto);
                    return new CapybaraNotFoundException();
                });
        if (!map.containsKey(historyDto)) {
            throw new CapybaraException("Ты не играешь!");
        }
        CasinoCtx ctx = map.get(historyDto);
        Long betAmount = ctx.getBet();

        if (capybara.getCurrency() < betAmount) {
            map.remove(historyDto);
            throw new CapybaraHasNoMoneyException("", chatId);
        }
        long minBetAmount = (capybara.getLevel().getValue() / 10) * 25L;
        if (betAmount < minBetAmount) {
            map.remove(historyDto);
            throw new CapybaraException("ur min bet amount is " + minBetAmount);
        }
        CasinoTargetType wonType = RandomUtils.randomWeighted();
        PhotoDto response = PhotoDto.builder()
                .chatId(chatId)
                .build();

        if (wonType == type) {
            Long winAmount = type.getCalculateWin().apply(betAmount);
            capybara.setCurrency(capybara.getCurrency() + winAmount);
            response.setCaption("Вау! Вот это везение! Выпало " + wonType.getLabel() + "! Твоя капибара выиграла " + winAmount);
            response.setUrl(WIN_PHOTO_URL);
        } else {
            capybara.setCurrency(capybara.getCurrency() - betAmount);
            response.setCaption("Твоя капибара была близка Выпало " + wonType.getLabel() + "! она  проиграла " + betAmount);
            response.setUrl(LOSE_PHOTO_URL);
        }

        map.remove(historyDto);

        capybaraService.save(capybara);
        return response;
    }

    public BiConsumer<TelegramBot, CapybaraContext> slots(CapybaraContext historyDto, long bet) {
        if (!map.containsKey(historyDto)) {
            throw new CapybaraException("ты не играешь!");
        }
        Capybara capybara = capybaraService.findCapybara(historyDto)
                .orElseThrow(() -> {
                    map.remove(historyDto);
                    return new CapybaraNotFoundException();
                });
        return (bot, ctx) -> {
            if (capybara.getCurrency() < bet) {
                map.remove(historyDto);
                throw new CapybaraHasNoMoneyException();
            }

            Message response = bot.execute(new SendDice(ctx.chatId()).slotMachine()).message();
            int diceValue = response.dice().value() - 1;
            List<SlotType> result = IntStream.range(0, 3)
                    .mapToObj(i -> {
                        int index = (diceValue) / (int) Math.pow(4, i) % 4;
                        return SlotType.fromIndex(index);
                    })
                    .toList();

            SlotResult slotResult = getResult(result);

            long win = (long) (bet * slotResult.multiplier());
            capybara.setCurrency(capybara.getCurrency() + win);
            capybaraService.save(capybara);
            map.remove(historyDto);
            scheduler.schedule(() -> {
                long chatId = Long.parseLong(ctx.chatId());
                SendPhoto sendPhoto;
                if (slotResult == SlotResult.LOSE) {
                    sendPhoto = new SendPhoto(chatId, LOSE_PHOTO_URL);
                    sendPhoto.caption("Не повезло( Твоя капибара проиграла " + bet);
                } else {
                    sendPhoto = new SendPhoto(chatId, WIN_PHOTO_URL);
                    sendPhoto.caption("Твоя капибара выиграла " + win);
                }
                bot.execute(sendPhoto);
            }, 2000, TimeUnit.MILLISECONDS);
        };
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

    public void startSlots(CapybaraContext ctx) {
        map.put(ctx, new CasinoCtx(null, null));
        historyService.setHistory(ctx, HistoryType.SLOTS_SET_BET);
    }

    @Builder
    @Data
    static class CasinoCtx {
        private Long bet;
        private CasinoTargetType target;
    }
}
