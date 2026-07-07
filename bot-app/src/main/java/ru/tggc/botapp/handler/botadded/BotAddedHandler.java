package ru.tggc.botapp.handler.botadded;

import lombok.RequiredArgsConstructor;
import ru.tggc.botapp.keyboard.KeyboardFactory;
import ru.tggc.botapp.keyboard.KeyboardKey;
import ru.tggc.botapp.util.Text;
import ru.tggc.telegrambotcore.annotation.handle.BotAddedHandle;
import ru.tggc.telegrambotcore.annotation.handle.BotHandler;
import ru.tggc.telegrambotcore.annotation.params.Ctx;
import ru.tggc.telegrambotcore.dto.Response;
import ru.tggc.telegrambotcore.dto.UpdateContext;
import ru.tggc.telegrambotcore.handler.Handler;

@BotHandler
@RequiredArgsConstructor
public class BotAddedHandler extends Handler {
    private final KeyboardFactory keyboardFactory;

    @BotAddedHandle
    public Response botAdded(@Ctx UpdateContext ctx) {
        return sendSimpleMessage(
                ctx.chatId(),
                Text.GREETINGS,
                keyboardFactory.getKeyboardInline(KeyboardKey.TAKE_CAPYBARA)
        );
    }
}
