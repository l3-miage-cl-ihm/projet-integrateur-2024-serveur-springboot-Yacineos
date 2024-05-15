package fr.uga.l3miage.integrator.models;

import fr.uga.l3miage.integrator.enums.OrderState;
import lombok.*;
import javax.persistence.*;
import java.time.LocalDate;
import java.util.Set;

@Entity
@Getter
@Builder
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderEntity {

    @Id
    private String reference ;

    private OrderState state;

    private LocalDate creationDate;
    @Column(nullable = true)
    private Integer rate;

    @Column(nullable = true)
    private String feedback;

    @ManyToOne
    private CustomerEntity customer;

    @OneToMany(mappedBy = "order")
    private Set<LineEntity> lines ;





}
