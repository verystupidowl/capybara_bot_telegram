package ru.tggc.capybaratelegrambot.service.impl;

import com.pengrad.telegrambot.model.CallbackQuery;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.tggc.capybaratelegrambot.domain.dto.CapybaraTeaDto;
import ru.tggc.capybaratelegrambot.utils.CasinoTargetType;
import ru.tggc.capybaratelegrambot.domain.dto.HappinessThings;
import ru.tggc.capybaratelegrambot.domain.dto.PhotoDto;
import ru.tggc.capybaratelegrambot.domain.dto.TopCapybaraDto;
import ru.tggc.capybaratelegrambot.domain.model.Capybara;
import ru.tggc.capybaratelegrambot.domain.model.Happiness;
import ru.tggc.capybaratelegrambot.domain.model.Improvement;
import ru.tggc.capybaratelegrambot.domain.model.Job;
import ru.tggc.capybaratelegrambot.domain.model.Level;
import ru.tggc.capybaratelegrambot.domain.model.Satiety;
import ru.tggc.capybaratelegrambot.domain.model.Tea;
import ru.tggc.capybaratelegrambot.domain.model.User;
import ru.tggc.capybaratelegrambot.domain.model.enums.ImprovementValue;
import ru.tggc.capybaratelegrambot.domain.model.enums.JobType;
import ru.tggc.capybaratelegrambot.domain.model.enums.Type;
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
import ru.tggc.capybaratelegrambot.service.CapybaraService;
import ru.tggc.capybaratelegrambot.service.RaceService;
import ru.tggc.capybaratelegrambot.service.UserService;
import ru.tggc.capybaratelegrambot.service.WeddingService;
import ru.tggc.capybaratelegrambot.utils.CapybaraBuilder;
import ru.tggc.capybaratelegrambot.utils.RandomUtils;
import ru.tggc.capybaratelegrambot.utils.Text;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;

import static ru.tggc.capybaratelegrambot.utils.Utils.timeToString;

@Service
@RequiredArgsConstructor
@Setter
public class CapybaraServiceImpl implements CapybaraService {
    private final CapybaraRepository capybaraRepository;
    private final UserService userService;
    private final TeaRepository teaRepository;
    private final CapybaraTeaMapper capybaraTeaMapper;
    private CapybaraServiceImpl capybaraService;
    private JobProviderFactory jobProviderFactory;
    private RaceService raceService;
    private WeddingService weddingService;

    @Override
    public Capybara getCapybara(Long id, String chatId) {
        return capybaraRepository.findById(id)
                .orElseThrow(() -> new CapybaraNotFoundException("Capy didnt found", chatId));
    }

    @Override
    public Capybara getCapybaraByUserId(String userId, String chatId) {
        return getCapybara(userId, chatId);
    }

    @Override
    @Transactional
    public String setDefaultPhoto(String userId, String chatId) {
        Capybara capybara = getCapybara(userId, chatId);
        checkCurrency(capybara, 25);
        capybara.setPhoto(RandomUtils.getRandomPhoto());
        capybara.setCurrency(capybara.getCurrency() - 25);
        capybaraRepository.save(capybara);
        return "Выбрано случайное фото. Со счета капибры списано 25 арбузных долек";
    }

