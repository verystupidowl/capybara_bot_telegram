package ru.tggc.capybaratelegrambot.domain.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import ru.tggc.capybaratelegrambot.domain.model.enums.UserRole;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "usr")
@Builder
public class User {
    @Id
    private Long id;
    private String username;
    @OneToMany(mappedBy = "user")
    @ToString.Exclude
    private List<Capybara> capybara;
    private LocalDateTime createdAt;
    private LocalDateTime lastTimeUpdatedAt;
    @Enumerated(EnumType.STRING)
    private UserRole userRole;
}
