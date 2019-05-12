package hk.ust.char1.server.unit.jpa;

import hk.ust.char1.server.model.Buyer;
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
public class BuyerJPATests {
    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private BuyerRepository buyerRepository;

    @Before
    public void clearDatabase(){
        buyerRepository.deleteAll();
    }

    @Test
    public void findAllInEmptyTest(){
        assertThat(buyerRepository.findAll()).isEmpty();
    }

    @Test
    public void addSingleBuyerTest(){
        var savedBuyer = buyerRepository.save(new Buyer("comp3311","helloworld", "1234-5678", "iloveyou@gmail.com", new BigDecimal("50.00"), 3));

        AssertionsForClassTypes.assertThat(savedBuyer).hasFieldOrPropertyWithValue("username", "comp3311");
        AssertionsForClassTypes.assertThat(savedBuyer).hasFieldOrPropertyWithValue("password","helloworld");
        AssertionsForClassTypes.assertThat(savedBuyer).hasFieldOrPropertyWithValue("buyerRating",new BigDecimal("50.00"));
    }

    @Test
    public void removeAllBuyersTest(){
        testEntityManager.persist(new Buyer("obama","amabo", "1234-5677", "iloveyu@gmail.com", new BigDecimal("50.00"), 3));
        testEntityManager.persist(new Buyer("trump", "pmurt", "1234-5778", "ilveyou@gmail.com", new BigDecimal("50.00"), 3));

        buyerRepository.deleteAll();

        assertThat(buyerRepository.findAll()).isEmpty();
    }

    @Test
    public void findAllTest(){
        var buyer1 = new Buyer("obama","amabo", "1234-5677", "iloveyu@gmail.com", new BigDecimal("50.00"), 3);
        var buyer2 = new Buyer("trump", "pmurt", "1234-5778", "ilveyou@gmail.com", new BigDecimal("50.00"), 3);
        var buyer3 = new Buyer("bush", "hsub", "1234-5678", "iloveyou@gmail.com", new BigDecimal("50.00"), 3);

        testEntityManager.persist(buyer1);
        testEntityManager.persist(buyer2);
        testEntityManager.persist(buyer3);

        assertThat(buyerRepository.findAll()).hasSize(3).contains(buyer1,buyer2,buyer3);
    }

    @Test
    public void findByIdTest(){
        var buyer1 = new Buyer("obama","amabo", "1234-5677", "iloveyu@gmail.com", new BigDecimal("50.00"), 3);
        var buyer2 = new Buyer("trump", "pmurt", "1234-5778", "ilveyou@gmail.com", new BigDecimal("50.00"), 3);
        var buyer3 = new Buyer("bush", "hsub", "1234-5678", "iloveyou@gmail.com", new BigDecimal("50.00"), 3);

        testEntityManager.persist(buyer1);
        testEntityManager.persist(buyer2);
        testEntityManager.persist(buyer3);

        var foundBuyer = buyerRepository.getOne(buyer2.getUsername());

        AssertionsForClassTypes.assertThat(foundBuyer).isEqualTo(buyer2);

    }
}
