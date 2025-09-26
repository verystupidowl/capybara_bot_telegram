package ru.tggc.capybaratelegrambot.domain.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import ru.tggc.capybaratelegrambot.domain.model.timedaction.Happiness;
import ru.tggc.capybaratelegrambot.domain.model.timedaction.Satiety;
import ru.tggc.capybaratelegrambot.domain.model.timedaction.Tea;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SQLDelete(sql = "UPDATE capybara SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
public class Capybara {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private Integer wins;
    private Integer defeats;
    private Long currency;
    private LocalDateTime created;
    @OneToOne(fetch = FetchType.LAZY)
    private Chat chat;
    private int consecutiveRaces;
    private LocalDateTime lastRaceAt;
    @OneToOne(fetch = FetchType.LAZY)
    private Capybara spouse;
    boolean deleted = false;
    @OneToOne
    private RaceRequest raceRequest;
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Level level;
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Happiness happiness;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Race> races;
    @ManyToOne
    private User user;
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Tea tea;
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Work work;
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Improvement improvement;
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Photo photo;
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Preparation preparation;
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Satiety satiety;
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private WeddingGift weddingGift;
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Cheerfulness cheerfulness;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Capybara capybara = (Capybara) o;
        return Objects.equals(id, capybara.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
