package fr.uga.l3miage.integrator.repositories;

import fr.uga.l3miage.integrator.models.DayEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@AutoConfigureTestDatabase
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, properties = "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect")
public class DayRepositoryTest {
    @Autowired
    private DayRepository dayRepository;


    @Test
    void findByDate(){
        //Given
        LocalDate now=LocalDate.now();
        DayEntity day1= DayEntity.builder().date(now).reference("J124G").tours(Set.of()).build();
        DayEntity day2= DayEntity.builder().date(now.plusDays(23)).reference("J057G").tours(Set.of()).build();
        dayRepository.save(day1);
        dayRepository.save(day2);

        //when

        Optional<DayEntity> response1= dayRepository.findByDate(now);
        Optional<DayEntity> response2= dayRepository.findByDate(LocalDate.now().plusDays(2));


        //then
        assertThat(response1.isPresent()).isEqualTo(true);
        assertThat(response1.get().getReference()).isEqualTo(day1.getReference());
        assertThat(response1.get().getDate()).isEqualTo(day1.getDate());
        assertThat(response2.isPresent()).isEqualTo(false);







    }
}
