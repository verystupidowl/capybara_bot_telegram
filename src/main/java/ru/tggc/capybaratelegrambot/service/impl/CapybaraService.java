package ru.tggc.capybaratelegrambot.service.impl;

import com.pengrad.telegrambot.model.CallbackQuery;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.tggc.capybaratelegrambot.domain.dto.CapybaraContext;
import ru.tggc.capybaratelegrambot.domain.dto.CapybaraTeaDto;
import ru.tggc.capybaratelegrambot.domain.dto.HappinessThings;
import ru.tggc.capybaratelegrambot.domain.dto.PhotoDto;
import ru.tggc.capybaratelegrambot.domain.dto.TopCapybaraDto;
import ru.tggc.capybaratelegrambot.domain.model.Capybara;
import ru.tggc.capybaratelegrambot.domain.model.Improvement;
import ru.tggc.capybaratelegrambot.domain.model.Work;
import ru.tggc.capybaratelegrambot.domain.model.Level;
import ru.tggc.capybaratelegrambot.domain.model.User;
import ru.tggc.capybaratelegrambot.domain.model.enums.ImprovementValue;
import ru.tggc.capybaratelegrambot.domain.model.enums.WorkType;
import ru.tggc.capybaratelegrambot.domain.model.enums.Type;
import ru.tggc.capybaratelegrambot.domain.model.timedaction.Happiness;
import ru.tggc.capybaratelegrambot.domain.model.timedaction.Satiety;
import ru.tggc.capybaratelegrambot.domain.model.timedaction.Tea;
import ru.tggc.capybaratelegrambot.exceptions.CapybaraAlreadyExistsException;
import ru.tggc.capybaratelegrambot.exceptions.CapybaraException;
import ru.tggc.capybaratelegrambot.exceptions.CapybaraHasNoMoneyException;
import ru.tggc.capybaratelegrambot.exceptions.CapybaraNotFoundException;
import ru.tggc.capybaratelegrambot.handler.callback.CallbackHandler;
import ru.tggc.capybaratelegrambot.mapper.CapybaraTeaMapper;
import ru.tggc.capybaratelegrambot.provider.JobProvider;
import ru.tggc.capybaratelegrambot.provider.JobProviderFactory;
import ru.tggc.capybaratelegrambot.repository.CapybaraRepository;
import ru.tggc.capybaratelegrambot.repository.TeaRepository;
import ru.tggc.capybaratelegrambot.service.RaceService;
import ru.tggc.capybaratelegrambot.utils.CapybaraBuilder;
import ru.tggc.capybaratelegrambot.utils.RandomUtils;
import ru.tggc.capybaratelegrambot.utils.Text;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;

@Service
@RequiredArgsConstructor
@Setter
public class CapybaraService {
    private final CapybaraRepository capybaraRepository;
    private final UserService userService;
    private final TeaRepository teaRepository;
    private final CapybaraTeaMapper capybaraTeaMapper;
    private JobProviderFactory jobProviderFactory;
    private RaceService raceService;
    private final TimedActionService timedActionService;
    private final CapybaraService capybaraService;

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
        Capybara capybara = getCapybaraByContext(ctx);
        Happiness happiness = capybara.getHappiness();

