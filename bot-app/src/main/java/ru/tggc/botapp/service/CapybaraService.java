package ru.tggc.botapp.service;

import com.pengrad.telegrambot.model.Animation;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.PhotoSize;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.tggc.botapp.domain.dto.CapybaraTeaDto;
import ru.tggc.botapp.domain.dto.FightCapybaraDto;
import ru.tggc.botapp.domain.dto.HappinessThingDto;
import ru.tggc.botapp.domain.dto.MyCapybaraDto;
import ru.tggc.botapp.domain.dto.StatKey;
import ru.tggc.botapp.domain.dto.TopCapybaraDto;
import ru.tggc.botapp.domain.dto.info.CapybaraInfoDto;
import ru.tggc.botapp.domain.model.Capybara;
import ru.tggc.botapp.domain.model.Chat;
import ru.tggc.botapp.domain.model.Improvement;
import ru.tggc.botapp.domain.model.Photo;
import ru.tggc.botapp.domain.model.User;
import ru.tggc.botapp.domain.model.Work;
import ru.tggc.botapp.domain.model.enums.ImprovementValue;
import ru.tggc.botapp.domain.model.enums.WorkType;
import ru.tggc.botapp.domain.model.enums.fight.BuffType;
import ru.tggc.botapp.domain.model.enums.fight.FightBuffHeal;
import ru.tggc.botapp.domain.model.enums.fight.FightBuffShield;
import ru.tggc.botapp.domain.model.enums.fight.FightBuffSpecial;
import ru.tggc.botapp.domain.model.enums.fight.FightBuffWeapon;
import ru.tggc.botapp.domain.model.timedaction.Happiness;
import ru.tggc.botapp.domain.model.timedaction.Satiety;
import ru.tggc.botapp.domain.model.timedaction.Tea;
import ru.tggc.botapp.exceptions.CapybaraAlreadyExistsException;
import ru.tggc.botapp.exceptions.CapybaraException;
import ru.tggc.botapp.exceptions.CapybaraNotFoundException;
import ru.tggc.botapp.formatter.msgkey.CommonMsgKey;
import ru.tggc.botapp.formatter.msgkey.ErrorMsgKey;
import ru.tggc.botapp.formatter.msgkey.RaceMsgKey;
import ru.tggc.botapp.formatter.msgkey.WorkMsgKey;
import ru.tggc.botapp.keyboard.KeyboardType;
import ru.tggc.botapp.mapper.CapybaraInfoMapper;
import ru.tggc.botapp.mapper.CapybaraTeaMapper;
import ru.tggc.botapp.mapper.FightCapybaraMapper;
import ru.tggc.botapp.mapper.MyCapybaraMapper;
import ru.tggc.botapp.repository.CapybaraRepository;
import ru.tggc.botapp.repository.ChatRepository;
import ru.tggc.botapp.repository.TeaRepository;
import ru.tggc.botapp.service.factory.WorkServiceFactory;
import ru.tggc.botapp.service.impl.UserServiceImpl;
import ru.tggc.botapp.service.stats.CapybaraStatsService;
import ru.tggc.botapp.util.CapybaraBuilder;
import ru.tggc.telegrambotcore.dto.FileType;
import ru.tggc.telegrambotcore.dto.PhotoDto;
import ru.tggc.telegrambotcore.dto.UpdateContext;
import ru.tggc.telegrambotcore.ext.TelegramMessageUtils;
import ru.tggc.telegrambotcore.formatter.FormatService;
import ru.tggc.telegrambotcore.keyboard.KeyboardFactory;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

import static ru.tggc.telegrambotcore.util.Utils.getOrElse;
import static ru.tggc.telegrambotcore.util.Utils.throwIf;

@Service
@RequiredArgsConstructor
public class CapybaraService {
    @Value("${bot.photos.feed}")
    private String feedPhoto;
    @Value("${bot.photos.fatten}")
    private String fattenPhoto;
    @Value("${bot.photos.tea.go-tea}")
    private String teaPhoto;

