package fr.uga.l3miage.integrator.exceptions.handlers;

import fr.uga.l3miage.integrator.exceptions.NotFoundErrorResponse;
import fr.uga.l3miage.integrator.exceptions.PlanDayErrorResponse;
import fr.uga.l3miage.integrator.exceptions.rest.DayCreationRestException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;


@Slf4j
@ControllerAdvice
public class DayCreationExceptionHandler {

    @ExceptionHandler(DayCreationRestException.class)
    public ResponseEntity<PlanDayErrorResponse> handle(HttpServletRequest httpServletRequest, Exception e){
        DayCreationRestException exception = (DayCreationRestException) e;
        final PlanDayErrorResponse response = PlanDayErrorResponse.
                builder()
                .errorMessage(exception.getMessage())
                .uri(httpServletRequest.getRequestURI())
                .build();
        log.warn(exception.getMessage());
        return ResponseEntity.status(406).body(response);
    }
}


