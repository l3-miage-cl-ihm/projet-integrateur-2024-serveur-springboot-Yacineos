package fr.uga.l3miage.integrator.configuration;

import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

public class TokenRetriever {
    public static String getAccessToken(String email, String password) {
        // Define the request body with email, password, and returnSecureToken
        String requestBody = "{\"email\":\"" + email + "\",\"password\":\"" + password + "\",\"returnSecureToken\":true}";

        // Set the headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String API_KEY = "AIzaSyBmdkFsMV3403xQd17RVHZcBPzeWt7YISA";

        // Set the URL of the signInWithPassword endpoint along with the API key
        String url = "https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key="+API_KEY;

        // Create a RestTemplate instance
        RestTemplate restTemplate = new RestTemplate();

        // Create the request entity with headers and body
        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);

        // Send the POST request and get the response
        ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);

        // Check if the response is successful
        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            // Parse the response body to extract the access token
            String responseBody = responseEntity.getBody();
            // Extract the access token from the response body
            // You need to implement this part based on the response structure
            // For example, if the response is in JSON format, you can use a JSON parser to extract the token
            // Example: String accessToken = parseAccessTokenFromJson(responseBody);
            return getTokenFromJsonObject(responseBody);
        } else {
            // Handle the error response
            // For example, log the error message or throw an exception
            throw new RuntimeException("Failed to retrieve access token. Status code: " + responseEntity.getStatusCodeValue());
        }
    }

    private static String getTokenFromJsonObject(String apiResponse){
        String token = "";
        JSONParser parser = new JSONParser();
        try{
            JSONObject json = (JSONObject) parser.parse(apiResponse);
            token = json.get("idToken").toString();
        }catch (net.minidev.json.parser.ParseException e) {
            throw new RuntimeException(e);
        }

        return token;
    }
}
