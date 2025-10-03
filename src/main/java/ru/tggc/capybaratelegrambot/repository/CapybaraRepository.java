package ru.tggc.capybaratelegrambot.repository;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.tggc.capybaratelegrambot.domain.model.Capybara;
import ru.tggc.capybaratelegrambot.domain.model.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface CapybaraRepository extends JpaRepository<Capybara, Long> {

    @NotNull
    @EntityGraph(attributePaths = {
            "improvement", "improvement.improvementValue",
            "level",
            "happiness",
            "chat"
    })
    Optional<Capybara> findById(@NotNull Long id);

    @EntityGraph(attributePaths = {
            "level", "level.type",
            "work", "work.workAction",
            "happiness",
            "satiety",
            "chat"
    })
    Optional<Capybara> findByUserIdAndChatId(Long userId, Long chatId);

    int countByChatId(Long chatId);

    String user(User user);

    @EntityGraph(attributePaths = {
            "photo",
            "level"
    })
    List<Capybara> findTop10ByOrderByLevelValueDesc();

    @EntityGraph(attributePaths = {
            "level", "level.type",
            "work", "work.workType", "work.workAction",
            "happiness",
            "satiety",
            "spouse",
            "photo",
            "race"
    })
    Optional<Capybara> findMyCapybaraByUserIdAndChatId(Long userId, Long chatId);

    @EntityGraph(attributePaths = {
            "tea",
            "happiness",
            "work", "work.workType", "work.workAction",
            "improvement",
            "satiety",
            "race"
    })
    Optional<Capybara> findInfoCapybaraByUserIdAndChatId(Long userId, Long chatId);

    Boolean existsCapybaraByUserIdAndChatId(long userId, Long chatId);

    @EntityGraph(attributePaths = {
            "satiety",
            "level",
            "happiness"
    })
    Optional<Capybara> findSatietyAndHappinessCapybaraByUserIdAndChatId(Long userId, Long chatId);

    @EntityGraph(attributePaths = {
            "tea",
            "photo",
            "happiness",
            "level",
            "satiety",
            "chat"
    })
    Optional<Capybara> findTeaCapybaraByUserIdAndChatId(Long userId, Long chatId);

    @EntityGraph(attributePaths = {
            "improvement", "improvement.improvementValue",
            "level",
            "happiness",
            "chat",
            "race"
    })
    Optional<Capybara> findRaceCapybaraByUserIdAndChatId(Long userId, Long chatId);

    @EntityGraph(attributePaths = {
            "level",
            "fight"
    })
    Optional<Capybara> findFightCapybaraByChatIdAndUserId(Long chatId, Long userId);
}
