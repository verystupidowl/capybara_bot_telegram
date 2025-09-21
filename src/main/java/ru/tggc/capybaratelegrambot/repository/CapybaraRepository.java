package ru.tggc.capybaratelegrambot.repository;

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
    @Query(nativeQuery = true, value = " SELECT * FROM capybara WHERE user_id = :userId AND chat_id = :chatId")
    Optional<Capybara> findByUserIdAndChatId(@Param("userId") String userId, @Param("chatId") String chatId);

    List<Capybara> findByChatId(String chatId);

    String user(User user);

    @Query(nativeQuery = true, "SELECT * FROM capybara ORDER BY level LIMIT 10")
    List<Capybara> getTopCapybaras();
}
