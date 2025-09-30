package ru.tggc.capybaratelegrambot.service;

import com.pengrad.telegrambot.model.Animation;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.PhotoSize;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.tggc.capybaratelegrambot.domain.dto.CapybaraContext;
import ru.tggc.capybaratelegrambot.domain.dto.CapybaraInfoDto;
import ru.tggc.capybaratelegrambot.domain.dto.CapybaraTeaDto;
import ru.tggc.capybaratelegrambot.domain.dto.MyCapybaraDto;
import ru.tggc.capybaratelegrambot.domain.dto.PhotoDto;
import ru.tggc.capybaratelegrambot.domain.dto.TopCapybaraDto;
import ru.tggc.capybaratelegrambot.domain.dto.enums.FileType;
import ru.tggc.capybaratelegrambot.domain.dto.enums.HappinessThings;
import ru.tggc.capybaratelegrambot.domain.model.Capybara;
import ru.tggc.capybaratelegrambot.domain.model.Chat;
import ru.tggc.capybaratelegrambot.domain.model.Improvement;
import ru.tggc.capybaratelegrambot.domain.model.Level;
import ru.tggc.capybaratelegrambot.domain.model.Photo;
import ru.tggc.capybaratelegrambot.domain.model.User;
import ru.tggc.capybaratelegrambot.domain.model.Work;
import ru.tggc.capybaratelegrambot.domain.model.enums.ImprovementValue;
import ru.tggc.capybaratelegrambot.domain.model.enums.Type;
import ru.tggc.capybaratelegrambot.domain.model.enums.WorkType;
import ru.tggc.capybaratelegrambot.domain.model.timedaction.Happiness;
import ru.tggc.capybaratelegrambot.domain.model.timedaction.Satiety;
import ru.tggc.capybaratelegrambot.domain.model.timedaction.Tea;
import ru.tggc.capybaratelegrambot.exceptions.CapybaraAlreadyExistsException;
import ru.tggc.capybaratelegrambot.exceptions.CapybaraException;
import ru.tggc.capybaratelegrambot.exceptions.CapybaraHasNoMoneyException;
import ru.tggc.capybaratelegrambot.exceptions.CapybaraNotFoundException;
import ru.tggc.capybaratelegrambot.keyboard.InlineKeyboardCreator;
import ru.tggc.capybaratelegrambot.mapper.CapybaraInfoMapper;
import ru.tggc.capybaratelegrambot.mapper.CapybaraTeaMapper;
import ru.tggc.capybaratelegrambot.mapper.MyCapybaraMapper;
import ru.tggc.capybaratelegrambot.provider.WorkProvider;
import ru.tggc.capybaratelegrambot.provider.WorkProviderFactory;
import ru.tggc.capybaratelegrambot.repository.CapybaraRepository;
import ru.tggc.capybaratelegrambot.repository.ChatRepository;
import ru.tggc.capybaratelegrambot.repository.TeaRepository;
import ru.tggc.capybaratelegrambot.utils.CapybaraBuilder;
import ru.tggc.capybaratelegrambot.utils.RandomUtils;
import ru.tggc.capybaratelegrambot.utils.Text;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static java.lang.Math.max;
import static ru.tggc.capybaratelegrambot.utils.Utils.throwIf;

@Service
@RequiredArgsConstructor
public class CapybaraService {
    private final CapybaraRepository capybaraRepository;
    private final UserService userService;
    private final TeaRepository teaRepository;
    private final CapybaraTeaMapper capybaraTeaMapper;
    private final WorkProviderFactory workProviderFactory;
    private final TimedActionService timedActionService;
    private final MyCapybaraMapper myCapybaraMapper;
    private final CapybaraInfoMapper capybaraInfoMapper;
    private final InlineKeyboardCreator inlineKeyboardCreator;
    private final ChatRepository chatRepository;

    @Setter(onMethod_ = {@Autowired, @Lazy})
    private CapybaraService self;

    public Optional<Capybara> findCapybara(CapybaraContext ctx) {
        return capybaraRepository.findMyCapybaraByUserIdAndChatId(ctx.userId(), ctx.chatId());
    }

    public Capybara getCapybara(Long id) {
        return capybaraRepository.findById(id)
                .orElseThrow(() -> new CapybaraNotFoundException("Capy didnt found"));
    }

