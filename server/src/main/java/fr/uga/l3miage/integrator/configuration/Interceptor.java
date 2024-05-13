package fr.uga.l3miage.integrator.configuration;

import fr.uga.l3miage.integrator.components.EmployeeComponent;
import fr.uga.l3miage.integrator.enums.Job;
import fr.uga.l3miage.integrator.models.EmployeeEntity;
import fr.uga.l3miage.integrator.models.WarehouseEntity;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Base64;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.log;

@Slf4j
@Component
@AllArgsConstructor
public class Interceptor implements HandlerInterceptor {

    private final EmployeeComponent employeeComponent ;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //get endpoint
        String endpointFromRequest = request.getRequestURI() ;
        boolean isPlannerEndpoint = endpointFromRequest.startsWith("/api/v3.0/planner");
        boolean isDeliveryManEndpoint = endpointFromRequest.startsWith("/api/v3.0/deliveryman");
        System.out.println("\n\n-----------------\n\n is planner endpoint : "+isPlannerEndpoint);
        System.out.println("\n\n-----------------\n\n is delivery man endpoint : "+isDeliveryManEndpoint);
        System.out.println("\n\n I got : "+endpointFromRequest);
        String tokenFromHeader = request.getHeader("Authorization") ;

        //decode le token ->email
        String userEmail = getUidFirebaseFromToken(tokenFromHeader);
        System.out.println("\n\n\n\n---------------------User Email : \n\n" + userEmail );
        // get role from email ( form database )
        Job userJob = employeeComponent.getEmployeeJobFromEmail(userEmail);

        //return true or false
        Interceptor.log.info("Request intercepted: " );

        if(!(( userJob.equals(Job.PLANNER) && isPlannerEndpoint) || (userJob.equals(Job.DELIVERYMAN) && isDeliveryManEndpoint))) {
            response.setStatus(HttpStatus.FORBIDDEN.value());
            response.getWriter().write("You do not have the required role to access this resource.\n");
            response.getWriter().flush();
            return false ;
        }
        return true;
    }

    private String getUidFirebaseFromToken(String token) {
        String headerSplit[] = token.split(" ");
        String[] chunks = headerSplit[1].split("\\.");
        Base64.Decoder decoder = Base64.getUrlDecoder();
        String payload = new String(decoder.decode(chunks[1]));
        System.out.println(payload);
        JSONParser parser = new JSONParser();
        String email = "";
        try {
            JSONObject json = (JSONObject) parser.parse(payload);
            email = json.get("email").toString();
        } catch (net.minidev.json.parser.ParseException e) {
            throw new RuntimeException(e);
        }

        return email;
    }



}