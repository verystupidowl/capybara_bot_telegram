package ru.tggc.botapp.service;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.request.SendDice;
import com.pengrad.telegrambot.request.SendPhoto;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.tggc.botapp.domain.model.Capybara;
import ru.tggc.botapp.exceptions.CapybaraException;
import ru.tggc.botapp.exceptions.CapybaraHasNoMoneyException;
import ru.tggc.botapp.exceptions.CapybaraNotFoundException;
import ru.tggc.botapp.service.impl.HistoryServiceImpl;
import ru.tggc.botapp.util.CasinoTargetType;
import ru.tggc.botapp.util.HistoryType;
import ru.tggc.botapp.util.RandomUtils;
import ru.tggc.botapp.util.SlotResult;
import ru.tggc.botapp.util.SlotType;
import ru.tggc.telegrambotframework.dto.PhotoDto;
import ru.tggc.telegrambotframework.dto.Response;
import ru.tggc.telegrambotframework.dto.UpdateContext;
import ru.tggc.telegrambotframework.service.TelegramBotSender;
import ru.tggc.telegrambotframework.util.Utils;

import java.util.List;
import java.util.stream.IntStream;

import static ru.tggc.telegrambotframework.util.Utils.throwIf;
import static ru.tggc.telegrambotframework.util.Utils.throwIfNull;

@Service
@RequiredArgsConstructor
public class CasinoService {
    private final HistoryServiceImpl historyService;
    private final CapybaraService capybaraService;
    private final TelegramBotSender sender;

    private static final String WIN_PHOTO_URL = "https://vk.com/photo-209917797_457246197";
    private static final String LOSE_PHOTO_URL = "https://vk.com/photo-209917797_457246192";

    public void startCasino(UpdateContext ctx) {
        historyService.setHistory(ctx, HistoryType.CASINO_SET_BET);
    }

    public void setBet(UpdateContext historyDto, String bet) {
        bet = Utils.checkNumber(bet);
        throwIf(!historyService.isEmpty(historyDto), () -> new CapybaraException("Error"));
        historyService.putData(historyDto, "bet", bet);
    }

    @Transactional
    public PhotoDto casino(UpdateContext ctx, CasinoTargetType type) {
        long chatId = ctx.chatId();
        Capybara capybara = capybaraService.findCapybara(ctx)
                .orElseThrow(() -> {
                    historyService.removeFromHistory(ctx);
                    return new CapybaraNotFoundException();
                });

        throwIfNull(historyService.getFromHistory(ctx), () -> new CapybaraException("Ты не играешь"));
        Long betAmount = historyService.getData(ctx, "bet")
                .map(Long::parseLong)
                .orElseThrow();

        checkBet(ctx, betAmount, capybara);

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

        historyService.removeFromHistory(ctx);

        capybaraService.save(capybara);
        return response;
    }

    @Transactional
    public Response slots(UpdateContext ctx, long bet) {
        throwIfNull(historyService.getFromHistory(ctx), () -> new CapybaraException("Ты не играешь!"));

        Capybara capybara = capybaraService.findCapybara(ctx)
                .orElseThrow(() -> {
                    historyService.removeFromHistory(ctx);
                    return new CapybaraNotFoundException();
                });
        checkBet(ctx, bet, capybara);

        capybara.setCurrency(capybara.getCurrency() - bet);

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

            long win = (long) (bet * slotResult.multiplier());
            capybara.setCurrency(capybara.getCurrency() + win);
            capybaraService.save(capybara);
            sender.sendDelayed(tb -> {
                long chatId = ctx.chatId();
                SendPhoto sendPhoto;
                if (slotResult == SlotResult.LOSE) {
                    sendPhoto = new SendPhoto(chatId, LOSE_PHOTO_URL);
                    sendPhoto.caption("Не повезло( Твоя капибара проиграла " + bet);
                } else {
                    sendPhoto = new SendPhoto(chatId, WIN_PHOTO_URL);
                    sendPhoto.caption("Твоя капибара выиграла " + win);
                }
                tb.execute(sendPhoto);
                historyService.removeFromHistory(ctx);
            }, 2000L);
        };
    }

    private void checkBet(UpdateContext ctx, long bet, Capybara capybara) {
        throwIf(capybara.getCurrency() < bet, () -> {
            historyService.removeFromHistory(ctx);
            return new CapybaraHasNoMoneyException();
        });
        long minBetAmount = (capybara.getLevel().getValue() / 10) * 25L;

        throwIf(bet < minBetAmount, () -> {
            historyService.removeFromHistory(ctx);
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

    public void startSlots(UpdateContext ctx) {
        historyService.setHistory(ctx, HistoryType.SLOTS_SET_BET);
    }
}
