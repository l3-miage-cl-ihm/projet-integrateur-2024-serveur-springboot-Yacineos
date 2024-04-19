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
     @JoinColumn(name = "warehouseName", referencedColumnName = "name")
    private Set<DayEntity> days;

     @OneToMany
     @JoinColumn(name = "warehouseName", referencedColumnName = "name")
    private Set<TruckEntity> trucks;

     @OneToMany
     @JoinColumn(name = "warehouseName", referencedColumnName = "name")
    private Set<EmployeeEntity> employees;


}
