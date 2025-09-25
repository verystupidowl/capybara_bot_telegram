package ru.tggc.capybaratelegrambot.provider;

import org.springframework.stereotype.Service;
import ru.tggc.capybaratelegrambot.domain.model.enums.WorkType;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class WorkProviderFactory {
    private final Map<WorkType, WorkProvider> jobTypeJobProviderMap;

    public WorkProviderFactory(List<WorkProvider> workProviders) {
        this.jobTypeJobProviderMap = workProviders.stream()
                .collect(Collectors.toMap(WorkProvider::getJobType, Function.identity()));
    }

    public WorkProvider getJobProvider(WorkType workType) {
        return jobTypeJobProviderMap.get(workType);
    }
}
