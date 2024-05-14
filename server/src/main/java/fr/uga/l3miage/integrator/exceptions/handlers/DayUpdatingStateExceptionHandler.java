package fr.uga.l3miage.integrator.exceptions.handlers;

import fr.uga.l3miage.integrator.exceptions.DayStateNotUpdatedResponse;
import fr.uga.l3miage.integrator.exceptions.DeliveryStatusNotUpdatedResponse;
import fr.uga.l3miage.integrator.exceptions.rest.UpdateDayStateRestException;
import fr.uga.l3miage.integrator.exceptions.rest.UpdateDeliveryStateRestException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;


@ControllerAdvice
public class DayUpdatingStateExceptionHandler {
    @ExceptionHandler(UpdateDayStateRestException.class)
    public ResponseEntity<DayStateNotUpdatedResponse> handle(HttpServletRequest httpServletRequest, Exception exception){
        UpdateDayStateRestException updateDayStateRestException = (UpdateDayStateRestException) exception;
        DayStateNotUpdatedResponse response = DayStateNotUpdatedResponse
                .builder()
                .errorMessage(updateDayStateRestException.getMessage())
                .uri(httpServletRequest.getRequestURI())
                .build();
        return ResponseEntity.status(409).body(response);
    }
}
