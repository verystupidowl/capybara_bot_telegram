package ru.tggc.capybaratelegrambot.domain.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tea {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;
    private LocalDateTime lastTime;
    private Boolean isWaiting;
    private LocalDateTime nextTime;
    @OneToOne
    private Capybara waitingCapybara;
}
