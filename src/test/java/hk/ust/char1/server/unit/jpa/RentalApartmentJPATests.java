package hk.ust.char1.server.unit.jpa;

import hk.ust.char1.server.model.ApartmentOwner;
import hk.ust.char1.server.model.Geolocation;
import hk.ust.char1.server.model.RentalApartment;
import hk.ust.char1.server.repository.ApartmentOwnerRepository;
import hk.ust.char1.server.repository.RentalApartmentRepository;
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

import static hk.ust.char1.server.model.RentalApartment.RentalMode.HOME_SHARING;
import static hk.ust.char1.server.model.RentalApartment.RentalMode.INDIVIDUAL;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;

@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = NONE)
public class RentalApartmentJPATests {
    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private RentalApartmentRepository rentalApartmentRepository;

    @Autowired
    private ApartmentOwnerRepository apartmentOwnerRepository;

    @Before
    public void clearDatabase(){
        apartmentOwnerRepository.deleteAll();
        rentalApartmentRepository.deleteAll();
    }

    @Test
    public void findAllInEmptyTest(){
        assertThat(rentalApartmentRepository.findAll()).isEmpty();
    }

    @Test
    public void addSingleApartmentTest(){
        byte[] apartmentPhoto = new byte[5000];
        ApartmentOwner apartmentOwner = new ApartmentOwner("charlesz", "charlesz", "12345678", "charlesz@ust.hk", new ArrayList<>());
        testEntityManager.persist(apartmentOwner);

        RentalApartment savedApartment = rentalApartmentRepository.save(
                new RentalApartment("title","CHARLESZ1",new BigDecimal("500.00"), "My address", apartmentPhoto, new Geolocation(), new ArrayList<>(), true, apartmentOwner, new BigDecimal("3000.00"), true, INDIVIDUAL));

        AssertionsForClassTypes.assertThat(savedApartment).hasFieldOrPropertyWithValue("rentalMode", INDIVIDUAL);
        AssertionsForClassTypes.assertThat(savedApartment).hasFieldOrPropertyWithValue("childrenAllowed",true);
    }

    @Test
    public void removeAllApartmentsTest(){
        var apartmentPhoto = new byte[5000];

        ApartmentOwner apartmentOwner1 = new ApartmentOwner("charlesz", "charlesz", "12345678", "charlesz@ust.hk", new ArrayList<>());
        testEntityManager.persist(apartmentOwner1);
        ApartmentOwner apartmentOwner2 = new ApartmentOwner("charlesz1", "charlesz1", "12345677", "charesz@ust.hk", new ArrayList<>());
        testEntityManager.persist(apartmentOwner2);

        testEntityManager.persist(new RentalApartment("asd", "CHARLESZ1", new BigDecimal("500.00"), "My address1", apartmentPhoto, new Geolocation(), new ArrayList<>(), true, apartmentOwner1, new BigDecimal("3000.00"), true, INDIVIDUAL));
        testEntityManager.persist(new RentalApartment("asiu", "CHARLESZ2", new BigDecimal("500.00"), "My address2", apartmentPhoto, new Geolocation(), new ArrayList<>(), true, apartmentOwner2, new BigDecimal("3000.00"), true, HOME_SHARING));

        rentalApartmentRepository.deleteAll();

        assertThat(rentalApartmentRepository.findAll()).isEmpty();
    }

    @Test
    public void findAllTest(){
        byte[] apartmentPhoto = new byte[5000];
        ApartmentOwner apartmentOwner1 = new ApartmentOwner("charlesz", "charlesz", "12345678", "charlesz@ust.hk", new ArrayList<>());
        testEntityManager.persist(apartmentOwner1);
        ApartmentOwner apartmentOwner2 = new ApartmentOwner("charlesz1", "charlesz1", "12345677", "charesz@ust.hk", new ArrayList<>());
        testEntityManager.persist(apartmentOwner2);
        ApartmentOwner apartmentOwner3 = new ApartmentOwner("charesz", "charesz", "12355678", "charlesz@us.hk", new ArrayList<>());
        testEntityManager.persist(apartmentOwner3);

        RentalApartment rentalApartment1 = new RentalApartment("asuy", "CHARLESZ1", new BigDecimal("500.00"), "My address1", apartmentPhoto, new Geolocation(), new ArrayList<>(), true, apartmentOwner1, new BigDecimal("3000.00"), true, INDIVIDUAL);
        RentalApartment rentalApartment2 = new RentalApartment("eiri", "CHARLESZ2", new BigDecimal("500.00"), "My address2", apartmentPhoto, new Geolocation(), new ArrayList<>(), true, apartmentOwner2, new BigDecimal("3000.00"), true, HOME_SHARING);
        RentalApartment rentalApartment3 = new RentalApartment("w", "CHARLESZ3", new BigDecimal("500.00"), "My address3", apartmentPhoto, new Geolocation(), new ArrayList<>(), true, apartmentOwner3, new BigDecimal("3000.00"), true, INDIVIDUAL);

        testEntityManager.persist(rentalApartment1);
        testEntityManager.persist(rentalApartment2);
        testEntityManager.persist(rentalApartment3);

        assertThat(rentalApartmentRepository.findAll()).hasSize(3).contains(rentalApartment1,rentalApartment2,rentalApartment3).doesNotContain(new RentalApartment());
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
        RentalApartment rentalApartment1 = new RentalApartment("asuy", "CHARLESZ1", new BigDecimal("500.00"), "My address1", apartmentPhoto, new Geolocation(), new ArrayList<>(), true, apartmentOwner1, new BigDecimal("3000.00"), true, INDIVIDUAL);
        RentalApartment rentalApartment2 = new RentalApartment("eiri", "CHARLESZ2", new BigDecimal("500.00"), "My address2", apartmentPhoto, new Geolocation(), new ArrayList<>(), true, apartmentOwner2, new BigDecimal("3000.00"), true, HOME_SHARING);
        RentalApartment rentalApartment3 = new RentalApartment("w", "CHARLESZ3", new BigDecimal("500.00"), "My address3", apartmentPhoto, new Geolocation(), new ArrayList<>(), true, apartmentOwner3, new BigDecimal("3000.00"), true, INDIVIDUAL);

        testEntityManager.persist(rentalApartment1);
        testEntityManager.persist(rentalApartment2);
        testEntityManager.persist(rentalApartment3);

        RentalApartment foundApartment = rentalApartmentRepository.getOne(rentalApartment2.getUniqueTag());

        AssertionsForClassTypes.assertThat(foundApartment).isEqualTo(rentalApartment2);

    }
}
