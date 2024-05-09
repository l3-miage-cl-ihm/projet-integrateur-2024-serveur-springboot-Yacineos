package fr.uga.l3miage.integrator.components;

import fr.uga.l3miage.integrator.models.DeliveryEntity;
import fr.uga.l3miage.integrator.repositories.DeliveryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class DeliveryComponent {
    private final DeliveryRepository deliveryRepository;
    public String generateDeliveryReference(LocalDate date, int deliveryIndex ,String tourLetter) {
        String dayNumber = String.format("%03d", date.getDayOfYear());
        return 'l' + dayNumber+'G'+'-'+tourLetter+deliveryIndex;
    }

    public void saveDelivery(DeliveryEntity delivery){
        deliveryRepository.save(delivery);
    }
}
