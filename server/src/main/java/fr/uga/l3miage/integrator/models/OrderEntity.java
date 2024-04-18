package fr.uga.l3miage.integrator.models;

import fr.uga.l3miage.integrator.enums.OrderState;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.util.Date;

@Entity
public class OrderEntity {

    @Id
    private String reference ;

    private OrderState state;

    private Date creationDate;
    @Column(nullable = true)
    private Integer rate;

    @Column(nullable = true)
    private String feedback;

    @ManyToOne
    private CustomerEntity customer;




}
