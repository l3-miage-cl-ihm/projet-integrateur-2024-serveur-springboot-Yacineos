package fr.uga.l3miage.integrator;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import fr.uga.l3miage.integrator.datatypes.Address;
import fr.uga.l3miage.integrator.enums.CustomerState;
import fr.uga.l3miage.integrator.enums.Job;
import fr.uga.l3miage.integrator.enums.OrderState;
import fr.uga.l3miage.integrator.models.*;
import fr.uga.l3miage.integrator.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.swing.text.Style;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
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

    @Autowired
    private TruckRepository truckRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private WarehouseRepository warehouseRepository;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private ProductRepository productRepository;

    public static void main(String[] args) {
        SpringApplication.run(IntegratorProjectSpringApplication.class,args);

    }


    @Transactional
    public void saveTrucksFromCsv(String filePath) throws IOException {
        String line="";
        int i=1;
        try{
            BufferedReader reader = new BufferedReader(new FileReader(filePath));
            line=reader.readLine(); //skip the header (column names)
            while ((line = reader.readLine()) !=null){
                    String  [] row =line.split(",");
                    TruckEntity truck = TruckEntity.builder().immatriculation(row[0]).build();
                    truckRepository.save(truck);
                    System.out.println(" truck "+i+ ": ****** "+truckRepository.findById(truck.getImmatriculation()).get().getImmatriculation());
                    i++;
            }


        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Transactional
    public void saveWarehousesFromCsv(String filePath) throws IOException {
        String line="";
        int i=1;
        try{
            BufferedReader reader = new BufferedReader(new FileReader(filePath));
            line=reader.readLine(); //skip the header (column names)
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
                System.out.println(" warehouse "+i+ ": ****** "+warehouseRepository.findById(warehouse.getName()).get().getName());
                i++;
            }


        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Transactional
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
                System.out.println(" employee "+i+ ": ****** "+employeeRepository.findById(employee.getTrigram()).get().getTrigram() + " -  Job :"+employeeRepository.findById(employee.getTrigram()).get().getJob());
                i++;
            }


        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Transactional
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
                System.out.println(" customer "+i+ ": ****** "+customerRepository.findById(customer.getEmail()).get().getEmail());
                i++;
            }


        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Transactional
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
        saveTrucksFromCsv("/home/jugurta/Documents/Licence3/MIAGE6/integrator_project/serveur-springboot/server/src/main/java/fr/uga/l3miage/integrator/utils/data/trucks.csv");
        saveWarehousesFromCsv("/home/jugurta/Documents/Licence3/MIAGE6/integrator_project/serveur-springboot/server/src/main/java/fr/uga/l3miage/integrator/utils/data/warehouses.csv");
        saveEmployeesFromCsv("/home/jugurta/Documents/Licence3/MIAGE6/integrator_project/serveur-springboot/server/src/main/java/fr/uga/l3miage/integrator/utils/data/employees.csv");
        saveCustomersFromCsv("/home/jugurta/Documents/Licence3/MIAGE6/integrator_project/serveur-springboot/server/src/main/java/fr/uga/l3miage/integrator/utils/data/customers.csv");
        saveOrdersFromCsv("/home/jugurta/Documents/Licence3/MIAGE6/integrator_project/serveur-springboot/server/src/main/java/fr/uga/l3miage/integrator/utils/data/orders.csv");





    }

}
