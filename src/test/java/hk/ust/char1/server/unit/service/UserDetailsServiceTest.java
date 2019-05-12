package hk.ust.char1.server.unit.service;

import hk.ust.char1.server.dto.UserDTO;
import hk.ust.char1.server.dto.UserEmailDTO;
import hk.ust.char1.server.model.User;
import hk.ust.char1.server.model.VerificationToken;
import hk.ust.char1.server.repository.TokenRepository;
import hk.ust.char1.server.repository.UserRepository;
import hk.ust.char1.server.service.UserDetailsService;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.util.HashMap;

import static hk.ust.char1.server.service.UserDetailsService.*;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserDetailsServiceTest {

    @Autowired
    private UserDetailsService userDetailsService;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private TokenRepository tokenRepository;

    private User user1 = new User("charlesz", "9zselrahC", "5555-5555", "charlesz@connect.ust.hk");
    private VerificationToken token1 = new VerificationToken("1234567890", user1);

    @Before
    public void setup(){
        when(userRepository.findUserByUsername("charlesz")).thenReturn(user1);
        when(userRepository.existsUserByEmail(user1.getEmail())).thenReturn(true);
        when(userRepository.existsUserByPhoneNumber(user1.getPhoneNumber())).thenReturn(true);
        when(userRepository.existsUserByUsername(user1.getUsername())).thenReturn(true);
        when(userRepository.save(user1)).thenReturn(user1);
        when(userRepository.saveAndFlush(user1)).thenReturn(user1);

        when(tokenRepository.findVerificationTokenByToken(token1.getToken())).thenReturn(token1);
        when(tokenRepository.findVerificationTokenByUser_Username(user1.getUsername())).thenReturn(token1);
        when(tokenRepository.save(token1)).thenReturn(token1);
        when(tokenRepository.saveAndFlush(token1)).thenReturn(token1);
    }

    @Test
    public void incomingNewRegistrationTest(){
        // DTO for
        UserDTO incomingDTO1 = new UserDTO();
        incomingDTO1.setEmail("charlesz@connect.ust.hk");
        incomingDTO1.setUsername("eto");
        incomingDTO1.setPassword("etoCH908");
        incomingDTO1.setPhone("0000-0000");

        HashMap<RegistrationStatus, User> expectedMap1 = new HashMap<>();

        expectedMap1.put(RegistrationStatus.EMAIL_EXISTS, null);
        AssertionsForClassTypes.assertThat(userDetailsService.incomingNewRegistration(incomingDTO1)).isEqualTo(expectedMap1);

        UserDTO incomingDTO2 = new UserDTO();
        incomingDTO2.setEmail("charlez@connect.ust.hk");
        incomingDTO2.setUsername("eto");
        incomingDTO2.setPassword("etoCH908");
        incomingDTO2.setPhone("5555-5555");

        HashMap<RegistrationStatus, User> expectedMap2 = new HashMap<>();
        expectedMap2.put(RegistrationStatus.PHONE_EXISTS, null);
        AssertionsForClassTypes.assertThat(userDetailsService.incomingNewRegistration(incomingDTO2)).isEqualTo(expectedMap2);

        UserDTO incomingDTO3 = new UserDTO();
        incomingDTO3.setEmail("charlez@connect.ust.hk");
        incomingDTO3.setUsername("eto");
        incomingDTO3.setPassword("etoCH98");
        incomingDTO3.setPhone("0000-0000");

        HashMap<RegistrationStatus, User> expectedMap3 = new HashMap<>();
        expectedMap3.put(RegistrationStatus.POOR_PASSWORD, null);
        AssertionsForClassTypes.assertThat(userDetailsService.incomingNewRegistration(incomingDTO3)).isEqualTo(expectedMap3);

        UserDTO incomingDTO4 = new UserDTO();
        incomingDTO4.setEmail("charlez@connect.ust.hk");
        incomingDTO4.setUsername("charlesz");
        incomingDTO4.setPassword("etoCH908");
        incomingDTO4.setPhone("0000-0000");

        var expectedMap4 = new HashMap<RegistrationStatus, User>();
        expectedMap4.put(RegistrationStatus.USERNAME_EXISTS, null);
        AssertionsForClassTypes.assertThat(userDetailsService.incomingNewRegistration(incomingDTO4)).isEqualTo(expectedMap4);

        UserDTO incomingDTO5 = new UserDTO();
        incomingDTO5.setPhone("0000-0000");
        incomingDTO5.setEmail("brianmak@connect.ust.hk");
        incomingDTO5.setUsername("brianmak");
        incomingDTO5.setPassword("ABCdef12");

        User savingUser = new User();
        savingUser.setUsername(incomingDTO5.getUsername());
        savingUser.setPhoneNumber(incomingDTO5.getPhone());
        savingUser.setEmail(incomingDTO5.getEmail());
        savingUser.setPassword(passwordEncoder.encode(incomingDTO5.getPassword()));
        when(userRepository.saveAndFlush(savingUser)).thenReturn(savingUser);

        assertThat(userDetailsService.incomingNewRegistration(incomingDTO5)).containsOnlyKeys(RegistrationStatus.AWAIT_FOR_VERIFICATION);
    }

    @Test
    public void updateVerificationTokenTest(){

        var beforeTime = LocalDateTime.now();
        var user2 = new User("brianmak","47174hghsiHSGHI", "9999-9999", "brianmak@ust.hk");
        var token2 = new VerificationToken("toegdfsk", user2);
        var newToken = "COMP3311";

        assertThat(token2).hasFieldOrPropertyWithValue("user", user2);
        assertThat(token2).hasFieldOrPropertyWithValue("token", "toegdfsk");

        when(tokenRepository.findVerificationTokenByUser_Username(user2.getUsername())).thenReturn(token2);
        userDetailsService.updateVerificationToken(user2, newToken);

        var afterTime = LocalDateTime.now();
        assertThat(token2).hasFieldOrPropertyWithValue("token", newToken);
        assertThat(token2.getCreatedDate()).isBeforeOrEqualTo(afterTime);
        assertThat(token2.getCreatedDate()).isAfterOrEqualTo(beforeTime);
    }

    @Test
    public void getVerificationTokenTest() {
        assertThat(userDetailsService.getVerificationToken("1234567890")).hasFieldOrPropertyWithValue("token", "1234567890");
    }

    @Test
    public void activateUserTest() {
        var user2 = new User("brianmak","47174hghsiHSGHI", "9999-9999", "brianmak@ust.hk");

        assertThat(user2).hasFieldOrPropertyWithValue("activated", false);

        when(userRepository.saveAndFlush(user2)).thenReturn(user2);

        userDetailsService.activateUser(user2);

        assertThat(user2).hasFieldOrPropertyWithValue("activated", true);
    }

    @Test
    public void handleChangeEmailRequestTest() {
        UserEmailDTO userEmailDTO = new UserEmailDTO();
        userEmailDTO.setUsername(user1.getUsername());
        userEmailDTO.setEmail("charlesz@connect.ust.hk");
        assertThat(userDetailsService.handleChangeEmailRequest(userEmailDTO)).isEqualTo(ActivationStatus.ALLOW_CHANGE);


        userEmailDTO.setEmail("charles12z@connect.ust.hk");
        userDetailsService.activateUser(user1);
        assertThat(userDetailsService.handleChangeEmailRequest(userEmailDTO)).isEqualTo(ActivationStatus.ALREADY_ACTIVATED);

        userEmailDTO.setUsername(user1.getUsername()+ "ABC");
        assertThat(userDetailsService.handleChangeEmailRequest(userEmailDTO)).isEqualTo(ActivationStatus.NO_SUCH_USER);
    }

    @Test
    public void findUserByUsernameTest() {
        assertThat(userDetailsService.findUserByUsername("charlesz")).hasFieldOrPropertyWithValue("password", user1.getPassword());
        assertThat(userDetailsService.findUserByUsername("charlesz")).hasFieldOrPropertyWithValue("email", user1.getEmail());
        assertThat(userDetailsService.findUserByUsername("charlesz")).hasFieldOrPropertyWithValue("phoneNumber", user1.getPhoneNumber());
    }

    @Test
    public void findVerificationTokenByUserTest() {
        assertThat(userDetailsService.findVerificationTokenByUsername(user1.getUsername())).isEqualTo(token1);
    }
}