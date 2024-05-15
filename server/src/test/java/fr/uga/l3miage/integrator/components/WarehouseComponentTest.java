package fr.uga.l3miage.integrator.components;

import fr.uga.l3miage.integrator.datatypes.Address;
import fr.uga.l3miage.integrator.datatypes.Coordinates;
import fr.uga.l3miage.integrator.exceptions.technical.WarehouseNotFoundException;
import fr.uga.l3miage.integrator.models.DayEntity;
import fr.uga.l3miage.integrator.models.TourEntity;
import fr.uga.l3miage.integrator.models.TruckEntity;
import fr.uga.l3miage.integrator.models.WarehouseEntity;
import fr.uga.l3miage.integrator.repositories.WarehouseRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class WarehouseComponentTest {

    @Autowired
    private WarehouseComponent warehouseComponent;
    @MockBean
    private WarehouseRepository warehouseRepository;

    @Test
    void getWarehouseOK() {
        WarehouseEntity warehouseEntity = WarehouseEntity.builder()
                .name("Grenis")
                .letter("G")
                .address(new Address("Avenue Oukkaly", "33434", "hmmmmm"))
                .coordinates(new Coordinates(12, 21))
                .build();

        when(warehouseRepository.findById("Grenis")).thenReturn(Optional.of(warehouseEntity));
        WarehouseEntity warehouse = warehouseComponent.getWarehouse("Grenis");

        //then
        assertThat(warehouse).isEqualTo(warehouseEntity);
    }

    @Test
    void getWarehouseNotOK() {
        when(warehouseRepository.findById("Grenis")).thenReturn(Optional.empty());
        assertThrows(WarehouseNotFoundException.class, () -> warehouseComponent.getWarehouse("Grenis"));
    }

    @Test
    void getAllTrucks() {
        //given
        TruckEntity truck = TruckEntity.builder().immatriculation("zefzefzefze").build();
        WarehouseEntity warehouseEntity = WarehouseEntity.builder()
                .name("Grenis")
                .letter("G")
                .trucks(Set.of(truck))
                .address(new Address("Avenue Oukkaly", "33434", "hmmmmm"))
                .coordinates(new Coordinates(12, 21))
                .build();
        //when
        when(warehouseRepository.findById("Grenis")).thenReturn(Optional.of(warehouseEntity));
        Set<String> response = warehouseComponent.getAllTrucks("Grenis");

        //when
        assertThat(response.stream().findFirst().get()).isEqualTo(truck.getImmatriculation());
    }

    @Test
    void getWarehouseCoordinates() {
        //given
        WarehouseEntity warehouseEntity = WarehouseEntity.builder()
                .name("Grenis")
                .letter("G")
                .address(new Address("Avenue Oukkaly", "33434", "hmmmmm"))
                .coordinates(new Coordinates(12, 21))
                .build();
        //when
        when(warehouseRepository.findById("Grenis")).thenReturn(Optional.ofNullable(warehouseEntity));
        Coordinates coordinates = warehouseComponent.getWarehouseCoordinates("Grenis");

        //when
        assertThat(coordinates).isEqualTo(warehouseEntity.getCoordinates());
    }


}
