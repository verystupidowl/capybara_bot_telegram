package ru.tggc.botapp.domain.dto.info;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public abstract class LongActionInfo extends ActionInfo {
    private boolean canTakeFrom;
    private String timeToTake;
    private boolean isActing;
}
