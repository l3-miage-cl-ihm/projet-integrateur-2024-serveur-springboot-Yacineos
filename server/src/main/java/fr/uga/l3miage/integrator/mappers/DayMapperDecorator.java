package fr.uga.l3miage.integrator.mappers;

import fr.uga.l3miage.integrator.dataTypes.Address;
import fr.uga.l3miage.integrator.enums.DayState;
import fr.uga.l3miage.integrator.enums.DeliveryState;
import fr.uga.l3miage.integrator.enums.Job;
import fr.uga.l3miage.integrator.enums.TourState;
import fr.uga.l3miage.integrator.models.*;
import fr.uga.l3miage.integrator.repositories.EmployeeRepository;
import fr.uga.l3miage.integrator.repositories.OrderRepository;
import fr.uga.l3miage.integrator.repositories.TruckRepository;
import fr.uga.l3miage.integrator.requests.DayCreationRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@RequiredArgsConstructor
public abstract  class DayMapperDecorator implements DayPlannerMapper {
    //other mappers injection
    private final DayPlannerMapper delegate;
    private final TourPlannerMapper tourMapper;
    private final DeliveryPlannerMapper deliveryMapper;
    private final TruckRepository truckRepository;
    private final EmployeeRepository employeeRepository;
    private final OrderRepository orderRepository;

    @Override
    public DayEntity toEntity(DayCreationRequest dayCreationRequest) {
        //setting day fields
        DayEntity dayEntity = delegate.toEntity(dayCreationRequest);
        dayEntity.setDate(dayCreationRequest.getDate());
        dayEntity.setReference(generateDayReference(dayCreationRequest.getDate()));
        dayEntity.setState(DayState.PLANNED);

        WarehouseEntity grenis =WarehouseEntity.builder().days(Set.of(dayEntity)).photo("grenis.png").name("Grenis").letter("G")
                .address(new Address("21 rue des cafards","65001","San antonio")).trucks(Set.of()).build();

        EmployeeEntity planner = EmployeeEntity.builder().email("christian.paul@grenis.com").job(Job.PLANNER).photo("chris.png")
                .lastName("Paul").firstName("Christian").mobilePhone("0765437876").trigram("CPL").warehouse(grenis).build();

        dayEntity.setPlanner(planner);

        //setting tour fields
        AtomicInteger tourIndex = new AtomicInteger(0);
        dayCreationRequest.getTours().forEach(tour ->{
            TourEntity tourEntity = tourMapper.toEntity(tour);  //solution 2 mapper la tour puis rajouter sa réference
            String tourReference = generateTourReference(dayCreationRequest.getDate(), tourIndex.get());
            tourEntity.setReference(tourReference);
            tourEntity.setTruck(truckRepository.findById(tour.getTruck()).get()); //truck must exist
            tourEntity.setState(TourState.PLANNED);
            tourEntity.setLetter( Character.toString(tourReference.charAt(tourReference.length()-1)));

            //setting delivery fields
            Set<DeliveryEntity> deliveries = new HashSet<>();
            AtomicInteger deliveryIndex = new AtomicInteger(0);
            tour.getDeliveries().forEach(delivery->{
                DeliveryEntity deliveryEntity = deliveryMapper.toEntity(delivery);
                deliveryEntity.setState(DeliveryState.PLANNED);
                deliveryEntity.setReference( generateDeliveryReference(dayCreationRequest.getDate(),deliveryIndex.get()));


                //setting order fields
                Set<OrderEntity> orders = new HashSet<>();
                delivery.getOrders().forEach(orderId->{
                    orderRepository.findById(orderId).ifPresent(orders::add);
                });

                deliveryEntity.setOrders(orders);
                deliveries.add(deliveryEntity);
                deliveryIndex.getAndIncrement();
            });

            //setting deliverymen (Employee) fields
            Set<EmployeeEntity> deliverymen=new HashSet<>();
            tour.getDeliverymen().forEach(deliverymanId->{
                employeeRepository.findById(deliverymanId).ifPresent(deliverymen::add);

            });
            tourEntity.setDeliveries(deliveries);
            tourEntity.setDeliverymen(deliverymen);

            dayEntity.getTours().add(tourEntity);
            tourIndex.getAndIncrement();
        });

        return dayEntity;
    }



    //some utils fucntions
    private String generateTourReference(LocalDate date, int tourIndex) {
        String dayNumber = String.format("%03d", date.getDayOfYear());
        char letter = (char) ('A' + tourIndex);
        return "t" + dayNumber + "-G" + letter;
    }

    private String generateDayReference(LocalDate date) {
        String dayNumber = String.format("%03d", date.getDayOfYear());
        return 'J' + dayNumber+'G';
    }

    private String generateDeliveryReference(LocalDate date,int deliveryIndex ) {
        String dayNumber = String.format("%03d", date.getDayOfYear());
        return 'l' + dayNumber+'G'+'-'+'A'+deliveryIndex;
    }




}
