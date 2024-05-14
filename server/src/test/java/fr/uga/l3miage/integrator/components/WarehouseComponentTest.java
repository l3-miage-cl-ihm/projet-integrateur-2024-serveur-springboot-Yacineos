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
    void getWarehouseOK(){
        TourEntity tour = TourEntity.builder().reference("sdfze").build();
        TourEntity tour2 = TourEntity.builder().reference("sdfze").build();
        DayEntity day = DayEntity.builder().reference("zeefz").tours(List.of(tour)).build();
        DayEntity day1 = DayEntity.builder().reference("zeeEfz").tours(List.of(tour2)).build();
        WarehouseEntity warehouseEntity = WarehouseEntity.builder()
                .name("Grenis")
                .letter("G")
                .days(Set.of(day))
                .address(new Address("Avenue Oukkaly","33434","hmmmmm"))
                .coordinates(new Coordinates(12,21))
                .photo("oukkal.png")
                .build();
        WarehouseEntity warehouseEntity2 = WarehouseEntity.builder()
                .name("Paris")
                .letter("P")
                .days(Set.of(day1))
                .trucks(Set.of())
                .address(new Address("Avenue Oukkaly","33434","hmmmmmm"))
                .coordinates(new Coordinates(21,21))
                .photo("oukkal.png")
                .build();

        when(warehouseRepository.findById("Grenis")).thenReturn(Optional.ofNullable(warehouseEntity));
        WarehouseEntity warehouse = warehouseComponent.getWarehouse("Grenis");

        assertThat(warehouse).isEqualTo(warehouseEntity);
    }
    @Test
    void getWarehouseNotOK(){
        when(warehouseRepository.findById("Grenis")).thenReturn(Optional.empty());

        assertThrows(WarehouseNotFoundException.class,()-> warehouseComponent.getWarehouse("Grenis"));
    }

    @Test
    void getAllTrucks(){
        TruckEntity truck = TruckEntity.builder().immatriculation("zefzefzefze").build();
        TourEntity tour = TourEntity.builder().reference("sdfze").build();
        DayEntity day = DayEntity.builder().reference("zeefz").tours(List.of(tour)).build();
        WarehouseEntity warehouseEntity = WarehouseEntity.builder()
                .name("Grenis")
                .letter("G")
                .days(Set.of(day))
                .trucks(Set.of(truck))
                .address(new Address("Avenue Oukkaly","33434","hmmmmm"))
                .coordinates(new Coordinates(12,21))
                .photo("oukkal.png")
                .build();
        when(warehouseRepository.findById("Grenis")).thenReturn(Optional.ofNullable(warehouseEntity));
        Set<String> response = warehouseComponent.getAllTrucks("Grenis");

        assertThat(response.stream().findFirst().get()).isEqualTo(truck.getImmatriculation());
    }
    @Test
    void getWarehouseCoordinates(){
        TruckEntity truck = TruckEntity.builder().immatriculation("zefzefzefze").build();
        TourEntity tour = TourEntity.builder().reference("sdfze").build();
        DayEntity day = DayEntity.builder().reference("zeefz").tours(List.of(tour)).build();
        WarehouseEntity warehouseEntity = WarehouseEntity.builder()
                .name("Grenis")
                .letter("G")
                .days(Set.of(day))
                .trucks(Set.of(truck))
                .address(new Address("Avenue Oukkaly","33434","hmmmmm"))
                .coordinates(new Coordinates(12,21))
                .photo("oukkal.png")
                .build();
        when(warehouseRepository.findById("Grenis")).thenReturn(Optional.ofNullable(warehouseEntity));
        Coordinates coordinates = warehouseComponent.getWarehouseCoordinates("Grenis");
        assertThat(coordinates).isEqualTo(warehouseEntity.getCoordinates());
    }


}
