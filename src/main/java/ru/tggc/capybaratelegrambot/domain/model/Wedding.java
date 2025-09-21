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

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Wedding {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;
    @OneToOne
    private Capybara firstCapybara;
    @OneToOne
    private Capybara secondCapybara;
    private Boolean isWedding;
    private Boolean active;
}
