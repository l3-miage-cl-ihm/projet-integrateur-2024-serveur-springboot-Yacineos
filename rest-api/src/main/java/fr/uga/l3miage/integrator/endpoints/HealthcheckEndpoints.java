package fr.uga.l3miage.integrator.endpoints;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;


@RestController
@Tag(name = "Healthcheck endpoints")
@RequestMapping("/api/v1/")
public interface HealthcheckEndpoints {

    @Operation(description = "check health of own server")
    @ApiResponse(responseCode= "200", description = "Le serveurs est en routes")
    @ApiResponse(responseCode = "503", description = "La base de donné n'est pas disponible")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/healthcheck")
    void healthcheck();
}
