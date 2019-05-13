package hk.ust.char1.server.integration.service;

import hk.ust.char1.server.dto.UserDTO;
import hk.ust.char1.server.dto.UserEmailDTO;
import hk.ust.char1.server.model.User;
import hk.ust.char1.server.model.VerificationToken;
import hk.ust.char1.server.repository.TokenRepository;
import hk.ust.char1.server.repository.UserRepository;
import hk.ust.char1.server.service.UserDetailsService;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserDetailsServiceTest {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TokenRepository tokenRepository;

    private User user1 = new User("charlesz", "9zselrahC", "55555555", "charlesz@connect.ust.hk");
    private VerificationToken token1 = new VerificationToken();

    @Before
    @Transactional
    public void setup(){
        userRepository.save(user1);
        userRepository.flush();
        token1.setToken("1234567890");
        token1.setUser(userRepository.findUserByUsername(user1.getUsername()));
        tokenRepository.save(token1);
        tokenRepository.flush();
    }

    @After
    @Transactional
    public void dropDatabase(){
        tokenRepository.deleteAll();
        tokenRepository.flush();
        userRepository.deleteAll();
        userRepository.flush();
    }


    @Test
    @Transactional
    public void incomingNewRegistrationTest(){
        // DTO for
        var incomingDTO1 = new UserDTO();
        incomingDTO1.setEmail("charlesz@connect.ust.hk");
        incomingDTO1.setUsername("eto");
        incomingDTO1.setPassword("etoCH908");
        incomingDTO1.setPhone("00000000");

        var expectedMap1 = new HashMap<UserDetailsService.RegistrationStatus, User>();

        expectedMap1.put(UserDetailsService.RegistrationStatus.EMAIL_EXISTS, null);
        AssertionsForClassTypes.assertThat(userDetailsService.incomingNewRegistration(incomingDTO1)).isEqualTo(expectedMap1);

        var incomingDTO2 = new UserDTO();
        incomingDTO2.setEmail("charlez@connect.ust.hk");
        incomingDTO2.setUsername("eto");
        incomingDTO2.setPassword("etoCH908");
        incomingDTO2.setPhone("55555555");

        var expectedMap2 = new HashMap<UserDetailsService.RegistrationStatus, User>();
        expectedMap2.put(UserDetailsService.RegistrationStatus.PHONE_EXISTS, null);
        AssertionsForClassTypes.assertThat(userDetailsService.incomingNewRegistration(incomingDTO2)).isEqualTo(expectedMap2);

        var incomingDTO3 = new UserDTO();
        incomingDTO3.setEmail("charlez@connect.ust.hk");
        incomingDTO3.setUsername("eto");
        incomingDTO3.setPassword("etoCH98");
        incomingDTO3.setPhone("00000000");

        var expectedMap3 = new HashMap<UserDetailsService.RegistrationStatus, User>();
        expectedMap3.put(UserDetailsService.RegistrationStatus.POOR_PASSWORD, null);
        AssertionsForClassTypes.assertThat(userDetailsService.incomingNewRegistration(incomingDTO3)).isEqualTo(expectedMap3);

        var incomingDTO4 = new UserDTO();
        incomingDTO4.setEmail("charlez@connect.ust.hk");
        incomingDTO4.setUsername("charlesz");
        incomingDTO4.setPassword("etoCH908");
        incomingDTO4.setPhone("00000000");

        var expectedMap4 = new HashMap<UserDetailsService.RegistrationStatus, User>();
        expectedMap4.put(UserDetailsService.RegistrationStatus.USERNAME_EXISTS, null);
        AssertionsForClassTypes.assertThat(userDetailsService.incomingNewRegistration(incomingDTO4)).isEqualTo(expectedMap4);

        var incomingDTO5 = new UserDTO();
        incomingDTO5.setPhone("00000000");
        incomingDTO5.setEmail("brianmak@connect.ust.hk");
        incomingDTO5.setUsername("brianmak");
        incomingDTO5.setPassword("ABCdef12");

        var savingUser = new User();
        savingUser.setUsername(incomingDTO5.getUsername());
        savingUser.setPhoneNumber(incomingDTO5.getPhone());
        savingUser.setEmail(incomingDTO5.getEmail());
        savingUser.setPassword(passwordEncoder.encode(incomingDTO5.getPassword()));

        assertThat(userDetailsService.incomingNewRegistration(incomingDTO5)).containsOnlyKeys(UserDetailsService.RegistrationStatus.AWAIT_FOR_VERIFICATION);
    }

    @Test
    @Transactional
    public void updateVerificationTokenTest(){

        var beforeTime = LocalDateTime.now();
        var user2 = new User("brianmak","47174hghsiHSGHI", "99999999", "brianmak@ust.hk");
        var token2 = new VerificationToken("toegdfsk", user2);
        var newToken = "COMP3311";

        AssertionsForClassTypes.assertThat(token2).hasFieldOrPropertyWithValue("user", user2);
        AssertionsForClassTypes.assertThat(token2).hasFieldOrPropertyWithValue("token", "toegdfsk");

        userRepository.save(user2);
        userRepository.flush();

        token2.setUser(userRepository.findUserByUsername(user2.getUsername()));
        tokenRepository.saveAndFlush(token2);

        userDetailsService.updateVerificationToken(userRepository.findUserByUsername(user2.getUsername()), newToken);

        var afterTime = LocalDateTime.now();
        AssertionsForClassTypes.assertThat(tokenRepository.findVerificationTokenByUser_Username(user2.getUsername())).hasFieldOrPropertyWithValue("token", newToken);
        AssertionsForClassTypes.assertThat(tokenRepository.findVerificationTokenByUser_Username(user2.getUsername()).getCreatedDate()).isBeforeOrEqualTo(afterTime);
        AssertionsForClassTypes.assertThat(tokenRepository.findVerificationTokenByUser_Username(user2.getUsername()).getCreatedDate()).isAfterOrEqualTo(beforeTime);
    }

    @Test
    @Transactional
    public void getVerificationTokenTest() {
        AssertionsForClassTypes.assertThat(userDetailsService.getVerificationToken("1234567890")).hasFieldOrPropertyWithValue("token", "1234567890");
    }

    @Test
    @Transactional
    public void activateUserTest() {
        var user2 = new User("brianmak","47174hghsiHSGHI", "99999999", "brianmak@ust.hk");

        AssertionsForClassTypes.assertThat(user2).hasFieldOrPropertyWithValue("activated", false);

        userDetailsService.activateUser(user2);

        AssertionsForClassTypes.assertThat(user2).hasFieldOrPropertyWithValue("activated", true);
    }

    @Test
    @Transactional
    public void handleChangeEmailRequestTest() {
        var userEmailDTO = new UserEmailDTO();
        userEmailDTO.setUsername(user1.getUsername());
        userEmailDTO.setEmail("charlesz@connect.ust.hk");
        assertThat(userDetailsService.handleChangeEmailRequest(userEmailDTO)).isEqualTo(UserDetailsService.ActivationStatus.ALLOW_CHANGE);


        userEmailDTO.setEmail("charles12z@connect.ust.hk");
        userDetailsService.activateUser(user1);
        assertThat(userDetailsService.handleChangeEmailRequest(userEmailDTO)).isEqualTo(UserDetailsService.ActivationStatus.ALREADY_ACTIVATED);

        userEmailDTO.setUsername(user1.getUsername()+ "ABC");
        assertThat(userDetailsService.handleChangeEmailRequest(userEmailDTO)).isEqualTo(UserDetailsService.ActivationStatus.NO_SUCH_USER);
    }

    @Test
    @Transactional
    public void findUserByUsernameTest() {
        AssertionsForClassTypes.assertThat(userDetailsService.findUserByUsername("charlesz")).hasFieldOrPropertyWithValue("password", user1.getPassword());
        AssertionsForClassTypes.assertThat(userDetailsService.findUserByUsername("charlesz")).hasFieldOrPropertyWithValue("email", user1.getEmail());
        AssertionsForClassTypes.assertThat(userDetailsService.findUserByUsername("charlesz")).hasFieldOrPropertyWithValue("phoneNumber", user1.getPhoneNumber());
    }

    @Test
    @Transactional
    public void findVerificationTokenByUserTest() {
        AssertionsForClassTypes.assertThat(userDetailsService.findVerificationTokenByUsername(user1.getUsername()).getToken()).isEqualTo(token1.getToken());
        AssertionsForClassTypes.assertThat(userDetailsService.findVerificationTokenByUsername(user1.getUsername()).getUser().getUsername()).isEqualTo(token1.getUser().getUsername());
    }
}
