package ru.tggc.botapp.service.capybara;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.tggc.botapp.domain.dto.TopCapybaraDto;
import ru.tggc.botapp.domain.model.Capybara;
import ru.tggc.botapp.domain.model.timedaction.Tea;
import ru.tggc.botapp.exceptions.CapybaraAlreadyExistsException;
import ru.tggc.botapp.exceptions.CapybaraNotFoundException;
import ru.tggc.botapp.repository.CapybaraRepository;
import ru.tggc.telegrambotcore.dto.PhotoDto;
import ru.tggc.telegrambotcore.dto.UpdateContext;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Supplier;

import static ru.tggc.telegrambotcore.util.Utils.throwIf;

@Slf4j
@Service
@RequiredArgsConstructor
public class CapybaraQueryService {
    private final CapybaraRepository repository;

    @Transactional(readOnly = true)
    public Capybara getCapybara(Long id) {
        return repository.findById(id)
                .orElseThrow(CapybaraNotFoundException::new);
    }

    public Capybara getCapybaraByUserId(long userId, long chatId) {
        return repository.findMyCapybaraByUserIdAndChatId(userId, chatId)
                .orElseThrow(CapybaraNotFoundException::new);
    }

    public Capybara getCapybaraByContext(UpdateContext ctx) {
        return getCapybaraByUserId(ctx.userId(), ctx.chatId());
    }

    public Capybara getCapybaraByContext(UpdateContext ctx, Supplier<RuntimeException> supplier) {
        return repository.findMyCapybaraByUserIdAndChatId(ctx.userId(), ctx.chatId())
                .orElseThrow(supplier);
    }

    @Transactional
    public void save(Capybara capybara) {
        repository.save(capybara);
    }

    @Transactional(readOnly = true)
    public Capybara getWorkCapybara(UpdateContext ctx) {
        return repository.findByUserIdAndChatId(ctx.userId(), ctx.chatId())
                .orElseThrow(CapybaraNotFoundException::new);
    }

    @Transactional
    public void delete(Capybara capybara) {
        repository.delete(capybara);
    }

    @Transactional(readOnly = true)
    public Capybara getMyCapybara(UpdateContext ctx) {
        return repository.findMyCapybaraByUserIdAndChatId(ctx.userId(), ctx.chatId())
                .orElseThrow(CapybaraNotFoundException::new);
    }

    @Transactional(readOnly = true)
    public Capybara getInfoCapybara(UpdateContext ctx) {
        return repository.findInfoCapybaraByUserIdAndChatId(ctx.userId(), ctx.chatId())
                .orElseThrow(CapybaraNotFoundException::new);
    }

    @Transactional(readOnly = true)
    public void checkExists(UpdateContext ctx) {
        Boolean capybaraExists = repository.existsCapybaraByUserIdAndChatId(ctx.userId(), ctx.chatId());
        throwIf(capybaraExists, CapybaraAlreadyExistsException::new);
    }

    @Transactional(readOnly = true)
    public int getCountByChatId(long chatId) {
        return repository.countByChatId(chatId);
    }

    @Transactional(readOnly = true)
    public Capybara getFightCapybara(UpdateContext ctx) {
        return repository.findFightCapybaraByChatIdAndUserId(ctx.chatId(), ctx.userId())
                .orElseThrow(CapybaraNotFoundException::new);
    }

    public Capybara getFightCapybara(long chatId, long userId) {
        return repository.findFightCapybaraByChatIdAndUserId(chatId, userId)
                .orElseThrow(CapybaraNotFoundException::new);
    }

    @Transactional(readOnly = true)
    public Capybara getTeaCapybara(UpdateContext ctx) {
        return repository.findTeaCapybaraByUserIdAndChatId(ctx.userId(), ctx.chatId())
                .orElseThrow(CapybaraNotFoundException::new);
    }

    @Transactional
    public void updateTeas(Tea... teas) {
        for (Tea tea : teas) {
            tea.setWaiting(false);
            tea.setLastTea(LocalDateTime.now());
        }
    }

    @Transactional(readOnly = true)
    public Capybara getSatietyAndHappinessCapybara(UpdateContext ctx) {
        return repository.findSatietyAndHappinessCapybaraByUserIdAndChatId(ctx.userId(), ctx.chatId())
                .orElseThrow(CapybaraNotFoundException::new);
    }

    @Transactional(readOnly = true)
    public Capybara getRaceCapybara(UpdateContext ctx) {
        return repository.findRaceCapybaraByUserIdAndChatId(ctx.userId(), ctx.chatId())
                .orElseThrow(CapybaraNotFoundException::new);
    }

    @Transactional(readOnly = true)
    public List<TopCapybaraDto> getTopCapybaras() {
        return repository.findTop10ByOrderByLevelValueDesc().stream()
                .map(c -> {
                    PhotoDto photo = new PhotoDto(c.getPhoto().getUrl());
                    return new TopCapybaraDto(c.getName(), photo, c.getLevel().getValue());
                })
                .toList();
    }

    public void saveAll(List<Capybara> sourcecapybara) {
        repository.saveAll(sourcecapybara);
    }
}
