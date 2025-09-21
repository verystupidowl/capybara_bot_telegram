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
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Happiness {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;
    private LocalDateTime lastTime;
    private Integer level;
    private LocalDateTime nextTime;
    private Integer maxLevel;

    @OneToOne
    private Capybara capybara;
}
