package fr.uga.l3miage.integrator.mappers.utils;

import lombok.RequiredArgsConstructor;
import org.mapstruct.Qualifier;
import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class DayPlannerMapperUtils {
    @Qualifier
    @Retention(RetentionPolicy.CLASS)
    @Target(ElementType.METHOD)
    public @interface GenerateDayReference{}

    @GenerateDayReference
    public String generateDayReference(LocalDate date) {
        String dayNumber = String.format("%03d", date.getDayOfYear());
        return 'J' + dayNumber+'G';
    }

}
