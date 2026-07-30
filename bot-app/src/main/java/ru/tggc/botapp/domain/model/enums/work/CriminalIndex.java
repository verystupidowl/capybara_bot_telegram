package ru.tggc.botapp.domain.model.enums.work;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CriminalIndex implements WorkIndex {
    PICKPOCKET("Карманник"),
    PRETTY_THIEF("Мелкий воришка"),
    ROBBER("Грабитель"),
    GANGSTER("Гангстер"),
    CRIMINAL_AUTHORITY("Криминальный авторитет"),
    MAFIA_BOSS("Босс мафии")
    ,;

    private final String label;
}
