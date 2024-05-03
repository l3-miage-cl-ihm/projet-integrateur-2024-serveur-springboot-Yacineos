package fr.uga.l3miage.integrator.components;
import fr.uga.l3miage.integrator.datatypes.Address;
import fr.uga.l3miage.integrator.enums.DayState;
import fr.uga.l3miage.integrator.enums.Job;
import fr.uga.l3miage.integrator.models.DayEntity;
import fr.uga.l3miage.integrator.models.EmployeeEntity;
import fr.uga.l3miage.integrator.models.WarehouseEntity;
import fr.uga.l3miage.integrator.repositories.DayRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import fr.uga.l3miage.integrator.exceptions.technical.DayNotFoundException;
import fr.uga.l3miage.integrator.models.TourEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.util.HashSet;
import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class DayComponentTest {
    @Autowired
    private DayComponent dayComponent;
    @MockBean
    private DayRepository dayRepository;
  
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

    @Test
    void planDayOK(){
        //given
        LocalDate date = LocalDate.of(2024,4,15);
        WarehouseEntity warehouse= WarehouseEntity.builder()
                .name("Grenis")
                .letter("G")
                .address(new Address("21 rue des cafards","15002","Bristol"))
                .days(Set.of()).trucks(Set.of())
                .photo(".jpeg")
                .build();
        EmployeeEntity planner= EmployeeEntity.builder()
                .job(Job.PLANNER)
                .photo(".png")
                .email("email@xx.y")
                .trigram("JPL")
                .warehouse(warehouse)
                .mobilePhone("342532")
                .firstName("Massi")
                .lastName("leroy")
                .build();

        DayEntity dayEntity= DayEntity.builder()
                .reference("J124G")
                .date(date)
                .state(DayState.PLANNED)
                .tours(Set.of())
                .planner(planner)
                .build();

        when(dayRepository.save(any(DayEntity.class))).thenReturn(dayEntity);

        //when
        dayComponent.planDay(dayEntity);
        //then
        verify(dayRepository, times(1)).save(any(DayEntity.class));

    }

    @Test
    void isDayAlreadyPlannedTrue(){
        //given
        LocalDate date = LocalDate.of(2024,4,15);
        WarehouseEntity warehouse= WarehouseEntity.builder()
                .name("Grenis")
                .letter("G")
                .address(new Address("21 rue des cafards","15002","Bristol"))
                .days(Set.of()).trucks(Set.of())
                .photo(".jpeg")
                .build();
        EmployeeEntity planner= EmployeeEntity.builder()
                .job(Job.PLANNER)
                .photo(".png")
                .email("email@xx.y")
                .trigram("JPL")
                .warehouse(warehouse)
                .mobilePhone("342532")
                .firstName("Massi")
                .lastName("leroy")
                .build();

        DayEntity dayEntity= DayEntity.builder()
                .reference("J124G")
                .date(date)
                .state(DayState.PLANNED)
                .tours(Set.of())
                .planner(planner)
                .build();

        //when
        when(dayRepository.findByDate(any(LocalDate.class))).thenReturn(Optional.of(dayEntity));

        boolean response=dayComponent.isDayAlreadyPlanned(date);

        //then
        assertThat(response).isEqualTo(true);

    }

    @Test
    void  isDayAlreadyPlannedFalse(){
        //given
        LocalDate date = LocalDate.of(2024,4,15);
        //when
        when(dayRepository.findByDate(any(LocalDate.class))).thenReturn(Optional.empty());
        boolean response=dayComponent.isDayAlreadyPlanned(date);
        //then
        assertThat(response).isEqualTo(false);
    }
   

}
