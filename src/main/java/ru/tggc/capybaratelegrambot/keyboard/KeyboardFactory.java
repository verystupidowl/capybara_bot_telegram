package ru.tggc.capybaratelegrambot.keyboard;

import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class KeyboardFactory {
    private final Map<KeyboardType, AbstractInlineKeyboardCreator<?>> keyboardInlineCreators;

    public KeyboardFactory(List<AbstractInlineKeyboardCreator<?>> keyboardInlineCreators) {
        this.keyboardInlineCreators = keyboardInlineCreators.stream()
                .collect(Collectors.toMap(AbstractInlineKeyboardCreator::getKeyboardType, Function.identity()));
    }

    public AbstractInlineKeyboardCreator<?> getKeyboardCreator(KeyboardType key) {
        return keyboardInlineCreators.get(key);
    }

    @SuppressWarnings("unchecked")
    public <T> InlineKeyboardMarkup getKeyboardInline(KeyboardType type, T data) {
        AbstractInlineKeyboardCreator<T> creator = (AbstractInlineKeyboardCreator<T>) getKeyboardCreator(type);
        return creator.create(data);
    }

    public InlineKeyboardMarkup getKeyboardInline(KeyboardType type) {
        return getKeyboardInline(type, null);
    }
}
