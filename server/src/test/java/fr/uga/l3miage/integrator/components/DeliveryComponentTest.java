package fr.uga.l3miage.integrator.components;

import fr.uga.l3miage.integrator.enums.DeliveryState;
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
import java.util.Set;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
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
}
