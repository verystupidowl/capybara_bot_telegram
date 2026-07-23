package ru.tggc.botapp.mapper;

import org.springframework.stereotype.Component;
import ru.tggc.botapp.domain.dto.info.BigJobInfoDto;
import ru.tggc.botapp.domain.dto.info.CapybaraInfoDto;
import ru.tggc.botapp.domain.dto.info.HappinessInfoDto;
import ru.tggc.botapp.domain.dto.info.RaceInfoDto;
import ru.tggc.botapp.domain.dto.info.SatietyInfoDto;
import ru.tggc.botapp.domain.dto.info.TeaInfoDto;
import ru.tggc.botapp.domain.dto.info.WeddingGiftInfoDto;
import ru.tggc.botapp.domain.dto.info.WorkInfoDto;
import ru.tggc.botapp.domain.model.Capybara;
import ru.tggc.botapp.domain.model.Work;
import ru.tggc.botapp.domain.model.enums.WorkType;
import ru.tggc.botapp.service.TimedActionService;
import ru.tggc.telegrambotcore.util.Utils;

@Component
public class CapybaraInfoMapper extends AbstractMapper<Capybara, CapybaraInfoDto> {

    public CapybaraInfoMapper(TimedActionService timedActionService) {
        super(timedActionService);
    }

    public CapybaraInfoDto toDto(Capybara capybara) {
        WorkInfoDto workInfo = new WorkInfoDto(false);
        BigJobInfoDto bigJobInfo = new BigJobInfoDto();
        Work work = capybara.getWork();

        if (work.getWorkType() != WorkType.NONE) {
            workInfo = mapLongAction(work.getWorkAction(), WorkInfoDto::new, w -> {
                w.setHasWork(true);
                w.setRise(work.getRise());
                w.setIndex(work.getIndex());
            });

            if (work.getBigJob().isActive()) {
                bigJobInfo = mapLongAction(
                        work.getBigJob().getBigJobAction(),
                        BigJobInfoDto::new
                );
            }
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

        WeddingGiftInfoDto weddingGift = Utils.getOrNull(
                capybara.getWeddingGift(),
                wg -> mapActionInfo(wg, WeddingGiftInfoDto::new)
        );

        return CapybaraInfoDto.builder()
                .name(capybara.getName())
                .level(capybara.getLevel().getValue())
                .happiness(mapActionInfo(capybara.getHappiness(), HappinessInfoDto::new))
                .satiety(mapActionInfo(capybara.getSatiety(), SatietyInfoDto::new))
                .weddingGift(weddingGift)
                .tea(tea)
                .work(workInfo)
                .race(race)
                .bigJob(bigJobInfo)
                .build();
    }
}
