package ru.tggc.capybaratelegrambot.service;

import org.springframework.stereotype.Service;
import ru.tggc.capybaratelegrambot.domain.dto.CapybaraContext;
import ru.tggc.capybaratelegrambot.domain.dto.PhotoDto;
import ru.tggc.capybaratelegrambot.domain.dto.RequestType;
import ru.tggc.capybaratelegrambot.domain.model.Capybara;
import ru.tggc.capybaratelegrambot.domain.model.WeddingRequest;
import ru.tggc.capybaratelegrambot.domain.model.enums.WeddingRequestType;
import ru.tggc.capybaratelegrambot.domain.model.enums.WeddingStatus;
import ru.tggc.capybaratelegrambot.exceptions.CapybaraException;
import ru.tggc.capybaratelegrambot.repository.WeddingRequestRepository;
import ru.tggc.capybaratelegrambot.service.factory.AbstractRequestService;

import java.time.LocalDateTime;

@Service
public class WeddingService extends AbstractRequestService<WeddingRequest> {
    private final WeddingRequestRepository weddingRequestRepository;
    private final CapybaraService capybaraService;

    public WeddingService(CapybaraService capybaraService,
                          UserService userService,
                          WeddingRequestRepository weddingRequestRepository) {
        super(capybaraService, userService);
        this.weddingRequestRepository = weddingRequestRepository;
        this.capybaraService = capybaraService;
    }

    public PhotoDto respondWedding(CapybaraContext ctx, boolean accept) {
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
        return PhotoDto.builder()
                .url("https://vk.com/photo-209917797_457245520")
                .caption(caption)
                .chatId(ctx.chatId())
                .build();
    }

    public String respondUnWedding(CapybaraContext ctx, boolean accept) {
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
        return WeddingRequest.builder()
                .type(WeddingRequestType.WEDDING)
                .createdAt(LocalDateTime.now())
                .status(WeddingStatus.PENDING)
                .target(opponent)
                .proposer(opponent)
                .build();
    }

    @Override
    public RequestType getRequestType() {
        return RequestType.WEDDING;
    }
}
