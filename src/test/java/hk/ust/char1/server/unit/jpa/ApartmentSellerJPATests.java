package hk.ust.char1.server.unit.jpa;

import hk.ust.char1.server.model.ApartmentSeller;
import hk.ust.char1.server.repository.ApartmentSellerRepository;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.ArrayList;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;

@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = NONE)
public class ApartmentSellerJPATests {
    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private ApartmentSellerRepository apartmentSellerRepository;

    @Before
    public void clearDatabase(){
        apartmentSellerRepository.deleteAll();
    }

    @Test
    public void findAllInEmptyTest(){
        assertThat(apartmentSellerRepository.findAll()).isEmpty();
    }

    @Test
    public void addSingleApartmentSellerTest(){
        var savedApartmentSeller = apartmentSellerRepository.save(new ApartmentSeller("comp3311","helloworld", "12345678", "iloveyou@gmail.com", new ArrayList<>(),new BigDecimal("20.00")));

        AssertionsForClassTypes.assertThat(savedApartmentSeller).hasFieldOrPropertyWithValue("username", "comp3311");
        AssertionsForClassTypes.assertThat(savedApartmentSeller).hasFieldOrPropertyWithValue("password","helloworld");
        AssertionsForClassTypes.assertThat(savedApartmentSeller).hasFieldOrPropertyWithValue("sellerRating", new BigDecimal("20.00"));
    }

    @Test
    public void removeAllApartmentSellersTest(){
        testEntityManager.persist(new ApartmentSeller("obama","amabo", "12345677", "iloveyu@gmail.com", new ArrayList<>(), new BigDecimal("35.00")));
        testEntityManager.persist(new ApartmentSeller("trump", "pmurt", "12345778", "ilveyou@gmail.com", new ArrayList<>(), new BigDecimal("35.00")));

        apartmentSellerRepository.deleteAll();

        assertThat(apartmentSellerRepository.findAll()).isEmpty();
    }

    @Test
    public void findAllTest(){
        var apartmentSeller1 = new ApartmentSeller("obama","amabo", "12345677", "iloveyu@gmail.com", new ArrayList<>(), new BigDecimal("35.00"));
        var apartmentSeller2 = new ApartmentSeller("trump", "pmurt", "12345778", "ilveyou@gmail.com", new ArrayList<>(), new BigDecimal("35.00"));
        var apartmentSeller3 = new ApartmentSeller("bush", "hsub", "12345678", "iloveyou@gmail.com", new ArrayList<>(), new BigDecimal("35.00"));

        testEntityManager.persist(apartmentSeller1);
        testEntityManager.persist(apartmentSeller2);
        testEntityManager.persist(apartmentSeller3);

        assertThat(apartmentSellerRepository.findAll()).hasSize(3).contains(apartmentSeller1,apartmentSeller2,apartmentSeller3);
    }

    @Test
    public void findByIdTest(){
        var apartmentSeller1 = new ApartmentSeller("obama","amabo", "12345677", "iloveyu@gmail.com", new ArrayList<>(), new BigDecimal("35.00"));
        var apartmentSeller2 = new ApartmentSeller("trump", "pmurt", "12345778", "ilveyou@gmail.com", new ArrayList<>(), new BigDecimal("35.00"));
        var apartmentSeller3 = new ApartmentSeller("bush", "hsub", "12345678", "iloveyou@gmail.com", new ArrayList<>(), new BigDecimal("35.00"));

        testEntityManager.persist(apartmentSeller1);
        testEntityManager.persist(apartmentSeller2);
        testEntityManager.persist(apartmentSeller3);

        var foundApartmentSeller = apartmentSellerRepository.getOne(apartmentSeller2.getUsername());

        AssertionsForClassTypes.assertThat(foundApartmentSeller).isEqualTo(apartmentSeller2);

    }
}
