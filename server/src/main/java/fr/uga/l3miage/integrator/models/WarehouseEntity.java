package fr.uga.l3miage.integrator.models;


import fr.uga.l3miage.integrator.dataTypes.Address;

import javax.persistence.*;
import java.util.Set;

@Entity
public class WarehouseEntity {
    @Id
    private String name;

    @Column(nullable = false)
    private String letter;

    private String photo;
     @Embedded
    private Address address;

     @OneToMany
    private Set<DayEntity> days;

     @OneToMany
    private Set<TruckEntity> trucks;

     @OneToMany
    private Set<EmployeeEntity> employees;


}
