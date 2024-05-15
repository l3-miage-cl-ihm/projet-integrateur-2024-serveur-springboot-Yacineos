package fr.uga.l3miage.integrator.components;

import fr.uga.l3miage.integrator.enums.TourState;
import fr.uga.l3miage.integrator.exceptions.technical.DayNotFoundException;
import fr.uga.l3miage.integrator.exceptions.technical.TourNotFoundException;
import fr.uga.l3miage.integrator.models.DayEntity;
import fr.uga.l3miage.integrator.models.EmployeeEntity;
import fr.uga.l3miage.integrator.models.TourEntity;
import fr.uga.l3miage.integrator.models.TruckEntity;
import fr.uga.l3miage.integrator.repositories.DayRepository;
import fr.uga.l3miage.integrator.repositories.TourRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class TourComponentTest {

    @MockBean
    private DayRepository dayRepository;
    @Autowired
    private TourComponent tourComponent;
    @MockBean
    private TourRepository tourRepository;

    @Test
    void getTourOfTheDayDayNotFound (){
        //when
        when(dayRepository.findByDate(any())).thenReturn(Optional.empty());
        //then
        assertThrows(DayNotFoundException.class,()->tourComponent.getTourOfTheDay("jojo@gmail.fr"));
    }

    @Test
    void getTourOfTheDayTourNotFound (){
        //given
        DayEntity day = DayEntity.builder().tours(new LinkedList<>()).reference("J238").build();
        //when
        when(dayRepository.findByDate(any())).thenReturn(Optional.of(day));
        //then
        assertThrows(TourNotFoundException.class,()->tourComponent.getTourOfTheDay("jojo@gmail.fr"));
    }



    @Test
    void getTourOfTheDayTourWithoutTargetedDeliveryman(){

        //given
        EmployeeEntity m1=EmployeeEntity.builder().email("jojo@gmail.com").build();
        EmployeeEntity m2=EmployeeEntity.builder().email("axel@gmail.com").build();
        TourEntity tour= TourEntity.builder().reference("T238G-A").build();
        tour.setDeliverymen(Set.of(m1,m2));

        DayEntity day = DayEntity.builder().tours(List.of(tour)).reference("J238").build();
        //when
        when(dayRepository.findByDate(any())).thenReturn(Optional.of(day));
        //then
        assertThrows(TourNotFoundException.class,()->tourComponent.getTourOfTheDay("dumas@gmail.fr"));
    }

    @Test
    void getTourOfTheDayOK() throws TourNotFoundException, DayNotFoundException {

        //given
        EmployeeEntity m1=EmployeeEntity.builder().email("jojo@gmail.com").build();
        EmployeeEntity m2=EmployeeEntity.builder().email("axel@gmail.com").build();

        TourEntity tour1= TourEntity.builder().reference("T238G-A").build();
        tour1.setDeliverymen(Set.of(m1,m2));
        EmployeeEntity m3=EmployeeEntity.builder().email("juju@gmail.com").build();
        EmployeeEntity m4=EmployeeEntity.builder().email("alexis@gmail.com").build();

        TourEntity tour2= TourEntity.builder().reference("T238G-B").build();
        tour2.setDeliverymen(Set.of(m3,m4));

        DayEntity day = DayEntity.builder().reference("J238").tours(List.of(tour1,tour2)).build();
        //when
        when(dayRepository.findByDate(any())).thenReturn(Optional.of(day));
        TourEntity response= tourComponent.getTourOfTheDay("jojo@gmail.com");
        //then
        assertThat(response.getDeliverymen().stream().anyMatch(man-> man.getEmail().equals("jojo@gmail.com"))).isEqualTo(true);
        assertThat(response.getReference()).isEqualTo("T238G-A");
        assertThat(response.getDeliverymen().stream().anyMatch(man-> man.getEmail().equals("axel@gmail.com"))).isEqualTo(true);


    }

    @Test
    void saveTour(){
        //given
        TourEntity tourEntity=TourEntity.builder()
                .reference("t123G-A")
                .state(TourState.PLANNED)
                .build();
        tourComponent.saveTour(tourEntity);

        //when
        when(tourRepository.save(any(TourEntity.class))).thenReturn(tourEntity);
        //then
        verify(tourRepository, times(1)).save(any(TourEntity.class));



    }

    @Test
    void generateTourReference(){
        //given : first tour of today
        LocalDate date=LocalDate.of(2024,5,2);
        String expectedTourRef="t123G-A";
        String repsonse=tourComponent.generateTourReference(date,0);
        //then
        assertThat(repsonse).isEqualTo(expectedTourRef);
    }





}
