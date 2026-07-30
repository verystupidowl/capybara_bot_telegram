package ru.tggc.botapp.domain.model.enums.work;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ItIndex implements WorkIndex {
    STUDENT("Обучающийся на программиста"),
    JUNIOR("Джуниор разработчик"),
    MIDDLE("Миддл разработчик"),
    SENIOR("Сеньор разработчик"),
    TEAM_LEAD("Тим лид"),
    DIRECTOR("Директор IT компании");

    private final String label;
}
