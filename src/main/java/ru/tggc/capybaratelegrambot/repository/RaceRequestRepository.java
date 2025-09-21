package ru.tggc.capybaratelegrambot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.tggc.capybaratelegrambot.domain.model.RaceRequest;

@Repository
public interface RaceRequestRepository extends JpaRepository<RaceRequest, Long> {
}
