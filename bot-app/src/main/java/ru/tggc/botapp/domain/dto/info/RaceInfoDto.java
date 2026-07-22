package ru.tggc.botapp.domain.dto.info;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@Getter
@Setter
@ToString
public class RaceInfoDto extends ActionInfo {
    private String improvement;
}
