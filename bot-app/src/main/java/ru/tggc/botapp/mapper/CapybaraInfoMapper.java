package ru.tggc.botapp.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.tggc.botapp.domain.dto.info.ActionInfo;
import ru.tggc.botapp.domain.dto.info.BigJobInfoDto;
import ru.tggc.botapp.domain.dto.info.CapybaraInfoDto;
import ru.tggc.botapp.domain.dto.info.HappinessInfoDto;
import ru.tggc.botapp.domain.dto.info.LongActionInfo;
import ru.tggc.botapp.domain.dto.info.RaceInfoDto;
import ru.tggc.botapp.domain.dto.info.SatietyInfoDto;
import ru.tggc.botapp.domain.dto.info.TeaInfoDto;
import ru.tggc.botapp.domain.dto.info.WorkInfoDto;
import ru.tggc.botapp.domain.model.Capybara;
import ru.tggc.botapp.domain.model.Work;
import ru.tggc.botapp.domain.model.enums.WorkType;
import ru.tggc.botapp.domain.model.timedaction.LongTimedAction;
import ru.tggc.botapp.domain.model.timedaction.TimedAction;
import ru.tggc.botapp.service.TimedActionService;

import java.util.function.Consumer;
import java.util.function.Supplier;

@Component
@RequiredArgsConstructor
public class CapybaraInfoMapper {
    private final TimedActionService timedActionService;

    public CapybaraInfoDto toDto(Capybara capybara) {
        WorkInfoDto workInfoDto = new WorkInfoDto(false);
        BigJobInfoDto bigJobInfoDto = new BigJobInfoDto();

        if (capybara.getWork().getWorkType() != WorkType.NONE) {
            Work work = capybara.getWork();
            workInfoDto = mapLongAction(work.getWorkAction(), WorkInfoDto::new, w -> {
                w.setHasWork(true);
                w.setRise(work.getRise());
                w.setIndex(work.getIndex());
            });

            bigJobInfoDto = mapLongAction(capybara.getWork().getBigJob().getBigJobAction(), BigJobInfoDto::new);
        }
        RaceInfoDto race = mapActionInfo(
                capybara.getRace().getRaceAction(),
                RaceInfoDto::new,
                r -> r.setImprovement(capybara.getImprovement().getImprovementValue().getLabel()));
        TeaInfoDto tea = mapActionInfo(
                capybara.getTea(),
                TeaInfoDto::new,
                t -> t.setWaiting(capybara.getTea().isWaiting())
        );

        return CapybaraInfoDto.builder()
                .name(capybara.getName())
                .level(capybara.getLevel().getValue())
                .happiness(mapActionInfo(capybara.getHappiness(), HappinessInfoDto::new))
                .satiety(mapActionInfo(capybara.getSatiety(), SatietyInfoDto::new))
                .tea(tea)
                .work(workInfoDto)
                .race(race)
                .bigJob(bigJobInfoDto)
                .build();
    }

    private <T extends ActionInfo> T mapActionInfo(TimedAction action, Supplier<T> factory, Consumer<T> customizer) {
        T actionInfo = factory.get();

        actionInfo.setCanAct(action.canPerform());
        actionInfo.setTimeToAct(timedActionService.getStatus(action));

        customizer.accept(actionInfo);

        return actionInfo;
    }

    private <T extends ActionInfo> T mapActionInfo(TimedAction action, Supplier<T> factory) {
        return mapActionInfo(action, factory, _ -> {
        });
    }

    private <T extends LongActionInfo> T mapLongAction(LongTimedAction action, Supplier<T> factory, Consumer<T> customizer) {
        T actionInfo = factory.get();
        actionInfo.setActing(action.isInProgress());
        actionInfo.setCanAct(action.canPerform());
        actionInfo.setTimeToAct(timedActionService.getStatus(action));

        if (action.isInProgress()) {
            actionInfo.setTimeToTake(timedActionService.getStatus(action));
            actionInfo.setCanTakeFrom(action.canTakeFrom());
        }

        customizer.accept(actionInfo);

        return actionInfo;
    }

    private <T extends LongActionInfo> T mapLongAction(LongTimedAction action, Supplier<T> factory) {
        return mapLongAction(action, factory, _ -> {
        });
    }
}