    public Capybara getCapybaraByUserId(long userId, long chatId) {
        return getCapybara(userId, chatId);
    }

    public Capybara getCapybaraByContext(CapybaraContext ctx) {
        return getCapybaraByUserId(ctx.userId(), ctx.chatId());
    }

    public MyCapybaraDto getMyCapybara(CapybaraContext ctx) {
        Capybara capybara = capybaraRepository.findMyCapybaraByUserIdAndChatId(ctx.userId(), ctx.chatId())
                .orElseThrow(() -> new CapybaraNotFoundException("U have no capy"));
        return myCapybaraMapper.toDto(capybara);
    }

    public CapybaraInfoDto getInfo(CapybaraContext ctx) {
        Capybara capybara = capybaraRepository.findInfoCapybaraByUserIdAndChatId(ctx.userId(), ctx.chatId())
                .orElseThrow(() -> new CapybaraNotFoundException("u have no capy"));
        return capybaraInfoMapper.toDto(capybara);
    }

    @Transactional
    public String setDefaultPhoto(CapybaraContext ctx) {
        Capybara capybara = getCapybaraByContext(ctx);
        checkCurrency(capybara, 25);
        capybara.setPhoto(RandomUtils.getRandomDefaultPhoto());
        capybara.setCurrency(capybara.getCurrency() - 25);
        capybaraRepository.save(capybara);
        return "Выбрано случайное фото. Со счета капибры списано 25 арбузных долек";
    }

    @Transactional
    public List<PhotoDto> makeHappy(CapybaraContext ctx) {
        List<PhotoDto> messages = new ArrayList<>();
        Capybara capybara = capybaraRepository.findSatietyAndHappinessCapybaraByUserIdAndChatId(ctx.userId(), ctx.chatId())
                .orElseThrow(CapybaraNotFoundException::new);
        Happiness happiness = capybara.getHappiness();

        throwIf(!happiness.canPerform(), () -> {
            String message = "Ты сможешь сделать хорошие дела для вашей капибары только " +
                    timedActionService.getStatus(happiness);
            return new CapybaraException(message);
        });

        HappinessThings happinessThing = RandomUtils.getRandomHappinessThing();
        happiness.setLevel(max(0, happiness.getLevel() + happinessThing.getLevel()));
        happiness.setLastHappy(LocalDateTime.now());
        messages.add(PhotoDto.builder()
                .caption(happinessThing.getLabel())
                .chatId(ctx.chatId())
                .url(happinessThing.getPhotoUrl())
                .markup(inlineKeyboardCreator.toMainMenu())
                .build());
        messages.addAll(self.checkNewLevel(capybara));
        capybaraRepository.save(capybara);
        return messages;
    }

    public List<PhotoDto> feed(CapybaraContext ctx) {
        Capybara capybara = capybaraRepository.findSatietyAndHappinessCapybaraByUserIdAndChatId(ctx.userId(), ctx.chatId())
                .orElseThrow(CapybaraNotFoundException::new);
        List<PhotoDto> messages = self.feed(capybara, 5);
        messages.add(PhotoDto.builder()
                .caption("Твоя капибара успешно покушала, возвращайся через 2 часа!")
                .chatId(ctx.chatId())
                .url("https://vk.com/photo-209917797_457245510")
                .markup(inlineKeyboardCreator.toMainMenu())
                .build());
        return messages;
    }

    public List<PhotoDto> fatten(CapybaraContext ctx) {
        Capybara capybara = capybaraRepository.findSatietyAndHappinessCapybaraByUserIdAndChatId(ctx.userId(), ctx.chatId())
                .orElseThrow(() -> new CapybaraNotFoundException("ur capy didint found"));
        checkCurrency(capybara, 50);
        List<PhotoDto> messages = self.feed(capybara, 50);
        capybara.setCurrency(capybara.getCurrency() - 50);
        messages.add(PhotoDto.builder()
                .caption("""
                        Твоя капибара съела целый арбуз!
                        Её сытость увеличилась на 50!
                        Возвращайся через 2 часа!""")
                .url("https://vk.com/photo-209917797_457246187")
                .chatId(ctx.chatId())
                .markup(inlineKeyboardCreator.toMainMenu())
                .build());
        return messages;
    }

