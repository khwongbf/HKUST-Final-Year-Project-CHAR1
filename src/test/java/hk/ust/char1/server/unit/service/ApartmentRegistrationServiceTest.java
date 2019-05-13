package hk.ust.char1.server.unit.service;

import hk.ust.char1.server.dto.ApartmentDTO;
import hk.ust.char1.server.model.ApartmentOwner;
import hk.ust.char1.server.model.Role;
import hk.ust.char1.server.model.User;
import hk.ust.char1.server.repository.ApartmentOwnerRepository;
import hk.ust.char1.server.repository.ApartmentRepository;
import hk.ust.char1.server.repository.RoleRepository;
import hk.ust.char1.server.repository.UserRepository;
import hk.ust.char1.server.service.ApartmentRegistrationService;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.ArrayList;


@RunWith(SpringRunner.class)
@SpringBootTest
public class ApartmentRegistrationServiceTest {

    @Autowired
    private ApartmentRegistrationService apartmentRegistrationService;

    @MockBean
    private ApartmentOwnerRepository apartmentOwnerRepository;

    @MockBean
    private ApartmentRepository apartmentRepository;

    @MockBean
    private RoleRepository roleRepository;

    @MockBean
    private UserRepository userRepository;

    private User user = new User("charlesz", "charlesz","88888888","charlesz@cse.ust.hk");
    private ApartmentDTO apartmentDTO = new ApartmentDTO();
    private Role role = new Role("LANDLORD");

    @Before
    public void setup(){
        user.setActivated(true);
        user.getRole().add(new Role("USER"));

        apartmentDTO.setPhoto(new byte[5000]);
        apartmentDTO.setSize(new BigDecimal("500.00"));
        apartmentDTO.setPetsAllowed(true);
        apartmentDTO.setAddress("HKUST");
        apartmentDTO.setFacilities(new ArrayList<>());
        apartmentDTO.getFacilities().add("CSE Department");
        apartmentDTO.setLatitude(new BigDecimal("34.123554"));
        apartmentDTO.setLongitude(new BigDecimal("123.856485"));

        Mockito.when(userRepository.existsUserByUsername(user.getUsername())).thenReturn(true);
        Mockito.when(userRepository.findUserByUsername(user.getUsername())).thenReturn(user);
        Mockito.when(apartmentOwnerRepository.existsApartmentOwnerByUsername(user.getUsername())).thenReturn(false);
        Mockito.when(roleRepository.existsRoleByRole(role.getRole())).thenReturn(true);
        Mockito.when(roleRepository.findRoleByRole(role.getRole())).thenReturn(role);
        Mockito.when(apartmentOwnerRepository.findApartmentOwnerByUsername(user.getUsername())).thenReturn(new ApartmentOwner(user));
    }

    @Test
    public void addNewApartmentTest(){
        AssertionsForClassTypes.assertThat(apartmentRegistrationService.addNewApartment(user.getUsername(), apartmentDTO)).isTrue();

    }

    @Test
    public void registerNewOwnerTest(){
        AssertionsForClassTypes.assertThat(apartmentRegistrationService.registerNewOwner(user.getUsername())).isTrue();
    }

}