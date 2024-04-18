package fr.uga.l3miage.integrator.requests;


import lombok.Builder;
import lombok.Data;

import java.util.Date;
import java.util.Set;

@Data
@Builder
public class DayCreationRequest {
    private Date date;
    private Set<TourCreationRequest> tours;
}
