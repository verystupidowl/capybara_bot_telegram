package ru.tggc.botapp.domain.model.enums.work;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CashierIndex implements WorkIndex {
    INTERN("Стажер"),
    CASHIER("Кассир"),
    MANAGER("Менеджер"),
    STORE_DIRECTOR("Директор магазина"),
    BRANCH_DIRECTOR("Директор филиала"),
    GENERAL_DIRECTOR("Генеральный директор"),
    ;

    private final String label;
}
