package ru.tggc.capybaratelegrambot.keyboard;

import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class KeyboardFactory {
    private final Map<KeyboardKey<?>, AbstractInlineKeyboardCreator<?>> keyboardInlineCreators;

    public KeyboardFactory(List<AbstractInlineKeyboardCreator<?>> keyboardInlineCreators) {
        this.keyboardInlineCreators = keyboardInlineCreators.stream()
                .collect(Collectors.toMap(AbstractInlineKeyboardCreator::getKeyboardKey, Function.identity()));
    }

    @SuppressWarnings("unchecked")
    public <T> AbstractInlineKeyboardCreator<T> getKeyboardCreator(KeyboardKey<T> key) {
        return (AbstractInlineKeyboardCreator<T>) keyboardInlineCreators.get(key);
    }

    public <T> InlineKeyboardMarkup getKeyboardInline(KeyboardKey<T> type, T data) {
        AbstractInlineKeyboardCreator<T> creator = getKeyboardCreator(type);
        return creator.create(data);
    }

    public InlineKeyboardMarkup getKeyboardInline(KeyboardKey<Void> type) {
        return getKeyboardInline(type, null);
    }
}
