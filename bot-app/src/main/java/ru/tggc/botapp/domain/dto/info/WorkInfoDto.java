package ru.tggc.botapp.domain.dto.info;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class WorkInfoDto extends LongActionInfo {
    private Boolean hasWork;
    private Integer rise;
    private Integer index;

    public WorkInfoDto(boolean hasWork) {
        this.hasWork = hasWork;
    }
}
