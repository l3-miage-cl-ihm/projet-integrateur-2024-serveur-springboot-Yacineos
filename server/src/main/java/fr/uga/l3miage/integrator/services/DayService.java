package fr.uga.l3miage.integrator.services;

import fr.uga.l3miage.integrator.components.DayComponent;
import fr.uga.l3miage.integrator.components.EmployeeComponent;
import fr.uga.l3miage.integrator.components.OrderComponent;
import fr.uga.l3miage.integrator.components.TruckComponent;
import fr.uga.l3miage.integrator.dataTypes.MultipleOrder;
import fr.uga.l3miage.integrator.mappers.DayPlannerMapper;
import fr.uga.l3miage.integrator.models.DayEntity;
import fr.uga.l3miage.integrator.repositories.DayRepository;
import fr.uga.l3miage.integrator.requests.DayCreationRequest;
import fr.uga.l3miage.integrator.responses.DayResponseDTO;
import fr.uga.l3miage.integrator.responses.SetUpBundleResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DayService {

    private final DayComponent dayComponent;
    private final DayPlannerMapper dayMapper;
    private final EmployeeComponent employeeComponent;
    private final OrderComponent orderComponent;
    private final TruckComponent truckComponent;

    public void planDay(DayCreationRequest dayCreationRequest){


    }
    public SetUpBundleResponse getSetUpBundle(){

        SetUpBundleResponse setUpBundleResponse = new SetUpBundleResponse();


        Set<MultipleOrder> multipleOrder = orderComponent.createMultipleOrders();

        Set<String> immatriculationTrucks = truckComponent.getAllTrucksImmatriculation();

        Set<String> idLivreurs = employeeComponent.getAllDeliveryMenID();


        setUpBundleResponse.setMultipleOrders(multipleOrder);

        setUpBundleResponse.setDeliverymen(idLivreurs);
        setUpBundleResponse.setTruck(immatriculationTrucks);
        return setUpBundleResponse ;
    }
    /*public DayResponseDTO getDay(LocalDate date){


            DayEntity day = dayComponent.getDay(date);
            return dayMapper.toResponse(day);

    }

     */
}
