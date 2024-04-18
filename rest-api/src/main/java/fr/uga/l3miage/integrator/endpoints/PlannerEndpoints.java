package fr.uga.l3miage.integrator.endpoints;

import fr.uga.l3miage.integrator.exceptions.NotFoundErrorResponse;
import fr.uga.l3miage.integrator.exceptions.PlanDayErrorResponse;
import fr.uga.l3miage.integrator.requests.DayCreationRequest;
import fr.uga.l3miage.integrator.responses.DayResponseDTO;
import fr.uga.l3miage.integrator.responses.SetUpBundleResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@Tag(name = "Planner endpoints")
@RequestMapping("/api/v1.0/day")
public interface PlannerEndpoints {

    @Operation(description = "Get set up bundle (orders, deliverymen,trucks) ")
    @ApiResponse(responseCode= "200", description = "Bundle sent  ")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/bundle")
    SetUpBundleResponse getSetUpBundle();


    @Operation(description = "Get planned day ")
    @ApiResponse(responseCode= "200", description = "Day found  ")
    @ApiResponse(responseCode= "404", description = "Day not found with given date",content = @Content(schema = @Schema(implementation = NotFoundErrorResponse.class),mediaType = MediaType.APPLICATION_JSON_VALUE))
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/")
    DayResponseDTO getDay(@RequestParam Date date);



    @Operation(description = "PLan a day with given day creation request ")
    @ApiResponse(responseCode= "200", description = "Day successfully planned ")
    @ApiResponse(responseCode= "406", description = "Invalid input value ",content = @Content(schema = @Schema(implementation = PlanDayErrorResponse.class),mediaType = MediaType.APPLICATION_JSON_VALUE))
    @ApiResponse(responseCode= "406", description = "Day is already planned ",content = @Content(schema = @Schema(implementation = PlanDayErrorResponse.class),mediaType = MediaType.APPLICATION_JSON_VALUE))
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/plan")
    void planDay(@RequestBody DayCreationRequest dayCreationRequest);


}
