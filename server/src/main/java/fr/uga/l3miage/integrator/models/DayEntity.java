package fr.uga.l3miage.integrator.models;


import fr.uga.l3miage.integrator.enums.DayState;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
@Builder
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DayEntity {

    @Id
    private String reference;

    @Enumerated(EnumType.STRING)
    private DayState state;

    private LocalDate date;

    @ManyToOne
    private EmployeeEntity planner;

    @OneToMany
    @JoinColumn(name = "refDay", referencedColumnName = "reference")
    private List<TourEntity> tours;

}
