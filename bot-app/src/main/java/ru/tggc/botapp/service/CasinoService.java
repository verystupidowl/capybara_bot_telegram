package ru.tggc.botapp.service;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.request.InputMediaPhoto;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.DeleteMessage;
import com.pengrad.telegrambot.request.EditMessageCaption;
import com.pengrad.telegrambot.request.EditMessageMedia;
import com.pengrad.telegrambot.request.SendDice;
import com.pengrad.telegrambot.request.SendPhoto;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.tggc.botapp.domain.model.Capybara;
import ru.tggc.botapp.exceptions.CapybaraException;
import ru.tggc.botapp.exceptions.CapybaraHasNoMoneyException;
import ru.tggc.botapp.exceptions.CapybaraNotFoundException;
import ru.tggc.botapp.formatter.msgkey.CasinoMsgKey;
import ru.tggc.botapp.formatter.msgkey.ErrorMsgKey;
import ru.tggc.botapp.keyboard.KeyboardType;
import ru.tggc.botapp.util.CasinoTargetType;
import ru.tggc.botapp.util.RandomUtils;
import ru.tggc.botapp.util.SlotResult;
import ru.tggc.botapp.util.SlotType;
import ru.tggc.telegrambotcore.dto.PhotoDto;
import ru.tggc.telegrambotcore.dto.Response;
import ru.tggc.telegrambotcore.dto.UpdateContext;
import ru.tggc.telegrambotcore.formatter.FormatService;
import ru.tggc.telegrambotcore.keyboard.KeyboardFactory;
import ru.tggc.telegrambotcore.service.HistoryService;
import ru.tggc.telegrambotcore.service.TelegramBotSender;
import ru.tggc.telegrambotcore.util.Utils;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.IntStream;

import static ru.tggc.telegrambotcore.util.Utils.throwIf;

@Service
@RequiredArgsConstructor
public class CasinoService {
    private final HistoryService historyService;
    private final CapybaraService capybaraService;
    private final TelegramBotSender sender;
    private final FormatService formatService;
    private final KeyboardFactory keyboardFactory;

    @Value("${bot.photos.casino.win}")
    private String winPhoto;
    @Value("${bot.photos.casino.lose}")
    private String losePhoto;
    @Value("${bot.photos.casino.set-bet}")
    private String casinoSetBetPhoto;

    @Setter(onMethod = @__({@Lazy, @Autowired}))
    private CasinoService self;

    public PhotoDto setBet(UpdateContext historyDto, String bet) {
        bet = Utils.checkNumber(bet);
        historyService.putData(historyDto, "bet", bet);
        return new PhotoDto(
                casinoSetBetPhoto,
                "Введите цель",
                historyDto.chatId(),
                keyboardFactory.getKeyboardInline(KeyboardType.CASINO_TARGET)
        );
    }

    @Transactional
    public Response casino(UpdateContext ctx, CasinoTargetType userGuess) {
        long chatId = ctx.chatId();
        Capybara capybara = capybaraService.findCapybara(ctx)
                .orElseThrow(CapybaraNotFoundException::new);

        Long betAmount = historyService.getData(ctx, "bet")
                .map(Long::parseLong)
                .orElseThrow();

        checkBet(betAmount, capybara);

        CasinoTargetType wonType = RandomUtils.randomWeighted();
        boolean isWin = (wonType == userGuess);
        long winAmount = isWin ? userGuess.getCalculateWin().apply(betAmount) : 0;

        if (isWin) {
            capybara.increaseMoney((int) winAmount);
        } else {
            capybara.decreaseMoney(Math.toIntExact(betAmount));
        }
        capybaraService.save(capybara);

        String[] frames = getRouletteFrames(wonType);

        return bot -> {
            SendPhoto sp = new SendPhoto(chatId, casinoSetBetPhoto)
                    .caption(frames[0])
                    .parseMode(ParseMode.HTML);
            Message msg = bot.execute(sp).message();
            int messageId = msg.messageId();

            for (int i = 1; i < frames.length; i++) {
                final int frameIndex = i;
                sender.sendDelayed(b -> {
                    b.execute(new EditMessageCaption(chatId, messageId)
                            .caption(frames[frameIndex])
                            .parseMode(ParseMode.HTML));
                }, i * 1000L);
            }

            sender.sendDelayed(b -> {
                String resultEmoji = getEmojiForType(wonType);
                String resultText = isWin
                        ? "🎉 <b>Победа!</b> Выпало " + resultEmoji + " Вы выиграли <b>" + winAmount + "</b> 🪙!"
                        : "😢 <b>Увы!</b> Выпало " + resultEmoji + " Ставка <b>" + betAmount + "</b> 🪙 сгорела.";

                String finalMessage = frames[3] + "\n\n" + resultText;

                InputMediaPhoto imp = new InputMediaPhoto(isWin ? winPhoto : losePhoto)
                        .caption(finalMessage)
                        .parseMode(ParseMode.HTML);
                b.execute(new EditMessageMedia(chatId, messageId, imp)
                        .replyMarkup(keyboardFactory.getKeyboardInline(KeyboardType.TO_MAIN_MENU)));
            }, 4000L);

            return CompletableFuture.completedFuture(null);
        };
    }

