package ru.tggc.capybaratelegrambot.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.tggc.capybaratelegrambot.domain.dto.enums.BossType;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BossFightState {
    private BossType boss;
    private Integer bossHp;
    private int turn;
    private Map<Long, PlayerState> players;
    private List<ActionLog> actionLogs;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class PlayerState {
        private Long userId;

        private String username;
        private boolean defending;

        private boolean stunned;
        private boolean alive;
        private ActionType lastAction;

        private PlayerStats playerStats;

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;
            PlayerState that = (PlayerState) o;
            return Objects.equals(userId, that.userId);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(userId);
        }
    }


    @AllArgsConstructor
    @Data
    @NoArgsConstructor
    public static class ActionLog {
        private String actor;
        private String action;
        private int value;
        private String whom;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    @Builder
    public static class PlayerStats {
        private double baseDamage;
        private double baseHeal;
        private double baseDefend;
        private double critChance;
        private double vampirism;
        private int hp;
    }

    public enum ActionType {
        ATTACK,
        DEFEND,
        HEAL,
        SKIP
    }
}
