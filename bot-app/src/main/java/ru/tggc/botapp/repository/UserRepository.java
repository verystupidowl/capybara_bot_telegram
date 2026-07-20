package ru.tggc.botapp.repository;

import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.tggc.botapp.domain.model.User;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<@NonNull User, @NonNull Long> {
    Optional<User> findByUsername(String username);
}
