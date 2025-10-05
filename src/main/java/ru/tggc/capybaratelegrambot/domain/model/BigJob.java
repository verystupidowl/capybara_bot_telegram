package ru.tggc.capybaratelegrambot.domain.model;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.tggc.capybaratelegrambot.domain.model.timedaction.BigJobAction;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BigJob {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Embedded
    BigJobAction bigJobAction;
    private boolean active;
}
