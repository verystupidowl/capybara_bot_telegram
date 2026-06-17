package ru.tggc.botapp.formatter.msgkey;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CommonMsgKey implements MsgKey {
    CAPYBARA_CHOSEN_RANDOM_PHOTO("capybara.chosen-random-photo"),
    CAPYBARA_HAPPINESS_COOLDOWN("capybara.happiness.cooldown"),
    CAPYBARA_FEED_SUCCESS("capybara.feed.feed"),
    CAPYBARA_FEED_FATTEN("capybara.feed.fatten"),

    CAPYBARA_TEA_WAITING("capybara.tea.waiting"),

    CAPYBARA_CREATED("capybara.created"),

    ;

    private final String key;
}
