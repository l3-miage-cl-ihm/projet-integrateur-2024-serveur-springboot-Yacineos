package fr.uga.l3miage.integrator.models;

import javax.persistence.*;

@Entity
public class LineEntity {
    @EmbeddedId
    LineKey id;

    @ManyToOne
    @MapsId("orderRef")
    OrderEntity order;

    @ManyToOne
    @MapsId("productRef")
    ProductEntity product;

    int quantity;
    @Column(nullable = true)
    Boolean assemblyOption;
    double amount;
}

