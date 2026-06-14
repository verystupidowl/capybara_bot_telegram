package ru.tggc.botapp.util;

import lombok.experimental.UtilityClass;
import ru.tggc.botapp.domain.dto.MyCapybaraDto;

import java.util.Map;
import java.util.function.Function;

import static ru.tggc.telegrambotframework.util.Utils.getOrElse;
import static ru.tggc.telegrambotframework.util.Utils.getText;
import static ru.tggc.telegrambotframework.util.Utils.renderStaminaBar;

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
                "wedding", getOrElse(capybara.wedding(), Function.identity(), "Нет"),
                "satiety", capybara.satietyLevel() + "/" + capybara.satietyMaxLevel(),
                "happiness", capybara.happinessLevel() + "/" + capybara.happinessMaxLevel()
        );
        return getText(Text.MY_CAPYBARA, params);
    }
}
