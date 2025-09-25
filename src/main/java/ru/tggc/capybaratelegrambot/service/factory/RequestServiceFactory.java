package ru.tggc.capybaratelegrambot.service.factory;

import org.springframework.stereotype.Component;
import ru.tggc.capybaratelegrambot.domain.dto.RequestType;
import ru.tggc.capybaratelegrambot.service.RequestService;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class RequestServiceFactory {
    private final Map<RequestType, RequestService> requestServices;

    public RequestServiceFactory(List<RequestService> requestServices) {
        this.requestServices = requestServices.stream()
                .collect(Collectors.toMap(RequestService::getRequestType, Function.identity()));
    }

    public RequestService getRequestService(RequestType requestType) {
        return requestServices.get(requestType);
    }
}
