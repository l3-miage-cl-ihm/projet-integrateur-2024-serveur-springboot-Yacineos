package fr.uga.l3miage.integrator.models;


import fr.uga.l3miage.integrator.dataTypes.Address;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.util.Set;

@Entity
@Getter
@SuperBuilder
@Setter
@AllArgsConstructor
@NoArgsConstructor
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




}
