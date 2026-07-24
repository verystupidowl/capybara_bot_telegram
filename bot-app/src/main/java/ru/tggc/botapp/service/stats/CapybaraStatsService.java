package ru.tggc.botapp.service.stats;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.tggc.botapp.domain.dto.StatKey;
import ru.tggc.botapp.domain.model.Capybara;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class CapybaraStatsService {
    private final Map<StatKey<?>, CapybaraStats<?>> capybaraStats;

    public CapybaraStatsService(List<CapybaraStats<?>> capybaraStats) {
        this.capybaraStats = capybaraStats.stream()
                .collect(Collectors.toMap(CapybaraStats::getStatKey, Function.identity()));
    }

    @SuppressWarnings("unchecked")
    public <T> CapybaraStats<T> getStats(StatKey<T> statKey) {
        return (CapybaraStats<T>) capybaraStats.get(statKey);
    }

    @Transactional
    public <T> void modify(Capybara capybara, StatKey<T> statKey, Integer value) {
        CapybaraStats<T> stats = getStats(statKey);
        stats.modify(capybara, value);
    }
}