    private final CapybaraRepository capybaraRepository;
    private final UserServiceImpl userService;
    private final TeaRepository teaRepository;
    private final CapybaraTeaMapper capybaraTeaMapper;
    private final WorkServiceFactory workServiceFactory;
    private final TimedActionService timedActionService;
    private final MyCapybaraMapper myCapybaraMapper;
    private final CapybaraInfoMapper capybaraInfoMapper;
    private final KeyboardFactory keyboardFactory;
    private final ChatRepository chatRepository;
    private final FightCapybaraMapper fightCapybaraMapper;
    private final FormatService formatService;
    private final PhotoService photoService;
    private final CapybaraStatsService statsService;

    @Setter(onMethod_ = {@Autowired, @Lazy})
    private CapybaraService self;

    @Transactional(readOnly = true)
    public Optional<Capybara> findCapybara(UpdateContext ctx) {
        return capybaraRepository.findMyCapybaraByUserIdAndChatId(ctx.userId(), ctx.chatId());
    }

    public boolean existsCapybara(UpdateContext ctx) {
        return capybaraRepository.existsCapybaraByUserIdAndChatId(ctx.userId(), ctx.chatId());
    }

    @Transactional(readOnly = true)
    public Capybara getCapybara(Long id) {
        return capybaraRepository.findById(id)
                .orElseThrow(CapybaraNotFoundException::new);
    }

    public Capybara getCapybaraByUserId(long userId, long chatId) {
        return self.getCapybara(userId, chatId);
    }

    public Capybara getCapybaraByContext(UpdateContext ctx) {
        return getCapybaraByUserId(ctx.userId(), ctx.chatId());
    }

    public Capybara getCapybaraByContext(UpdateContext ctx, Supplier<RuntimeException> supplier) {
        return capybaraRepository.findMyCapybaraByUserIdAndChatId(ctx.userId(), ctx.chatId())
                .orElseThrow(supplier);
    }

    @Transactional(readOnly = true)
    public MyCapybaraDto getMyCapybara(UpdateContext ctx) {
        Capybara capybara = capybaraRepository.findMyCapybaraByUserIdAndChatId(ctx.userId(), ctx.chatId())
                .orElseThrow(CapybaraNotFoundException::new);
        return myCapybaraMapper.toDto(capybara);
    }

    @Transactional(readOnly = true)
    public CapybaraInfoDto getInfo(UpdateContext ctx) {
        Capybara capybara = capybaraRepository.findInfoCapybaraByUserIdAndChatId(ctx.userId(), ctx.chatId())
                .orElseThrow(CapybaraNotFoundException::new);
        return capybaraInfoMapper.toDto(capybara);
    }

    @Transactional
    public String setDefaultPhoto(UpdateContext ctx) {
        Capybara capybara = getCapybaraByContext(ctx);
        Photo photo = photoService.getRandomDefaultPhoto();

        capybara.decreaseMoney(25);
        capybara.setPhoto(photo);

        capybaraRepository.save(capybara);
        return formatService.get(CommonMsgKey.CHOSEN_RANDOM_PHOTO);
    }

    @Transactional
    public PhotoDto makeHappy(UpdateContext ctx) {
        Capybara capybara = capybaraRepository.findSatietyAndHappinessCapybaraByUserIdAndChatId(ctx.userId(), ctx.chatId())
                .orElseThrow(CapybaraNotFoundException::new);
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

        capybaraRepository.save(capybara);
        return photo;
    }

    @Transactional
    public PhotoDto feed(UpdateContext ctx) {
        Capybara capybara = capybaraRepository.findSatietyAndHappinessCapybaraByUserIdAndChatId(ctx.userId(), ctx.chatId())
                .orElseThrow(CapybaraNotFoundException::new);
        self.feed(capybara, 5);
        String caption = formatService.get(CommonMsgKey.FEED);
        return new PhotoDto(
                feedPhoto,
                caption,
                ctx.chatId(),
                keyboardFactory.getKeyboardInline(KeyboardType.TO_MAIN_MENU)
        );
    }

