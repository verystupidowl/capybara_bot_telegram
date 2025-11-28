package ru.tggc.capybaratelegrambot.service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.model.request.InputMediaPhoto;
import com.pengrad.telegrambot.request.DeleteMessage;
import com.pengrad.telegrambot.request.EditMessageCaption;
import com.pengrad.telegrambot.request.EditMessageMedia;
import com.pengrad.telegrambot.request.EditMessageText;
import com.pengrad.telegrambot.request.SendPhoto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import ru.tggc.capybaratelegrambot.domain.fight.BossFightState;
import ru.tggc.capybaratelegrambot.keyboard.InlineKeyboardCreator;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static ru.tggc.capybaratelegrambot.utils.Utils.ifPresent;

@Service
@RequiredArgsConstructor
public class BossFightMessageSender {
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    private final InlineKeyboardCreator inlineKeyboardCreator;

    public int sendMessages(long chatId, int oldMessageId, BossFightState fight, TelegramBot bot) {
        List<AnimationStep> steps = getAnimationSteps(fight.getActionLogs());
        CompletableFuture<Void> overall = new CompletableFuture<>();
        bot.execute(new DeleteMessage(chatId, oldMessageId));
        int messageId = bot.execute(new SendPhoto(chatId, "https://thumbs.dreamstime.com/b/%D0%BF%D1%80%D0%B8%D0%BC%D0%B0%D0%BD%D0%BA%D0%B0-%D0%BA%D1%80%D0%BE%D0%BA%D0%BE-%D0%B8-%D0%B0-%D0%B0%D1%82%D0%B0%D0%BA%D1%83%D1%8F-75539401.jpg")).message().messageId();
        for (int i = 1; i < steps.size() + 1; i++) {
            AnimationStep step = steps.get(i - 1);
            scheduler.schedule(() -> {
                try {
                    if (step.photoPath != null) {
                        EditMessageMedia request = new EditMessageMedia(
                                chatId,
                                messageId,
                                new InputMediaPhoto(step.photoPath)
                                        .caption(step.text)
                        );
                        ifPresent(step.markup, request::replyMarkup);
                        bot.execute(request);
                    } else {
                        bot.execute(new EditMessageText(chatId, messageId, step.text));
                    }
                } catch (Exception e) {
                    overall.completeExceptionally(e);
                }
            }, i * 4L, TimeUnit.SECONDS);
        }

        scheduler.schedule(() -> {
            String text = steps.stream()
                    .map(AnimationStep::getText)
                    .collect(Collectors.joining());
            bot.execute(new EditMessageCaption(chatId, messageId).caption(text).replyMarkup(inlineKeyboardCreator.fightKeyboard()));
            fight.getPlayers().values().forEach(ps -> ps.setLastAction(null));
            fight.setActionLogs(new ArrayList<>());
            overall.complete(null);
        }, (steps.size() + 1) * 4L, TimeUnit.SECONDS);
        return messageId;
    }


    @NotNull
    private List<AnimationStep> getAnimationSteps(List<String> playersAction) {
        return playersAction.stream()
                .map(log -> new AnimationStep(
                        log + "\n==========================\n",
                        "https://thumbs.dreamstime.com/b/%D0%BF%D1%80%D0%B8%D0%BC%D0%B0%D0%BD%D0%BA%D0%B0-%D0%BA%D1%80%D0%BE%D0%BA%D0%BE-%D0%B8-%D0%B0-%D0%B0%D1%82%D0%B0%D0%BA%D1%83%D1%8F-75539401.jpg"
                ))
                .toList();
    }

    public void sendFinishMessage(List<String> actionLogs, boolean bossDead, long chatId, TelegramBot bot, int messageId, int cost) {
        String text = String.join("\n==========================\n", actionLogs) +
                (bossDead ? "\nТы заработал " + cost : "boss won!");
        bot.execute(new EditMessageCaption(chatId, messageId).caption(text));
    }

    @AllArgsConstructor
    @Data
    public static class AnimationStep {
        private String text;
        private String photoPath;
        private InlineKeyboardMarkup markup;

        public AnimationStep(String text, String photoPath) {
            this.text = text;
            this.photoPath = photoPath;
        }
    }
}
