package ru.tggc.botapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.tggc.botapp.domain.model.Chat;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {
}