    @Transactional
    public PhotoDto fatten(UpdateContext ctx) {
        Capybara capybara = capybaraRepository.findSatietyAndHappinessCapybaraByUserIdAndChatId(ctx.userId(), ctx.chatId())
                .orElseThrow(CapybaraNotFoundException::new);
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
    public List<PhotoDto> goTea(UpdateContext ctx) {
        List<PhotoDto> photosToReturn = new ArrayList<>();
        Capybara capybara = capybaraRepository.findTeaCapybaraByUserIdAndChatId(ctx.userId(), ctx.chatId())
                .orElseThrow(CapybaraNotFoundException::new);
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
            CapybaraTeaDto myDto = capybaraTeaMapper.toDto(capybara);
            CapybaraTeaDto interlocutorDto = capybaraTeaMapper.toDto(interlocutor);

            self.updateTea(tea);
            self.updateTea(incerlocutorTea);

            statsService.modify(capybara, StatKey.HAPPINESS, 10);
            statsService.modify(interlocutor, StatKey.HAPPINESS, 10);

            capybaraRepository.save(interlocutor);
            capybaraRepository.save(capybara);

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

            System.out.println("PHOTOS" + photosToReturn);
            return photosToReturn;
        }
        tea.setWaiting(true);
        capybaraRepository.save(capybara);
        PhotoDto photo = new PhotoDto(
                teaPhoto,
                formatService.get(CommonMsgKey.TEA_WAITING),
                ctx.chatId(),
                keyboardFactory.getKeyboardInline(KeyboardType.TEA)
        );

        return List.of(photo);
    }

    public void takeFromTea(UpdateContext ctx) {
        Capybara capybara = capybaraRepository.findTeaCapybaraByUserIdAndChatId(ctx.userId(), ctx.chatId())
                .orElseThrow(CapybaraNotFoundException::new);
        capybara.getTea().setWaiting(false);
        capybaraRepository.save(capybara);
    }

    @Transactional
    public PhotoDto saveCapybara(UpdateContext ctx) {
        long chatId = ctx.chatId();
        long userId = ctx.userId();
        Boolean capybaraExists = capybaraRepository.existsCapybaraByUserIdAndChatId(userId, chatId);
        throwIf(capybaraExists, CapybaraAlreadyExistsException::new);

        User user = userService.getUserById(userId);
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(IllegalArgumentException::new);

        int size = capybaraRepository.countByChatId(chatId);
        Photo photo = photoService.getRandomDefaultPhoto();
        Capybara capybara = CapybaraBuilder.buildCapybara(size, chat, user, photo);
        capybaraRepository.save(capybara);
        String caption = formatService.get(CommonMsgKey.CAPYBARA_CREATED, capybara.getName());
        return new PhotoDto(
                capybara.getPhoto().getUrl(),
                caption,
                chatId,
                keyboardFactory.getKeyboardInline(KeyboardType.TO_MAIN_MENU)
        );
    }

    public boolean hasWork(UpdateContext ctx) {
        return capybaraRepository.findByUserIdAndChatId(ctx.userId(), ctx.chatId())
                .map(Capybara::getWork)
                .map(Work::getWorkType)
                .map(jt -> jt != WorkType.NONE)
                .orElse(false);
    }

    public String setJob(UpdateContext ctx, WorkType workType) {
        Capybara capybara = getCapybaraByContext(ctx);

        WorkService workService = workServiceFactory.getJobProvider(workType);
        String photoUrl = workService.setWork(capybara);
        capybaraRepository.save(capybara);
        return photoUrl;
    }

    public PhotoDto goJob(UpdateContext ctx) {
        Capybara capybara = getCapybaraByContext(ctx);
        WorkType workType = capybara.getWork().getWorkType();
        WorkService workService = workServiceFactory.getJobProvider(workType);
        workService.goWork(capybara);
        capybaraRepository.save(capybara);
        String photoUrl = photoService.getGoWorkPhoto(workType);
        return PhotoDto.builder()
                .url(photoUrl)
                .chatId(ctx.chatId())
                .caption(formatService.get(WorkMsgKey.GO_WORK))
                .markup(keyboardFactory.getKeyboardInline(KeyboardType.TO_MAIN_MENU))
                .build();
    }

    public String takeFromWork(UpdateContext ctx) {
        Capybara capybara = getCapybaraByContext(ctx);
        WorkService workService = workServiceFactory.getJobProvider(capybara.getWork().getWorkType());
        String messages = workService.takeFromWork(capybara);
        capybaraRepository.save(capybara);
        return messages;
    }

    public void doMassage(UpdateContext ctx) {
        Capybara capybara = self.getRaceCapybara(ctx);
        capybara.decreaseMoney(50);
        capybara.getRace().getRaceAction().setCharges(capybara.getRace().getRaceAction().getMaxCharges());

        capybaraRepository.save(capybara);
    }

