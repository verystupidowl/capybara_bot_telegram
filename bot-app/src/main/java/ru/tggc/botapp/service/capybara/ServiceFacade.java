package ru.tggc.botapp.service.capybara;

import com.pengrad.telegrambot.model.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.tggc.botapp.domain.dto.FightCapybaraDto;
import ru.tggc.botapp.domain.dto.MyCapybaraDto;
import ru.tggc.botapp.domain.dto.TopCapybaraDto;
import ru.tggc.botapp.domain.dto.info.CapybaraInfoDto;
import ru.tggc.botapp.domain.model.Capybara;
import ru.tggc.botapp.domain.model.enums.ImprovementValue;
import ru.tggc.botapp.domain.model.enums.fight.BuffType;
import ru.tggc.botapp.domain.model.enums.work.WorkType;
import ru.tggc.botapp.formatter.common.CapybaraFormatter;
import ru.tggc.botapp.formatter.fight.FightFormatService;
import ru.tggc.botapp.formatter.msgkey.CommonMsgKey;
import ru.tggc.botapp.formatter.msgkey.RaceMsgKey;
import ru.tggc.botapp.formatter.msgkey.WorkMsgKey;
import ru.tggc.botapp.keyboard.KeyboardType;
import ru.tggc.telegrambotcore.dto.PhotoDto;
import ru.tggc.telegrambotcore.dto.Response;
import ru.tggc.telegrambotcore.dto.UpdateContext;
import ru.tggc.telegrambotcore.formatter.FormatService;
import ru.tggc.telegrambotcore.keyboard.KeyboardFactory;
import ru.tggc.telegrambotcore.service.HistoryService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ServiceFacade {
    private final CapybaraQueryService queryService;
    private final CapybaraWorkService workService;
    private final CapybaraProfileService profileService;
    private final CapybaraCareService careService;
    private final CapybaraRaceService raceService;
    private final CapybaraFightService fightService;
    private final KeyboardFactory keyboardFactory;
    private final FormatService formatService;
    private final CapybaraFormatter capybaraFormatter;
    private final FightFormatService fightFormatService;
    private final HistoryService historyService;

    public Response getMyCapybara(UpdateContext ctx) {
        historyService.removeFromHistory(ctx);
        MyCapybaraDto capybara = profileService.getMyCapybara(ctx);
        return ctx.edit(
                capybara.photo(),
                capybaraFormatter.getMyCapybara(capybara),
                keyboardFactory.getKeyboardInline(KeyboardType.MY_CAPYBARA, capybara)
        );
    }

    public Response getInfo(UpdateContext ctx) {
        CapybaraInfoDto info = profileService.getInfo(ctx);
        return ctx.edit(
                capybaraFormatter.getCapybaraInfo(info),
                keyboardFactory.getKeyboardInline(KeyboardType.INFO, info)
        );
    }

    public Response setDefaultPhoto(UpdateContext ctx) {
        return ctx.edit(profileService.setDefaultPhoto(ctx));
    }

    public Response makeHappy(UpdateContext ctx) {
        return ctx.edit(careService.makeHappy(ctx));
    }

    public Response feed(UpdateContext ctx) {
        return ctx.edit(careService.feed(ctx));
    }

    public Response fatten(UpdateContext ctx) {
        return ctx.edit(careService.fatten(ctx));
    }

    public Response goTea(UpdateContext ctx) {
        return ctx.sendWithDelete(careService.goTea(ctx));
    }

    public Response takeFromTea(UpdateContext ctx) {
        careService.takeFromTea(ctx);
        return ctx.edit("Ты забрал капибару с чаепития", keyboardFactory.getKeyboardInline(KeyboardType.TO_MAIN_MENU));
    }

    public Response saveCapybara(UpdateContext ctx) {
        return ctx.sendWithLoader(() -> profileService.saveCapybara(ctx), true);
    }

    public Response getWorks(UpdateContext ctx) {
        boolean hasWork = workService.hasWork(ctx);
        if (!hasWork) {
            return ctx.sendWithDelete(
                    formatService.get(WorkMsgKey.LIST_OF_WORK),
                    keyboardFactory.getKeyboardInline(KeyboardType.NEW_WORK)
            );
        } else {
            return ctx.edit(formatService.get(WorkMsgKey.ERROR_ALREADY_HAS_WORK));
        }
    }

    public Response setJob(UpdateContext ctx, WorkType workType) {
        String photoUrl = workService.setJob(ctx, workType);
        return ctx.edit(
                photoUrl,
                formatService.get(WorkMsgKey.NEW_WORK, workType.getLabel()),
                keyboardFactory.getKeyboardInline(KeyboardType.TO_MAIN_MENU)
        );
    }

    public Response goJob(UpdateContext ctx) {
        return ctx.edit(workService.goJob(ctx));
    }

    public Response takeFromWork(UpdateContext ctx) {
        return ctx.edit(workService.takeFromWork(ctx), keyboardFactory.getKeyboardInline(KeyboardType.TO_MAIN_MENU));
    }

    public Response doMassage(UpdateContext ctx) {
        raceService.doMassage(ctx);
        return ctx.edit(formatService.get(RaceMsgKey.MASSAGE));
    }

    public Response setImprovement(UpdateContext ctx, ImprovementValue improvementValue) {
        PhotoDto photoDto = raceService.setImprovement(ctx, improvementValue);
        return ctx.edit(photoDto);
    }

    public Response deleteCapybara(UpdateContext ctx) {
        profileService.deleteCapybara(ctx);
        String message = formatService.get(CommonMsgKey.DELETED);
        return ctx.send(message);
    }

    public Response getTopCapybaras(UpdateContext ctx) {
        List<TopCapybaraDto> topCapybaras = queryService.getTopCapybaras();
        PhotoDto photo = topCapybaras.getFirst().photoDto();
        String caption = topCapybaras.stream()
                .map(c -> c.name() + " - " + c.level())
                .collect(Collectors.joining("\n"));
        return ctx.send(new PhotoDto(photo.url(), caption, ctx.chatId()));
    }

    public Response dismissal(UpdateContext ctx) {
        workService.dismissal(ctx);
        return ctx.send("Твоя капибара уволилась с работы");
    }

    public Response transferMoney(UpdateContext ctx, String targetUsername, Integer amount) {
        profileService.transferMoney(ctx, targetUsername, amount);
        return ctx.send("ok");
    }

    public void save(Capybara capybara) {
        queryService.save(capybara);
    }

    public Response changeName(UpdateContext ctx, String newName) {
        profileService.changeName(ctx, newName);

        String text = formatService.get(CommonMsgKey.NAME_CHANGED, newName);
        return ctx.send(text, keyboardFactory.getKeyboardInline(KeyboardType.TO_MAIN_MENU));
    }

    public Response setPhoto(UpdateContext ctx, Message message) {
        profileService.setPhoto(ctx, message);
        return ctx.send(
                formatService.get(CommonMsgKey.PHOTO_CHANGED),
                keyboardFactory.getKeyboardInline(KeyboardType.TO_MAIN_MENU)
        ).andThen(ctx.cleanPromptAndInput());
    }

    public Response buyBuff(UpdateContext ctx, String buff, BuffType buffType) {
        fightService.buyBuff(ctx, buff, buffType);
        return ctx.edit("u bought a buff", keyboardFactory.getKeyboardInline(KeyboardType.TO_MAIN_MENU));
    }

    public Response getImprovements(UpdateContext ctx) {
        return raceService.getImprovements(ctx);
    }

    public Response getFightInfo(UpdateContext ctx) {
        FightCapybaraDto fightInfo = fightService.getFightInfo(ctx);
        return ctx.edit(
                fightFormatService.getFightInfo(fightInfo),
                keyboardFactory.getKeyboardInline(KeyboardType.FIGHT_INFO, fightInfo)
        );
    }
}
