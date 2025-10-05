package ru.tggc.capybaratelegrambot.domain.dto.fight.effect;

public abstract class AbstractEffect implements Effect {

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        return o != null && getClass() == o.getClass();
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
