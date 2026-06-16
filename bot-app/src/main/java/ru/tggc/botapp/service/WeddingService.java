package ru.tggc.botapp.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.tggc.botapp.domain.dto.RequestType;
import ru.tggc.botapp.domain.model.Capybara;
import ru.tggc.botapp.domain.model.WeddingRequest;
import ru.tggc.botapp.domain.model.enums.WeddingRequestType;
import ru.tggc.botapp.domain.model.enums.WeddingStatus;
import ru.tggc.botapp.exceptions.CapybaraException;
import ru.tggc.botapp.repository.WeddingRequestRepository;
import ru.tggc.botapp.service.factory.AbstractRequestService;
import ru.tggc.botapp.service.impl.UserServiceImpl;
import ru.tggc.telegrambotframework.dto.PhotoDto;
import ru.tggc.telegrambotframework.dto.UpdateContext;

import java.time.LocalDateTime;

import static ru.tggc.telegrambotframework.util.Utils.throwIf;


@Service
public class WeddingService extends AbstractRequestService<WeddingRequest> {
    private final WeddingRequestRepository weddingRequestRepository;
    private final CapybaraService capybaraService;

    public WeddingService(CapybaraService capybaraService,
                          UserServiceImpl userService,
                          WeddingRequestRepository weddingRequestRepository) {
        super(capybaraService, userService);
        this.weddingRequestRepository = weddingRequestRepository;
        this.capybaraService = capybaraService;
    }

    @Transactional
    public PhotoDto respondWedding(UpdateContext ctx, boolean accept) {
        Capybara accepter = capybaraService.getCapybaraByContext(ctx);
        WeddingRequest request = weddingRequestRepository.findByTargetIdAndStatusAndType(
                        accepter.getId(),
                        WeddingStatus.PENDING,
                        WeddingRequestType.WEDDING
                )
                .orElseThrow(() -> new CapybaraException("No pending wedding proposal!"));

        Capybara proposer = capybaraService.getCapybara(request.getProposer().getId());

        String caption;
        if (accept) {
            request.setStatus(WeddingStatus.ACCEPTED);
            accepter.setSpouse(proposer);
            proposer.setSpouse(accepter);
            caption = "Ура! " + accepter.getName() + " и " + proposer.getName() + " теперь женаты!";
        } else {
            request.setStatus(WeddingStatus.DECLINED);
            caption = accepter.getName() + " отклонил предложение.";
        }

        weddingRequestRepository.save(request);
        capybaraService.save(proposer);
        capybaraService.save(accepter);
        return PhotoDto.builder()
                .url("https://vk.com/photo-209917797_457245520")
                .caption(caption)
                .chatId(ctx.chatId())
                .build();
    }

    @Transactional
    public String respondUnWedding(UpdateContext ctx, boolean accept) {
        Capybara accepter = capybaraService.getCapybaraByContext(ctx);
        WeddingRequest request = weddingRequestRepository.findByTargetIdAndStatusAndType(
                        accepter.getId(),
                        WeddingStatus.PENDING,
                        WeddingRequestType.UNWEDDING
                )
                .orElseThrow(() -> new CapybaraException("No pending wedding proposal!"));

        Capybara proposer = capybaraService.getCapybara(request.getProposer().getId());

        String message;
        if (accept) {
            request.setStatus(WeddingStatus.ACCEPTED);
            accepter.setSpouse(null);
            proposer.setSpouse(null);
            message = accepter.getName() + " и " + proposer.getName() + " теперь разведены!";
        } else {
            request.setStatus(WeddingStatus.DECLINED);
            message = accepter.getName() + " отклонил предложение.";
        }

        weddingRequestRepository.save(request);

        return message;
    }

    @Override
    protected void saveRequest(Capybara challenger, Capybara opponent, WeddingRequest request) {
        weddingRequestRepository.save(request);
    }

    @Override
    protected WeddingRequest getRequest(Capybara challenger, Capybara opponent) {
        boolean alreadyHasRequest = weddingRequestRepository.existsByProposerOrTarget(challenger, opponent);
        throwIf(alreadyHasRequest, () -> new CapybaraException("already has a challenge"));
        return WeddingRequest.builder()
                .type(WeddingRequestType.WEDDING)
                .createdAt(LocalDateTime.now())
                .status(WeddingStatus.PENDING)
                .target(opponent)
                .proposer(challenger)
                .build();
    }

    @Override
    public RequestType getRequestType() {
        return RequestType.WEDDING;
    }
}
