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
import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class DayComponentTest {
    @Autowired
    private DayComponent dayComponent;
    @MockBean
    private DayRepository dayRepository;

    @Test
    void planDayOK(){
        //given
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
                .date(LocalDate.now())
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
                .date(LocalDate.now())
                .state(DayState.PLANNED)
                .tours(Set.of())
                .planner(planner)
                .build();

        //when
        when(dayRepository.findByDate(any(LocalDate.class))).thenReturn(Optional.of(dayEntity));

        boolean response=dayComponent.isDayAlreadyPlanned(LocalDate.now());

        //then
        assertThat(response).isEqualTo(true);

    }

    @Test
    void  isDayAlreadyPlannedFalse(){
        //when
        when(dayRepository.findByDate(any(LocalDate.class))).thenReturn(Optional.empty());
        boolean response=dayComponent.isDayAlreadyPlanned(LocalDate.now());
        //then
        assertThat(response).isEqualTo(false);
    }

}
