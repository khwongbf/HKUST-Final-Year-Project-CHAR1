package hk.ust.char1.server.unit.jpa;

import hk.ust.char1.server.model.Tenant;
import hk.ust.char1.server.model.TenantFlatmatePreference;
import hk.ust.char1.server.model.User;
import hk.ust.char1.server.repository.TenantFlatmatePreferenceRepository;
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

import static hk.ust.char1.server.model.TenantFlatmatePreference.Gender.FEMALE;
import static hk.ust.char1.server.model.TenantFlatmatePreference.MarriageStatus.SINGLE;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;

@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = NONE)
public class TenantFlatmatePreferenceJPATests {
    @Autowired
    private TestEntityManager testEntityManager;
    
    @Autowired
    private TenantFlatmatePreferenceRepository tenantFlatmatePreferenceRepository;
    
    @Autowired
    private TenantRepository tenantRepository;

    @Before
    public void clearDatabase(){
        tenantFlatmatePreferenceRepository.deleteAll();
    }

    @Test
    public void findAllInEmpty(){
        assertThat(tenantFlatmatePreferenceRepository.findAll()).isEmpty();
    }

    @Test
    public void addSingleTenantFlatmatePreferenceTest(){

        var tenant = new Tenant(new User("a", "a", "12345678", "a@b.com"));
        testEntityManager.persist(tenant);

        var entityToBePersisted = new TenantFlatmatePreference();
        entityToBePersisted.setTenant(tenant);
        entityToBePersisted.setGender(FEMALE);
        entityToBePersisted.setMaximumAge(50);
        entityToBePersisted.setHaveChildren(false);
        entityToBePersisted.setMinimumAge(40);
        entityToBePersisted.setHavePets(false);
        entityToBePersisted.setLifestyleDescription("");
        entityToBePersisted.setMarriageStatus(SINGLE);

        var savedTenantFlatmatePreference = tenantFlatmatePreferenceRepository.save(
                entityToBePersisted);

        AssertionsForClassTypes.assertThat(savedTenantFlatmatePreference).hasFieldOrPropertyWithValue("gender", entityToBePersisted.getGender());
        AssertionsForClassTypes.assertThat(savedTenantFlatmatePreference).hasFieldOrPropertyWithValue("marriageStatus", entityToBePersisted.getMarriageStatus());
        AssertionsForClassTypes.assertThat(savedTenantFlatmatePreference).hasFieldOrPropertyWithValue("minimumAge", entityToBePersisted.getMinimumAge());
    }

    @Test
    public void removeAllTenantFlatmatePreferencesTest(){

        var tenant1 = new Tenant(new User("a", "a", "12345678", "a@b.com"));
        testEntityManager.persist(tenant1);

        var tenant2 = new Tenant(new User("b", "a", "12355678", "b@b.com"));
        testEntityManager.persist(tenant2);

        var entityToBePersisted1 = new TenantFlatmatePreference();
        entityToBePersisted1.setTenant(tenant1);
        entityToBePersisted1.setGender(FEMALE);
        entityToBePersisted1.setMaximumAge(50);
        entityToBePersisted1.setHaveChildren(false);
        entityToBePersisted1.setMinimumAge(40);
        entityToBePersisted1.setHavePets(false);
        entityToBePersisted1.setLifestyleDescription("");
        entityToBePersisted1.setMarriageStatus(SINGLE);

        var entityToBePersisted2 = new TenantFlatmatePreference();
        entityToBePersisted2.setTenant(tenant2);
        entityToBePersisted2.setGender(FEMALE);
        entityToBePersisted2.setMaximumAge(50);
        entityToBePersisted2.setHaveChildren(false);
        entityToBePersisted2.setMinimumAge(40);
        entityToBePersisted2.setHavePets(false);
        entityToBePersisted2.setLifestyleDescription("");
        entityToBePersisted2.setMarriageStatus(SINGLE);

        testEntityManager.persist(entityToBePersisted1);
        testEntityManager.persist(entityToBePersisted2);

        tenantFlatmatePreferenceRepository.deleteAll();

        assertThat(tenantFlatmatePreferenceRepository.findAll()).isEmpty();
    }

    @Test
    public void findAllTest(){
        var tenant1 = new Tenant(new User("a", "a", "12345678", "a@b.com"));
        testEntityManager.persist(tenant1);

        var tenant2 = new Tenant(new User("b", "a", "12355678", "b@b.com"));
        testEntityManager.persist(tenant2);

        var tenant3 = new Tenant(new User("c", "a", "12355678", "b@b.com"));
        testEntityManager.persist(tenant3);

        var tenantFlatmatePreference1 = new TenantFlatmatePreference();
        tenantFlatmatePreference1.setTenant(tenant1);
        tenantFlatmatePreference1.setMinimumAge(40);
        tenantFlatmatePreference1.setMaximumAge(50);
        var tenantFlatmatePreference2 = new TenantFlatmatePreference();
        tenantFlatmatePreference2.setTenant(tenant2);
        tenantFlatmatePreference2.setMinimumAge(40);
        tenantFlatmatePreference2.setMaximumAge(50);
        var tenantFlatmatePreference3 = new TenantFlatmatePreference();
        tenantFlatmatePreference3.setTenant(tenant3);
        tenantFlatmatePreference3.setMinimumAge(40);
        tenantFlatmatePreference3.setMaximumAge(50);

        testEntityManager.persist(tenantFlatmatePreference1);
        testEntityManager.persist(tenantFlatmatePreference2);
        testEntityManager.persist(tenantFlatmatePreference3);

        assertThat(tenantFlatmatePreferenceRepository.findAll()).hasSize(3).contains(tenantFlatmatePreference1,tenantFlatmatePreference2,tenantFlatmatePreference3).doesNotContain(new TenantFlatmatePreference());
    }

    @Test
    public void findByIdTest(){

        var tenant1 = new Tenant(new User("a", "a", "12345678", "a@b.com"));
        testEntityManager.persist(tenant1);

        var tenant2 = new Tenant(new User("b", "a", "12355678", "b@b.com"));
        testEntityManager.persist(tenant2);

        var tenant3 = new Tenant(new User("c", "a", "12355678", "b@b.com"));
        testEntityManager.persist(tenant3);

        var tenantFlatmatePreference1 = new TenantFlatmatePreference();
        tenantFlatmatePreference1.setTenant(tenant1);
        tenantFlatmatePreference1.setMinimumAge(40);
        tenantFlatmatePreference1.setMaximumAge(50);
        var tenantFlatmatePreference2 = new TenantFlatmatePreference();
        tenantFlatmatePreference2.setTenant(tenant2);
        tenantFlatmatePreference2.setMinimumAge(40);
        tenantFlatmatePreference2.setMaximumAge(50);
        var tenantFlatmatePreference3 = new TenantFlatmatePreference();
        tenantFlatmatePreference3.setTenant(tenant3);
        tenantFlatmatePreference3.setMinimumAge(40);
        tenantFlatmatePreference3.setMaximumAge(50);

        testEntityManager.persist(tenantFlatmatePreference1);
        testEntityManager.persist(tenantFlatmatePreference2);
        testEntityManager.persist(tenantFlatmatePreference3);

        var foundTenantFlatmatePreference = tenantFlatmatePreferenceRepository.getOne(tenantFlatmatePreference2.getFlatmatePreferenceID());

        AssertionsForClassTypes.assertThat(foundTenantFlatmatePreference).isEqualTo(tenantFlatmatePreference2);

    }
}
