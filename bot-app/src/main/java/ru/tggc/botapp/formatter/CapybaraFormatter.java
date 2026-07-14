package ru.tggc.botapp.formatter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.tggc.botapp.domain.dto.MyCapybaraDto;
import ru.tggc.botapp.formatter.msgkey.CommonMsgKey;
import ru.tggc.telegrambotcore.formatter.FormatService;

import java.util.function.Function;

import static ru.tggc.telegrambotcore.util.Utils.getOrElse;
import static ru.tggc.telegrambotcore.util.Utils.renderStaminaBar;

@Component
@RequiredArgsConstructor
@Slf4j
public class CapybaraFormatter {
    private final FormatService formatService;

    public String getMyCapybara(MyCapybaraDto capybara) {
        Object[] params = {
                capybara.name(),
                capybara.level().toString(),
                capybara.type(),
                renderStaminaBar(capybara.stamina()),
                capybara.job(),
                String.valueOf(capybara.currency()),
                getOrElse(capybara.wedding(), Function.identity(), "Нет"),
                capybara.satietyLevel() + "/" + capybara.satietyMaxLevel(),
                capybara.happinessLevel() + "/" + capybara.happinessMaxLevel()
        };
        return formatService.getMessage(CommonMsgKey.MY_CAPYBARA, params);
    }
}
