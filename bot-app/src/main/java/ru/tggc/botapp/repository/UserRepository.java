package ru.tggc.botapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.tggc.botapp.domain.model.User;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    @Query("SELECT u.blockedReason FROM User u WHERE u.username = :username")
    Optional<String> findBlockedReasonByUsername(@Param("username") String username);
}
