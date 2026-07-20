package ru.tggc.botapp.domain.dto.info;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
public class TeaInfoDto extends ActionInfo {
    private boolean isWaiting;
}
