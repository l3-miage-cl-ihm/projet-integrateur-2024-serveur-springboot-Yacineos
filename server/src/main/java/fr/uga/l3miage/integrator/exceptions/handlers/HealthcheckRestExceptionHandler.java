package fr.uga.l3miage.integrator.exceptions.handlers;

import fr.uga.l3miage.integrator.exceptions.HealthcheckError;
import fr.uga.l3miage.integrator.exceptions.rest.HealthcheckRestException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
public class HealthcheckRestExceptionHandler {

    @ExceptionHandler(HealthcheckRestException.class)
    ResponseEntity<HealthcheckError> handle(HttpServletRequest httpServletRequest, HealthcheckRestException exception) {
        HealthcheckError error = HealthcheckError.builder()
                .message(exception.getType() == HealthcheckRestException.Type.DATABASE ? "La base de données n'est pas correctement configurée" : "Le web service n'est pas correctement configuré")
                .typeError(exception.getType() == HealthcheckRestException.Type.DATABASE ? HealthcheckError.TypeError.DATABASE : HealthcheckError.TypeError.WEB_SERVICE )
                .build();
        return ResponseEntity.status(503).body(error);
    }
}
