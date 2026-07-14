package ru.tggc.botapp.formatter.msgkey;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.tggc.telegrambotcore.formatter.MsgKey;

@Getter
@AllArgsConstructor
public enum CommonMsgKey implements MsgKey {
    CHOSEN_RANDOM_PHOTO("capybara.chosen-random-photo"),
    HAPPINESS_COOLDOWN("capybara.happiness.cooldown"),
    FEED_SUCCESS("capybara.feed.feed"),
    FEED_FATTEN("capybara.feed.fatten"),

    TEA_WAITING("capybara.tea.waiting"),

    CAPYBARA_CREATED("capybara.created"),
    MY_CAPYBARA("capybara.my-capybara"),
    START_MESSAGE("capybara.start-message"),
    GREETINGS("capybara.greetings"),
    START_CHANGE_NAME("capybara.start-change-name"),
    START_CHANGE_PHOTO("capybara.start-change-photo"),
    DELETED("capybara.deleted"),
    ;

    private final String key;
}
