package fr.uga.l3miage.integrator.components;

import fr.uga.l3miage.integrator.enums.DeliveryState;
import fr.uga.l3miage.integrator.enums.TourState;
import fr.uga.l3miage.integrator.exceptions.technical.DeliveryNotFoundException;
import fr.uga.l3miage.integrator.exceptions.technical.TourNotFoundException;
import fr.uga.l3miage.integrator.exceptions.technical.UpdateDeliveryStateException;
import fr.uga.l3miage.integrator.models.DayEntity;
import fr.uga.l3miage.integrator.models.DeliveryEntity;
import fr.uga.l3miage.integrator.models.OrderEntity;
import fr.uga.l3miage.integrator.models.TourEntity;
import fr.uga.l3miage.integrator.repositories.DeliveryRepository;
import fr.uga.l3miage.integrator.repositories.TourRepository;
import org.apache.tomcat.jni.Local;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class DeliveryComponentTest {

    @Autowired
    private DeliveryComponent deliveryComponent;
    @MockBean
    private TourRepository tourRepository;

    @MockBean
    private DeliveryRepository deliveryRepository;

    @Test
    void saveDelivery(){
        //given
        DeliveryEntity deliveryEntity=DeliveryEntity.builder()
                .reference("l123G-A")
                .state(DeliveryState.PLANNED)
                .distanceToCover(3)
                .build();

        when(deliveryRepository.save(any(DeliveryEntity.class))).thenReturn(deliveryEntity);
        //when
        deliveryComponent.saveDelivery(deliveryEntity);
        //then
        verify(deliveryRepository, times(1)).save(any(DeliveryEntity.class));




    }

    @Test
    void generateDeliveryReference(){
        //given
        LocalDate date= LocalDate.of(2024,5,2);
        String expectedDeliveryRef= "l123G-A2";

        //when
        String response=deliveryComponent.generateDeliveryReference(date,2,"A");

        //then
        assertThat(expectedDeliveryRef).isEqualTo(response);
    }

    @Test
    void updateDeliveryStateOK1() throws DeliveryNotFoundException, UpdateDeliveryStateException {

        //given
        DeliveryEntity deliveryEntity=DeliveryEntity.builder()
                .reference("l120G-A1")
                .state(DeliveryState.PLANNED)
                .build();
        TourEntity tour=TourEntity.builder().reference("t120G-A").deliveries(List.of(deliveryEntity)).build();

        //when
        when(deliveryRepository.findById(deliveryEntity.getReference())).thenReturn(Optional.of(deliveryEntity));
        when(deliveryRepository.save(any(DeliveryEntity.class))).thenReturn(deliveryEntity);
        when(tourRepository.findById(tour.getReference())).thenReturn(Optional.of(tour));

        DeliveryEntity response=deliveryComponent.updateDeliveryState(deliveryEntity.getReference(),DeliveryState.IN_COURSE,tour.getReference());

        //then
        assertThat(response.getState()).isEqualTo(DeliveryState.IN_COURSE);

    }
    @Test
    void updateDeliveryState_OK2() throws DeliveryNotFoundException, UpdateDeliveryStateException, TourNotFoundException {
        //given
        DeliveryEntity deliveryEntity=DeliveryEntity.builder()
                .reference("l120G-A1")
                .state(DeliveryState.WITH_CUSTOMER)
                .build();

        TourEntity tour=TourEntity.builder().state(TourState.CUSTOMER).reference("t120G-A").deliveries(List.of(deliveryEntity)).build();

        //when
        when(deliveryRepository.findById(deliveryEntity.getReference())).thenReturn(Optional.of(deliveryEntity));
        when(deliveryRepository.save(any(DeliveryEntity.class))).thenReturn(deliveryEntity);
        when(tourRepository.findById(tour.getReference())).thenReturn(Optional.of(tour));

        DeliveryEntity response=deliveryComponent.updateDeliveryState(deliveryEntity.getReference(),DeliveryState.COMPLETED,tour.getReference());

        //then
        assertThat(response.getState()).isEqualTo(DeliveryState.COMPLETED);

    }
    @Test
    void updateDeliveryState_OK3() throws DeliveryNotFoundException, UpdateDeliveryStateException, TourNotFoundException {
        //given
        DeliveryEntity deliveryEntity=DeliveryEntity.builder()
                .reference("l120G-A1")
                .state(DeliveryState.IN_COURSE)
                .distanceToCover(3.9)
                .orders(Set.of())
                .build();

        TourEntity tour=TourEntity.builder().state(TourState.IN_COURSE).reference("t120G-A").deliveries(List.of(deliveryEntity)).build();

        //when
        when(deliveryRepository.findById(deliveryEntity.getReference())).thenReturn(Optional.of(deliveryEntity));
        when(deliveryRepository.save(any(DeliveryEntity.class))).thenReturn(deliveryEntity);
        when(tourRepository.findById(tour.getReference())).thenReturn(Optional.of(tour));

        DeliveryEntity response=deliveryComponent.updateDeliveryState(deliveryEntity.getReference(),DeliveryState.PLANNED,tour.getReference());

        //then
        assertThat(response.getState()).isEqualTo(DeliveryState.PLANNED);

    }
    @Test
    void updateDeliveryState_OK4() throws DeliveryNotFoundException, UpdateDeliveryStateException, TourNotFoundException {
        //given
        DeliveryEntity deliveryEntity=DeliveryEntity.builder()
                .reference("l120G-A1")
                .state(DeliveryState.ASSEMBLY)
                .build();

        TourEntity tour=TourEntity.builder().state(TourState.ASSEMBLY).reference("t120G-A").deliveries(List.of(deliveryEntity)).build();

        //when
        when(deliveryRepository.findById(deliveryEntity.getReference())).thenReturn(Optional.of(deliveryEntity));
        when(deliveryRepository.save(any(DeliveryEntity.class))).thenReturn(deliveryEntity);
        when(tourRepository.findById(tour.getReference())).thenReturn(Optional.of(tour));

        DeliveryEntity response=deliveryComponent.updateDeliveryState(deliveryEntity.getReference(),DeliveryState.WITH_CUSTOMER,tour.getReference());

        //then
        assertThat(response.getState()).isEqualTo(DeliveryState.WITH_CUSTOMER);

    }
    @Test
    void updateDeliveryState_NotOK_BeacauseOf_NotFoundDelivery()  {

        //when
        when(deliveryRepository.findById(anyString())).thenReturn(Optional.empty());

        //then
        assertThrows(DeliveryNotFoundException.class,()->deliveryComponent.updateDeliveryState(anyString(),DeliveryState.IN_COURSE,"t120G-A"));


    }

    @Test
    void updateDeliveryState_NotOK_BeacauseOf_WrongState1() throws DeliveryNotFoundException, UpdateDeliveryStateException {
        //given
        DeliveryEntity deliveryEntity=DeliveryEntity.builder()
                .reference("l120G-A1")
                .state(DeliveryState.PLANNED)
                .build();
        //when
        when(deliveryRepository.findById(deliveryEntity.getReference())).thenReturn(Optional.of(deliveryEntity));
        when(deliveryRepository.save(any(DeliveryEntity.class))).thenReturn(deliveryEntity);

        //then
        assertThrows(UpdateDeliveryStateException.class,()->deliveryComponent.updateDeliveryState(deliveryEntity.getReference(),DeliveryState.UNLOADING,anyString()));

    }
    @Test
    void updateDeliveryState_NotOK_BeacauseOf_WrongState2() throws DeliveryNotFoundException, UpdateDeliveryStateException {
        //given
        DeliveryEntity deliveryEntity=DeliveryEntity.builder()
                .reference("l120G-A1")
                .state(DeliveryState.IN_COURSE)
                .build();

        //when
        when(deliveryRepository.findById(deliveryEntity.getReference())).thenReturn(Optional.of(deliveryEntity));
        when(deliveryRepository.save(any(DeliveryEntity.class))).thenReturn(deliveryEntity);

        //then
        assertThrows(UpdateDeliveryStateException.class,()->deliveryComponent.updateDeliveryState(deliveryEntity.getReference(),DeliveryState.WITH_CUSTOMER,anyString()));

    }

    @Test
    void updateDeliveryState_NotOK_BeacauseOf_WrongState3() throws DeliveryNotFoundException, UpdateDeliveryStateException {
        //given
        DeliveryEntity deliveryEntity=DeliveryEntity.builder()
                .reference("l120G-A1")
                .state(DeliveryState.UNLOADING)
                .build();


        //when
        when(deliveryRepository.findById(deliveryEntity.getReference())).thenReturn(Optional.of(deliveryEntity));
        when(deliveryRepository.save(any(DeliveryEntity.class))).thenReturn(deliveryEntity);

        //then
        assertThrows(UpdateDeliveryStateException.class,()->deliveryComponent.updateDeliveryState(deliveryEntity.getReference(),DeliveryState.COMPLETED,anyString()));

    }

    @Test
    void updateDeliveryState_NotOK_BeacauseOf_WrongState4() throws DeliveryNotFoundException, UpdateDeliveryStateException {
        //given
        DeliveryEntity deliveryEntity=DeliveryEntity.builder()
                .reference("l120G-A1")
                .state(DeliveryState.COMPLETED)
                .build();

        //when
        when(deliveryRepository.findById(deliveryEntity.getReference())).thenReturn(Optional.of(deliveryEntity));
        when(deliveryRepository.save(any(DeliveryEntity.class))).thenReturn(deliveryEntity);

        //then
        assertThrows(UpdateDeliveryStateException.class,()->deliveryComponent.updateDeliveryState(deliveryEntity.getReference(),DeliveryState.COMPLETED,anyString()));


    }

}
