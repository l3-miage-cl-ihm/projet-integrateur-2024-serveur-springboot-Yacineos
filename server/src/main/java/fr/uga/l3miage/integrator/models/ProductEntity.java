package fr.uga.l3miage.integrator.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class ProductEntity {

    @Id
    private String reference;

    private String photo;

    @Column(nullable = false)
    private String title;

    private String description;

    private double price;

    @Column(nullable = true)
    private Boolean assemblyOption;

    @Column(nullable = true)
    private  Integer theoriticalAssemblyTime;
}
