package fr.uga.l3miage.integrator.components;

import fr.uga.l3miage.integrator.enums.DeliveryState;
import fr.uga.l3miage.integrator.enums.TourState;
import fr.uga.l3miage.integrator.exceptions.technical.DeliveryNotFoundException;
import fr.uga.l3miage.integrator.exceptions.technical.TourNotFoundException;
import fr.uga.l3miage.integrator.exceptions.technical.UpdateDeliveryStateException;
import fr.uga.l3miage.integrator.models.DeliveryEntity;
import fr.uga.l3miage.integrator.models.TourEntity;
import fr.uga.l3miage.integrator.repositories.DeliveryRepository;
import fr.uga.l3miage.integrator.repositories.TourRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class DeliveryComponent {
    private final DeliveryRepository deliveryRepository;
    private final TourRepository tourRepository;
    public String generateDeliveryReference(LocalDate date, int deliveryIndex ,String tourLetter) {
        String dayNumber = String.format("%03d", date.getDayOfYear());
        return 'l' + dayNumber+'G'+'-'+tourLetter+deliveryIndex;
    }

    public void saveDelivery(DeliveryEntity delivery){
        deliveryRepository.save(delivery);
    }

    public DeliveryEntity updateDeliveryState(String deliveryId, DeliveryState newState,String tourId) throws DeliveryNotFoundException, UpdateDeliveryStateException {
        DeliveryEntity deliveryEntity=deliveryRepository.findById(deliveryId).orElseThrow(()->new DeliveryNotFoundException("No delivery was found with given reference <"+deliveryId+">"));
        //do some logic and throw UpdateDeliverySTateException if necessary
        DeliveryState currentState= deliveryEntity.getState();
        switch (currentState){
            case PLANNED:
                if( newState == DeliveryState.IN_COURSE ){
                    deliveryEntity.setState(newState);
                } else{
                    throw new UpdateDeliveryStateException("Cannot switch from "+currentState+ " into <" + newState+">", deliveryEntity.getState());
                }
                break;


            case IN_COURSE:
                if( newState == DeliveryState.UNLOADING || newState == DeliveryState.PLANNED){
                    deliveryEntity.setState(newState);
                } else{
                    throw new UpdateDeliveryStateException("Cannot switch from "+currentState+ " into <" + newState+">", deliveryEntity.getState());
                }
                break;

            case UNLOADING, ASSEMBLY:
                if( newState == DeliveryState.WITH_CUSTOMER ){
                    deliveryEntity.setState(newState);
                } else{
                    throw new UpdateDeliveryStateException("Cannot switch from "+currentState+ "into <" + newState+">", deliveryEntity.getState());
                }
                break;

            case WITH_CUSTOMER:
                if( newState == DeliveryState.ASSEMBLY || newState == DeliveryState.COMPLETED  ){
                    deliveryEntity.setState(newState);
                } else{
                    throw new UpdateDeliveryStateException("Cannot switch from "+currentState+ "into <" + newState+">", deliveryEntity.getState());
                }
                break;

            default:
                throw new UpdateDeliveryStateException("Delivery is already completed !", deliveryEntity.getState());

        }



        return deliveryRepository.save(deliveryEntity);
    }


}