    @Transactional
    public PhotoDto setImprovement(UpdateContext ctx, ImprovementValue improvementValue) {
        Capybara capybara = getCapybaraByContext(ctx);

        Improvement improvement = capybara.getImprovement();
        throwIf(improvement.getImprovementValue() != ImprovementValue.NONE, () -> {
            String message = formatService.get(ErrorMsgKey.CAPYBARA_ALREADY_HAS_IMPROVEMENT);
            return new CapybaraException(message);
        });
        capybara.decreaseMoney(improvementValue.getCost());

        improvement.setImprovementValue(improvementValue);
        capybara.setImprovement(improvement);
        capybaraRepository.save(capybara);

        return PhotoDto.builder()
                .chatId(ctx.chatId())
                .caption(formatService.get(RaceMsgKey.getByImprovement(improvementValue)))
                .url(photoService.getImprovementPhoto(improvementValue))
                .markup(keyboardFactory.getKeyboardInline(KeyboardType.TO_MAIN_MENU))
                .build();
    }

    public Capybara getCapybaraById(Long id) {
        return capybaraRepository.findById(id)
                .orElseThrow(CapybaraNotFoundException::new);
    }

    @Transactional
    public void deleteCapybara(UpdateContext ctx) {
        Capybara capybara = getCapybaraByContext(ctx);
        capybaraRepository.delete(capybara);
    }

    public List<TopCapybaraDto> getTopCapybaras() {
        return capybaraRepository.findTop10ByOrderByLevelValueDesc().stream()
                .map(c -> {
                    PhotoDto photo = new PhotoDto(c.getPhoto().getUrl());
                    return new TopCapybaraDto(c.getName(), photo, c.getLevel().getValue());
                })
                .toList();
    }

    public void dismissal(UpdateContext ctx) {
        Capybara capybara = getCapybaraByContext(ctx);
        WorkService workService = workServiceFactory.getJobProvider(capybara.getWork().getWorkType());
        workService.dismissal(capybara);
        capybaraRepository.save(capybara);
    }

    @Transactional
    public void transferMoney(UpdateContext ctx, String targetUsername, Integer amount) {
        Capybara sourcecapybara = getCapybaraByContext(ctx);

        User user = userService.getUserByUsername(targetUsername);
        Capybara targetCapybara = self.getCapybara(user.getId());

        sourcecapybara.decreaseMoney(amount);
        targetCapybara.increaseMoney(amount);

        capybaraRepository.saveAll(List.of(sourcecapybara, targetCapybara));
    }

    public void save(Capybara capybara) {
        capybaraRepository.save(capybara);
    }

    @Transactional(readOnly = true)
    public Capybara getCapybara(long userId, long chatId) {
        return capybaraRepository.findMyCapybaraByUserIdAndChatId(userId, chatId)
                .orElseThrow(CapybaraNotFoundException::new);
    }

    @Transactional
    public void feed(Capybara capybara, Integer feed) {
        Satiety satiety = capybara.getSatiety();

        throwIf(!satiety.canPerform(), () -> {
            String status = timedActionService.getStatus(satiety);
            String message = formatService.get(ErrorMsgKey.CAPYBARA_FEED_COOLDOWN, status);
            return new CapybaraException(message);
        });

        statsService.modify(capybara, StatKey.SATIETY, feed);
        satiety.setLastFed(LocalDateTime.now());

        capybaraRepository.save(capybara);
    }

    @Transactional
    public void updateTea(Tea tea) {
        tea.setWaiting(false);
        tea.setLastTea(LocalDateTime.now());
    }

    @Transactional
    public void changeName(UpdateContext historyDto, String newName) {
        if (newName.length() > 25 || newName.isEmpty()) {
            throw new CapybaraException(formatService.get(ErrorMsgKey.CAPYBARA_NAME_TOO_LONG));
        }
        Capybara capybara = getCapybaraByContext(historyDto);
        capybara.setName(newName);
        capybaraRepository.save(capybara);
    }

    @Transactional(readOnly = true)
    public Capybara getRaceCapybara(UpdateContext ctx) {
        return capybaraRepository.findRaceCapybaraByUserIdAndChatId(ctx.userId(), ctx.chatId())
                .orElseThrow(CapybaraNotFoundException::new);
    }

