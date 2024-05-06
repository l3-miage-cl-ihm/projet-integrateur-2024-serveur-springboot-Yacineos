package fr.uga.l3miage.integrator.mappers;
import fr.uga.l3miage.integrator.datatypes.Address;
import fr.uga.l3miage.integrator.enums.DayState;
import fr.uga.l3miage.integrator.enums.Job;
import fr.uga.l3miage.integrator.mappers.utils.DayPlannerMapperUtils;
import fr.uga.l3miage.integrator.mappers.utils.TourDMMapperUtils;
import fr.uga.l3miage.integrator.models.*;
import fr.uga.l3miage.integrator.repositories.EmployeeRepository;
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
    @Autowired
    private EmployeeRepository employeeRepository;
    @Override
    public DayEntity toEntity(DayCreationRequest dayCreationRequest) {
        //setting day fields
        DayEntity dayEntity = delegate.toEntity(dayCreationRequest);
        dayEntity.setReference(dayPlannerMapperUtils.generateDayReference(dayCreationRequest.getDate()));
        dayEntity.setState(DayState.PLANNED);
        EmployeeEntity planner =employeeRepository.findById("STR").get() ; //We supposed to work with only one warehouse which is "GRENIS".

        dayEntity.setPlanner(planner);

        return dayEntity;
    }

    private String generateDayReference(LocalDate date) {
        String dayNumber = String.format("%03d", date.getDayOfYear());
        return 'J' + dayNumber+'G';
    }



}
