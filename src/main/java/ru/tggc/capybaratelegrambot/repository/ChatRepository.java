package ru.tggc.capybaratelegrambot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.tggc.capybaratelegrambot.domain.model.Chat;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {
}
