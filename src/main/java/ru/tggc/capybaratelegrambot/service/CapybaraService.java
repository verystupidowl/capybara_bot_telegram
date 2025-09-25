package ru.tggc.capybaratelegrambot.service;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.tggc.capybaratelegrambot.domain.dto.CapybaraContext;
import ru.tggc.capybaratelegrambot.domain.dto.CapybaraInfoDto;
import ru.tggc.capybaratelegrambot.domain.dto.CapybaraTeaDto;
import ru.tggc.capybaratelegrambot.domain.dto.HappinessThings;
import ru.tggc.capybaratelegrambot.domain.dto.MyCapybaraDto;
import ru.tggc.capybaratelegrambot.domain.dto.PhotoDto;
import ru.tggc.capybaratelegrambot.domain.dto.TopCapybaraDto;
import ru.tggc.capybaratelegrambot.domain.model.Capybara;
import ru.tggc.capybaratelegrambot.domain.model.Improvement;
import ru.tggc.capybaratelegrambot.domain.model.Level;
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
import ru.tggc.capybaratelegrambot.mapper.CapybaraInfoMapper;
import ru.tggc.capybaratelegrambot.mapper.CapybaraTeaMapper;
import ru.tggc.capybaratelegrambot.mapper.MyCapybaraMapper;
import ru.tggc.capybaratelegrambot.provider.JobProvider;
import ru.tggc.capybaratelegrambot.provider.JobProviderFactory;
import ru.tggc.capybaratelegrambot.repository.CapybaraRepository;
import ru.tggc.capybaratelegrambot.repository.TeaRepository;
import ru.tggc.capybaratelegrambot.utils.CapybaraBuilder;
import ru.tggc.capybaratelegrambot.utils.RandomUtils;
import ru.tggc.capybaratelegrambot.utils.Text;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CapybaraService {
    private final CapybaraRepository capybaraRepository;
    private final UserService userService;
    private final TeaRepository teaRepository;
    private final CapybaraTeaMapper capybaraTeaMapper;
    private final JobProviderFactory jobProviderFactory;
    private final TimedActionService timedActionService;
    private final MyCapybaraMapper myCapybaraMapper;
    private final CapybaraInfoMapper capybaraInfoMapper;
    @Setter(onMethod_ = {@Autowired, @Lazy})
    private CapybaraService self;

    public Capybara getCapybara(Long id, String chatId) {
        return capybaraRepository.findById(id)
                .orElseThrow(() -> new CapybaraNotFoundException("Capy didnt found", chatId));
    }

    public Capybara getCapybaraByUserId(String userId, String chatId) {
        return getCapybara(userId, chatId);
    }

    public Capybara getCapybaraByContext(CapybaraContext ctx) {
        return getCapybaraByUserId(ctx.userId(), ctx.chatId());
    }

    public MyCapybaraDto getMyCapybara(CapybaraContext ctx) {
        Capybara capybara = capybaraRepository.findMyCapybaraByUserIdAndChatId(Long.valueOf(ctx.userId()), ctx.chatId())
                .orElseThrow(() -> new CapybaraNotFoundException("U have no capy", ctx.chatId()));
        return myCapybaraMapper.toDto(capybara);
    }

    public CapybaraInfoDto getInfo(CapybaraContext ctx) {
        Capybara capybara = capybaraRepository.findInfoCapybaraByUserIdAndChatId(Long.parseLong(ctx.userId()), ctx.chatId())
                .orElseThrow(() -> new CapybaraNotFoundException("u have no capy", ctx.chatId()));
        return capybaraInfoMapper.toDto(capybara);
    }

    @Transactional
    public String setDefaultPhoto(CapybaraContext ctx) {
        Capybara capybara = getCapybaraByContext(ctx);
        checkCurrency(capybara, 25);
        capybara.setPhoto(RandomUtils.getRandomPhoto());
        capybara.setCurrency(capybara.getCurrency() - 25);
        capybaraRepository.save(capybara);
        return "Выбрано случайное фото. Со счета капибры списано 25 арбузных долек";
    }

    @Transactional
    public List<PhotoDto> makeHappy(CapybaraContext ctx) {
        List<PhotoDto> messages = new ArrayList<>();
        Capybara capybara = capybaraRepository.findSatietyAndHappinessCapybaraByUserIdAndChatId(Long.valueOf(ctx.userId()), ctx.chatId())
                .orElseThrow(() -> new CapybaraNotFoundException("ur capy didint found", ctx.chatId()));
        Happiness happiness = capybara.getHappiness();

        if (!happiness.canPerform()) {
            String message = "Ты сможешь сделать хорошие дела для вашей капибары только " +
                    timedActionService.getStatus(happiness);
            throw new CapybaraException(message);
        }
        HappinessThings happinessThing = RandomUtils.getRandomHappinessThing();
        happiness.setLevel(happiness.getLevel() + happinessThing.getLevel());
        happiness.setLastHappy(LocalDateTime.now());
        messages.add(PhotoDto.builder()
                .caption(happinessThing.getLabel())
                .chatId(ctx.chatId())
                .url(happinessThing.getPhotoUrl())
                .build());
        messages.addAll(self.checkNewLevel(capybara));
        capybaraRepository.save(capybara);
        return messages;
    }

    public List<PhotoDto> feed(CapybaraContext ctx) {
        Capybara capybara = capybaraRepository.findSatietyAndHappinessCapybaraByUserIdAndChatId(Long.valueOf(ctx.userId()), ctx.chatId())
                .orElseThrow(() -> new CapybaraNotFoundException("ur capy didint found", ctx.chatId()));
        List<PhotoDto> messages = self.feed(capybara, 5);
        messages.add(PhotoDto.builder()
                .caption("Твоя капибара успешно покушала, возвращайся через 2 часа!")
                .chatId(ctx.chatId())
                .url("https://vk.com/photo-209917797_457245510")
                .build());
        return messages;
    }

    public List<PhotoDto> fatten(CapybaraContext ctx) {
        Capybara capybara = capybaraRepository.findSatietyAndHappinessCapybaraByUserIdAndChatId(Long.valueOf(ctx.userId()), ctx.chatId())
                .orElseThrow(() -> new CapybaraNotFoundException("ur capy didint found", ctx.chatId()));
        checkCurrency(capybara, 50);
        List<PhotoDto> messages = self.feed(capybara, 50);
        messages.add(PhotoDto.builder()
                .caption("""
                        Твоя капибара съела целый арбуз!
                        Её сытость увеличилась на 50!
                        Возвращайся через 2 часа!""")
                .url("https://vk.com/photo-209917797_457246187")
                .chatId(ctx.chatId())
                .build());
        return messages;
    }

    public List<PhotoDto> goTea(CapybaraContext ctx) {
        Capybara capybara = capybaraRepository.findTeaCapybaraByUserIdAndChatId(Long.valueOf(ctx.userId()), ctx.chatId())
                .orElseThrow(CapybaraNotFoundException::new);
        Tea tea = capybara.getTea();
        if (!tea.canPerform()) {
            String message = "Твоя капибара сможет пойти на чаепитие только через " +
                    timedActionService.getStatus(tea);
            throw new CapybaraException(message);
        }
        if (tea.isWaiting()) {
            throw new CapybaraException("Твоя капибара и так ждет собеседника!");
        }
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
            photosToReturn.add(                    PhotoDto.builder()
                    .url(capybara.getPhoto().getUrl())
                    .caption(Text.getTea(interlocutorDto, myDto))
                    .chatId(interlocutor.getChatId())
                    .build());
            return photosToReturn;
        }
        tea.setWaiting(true);
        capybaraRepository.save(capybara);
        return List.of(PhotoDto.builder()
                .url("https://vk.com/photo-209917797_457246193")
                .chatId(ctx.chatId())
                .caption("Твоя капибара ждет собеседника для чаепития!")
                .build());
    }

    public void takeFromTea(CapybaraContext ctx) {
        Capybara capybara = getCapybaraByContext(ctx);
        capybara.getTea().setWaiting(false);
        capybaraRepository.save(capybara);
    }

    @Transactional
    public PhotoDto saveCapybara(CapybaraContext ctx) {
        String chatId = ctx.chatId();
        long userId = Long.parseLong(ctx.userId());
        Boolean capybaraExists = capybaraRepository.existsCapybaraByUserIdAndChatId(userId, chatId);
        if (Boolean.TRUE.equals(capybaraExists)) {
            throw new CapybaraAlreadyExistsException("U already have a capy", chatId);
        }
        User user = userService.getUserById(userId);
        int size = capybaraRepository.findByChatId(chatId).size();
        Capybara capybara = CapybaraBuilder.buildCapybara(size, chatId, user);
        capybaraRepository.save(capybara);
        return PhotoDto.builder()
                .chatId(chatId)
                .caption("Теперь у тебя есть капибара!\nПоздравляю!!!" +
                        "\nЕё имя: " + capybara.getName() + ". \nНо ты всегда можешь поменять его!")
                .url(capybara.getPhoto().getUrl())
                .build();
    }

    public boolean hasWork(CapybaraContext ctx) {
        return capybaraRepository.findByUserIdAndChatId(Long.parseLong(ctx.userId()), ctx.chatId())
                .map(Capybara::getWork)
                .map(Work::getWorkType)
                .map(jt -> jt != WorkType.NONE)
                .orElse(false);
    }

    public void setJob(CapybaraContext ctx, WorkType workType) {
        Capybara capybara = getCapybaraByContext(ctx);

        JobProvider jobProvider = jobProviderFactory.getJobProvider(workType);
        jobProvider.setJob(capybara);
        capybaraRepository.save(capybara);
    }

    public void goJob(CapybaraContext ctx) {
        Capybara capybara = getCapybaraByContext(ctx);
        JobProvider jobProvider = jobProviderFactory.getJobProvider(capybara.getWork().getWorkType());
        jobProvider.goWork(capybara);
        capybaraRepository.save(capybara);
    }

    public List<String> takeFromWork(CapybaraContext ctx) {
        Capybara capybara = getCapybaraByContext(ctx);
        JobProvider jobProvider = jobProviderFactory.getJobProvider(capybara.getWork().getWorkType());
        List<String> messages = jobProvider.takeFromWork(capybara);
        capybaraRepository.save(capybara);
        return messages;
    }

    public void doMassage(CapybaraContext ctx) {
        Capybara capybara = getCapybaraByContext(ctx);
        if (capybara.getCurrency() <= 50) {
            throw new CapybaraHasNoMoneyException();
        }
        capybara.setCurrency(capybara.getCurrency() - 50);
        capybara.setConsecutiveRaces(0);

        capybaraRepository.save(capybara);
    }

    @Transactional
    public void setImprovement(CapybaraContext ctx, ImprovementValue improvementValue) {
        Capybara capybara = getCapybaraByContext(ctx);
        if (capybara.getCurrency() <= 50) {
            throw new CapybaraHasNoMoneyException();
        }
        Improvement improvement = capybara.getImprovement();
        if (improvement.getImprovement() == ImprovementValue.NONE) {
            improvement.setImprovement(improvementValue);
            capybara.setImprovement(improvement);
            capybara.setCurrency(capybara.getCurrency() - 50);
            capybaraRepository.save(capybara);
        }
    }

    public void acceptWedding(String userId, String chatId) {
        //todo
    }

    @Transactional
    public void deleteCapybara(CapybaraContext ctx) {
        Capybara capybara = getCapybaraByContext(ctx);
        capybaraRepository.delete(capybara);
    }

    public List<TopCapybaraDto> getTopCapybaras() {
        return capybaraRepository.getTopCapybaras().stream()
                .map(c -> {
                    PhotoDto photo = PhotoDto.builder()
                            .url(c.getPhoto().getUrl())
                            .build();
                    return new TopCapybaraDto(c.getName(), photo);
                })
                .toList();
    }

    public void dismissal(CapybaraContext ctx) {
        Capybara capybara = getCapybaraByContext(ctx);
        JobProvider jobProvider = jobProviderFactory.getJobProvider(capybara.getWork().getWorkType());
        jobProvider.dismissal(capybara);
        capybaraRepository.save(capybara);
    }

    public void transferMoney(CapybaraContext ctx, String targetUsername, Long amount) {
        Capybara sourcecapybara = getCapybaraByContext(ctx);
        if (sourcecapybara.getCurrency() < amount) {
            throw new CapybaraHasNoMoneyException("ur capy has no money", ctx.chatId());
        }
        User user = userService.getUserByUsername(targetUsername);
        Capybara targetCapybara = getCapybara(user.getId(), ctx.chatId());

        targetCapybara.setCurrency(targetCapybara.getCurrency() + amount);
        sourcecapybara.setCurrency(sourcecapybara.getCurrency() - amount);

        capybaraRepository.saveAll(List.of(sourcecapybara, targetCapybara));
    }

    public void save(Capybara capybara) {
        capybaraRepository.save(capybara);
    }

    private Capybara getCapybara(String userId, String chatId) {
        return capybaraRepository.findMyCapybaraByUserIdAndChatId(Long.parseLong(userId), chatId)
                .orElseThrow(() -> new CapybaraNotFoundException("User" + userId + "doesnt have capybara", chatId));
    }

    @Transactional
    public List<PhotoDto> checkNewLevel(Capybara capybara) {
        List<PhotoDto> messages = new ArrayList<>();
        if (capybara.getHappiness().getLevel() >= capybara.getHappiness().getMaxLevel()) {
            capybara.getHappiness().setLevel(0);
            capybara.getLevel().setValue(capybara.getLevel().getValue() + 1);
            messages.add(PhotoDto.builder()
                    .caption(Text.newLevel(capybara.getUser().getId().toString(), capybara.getChatId()))
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
                    .caption(Text.newLevel(capybara.getUser().getId().toString(), capybara.getChatId()))
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
        if (!satiety.canPerform()) {
            String message = "Ты сможешь покормить капибару только через " +
                    timedActionService.getStatus(satiety);
            throw new CapybaraException("Capybara cant be fed(", capybara.getChatId(), message);
        }
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
        if (capybara.getCurrency() <= currency) {
            String message = Text.NO_MONEY;
            throw new CapybaraException(
                    message
            );
        }
    }
}