    @Transactional
    public void setPhoto(UpdateContext ctx, Message message) {
        Capybara capybara = getCapybaraByContext(ctx);
        capybara.decreaseMoney(50);
        if (TelegramMessageUtils.hasPhoto(message)) {
            PhotoSize photoSize = message.photo()[0];
            Photo photo = capybara.getPhoto();
            photo.setFileId(photoSize.fileId());
            photo.setFileSize(photoSize.fileSize());
            photo.setFileUniqueId(photo.getFileUniqueId());
            photo.setType(FileType.PHOTO);
        } else if (message.animation() != null) {
            Animation animation = message.animation();
            Photo photo = capybara.getPhoto();
            photo.setFileId(animation.fileId());
            photo.setFileSize(animation.fileSize());
            photo.setFileUniqueId(photo.getFileUniqueId());
            photo.setType(FileType.DOC);
        }
        capybaraRepository.save(capybara);
    }

    public Capybara getFightCapybara(Long chatId, Long userId) {
        return capybaraRepository.findFightCapybaraByChatIdAndUserId(chatId, userId)
                .orElseThrow(CapybaraNotFoundException::new);
    }

    public FightCapybaraDto getFightInfo(UpdateContext ctx) {
        Capybara fightCapybara = getFightCapybara(ctx.chatId(), ctx.userId());
        return fightCapybaraMapper.toDto(fightCapybara.getFight(), ctx.chatId());
    }

    public void buyBuff(UpdateContext ctx, String buff, BuffType buffType) {
        Capybara fightCapybara = getFightCapybara(ctx.chatId(), ctx.userId());
        switch (buffType) {
            case ATTACK -> {
                throwIf(
                        fightCapybara.getFight().getWeapon() != FightBuffWeapon.NONE,
                        () -> new CapybaraException(formatService.get(ErrorMsgKey.CAPYBARA_FIGHT_ONLY_ONE, "оружия"))
                );
                buyWeapon(fightCapybara, FightBuffWeapon.valueOf(buff));
            }
            case DEFEND -> {
                throwIf(
                        fightCapybara.getFight().getShield() != FightBuffShield.NONE,
                        () -> new CapybaraException(formatService.get(ErrorMsgKey.CAPYBARA_FIGHT_ONLY_ONE, "щита"))
                );
                buyShield(fightCapybara, FightBuffShield.valueOf(buff));
            }
            case HEAL -> {
                throwIf(
                        fightCapybara.getFight().getHeal() != FightBuffHeal.NONE,
                        () -> new CapybaraException(formatService.get(ErrorMsgKey.CAPYBARA_FIGHT_ONLY_ONE, "лечения"))
                );
                buyHeal(fightCapybara, FightBuffHeal.valueOf(buff));
            }
            case SPECIAL -> {
                throwIf(
                        fightCapybara.getFight().getSpecial() != FightBuffSpecial.NONE,
                        () -> new CapybaraException(formatService.get(ErrorMsgKey.CAPYBARA_FIGHT_ONLY_ONE, "спец. оружия"))
                );
                buySpecial(fightCapybara, FightBuffSpecial.valueOf(buff));
            }
        }
        capybaraRepository.save(fightCapybara);
    }

    private void buySpecial(Capybara fightCapybara, FightBuffSpecial fightBuffSpecial) {
        fightCapybara.decreaseMoney(fightBuffSpecial.getCost());
        fightCapybara.getFight().setSpecial(fightBuffSpecial);
    }

    private void buyHeal(Capybara fightCapybara, FightBuffHeal fightBuffHeal) {
        fightCapybara.decreaseMoney(fightBuffHeal.getCost());
        fightCapybara.getFight().setHeal(fightBuffHeal);
    }

    private void buyShield(Capybara fightCapybara, FightBuffShield fightBuffShield) {
        fightCapybara.decreaseMoney(fightBuffShield.getCost());
        fightCapybara.getFight().setShield(fightBuffShield);
    }

    private void buyWeapon(Capybara fightCapybara, FightBuffWeapon fightBuffWeapon) {
        fightCapybara.decreaseMoney(fightBuffWeapon.getCost());
        fightCapybara.getFight().setWeapon(fightBuffWeapon);
    }
}
