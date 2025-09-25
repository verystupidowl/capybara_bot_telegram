package ru.tggc.capybaratelegrambot.repository;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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
            "races"
    })
    Optional<Capybara> findById(@NotNull Long id);

    @EntityGraph(attributePaths = {
            "level", "level.type",
            "work", "work.workAction",
            "happiness",
            "satiety"
    })
    Optional<Capybara> findByUserIdAndChatId(@Param("userId") Long userId, @Param("chatId") String chatId);

    List<Capybara> findByChatId(String chatId);

    String user(User user);

    @Query(nativeQuery = true, value = "SELECT * FROM capybara ORDER BY level LIMIT 10")
    List<Capybara> getTopCapybaras();

    @EntityGraph(attributePaths = {
            "level", "level.type",
            "cheerfulness",
            "work", "work.workType", "work.workAction",
            "happiness",
            "satiety",
            "spouse",
            "photo"
    })
    Optional<Capybara> findMyCapybaraByUserIdAndChatId(Long userId, String chatId);

    @EntityGraph(attributePaths = {
            "tea",
            "happiness",
            "work", "work.workType", "work.workAction",
            "cheerfulness",
            "improvement",
            "satiety"
    })
    Optional<Capybara> findInfoCapybaraByUserIdAndChatId(Long userId, String chatId);

    Boolean existsCapybaraByUserIdAndChatId(long userId, String chatId);

    @EntityGraph(attributePaths = {
            "satiety",
            "level",
            "happiness"
    })
    Optional<Capybara> findSatietyAndHappinessCapybaraByUserIdAndChatId(Long userId, String chatId);

    @EntityGraph(attributePaths = {
            "tea",
            "photo",
            "happiness",
            "level",
            "satiety"
    })
    Optional<Capybara> findTeaCapybaraByUserIdAndChatId(Long userId, String chatId);

    @EntityGraph(attributePaths = {
            "improvement", "improvement.improvementValue",
            "level",
            "happiness",
            "races"
    })
    Optional<Capybara> findRaceCapybaraByUserIdAndChatId(Long userId, String chatId);
}
