package ru.tggc.botapp.repository;

import lombok.NonNull;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.tggc.botapp.domain.model.Capybara;
import ru.tggc.botapp.domain.model.RaceRequest;
import ru.tggc.botapp.domain.model.enums.RaceStatus;

import java.util.Optional;

@Repository
public interface RaceRequestRepository extends JpaRepository<@NonNull RaceRequest, @NonNull Long> {
    @EntityGraph(attributePaths = {
            "challenger.improvement", "challenger.improvement.improvementValue",
            "challenger.level",
            "challenger.happiness",
            "challenger.race"
    })
    Optional<RaceRequest> findByOpponentIdAndStatus(Long id, RaceStatus status);

    boolean existsByChallengerOrOpponent(Capybara challenger, Capybara opponent);
}
