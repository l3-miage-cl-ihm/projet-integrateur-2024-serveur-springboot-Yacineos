package fr.uga.l3miage.integrator.requests;


import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.Date;
import java.util.Set;

@Data
@Builder
public class DayCreationRequest {
    private LocalDate date;
    private Set<TourCreationRequest> tours;
}
