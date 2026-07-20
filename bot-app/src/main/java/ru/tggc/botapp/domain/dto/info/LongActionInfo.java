package ru.tggc.botapp.domain.dto.info;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public abstract class LongActionInfo extends ActionInfo {
    private boolean canTakeFrom;
    private String timeToTake;
    private boolean isActing;
}
