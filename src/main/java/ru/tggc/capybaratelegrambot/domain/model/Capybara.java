package ru.tggc.capybaratelegrambot.domain.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
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
    private String chatId;
    private int consecutiveRaces;
    private LocalDateTime lastRaceAt;
    private Long spouseId;
    boolean deleted = false;
    @OneToOne(mappedBy = "challenger", cascade = CascadeType.ALL)
    private RaceRequest raceRequest;
    @OneToOne
    private Level level;
    @OneToOne
    private Happiness happiness;
    @OneToMany
    private List<Race> races;
    @ManyToOne
    private User user;
    @OneToOne
    private Tea tea;
    @OneToOne
    private Job job;
    @OneToOne
    private Improvement improvement;
    @OneToOne
    private Photo photo;
    @OneToOne
    private Preparation preparation;
    @OneToOne
    private Satiety satiety;
    @OneToOne
    private WeddingGift weddingGift;
    @OneToOne
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
