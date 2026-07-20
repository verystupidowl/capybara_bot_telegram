package ru.tggc.botapp.formatter.common;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.tggc.botapp.domain.dto.MyCapybaraDto;
import ru.tggc.botapp.domain.dto.info.BigJobInfoDto;
import ru.tggc.botapp.domain.dto.info.CapybaraInfoDto;
import ru.tggc.botapp.domain.dto.info.HappinessInfoDto;
import ru.tggc.botapp.domain.dto.info.RaceInfoDto;
import ru.tggc.botapp.domain.dto.info.SatietyInfoDto;
import ru.tggc.botapp.domain.dto.info.TeaInfoDto;
import ru.tggc.botapp.domain.dto.info.WorkInfoDto;
import ru.tggc.botapp.formatter.msgkey.CommonMsgKey;
import ru.tggc.botapp.formatter.msgkey.InfoMsgKey;
import ru.tggc.telegrambotcore.formatter.FormatService;
import ru.tggc.telegrambotcore.formatter.MessageBuilder;

import java.util.function.Function;

import static ru.tggc.telegrambotcore.util.Utils.getOrElse;
import static ru.tggc.telegrambotcore.util.Utils.renderStaminaBar;

@Component
@RequiredArgsConstructor
@Slf4j
public class CapybaraFormatter {
    private final FormatService formatService;

    public String getMyCapybara(MyCapybaraDto capybara) {
        Object[] params = {
                capybara.name(),
                capybara.level().toString(),
                capybara.type(),
                renderStaminaBar(capybara.stamina()),
                capybara.job(),
                String.valueOf(capybara.currency()),
                getOrElse(capybara.wedding(), Function.identity(), "Нет"),
                capybara.satietyLevel() + "/" + capybara.satietyMaxLevel(),
                capybara.happinessLevel() + "/" + capybara.happinessMaxLevel()
        };
        return formatService.get(CommonMsgKey.MY_CAPYBARA, params);
    }

    @SuppressWarnings("unchecked")
    public String getCapybaraInfo(CapybaraInfoDto capybara) {
        SatietyInfoDto satiety = capybara.satiety();
        HappinessInfoDto happiness = capybara.happiness();
        WorkInfoDto work = capybara.work();
        RaceInfoDto race = capybara.race();
        return MessageBuilder.create()
                .line(formatService.get(InfoMsgKey.CAPYBARA_HEADER))
                .empty()
                .line(formatService.get(InfoMsgKey.CAPYBARA_NAME, capybara.name()))
                .line(formatService.get(InfoMsgKey.CAPYBARA_TEA, getTeaStatus(capybara.tea())))
                .lines(
                        work.getHasWork(),
                        () -> getWorkStatus(capybara.work()),
                        () -> formatService.get(InfoMsgKey.CAPYBARA_WORK_RISE, work.getRise(), (work.getIndex() + 1) * 10)
                )
                .line(formatService.get(InfoMsgKey.CAPYBARA_FEED, availability(satiety.isCanAct(), satiety.getTimeToAct())))
                .line(formatService.get(InfoMsgKey.CAPYBARA_HAPPINESS, availability(happiness.isCanAct(), happiness.getTimeToAct())))
                .line(
                        !race.isCanAct(),
                        () -> formatService.get(InfoMsgKey.CAPYBARA_RACE_CHARGES, race.getTimeToAct())
                )
                .line(formatService.get(InfoMsgKey.CAPYBARA_RACE_IMPROVEMENTS, race.getImprovement()))
                .build();
    }

    private String getTeaStatus(TeaInfoDto tea) {
        if (!tea.isWaiting()) {
            return availability(tea.isCanAct(), tea.getTimeToAct());
        }
        return "в ожидании собеседника";
    }

    private String getWorkStatus(WorkInfoDto work) {
        if (!work.isActing()) {
            return formatService.get(InfoMsgKey.CAPYBARA_WORK_GO, availability(work.isCanAct(), work.getTimeToAct()));
        }
        return formatService.get(InfoMsgKey.CAPYBARA_WORK_TAKE, availability(work.isCanTakeFrom(), work.getTimeToTake()));
    }

    //todo будет в будущем
    private String getBigJobStatus(BigJobInfoDto bigJob) {
        if (bigJob == null) {
            return null;
        }
        if (bigJob.isActing()) {
            return formatService.get(InfoMsgKey.CAPYBARA_BIG_JOB_TAKE, availability(bigJob.isCanTakeFrom(), bigJob.getTimeToTake()));
        }
        return formatService.get(InfoMsgKey.CAPYBARA_BIG_JOB_GO, availability(bigJob.isCanAct(), bigJob.getTimeToAct()));
    }

    private String availability(Boolean condition, String time) {
        if (Boolean.TRUE.equals(condition)) {
            return formatService.get(InfoMsgKey.STATUS_READY);
        }
        return formatService.get(InfoMsgKey.STATUS_COOLDOWN, time);
    }
}
