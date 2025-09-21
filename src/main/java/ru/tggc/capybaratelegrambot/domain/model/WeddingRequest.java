package ru.tggc.capybaratelegrambot.domain.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.tggc.capybaratelegrambot.domain.model.enums.WeddingRequestType;
import ru.tggc.capybaratelegrambot.domain.model.enums.WeddingStatus;

import java.time.LocalDateTime;

@Entity
@Table(name = "wedding_requests")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WeddingRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne
    private Capybara proposer;
    @OneToOne
    private Capybara target;
    @Enumerated(EnumType.STRING)
    private WeddingStatus status;
    @Enumerated(EnumType.STRING)
    private WeddingRequestType type;
    private LocalDateTime createdAt;
}