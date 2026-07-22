package ru.tggc.botapp.keyboard;

import ru.tggc.botapp.domain.dto.FightCapybaraDto;
import ru.tggc.botapp.domain.dto.MyCapybaraDto;
import ru.tggc.botapp.domain.dto.info.CapybaraInfoDto;
import ru.tggc.botapp.domain.model.enums.fight.BuffType;
import ru.tggc.telegrambotcore.keyboard.KeyboardKey;

public record KeyboardType() {
    public static final KeyboardKey<Void> CASINO_TARGET = new KeyboardKey<>("CASINO_TARGET");
    public static final KeyboardKey<CapybaraInfoDto> INFO = new KeyboardKey<>("INFO", CapybaraInfoDto.class);
    public static final KeyboardKey<MyCapybaraDto> MY_CAPYBARA = new KeyboardKey<>("MY_CAPYBARA", MyCapybaraDto.class);
    public static final KeyboardKey<Void> REPLY = new KeyboardKey<>("REPLY");
    public static final KeyboardKey<Void> FIGHT = new KeyboardKey<>("FIGHT");
    public static final KeyboardKey<Void> IMPROVEMENTS = new KeyboardKey<>("IMPROVEMENTS");
    public static final KeyboardKey<Void> TEA = new KeyboardKey<>("TEA");
    public static final KeyboardKey<Void> DELETE_CAPYBARA = new KeyboardKey<>("DELETE_CAPYBARA");
    public static final KeyboardKey<Void> WEDDING = new KeyboardKey<>("WEDDING");
    public static final KeyboardKey<Void> UNWEDDING = new KeyboardKey<>("UNWEDDING");
    public static final KeyboardKey<Void> RACE = new KeyboardKey<>("RACE");
    public static final KeyboardKey<Void> RACE_MASSAGE = new KeyboardKey<>("RACE_MASSAGE");
    public static final KeyboardKey<Void> NOT_CHANGE = new KeyboardKey<>("NOT_CHANGE");
    public static final KeyboardKey<Void> DEFAULT_PHOTO = new KeyboardKey<>("DEFAULT_PHOTO");
    public static final KeyboardKey<Void> NEW_WORK = new KeyboardKey<>("NEW_WORK");
    public static final KeyboardKey<Void> ROBBERY_IMPROVEMENT = new KeyboardKey<>("ROBBERY_IMPROVEMENT");
    public static final KeyboardKey<Void> CASH_REPORT = new KeyboardKey<>("CASH_REPORT");
    public static final KeyboardKey<Void> BIG_IT_PROJECT = new KeyboardKey<>("BIG_IT_PROJECT");
    public static final KeyboardKey<Void> BIG_JOB = new KeyboardKey<>("BIG_JOB");
    public static final KeyboardKey<Void> FEED = new KeyboardKey<>("FEED");
    public static final KeyboardKey<Void> TAKE_CAPYBARA = new KeyboardKey<>("TAKE_CAPYBARA");
    public static final KeyboardKey<Void> TO_MAIN_MENU = new KeyboardKey<>("TO_MAIN_MENU");
    public static final KeyboardKey<FightCapybaraDto> FIGHT_INFO = new KeyboardKey<>("FIGHT_INFO", FightCapybaraDto.class);
    public static final KeyboardKey<Void> FIGHT_BUFF_TYPES = new KeyboardKey<>("FIGHT_BUFF_TYPES");
    public static final KeyboardKey<BuffType> FIGHT_BUFFS = new KeyboardKey<>("FIGHT_BUFFS", BuffType.class);
    public static final KeyboardKey<Void> LEAVE_FIGHT = new KeyboardKey<>("LEAVE_FIGHT");
    public static final KeyboardKey<Void> MAYBE_START_FIGHT = new KeyboardKey<>("MAYBE_START_FIGHT");
    public static final KeyboardKey<Void> ADMIN_MENU = new KeyboardKey<>("ADMIN_MENU");
}
