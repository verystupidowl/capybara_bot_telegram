package ru.tggc.botapp.formatter.fight;

import ru.tggc.botapp.fight.event.FightEvent;

public interface FightEventFormatter<T extends FightEvent> {

    Class<T> type();

    String format(T event);
}
