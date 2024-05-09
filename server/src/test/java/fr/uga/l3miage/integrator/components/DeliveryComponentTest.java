package fr.uga.l3miage.integrator.components;

import fr.uga.l3miage.integrator.enums.DeliveryState;
import fr.uga.l3miage.integrator.exceptions.technical.DeliveryNotFoundException;
import fr.uga.l3miage.integrator.exceptions.technical.UpdateDeliveryStateException;
import fr.uga.l3miage.integrator.models.DayEntity;
import fr.uga.l3miage.integrator.models.DeliveryEntity;
import fr.uga.l3miage.integrator.models.OrderEntity;
import fr.uga.l3miage.integrator.repositories.DeliveryRepository;
import org.apache.tomcat.jni.Local;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDate;
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
    private DeliveryRepository deliveryRepository;

    @Test
    void saveDelivery(){
        //given
        DeliveryEntity deliveryEntity=DeliveryEntity.builder()
                .reference("l123G-A")
                .state(DeliveryState.PLANNED)
                .distanceToCover(3)
                .orders(Set.of())
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
        int deliveryIndex=2;

        String expectedDeliveryRef= "l123G-A2";

        //when
        String response=deliveryComponent.generateDeliveryReference(date,deliveryIndex,"A");

        //then
        assertThat(expectedDeliveryRef).isEqualTo(response);
    }

    @Test
    void updateDeliveryStateOK1() throws DeliveryNotFoundException, UpdateDeliveryStateException {

        //given
        DeliveryEntity deliveryEntity=DeliveryEntity.builder()
                .reference("l120G-A1")
                .state(DeliveryState.PLANNED)
                .distanceToCover(3.9)
                .orders(Set.of())
                .build();


        //when
        when(deliveryRepository.findById(deliveryEntity.getReference())).thenReturn(Optional.of(deliveryEntity));
        when(deliveryRepository.save(any(DeliveryEntity.class))).thenReturn(deliveryEntity);

        DeliveryEntity response=deliveryComponent.updateDeliveryState(deliveryEntity.getReference(),DeliveryState.IN_COURSE);

        //then
        assertThat(response.getState()).isEqualTo(DeliveryState.IN_COURSE);


    }
    @Test
    void updateDeliveryState_OK2() throws DeliveryNotFoundException, UpdateDeliveryStateException {

        //given
        DeliveryEntity deliveryEntity=DeliveryEntity.builder()
                .reference("l120G-A1")
                .state(DeliveryState.WITH_CUSTOMER)
                .distanceToCover(3.9)
                .orders(Set.of())
                .build();


        //when
        when(deliveryRepository.findById(deliveryEntity.getReference())).thenReturn(Optional.of(deliveryEntity));
        when(deliveryRepository.save(any(DeliveryEntity.class))).thenReturn(deliveryEntity);

        DeliveryEntity response=deliveryComponent.updateDeliveryState(deliveryEntity.getReference(),DeliveryState.COMPLETED);

        //then
        assertThat(response.getState()).isEqualTo(DeliveryState.COMPLETED);

    }
    @Test
    void updateDeliveryState_NotOK_BeacauseOf_NotFoundDelivery()  {

        //when
        when(deliveryRepository.findById(anyString())).thenReturn(Optional.empty());

        //then
        assertThrows(DeliveryNotFoundException.class,()->deliveryComponent.updateDeliveryState(anyString(),DeliveryState.IN_COURSE));


    }

    @Test
    void updateDeliveryState_NotOK_BeacauseOf_WrongState1() throws DeliveryNotFoundException, UpdateDeliveryStateException {

        //given
        DeliveryEntity deliveryEntity=DeliveryEntity.builder()
                .reference("l120G-A1")
                .state(DeliveryState.PLANNED)
                .distanceToCover(3.9)
                .orders(Set.of())
                .build();


        //when
        when(deliveryRepository.findById(deliveryEntity.getReference())).thenReturn(Optional.of(deliveryEntity));
        when(deliveryRepository.save(any(DeliveryEntity.class))).thenReturn(deliveryEntity);

        //then
        assertThrows(UpdateDeliveryStateException.class,()->deliveryComponent.updateDeliveryState(deliveryEntity.getReference(),DeliveryState.UNLOADING));

    }
    @Test
    void updateDeliveryState_NotOK_BeacauseOf_WrongState2() throws DeliveryNotFoundException, UpdateDeliveryStateException {

        //given
        DeliveryEntity deliveryEntity=DeliveryEntity.builder()
                .reference("l120G-A1")
                .state(DeliveryState.IN_COURSE)
                .distanceToCover(3.9)
                .orders(Set.of())
                .build();


        //when
        when(deliveryRepository.findById(deliveryEntity.getReference())).thenReturn(Optional.of(deliveryEntity));
        when(deliveryRepository.save(any(DeliveryEntity.class))).thenReturn(deliveryEntity);

        //then
        assertThrows(UpdateDeliveryStateException.class,()->deliveryComponent.updateDeliveryState(deliveryEntity.getReference(),DeliveryState.WITH_CUSTOMER));

    }

    @Test
    void updateDeliveryState_NotOK_BeacauseOf_WrongState3() throws DeliveryNotFoundException, UpdateDeliveryStateException {

        //given
        DeliveryEntity deliveryEntity=DeliveryEntity.builder()
                .reference("l120G-A1")
                .state(DeliveryState.UNLOADING)
                .distanceToCover(3.9)
                .orders(Set.of())
                .build();


        //when
        when(deliveryRepository.findById(deliveryEntity.getReference())).thenReturn(Optional.of(deliveryEntity));
        when(deliveryRepository.save(any(DeliveryEntity.class))).thenReturn(deliveryEntity);

        //then
        assertThrows(UpdateDeliveryStateException.class,()->deliveryComponent.updateDeliveryState(deliveryEntity.getReference(),DeliveryState.COMPLETED));

    }

    @Test
    void updateDeliveryState_NotOK_BeacauseOf_WrongState4() throws DeliveryNotFoundException, UpdateDeliveryStateException {

        //given
        DeliveryEntity deliveryEntity=DeliveryEntity.builder()
                .reference("l120G-A1")
                .state(DeliveryState.COMPLETED)
                .distanceToCover(3.9)
                .orders(Set.of())
                .build();


        //when
        when(deliveryRepository.findById(deliveryEntity.getReference())).thenReturn(Optional.of(deliveryEntity));
        when(deliveryRepository.save(any(DeliveryEntity.class))).thenReturn(deliveryEntity);

        //then
        assertThrows(UpdateDeliveryStateException.class,()->deliveryComponent.updateDeliveryState(deliveryEntity.getReference(),DeliveryState.COMPLETED));


    }

}
