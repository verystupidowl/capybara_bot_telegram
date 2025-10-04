package ru.tggc.capybaratelegrambot.domain.dto.fight;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.tggc.capybaratelegrambot.domain.dto.fight.effect.Effect;
import ru.tggc.capybaratelegrambot.domain.dto.fight.enums.BossType;
import ru.tggc.capybaratelegrambot.domain.dto.fight.enums.PlayerActionType;
import ru.tggc.capybaratelegrambot.utils.RandomUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BossFightState {
    private BossState bossState;
    private int turn;
    private Map<Long, PlayerState> players;
    private List<ActionLog> actionLogs;


    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class BossState {
        private BossType bossType;
        private Integer bossHp;

        public void applyDamage(PlayerState ps, DamageEvent damageEvent) {
            ps.playerStats.effects.forEach(e -> e.onDamageGiven(ps, this, damageEvent));
            bossHp -= (int) damageEvent.getDamage();
        }
    }

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
        private PlayerActionType lastAction;

        private PlayerStats playerStats;
        private BossState boss;

        public void endTurn() {
            this.setDefending(false);
            this.setStunned(false);
            this.setLastAction(null);
            this.getPlayerStats().getEffects().removeIf(Effect::isExpired);
        }

        public DamageEvent applyDamage(double dmg) {
            if (this.isDefending()) {
                dmg *= (int) (RandomUtils.getRandomStat(this.getPlayerStats().getBaseDefend()));
            }
            DamageEvent damage = new DamageEvent(dmg);

            playerStats.effects.forEach(e -> e.onDamageTaken(this, boss, damage));

            this.getPlayerStats().setHp((int) (this.getPlayerStats().getHp() - damage.getDamage()));
            if (this.getPlayerStats().getHp() <= 0) {
                this.setAlive(false);
            }
            return damage;
        }

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

        public void applyHeal(int heal) {
            playerStats.setHp(playerStats.getHp() + heal);
            DamageEvent healEvent = new DamageEvent(heal);
            playerStats.effects.forEach(e -> e.onHeal(this, boss, healEvent));
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
        private int hp;
        private double baseDamage;
        private double baseHeal;
        private double baseDefend;
        private double critChance;
        private double damageReflection;
        private double vampirism;
        private Set<Effect> effects;
    }
}
