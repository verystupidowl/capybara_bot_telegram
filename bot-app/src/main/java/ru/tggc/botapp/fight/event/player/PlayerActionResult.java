package ru.tggc.botapp.fight.event.player;

import java.util.List;

public record PlayerActionResult(List<PlayerActionEvent> events) {
}
