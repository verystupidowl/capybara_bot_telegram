package ru.tggc.botapp.fight.event;

import java.util.List;

public record PlayerActionResult(List<PlayerActionEvent> events) {
}
