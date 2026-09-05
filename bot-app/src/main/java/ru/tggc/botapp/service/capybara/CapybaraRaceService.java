package ru.tggc.botapp.service.capybara;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.tggc.botapp.domain.model.Capybara;
import ru.tggc.botapp.domain.model.Improvement;
import ru.tggc.botapp.domain.model.enums.ImprovementValue;
import ru.tggc.botapp.exceptions.CapybaraException;
import ru.tggc.botapp.formatter.msgkey.CommonMsgKey;
import ru.tggc.botapp.formatter.msgkey.ErrorMsgKey;
import ru.tggc.botapp.formatter.msgkey.RaceMsgKey;
import ru.tggc.botapp.keyboard.KeyboardType;
import ru.tggc.botapp.service.PhotoService;
import ru.tggc.telegrambotcore.dto.PhotoDto;
import ru.tggc.telegrambotcore.dto.Response;
import ru.tggc.telegrambotcore.dto.UpdateContext;
import ru.tggc.telegrambotcore.formatter.FormatService;
import ru.tggc.telegrambotcore.keyboard.KeyboardFactory;

import static ru.tggc.telegrambotcore.util.Utils.throwIf;

@Slf4j
@Service
@RequiredArgsConstructor
public class CapybaraRaceService {
    private final CapybaraQueryService queryService;
    private final FormatService formatService;
    private final PhotoService photoService;
    private final KeyboardFactory keyboardFactory;

    @Transactional
    public PhotoDto setImprovement(UpdateContext ctx, ImprovementValue improvementValue) {
        Capybara capybara = queryService.getCapybaraByContext(ctx);

        Improvement improvement = capybara.getImprovement();
        throwIf(improvement.getImprovementValue() != ImprovementValue.NONE, () -> {
            String message = formatService.get(ErrorMsgKey.CAPYBARA_ALREADY_HAS_IMPROVEMENT);
            return new CapybaraException(message);
        });
        capybara.decreaseMoney(improvementValue.getCost());

        improvement.setImprovementValue(improvementValue);
        capybara.setImprovement(improvement);
        queryService.save(capybara);

        return PhotoDto.builder()
                .chatId(ctx.chatId())
                .caption(formatService.get(RaceMsgKey.getByImprovement(improvementValue)))
                .url(photoService.getImprovementPhoto(improvementValue))
                .markup(keyboardFactory.getKeyboardInline(KeyboardType.TO_MAIN_MENU))
                .build();
    }

    public Response getImprovements(UpdateContext ctx) {
        Capybara capybara = queryService.getRaceCapybara(ctx);
        if (capybara.getImprovement().getImprovementValue() == ImprovementValue.NONE) {
            String message = formatService.get(CommonMsgKey.LIST_OF_IMPROVEMENTS);
            return ctx.edit(message, keyboardFactory.getKeyboardInline(KeyboardType.IMPROVEMENTS));
        }
        return ctx.send(formatService.get(ErrorMsgKey.CAPYBARA_ALREADY_HAS_IMPROVEMENT));
    }

    public void doMassage(UpdateContext ctx) {
        Capybara capybara = queryService.getRaceCapybara(ctx);
        capybara.decreaseMoney(50);
        capybara.getRace().getRaceAction().setCharges(capybara.getRace().getRaceAction().getMaxCharges());

        queryService.save(capybara);
    }
}
