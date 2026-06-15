package ru.tggc.botapp.repository;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.tggc.botapp.domain.model.BlockInfo;

import java.util.Optional;

@Repository
public interface BlockRepository extends JpaRepository<BlockInfo, Long> {

    @NotNull
    @EntityGraph(attributePaths = {
            "user.username",
            "reporter.username"
    })
    @Query("SELECT bi FROM BlockInfo bi WHERE bi.user.username = :username")
    Optional<BlockInfo> findByUserUsername(@NotNull String username);
}
