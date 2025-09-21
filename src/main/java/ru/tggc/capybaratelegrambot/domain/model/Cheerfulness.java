package ru.tggc.capybaratelegrambot.domain.model;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
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
public class Cheerfulness {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;
    @OneToOne(fetch = FetchType.EAGER)
    private Capybara capybara;
    private Integer cheerfulnessLevel;
    private Integer maxLevel;
    private LocalDateTime nextTime;
}
