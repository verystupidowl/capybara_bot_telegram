package ru.tggc.botapp.domain.dto.info;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@Getter
@Setter
@ToString
public class WorkInfoDto extends LongActionInfo {
    private Boolean hasWork;
    private Integer rise;
    private Integer index;

    public WorkInfoDto(boolean hasWork) {
        this.hasWork = hasWork;
    }
}
