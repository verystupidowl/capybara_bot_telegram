package ru.tggc.botapp.formatter.msgkey;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.tggc.telegrambotcore.formatter.MsgKey;

@Getter
@AllArgsConstructor
public enum CommonMsgKey implements MsgKey {
    CHOSEN_RANDOM_PHOTO("capybara.chosen-random-photo"),

    HAPPINESS_COOLDOWN("capybara.happiness.cooldown"),
    HAPPINESS_THINGS("capybara.happiness.things"),

    FEED("capybara.feed.feed"),
    FATTEN("capybara.feed.fatten"),
    FEED_FATEN("capybara.feed.feed-fatten"),

    TEA_WAITING("capybara.tea.waiting"),
    DO_TEA("capybara.tea.do-tea"),

    CAPYBARA_CREATED("capybara.created"),
    MY_CAPYBARA("capybara.my-capybara"),
    START_MESSAGE("capybara.start-message"),
    GREETINGS("capybara.greetings"),
    START_CHANGE_NAME("capybara.start.change-name"),
    START_CHANGE_PHOTO("capybara.start.change-photo"),
    DELETED("capybara.deleted"),
    ALREADY_DOING("capybara.error.already-doing"),

    NEW_LEVEL("capybara.new-level"),
    NEW_TYPE("capybara.new-type"),

    LIST_OF_COMMANDS("capybara.list-of-commands"),
    LIST_OF_IMPROVEMENTS("capybara.list-of-improvements"),
    ;

    private final String key;
}
