package hk.ust.char1.server.unit.jpa;

import hk.ust.char1.server.model.Geolocation;
import hk.ust.char1.server.model.IndividualTenantApartmentPreference;
import hk.ust.char1.server.model.Tenant;
import hk.ust.char1.server.model.User;
import hk.ust.char1.server.repository.IndividualTenantApartmentPreferenceRepository;
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
public class IndividualTenantApartmentPreferenceJPATests {
    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private IndividualTenantApartmentPreferenceRepository individualTenantApartmentPreferenceRepository;

    @Autowired
    private TenantRepository tenantRepository;

    @Before
    public void clearDatabase(){
        tenantRepository.deleteAll();
        individualTenantApartmentPreferenceRepository.deleteAll();
    }

    @Test
    public void findAllInEmptyTest(){
        assertThat(individualTenantApartmentPreferenceRepository.findAll()).isEmpty();
    }

    @Test
    public void addSingleTest(){
        var tenant = new Tenant(new User("a","a", "1234-5678", "a@b.com"));
        testEntityManager.persist(tenant);

        var saved = individualTenantApartmentPreferenceRepository.save(
                new IndividualTenantApartmentPreference(new BigDecimal("500.00"), "rahgt", new Geolocation(new BigDecimal("12.753200"),new BigDecimal("179.132347")), true, new BigDecimal("4000.00"),tenant));

        AssertionsForClassTypes.assertThat(saved).hasFieldOrPropertyWithValue("tenant", tenant);
        AssertionsForClassTypes.assertThat(saved).hasFieldOrPropertyWithValue("petsAllowed",true);
    }

    @Test
    public void removeAllTest(){

        var tenant1 = new Tenant(new User("a","a", "1234-5678", "a@b.com"));
        testEntityManager.persist(tenant1);
        var tenant2 = new Tenant(new User("b","a", "1254-5678", "b@b.com"));
        testEntityManager.persist(tenant2);

        testEntityManager.persist(new IndividualTenantApartmentPreference(new BigDecimal("500.00"), "fuhrb", new Geolocation(new BigDecimal("12.753200"),new BigDecimal("179.132347")), true, new BigDecimal("4000.00"),tenant1));
        testEntityManager.persist(new IndividualTenantApartmentPreference(new BigDecimal("500.00"), "1243bn", new Geolocation(new BigDecimal("12.753200"),new BigDecimal("179.132347")), true, new BigDecimal("4000.00"),tenant2));

        individualTenantApartmentPreferenceRepository.deleteAll();

        assertThat(individualTenantApartmentPreferenceRepository.findAll()).isEmpty();
    }

    @Test
    public void findAllTest(){
        var tenant1 = new Tenant(new User("a","a", "1234-5678", "a@b.com"));
        testEntityManager.persist(tenant1);
        var tenant2 = new Tenant(new User("b","a", "1254-5678", "b@b.com"));
        testEntityManager.persist(tenant2);
        var tenant3 = new Tenant(new User("c","a", "1284-5678", "c@b.com"));
        testEntityManager.persist(tenant3);

        var individualTenantApartmentPreference1 = new IndividualTenantApartmentPreference(new BigDecimal("500.00"), "agja", new Geolocation(new BigDecimal("12.753200"),new BigDecimal("179.132347")), true, new BigDecimal("4000.00"),tenant1);
        var individualTenantApartmentPreference2 = new IndividualTenantApartmentPreference(new BigDecimal("500.00"), "a6ja", new Geolocation(new BigDecimal("12.753200"),new BigDecimal("179.132347")), true, new BigDecimal("4000.00"),tenant2);
        var individualTenantApartmentPreference3 = new IndividualTenantApartmentPreference(new BigDecimal("500.00"), "agju", new Geolocation(new BigDecimal("12.753200"),new BigDecimal("179.132347")), true, new BigDecimal("4000.00"),tenant3);

        testEntityManager.persist(individualTenantApartmentPreference1);
        testEntityManager.persist(individualTenantApartmentPreference2);
        testEntityManager.persist(individualTenantApartmentPreference3);

        assertThat(individualTenantApartmentPreferenceRepository.findAll()).hasSize(3).contains(individualTenantApartmentPreference1,individualTenantApartmentPreference2,individualTenantApartmentPreference3).doesNotContain(new IndividualTenantApartmentPreference());
    }

    @Test
    public void findByIdTest(){
        Tenant tenant1 = new Tenant(new User("a","a", "1234-5678", "a@b.com"));
        testEntityManager.persist(tenant1);
        Tenant tenant2 = new Tenant(new User("b","a", "1254-5678", "b@b.com"));
        testEntityManager.persist(tenant2);
        Tenant tenant3 = new Tenant(new User("c","a", "1284-5678", "c@b.com"));
        testEntityManager.persist(tenant3);
        IndividualTenantApartmentPreference individualTenantApartmentPreference1 = new IndividualTenantApartmentPreference(new BigDecimal("500.00"), "gabu", new Geolocation(new BigDecimal("12.753200"),new BigDecimal("179.132347")), true, new BigDecimal("4000.00"),tenant1);
        IndividualTenantApartmentPreference individualTenantApartmentPreference2 = new IndividualTenantApartmentPreference(new BigDecimal("500.00"), "ihdfijg", new Geolocation(new BigDecimal("12.753200"),new BigDecimal("179.132347")), true, new BigDecimal("4000.00"),tenant2);
        IndividualTenantApartmentPreference individualTenantApartmentPreference3 = new IndividualTenantApartmentPreference(new BigDecimal("500.00"), "88trgf", new Geolocation(new BigDecimal("12.753200"),new BigDecimal("179.132347")), true, new BigDecimal("4000.00"),tenant3);

        testEntityManager.persist(individualTenantApartmentPreference1);
        testEntityManager.persist(individualTenantApartmentPreference2);
        testEntityManager.persist(individualTenantApartmentPreference3);

        var found = individualTenantApartmentPreferenceRepository.getOne(individualTenantApartmentPreference2.getTenantApartmentPreferenceID());

        AssertionsForClassTypes.assertThat(found).isEqualTo(individualTenantApartmentPreference2);

    }
}
