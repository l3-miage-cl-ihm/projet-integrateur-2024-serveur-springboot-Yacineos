package fr.uga.l3miage.integrator.endpoints;

import fr.uga.l3miage.integrator.exceptions.NotFoundErrorResponse;
import fr.uga.l3miage.integrator.responses.DeliveryDMResponseDTO;
import fr.uga.l3miage.integrator.responses.TourDMResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name = "Deliveryman endpoints")
@RequestMapping("/api/v3.0/deliveryman")
public interface DeliverymanEndpoints {
    @Operation(description = "Deliveryman oeprations ")
    @ApiResponse(responseCode= "200", description = "tour found ")
    @ApiResponse(responseCode = "404", description = "No tour was found !",content = @Content(schema = @Schema(implementation = NotFoundErrorResponse.class),mediaType = MediaType.APPLICATION_JSON_VALUE))
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/tour")
    TourDMResponseDTO getTour(@RequestParam String email);
}

