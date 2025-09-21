package ru.tggc.capybaratelegrambot.domain.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.tggc.capybaratelegrambot.domain.model.enums.JobType;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Job {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;
    private LocalDateTime lastTime;
    private Boolean isWorking;
    private LocalDateTime nextTime;
    private LocalDateTime timer;
    private Integer rise;
    private Integer index;
    @OneToOne
    private BigJob bigJob;
    @Enumerated(EnumType.STRING)
    private JobType jobType;
}
