package ru.tggc.botapp.service.capybara;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.tggc.botapp.domain.dto.CapybaraTeaDto;
import ru.tggc.botapp.domain.dto.HappinessThingDto;
import ru.tggc.botapp.domain.dto.StatKey;
import ru.tggc.botapp.domain.model.Capybara;
import ru.tggc.botapp.domain.model.timedaction.Happiness;
import ru.tggc.botapp.domain.model.timedaction.Satiety;
import ru.tggc.botapp.domain.model.timedaction.Tea;
import ru.tggc.botapp.exceptions.CapybaraException;
import ru.tggc.botapp.formatter.msgkey.CommonMsgKey;
import ru.tggc.botapp.formatter.msgkey.ErrorMsgKey;
import ru.tggc.botapp.keyboard.KeyboardType;
import ru.tggc.botapp.mapper.CapybaraTeaMapper;
import ru.tggc.botapp.repository.TeaRepository;
import ru.tggc.botapp.service.PhotoService;
import ru.tggc.botapp.service.TimedActionService;
import ru.tggc.botapp.service.stats.CapybaraStatsService;
import ru.tggc.telegrambotcore.dto.PhotoDto;
import ru.tggc.telegrambotcore.dto.UpdateContext;
import ru.tggc.telegrambotcore.formatter.FormatService;
import ru.tggc.telegrambotcore.keyboard.KeyboardFactory;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static ru.tggc.telegrambotcore.util.Utils.getOrElse;
import static ru.tggc.telegrambotcore.util.Utils.throwIf;

@Slf4j
@Service
@RequiredArgsConstructor
public class CapybaraCareService {
    @Value("${bot.photos.tea.go-tea}")
    private String teaPhoto;
    @Value("${bot.photos.feed}")
    private String feedPhoto;
    @Value("${bot.photos.fatten}")
    private String fattenPhoto;

    private final CapybaraQueryService queryService;
    private final TimedActionService timedActionService;
    private final FormatService formatService;
    private final TeaRepository teaRepository;
    private final CapybaraTeaMapper mapper;
    private final KeyboardFactory keyboardFactory;
    private final CapybaraStatsService statsService;
    private final PhotoService photoService;

    @Transactional
    public void takeFromTea(UpdateContext ctx) {
        Capybara capybara = queryService.getTeaCapybara(ctx);
        capybara.getTea().setWaiting(false);
        queryService.save(capybara);
    }

    @Transactional
    public List<PhotoDto> goTea(UpdateContext ctx) {
        List<PhotoDto> photosToReturn = new ArrayList<>();
        Capybara capybara = queryService.getTeaCapybara(ctx);
        Tea tea = capybara.getTea();

        throwIf(!tea.canPerform(), () -> {
            String status = timedActionService.getStatus(tea);
            String message = formatService.get(ErrorMsgKey.CAPYBARA_TEA_COOLDOWN, status);
            return new CapybaraException(message);
        });

        throwIf(tea.isWaiting(), () -> new CapybaraException(formatService.get(ErrorMsgKey.CAPYBARA_TEA_ALREADY_WAITING)));

        List<Tea> byIsWaiting = teaRepository.findByIsWaiting(true);
        if (!byIsWaiting.isEmpty()) {
            Tea incerlocutorTea = byIsWaiting.getFirst();
            Capybara interlocutor = incerlocutorTea.getCapybara();
            CapybaraTeaDto myDto = mapper.toDto(capybara);
            CapybaraTeaDto interlocutorDto = mapper.toDto(interlocutor);

            queryService.updateTeas(tea, incerlocutorTea);

            statsService.modify(capybara, StatKey.HAPPINESS, 10);
            statsService.modify(interlocutor, StatKey.HAPPINESS, 10);

            queryService.save(interlocutor);
            queryService.save(capybara);

            if (ctx.chatId() != interlocutor.getChat().getId()) {
                String text1 = formatService.get(CommonMsgKey.DO_TEA, myDto.name(), interlocutorDto.name());
                String text2 = formatService.get(CommonMsgKey.DO_TEA, interlocutorDto.name(), myDto.name());

                String url1 = getOrElse(capybara.getPhoto().getFileId(), Function.identity(), capybara.getPhoto().getUrl());
                String url2 = getOrElse(interlocutor.getPhoto().getFileId(), Function.identity(), interlocutor.getPhoto().getUrl());

                photosToReturn.add(new PhotoDto(
                        url1,
                        text2,
                        interlocutor.getChat().getId(),
                        keyboardFactory.getKeyboardInline(KeyboardType.TO_MAIN_MENU)
                ));

                photosToReturn.add(new PhotoDto(
                        url2,
                        text1,
                        ctx.chatId(),
                        keyboardFactory.getKeyboardInline(KeyboardType.TO_MAIN_MENU)
                ));
            } else {
                String text = formatService.get(CommonMsgKey.DO_TEA_IN_CHAT, myDto.name(), interlocutorDto.name());
                photosToReturn.add(new PhotoDto(
                        photoService.getRandomGoTeaPhoto(),
                        text,
                        ctx.chatId(),
                        keyboardFactory.getKeyboardInline(KeyboardType.TO_MAIN_MENU)
                ));
            }

            return photosToReturn;
        }
        tea.setWaiting(true);
        queryService.save(capybara);
        PhotoDto photo = new PhotoDto(
                teaPhoto,
                formatService.get(CommonMsgKey.TEA_WAITING),
                ctx.chatId(),
                keyboardFactory.getKeyboardInline(KeyboardType.TEA)
        );

        return List.of(photo);
    }

