package fr.uga.l3miage.integrator.mappers;

public abstract class TourPlannerMapperDecorator implements TourPlannerMapper {
/*
    @Autowired
    @Qualifier("delegate")
    private TourPlannerMapper delegate;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private TruckRepository truckRepository;

    @Override
    public TourEntity toEntity(TourCreationRequest tourCreationRequest, String tourRef) throws InvalidInputValueException {
        TourEntity tourEntity= delegate.toEntity(tourCreationRequest,tourRef);
        tourEntity.setReference(tourRef);
        tourEntity.setState(TourState.PLANNED);

        TruckEntity truck=truckRepository.findById(tourCreationRequest.getTruck()).orElseThrow(()-> new InvalidInputValueException("Truck <"+tourCreationRequest.getTruck()+"> not found !"));
        tourEntity.setTruck(truck);

        Set<EmployeeEntity> deliverymen= new HashSet<>();
        tourCreationRequest.getDeliverymen()
                .forEach(deliverymanTrigram -> {
                    try {
                        EmployeeEntity deliveryman= employeeRepository
                                .findById(deliverymanTrigram).orElseThrow(()-> new InvalidInputValueException("No deliveryman was found with given trigram <"+deliverymanTrigram+">"));
                        deliverymen.add(deliveryman);
                    } catch (InvalidInputValueException e) {
                        throw new DayCreationRestException(e.getMessage());
                    }

                });

        tourEntity.setDeliverymen(deliverymen);
        return tourEntity;
    }*/
}