    private String[] getRouletteFrames(CasinoTargetType wonType) {
        String template = """
                🎡 <b>Ставки сделаны! Рулетка крутится...</b>
                
                <b>[ %s ]</b>
                                   ⬆️""";

        return switch (wonType) {
            case RED -> new String[]{
                    String.format(template, "⬛ | 🟢 | ⬛ | 🔴 | ⬛"),
                    String.format(template, "🟢 | ⬛ | 🔴 | ⬛ | 🔴"),
                    String.format(template, "🔴 | ⬛ | 🔴 | ⬛ | 🟢"),
                    String.format(template, "⬛ | 🟢 | 🔴 | ⬛ | 🔴")
            };
            case BLACK -> new String[]{
                    String.format(template, "🟢 | ⬛ | 🔴 | ⬛ | 🔴"),
                    String.format(template, "⬛ | 🔴 | ⬛ | 🔴 | ⬛"),
                    String.format(template, "🔴 | ⬛ | 🟢 | 🔴 | ⬛"),
                    String.format(template, "🟢 | 🔴 | ⬛ | 🔴 | ⬛")
            };
            case ZERO -> new String[]{
                    String.format(template, "🔴 | ⬛ | 🔴 | ⬛ | 🔴"),
                    String.format(template, "⬛ | 🔴 | ⬛ | 🔴 | ⬛"),
                    String.format(template, "🔴 | ⬛ | 🔴 | ⬛ | 🟢"),
                    String.format(template, "🔴 | ⬛ | 🟢 | 🔴 | ⬛")
            };
        };
    }


    private String getEmojiForType(CasinoTargetType type) {
        return switch (type) {
            case RED -> "🔴 Красное";
            case BLACK -> "⬛ Черное";
            case ZERO -> "🟢 Зеро";
        };
    }

    public Response slots(UpdateContext ctx, long bet) {
        Capybara capybara = capybaraService.findCapybara(ctx)
                .orElseThrow(CapybaraNotFoundException::new);
        checkBet(bet, capybara);

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

            long win = self.processSlots(capybara, bet, slotResult);
            sender.sendDelayed(tb -> {
                long chatId = ctx.chatId();
                SendPhoto sendPhoto;
                if (slotResult == SlotResult.LOSE) {
                    sendPhoto = new SendPhoto(chatId, losePhoto);
                    sendPhoto.caption(formatService.get(CasinoMsgKey.CASINO_SLOTS_LOSE, bet));
                } else {
                    sendPhoto = new SendPhoto(chatId, winPhoto);
                    sendPhoto.caption(formatService.get(CasinoMsgKey.CASINO_SLOTS_WIN, (win - bet)));
                }
                tb.execute(sendPhoto
                        .parseMode(ParseMode.HTML)
                        .replyMarkup(keyboardFactory.getKeyboardInline(KeyboardType.TO_MAIN_MENU)));

                sender.sendDelayed(b -> {
                    b.execute(new DeleteMessage(chatId, response.messageId()));
                }, 10000L);

            }, 2500L);
            return CompletableFuture.completedFuture(null);
        };
    }

    @Transactional
    public long processSlots(Capybara capybara, long bet, SlotResult slotResult) {
        long win = (long) (bet * slotResult.multiplier());
        long currency = capybara.getCurrency() - bet + win;
        capybara.setCurrency(currency);
        capybaraService.save(capybara);
        return win;
    }

    private void checkBet(long bet, Capybara capybara) {
        throwIf(capybara.getCurrency() < bet, CapybaraHasNoMoneyException::new);
        long minBetAmount = (capybara.getLevel().getValue() / 10) * 25L;

        throwIf(bet < minBetAmount, () -> {
            String message = formatService.get(ErrorMsgKey.CASINO_MIN_BET, minBetAmount);
            return new CapybaraException(message);
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

    public PhotoDto getInfo(UpdateContext ctx) {
        return new PhotoDto(
                casinoSetBetPhoto,
                "Казино",
                ctx.chatId(),
                keyboardFactory.getKeyboardInline(KeyboardType.CASINO_INFO)
        );
    }
}