    @Transactional
    public PhotoDto fatten(UpdateContext ctx) {
        Capybara capybara = queryService.getSatietyAndHappinessCapybara(ctx);
        capybara.decreaseMoney(50);

        feed(capybara, 50);

        String caption = formatService.get(CommonMsgKey.FATTEN);
        return new PhotoDto(
                fattenPhoto,
                caption,
                ctx.chatId(),
                keyboardFactory.getKeyboardInline(KeyboardType.TO_MAIN_MENU)
        );
    }

    @Transactional
    public PhotoDto feed(UpdateContext ctx) {
        Capybara capybara = queryService.getSatietyAndHappinessCapybara(ctx);

        feed(capybara, 5);
        String caption = formatService.get(CommonMsgKey.FEED);
        return new PhotoDto(
                feedPhoto,
                caption,
                ctx.chatId(),
                keyboardFactory.getKeyboardInline(KeyboardType.TO_MAIN_MENU)
        );
    }

    @Transactional
    public PhotoDto makeHappy(UpdateContext ctx) {
        Capybara capybara = queryService.getSatietyAndHappinessCapybara(ctx);
        Happiness happiness = capybara.getHappiness();

        throwIf(!happiness.canPerform(), () -> {
            String status = timedActionService.getStatus(happiness);
            String message = formatService.get(CommonMsgKey.HAPPINESS_COOLDOWN, status);
            return new CapybaraException(message);
        });

        HappinessThingDto happinessThing = formatService.randomObject(CommonMsgKey.HAPPINESS_THINGS, HappinessThingDto.class);
        statsService.modify(capybara, StatKey.HAPPINESS, happinessThing.level());

        happiness.setLastHappy(LocalDateTime.now());
        PhotoDto photo = new PhotoDto(
                happinessThing.photoUrl(),
                happinessThing.title(),
                ctx.chatId(),
                keyboardFactory.getKeyboardInline(KeyboardType.TO_MAIN_MENU)
        );

        queryService.save(capybara);
        return photo;
    }

    @Transactional
    public void feed(Capybara capybara, int feed) {
        Satiety satiety = capybara.getSatiety();

        throwIf(!satiety.canPerform(), () -> {
            String status = timedActionService.getStatus(satiety);
            String message = formatService.get(ErrorMsgKey.CAPYBARA_FEED_COOLDOWN, status);
            return new CapybaraException(message);
        });

        statsService.modify(capybara, StatKey.SATIETY, feed);
        satiety.setLastFed(LocalDateTime.now());

        queryService.save(capybara);
    }
}
