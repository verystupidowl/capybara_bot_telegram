package ru.tggc.botapp.domain.dto.info;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public abstract class ActionInfo {
    private boolean canAct;
    private String timeToAct;
}
