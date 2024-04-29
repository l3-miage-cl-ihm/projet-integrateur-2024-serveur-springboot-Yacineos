package fr.uga.l3miage.integrator.controllers;

import fr.uga.l3miage.integrator.endpoints.PlannerEndpoints;
import fr.uga.l3miage.integrator.requests.DayCreationRequest;
import fr.uga.l3miage.integrator.responses.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;

import java.time.LocalDate;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Controller
@RequiredArgsConstructor
public class PlannerControllers implements PlannerEndpoints {
    @Override
    public DayResponseDTO getDay(LocalDate date) {

        DayResponseDTO mockedDayResponse= new DayResponseDTO();
        mockedDayResponse.setDate(LocalDate.now());
        //tour 1
        TourPlannerResponseDTO tour1 = new TourPlannerResponseDTO();
        tour1.setRefTour("t028G-A");
        tour1.setTruck("XY-456-ZT");
        tour1.setDistanceToCover(76.2);

        Set<String> deliverymen = new HashSet<>();
        deliverymen.add("EBD");
        deliverymen.add("SWL");
        tour1.setDeliveryMen(deliverymen);

        //delivery 1
        DeliveryPlannerResponseDTO d1= new DeliveryPlannerResponseDTO();
        Set<String> orders1 = new HashSet<>();
        orders1.add("c053");
        d1.setOrders(orders1);
        d1.setDistanceToCover(6.2);

        //delivery 2
        DeliveryPlannerResponseDTO d2= new DeliveryPlannerResponseDTO();
        Set<String> orders2 = new HashSet<>();
        orders2.add("c002");
        orders2.add("c273");
        orders2.add("c289");
        d2.setOrders(orders2);
        d2.setDistanceToCover(20.0);

        //delivery 3
        DeliveryPlannerResponseDTO d3= new DeliveryPlannerResponseDTO();
        Set<String> orders3 = new HashSet<>();
        orders3.add("c105");
        d3.setOrders(orders3);
        d3.setDistanceToCover(50.0);

        Set<DeliveryPlannerResponseDTO> deliveries1= new HashSet<>();
        deliveries1.add(d1);
        deliveries1.add(d2);
        deliveries1.add(d3);

        //tour2
        TourPlannerResponseDTO tour2 = new TourPlannerResponseDTO();
        tour2.setRefTour("t028G-B");
        tour2.setTruck("XT-483-AE");
        tour2.setDistanceToCover(20.0);

        Set<String> deliverymen2 = new HashSet<>();
        deliverymen2.add("EDT");
        deliverymen2.add("SOP");
        tour2.setDeliveryMen(deliverymen2);


        //delivery 4
        DeliveryPlannerResponseDTO d4= new DeliveryPlannerResponseDTO();
        Set<String> orders4 = new HashSet<>();
        orders4.add("c057");
        d4.setOrders(orders4);
        d4.setDistanceToCover(8.0);

        //delivery 5
        DeliveryPlannerResponseDTO d5= new DeliveryPlannerResponseDTO();
        Set<String> orders5 = new HashSet<>();
        orders5.add("c132");
        orders5.add("c233");
        orders5.add("c209");
        d5.setOrders(orders5);
        d5.setDistanceToCover(12.0);


        Set<DeliveryPlannerResponseDTO> deliveries2= new HashSet<>();
        deliveries2.add(d4);
        deliveries2.add(d5);


        tour1.setDeliveries(deliveries1);
        tour2.setDeliveries(deliveries2);

        Set<TourPlannerResponseDTO> tours = new HashSet<>();
        tours.add(tour1);
        tours.add(tour2);

        mockedDayResponse.setTours(tours);
        return  mockedDayResponse;
    }

    @Override
    public void planDay(DayCreationRequest dayCreationRequest) {
        //Just print the received request body
        System.out.println("Day:"+ dayCreationRequest.getDate());
        System.out.println("Camions des tournée :");
        dayCreationRequest.getTours().forEach(tour -> System.out.println(tour.getTruck()));

    }

    @Override
    public SetUpBundleResponse getSetUpBundle() {

        SetUpBundleResponse mockedSetUpBundleResponse = new SetUpBundleResponse();

        Set<String> trucks = new HashSet<>();
        trucks.add("ZE-765-TR");
        trucks.add("OY-435-FD");
        trucks.add("JO-435-FZ");
        trucks.add("OY-435-FU");
        trucks.add("YT-485-FD");
        trucks.add("BH-009-AZ");
        trucks.add("MQ-430-LK");
        mockedSetUpBundleResponse.setTrucks(trucks);

        Set<String> deliverymen= new HashSet<>();
        deliverymen.add("TRS");
        deliverymen.add("JUH");
        deliverymen.add("OKJ");
        deliverymen.add("PMS");
        deliverymen.add("CSX");
        deliverymen.add("UHE");
        deliverymen.add("MLK");
        deliverymen.add("LKD");
        deliverymen.add("AER");
        mockedSetUpBundleResponse.setDeliverymen(deliverymen);

        Set<Set<String>> multipleOrders= new HashSet<>();

        Set<String> orders1= new HashSet<>();
        orders1.add("c765");
        orders1.add("c436");

        Set<String> orders2= new HashSet<>();
        orders2.add("c75");
        orders2.add("c43");
        orders2.add("c498");

        Set<String> orders3= new HashSet<>();
        orders3.add("c759");


        multipleOrders.add(orders1);
        multipleOrders.add(orders2);
        multipleOrders.add(orders3);

        mockedSetUpBundleResponse.setMultipleOrders(multipleOrders);

        return mockedSetUpBundleResponse;
    }



}
