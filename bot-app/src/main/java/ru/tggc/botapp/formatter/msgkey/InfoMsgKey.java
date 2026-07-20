package ru.tggc.botapp.formatter.msgkey;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.tggc.telegrambotcore.formatter.MsgKey;

@Getter
@RequiredArgsConstructor
public enum InfoMsgKey implements MsgKey {
    STATUS_READY("capybara.info.common.status.ready"),
    STATUS_COOLDOWN("capybara.info.common.status.cooldown"),
    CAPYBARA_HEADER("capybara.info.header"),
    CAPYBARA_NAME("capybara.info.name"),
    CAPYBARA_TEA("capybara.info.tea"),
    CAPYBARA_WORK_GO("capybara.info.work.go-work"),
    CAPYBARA_WORK_TAKE("capybara.info.work.take-from-work"),
    CAPYBARA_WORK_RISE("capybara.info.work.rise"),
    CAPYBARA_BIG_JOB_GO("capybara.info.work.big-work.go-big-work"),
    CAPYBARA_BIG_JOB_TAKE("capybara.info.work.big-work.take-from-big-work"),
    CAPYBARA_FEED("capybara.info.feed"),
    CAPYBARA_HAPPINESS("capybara.info.happiness"),
    CAPYBARA_RACE_CHARGES("capybara.info.race.charges"),
    CAPYBARA_RACE_IMPROVEMENTS("capybara.info.race.improvements"),
    ;
    private final String key;
}
