package fr.uga.l3miage.integrator.mappers;
import fr.uga.l3miage.integrator.datatypes.Address;
import fr.uga.l3miage.integrator.enums.DayState;
import fr.uga.l3miage.integrator.enums.Job;
import fr.uga.l3miage.integrator.mappers.utils.DayPlannerMapperUtils;
import fr.uga.l3miage.integrator.mappers.utils.TourDMMapperUtils;
import fr.uga.l3miage.integrator.models.*;
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
    private DayPlannerMapperUtils dayPlannerMapperUtils;
    @Override
    public DayEntity toEntity(DayCreationRequest dayCreationRequest) {
        //setting day fields
        DayEntity dayEntity = delegate.toEntity(dayCreationRequest);
        dayEntity.setReference(dayPlannerMapperUtils.generateDayReference(dayCreationRequest.getDate()));
        dayEntity.setState(DayState.PLANNED);

        //After setting up the main to feed the db, register the planner and the warehouse from the main. So I have just to find them with
        // their respective repositories in the db and add the planned to the dayEntity. Because if we want to plan another day, according to this current implementation
        //the mapper creates the same planner and this should throw an exception because he is already exist. That's why I need to feed the db first from the main of the
        //spring boot application with the data with the planner and the warehouse.
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
