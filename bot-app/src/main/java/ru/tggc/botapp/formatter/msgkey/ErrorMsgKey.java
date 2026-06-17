package ru.tggc.botapp.formatter.msgkey;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorMsgKey implements MsgKey {
    CAPYBARA_FEED_COOLDOWN("capybara.feed.cooldown"),
    CAPYBARA_TEA_COOLDOWN("capybara.tea.cooldown"),
    CAPYBARA_TEA_ALREADY_WAITING("capybara.tea.already-waiting"),
    CAPYBARA_NAME_TOO_LONG("capybara.name.too-long"),
    CAPYBARA_FIGHT_ONLY_ONE("capybara.fight.only-one"),
    CAPYBARA_ALREADY_HAS_IMPROVEMENT("capybara.error.race.already-has-improvement"),

    USER_USERNAME_NOT_FOUND("user.username-not-found"),

    CASINO_NOT_PLAYING("casino.error.not-playing"),
    CASINO_MIN_BET("casino.error.min-bet"),
    ;

    private final String key;
}
