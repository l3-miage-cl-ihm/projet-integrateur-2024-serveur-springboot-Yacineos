package fr.uga.l3miage.integrator.components;



import fr.uga.l3miage.integrator.exceptions.technical.DayNotFoundException;
import fr.uga.l3miage.integrator.exceptions.technical.TourNotFoundException;
import fr.uga.l3miage.integrator.models.DayEntity;
import fr.uga.l3miage.integrator.models.EmployeeEntity;
import fr.uga.l3miage.integrator.models.TourEntity;
import fr.uga.l3miage.integrator.repositories.DayRepository;
import fr.uga.l3miage.integrator.repositories.TourRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@AutoConfigureTestDatabase
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class TourComponentTest {

    @MockBean
    private DayRepository dayRepository;

    @Autowired
    private TourComponent tourComponent;




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
        DayEntity day = DayEntity.builder().tours(Set.of()).reference("J238").build();

        //when
        when(dayRepository.findByDate(any())).thenReturn(Optional.of(day));

        //then
        assertThrows(TourNotFoundException.class,()->tourComponent.getTourOfTheDay("jojo@gmail.fr"));
    }



    @Test
    void getTourOfTheDayTourWithoutTargetedDeliveryman(){


        //given
        Set<TourEntity> tours= new HashSet<>();
        Set<EmployeeEntity> deliverymen= new HashSet<>();
        EmployeeEntity m1=EmployeeEntity.builder().email("jojo@gmail.com").build();
        EmployeeEntity m2=EmployeeEntity.builder().email("axel@gmail.com").build();
        deliverymen.add(m1);
        deliverymen.add(m2);

        TourEntity tour= TourEntity.builder().reference("T238G-A").build();
        tour.setDeliverymen(deliverymen);

        tours.add(tour);
        DayEntity day = DayEntity.builder().tours(Set.of()).reference("J238").build();

        //when
        when(dayRepository.findByDate(any())).thenReturn(Optional.of(day));

        //then
        assertThrows(TourNotFoundException.class,()->tourComponent.getTourOfTheDay("dumas@gmail.fr"));
    }

    @Test
    void getTourOfTheDayOK() throws TourNotFoundException, DayNotFoundException {


        //given
        Set<TourEntity> tours= new HashSet<>();
        Set<EmployeeEntity> deliverymen= new HashSet<>();
        EmployeeEntity m1=EmployeeEntity.builder().email("jojo@gmail.com").build();
        EmployeeEntity m2=EmployeeEntity.builder().email("axel@gmail.com").build();
        deliverymen.add(m1);
        deliverymen.add(m2);

        TourEntity tour= TourEntity.builder().reference("T238G-A").build();
        tours.add(tour);
        tour.setDeliverymen(deliverymen);
        Set<EmployeeEntity> deliverymen2= new HashSet<>();
        EmployeeEntity m3=EmployeeEntity.builder().email("juju@gmail.com").build();
        EmployeeEntity m4=EmployeeEntity.builder().email("alexis@gmail.com").build();
        deliverymen2.add(m3);
        deliverymen2.add(m4);

        TourEntity tour2= TourEntity.builder().reference("T238G-B").build();
        tour2.setDeliverymen(deliverymen2);

        tours.add(tour2);
        DayEntity day = DayEntity.builder().reference("J238").build();
        day.setTours(tours);

        //when
        when(dayRepository.findByDate(any())).thenReturn(Optional.of(day));
        TourEntity response= tourComponent.getTourOfTheDay("jojo@gmail.com");
        //then
        assertThat(response.getDeliverymen().stream().anyMatch(man-> man.getEmail().equals("jojo@gmail.com"))).isEqualTo(true);
        assertThat(response.getReference()).isEqualTo("T238G-A");
        assertThat(response.getDeliverymen().stream().anyMatch(man-> man.getEmail().equals("axel@gmail.com"))).isEqualTo(true);


    }





}