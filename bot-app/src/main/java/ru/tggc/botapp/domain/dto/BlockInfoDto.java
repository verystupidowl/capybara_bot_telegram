package ru.tggc.botapp.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BlockInfoDto {
    private String reason;
    private String username;
    private String reporter;
}
