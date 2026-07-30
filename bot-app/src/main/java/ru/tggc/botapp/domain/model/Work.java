package ru.tggc.botapp.domain.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.tggc.botapp.domain.model.enums.work.WorkIndex;
import ru.tggc.botapp.domain.model.enums.work.WorkType;
import ru.tggc.botapp.domain.model.timedaction.WorkAction;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Work {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Embedded
    private WorkAction workAction;
    private Integer rise;
    private Integer index;
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private BigJob bigJob;
    @Enumerated(EnumType.STRING)
    private WorkType workType;

    public boolean hasWork() {
        return workType != WorkType.NONE;
    }

    public WorkIndex getCurrentWorkLevel() {
        return workType.getLevelByIndex(index);
    }

    public String getCurrentRoleLabel() {
        if (!hasWork()) {
            return workType.getLabel();
        }
        return getCurrentWorkLevel().getLabel();
    }
}
