package fr.uga.l3miage.integrator.dataTypes;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import java.util.Set;


import javax.persistence.Embeddable;

@Embeddable
@Getter
@SuperBuilder
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MultipleOrder {
    private Set<String> orders;
    private String address;

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("[[");
        if (orders != null && !orders.isEmpty()) {
            for (String order : orders) {
                stringBuilder.append(order);
                stringBuilder.append(",");
            }
            stringBuilder.setLength(stringBuilder.length() - 1); // Retirer la dernière virgule et l'espace
        }
        stringBuilder.append("],");
        stringBuilder.append(address);
        stringBuilder.append("]");
        return stringBuilder.toString();
    }
    
}
