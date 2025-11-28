package ru.tggc.capybaratelegrambot.domain.fight.effect;

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

    @Override
    public boolean isExpired() {
        return turnsLeft <= 0;
    }
}
