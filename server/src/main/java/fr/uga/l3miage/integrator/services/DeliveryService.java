package fr.uga.l3miage.integrator.services;

import fr.uga.l3miage.integrator.components.DeliveryComponent;
import fr.uga.l3miage.integrator.components.TourComponent;
import fr.uga.l3miage.integrator.enums.DeliveryState;
import fr.uga.l3miage.integrator.enums.TourState;
import fr.uga.l3miage.integrator.exceptions.rest.DeliveryNotFoundRestException;
import fr.uga.l3miage.integrator.exceptions.rest.TourNotFoundRestException;
import fr.uga.l3miage.integrator.exceptions.rest.UpdateDeliveryStateRestException;
import fr.uga.l3miage.integrator.exceptions.technical.DeliveryNotFoundException;
import fr.uga.l3miage.integrator.exceptions.technical.TourNotFoundException;
import fr.uga.l3miage.integrator.exceptions.technical.UpdateDeliveryStateException;
import fr.uga.l3miage.integrator.models.DeliveryEntity;
import fr.uga.l3miage.integrator.models.TourEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DeliveryService {

    private final DeliveryComponent deliveryComponent;
    private final TourComponent tourComponent;

    public DeliveryEntity updateDeliveryState(DeliveryState deliveryState, String deliveryId, String tourId) throws DeliveryNotFoundRestException, UpdateDeliveryStateRestException {
        try {
            DeliveryEntity deliveryEntity = deliveryComponent.updateDeliveryState(deliveryId, deliveryState, tourId);
            TourEntity tourEntity = tourComponent.findTourById(tourId);
            List<DeliveryEntity> deliveries = tourEntity.getDeliveries();
            switch (deliveryEntity.getState()) {
                case IN_COURSE:
                    tourEntity.setState(TourState.IN_COURSE);
                    break;

                case UNLOADING:
                    tourEntity.setState(TourState.UNLOADING);
                    break;

                case WITH_CUSTOMER:
                    tourEntity.setState(TourState.CUSTOMER);
                    break;

                case ASSEMBLY:
                    tourEntity.setState(TourState.ASSEMBLY);
                    break;

                default:
                    break;
            }

            if (deliveries.stream().allMatch(delivery -> delivery.getState() == DeliveryState.COMPLETED)) {
                tourEntity.setState(TourState.COMPLETED);

            }
            tourComponent.saveTour(tourEntity);
            return deliveryEntity;
        } catch (DeliveryNotFoundException e) {
            throw new DeliveryNotFoundRestException(e.getMessage());
        } catch (TourNotFoundException e) {
            throw new TourNotFoundRestException(e.getMessage());
        } catch (UpdateDeliveryStateException e) {
            throw new UpdateDeliveryStateRestException(e.getMessage(), e.getState());
        }


    }
}
