package hk.ust.char1.server.unit.jpa;

import hk.ust.char1.server.model.ApartmentOwner;
import hk.ust.char1.server.model.Geolocation;
import hk.ust.char1.server.model.SellableApartment;
import hk.ust.char1.server.repository.ApartmentOwnerRepository;
import hk.ust.char1.server.repository.SellableApartmentRepository;
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
public class SellableApartmentJPATests {
    @Autowired
    private TestEntityManager testEntityManager;
    
    @Autowired
    private SellableApartmentRepository sellableApartmentRepository;

    @Autowired
    private ApartmentOwnerRepository apartmentOwnerRepository;

    @Before
    public void clearDatabase(){
        apartmentOwnerRepository.deleteAll();
        sellableApartmentRepository.deleteAll();
    }

    @Test
    public void findAllInEmptyTest(){
        assertThat(sellableApartmentRepository.findAll()).isEmpty();
    }

    @Test
    public void addSingleApartmentTest(){
        var apartmentPhoto = new byte[5000];
        var apartmentOwner = new ApartmentOwner("charlesz", "charlesz", "1234-5678", "charlesz@ust.hk", new ArrayList<>());
        testEntityManager.persist(apartmentOwner);

        var savedApartment = sellableApartmentRepository.save(
                new SellableApartment("CHARLESZ1",new BigDecimal("500.00"), "My address", apartmentPhoto, new Geolocation(), new ArrayList<>(), true, apartmentOwner, new BigDecimal("300000.00")));

        AssertionsForClassTypes.assertThat(savedApartment).hasFieldOrPropertyWithValue("price", new BigDecimal("300000.00"));
        AssertionsForClassTypes.assertThat(savedApartment).hasFieldOrPropertyWithValue("petsAllowed",true);
    }

    @Test
    public void removeAllApartmentsTest(){
        var apartmentPhoto = new byte[5000];

        ApartmentOwner apartmentOwner1 = new ApartmentOwner("charlesz", "charlesz", "1234-5678", "charlesz@ust.hk", new ArrayList<>());
        testEntityManager.persist(apartmentOwner1);
        ApartmentOwner apartmentOwner2 = new ApartmentOwner("charlesz1", "charlesz1", "1234-5677", "charesz@ust.hk", new ArrayList<>());
        testEntityManager.persist(apartmentOwner2);

        testEntityManager.persist(new  SellableApartment("CHARLESZ1",new BigDecimal("500.00"), "My address1", apartmentPhoto, new Geolocation(), new ArrayList<>(), true, apartmentOwner1, new BigDecimal("3000.00")));
        testEntityManager.persist(new  SellableApartment("CHARLESZ2",new BigDecimal("500.00"), "My address2", apartmentPhoto, new Geolocation(), new ArrayList<>(), true, apartmentOwner2, new BigDecimal("3000.00")));

        sellableApartmentRepository.deleteAll();

        assertThat(sellableApartmentRepository.findAll()).isEmpty();
    }

    @Test
    public void findAllTest(){
        var apartmentPhoto = new byte[5000];
        ApartmentOwner apartmentOwner1 = new ApartmentOwner("charlesz", "charlesz", "1234-5678", "charlesz@ust.hk", new ArrayList<>());
        testEntityManager.persist(apartmentOwner1);
        ApartmentOwner apartmentOwner2 = new ApartmentOwner("charlesz1", "charlesz1", "1234-5677", "charesz@ust.hk", new ArrayList<>());
        testEntityManager.persist(apartmentOwner2);
        ApartmentOwner apartmentOwner3 = new ApartmentOwner("charesz", "charesz", "1235-5678", "charlesz@us.hk", new ArrayList<>());
        testEntityManager.persist(apartmentOwner3);

        var sellableApartment1 = new  SellableApartment("CHARLESZ1",new BigDecimal("500.00"), "My address1", apartmentPhoto, new Geolocation(), new ArrayList<>(), true, apartmentOwner1, new BigDecimal("3000.00"));
        var sellableApartment2 = new  SellableApartment("CHARLESZ2",new BigDecimal("500.00"), "My address2", apartmentPhoto, new Geolocation(), new ArrayList<>(), true, apartmentOwner2, new BigDecimal("3000.00"));
        var sellableApartment3 = new  SellableApartment("CHARLESZ3",new BigDecimal("500.00"), "My address3", apartmentPhoto, new Geolocation(), new ArrayList<>(), true, apartmentOwner3, new BigDecimal("3000.00"));

        testEntityManager.persist(sellableApartment1);
        testEntityManager.persist(sellableApartment2);
        testEntityManager.persist(sellableApartment3);

        assertThat(sellableApartmentRepository.findAll()).hasSize(3).contains(sellableApartment1,sellableApartment2,sellableApartment3).doesNotContain(new  SellableApartment());
    }

    @Test
    public void findByIdTest(){
        byte[] apartmentPhoto = new byte[5000];
        ApartmentOwner apartmentOwner1 = new ApartmentOwner("charlesz", "charlesz", "1234-5678", "charlesz@ust.hk", new ArrayList<>());
        testEntityManager.persist(apartmentOwner1);
        ApartmentOwner apartmentOwner2 = new ApartmentOwner("charlesz1", "charlesz1", "1234-5677", "charesz@ust.hk", new ArrayList<>());
        testEntityManager.persist(apartmentOwner2);
        ApartmentOwner apartmentOwner3 = new ApartmentOwner("charesz", "charesz", "1235-5678", "charlesz@us.hk", new ArrayList<>());
        testEntityManager.persist(apartmentOwner3);
        SellableApartment sellableApartment1 = new SellableApartment("CHARLESZ1",new BigDecimal("500.00"), "My address1", apartmentPhoto, new Geolocation(), new ArrayList<>(), true, apartmentOwner1, new BigDecimal("3000.00"));
        SellableApartment sellableApartment2 = new  SellableApartment("CHARLESZ2",new BigDecimal("500.00"), "My address2", apartmentPhoto, new Geolocation(), new ArrayList<>(), true, apartmentOwner2, new BigDecimal("3000.00"));
        SellableApartment sellableApartment3 = new  SellableApartment("CHARLESZ3",new BigDecimal("500.00"), "My address3", apartmentPhoto, new Geolocation(), new ArrayList<>(), true, apartmentOwner3, new BigDecimal("3000.00"));

        testEntityManager.persist(sellableApartment1);
        testEntityManager.persist(sellableApartment2);
        testEntityManager.persist(sellableApartment3);

        SellableApartment foundApartment = sellableApartmentRepository.getOne(sellableApartment2.getUniqueTag());

        AssertionsForClassTypes.assertThat(foundApartment).isEqualTo(sellableApartment2);

    }
}
