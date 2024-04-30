package fr.uga.l3miage.integrator.mappers;

import fr.uga.l3miage.integrator.datatypes.Address;
import fr.uga.l3miage.integrator.enums.DayState;
import fr.uga.l3miage.integrator.enums.Job;
import fr.uga.l3miage.integrator.models.*;
import fr.uga.l3miage.integrator.repositories.EmployeeRepository;
import fr.uga.l3miage.integrator.repositories.OrderRepository;
import fr.uga.l3miage.integrator.repositories.TruckRepository;
import fr.uga.l3miage.integrator.requests.DayCreationRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.time.LocalDate;
import java.util.Set;


public abstract class DayPlannerMapperDecorator implements DayPlannerMapper {
    //other mappers injection
    @Autowired
    @Qualifier("delegate")
    private DayPlannerMapper delegate;
    @Autowired
    private TourPlannerMapper tourMapper;
    @Autowired
    private DeliveryPlannerMapper deliveryMapper;

    @Autowired
    private TruckRepository truckRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private EmployeeRepository employeeRepository;



    @Override
    public DayEntity toEntity(DayCreationRequest dayCreationRequest) {
        //setting day fields
        DayEntity dayEntity = delegate.toEntity(dayCreationRequest);
        dayEntity.setReference(generateDayReference(dayCreationRequest.getDate()));
        dayEntity.setState(DayState.PLANNED);

        WarehouseEntity grenis =WarehouseEntity.builder().days(Set.of(dayEntity)).photo("grenis.png").name("Grenis").letter("G")
                .address(new Address("21 rue des cafards","65001","San antonio")).trucks(Set.of()).build();

        EmployeeEntity planner = EmployeeEntity.builder().email("christian.paul@grenis.com").job(Job.PLANNER).photo("chris.png")
                .lastName("Paul").firstName("Christian").mobilePhone("0765437876").trigram("CPL").warehouse(grenis).build();

        dayEntity.setPlanner(planner);

        return dayEntity;
    }




    private String generateDayReference(LocalDate date) {
        String dayNumber = String.format("%03d", date.getDayOfYear());
        return 'J' + dayNumber+'G';
    }



}
