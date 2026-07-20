package ru.tggc.botapp.formatter.msgkey;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.tggc.telegrambotcore.formatter.MsgKey;

@Getter
@AllArgsConstructor
public enum ErrorMsgKey implements MsgKey {
    CAPYBARA_FEED_COOLDOWN("capybara.feed.cooldown"),
    CAPYBARA_TEA_COOLDOWN("capybara.tea.cooldown"),
    CAPYBARA_TEA_ALREADY_WAITING("capybara.tea.already-waiting"),
    CAPYBARA_NAME_TOO_LONG("capybara.error.name-too-long"),
    CAPYBARA_FIGHT_ONLY_ONE("fight.only-one"),
    CAPYBARA_ALREADY_HAS_IMPROVEMENT("race.improvement.already-has-improvement"),
    CAPYBARA_NOT_FOUND("capybara.error.not-found"),
    ALREADY_HAVE("capybara.error.already-have"),

    USER_USERNAME_NOT_FOUND("capybara.error.username-not-found"),

    CASINO_NOT_PLAYING("casino.error.not-playing"),
    CASINO_MIN_BET("casino.error.min-bet"),
    NO_MONEY("capybara.error.no-money");

    private final String key;
}
