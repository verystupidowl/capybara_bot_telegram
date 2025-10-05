package ru.tggc.capybaratelegrambot.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.request.SendDice;
import com.pengrad.telegrambot.request.SendPhoto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.tggc.capybaratelegrambot.domain.dto.CapybaraContext;
import ru.tggc.capybaratelegrambot.domain.dto.PhotoDto;
import ru.tggc.capybaratelegrambot.domain.model.Capybara;
import ru.tggc.capybaratelegrambot.domain.response.Response;
import ru.tggc.capybaratelegrambot.exceptions.CapybaraException;
import ru.tggc.capybaratelegrambot.exceptions.CapybaraHasNoMoneyException;
import ru.tggc.capybaratelegrambot.exceptions.CapybaraNotFoundException;
import ru.tggc.capybaratelegrambot.utils.CasinoTargetType;
import ru.tggc.capybaratelegrambot.utils.HistoryType;
import ru.tggc.capybaratelegrambot.utils.RandomUtils;
import ru.tggc.capybaratelegrambot.utils.SlotResult;
import ru.tggc.capybaratelegrambot.utils.SlotType;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import static ru.tggc.capybaratelegrambot.utils.Utils.throwIf;
import static ru.tggc.capybaratelegrambot.utils.Utils.throwIfNull;

@Service
@RequiredArgsConstructor
public class CasinoService {
    private final HistoryService historyService;
    private final CapybaraService capybaraService;

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(4);
    private final Cache<CapybaraContext, CasinoCtx> map = Caffeine.newBuilder()
            .expireAfterWrite(Duration.ofMinutes(5))
            .maximumSize(10_000)
            .build();

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
        if (map.getIfPresent(historyDto) != null) {
            throw new CapybaraException("ошибка!");
        }
        map.put(historyDto, ctx);
    }

    @Transactional
    public PhotoDto casino(CapybaraContext historyDto, CasinoTargetType type) {
        long chatId = historyDto.chatId();
        Capybara capybara = capybaraService.findCapybara(historyDto)
                .orElseThrow(() -> {
                    map.invalidate(historyDto);
                    return new CapybaraNotFoundException();
                });

        throwIfNull(map.getIfPresent(historyDto), () -> new CapybaraException("Ты не играешь"));
        CasinoCtx ctx = map.get(historyDto, dto -> new CasinoCtx());
        Long betAmount = ctx.getBet();

        checkBet(historyDto, betAmount, capybara);

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

        map.invalidate(historyDto);

        capybaraService.save(capybara);
        return response;
    }

    @Transactional
    public Response slots(CapybaraContext ctx, long bet) {
        throwIfNull(map.getIfPresent(ctx), () -> new CapybaraException("Ты не играешь!"));

        Capybara capybara = capybaraService.findCapybara(ctx)
                .orElseThrow(() -> {
                    map.invalidate(ctx);
                    return new CapybaraNotFoundException();
                });
        checkBet(ctx, bet, capybara);

        return bot -> {
            Message response = bot.execute(new SendDice(ctx.chatId()).slotMachine()).message();
            int diceValue = response.dice().value() - 1;
            List<SlotType> result = IntStream.range(0, 3)
                    .mapToObj(i -> {
                        int index = (diceValue) / (int) Math.pow(4, i) % 4;
                        return SlotType.fromIndex(index);
                    })
                    .toList();

            SlotResult slotResult = getResult(result);

            map.invalidate(ctx);
            long win = (long) (bet * slotResult.multiplier());
            capybara.setCurrency(capybara.getCurrency() + win);
            capybaraService.save(capybara);
            CompletableFuture<Void> future = new CompletableFuture<>();
            scheduler.schedule(() -> {
                long chatId = ctx.chatId();
                SendPhoto sendPhoto;
                if (slotResult == SlotResult.LOSE) {
                    sendPhoto = new SendPhoto(chatId, LOSE_PHOTO_URL);
                    sendPhoto.caption("Не повезло( Твоя капибара проиграла " + bet);
                } else {
                    sendPhoto = new SendPhoto(chatId, WIN_PHOTO_URL);
                    sendPhoto.caption("Твоя капибара выиграла " + win);
                }
                bot.execute(sendPhoto);
                future.complete(null);
            }, 2000, TimeUnit.MILLISECONDS);
            return future;
        };
    }

    private void checkBet(CapybaraContext ctx, long bet, Capybara capybara) {
        throwIf(capybara.getCurrency() < bet, () -> {
            map.invalidate(ctx);
            return new CapybaraHasNoMoneyException();
        });
        long minBetAmount = (capybara.getLevel().getValue() / 10) * 25L;

        throwIf(bet < minBetAmount, () -> {
            map.invalidate(ctx);
            return new CapybaraException("Минимальная твоя ставка - " + minBetAmount);
        });
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
    @NoArgsConstructor
    @AllArgsConstructor
    static class CasinoCtx {
        private Long bet;
        private CasinoTargetType target;
    }
}
