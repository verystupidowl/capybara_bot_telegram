package ru.tggc.capybaratelegrambot.domain.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import ru.tggc.capybaratelegrambot.domain.model.timedaction.Happiness;
import ru.tggc.capybaratelegrambot.domain.model.timedaction.Satiety;
import ru.tggc.capybaratelegrambot.domain.model.timedaction.Tea;
import ru.tggc.capybaratelegrambot.domain.model.timedaction.WeddingGift;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@ToString
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SQLDelete(sql = "UPDATE capybara SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Table(
        indexes = {
                @Index(name = "idx_capybara_chat", columnList = "chat_id"),
                @Index(name = "idx_capybara_user", columnList = "user_id"),
                @Index(name = "idx_capybara_chat_user", columnList = "chat_id, user_id")
        }
)
public class Capybara {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private Long currency;
    private LocalDateTime created;
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    private Fight fight;
    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    private Chat chat;
    @OneToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    private Capybara spouse;
    boolean deleted = false;
    @OneToOne
    private RaceRequest raceRequest;
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    private Race race;
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    private Level level;
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    private Happiness happiness;
    @ManyToOne
    private User user;
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    private Tea tea;
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    private Work work;
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    private Improvement improvement;
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    private Photo photo;
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    private Preparation preparation;
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    private Satiety satiety;
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    private WeddingGift weddingGift;

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
