package fr.uga.l3miage.integrator.models;

import fr.uga.l3miage.integrator.enums.DeliveryState;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.time.LocalTime;
import java.util.Set;

@Entity
@Getter
@SuperBuilder
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DeliveryEntity {

    @Id
    private String reference;

    private DeliveryState state;
    private double distanceToCover;

    @Column(nullable = true)
    private Integer actualAssemblyTime;
    @Column(nullable = true)
    private LocalTime actualDeliveryTime;

    @OneToMany
    @JoinColumn(name = "refDelivery", referencedColumnName = "reference")
    private Set<OrderEntity> orders;

}
