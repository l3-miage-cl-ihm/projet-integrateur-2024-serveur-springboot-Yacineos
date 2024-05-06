package fr.uga.l3miage.integrator;

import fr.uga.l3miage.integrator.datatypes.Address;
import fr.uga.l3miage.integrator.enums.CustomerState;
import fr.uga.l3miage.integrator.enums.Job;
import fr.uga.l3miage.integrator.enums.OrderState;
import fr.uga.l3miage.integrator.models.*;
import fr.uga.l3miage.integrator.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Set;

/**
 * Correspond au main de l'application et donc ce qui va la lancer
 * Les Annotations :
 * <ul>
 *     <li>{@link SpringBootApplication} permet de dire à spring que cette classe est le run de l'application</li>
 * </ul>
 */
@SpringBootApplication
public class IntegratorProjectSpringApplication {

    private final TruckRepository truckRepository;

    private final OrderRepository orderRepository;

    private final EmployeeRepository employeeRepository;
    private final WarehouseRepository warehouseRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    @Autowired
    public IntegratorProjectSpringApplication(TruckRepository truckRepository,OrderRepository orderRepository,EmployeeRepository employeeRepository,WarehouseRepository warehouseRepository,CustomerRepository customerRepository,ProductRepository productRepository) {
        this.truckRepository = truckRepository;
        this.orderRepository=orderRepository;
        this.employeeRepository=employeeRepository;
        this.productRepository=productRepository;
        this.warehouseRepository=warehouseRepository;
        this.customerRepository=customerRepository;
    }


    public static void main(String[] args) {
        SpringApplication.run(IntegratorProjectSpringApplication.class,args);

    }



