package ru.tggc.botapp.service.capybara;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.tggc.botapp.domain.model.Capybara;
import ru.tggc.botapp.exceptions.CapybaraException;
import ru.tggc.botapp.domain.model.enums.work.WorkType;
import ru.tggc.botapp.formatter.msgkey.WorkMsgKey;
import ru.tggc.botapp.keyboard.KeyboardType;
import ru.tggc.botapp.service.PhotoService;
import ru.tggc.botapp.service.WorkProvider;
import ru.tggc.botapp.service.factory.WorkServiceFactory;
import ru.tggc.telegrambotcore.dto.PhotoDto;
import ru.tggc.telegrambotcore.dto.UpdateContext;
import ru.tggc.telegrambotcore.formatter.FormatService;
import ru.tggc.telegrambotcore.keyboard.KeyboardFactory;

@Slf4j
@Service
@RequiredArgsConstructor
public class CapybaraWorkService {
    private final CapybaraQueryService queryService;
    private final WorkServiceFactory workServiceFactory;
    private final PhotoService photoService;
    private final FormatService formatService;
    private final KeyboardFactory keyboardFactory;

    @Transactional
    public String takeFromWork(UpdateContext ctx) {
        Capybara capybara = queryService.getWorkCapybaraForUpdate(ctx);
        requireJob(capybara);
        WorkProvider workProvider = workServiceFactory.getWorkProvider(capybara.getWork().getWorkType());
        String messages = workProvider.takeFromWork(capybara);
        queryService.save(capybara);
        return messages;
    }

    @Transactional
    public PhotoDto goJob(UpdateContext ctx) {
        Capybara capybara = queryService.getWorkCapybaraForUpdate(ctx);
        requireJob(capybara);
        WorkType workType = capybara.getWork().getWorkType();
        WorkProvider workProvider = workServiceFactory.getWorkProvider(workType);
        workProvider.goWork(capybara);
        queryService.save(capybara);
        String photoUrl = photoService.getGoWorkPhoto(workType);
        return PhotoDto.builder()
                .url(photoUrl)
                .chatId(ctx.chatId())
                .caption(formatService.get(WorkMsgKey.GO_WORK))
                .markup(keyboardFactory.getKeyboardInline(KeyboardType.TO_MAIN_MENU))
                .build();
    }

    @Transactional
    public String setJob(UpdateContext ctx, WorkType workType) {
        if (workType == null || workType == WorkType.NONE) {
            throw new CapybaraException(formatService.get(WorkMsgKey.ERROR_HAS_NO_WORK));
        }
        Capybara capybara = queryService.getWorkCapybaraForUpdate(ctx);

        WorkProvider workProvider = workServiceFactory.getWorkProvider(workType);
        String photoUrl = workProvider.setWork(capybara);
        queryService.save(capybara);
        return photoUrl;
    }

    @Transactional(readOnly = true)
    public boolean hasWork(UpdateContext ctx) {
        Capybara workCapybara = queryService.getWorkCapybara(ctx);
        return workCapybara.getWork().hasWork();
    }

    @Transactional
    public void dismissal(UpdateContext ctx) {
        Capybara capybara = queryService.getWorkCapybaraForUpdate(ctx);
        requireJob(capybara);
        if (capybara.getWork().getWorkAction().isInProgress()) {
            throw new CapybaraException("Сначала забери капибару с работы");
        }
        WorkProvider workProvider = workServiceFactory.getWorkProvider(capybara.getWork().getWorkType());
        workProvider.dismissal(capybara);
        queryService.save(capybara);
    }

    private void requireJob(Capybara capybara) {
        if (capybara.getWork() == null || !capybara.getWork().hasWork()) {
            throw new CapybaraException(formatService.get(WorkMsgKey.ERROR_HAS_NO_WORK));
        }
    }
}
