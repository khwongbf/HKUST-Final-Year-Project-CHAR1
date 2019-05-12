package hk.ust.char1.server.unit.jpa;

import hk.ust.char1.server.model.Geolocation;
import hk.ust.char1.server.model.GroupTenantApartmentPreference;
import hk.ust.char1.server.model.TenantGroup;
import hk.ust.char1.server.repository.GroupTenantApartmentPreferenceRepository;
import hk.ust.char1.server.repository.TenantGroupRepository;
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
public class GroupTenantApartmentPreferenceJPATests {
    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private GroupTenantApartmentPreferenceRepository groupTenantApartmentPreferenceRepository;

    @Autowired
    private TenantGroupRepository tenantGroupRepository;

    @Before
    public void clearDatabase(){
        tenantGroupRepository.deleteAll();
        groupTenantApartmentPreferenceRepository.deleteAll();
    }

    @Test
    public void findAllInEmptyTest(){
        assertThat(groupTenantApartmentPreferenceRepository.findAll()).isEmpty();
    }

    @Test
    public void addSingleTest(){
        var tenantGroup = new TenantGroup();
        testEntityManager.persist(tenantGroup);

        var saved = groupTenantApartmentPreferenceRepository.save(
                new GroupTenantApartmentPreference(new BigDecimal("500.00"), "aut", new Geolocation(new BigDecimal("12.753200"),new BigDecimal("179.132347")), true, new BigDecimal("4000.00"),tenantGroup));

        AssertionsForClassTypes.assertThat(saved).hasFieldOrPropertyWithValue("tenantGroup", tenantGroup);
        AssertionsForClassTypes.assertThat(saved).hasFieldOrPropertyWithValue("petsAllowed",true);
    }

    @Test
    public void removeAllTest(){

        var tenantGroup1 = new TenantGroup();
        tenantGroup1.setGroupName("grop 1");
        testEntityManager.persist(tenantGroup1);
        var tenantGroup2 = new TenantGroup();
        tenantGroup2.setGroupName("group 2");
        testEntityManager.persist(tenantGroup2);

        testEntityManager.persist(new GroupTenantApartmentPreference(new BigDecimal("500.00"), "iusadfhg", new Geolocation(new BigDecimal("12.753200"),new BigDecimal("179.132347")), true, new BigDecimal("4000.00"),tenantGroup1));
        testEntityManager.persist(new GroupTenantApartmentPreference(new BigDecimal("500.00"), "we89t", new Geolocation(new BigDecimal("12.753200"),new BigDecimal("179.132347")), true, new BigDecimal("4000.00"),tenantGroup2));

        groupTenantApartmentPreferenceRepository.deleteAll();

        assertThat(groupTenantApartmentPreferenceRepository.findAll()).isEmpty();
    }

    @Test
    public void findAllTest(){
        var tenantGroup1 = new TenantGroup();
        tenantGroup1.setGroupName("group 1");
        testEntityManager.persist(tenantGroup1);
        var tenantGroup2 = new TenantGroup();
        tenantGroup2.setGroupName("group 2");
        testEntityManager.persist(tenantGroup2);
        var tenantGroup3 = new TenantGroup();
        tenantGroup3.setGroupName("group 3");
        testEntityManager.persist(tenantGroup3);

        var groupTenantApartmentPreference1 = new GroupTenantApartmentPreference(new BigDecimal("500.00"), "aitw2", new Geolocation(new BigDecimal("12.753200"),new BigDecimal("179.132347")), true, new BigDecimal("4000.00"),tenantGroup1);
        var groupTenantApartmentPreference2 = new GroupTenantApartmentPreference(new BigDecimal("500.00"), "faiwen", new Geolocation(new BigDecimal("12.753200"),new BigDecimal("179.132347")), true, new BigDecimal("4000.00"),tenantGroup2);
        var groupTenantApartmentPreference3 = new GroupTenantApartmentPreference(new BigDecimal("500.00"), "aejuirgj", new Geolocation(new BigDecimal("12.753200"),new BigDecimal("179.132347")), true, new BigDecimal("4000.00"),tenantGroup3);

        testEntityManager.persist(groupTenantApartmentPreference1);
        testEntityManager.persist(groupTenantApartmentPreference2);
        testEntityManager.persist(groupTenantApartmentPreference3);

        assertThat(groupTenantApartmentPreferenceRepository.findAll()).hasSize(3);
    }

    @Test
    public void findByIdTest(){
        var tenantGroup1 = new TenantGroup();
        testEntityManager.persist(tenantGroup1);
        var tenantGroup2 = new TenantGroup();
        testEntityManager.persist(tenantGroup2);
        var tenantGroup3 = new TenantGroup();
        testEntityManager.persist(tenantGroup3);
        var groupTenantApartmentPreference1 = new GroupTenantApartmentPreference(new BigDecimal("500.00"), "iautg", new Geolocation(new BigDecimal("12.753200"),new BigDecimal("179.132347")), true, new BigDecimal("4000.00"),tenantGroup1);
        var groupTenantApartmentPreference2 = new GroupTenantApartmentPreference(new BigDecimal("500.00"), "8wgn", new Geolocation(new BigDecimal("12.753200"),new BigDecimal("179.132347")), true, new BigDecimal("4000.00"),tenantGroup2);
        var groupTenantApartmentPreference3 = new GroupTenantApartmentPreference(new BigDecimal("500.00"), "8a9grn", new Geolocation(new BigDecimal("12.753200"),new BigDecimal("179.132347")), true, new BigDecimal("4000.00"),tenantGroup3);

        testEntityManager.persist(groupTenantApartmentPreference1);
        testEntityManager.persist(groupTenantApartmentPreference2);
        testEntityManager.persist(groupTenantApartmentPreference3);

        var found = groupTenantApartmentPreferenceRepository.getOne(groupTenantApartmentPreference2.getTenantApartmentPreferenceID());

        AssertionsForClassTypes.assertThat(found).isEqualTo(groupTenantApartmentPreference2);

    }
}