        if (!happiness.canPerform()) {
            String message = "Ты сможешь сделать хорошие дела для вашей капибары только " +
                    timedActionService.getStatus(happiness);
            throw new CapybaraException("Capybara cant be happy:(", ctx.chatId(), message);
        }
        HappinessThings happinessThing = RandomUtils.getRandomHappinessThing();
        happiness.setLevel(happiness.getLevel() + happinessThing.getLevel());
        happiness.setLastHappy(LocalDateTime.now());
        messages.add(PhotoDto.builder()
                .caption(happinessThing.getLabel())
                .url("//TODO") //todo
                .build());
        messages.addAll(capybaraService.checkNewLevel(capybara));
        capybaraRepository.save(capybara);
        return messages;
    }

    public List<PhotoDto> feed(CapybaraContext ctx) {
        Capybara capybara = getCapybaraByContext(ctx);
        List<PhotoDto> messages = feed(capybara, 5);
        messages.add(PhotoDto.builder()
                .caption("Твоя капибара успешно покушала, возвращайся через 2 часа!")
                .url("//TODO") //todo
                .build());
        return messages;
    }

    public List<PhotoDto> fatten(CapybaraContext ctx) {
        Capybara capybara = getCapybaraByContext(ctx);
        checkCurrency(capybara, 50);
        List<PhotoDto> messages = capybaraService.feed(ctx);
        messages.add(PhotoDto.builder()
                .caption("""
                        Твоя капибара съела целый арбуз!
                        Её сытость увеличилась на 50!
                        Возвращайся через 2 часа!""")
                .url("https://vk.com/photo-209917797_457245510")
                .build());
        return messages;
    }

    public List<PhotoDto> goTea(CapybaraContext ctx) {
        Capybara capybara = getCapybaraByContext(ctx);
        Tea tea = capybara.getTea();
        if (!tea.canPerform()) {
            String message = "Твоя капибара сможет пойти на чаепитие только через " +
                    timedActionService.getStatus(tea);
            throw new CapybaraException("Capybara cant go tea:(", ctx.chatId(), message);
        }
        if (!tea.isWaiting()) {
            throw new CapybaraException("Capybara cant go tea:(", ctx.chatId(), "Твоя капибара и так ждет собеседника!");
        }
        List<Tea> byIsWaiting = teaRepository.findByIsWaiting(true);
        if (!byIsWaiting.isEmpty()) {
            Tea incerlocutorTea = byIsWaiting.getFirst();
            Capybara interlocutor = byIsWaiting.getFirst().getCapybara();
            String photo = interlocutor.getPhoto().getUrl();
            CapybaraTeaDto myDto = capybaraTeaMapper.toDto(capybara);
            CapybaraTeaDto interlocutorDto = capybaraTeaMapper.toDto(interlocutor);
            updateTea(tea);
            updateTea(incerlocutorTea);
            capybaraRepository.save(interlocutor);
            capybaraRepository.save(capybara);
            return List.of(
                    PhotoDto.builder()
                            .url(photo)
                            .caption(Text.getTea(myDto, interlocutorDto))
                            .build(),
                    PhotoDto.builder()
                            .url(capybara.getPhoto().getUrl())
                            .caption(Text.getTea(interlocutorDto, myDto))
                            .chatId(interlocutor.getChatId())
                            .build()
            );
        }
        tea.setWaiting(true);
        capybaraRepository.save(capybara);
        return List.of(PhotoDto.builder()
                //todo .url()
                .caption("Твоя капибара ждет собеседника для чаепития!")
                .build());
    }

    public void takeFromTea(CapybaraContext ctx) {
        Capybara capybara = getCapybaraByContext(ctx);
        capybara.getTea().setWaiting(false);
        capybaraRepository.save(capybara);
    }

    public PhotoDto saveCapybara(CapybaraContext ctx) {
        String chatId = ctx.chatId();
        String userId = ctx.userId();
        capybaraRepository.findByUserIdAndChatId(userId, chatId)
                .ifPresent(cap -> {
                    throw new CapybaraAlreadyExistsException("Capybara already exists " + cap, chatId);
                });
        User user = userService.getUserByUserId(userId);
        int size = capybaraRepository.findByChatId(chatId).size();
        Capybara capybara = CapybaraBuilder.buildCapybara(size, chatId, user);
        capybaraRepository.save(capybara);
        return PhotoDto.builder()
                .caption("Теперь у тебя есть капибара!\nПоздравляю!!!" +
                        "\nЕё имя: " + capybara.getName() + ". \nНо ты всегда можешь поменять его!")
                .url(capybara.getPhoto().getUrl())
                .build();
    }

    public boolean hasWork(CapybaraContext ctx) {
        return capybaraRepository.findByUserIdAndChatId(ctx.userId(), ctx.chatId())
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

    public BiConsumer<CallbackHandler, CallbackQuery> acceptRace(CapybaraContext ctx) {
        Capybara capybara = getCapybaraByContext(ctx);
        return raceService.respondRace(capybara, true)
                .andThen((h, q) -> capybaraRepository.save(capybara));
    }

    public void refuseRace(CapybaraContext ctx) {
        Capybara capybara = getCapybaraByContext(ctx);
        raceService.respondRace(capybara, false);
        capybaraRepository.save(capybara);
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
            throw new CapybaraHasNoMoneyException("ur capy has no money",ctx.chatId());
        }
        User user = userService.getUserByUsername(targetUsername);
        Capybara targetCapybara = getCapybara(user.getUserId(), ctx.chatId());

        targetCapybara.setCurrency(targetCapybara.getCurrency() + amount);
        sourcecapybara.setCurrency(sourcecapybara.getCurrency() - amount);

        capybaraRepository.saveAll(List.of(sourcecapybara, targetCapybara));
    }

    public void save(Capybara capybara) {
        capybaraRepository.save(capybara);
    }

    private Capybara getCapybara(String userId, String chatId) {
        return capybaraRepository.findByUserIdAndChatId(userId, chatId)
                .orElseThrow(() -> new CapybaraNotFoundException("User" + userId + "doesnt have capybara", chatId));
    }

    @Transactional
    public List<PhotoDto> checkNewLevel(Capybara capybara) {
        List<PhotoDto> messages = new ArrayList<>();
        if (capybara.getHappiness().getLevel() >= capybara.getHappiness().getMaxLevel()) {
            capybara.getHappiness().setLevel(0);
            capybara.getLevel().setValue(capybara.getLevel().getValue() + 1);
            messages.add(PhotoDto.builder()
                    .caption(Text.newLevel(capybara.getUser().getUserId(), capybara.getChatId()))
                    .url("//TODO)") //todo
                    .build());
            capybaraService.checkNewType(capybara).ifPresent(messages::add);
            capybara.getHappiness().setMaxLevel((capybara.getLevel().getValue() / 10) * 10 * 2);
            capybara.getSatiety().setMaxLevel((capybara.getLevel().getValue() / 10) * 10 * 2);
        }
        if (capybara.getSatiety().getLevel() >= capybara.getSatiety().getMaxLevel()) {
            capybara.getSatiety().setLevel(0);
            capybara.getLevel().setValue(capybara.getLevel().getValue() + 1);
            messages.add(PhotoDto.builder()
                    .caption(Text.newLevel(capybara.getUser().getUserId(), capybara.getChatId()))
                    .url("//TODO") //todo
                    .build());
            capybaraService.checkNewType(capybara).ifPresent(messages::add);
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
        List<PhotoDto> messages = new ArrayList<>(capybaraService.checkNewLevel(capybara));
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
                                .url("//TODO") //todo
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
                    "Capybara " + capybara + " doesnt have enough money " + currency,
                    capybara.getChatId(),
                    message
            );
        }
    }
}
