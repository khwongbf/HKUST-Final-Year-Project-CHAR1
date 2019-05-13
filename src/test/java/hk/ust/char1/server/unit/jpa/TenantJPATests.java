package hk.ust.char1.server.unit.jpa;

import hk.ust.char1.server.model.Tenant;
import hk.ust.char1.server.model.User;
import hk.ust.char1.server.repository.TenantRepository;
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
public class TenantJPATests {
    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private TenantRepository tenantRepository;

    @Before
    public void clearDatabase(){
        tenantRepository.deleteAll();
    }

    @Test
    public void findAllInEmptyTest(){
        assertThat(tenantRepository.findAll()).isEmpty();
    }

    @Test
    public void addSingleUserTest(){
        var savedTenant = tenantRepository.save(new Tenant("comp3311","helloworld", "12345678", "iloveyou@gmail.com", new BigDecimal("90.00"), 3));

        AssertionsForClassTypes.assertThat(savedTenant).hasFieldOrPropertyWithValue("username", "comp3311");
        AssertionsForClassTypes.assertThat(savedTenant).hasFieldOrPropertyWithValue("password","helloworld");
        AssertionsForClassTypes.assertThat(savedTenant).hasFieldOrPropertyWithValue("tenantRating", new BigDecimal("90.00"));
        AssertionsForClassTypes.assertThat(savedTenant).hasFieldOrPropertyWithValue("tenantFlatmatePreference",null);
    }

    @Test
    public void removeAllUsersTest(){
        testEntityManager.persist(new Tenant(new User("a", "a", "12345678", "a@b.com")));
        testEntityManager.persist(new Tenant(new User("b", "a", "12545678", "b@b.com")));

        tenantRepository.deleteAll();

        assertThat(tenantRepository.findAll()).isEmpty();
    }

    @Test
    public void findAllTest(){
        var tenant1 = new Tenant("obama","amabo", "12345677", "iloveyu@gmail.com", new BigDecimal("90.00"), 3);
        var tenant2 = new Tenant("trump", "pmurt", "12345778", "ilveyou@gmail.com", new BigDecimal("90.00"), 3);
        var tenant3 = new Tenant("bush", "hsub", "12345678", "iloveyou@gmail.com", new BigDecimal("90.00"), 3);

        testEntityManager.persist(tenant1);
        testEntityManager.persist(tenant2);
        testEntityManager.persist(tenant3);

        assertThat(tenantRepository.findAll()).hasSize(3).contains(tenant1,tenant2,tenant3);
    }

    @Test
    public void findByIdTest(){
        var tenant1 = new Tenant("obama","amabo", "12345677", "iloveyu@gmail.com", new BigDecimal("90.00"), 3);
        var tenant2 = new Tenant("trump", "pmurt", "12345778", "ilveyou@gmail.com", new BigDecimal("90.00"), 3);
        var tenant3 = new Tenant("bush", "hsub", "12345678", "iloveyou@gmail.com", new BigDecimal("90.00"), 3);

        testEntityManager.persist(tenant1);
        testEntityManager.persist(tenant2);
        testEntityManager.persist(tenant3);

        var foundTenant = tenantRepository.getOne(tenant2.getUsername());

        AssertionsForClassTypes.assertThat(foundTenant).isEqualTo(tenant2);

    }
}
