package fr.uga.l3miage.integrator.mappers;
import fr.uga.l3miage.integrator.datatypes.Address;
import fr.uga.l3miage.integrator.enums.DayState;
import fr.uga.l3miage.integrator.enums.Job;
import fr.uga.l3miage.integrator.exceptions.rest.DayCreationRestException;
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
    private EmployeeRepository employeeRepository;
    @Override
    public DayEntity toEntity(DayCreationRequest dayCreationRequest) {
        DayEntity dayEntity = delegate.toEntity(dayCreationRequest);
        dayEntity.setState(DayState.PLANNED);
        dayEntity.setPlanner(employeeRepository.findById("STR").orElseThrow(()->new DayCreationRestException("No planner was found with given trigram <STR> !")));//We supposed to work with only one warehouse which is "GRENIS".
        return dayEntity;
    }




}
