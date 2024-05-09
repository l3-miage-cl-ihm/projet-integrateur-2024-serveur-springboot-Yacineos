package fr.uga.l3miage.integrator.services;

import fr.uga.l3miage.integrator.components.DeliveryComponent;
import fr.uga.l3miage.integrator.enums.DeliveryState;
import fr.uga.l3miage.integrator.exceptions.rest.DeliveryNotFoundRestException;
import fr.uga.l3miage.integrator.exceptions.rest.UpdateDeliveryStateRestException;
import fr.uga.l3miage.integrator.exceptions.technical.DeliveryNotFoundException;
import fr.uga.l3miage.integrator.exceptions.technical.UpdateDeliveryStateException;
import fr.uga.l3miage.integrator.models.DeliveryEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Set;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class DeliveryServiceTest {
    @Autowired
    private DeliveryService deliveryService;

    @MockBean
    private DeliveryComponent deliveryComponent;




    @Test
   void  updateDeliveryStateOK() throws DeliveryNotFoundException, UpdateDeliveryStateException {
        //given
        DeliveryEntity deliveryEntity=DeliveryEntity.builder()
                .reference("l120G-A1")
                .state(DeliveryState.PLANNED)
                .distanceToCover(3.9)
                .orders(Set.of())
                .build();

        //when
        when(deliveryComponent.updateDeliveryState(anyString(),any(DeliveryState.class))).thenReturn(deliveryEntity);

        deliveryEntity.setState(DeliveryState.IN_COURSE);

        DeliveryEntity response = deliveryService.updateDeliveryState(DeliveryState.IN_COURSE,deliveryEntity.getReference());

        //then
        assertThat(deliveryEntity).usingRecursiveComparison().isEqualTo(response);
    }


    @Test
    void  updateDeliveryState_NotOK_BecauseOfNotFoundDelivery() throws DeliveryNotFoundException, UpdateDeliveryStateException {
        //given
        String deliveryId="l130G-A1";

        //when
        when(deliveryComponent.updateDeliveryState(anyString(),any(DeliveryState.class))).thenThrow(new DeliveryNotFoundException("No delivery was found with given reference <"+deliveryId+">"));

        //then
        assertThrows(DeliveryNotFoundRestException.class,()->deliveryService.updateDeliveryState(DeliveryState.IN_COURSE,deliveryId));

    }

    @Test
    void  updateDeliveryState_NotOK_BecauseOfWrongState() throws DeliveryNotFoundException, UpdateDeliveryStateException {
        //given
        DeliveryState state=DeliveryState.COMPLETED;
        //when
        when(deliveryComponent.updateDeliveryState(anyString(),any(DeliveryState.class))).thenThrow(new UpdateDeliveryStateException("Delivery is already Completed !",state));

        //then
        assertThrows(UpdateDeliveryStateRestException.class,()->deliveryService.updateDeliveryState(DeliveryState.COMPLETED,"l130G-A1"));

    }
}
