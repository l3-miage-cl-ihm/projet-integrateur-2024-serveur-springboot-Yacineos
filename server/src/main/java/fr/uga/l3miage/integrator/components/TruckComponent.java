package fr.uga.l3miage.integrator.components;

import fr.uga.l3miage.integrator.models.TruckEntity;
import fr.uga.l3miage.integrator.repositories.TruckRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Component
@RequiredArgsConstructor
public class TruckComponent {
    private final TruckRepository truckRepository;

    public Set<String> getAllTrucksImmatriculation(){
        List<TruckEntity> truckEntities = truckRepository.findAll();
        Set<String> immatriculations = new HashSet<>();
        for(TruckEntity truckEntity : truckEntities){
            immatriculations.add(truckEntity.getImmatriculation());
        }
        return immatriculations;

    }
}
