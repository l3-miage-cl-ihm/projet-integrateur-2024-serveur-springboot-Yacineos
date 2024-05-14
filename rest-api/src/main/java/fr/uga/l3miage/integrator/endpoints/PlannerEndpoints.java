package fr.uga.l3miage.integrator.endpoints;

import fr.uga.l3miage.integrator.enums.DayState;
import fr.uga.l3miage.integrator.enums.DeliveryState;
import fr.uga.l3miage.integrator.exceptions.*;
import fr.uga.l3miage.integrator.requests.DayCreationRequest;
import fr.uga.l3miage.integrator.responses.DayResponseDTO;
import fr.uga.l3miage.integrator.responses.SetUpBundleResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Date;

@RestController
@Tag(name = "Planner endpoints")
@RequestMapping("/api/v3.0/planner")
public interface PlannerEndpoints {

    @Operation(description = "Get set up bundle  (orders, deliverymen,trucks) ")
    @ApiResponse(responseCode= "200", description = "Bundle sent  ")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/bundle/{idWarehouse}")
    SetUpBundleResponse getSetUpBundle(@RequestParam String idWarehouse);


    @Operation(description = "Get planned day ")
    @ApiResponse(responseCode= "200", description = "Day found  ")
    @ApiResponse(responseCode= "404", description = "Day not found with given date",content = @Content(schema = @Schema(implementation = NotFoundErrorResponse.class),mediaType = MediaType.APPLICATION_JSON_VALUE))
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/day")
    DayResponseDTO getDay(@RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) ;



    @Operation(description = "PLan a day with given day creation request ")
    @ApiResponse(responseCode= "200", description = "Day successfully planned ")
    @ApiResponse(responseCode= "406", description = "Invalid input value ",content = @Content(schema = @Schema(implementation = PlanDayErrorResponse.class),mediaType = MediaType.APPLICATION_JSON_VALUE))
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/day/plan")
    void planDay(@RequestBody DayCreationRequest dayCreationRequest);



    @Operation(description = "Edit an already planned day with given update day creation request ")
    @ApiResponse(responseCode= "200", description = "Day successfully edited ")
    @ApiResponse(responseCode= "406", description = "Invalid input value ",content = @Content(schema = @Schema(implementation = EditDayErrorResponse.class),mediaType = MediaType.APPLICATION_JSON_VALUE))
    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/days/{dayId}/edit")
    void editDay(@RequestBody DayCreationRequest dayEditRequest, @PathVariable String dayId);


    @Operation(description = "Day state update")
    @ApiResponse(responseCode = "200",description = "Day state updated suyccessfully")
    @ApiResponse(responseCode = "404" ,description = "No day  was found !", content = @Content(schema = @Schema(implementation = NotFoundErrorResponse.class),mediaType = MediaType.APPLICATION_JSON_VALUE))
    @ApiResponse(responseCode = "409" ,description = "Cannot update day state", content = @Content(schema = @Schema(implementation = DayStateNotUpdatedResponse.class),mediaType = MediaType.APPLICATION_JSON_VALUE))
    @ResponseStatus(HttpStatus.OK)
    @PatchMapping("/days/{dayId}/updateState")
    void updateDayState(@PathVariable String dayId,@RequestParam DayState newDayState);


}
