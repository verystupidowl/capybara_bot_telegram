package ru.tggc.botapp.domain.dto.info;

import lombok.Builder;

@Builder
public record CapybaraInfoDto(
        String name,
        Integer level,
        HappinessInfoDto happiness,
        SatietyInfoDto satiety,
        TeaInfoDto tea,
        WorkInfoDto work,
        RaceInfoDto race,
        BigJobInfoDto bigJob
) {
}
