package ru.tggc.botapp.domain.dto.info;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class RaceInfoDto extends ActionInfo {
    private String improvement;
}