    @Override
    @Transactional
    public List<PhotoDto> makeHappy(String userId, String chatId) {
        List<PhotoDto> messages = new ArrayList<>();
        Capybara capybara = getCapybara(userId, chatId);
        Happiness happiness = capybara.getHappiness();
        if (happiness.getNextTime().isAfter(LocalDateTime.now())) {
            String message = "Ты сможешь сделать хорошие дела для вашей капибары только через " +
                    timeToString(capybara.getHappiness().getNextTime().compareTo(LocalDateTime.now()));
            throw new CapybaraException("Capybara cant be happy:(", chatId, message);
        }
        HappinessThings happinessThing = RandomUtils.getRandomHappinessThing();
        happiness.setLevel(happiness.getLevel() + happinessThing.getLevel());
        happiness.setLastTime(LocalDateTime.now());
        happiness.setNextTime(LocalDateTime.now().plusHours(2));
        messages.add(PhotoDto.builder()
                .caption(happinessThing.getLabel())
                .url(//TODO)
                .build());
        messages.addAll(capybaraService.checkNewLevel(capybara));
        capybaraRepository.save(capybara);
        return messages;
    }

    @Override
    public List<PhotoDto> feed(String userId, String chatId) {
        Capybara capybara = getCapybara(userId, chatId);
        List<PhotoDto> messages = capybaraService.feed(capybara, 5);
        messages.add(PhotoDto.builder()
                .caption("Твоя капибара успешно покушала, возвращайся через 2 часа!")
                .url(//TODO)
                .build());
        return messages;
    }

    @Override
    public List<PhotoDto> fatten(String userId, String chatId) {
        Capybara capybara = getCapybara(userId, chatId);
        checkCurrency(capybara, 50);
        List<PhotoDto> messages = capybaraService.feed(capybara, 50);
        messages.add(PhotoDto.builder()
                .caption("""
                        Твоя капибара съела целый арбуз!
                        Её сытость увеличилась на 50!
                        Возвращайся через 2 часа!""")
                .url("https://vk.com/photo-209917797_457245510")
                .build());
        return messages;
    }

    @Override
    public List<PhotoDto> goTea(String userId, String chatId) {
        Capybara capybara = getCapybara(userId, chatId);
        Tea tea = capybara.getTea();
        if (tea.getNextTime().isAfter(LocalDateTime.now())) {
            String message = "Твоя капибара сможет пойти на чаепитие только через " +
                    timeToString(capybara.getTea().getNextTime().compareTo(LocalDateTime.now()));
            throw new CapybaraException("Capybara cant go tea:(", chatId, message);
        }
        if (tea.getIsWaiting()) {
            throw new CapybaraException("Capybara cant go tea:(", chatId, "Твоя капибара и так ждет собеседника!");
        }
        List<Tea> byIsWaiting = teaRepository.findByIsWaiting(true);
        if (!byIsWaiting.isEmpty()) {
            Tea incerlocutorTea = byIsWaiting.getFirst();
            Capybara interlocutor = byIsWaiting.getFirst().getWaitingCapybara();
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
        tea.setIsWaiting(true);
        capybaraRepository.save(capybara);
        return List.of(PhotoDto.builder()
                //todo .url()
                .caption("Твоя капибара ждет собеседника для чаепития!")
                .build());
    }

    @Override
    public void takeFromTea(String userId, String chatId) {
        Capybara capybara = getCapybara(userId, chatId);
        capybara.getTea().setIsWaiting(false);
        capybaraRepository.save(capybara);
    }

    @Override
    public PhotoDto saveCapybara(String userId, String chatId) {
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

    @Override
    public boolean hasWork(String userId, String chatId) {
        return capybaraRepository.findByUserIdAndChatId(userId, chatId)
                .map(Capybara::getJob)
                .map(Job::getIsWorking)
                .orElseThrow(() -> new CapybaraNotFoundException("User " + userId + "doesnt have capybara", chatId));
    }

    @Override
    public void setJob(String userId, String chatId, JobType jobType) {
        Capybara capybara = getCapybara(userId, chatId);

        JobProvider jobProvider = jobProviderFactory.getJobProvider(jobType);
        jobProvider.setJob(capybara);
        capybaraRepository.save(capybara);
    }

    @Override
    public void goJob(String userId, String chatId) {
        Capybara capybara = getCapybara(userId, chatId);
        JobProvider jobProvider = jobProviderFactory.getJobProvider(capybara.getJob().getJobType());
        jobProvider.goWork(capybara);
        capybaraRepository.save(capybara);
    }

    @Override
    public List<String> takeFromWork(String userId, String chatId) {
        Capybara capybara = getCapybara(userId, chatId);
        JobProvider jobProvider = jobProviderFactory.getJobProvider(capybara.getJob().getJobType());
        List<String> messages = jobProvider.takeFromWork(capybara);
        capybaraRepository.save(capybara);
        return messages;
    }

    @Override
    public BiConsumer<CallbackHandler, CallbackQuery> acceptRace(String userId, String chatId) {
        Capybara capybara = getCapybara(userId, chatId);
        return raceService.respondRace(capybara, true)
                .andThen((_, _) -> capybaraRepository.save(capybara));
    }

    @Override
    public void refuseRace(String userId, String chatId) {
        Capybara capybara = getCapybara(userId, chatId);
        raceService.respondRace(capybara, false);
        capybaraRepository.save(capybara);
    }

    @Override
    public void doMassage(String userId, String chatId) {
        Capybara capybara = getCapybara(userId, chatId);
        if (capybara.getCurrency() <= 50) {
            throw new CapybaraHasNoMoneyException();
        }
        capybara.setCurrency(capybara.getCurrency() - 50);
        capybara.setConsecutiveRaces(0);

        capybaraRepository.save(capybara);
    }

    @Override
    @Transactional
    public void setImprovement(String userId, String chatId, ImprovementValue improvementValue) {
        Capybara capybara = getCapybara(userId, chatId);
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

    @Override
    public void acceptWedding(String userId, String chatId) {
        //todo
    }

    @Override
    @Transactional
    public void deleteCapybara(String userId, String chatId) {
        Capybara capybara = getCapybara(userId, chatId);
        capybaraRepository.delete(capybara);
    }

    @Override
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

    @Override
    public void dismissal(String userId, String chatId) {
        Capybara capybara = getCapybara(userId, chatId);
        JobProvider jobProvider = jobProviderFactory.getJobProvider(capybara.getJob().getJobType());
        jobProvider.dismissal(capybara);
        capybaraRepository.save(capybara);
    }

    @Override
    public String casino(String userId, String chatId, long betAmount, CasinoTargetType type) {
        Capybara capybara = getCapybara(userId, chatId);
        if (capybara.getCurrency() <= betAmount) {
            throw new CapybaraHasNoMoneyException("", chatId);
        }
        if (betAmount < (capybara.getLevel().getValue() / 10) * 25L) {
            throw new CapybaraException("ur min bet amount is ");
        }
        CasinoTargetType wonType = RandomUtils.randomWeighted();
        String response;

        if (wonType == type) {
            Long winAmount = type.getCalculateWin().apply(betAmount);
            capybara.setCurrency(capybara.getCurrency() + winAmount);
            response = "Вау! Вот это везение! Твоя капибара выиграла " + winAmount;
        } else {
            capybara.setCurrency(capybara.getCurrency() - betAmount);
            response = "Твоя капибара была близка, но проиграла " + betAmount;
        }

        capybaraRepository.save(capybara);
        return response;
    }

    @Override
    public void transferMoney(String userId, String chatId, String targetUsername, Long amount) {
        Capybara sourcecapybara = getCapybara(userId, chatId);
        if (sourcecapybara.getCurrency() < amount) {
            throw new CapybaraHasNoMoneyException("ur capy has no money");
        }
        User user = userService.getUserByUsername(targetUsername);
        Capybara targetCapybara = getCapybara(user.getUserId(), chatId);

        targetCapybara.setCurrency(targetCapybara.getCurrency() + amount);
        sourcecapybara.setCurrency(sourcecapybara.getCurrency() - amount);

        capybaraRepository.saveAll(List.of(sourcecapybara, targetCapybara));
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
                    .url(//TODO))
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
                    .url(//TODO)
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
        if (satiety.getNextTime().isAfter(LocalDateTime.now())) {
            String message = "Ты сможешь покормить капибару только через " +
                    timeToString(capybara.getSatiety().getNextTime().compareTo(LocalDateTime.now()));
            throw new CapybaraException("Capybara cant be fed(", capybara.getChatId(), message);
        }
        satiety.setLevel(satiety.getLevel() + feed);
        satiety.setLastTime(LocalDateTime.now());
        satiety.setNextTime(LocalDateTime.now().plusHours(2));
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
                                .url(//TODO)
                                .build();
                    });
        } else {
            return Optional.empty();
        }
    }

    private void updateTea(Tea tea) {
        tea.setIsWaiting(false);
        tea.setLastTime(LocalDateTime.now());
        tea.setNextTime(LocalDateTime.now().plusHours(2));
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
