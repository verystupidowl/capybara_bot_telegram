package ru.tggc.capybaratelegrambot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import ru.tggc.capybaratelegrambot.domain.model.Capybara;
import ru.tggc.capybaratelegrambot.domain.model.WeddingRequest;
import ru.tggc.capybaratelegrambot.domain.model.enums.WeddingRequestType;
import ru.tggc.capybaratelegrambot.domain.model.enums.WeddingStatus;

import java.util.Optional;

public interface WeddingRequestRepository extends JpaRepository<WeddingRequest, Long> {

    Optional<WeddingRequest> findByTargetIdAndStatusAndType(@Param("targetId") Long targetId, @Param("status") WeddingStatus status, WeddingRequestType type);

    boolean existsByProposerOrTarget(Capybara proposer, Capybara target);
}
