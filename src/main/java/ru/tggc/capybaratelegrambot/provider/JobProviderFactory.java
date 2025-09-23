package ru.tggc.capybaratelegrambot.provider;

import org.springframework.stereotype.Service;
import ru.tggc.capybaratelegrambot.domain.model.enums.WorkType;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class JobProviderFactory {
    private final Map<WorkType, JobProvider> jobTypeJobProviderMap;

    public JobProviderFactory(List<JobProvider> jobProviders) {
        this.jobTypeJobProviderMap = jobProviders.stream()
                .collect(Collectors.toMap(JobProvider::getJobType, Function.identity()));
    }

    public JobProvider getJobProvider(WorkType workType) {
        return jobTypeJobProviderMap.get(workType);
    }
}
