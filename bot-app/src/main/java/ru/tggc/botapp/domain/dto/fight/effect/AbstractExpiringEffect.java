package ru.tggc.botapp.domain.dto.fight.effect;

public abstract class AbstractExpiringEffect extends AbstractEffect {
    protected int turnsLeft;

    protected AbstractExpiringEffect(int turnsLeft) {
        this.turnsLeft = turnsLeft;
    }

    protected void doEffect(Runnable damageConsumer) {
        if (this.turnsLeft > 0) {
            damageConsumer.run();
            this.turnsLeft--;
        }
    }

    protected void decreaseTurnsLeft() {
        if (this.turnsLeft > 0) {
            this.turnsLeft--;
        }
    }

    @Override
    public boolean isExpired() {
        return turnsLeft <= 0;
    }
}
