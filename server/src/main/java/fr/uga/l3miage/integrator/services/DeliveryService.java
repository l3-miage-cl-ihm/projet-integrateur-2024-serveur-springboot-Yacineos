package fr.uga.l3miage.integrator.services;

import fr.uga.l3miage.integrator.components.DeliveryComponent;
import fr.uga.l3miage.integrator.components.TourComponent;
import fr.uga.l3miage.integrator.enums.DeliveryState;
import fr.uga.l3miage.integrator.exceptions.rest.DeliveryNotFoundRestException;
import fr.uga.l3miage.integrator.exceptions.rest.EntityNotFoundRestException;
import fr.uga.l3miage.integrator.exceptions.rest.UpdateDeliveryStateRestException;
import fr.uga.l3miage.integrator.exceptions.technical.DeliveryNotFoundException;
import fr.uga.l3miage.integrator.exceptions.technical.UpdateDeliveryStateException;
import fr.uga.l3miage.integrator.models.DeliveryEntity;
import lombok.RequiredArgsConstructor;
import net.bytebuddy.implementation.bytecode.Throw;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeliveryService {

    private final DeliveryComponent deliveryComponent;
    public void updateDeliveryState(DeliveryState deliveryState, String deliveryId) throws DeliveryNotFoundRestException,UpdateDeliveryStateRestException {
        try {
            deliveryComponent.updateDeliveryState(deliveryId, deliveryState);
        }catch(DeliveryNotFoundException e){
            throw  new DeliveryNotFoundRestException(e.getMessage());
        }catch (UpdateDeliveryStateException e){
            throw  new UpdateDeliveryStateRestException(e.getMessage(),e.getState());
        }


    }
}
