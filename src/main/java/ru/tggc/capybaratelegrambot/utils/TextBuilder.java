package ru.tggc.capybaratelegrambot.utils;

import lombok.experimental.UtilityClass;
import ru.tggc.capybaratelegrambot.domain.dto.MyCapybaraDto;

import java.util.Map;
import java.util.function.Function;

import static ru.tggc.capybaratelegrambot.utils.Utils.getOr;
import static ru.tggc.capybaratelegrambot.utils.Utils.getText;
import static ru.tggc.capybaratelegrambot.utils.Utils.renderStaminaBar;

@UtilityClass
public class TextBuilder {

    public String getMyCapybara(MyCapybaraDto capybara) {
        Map<String, String> params = Map.of(
                "name", capybara.name(),
                "level", capybara.level().toString(),
                "type", capybara.type(),
                "stamina", renderStaminaBar(capybara.stamina()),
                "work", capybara.job(),
                "currency", String.valueOf(capybara.currency()),
                "wedding", getOr(capybara.wedding(), Function.identity(), "Нет"),
                "satiety", capybara.satietyLevel() + "/" + capybara.satietyMaxLevel(),
                "happiness", capybara.happinessLevel() + "/" + capybara.happinessMaxLevel()
        );
        return getText(Text.MY_CAPYBARA, params);
    }
}
