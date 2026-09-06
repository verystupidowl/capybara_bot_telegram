package ru.tggc.botapp.service.factory;

import org.springframework.stereotype.Service;
import ru.tggc.botapp.domain.model.enums.work.WorkType;
import ru.tggc.botapp.service.WorkProvider;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class WorkServiceFactory {
    private final Map<WorkType, WorkProvider> workProviders;

    public WorkServiceFactory(List<WorkProvider> workProviders) {
        this.workProviders = workProviders.stream()
                .collect(Collectors.toMap(WorkProvider::getWorkType, Function.identity()));
    }

    public WorkProvider getWorkProvider(WorkType workType) {
        return workProviders.get(workType);
    }
}
