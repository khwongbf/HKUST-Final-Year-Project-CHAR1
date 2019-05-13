package hk.ust.char1.server.unit.jpa;

import hk.ust.char1.server.model.Apartment;
import hk.ust.char1.server.model.ApartmentOwner;
import hk.ust.char1.server.model.Geolocation;
import hk.ust.char1.server.repository.ApartmentOwnerRepository;
import hk.ust.char1.server.repository.ApartmentRepository;
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
public class ApartmentJPATests {
    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private ApartmentRepository apartmentRepository;

    @Autowired
    private ApartmentOwnerRepository apartmentOwnerRepository;

    @Before
    public void clearDatabase(){
        apartmentOwnerRepository.deleteAll();
        apartmentRepository.deleteAll();
    }

    @Test
    public void findAllInEmptyTest(){
        assertThat(apartmentRepository.findAll()).isEmpty();
    }

    @Test
    public void addSingleApartmentTest(){
        byte[] apartmentPhoto = new byte[5000];
        ApartmentOwner apartmentOwner = new ApartmentOwner("charlesz", "charlesz", "12345678", "charlesz@ust.hk", new ArrayList<>());
        testEntityManager.persist(apartmentOwner);

        var savedApartment = apartmentRepository.save(new Apartment("CHARLESZ", new BigDecimal("500.00"), "My address", apartmentPhoto, new Geolocation(), new ArrayList<>(), true, apartmentOwner));

        AssertionsForClassTypes.assertThat(savedApartment).hasFieldOrPropertyWithValue("size", new BigDecimal("500.00"));
        AssertionsForClassTypes.assertThat(savedApartment).hasFieldOrPropertyWithValue("photo",apartmentPhoto);
    }

    @Test
    public void removeAllApartmentsTest(){
        byte[] apartmentPhoto = new byte[5000];

        ApartmentOwner apartmentOwner1 = new ApartmentOwner("a", "a", "12345678", "a@b.com", new ArrayList<>());
        testEntityManager.persist(apartmentOwner1);
        ApartmentOwner apartmentOwner2 = new ApartmentOwner("b", "a", "12345668", "b@b.com", new ArrayList<>());
        testEntityManager.persist(apartmentOwner2);

        testEntityManager.persist(new Apartment("CHARLESZ1", new BigDecimal("500.00"), "My address1", apartmentPhoto, new Geolocation(), new ArrayList<>(), true, apartmentOwner1));
        testEntityManager.persist(new Apartment("CHARLESZ2",new BigDecimal("500.00"), "My address2", apartmentPhoto, new Geolocation(), new ArrayList<>(), true, apartmentOwner2));

        apartmentRepository.deleteAll();

        assertThat(apartmentRepository.findAll()).isEmpty();
    }

    @Test
    public void findAllTest(){
        byte[] apartmentPhoto = new byte[5000];
        ApartmentOwner apartmentOwner1 = new ApartmentOwner("a","a", "12345678", "a@b.com", new ArrayList<>());
        testEntityManager.persist(apartmentOwner1);
        ApartmentOwner apartmentOwner2 = new ApartmentOwner("b","a", "12355678", "b@b.com", new ArrayList<>());
        testEntityManager.persist(apartmentOwner2);
        ApartmentOwner apartmentOwner3 = new ApartmentOwner("c","a", "12145678", "c@b.com", new ArrayList<>());
        testEntityManager.persist(apartmentOwner3);

        Apartment Apartment1 = new Apartment("CHARLESZ1", new BigDecimal("500.00"), "My address1", apartmentPhoto, new Geolocation(), new ArrayList<>(), true, apartmentOwner1);
        Apartment Apartment2 = new Apartment("CHARLESZ2", new BigDecimal("500.00"), "My address2", apartmentPhoto, new Geolocation(), new ArrayList<>(), true, apartmentOwner2);
        Apartment Apartment3 = new Apartment("CHARLESZ3", new BigDecimal("500.00"), "My address3", apartmentPhoto, new Geolocation(), new ArrayList<>(), true, apartmentOwner3);

        testEntityManager.persist(Apartment1);
        testEntityManager.persist(Apartment2);
        testEntityManager.persist(Apartment3);

        assertThat(apartmentRepository.findAll()).hasSize(3).contains(Apartment1,Apartment2,Apartment3).doesNotContain(new Apartment());
    }

    @Test
    public void findByIdTest(){
        byte[] apartmentPhoto = new byte[5000];
        ApartmentOwner apartmentOwner1 = new ApartmentOwner("charlesz", "charlesz", "12345678", "charlesz@ust.hk", new ArrayList<>());
        testEntityManager.persist(apartmentOwner1);
        ApartmentOwner apartmentOwner2 = new ApartmentOwner("charlesz1", "charlesz1", "12345677", "charesz@ust.hk", new ArrayList<>());
        testEntityManager.persist(apartmentOwner2);
        ApartmentOwner apartmentOwner3 = new ApartmentOwner("charesz", "charesz", "12355678", "charlesz@us.hk", new ArrayList<>());
        testEntityManager.persist(apartmentOwner3);
        Apartment Apartment1 = new Apartment("CHARLESZ1", new BigDecimal("500.00"), "My address1", apartmentPhoto, new Geolocation(), new ArrayList<>(), true, apartmentOwner1);
        Apartment Apartment2 = new Apartment("CHARLESZ2", new BigDecimal("500.00"), "My address2", apartmentPhoto, new Geolocation(), new ArrayList<>(), true, apartmentOwner2);
        Apartment Apartment3 = new Apartment("CHARLESZ3", new BigDecimal("500.00"), "My address3", apartmentPhoto, new Geolocation(), new ArrayList<>(), true, apartmentOwner3);

        testEntityManager.persist(Apartment1);
        testEntityManager.persist(Apartment2);
        testEntityManager.persist(Apartment3);

        Apartment foundApartment = apartmentRepository.getOne(Apartment2.getUniqueTag());

        AssertionsForClassTypes.assertThat(foundApartment).isEqualTo(Apartment2);

    }
}
