package ru.tggc.botapp.keyboard;

import ru.tggc.botapp.domain.dto.CapybaraInfoDto;
import ru.tggc.botapp.domain.dto.FightCapybaraDto;
import ru.tggc.botapp.domain.dto.MyCapybaraDto;
import ru.tggc.botapp.domain.model.enums.fight.BuffType;

public record KeyboardKey<T>(KeyboardType type, Class<T> dataType) {
    public static final KeyboardKey<Void> CASINO_TARGET = new KeyboardKey<>(KeyboardType.CASINO_TARGET, Void.class);
    public static final KeyboardKey<CapybaraInfoDto> INFO = new KeyboardKey<>(KeyboardType.INFO, CapybaraInfoDto.class);
    public static final KeyboardKey<MyCapybaraDto> MY_CAPYBARA = new KeyboardKey<>(KeyboardType.MY_CAPYBARA, MyCapybaraDto.class);
    public static final KeyboardKey<Void> REPLY = new KeyboardKey<>(KeyboardType.REPLY, Void.class);
    public static final KeyboardKey<Void> FIGHT = new KeyboardKey<>(KeyboardType.FIGHT, Void.class);
    public static final KeyboardKey<Void> IMPROVEMENTS = new KeyboardKey<>(KeyboardType.IMPROVEMENTS, Void.class);
    public static final KeyboardKey<Void> TEA = new KeyboardKey<>(KeyboardType.TEA, Void.class);
    public static final KeyboardKey<Void> DELETE_CAPYBARA = new KeyboardKey<>(KeyboardType.DELETE_CAPYBARA, Void.class);
    public static final KeyboardKey<Void> WEDDING = new KeyboardKey<>(KeyboardType.WEDDING, Void.class);
    public static final KeyboardKey<Void> UNWEDDING = new KeyboardKey<>(KeyboardType.UNWEDDING, Void.class);
    public static final KeyboardKey<Void> RACE = new KeyboardKey<>(KeyboardType.RACE, Void.class);
    public static final KeyboardKey<Void> RACE_MASSAGE = new KeyboardKey<>(KeyboardType.RACE_MASSAGE, Void.class);
    public static final KeyboardKey<Void> NOT_CHANGE = new KeyboardKey<>(KeyboardType.NOT_CHANGE, Void.class);
    public static final KeyboardKey<Void> DEFAULT_PHOTO = new KeyboardKey<>(KeyboardType.DEFAULT_PHOTO, Void.class);
    public static final KeyboardKey<Void> NEW_WORK = new KeyboardKey<>(KeyboardType.NEW_WORK, Void.class);
    public static final KeyboardKey<Void> ROBBERY_IMPROVEMENT = new KeyboardKey<>(KeyboardType.ROBBERY_IMPROVEMENT, Void.class);
    public static final KeyboardKey<Void> CASH_REPORT = new KeyboardKey<>(KeyboardType.CASH_REPORT, Void.class);
    public static final KeyboardKey<Void> BIG_IT_PROJECT = new KeyboardKey<>(KeyboardType.BIG_IT_PROJECT, Void.class);
    public static final KeyboardKey<Void> BIG_JOB = new KeyboardKey<>(KeyboardType.BIG_JOB, Void.class);
    public static final KeyboardKey<Void> FEED = new KeyboardKey<>(KeyboardType.FEED, Void.class);
    public static final KeyboardKey<Void> TAKE_CAPYBARA = new KeyboardKey<>(KeyboardType.TAKE_CAPYBARA, Void.class);
    public static final KeyboardKey<Void> TO_MAIN_MENU = new KeyboardKey<>(KeyboardType.TO_MAIN_MENU, Void.class);
    public static final KeyboardKey<FightCapybaraDto> FIGHT_INFO = new KeyboardKey<>(KeyboardType.FIGHT_INFO, FightCapybaraDto.class);
    public static final KeyboardKey<Void> FIGHT_BUFF_TYPES = new KeyboardKey<>(KeyboardType.FIGHT_BUFF_TYPES, Void.class);
    public static final KeyboardKey<BuffType> FIGHT_BUFFS = new KeyboardKey<>(KeyboardType.FIGHT_BUFFS, BuffType.class);
    public static final KeyboardKey<Void> LEAVE_FIGHT = new KeyboardKey<>(KeyboardType.LEAVE_FIGHT, Void.class);
    public static final KeyboardKey<Void> MAYBE_START_FIGHT = new KeyboardKey<>(KeyboardType.MAYBE_START_FIGHT, Void.class);
    public static final KeyboardKey<Void> ADMIN_MENU = new KeyboardKey<>(KeyboardType.ADMIN_MENU, Void.class);
}