    public List<PhotoDto> goTea(CapybaraContext ctx) {
        Capybara capybara = capybaraRepository.findTeaCapybaraByUserIdAndChatId(ctx.userId(), ctx.chatId())
                .orElseThrow(CapybaraNotFoundException::new);
        Tea tea = capybara.getTea();

        throwIf(!tea.canPerform(), () -> {
            String message = "Твоя капибара сможет пойти на чаепитие только через " +
                    timedActionService.getStatus(tea);
            return new CapybaraException(message);
        });

        throwIf(tea.isWaiting(), () -> new CapybaraException("Твоя капибара и так ждет собеседника!"));

        List<Tea> byIsWaiting = teaRepository.findByIsWaiting(true);
        if (!byIsWaiting.isEmpty()) {
            Tea incerlocutorTea = byIsWaiting.getFirst();
            Capybara interlocutor = incerlocutorTea.getCapybara();
            String photo = interlocutor.getPhoto().getUrl();
            CapybaraTeaDto myDto = capybaraTeaMapper.toDto(capybara);
            CapybaraTeaDto interlocutorDto = capybaraTeaMapper.toDto(interlocutor);
            updateTea(tea);
            updateTea(incerlocutorTea);
            capybara.getHappiness().setLevel(capybara.getHappiness().getLevel() + 10);
            interlocutor.getHappiness().setLevel(interlocutor.getHappiness().getLevel() + 10);
            List<PhotoDto> photosToReturn = new ArrayList<>(self.checkNewLevel(capybara));
            photosToReturn.addAll(self.checkNewLevel(interlocutor));
            capybaraRepository.save(interlocutor);
            capybaraRepository.save(capybara);
            photosToReturn.add(PhotoDto.builder()
                    .url(photo)
                    .chatId(ctx.chatId())
                    .caption(Text.getTea(myDto, interlocutorDto))
                    .build());
            photosToReturn.add(PhotoDto.builder()
                    .url(capybara.getPhoto().getUrl())
                    .caption(Text.getTea(interlocutorDto, myDto))
                    .chatId(interlocutor.getChat().getId())
                    .build());
            return photosToReturn;
        }
        tea.setWaiting(true);
        capybaraRepository.save(capybara);
        return List.of(PhotoDto.builder()
                .url("https://vk.com/photo-209917797_457246193")
                .chatId(ctx.chatId())
                .markup(inlineKeyboardCreator.teaKeyboard())
                .caption("Твоя капибара ждет собеседника для чаепития!")
                .build());
    }

    public void takeFromTea(CapybaraContext ctx) {
        Capybara capybara = capybaraRepository.findTeaCapybaraByUserIdAndChatId(ctx.userId(), ctx.chatId())
                .orElseThrow(CapybaraNotFoundException::new);
        capybara.getTea().setWaiting(false);
        capybaraRepository.save(capybara);
    }

    @Transactional
    public PhotoDto saveCapybara(CapybaraContext ctx) {
        long chatId = ctx.chatId();
        long userId = ctx.userId();
        Boolean capybaraExists = capybaraRepository.existsCapybaraByUserIdAndChatId(userId, chatId);
        throwIf(capybaraExists, CapybaraAlreadyExistsException::new);

        User user = userService.getUserById(userId);
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(IllegalArgumentException::new);
        int size = capybaraRepository.countByChatId(chatId);
        Capybara capybara = CapybaraBuilder.buildCapybara(size, chat, user);
        capybaraRepository.save(capybara);
        return PhotoDto.builder()
                .chatId(chatId)
                .caption("Теперь у тебя есть капибара!\nПоздравляю!!!" +
                        "\nЕё имя: " + capybara.getName() + ". \nНо ты всегда можешь поменять его!")
                .url(capybara.getPhoto().getUrl())
                .markup(inlineKeyboardCreator.toMainMenu())
                .build();
    }

    public boolean hasWork(CapybaraContext ctx) {
        return capybaraRepository.findByUserIdAndChatId(ctx.userId(), ctx.chatId())
                .map(Capybara::getWork)
                .map(Work::getWorkType)
                .map(jt -> jt != WorkType.NONE)
                .orElse(false);
    }

