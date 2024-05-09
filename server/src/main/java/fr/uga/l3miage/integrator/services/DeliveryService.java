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

@Service
@RequiredArgsConstructor
public class DeliveryService {

    private final DeliveryComponent deliveryComponent;
    private final TourComponent tourComponent;
    public DeliveryEntity updateDeliveryState(DeliveryState deliveryState, String deliveryId,String tourId) throws DeliveryNotFoundRestException,UpdateDeliveryStateRestException {
        try {
            DeliveryEntity deliveryEntity= deliveryComponent.updateDeliveryState(deliveryId, deliveryState,tourId);
            TourEntity tourEntity= tourComponent.findTourById(tourId);
            DeliveryEntity lastDelivery = tourEntity.getDeliveries().get(tourEntity.getDeliveries().size() - 1);
            if(lastDelivery.getReference()==deliveryId && lastDelivery.getState()==DeliveryState.COMPLETED){
                //update tour state
                tourEntity.setState(TourState.COMPLETED);
                tourComponent.saveTour(tourEntity);
            }
            return deliveryEntity;
        }catch(DeliveryNotFoundException e){
            throw  new DeliveryNotFoundRestException(e.getMessage());
        }catch(TourNotFoundException e){
            throw  new TourNotFoundRestException(e.getMessage());
        }catch (UpdateDeliveryStateException e){
            throw  new UpdateDeliveryStateRestException(e.getMessage(),e.getState());
        }


    }
}
