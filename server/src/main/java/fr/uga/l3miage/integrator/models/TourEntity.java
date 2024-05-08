package fr.uga.l3miage.integrator.models;

import fr.uga.l3miage.integrator.enums.TourState;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;

@Entity
@Getter
@SuperBuilder
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TourEntity {
    @Id
    private String reference;

    private TourState state;
    private String letter;
    private double distanceToCover;

    @Column(nullable = true)
    private Integer actualAssemblyTime ;

    @ManyToMany
    private Set<EmployeeEntity> deliverymen;

    @OneToMany
    @JoinColumn(name = "refTour", referencedColumnName = "reference")
    private List<DeliveryEntity> deliveries;

    @ManyToOne
    private TruckEntity truck;

}
