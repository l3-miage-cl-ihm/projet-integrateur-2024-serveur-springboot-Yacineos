package fr.uga.l3miage.integrator.responses.datatypes;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import java.util.Set;


import javax.persistence.Embeddable;

@Embeddable
@Getter
@SuperBuilder
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MultipleOrder {
    private Set<String> orders;
    private String address;
}
