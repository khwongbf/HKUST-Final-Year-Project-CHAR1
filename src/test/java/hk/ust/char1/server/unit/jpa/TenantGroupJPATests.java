package hk.ust.char1.server.unit.jpa;

import hk.ust.char1.server.model.Tenant;
import hk.ust.char1.server.model.TenantGroup;
import hk.ust.char1.server.model.User;
import hk.ust.char1.server.repository.TenantGroupRepository;
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

import java.util.ArrayList;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;

@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = NONE)
public class TenantGroupJPATests {
    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private TenantGroupRepository tenantGroupRepository;

    @Autowired
    private TenantRepository tenantRepository;

    @Before
    public void clearDatabase(){
        tenantGroupRepository.deleteAll();
    }

    @Test
    public void findAllInEmpty(){
        assertThat(tenantGroupRepository.findAll()).isEmpty();
    }

    @Test
    public void addSingleTest(){

        var tenant1 = new Tenant(new User("a", "a", "12345678", "a@b.com"));
        testEntityManager.persist(tenant1);
        var tenant2 = new Tenant(new User("b", "a", "12545678", "b@b.com"));
        testEntityManager.persist(tenant2);

        var tenants = new ArrayList<Tenant>();
        tenants.add(tenant1);
        tenants.add(tenant2);

        var savedApartment = tenantGroupRepository.save(
                new TenantGroup("afg",tenants));

        AssertionsForClassTypes.assertThat(savedApartment).hasFieldOrProperty("tenants");
        assertThat(savedApartment.getTenants()).contains(tenant1, tenant2);
    }

    @Test
    public void removeAllTenantGroupsTest(){

        var tenant1 = new Tenant(new User("a", "a", "12345678", "a@b.com"));
        testEntityManager.persist(tenant1);
        var tenant2 = new Tenant(new User("b", "a", "12355678", "b@b.com"));
        testEntityManager.persist(tenant2);

        var tenants = new ArrayList<Tenant>();
        tenants.add(tenant1);
        tenants.add(tenant2);

        testEntityManager.persist(new TenantGroup("afc",tenants));

        tenantGroupRepository.deleteAll();

        assertThat(tenantGroupRepository.findAll()).isEmpty();
    }

    @Test
    public void findAllTest(){
        var tenantGroup1 = new TenantGroup();
        tenantGroup1.setGroupName("3285h");
        var tenantGroup2 = new TenantGroup();
        tenantGroup2.setGroupName("augn");
        var tenantGroup3 = new TenantGroup();
        tenantGroup3.setGroupName("aguweg");

        testEntityManager.persist(tenantGroup1);
        testEntityManager.persist(tenantGroup2);
        testEntityManager.persist(tenantGroup3);

        var aux = new TenantGroup();
        aux.setGroupName("augfr");
        assertThat(tenantGroupRepository.findAll()).hasSize(3).contains(tenantGroup1,tenantGroup2,tenantGroup3).doesNotContain(aux);
    }

    @Test
    public void findByIdTest(){

        var tenantGroup1 = new TenantGroup();
        var tenantGroup2 = new TenantGroup();
        var tenantGroup3 = new TenantGroup();

        testEntityManager.persist(tenantGroup1);
        testEntityManager.persist(tenantGroup2);
        testEntityManager.persist(tenantGroup3);

        var foundApartment = tenantGroupRepository.getOne(tenantGroup2.getGroupID());

        AssertionsForClassTypes.assertThat(foundApartment).isEqualTo(tenantGroup2);

    }

}
