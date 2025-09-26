package ru.tggc.capybaratelegrambot.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.tggc.capybaratelegrambot.domain.model.timedaction.Tea;

import java.util.List;

@Repository
public interface TeaRepository extends JpaRepository<Tea, String> {
    @EntityGraph(attributePaths = {
            "capybara",
            "capybara.photo",
            "capybara.user",
            "capybara.happiness",
            "capybara.level",
            "capybara.satiety",
            "capybara.chat"
    })
    List<Tea> findByIsWaiting(boolean isWaiting);
}