    public void saveTrucksFromCsv(String filePath) throws IOException {
        String line="";
        int i=1;
        try{
            BufferedReader reader = new BufferedReader(new FileReader(filePath));
            reader.readLine(); //skip the header (column names)
            while ((line = reader.readLine()) !=null){
                    String  [] row =line.split(",");
                    TruckEntity truck = TruckEntity.builder().immatriculation(row[0]).build();
                    truckRepository.save(truck);
                    //System.out.println(" truck "+i+ ": ****** "+truckRepository.findById(truck.getImmatriculation()).get().getImmatriculation());
                    i++;
            }


        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public void saveWarehousesFromCsv(String filePath)  {
        String line="";
        int i=1;
        try{
            BufferedReader reader = new BufferedReader(new FileReader(filePath));
            reader.readLine(); //skip the header (column names)
            while ((line = reader.readLine()) !=null){
                String  [] row=line.split(",");
                String name=row[0];
                String letter = row[1];
                String photo = row[2];
                String addressCsv = row[3];
                String postalCode = row[4];
                String city = row[5];
                Address address= new Address(addressCsv,postalCode,city);
                WarehouseEntity warehouse = WarehouseEntity.builder()
                        .days(Set.of())
                        .trucks(Set.of())
                        .name(name)
                        .letter(letter)
                        .photo(photo)
                        .address(address)
                        .build();

                warehouseRepository.save(warehouse);
                //System.out.println(" warehouse "+i+ ": ****** "+warehouseRepository.findById(warehouse.getName()).get().getName());
                i++;
            }


        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void saveEmployeesFromCsv(String filePath) throws IOException {
        String line="";
        int i=1;
        try{
            BufferedReader reader = new BufferedReader(new FileReader(filePath));
            line=reader.readLine(); //skip the header (column names)
            while ((line = reader.readLine()) !=null){
                String  [] row=line.split(",");
                String trigram=row[0];
                String firstName=row[1];
                String lastName=row[2];
                String email=firstName.toLowerCase()+lastName.toLowerCase()+"@gmail.com";
                String photo = row[3];
                String mobilePhone=row[4];
                String jobCsv=row[5];
                String warehouseCsv=row[6];

                Job job= jobCsv.equals("planificateur")? Job.PLANNER : Job.DELIVERYMAN;
                WarehouseEntity warehouse = warehouseRepository.findById(warehouseCsv).get();
                EmployeeEntity employee=EmployeeEntity.builder()
                        .trigram(trigram)
                        .firstName(firstName.toLowerCase())
                        .lastName(lastName.toLowerCase())
                        .photo(photo)
                        .email(email)
                        .warehouse(warehouse)
                        .job(job)
                        .mobilePhone(mobilePhone)
                        .build();
                employeeRepository.save(employee);
                //System.out.println(" employee "+i+ ": ****** "+employeeRepository.findById(employee.getTrigram()).get().getTrigram() + " -  Job :"+employeeRepository.findById(employee.getTrigram()).get().getJob());
                i++;
            }


        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public void saveCustomersFromCsv(String filePath) throws IOException {
        String line="";
        int i=1;
        try{
            BufferedReader reader = new BufferedReader(new FileReader(filePath));
            line=reader.readLine(); //skip the header (column names)
            while ((line = reader.readLine()) !=null){
                String  [] row=line.split(",");
                String email=row[0];
                String firstName=row[1];
                String lastName=row[2];
                String addressCsv = row[3];
                String postalCode = row[4];
                String city = row[5];

                Address address= new Address(addressCsv,postalCode,city);
                CustomerEntity customer=CustomerEntity.builder()
                        .email(email)
                        .firstName(firstName)
                        .lastName(lastName)
                        .address(address)
                        .state(CustomerState.REGISTERED) //default
                        .build();
                customerRepository.save(customer);
                //System.out.println(" customer "+i+ ": ****** "+customerRepository.findById(customer.getEmail()).get().getEmail());
                i++;
            }


        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public void saveOrdersFromCsv(String filePath) throws IOException {
        String line="";
        int i=1;
        try{
            BufferedReader reader = new BufferedReader(new FileReader(filePath));
            line=reader.readLine(); //skip the header (column names)
            while ((line = reader.readLine()) !=null){
                String  [] row=line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)"); // Split CSV line while ignoring commas inside quotes;
                String ref=row[0];
                String orderState=row[1];
                String creationDate=row[2].replaceAll("\"", ""); // Extract the creationDate field and remove quotes;
                String rate= row[3];
                String feedback = row[4];
                String customerEmail=row[5];

                // Split the date and time parts
                String[] dateTimeParts = creationDate.split(", ");
                // Parse the date string
                DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                LocalDate date = LocalDate.parse(dateTimeParts[0], dateFormatter);

                OrderState state= orderState.equals("livrée")?OrderState.DELIVERED:
                        orderState.equals("ouverte")? OrderState.OPENED:
                                orderState.equals("notée")? OrderState.RATED:
                                        orderState.equals("planifiée")? OrderState.PLANNED:OrderState.IN_DELIVERY;

                OrderEntity order = OrderEntity.builder()
                        .reference(ref)
                        .state(state)
                        .creationDate(date)
                        .customer(customerRepository.findById(customerEmail).get())
                        .feedback(feedback)
                        .lines(Set.of())
                        .build();
                orderRepository.save(order);
                System.out.println(" order "+i+ ": ****** "+orderRepository.findById(order.getReference()).get().getReference() +"State : "+orderRepository.findById(order.getReference()).get().getState() );
                i++;
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @PostConstruct
    public void init() throws IOException {
        String dirPath = System.getProperty("user.dir"); // in the spring-boot-server root (the project)

        saveTrucksFromCsv(dirPath+"/server/src/main/java/fr/uga/l3miage/integrator/utils/data/trucks.csv");
        saveWarehousesFromCsv(dirPath+"/server/src/main/java/fr/uga/l3miage/integrator/utils/data/warehouses.csv");
        saveEmployeesFromCsv(dirPath+"/server/src/main/java/fr/uga/l3miage/integrator/utils/data/employees.csv");
        saveCustomersFromCsv(dirPath+"/server/src/main/java/fr/uga/l3miage/integrator/utils/data/customers.csv");
        saveOrdersFromCsv(dirPath+"/server/src/main/java/fr/uga/l3miage/integrator/utils/data/orders.csv");
    }



}
