package fr.uga.l3miage.integrator.components;

import fr.uga.l3miage.integrator.models.TruckEntity;
import fr.uga.l3miage.integrator.repositories.TruckRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureTestDatabase
public class TruckComponentTest {

    @Autowired
    private TruckComponent truckComponent;

    @MockBean
    private TruckRepository truckRepository;

    @Test
    void getAllTrucksImmatriculation(){

        TruckEntity t1 = TruckEntity.builder()
                .immatriculation("ABC")
                .build();
        TruckEntity t2 = TruckEntity.builder()
                .immatriculation("DEF")
                .build();
        TruckEntity t3 = TruckEntity.builder()
                .immatriculation("GHI")
                .build();
        TruckEntity t4 = TruckEntity.builder()
                .immatriculation("JKL")
                .build();

        List<TruckEntity> truckEntities = new ArrayList<>();
        truckEntities.add(t1);
        truckEntities.add(t2);
        truckEntities.add(t3);
        truckEntities.add(t4);

        Set<String> truckImmat = new HashSet<>();
        truckImmat.add(t1.getImmatriculation());
        truckImmat.add(t2.getImmatriculation());
        truckImmat.add(t3.getImmatriculation());
        truckImmat.add(t4.getImmatriculation());

        when(truckRepository.findAll()).thenReturn(truckEntities);
        Set<String> stringSet = truckComponent.getAllTrucksImmatriculation();

        assertThat(stringSet.size()).isEqualTo(4);
        assertThat(stringSet.stream().findFirst()).isEqualTo(truckImmat.stream().findFirst());



    }
}
