package ru.tggc.botapp.service;

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
import ru.tggc.botapp.domain.dto.fight.BossFightState;
import ru.tggc.botapp.keyboard.KeyboardFactory;
import ru.tggc.botapp.keyboard.KeyboardKey;
import ru.tggc.telegrambotframework.service.TelegramBotSender;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static ru.tggc.telegrambotframework.util.Utils.ifPresent;


@Service
@RequiredArgsConstructor
public class BossFightMessageSender {
    private final TelegramBotSender sender;
    private final KeyboardFactory keyboardFactory;

    public void sendMessages(long chatId, int oldMessageId, BossFightState fight, TelegramBot bot) {
        List<AnimationStep> steps = getAnimationSteps(fight.getActionLogs());
        bot.execute(new DeleteMessage(chatId, oldMessageId));
        int messageId = bot.execute(new SendPhoto(chatId, "https://thumbs.dreamstime.com/b/%D0%BF%D1%80%D0%B8%D0%BC%D0%B0%D0%BD%D0%BA%D0%B0-%D0%BA%D1%80%D0%BE%D0%BA%D0%BE-%D0%B8-%D0%B0-%D0%B0%D1%82%D0%B0%D0%BA%D1%83%D1%8F-75539401.jpg")).message().messageId();
        for (int i = 1; i < steps.size() + 1; i++) {
            AnimationStep step = steps.get(i - 1);
            sender.sendDelayed(telegramBot -> {
                if (step.photoPath != null) {
                    EditMessageMedia request = new EditMessageMedia(
                            chatId,
                            messageId,
                            new InputMediaPhoto(step.photoPath)
                                    .caption(step.text)
                    );
                    ifPresent(step.markup, request::replyMarkup);
                    telegramBot.execute(request);
                } else {
                    telegramBot.execute(new EditMessageText(chatId, messageId, step.text));
                }
            }, i * 4L);
        }

        sender.sendDelayed(telegramBot -> {
            String text = steps.stream()
                    .map(AnimationStep::getText)
                    .collect(Collectors.joining());
            telegramBot.execute(new EditMessageCaption(chatId, messageId).caption(text).replyMarkup(keyboardFactory.getKeyboardInline(KeyboardKey.FIGHT)));
            fight.getPlayers().values().forEach(ps -> ps.setLastAction(null));
            fight.setActionLogs(new ArrayList<>());
        }, (steps.size() + 1) * 4L);
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
