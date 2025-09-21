package ru.tggc.capybaratelegrambot.service.impl;

import org.springframework.stereotype.Service;
import ru.tggc.capybaratelegrambot.domain.dto.PhotoDto;
import ru.tggc.capybaratelegrambot.domain.dto.RequestType;
import ru.tggc.capybaratelegrambot.domain.model.Capybara;
import ru.tggc.capybaratelegrambot.domain.model.WeddingRequest;
import ru.tggc.capybaratelegrambot.domain.model.enums.WeddingRequestType;
import ru.tggc.capybaratelegrambot.domain.model.enums.WeddingStatus;
import ru.tggc.capybaratelegrambot.exceptions.CapybaraException;
import ru.tggc.capybaratelegrambot.repository.WeddingRequestRepository;
import ru.tggc.capybaratelegrambot.service.CapybaraService;
import ru.tggc.capybaratelegrambot.service.UserService;
import ru.tggc.capybaratelegrambot.service.WeddingService;
import ru.tggc.capybaratelegrambot.service.factory.AbstractRequestService;

import java.time.LocalDateTime;

@Service
public class WeddingServiceImpl extends AbstractRequestService<WeddingRequest> implements WeddingService {
    private final WeddingRequestRepository weddingRequestRepository;
    private final CapybaraService capybaraService;

    public WeddingServiceImpl(CapybaraService capybaraService,
                              UserService userService,
                              WeddingRequestRepository weddingRequestRepository) {
        super(capybaraService, userService);
        this.weddingRequestRepository = weddingRequestRepository;
        this.capybaraService = capybaraService;
    }

    @Override
    public PhotoDto respondWedding(String userId, String chatId, boolean accept) {
        Capybara accepter = capybaraService.getCapybaraByUserId(userId, chatId);
        WeddingRequest request = weddingRequestRepository.findByTargetIdAndStatusAndType(
                        accepter.getId(),
                        WeddingStatus.PENDING,
                        WeddingRequestType.WEDDING
                )
                .orElseThrow(() -> new CapybaraException("No pending wedding proposal!"));

        Capybara proposer = capybaraService.getCapybara(request.getProposer().getId(), chatId);

        String caption;
        if (accept) {
            request.setStatus(WeddingStatus.ACCEPTED);
            accepter.setSpouseId(proposer.getId());
            proposer.setSpouseId(accepter.getId());
            caption = "Ура! " + accepter.getName() + " и " + proposer.getName() + " теперь женаты!";
        } else {
            request.setStatus(WeddingStatus.DECLINED);
            caption = accepter.getName() + " отклонил предложение.";
        }

        weddingRequestRepository.save(request);
        return PhotoDto.builder()
                .url(//todo)
                .caption(caption)
                .chatId(chatId)
                .build();
    }

    @Override
    public String respondUnWedding(String userId, String chatId, boolean accept) {
        Capybara accepter = capybaraService.getCapybaraByUserId(userId, chatId);
        WeddingRequest request = weddingRequestRepository.findByTargetIdAndStatusAndType(
                        accepter.getId(),
                        WeddingStatus.PENDING,
                        WeddingRequestType.UNWEDDING
                )
                .orElseThrow(() -> new CapybaraException("No pending wedding proposal!"));

        Capybara proposer = capybaraService.getCapybara(request.getProposer().getId(), chatId);

        String message;
        if (accept) {
            request.setStatus(WeddingStatus.ACCEPTED);
            accepter.setSpouseId(null);
            proposer.setSpouseId(null);
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
