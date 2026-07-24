package ru.tggc.botapp.fight;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.tggc.botapp.fight.effect.Effect;
import ru.tggc.botapp.fight.enums.BossType;
import ru.tggc.botapp.fight.enums.PlayerActionType;
import ru.tggc.botapp.util.RandomUtils;

import java.util.ArrayList;
import java.util.HashMap;
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
    private List<String> actionLogs;

    public BossFightState(BossFightState.BossState bossState) {
        this.bossState = bossState;
        this.turn = 1;
        this.players = new HashMap<>();
        this.actionLogs = new ArrayList<>();
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class BossState {
        private BossType bossType;
        private Integer bossHp;

        public void applyDamage(PlayerState ps, DamageEvent damageEvent) {
            ps.playerStats.effects.forEach(e -> e.onDamageGiven(ps, damageEvent));
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

        private boolean canAct;
        private boolean alive;
        private PlayerActionType lastAction;

        private PlayerStats playerStats;
        private BossState boss;
        private int specials;

        public void endTurn() {
            this.setDefending(false);
            this.setLastAction(null);
            this.getPlayerStats().getEffects().forEach(e -> e.onTurnEnd(this));
            this.getPlayerStats().getEffects().removeIf(e -> {
                if (e.isExpired()) {
                    e.onExpired(this);
                    return true;
                }
                return false;
            });
        }

        public DamageEvent applyDamage(double dmg) {
            if (this.isDefending()) {
                dmg *= (int) (RandomUtils.getRandomStat(this.getPlayerStats().getBaseDefend()));
            }
            DamageEvent damage = new DamageEvent(dmg);

            playerStats.effects.forEach(e -> e.onDamageTaken(this, damage));

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

        public DamageEvent applyHeal(int heal) {
            playerStats.setHp(playerStats.getHp() + heal);
            DamageEvent healEvent = new DamageEvent(heal);
            playerStats.effects.forEach(e -> e.onHeal(this, healEvent));
            return healEvent;
        }
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
        private double critMultiplier;
        private Set<Effect> effects;
    }
}
