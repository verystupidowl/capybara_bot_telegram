package ru.tggc.capybaratelegrambot.service.factory;

import org.springframework.stereotype.Service;
import ru.tggc.capybaratelegrambot.domain.model.enums.WorkType;
import ru.tggc.capybaratelegrambot.service.WorkService;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class WorkServiceFactory {
    private final Map<WorkType, WorkService> jobTypeJobProviderMap;

    public WorkServiceFactory(List<WorkService> workServices) {
        this.jobTypeJobProviderMap = workServices.stream()
                .collect(Collectors.toMap(WorkService::getJobType, Function.identity()));
    }

    public WorkService getJobProvider(WorkType workType) {
        return jobTypeJobProviderMap.get(workType);
    }
}
