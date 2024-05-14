package fr.uga.l3miage.integrator.mappers;

import fr.uga.l3miage.integrator.enums.Job;
import fr.uga.l3miage.integrator.enums.TourState;
import fr.uga.l3miage.integrator.exceptions.rest.DayCreationRestException;
import fr.uga.l3miage.integrator.exceptions.rest.DayNotFoundRestException;
import fr.uga.l3miage.integrator.exceptions.technical.InvalidInputValueException;
import fr.uga.l3miage.integrator.models.EmployeeEntity;
import fr.uga.l3miage.integrator.models.TourEntity;
import fr.uga.l3miage.integrator.models.TruckEntity;
import fr.uga.l3miage.integrator.repositories.EmployeeRepository;
import fr.uga.l3miage.integrator.repositories.TruckRepository;
import fr.uga.l3miage.integrator.requests.TourCreationRequest;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.*;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class TourPlannerMapperTest {
    @Autowired
    private TourPlannerMapper tourPlannerMapper;
    @MockBean
    private TruckRepository truckRepository;
    @MockBean
    private EmployeeRepository employeeRepository;

    @Test
    void TourCreationRequest_to_tourEntityOK() throws InvalidInputValueException {
        //given
        TruckEntity truck = TruckEntity.builder().immatriculation("WT-543-TR").build();
        EmployeeEntity deliveryman1=EmployeeEntity.builder().job(Job.DELIVERYMAN).trigram("JLY").mobilePhone("0876342324").build();
        EmployeeEntity deliveryman2=EmployeeEntity.builder().job(Job.DELIVERYMAN).trigram("PLE").mobilePhone("0876342654").build();
        TourCreationRequest tourCreationRequest = TourCreationRequest.builder()
                .distanceToCover(49)
                .truck(truck.getImmatriculation())
                .deliverymen(Set.of(deliveryman1.getTrigram(),deliveryman2.getTrigram()))
                .build();

        //when
        when(truckRepository.findById(truck.getImmatriculation())).thenReturn(Optional.of(truck));
        when(employeeRepository.findById(deliveryman1.getTrigram())).thenReturn(Optional.of(deliveryman1));
        when(employeeRepository.findById(deliveryman2.getTrigram())).thenReturn(Optional.of(deliveryman2));

        TourEntity expectedResponse= TourEntity.builder().reference("t122G-A").state(TourState.PLANNED).distanceToCover(49).letter("A").deliveries(new LinkedList<>()).deliverymen(Set.of(deliveryman1,deliveryman2)).truck(truck).build();

        TourEntity actualResponse= tourPlannerMapper.toEntity(tourCreationRequest,"t122G-A");
        //then
        assertThat(actualResponse.getReference()).isEqualTo(expectedResponse.getReference());
        assertThat(actualResponse.getTruck().getImmatriculation()).isEqualTo(expectedResponse.getTruck().getImmatriculation());

    }

    @Test
    void TourCreationRequest_to_tourEntity_NotOK_BecauseOfNotFoundTruck() {
        //given
        EmployeeEntity deliveryman1=EmployeeEntity.builder().job(Job.DELIVERYMAN).trigram("JLY").mobilePhone("0876342324").build();
        EmployeeEntity deliveryman2=EmployeeEntity.builder().job(Job.DELIVERYMAN).trigram("PLE").mobilePhone("0876342654").build();
        TourCreationRequest tourCreationRequest = TourCreationRequest.builder()
                .distanceToCover(49)
                .truck("TR-123-XS")
                .deliverymen(Set.of(deliveryman1.getTrigram(),deliveryman2.getTrigram()))
                .build();

        //when
        when(truckRepository.findById(anyString())).thenReturn(Optional.empty());
        when(employeeRepository.findById(deliveryman1.getTrigram())).thenReturn(Optional.of(deliveryman1));
        when(employeeRepository.findById(deliveryman2.getTrigram())).thenReturn(Optional.of(deliveryman2));

        //then
       assertThrows(InvalidInputValueException.class,()->tourPlannerMapper.toEntity(tourCreationRequest,"t122G-A"));
    }


    @Test
    void TourCreationRequest_to_tourEntity_NotOK_BecauseOfNotFoundDeliveryman() {
        //given
        TruckEntity truck = TruckEntity.builder().immatriculation("WT-543-TR").build();
        EmployeeEntity deliveryman1=EmployeeEntity.builder().job(Job.DELIVERYMAN).email("john@gmail.com").lastName("Leroy").firstName("John").trigram("JLY").mobilePhone("0876342324").build();
        TourCreationRequest tourCreationRequest = TourCreationRequest.builder()
                .distanceToCover(49)
                .truck(truck.getImmatriculation())
                .deliverymen(Set.of(deliveryman1.getTrigram(),"PLE"))
                .build();

        //when
        when(truckRepository.findById(anyString())).thenReturn(Optional.of(truck));
        when(employeeRepository.findById(deliveryman1.getTrigram())).thenReturn(Optional.of(deliveryman1));
        when(employeeRepository.findById("PLE")).thenReturn(Optional.empty());

        //then
        assertThrows(DayCreationRestException.class,()->tourPlannerMapper.toEntity(tourCreationRequest,"t122G-A"));
    }
}