    public String setJob(CapybaraContext ctx, WorkType workType) {
        Capybara capybara = getCapybaraByContext(ctx);

        WorkProvider workProvider = workProviderFactory.getJobProvider(workType);
        String photoUrl = workProvider.setJob(capybara);
        capybaraRepository.save(capybara);
        return photoUrl;
    }

    public void goJob(CapybaraContext ctx) {
        Capybara capybara = getCapybaraByContext(ctx);
        WorkProvider workProvider = workProviderFactory.getJobProvider(capybara.getWork().getWorkType());
        workProvider.goWork(capybara);
        capybaraRepository.save(capybara);
    }

    public List<String> takeFromWork(CapybaraContext ctx) {
        Capybara capybara = getCapybaraByContext(ctx);
        WorkProvider workProvider = workProviderFactory.getJobProvider(capybara.getWork().getWorkType());
        List<String> messages = workProvider.takeFromWork(capybara);
        capybaraRepository.save(capybara);
        return messages;
    }

    public void doMassage(CapybaraContext ctx) {
        Capybara capybara = getRaceCapybara(ctx);
        if (capybara.getCurrency() <= 50) {
            throw new CapybaraHasNoMoneyException();
        }
        capybara.setCurrency(capybara.getCurrency() - 50);
        capybara.getRace().getRaceAction().setCharges(capybara.getRace().getRaceAction().getMaxCharges());

        capybaraRepository.save(capybara);
    }

    @Transactional
    public void setImprovement(CapybaraContext ctx, ImprovementValue improvementValue) {
        Capybara capybara = getCapybaraByContext(ctx);
        throwIf(capybara.getCurrency() <= 50, CapybaraHasNoMoneyException::new);

        Improvement improvement = capybara.getImprovement();
        if (improvement.getImprovementValue() == ImprovementValue.NONE) {
            improvement.setImprovementValue(improvementValue);
            capybara.setImprovement(improvement);
            capybara.setCurrency(capybara.getCurrency() - 50);
            capybaraRepository.save(capybara);
        }
    }

    public Capybara getCapybaraById(Long id) {
        return capybaraRepository.findById(id)
                .orElseThrow(CapybaraNotFoundException::new);
    }

    @Transactional
    public void deleteCapybara(CapybaraContext ctx) {
        Capybara capybara = getCapybaraByContext(ctx);
        capybaraRepository.delete(capybara);
    }

    public List<TopCapybaraDto> getTopCapybaras() {
        return capybaraRepository.findTop10ByOrderByLevelValueDesc().stream()
                .map(c -> {
                    PhotoDto photo = PhotoDto.builder()
                            .url(c.getPhoto().getUrl())
                            .build();
                    return new TopCapybaraDto(c.getName(), photo, c.getLevel().getValue());
                })
                .toList();
    }

    public void dismissal(CapybaraContext ctx) {
        Capybara capybara = getCapybaraByContext(ctx);
        WorkProvider workProvider = workProviderFactory.getJobProvider(capybara.getWork().getWorkType());
        workProvider.dismissal(capybara);
        capybaraRepository.save(capybara);
    }

    public void transferMoney(CapybaraContext ctx, String targetUsername, Long amount) {
        Capybara sourcecapybara = getCapybaraByContext(ctx);
        throwIf(sourcecapybara.getCurrency() < amount, CapybaraHasNoMoneyException::new);

        User user = userService.getUserByUsername(targetUsername);
        Capybara targetCapybara = getCapybara(user.getId());

        targetCapybara.setCurrency(targetCapybara.getCurrency() + amount);
        sourcecapybara.setCurrency(sourcecapybara.getCurrency() - amount);

        capybaraRepository.saveAll(List.of(sourcecapybara, targetCapybara));
    }

    public void save(Capybara capybara) {
        capybaraRepository.save(capybara);
    }

    private Capybara getCapybara(long userId, long chatId) {
        return capybaraRepository.findMyCapybaraByUserIdAndChatId(userId, chatId)
                .orElseThrow(() -> new CapybaraNotFoundException("User" + userId + "doesnt have capybara"));
    }

