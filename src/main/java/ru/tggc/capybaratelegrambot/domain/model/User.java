package ru.tggc.capybaratelegrambot.domain.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.tggc.capybaratelegrambot.domain.model.enums.UserRole;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@ToString
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
    @ToString.Exclude
    @ManyToMany(fetch = FetchType.LAZY)
    private Set<Chat> chats;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
