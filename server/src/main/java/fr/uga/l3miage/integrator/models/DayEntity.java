package fr.uga.l3miage.integrator.models;


import fr.uga.l3miage.integrator.enums.DayState;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;

@Entity
public class DayEntity {

    @Id
    private String reference;

    @Enumerated(EnumType.STRING)
    private DayState state;

    private Date date;

    @ManyToOne
    private EmployeeEntity planner;

    @OneToMany
    private Set<TourEntity> tours;

}