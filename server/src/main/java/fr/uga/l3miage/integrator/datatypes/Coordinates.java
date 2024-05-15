package fr.uga.l3miage.integrator.datatypes;

import lombok.*;
import javax.persistence.Embeddable;

@Embeddable
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Coordinates {
    private double lat;
    private double lon;
}
