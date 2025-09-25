package ru.tggc.capybaratelegrambot.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.tggc.capybaratelegrambot.domain.model.RaceRequest;

import java.util.Optional;

@Repository
public interface RaceRequestRepository extends JpaRepository<RaceRequest, Long> {
    @EntityGraph(attributePaths = {
            "challenger.improvement", "challenger.improvement.improvementValue",
            "challenger.level",
            "challenger.happiness",
            "challenger.races"
    })
    Optional<RaceRequest> findByOpponentId(Long id);
}