    @Transactional
    public List<PhotoDto> checkNewLevel(Capybara capybara) {
        List<PhotoDto> messages = new ArrayList<>();
        if (capybara.getHappiness().getLevel() >= capybara.getHappiness().getMaxLevel()) {
            capybara.getHappiness().setLevel(0);
            capybara.getLevel().setValue(capybara.getLevel().getValue() + 1);
            messages.add(PhotoDto.builder()
                    .caption(Text.newLevel(capybara.getUser().getId().toString(), capybara.getChat().getId().toString()))
                    .url("https://vk.com/photo-209917797_457246194")
                    .build());
            self.checkNewType(capybara).ifPresent(messages::add);
            capybara.getHappiness().setMaxLevel((capybara.getLevel().getValue() / 10) * 10 * 2);
            capybara.getSatiety().setMaxLevel((capybara.getLevel().getValue() / 10) * 10 * 2);
        }
        if (capybara.getSatiety().getLevel() >= capybara.getSatiety().getMaxLevel()) {
            capybara.getSatiety().setLevel(0);
            capybara.getLevel().setValue(capybara.getLevel().getValue() + 1);
            messages.add(PhotoDto.builder()
                    .caption(Text.newLevel(capybara.getUser().getId().toString(), capybara.getChat().getId().toString()))
                    .url("https://vk.com/photo-209917797_457246194")
                    .build());
            self.checkNewType(capybara).ifPresent(messages::add);
            capybara.getSatiety().setMaxLevel((capybara.getLevel().getValue() / 10) * 10 * 2);
            capybara.getHappiness().setMaxLevel((capybara.getLevel().getValue() / 10) * 10 * 2);
        }
        return messages;
    }

    @Transactional
    public List<PhotoDto> feed(Capybara capybara, Integer feed) {
        Satiety satiety = capybara.getSatiety();
        throwIf(!satiety.canPerform(), () -> {
            String message = "Ты сможешь покормить капибару только через " +
                    timedActionService.getStatus(satiety);
            return new CapybaraException(message);
        });
        satiety.setLevel(satiety.getLevel() + feed);
        satiety.setLastFed(LocalDateTime.now());
        List<PhotoDto> messages = new ArrayList<>(self.checkNewLevel(capybara));
        capybaraRepository.save(capybara);
        return messages;
    }

    @Transactional
    public Optional<PhotoDto> checkNewType(Capybara capybara) {
        Level level = capybara.getLevel();
        if (level.getValue() >= level.getMaxValue()) {
            return Arrays.stream(Type.values()).
                    filter(type -> type.getLevel().equals(level.getMaxValue()))
                    .findFirst()
                    .map(type -> {
                        capybara.getLevel().setType(type);
                        capybara.getLevel().setMaxValue(calculateMaxLevel(level));
                        capybara.setCurrency(capybara.getCurrency() + type.getGift());
                        return PhotoDto.builder()
                                .caption(Text.newType(type.getLabel(), type.getGift()))
                                .url("/https://vk.com/photo-209917797_457246195")
                                .build();
                    });
        } else {
            return Optional.empty();
        }
    }

    private void updateTea(Tea tea) {
        tea.setWaiting(false);
        tea.setLastTea(LocalDateTime.now());
    }

    private Integer calculateMaxLevel(Level level) {
        return switch (level.getValue()) {
            case Integer i when i < 100 -> level.getMaxValue() + 10;
            case Integer i when i < 150 -> 150;
            default -> Integer.MAX_VALUE;
        };
    }

    private void checkCurrency(Capybara capybara, Integer currency) {
        throwIf(capybara.getCurrency() <= currency, CapybaraHasNoMoneyException::new);
    }

    @Transactional
    public void changeName(CapybaraContext historyDto, String newName) {
        if (newName.length() > 25 || newName.isEmpty()) {
            throw new CapybaraException("Имя капибары должно быть меньше 25 символов!");
        }
        Capybara capybara = getCapybaraByContext(historyDto);
        capybara.setName(newName);
        capybaraRepository.save(capybara);
    }

    public Capybara getRaceCapybara(CapybaraContext ctx) {
        return capybaraRepository.findRaceCapybaraByUserIdAndChatId(ctx.userId(), ctx.chatId())
                .orElseThrow(CapybaraNotFoundException::new);
    }

    @Transactional
    public void setPhoto(CapybaraContext ctx, Message message) {
        Capybara capybara = getCapybaraByContext(ctx);
        if (message.photo() != null && message.photo().length != 0) {
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
}
