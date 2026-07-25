package ru.tggc.botapp.formatter.msgkey;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.tggc.botapp.domain.model.enums.ImprovementValue;
import ru.tggc.telegrambotcore.formatter.MsgKey;

@Getter
@RequiredArgsConstructor
public enum RaceMsgKey implements MsgKey {
    MASSAGE("race.massage"),
    START_RACE("race.start-race"),
    IMPROVEMENT_BOOTS("race.improvement.boots"),
    IMPROVEMENT_WATERMELON("race.improvement.watermelon"),
    IMPROVEMENT_ANTI_LOSE("race.improvement.anti-lose"),
    OPPONENT_HAS_NO_CAPY("race.opponent-has-no-capy"),

    STARTING_RACE("race.running.starting-race"),
    RUNNING_RACE("race.running.running-race"),
    WIN("race.running.win"),
    DRAW("race.running.draw"),
    ;

    private final String key;

    public static RaceMsgKey getByImprovement(ImprovementValue improvement) {
        return switch (improvement) {
            case BOOTS -> IMPROVEMENT_BOOTS;
            case WATERMELON ->  IMPROVEMENT_WATERMELON;
            case ANTI_LOSE ->  IMPROVEMENT_ANTI_LOSE;
            default -> throw new IllegalArgumentException("Improvement not recognized");
        };
    }
}
