package fr.uga.l3miage.integrator.components;

import fr.uga.l3miage.integrator.exceptions.technical.DayNotFoundException;
import fr.uga.l3miage.integrator.models.DayEntity;
import fr.uga.l3miage.integrator.models.EmployeeEntity;
import fr.uga.l3miage.integrator.models.TourEntity;
import fr.uga.l3miage.integrator.repositories.DayRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@AutoConfigureTestDatabase
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class DayComponentTest {
    @MockBean
    private DayRepository dayRepository;

    @Autowired
    private DayComponent  dayComponent;

    @Test
    void getDayNotFound(){
        when(dayRepository.findByDate(any())).thenReturn(Optional.empty());

        //then
        assertThrows(DayNotFoundException.class,()->dayComponent.getDay(LocalDate.now()));


    }
    @Test
    void getDayOK() throws DayNotFoundException {
        //given
        Set<TourEntity> tours= new HashSet<>();
        Set<EmployeeEntity> deliverymen= new HashSet<>();
        EmployeeEntity m1=EmployeeEntity.builder().email("jojo@gmail.com").build();
        EmployeeEntity m2=EmployeeEntity.builder().email("axel@gmail.com").build();
        deliverymen.add(m1);
        deliverymen.add(m2);

        TourEntity tour1= TourEntity.builder().reference("T238G-A").build();
        tours.add(tour1);
        tour1.setDeliverymen(deliverymen);
        Set<EmployeeEntity> deliverymen2= new HashSet<>();
        EmployeeEntity m3=EmployeeEntity.builder().email("juju@gmail.com").build();
        EmployeeEntity m4=EmployeeEntity.builder().email("alexis@gmail.com").build();
        deliverymen2.add(m3);
        deliverymen2.add(m4);

        TourEntity tour2= TourEntity.builder().reference("T238G-B").build();
        tour2.setDeliverymen(deliverymen2);

        tours.add(tour2);
        DayEntity day = DayEntity.builder().reference("J238").date(LocalDate.of(2024,4,29)).build();
        day.setTours(tours);

        //when
        when(dayRepository.findByDate(any())).thenReturn(Optional.of(day));
        DayEntity response= dayComponent.getDay(LocalDate.now());
        //then
        assertThat(response.getReference()).isEqualTo("J238");
        assertThat(response.getDate()).isEqualTo(LocalDate.of(2024,4,29));
        assertThat(response.getTours().stream().anyMatch(tour-> tour.getReference().equals("T238G-B"))).isEqualTo(true);
        assertThat(response.getTours().stream().anyMatch(tour-> tour.getReference().equals("T238G-A"))).isEqualTo(true);
        assertThat(response.getTours().stream().allMatch(tour-> tour.getDeliverymen().size()==2)).isEqualTo(true);

    }
}
