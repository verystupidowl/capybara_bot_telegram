package ru.tggc.capybaratelegrambot.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.tggc.capybaratelegrambot.domain.dto.CapybaraInfoDto;
import ru.tggc.capybaratelegrambot.domain.model.Capybara;
import ru.tggc.capybaratelegrambot.domain.model.Work;
import ru.tggc.capybaratelegrambot.domain.model.enums.WorkType;
import ru.tggc.capybaratelegrambot.domain.model.timedaction.WorkAction;
import ru.tggc.capybaratelegrambot.service.TimedActionService;

import static ru.tggc.capybaratelegrambot.utils.Utils.getOr;

@Component
@RequiredArgsConstructor
public class CapybaraInfoMapper {
    private final TimedActionService timedActionService;

    public CapybaraInfoDto toDto(Capybara capybara) {
        Boolean canTea = capybara.getTea().canPerform();
        Boolean canHappiness = capybara.getHappiness().canPerform();
        String happinessTime = timedActionService.getStatus(capybara.getHappiness());
        String teaTime = timedActionService.getStatus(capybara.getTea());
        Work work = capybara.getWork();
        Boolean hasWork = work.getWorkType() != WorkType.NONE;
        Boolean hasBigJob = work != null;
        Boolean canGoWork = null;
        Boolean isWorking = null;
        Boolean canGoBigJob = null;
        Boolean isOnBigJob = null;
        Boolean canTakeFromWork = null;
        String takeFromWork = null;
        Integer rise = null;
        Integer index = null;
        Boolean canTakeFromBigJob = null;
        Integer takeFromBigJob = null;
        Integer bigJobTime = null;
        String workTime = null;
        String improvement = null;
        Boolean canSatiety = capybara.getSatiety().canPerform();
        String satietyTime = timedActionService.getStatus(capybara.getSatiety());
        Boolean canRace = capybara.getCheerfulness().getCheerfulnessLevel().equals(capybara.getCheerfulness().getMaxLevel());
//        Integer raceTime = capybara.getCheerfulness().getNextTime().compareTo(LocalDateTime.now());
        if (capybara.getImprovement() != null) {
            improvement = capybara.getImprovement().getImprovement().getLabel();
        }
        if (Boolean.TRUE.equals(hasWork)) {
            WorkAction workAction = work.getWorkAction();
            canGoWork = getOr(workAction, WorkAction::canPerform, false);
            isWorking = getOr(workAction, WorkAction::isInProgress, false);
            rise = work.getRise();
            index = work.getIndex();
            workTime = timedActionService.getStatus(workAction);
            if (Boolean.TRUE.equals(isWorking)) {
                canTakeFromWork = getOr(workAction, WorkAction::canTakeFrom, false);
                takeFromWork = timedActionService.getStatus(workAction);
            }
        }
//            if (Boolean.TRUE.equals(hasBigJob)) {
//                canGoBigJob = capybara.getBigJob().getNextTime().isBefore(LocalDateTime.now());
//                isOnBigJob = capybara.getBigJob().getIsOnBigJob();
//                if (Boolean.TRUE.equals(isOnBigJob)) {
//                    canTakeFromBigJob = capybara.getBigJob().getTimer().isBefore(LocalDateTime.now());
//                    takeFromBigJob = capybara.getBigJob().getTimer().compareTo(LocalDateTime.now());
//                    bigJobTime = capybara.getBigJob().getNextTime().compareTo(LocalDateTime.now());
//                }
//            }
//        }
        return CapybaraInfoDto.builder()
                .name(capybara.getName())
                .canTea(canTea)
                .isTeaWaiting(capybara.getTea().isWaiting())
                .canHappiness(canHappiness)
                .teaTime(teaTime)
                .hasWork(hasWork)
                .canGoWork(canGoWork)
                .canGoBigJob(canGoBigJob)
                .workTime(workTime)
                .isOnBigJob(isOnBigJob)
                .isWorking(isWorking)
                .canTakeFromWork(canTakeFromWork)
                .takeFromWork(takeFromWork)
                .rise(rise)
                .index(index)
                .canTakeFromBigJob(canTakeFromBigJob)
                .takeFromBigJob(takeFromBigJob)
                .bigJobTime(bigJobTime)
                .satietyTime(satietyTime)
                .canSatiety(canSatiety)
                .happinessTime(happinessTime)
                .canRace(canRace)
//                .raceTime(raceTime)
                .improvement(improvement)
                .build();
    }

}
