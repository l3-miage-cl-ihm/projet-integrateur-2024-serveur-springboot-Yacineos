package fr.uga.l3miage.integrator.controllers;

import fr.uga.l3miage.integrator.endpoints.DeliverymanEndpoints;
import fr.uga.l3miage.integrator.responses.DayResponseDTO;
import fr.uga.l3miage.integrator.responses.DeliveryDMResponseDTO;
import fr.uga.l3miage.integrator.responses.TourDMResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Controller
@RequiredArgsConstructor
public class DeliverymanControllers implements DeliverymanEndpoints {
    @Override
    public TourDMResponseDTO getTour(String email) {
        //tour 1
        TourDMResponseDTO mockedTourResponse=new TourDMResponseDTO();
        mockedTourResponse.setRefTour("t028G-A");
        mockedTourResponse.setRefDay("j028G");
        mockedTourResponse.setTruck("XY-456-ZT");
        mockedTourResponse.setWarehouseName("Grenis");
        Set<String> deliverymen = new HashSet<>();
        deliverymen.add("EBD");
        deliverymen.add("SWL");
        mockedTourResponse.setDeliverymen(deliverymen);

        //delivery 1
        DeliveryDMResponseDTO d1= new DeliveryDMResponseDTO();
        d1.setCustomer("Axel Gilles");
        d1.setDeliveryId("I028G-A1");
        d1.setCustomerAddress("160bis Avenue Jean Jaures, Eybens");

        Set<String> orders1 = new HashSet<>();
        orders1.add("c053");
        d1.setOrders(orders1);

        Set<DeliveryDMResponseDTO> deliveries= new HashSet<>();

        //delivery 2
        DeliveryDMResponseDTO d2= new DeliveryDMResponseDTO();
        d2.setCustomer("Ahmed ZAINAB");
        d2.setDeliveryId("I028G-A2");
        d2.setCustomerAddress("9 chemin des vouillants, Fontaine");
        Set<String> orders2 = new HashSet<>();
        orders2.add("c002");
        orders2.add("c273");
        orders2.add("c289");
        d2.setOrders(orders2);

        deliveries.add(d1);
        deliveries.add(d2);

        mockedTourResponse.setDeliveries(deliveries);

        return  mockedTourResponse;

    }
}
