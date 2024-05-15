package fr.uga.l3miage.integrator.components;

import fr.uga.l3miage.integrator.datatypes.Coordinates;
import fr.uga.l3miage.integrator.exceptions.technical.WarehouseNotFoundException;
import fr.uga.l3miage.integrator.models.TruckEntity;
import fr.uga.l3miage.integrator.models.WarehouseEntity;
import fr.uga.l3miage.integrator.repositories.WarehouseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class WarehouseComponent {
    private final WarehouseRepository warehouseRepository;

    public WarehouseEntity getWarehouse(String id) throws WarehouseNotFoundException{
        return warehouseRepository.findById(id).orElseThrow(() -> new WarehouseNotFoundException("entrepot non trouvé"));
    }

    public Set<String> getAllTrucks(String id) throws WarehouseNotFoundException{
        Set<TruckEntity> truckEntities = this.getWarehouse(id).getTrucks();
        return truckEntities.stream().map(TruckEntity::getImmatriculation).collect(Collectors.toSet());
    }

    public Coordinates getWarehouseCoordinates(String id)throws WarehouseNotFoundException{
        return this.getWarehouse(id).getCoordinates();
    }
}
