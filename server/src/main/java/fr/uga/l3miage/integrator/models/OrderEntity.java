package fr.uga.l3miage.integrator.models;

import fr.uga.l3miage.integrator.enums.OrderState;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import javax.sound.sampled.Line;
import java.time.LocalDate;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@SuperBuilder
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
