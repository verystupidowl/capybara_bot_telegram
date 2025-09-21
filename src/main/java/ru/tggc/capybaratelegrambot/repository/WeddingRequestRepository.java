package ru.tggc.capybaratelegrambot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.tggc.capybaratelegrambot.domain.model.WeddingRequest;
import ru.tggc.capybaratelegrambot.domain.model.enums.WeddingRequestType;
import ru.tggc.capybaratelegrambot.domain.model.enums.WeddingStatus;

import java.util.Optional;

public interface WeddingRequestRepository extends JpaRepository<WeddingRequest, Long> {

    @Query(nativeQuery = true, value = "SELECT * FROM wedding_request WHERE target_id = :targetId")
    Optional<WeddingRequest> findByTargetIdAndStatusAndType(@Param("targetId") Long targetId, WeddingStatus status, WeddingRequestType type);

    Optional<WeddingRequest> findByProposerIdAndStatus(Long proposerId, WeddingStatus status);
}
