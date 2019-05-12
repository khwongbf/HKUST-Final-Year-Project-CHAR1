package hk.ust.char1.server.unit.jpa;

import hk.ust.char1.server.model.Buyer;
import hk.ust.char1.server.model.BuyerApartmentPreference;
import hk.ust.char1.server.model.Geolocation;
import hk.ust.char1.server.repository.BuyerApartmentPreferenceRepository;
import hk.ust.char1.server.repository.BuyerRepository;
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

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;

@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = NONE)
public class BuyerApartmentPreferenceJPATests {
    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private BuyerApartmentPreferenceRepository buyerApartmentPreferenceRepository;

    @Autowired
    private BuyerRepository buyerRepository;

    @Before
    public void clearDatabase(){
        buyerRepository.deleteAll();
        buyerApartmentPreferenceRepository.deleteAll();
    }

    @Test
    public void findAllInEmptyTest(){
        assertThat(buyerApartmentPreferenceRepository.findAll()).isEmpty();
    }

    @Test
    public void addSingleApartmentTest(){
        var buyer = new Buyer("obama", "obama", "1234-5678", "a@b.com", new BigDecimal("0.00"),0);
        testEntityManager.persist(buyer);

        var savedApartment = buyerApartmentPreferenceRepository.save(
                new BuyerApartmentPreference( new BigDecimal("500.00"), new Geolocation(new BigDecimal("12.753200"),new BigDecimal("179.132347")), true, "fdsj", new BigDecimal("400000.00"),buyer));

        AssertionsForClassTypes.assertThat(savedApartment).hasFieldOrPropertyWithValue("buyer", buyer);
        AssertionsForClassTypes.assertThat(savedApartment).hasFieldOrPropertyWithValue("petsAllowed",true);
    }

    @Test
    public void removeAllApartmentsTest(){

        Buyer buyer1 = new Buyer("obama", "obama", "1234-5678", "a@b.com", new BigDecimal("0.00"),0);
        testEntityManager.persist(buyer1);
        Buyer buyer2 = new Buyer("trump", "trump", "2334-5678", "a@c.com", new BigDecimal("0.00"),0);
        testEntityManager.persist(buyer2);

        testEntityManager.persist(new BuyerApartmentPreference(new BigDecimal("500.00"), new Geolocation(new BigDecimal("12.753200"),new BigDecimal("179.132347")), true, "sfh", new BigDecimal("400000.00"),buyer1));
        testEntityManager.persist(new BuyerApartmentPreference(new BigDecimal("500.00"), new Geolocation(new BigDecimal("12.753200"),new BigDecimal("179.132347")), true, "f437", new BigDecimal("400000.00"),buyer2));

        buyerApartmentPreferenceRepository.deleteAll();

        assertThat(buyerApartmentPreferenceRepository.findAll()).isEmpty();
    }

    @Test
    public void findAllTest(){
        var buyer1 = new Buyer("obama", "obama", "1234-5678", "a@b.com", new BigDecimal("0.00"),0);
        testEntityManager.persist(buyer1);
        var buyer2 = new Buyer("trump", "trump", "2334-5678", "a@c.com", new BigDecimal("0.00"),0);
        testEntityManager.persist(buyer2);
        var buyer3 = new Buyer("bush", "bush", "3434-5678", "b@b.com", new BigDecimal("0.00"),0);
        testEntityManager.persist(buyer3);

        var buyerApartmentPreference1 = new BuyerApartmentPreference(new BigDecimal("500.00"), new Geolocation(new BigDecimal("12.753200"),new BigDecimal("179.132347")), true, "472134", new BigDecimal("400000.00"),buyer1);
        var buyerApartmentPreference2 = new BuyerApartmentPreference(new BigDecimal("500.00"), new Geolocation(new BigDecimal("12.753200"),new BigDecimal("179.132347")), true, "ahgf8", new BigDecimal("400000.00"),buyer2);
        var buyerApartmentPreference3 = new BuyerApartmentPreference(new BigDecimal("500.00"), new Geolocation(new BigDecimal("12.753200"),new BigDecimal("179.132347")), true, "67y23jfb", new BigDecimal("400000.00"),buyer3);

        testEntityManager.persist(buyerApartmentPreference1);
        testEntityManager.persist(buyerApartmentPreference2);
        testEntityManager.persist(buyerApartmentPreference3);

//        var auxBuyerPref = new BuyerApartmentPreference(new BigDecimal("500.00"), new Geolocation(new BigDecimal("12.753200"),new BigDecimal("179.132347")), true, "ahd", new BigDecimal("400000.00"),buyer1);
        assertThat(buyerApartmentPreferenceRepository.findAll()).hasSize(3);
    }

    @Test
    public void findByIdTest(){
        Buyer buyer1 = new Buyer("obama", "obama", "1234-5678", "a@b.com", new BigDecimal("0.00"),0);
        testEntityManager.persist(buyer1);
        Buyer buyer2 = new Buyer("trump", "trump", "2334-5678", "a@c.com", new BigDecimal("0.00"),0);
        testEntityManager.persist(buyer2);
        Buyer buyer3 = new Buyer("bush", "bush", "3434-5678", "b@b.com", new BigDecimal("0.00"),0);
        testEntityManager.persist(buyer3);
        var buyerApartmentPreference1 = new BuyerApartmentPreference(new BigDecimal("500.00"), new Geolocation(new BigDecimal("12.753200"),new BigDecimal("179.132347")), true, "372f", new BigDecimal("400000.00"),buyer1);
        var buyerApartmentPreference2 = new BuyerApartmentPreference(new BigDecimal("500.00"), new Geolocation(new BigDecimal("12.753100"),new BigDecimal("19.132347")), true, "gfaw", new BigDecimal("400000.00"),buyer2);
        var buyerApartmentPreference3 = new BuyerApartmentPreference(new BigDecimal("500.00"), new Geolocation(new BigDecimal("12.753200"),new BigDecimal("179.132347")), true, "7uay", new BigDecimal("400000.00"),buyer3);

        testEntityManager.persist(buyerApartmentPreference1);
        testEntityManager.persist(buyerApartmentPreference2);
        testEntityManager.persist(buyerApartmentPreference3);

        var foundApartment = buyerApartmentPreferenceRepository.getOne(buyerApartmentPreference2.getPreferenceID());

        AssertionsForClassTypes.assertThat(foundApartment).isEqualTo(buyerApartmentPreference2);

    }
}
