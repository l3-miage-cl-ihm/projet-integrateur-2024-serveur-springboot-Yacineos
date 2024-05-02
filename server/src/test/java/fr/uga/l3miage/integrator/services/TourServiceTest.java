package fr.uga.l3miage.integrator.services;


import fr.uga.l3miage.integrator.components.TourComponent;
import fr.uga.l3miage.integrator.exceptions.rest.EntityNotFoundRestException;
import fr.uga.l3miage.integrator.exceptions.technical.DayNotFoundException;
import fr.uga.l3miage.integrator.exceptions.technical.TourNotFoundException;
import fr.uga.l3miage.integrator.mappers.DeliveryDMMapper;
import fr.uga.l3miage.integrator.mappers.TourDMMapper;
import fr.uga.l3miage.integrator.models.EmployeeEntity;
import fr.uga.l3miage.integrator.models.TourEntity;
import fr.uga.l3miage.integrator.models.WarehouseEntity;
import fr.uga.l3miage.integrator.responses.TourDMResponseDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

 @SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class TourServiceTest {

    @Autowired
    private TourService tourService;
    @MockBean
    private TourComponent tourComponent;
    @SpyBean
    private TourDMMapper tourDMMapper;


    @Test
    void getDeliveryTourOfTheDayOK() throws TourNotFoundException, DayNotFoundException {

        //given
        TourEntity tour= TourEntity.builder().reference("T238G-A").build();
        Set<EmployeeEntity> deliverymen= new HashSet<>();
        EmployeeEntity m1=EmployeeEntity.builder().email("juju@gmail.com").build();
        EmployeeEntity m2=EmployeeEntity.builder().email("axel@gmail.com").build();
        WarehouseEntity w=WarehouseEntity.builder().name("Grenis").build();
        m1.setWarehouse(w);
        m2.setWarehouse(w);
        deliverymen.add(m1);
        deliverymen.add(m2);
        tour.setDeliverymen(deliverymen);

        //when
        when(tourComponent.getTourOfTheDay(anyString())).thenReturn(tour);

        TourDMResponseDTO expectedResponse= tourDMMapper.toResponse(tour);
        TourDMResponseDTO response= tourService.getDeliveryTourOfTheDay("juju@gmail.com");

        //then
        assertThat(response).usingRecursiveComparison().isEqualTo(expectedResponse);
        assertThat(response.getRefTour()).isEqualTo(expectedResponse.getRefTour());
        verify(tourDMMapper,times(2)).toResponse(tour);
        verify(tourComponent,times(1)).getTourOfTheDay("juju@gmail.com");


    }

    @Test
    void getDeliveryTourOfTheDayNotFound1() throws TourNotFoundException, DayNotFoundException {
        //when
        when(tourComponent.getTourOfTheDay(anyString())).thenThrow(new DayNotFoundException("No day was found !"));

        //then
        assertThrows( EntityNotFoundRestException.class,()-> tourService.getDeliveryTourOfTheDay("juju@gmail.com"));
    }
    @Test
    void getDeliveryTourOfTheDayNotFound2() throws TourNotFoundException, DayNotFoundException {
        //when
        when(tourComponent.getTourOfTheDay(anyString())).thenThrow(new TourNotFoundException("No tour was found for today !"));

        //then
        assertThrows( EntityNotFoundRestException.class,()-> tourService.getDeliveryTourOfTheDay("juju@gmail.com"));
    }


}
