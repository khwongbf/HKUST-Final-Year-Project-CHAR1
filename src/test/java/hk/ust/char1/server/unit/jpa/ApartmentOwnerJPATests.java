package hk.ust.char1.server.unit.jpa;

import hk.ust.char1.server.model.ApartmentOwner;
import hk.ust.char1.server.repository.ApartmentOwnerRepository;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;

@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = NONE)
public class ApartmentOwnerJPATests {
    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private ApartmentOwnerRepository apartmentOwnerRepository;

    @Before
    public void clearDatabase(){
        apartmentOwnerRepository.deleteAll();
    }

    @Test
    public void findAllInEmptyTest(){
        assertThat(apartmentOwnerRepository.findAll()).isEmpty();
    }

    @Test
    public void addSingleTest(){
        var savedApartmentOwner = apartmentOwnerRepository.save(new ApartmentOwner("comp3311","helloworld", "12345678", "iloveyou@gmail.com", new ArrayList<>()));

        AssertionsForClassTypes.assertThat(savedApartmentOwner).hasFieldOrPropertyWithValue("username", "comp3311");
        AssertionsForClassTypes.assertThat(savedApartmentOwner).hasFieldOrPropertyWithValue("password","helloworld");
    }

    @Test
    public void removeAllTest(){
        testEntityManager.persist(new ApartmentOwner("obama","amabo", "12345677", "ilovyou@gmail.com", new ArrayList<>()));
        testEntityManager.persist(new ApartmentOwner("trump", "pmurt","12345978", "ilveyou@gmail.com", new ArrayList<>()));

        apartmentOwnerRepository.deleteAll();

        assertThat(apartmentOwnerRepository.findAll()).isEmpty();
    }

    @Test
    public void findAllTest(){
        var apartmentOwner1 = new ApartmentOwner("obama","amabo", "12345678", "ilovyou@gmail.com", new ArrayList<>());
        var apartmentOwner2 = new ApartmentOwner("trump", "pmurt","12345978", "ilveyou@gmail.com", new ArrayList<>());
        var apartmentOwner3 = new ApartmentOwner("bush", "hsub", "12345677", "iloveyou@gmail.com", new ArrayList<>());

        testEntityManager.persist(apartmentOwner1);
        testEntityManager.persist(apartmentOwner2);
        testEntityManager.persist(apartmentOwner3);

        assertThat(apartmentOwnerRepository.findAll()).hasSize(3).contains(apartmentOwner1,apartmentOwner2,apartmentOwner3);
    }

    @Test
    public void findByIdTest(){
        var apartmentOwner1 = new ApartmentOwner("obama","amabo", "12345677", "ilovyou@gmail.com", new ArrayList<>());
        var apartmentOwner2 = new ApartmentOwner("trump", "pmurt","12345978", "ilveyou@gmail.com", new ArrayList<>());
        var apartmentOwner3 = new ApartmentOwner("bush", "hsub", "12345678", "iloveyou@gmail.com", new ArrayList<>());

        testEntityManager.persist(apartmentOwner1);
        testEntityManager.persist(apartmentOwner2);
        testEntityManager.persist(apartmentOwner3);

        var foundApartmentOwner = apartmentOwnerRepository.getOne(apartmentOwner2.getUsername());

        AssertionsForClassTypes.assertThat(foundApartmentOwner).isEqualTo(apartmentOwner2);

    }
}
