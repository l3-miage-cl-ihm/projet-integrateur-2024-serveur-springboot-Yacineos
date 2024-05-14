package fr.uga.l3miage.integrator.exceptions.handlers;

import fr.uga.l3miage.integrator.exceptions.DeliveryStatusNotUpdatedResponse;
import fr.uga.l3miage.integrator.exceptions.rest.UpdateDeliveryStateRestException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
public class DeliveryUpdatingStateExceptionHandler {
    @ExceptionHandler(UpdateDeliveryStateRestException.class)
    public ResponseEntity<DeliveryStatusNotUpdatedResponse> handle(HttpServletRequest httpServletRequest, Exception exception){
        UpdateDeliveryStateRestException deliveryUpdatingStatusRestException = (UpdateDeliveryStateRestException) exception;
        DeliveryStatusNotUpdatedResponse response = DeliveryStatusNotUpdatedResponse
                .builder()
                .errorMessage(deliveryUpdatingStatusRestException.getMessage())
                .uri(httpServletRequest.getRequestURI())
                .build();
        return ResponseEntity.status(409).body(response);
    }
}
