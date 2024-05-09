package fr.uga.l3miage.integrator.endpoints;

import fr.uga.l3miage.integrator.enums.DeliveryState;
import fr.uga.l3miage.integrator.exceptions.DeliveryStatusNotUpdatedResponse;
import fr.uga.l3miage.integrator.exceptions.NotFoundErrorResponse;
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


    /*
     * Updating delivery state
     * 1- search the delivery by the given deliveryId
     * 2- Update delivery state if possible . Planned -> IN_COURSE -> UNLOADING -> WITH_CUSTOMER -> ASSEMBLY -> COMPLETED  (cannot go back to the previous state )
     * 3- If the last delivery of the tour is completed then update tour state to  RETURNING.
     * */
    @Operation(description = "Delivery state update")
    @ApiResponse(responseCode = "200",description = "Delivery state updated suyccessfully")
    @ApiResponse(responseCode = "404" ,description = "No delivery  was found !", content = @Content(schema = @Schema(implementation = NotFoundErrorResponse.class),mediaType = MediaType.APPLICATION_JSON_VALUE))
    @ApiResponse(responseCode = "409" ,description = "Cannot update delivery state", content = @Content(schema = @Schema(implementation = DeliveryStatusNotUpdatedResponse.class),mediaType = MediaType.APPLICATION_JSON_VALUE))
    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/deliveries/{deliveryId}/updateState")
    void  updateDeliveryState(@RequestParam DeliveryState deliveryState, @PathVariable String deliveryId);



}

