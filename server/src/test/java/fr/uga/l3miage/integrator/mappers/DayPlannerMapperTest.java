package fr.uga.l3miage.integrator.mappers;

import fr.uga.l3miage.integrator.datatypes.Address;
import fr.uga.l3miage.integrator.enums.CustomerState;
import fr.uga.l3miage.integrator.enums.DayState;
import fr.uga.l3miage.integrator.enums.Job;
import fr.uga.l3miage.integrator.enums.OrderState;
import fr.uga.l3miage.integrator.mappers.utils.DayPlannerMapperUtils;
import fr.uga.l3miage.integrator.mappers.utils.TourDMMapperUtils;
import fr.uga.l3miage.integrator.models.*;
import fr.uga.l3miage.integrator.repositories.*;
import fr.uga.l3miage.integrator.requests.DayCreationRequest;
import fr.uga.l3miage.integrator.requests.DeliveryCreationRequest;
import fr.uga.l3miage.integrator.requests.TourCreationRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class DayPlannerMapperTest {

    @Autowired
    private  DayPlannerMapper dayPlannerMapper;

    @Autowired
    private DayPlannerMapperUtils dayPlannerMapperUtils;

    @Test
    void dayCreationReuest_To_DayEntity(){
        //given
        LocalDate now = LocalDate.now();
        TourCreationRequest tour1 = TourCreationRequest.builder().distanceToCover(40).build();
        DayCreationRequest dayCreationRequest = DayCreationRequest.builder().date(LocalDate.now()).tours(Set.of(tour1)).build();


        WarehouseEntity grenis =WarehouseEntity.builder().days(Set.of()).photo("grenis.png").name("Grenis").letter("G")
                .address(new Address("21 rue des cafards","65001","San antonio")).trucks(Set.of()).build();

        EmployeeEntity planner = EmployeeEntity.builder().email("christian.paul@grenis.com").job(Job.PLANNER).photo("chris.png")
                .lastName("Paul").firstName("Christian").mobilePhone("0765437876").trigram("CPL").warehouse(grenis).build();

        DayEntity expectedResponse= DayEntity.builder().reference(dayPlannerMapperUtils.generateDayReference(LocalDate.now())).state(DayState.PLANNED).date(now).planner(planner).tours(Set.of()).build();
        DayEntity dayEntityResponse=dayPlannerMapper.toEntity(dayCreationRequest);

        //then
        assertThat(dayEntityResponse.getReference()).isEqualTo(expectedResponse.getReference());
        assertThat(dayEntityResponse.getPlanner().getEmail()).isEqualTo(expectedResponse.getPlanner().getEmail());
    }

}
