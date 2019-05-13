package hk.ust.char1.server.unit.jpa;

import hk.ust.char1.server.model.User;
import hk.ust.char1.server.model.VerificationToken;
import hk.ust.char1.server.repository.TokenRepository;
import hk.ust.char1.server.repository.UserRepository;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;

@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = NONE)
public class VerificationTokenJPATests {
    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TokenRepository tokenRepository;

    @Before
    public void clearDatabase(){
        tokenRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    public void findAllInEmptyTest(){
        assertThat(tokenRepository.findAll()).isEmpty();
    }

    @Test
    public void addSingleTest(){
        var user = new User("comp3311","helloworld", "12345678", "iloveyou@gmail.com");
        testEntityManager.persist(user);
        var verificationToken = new VerificationToken("faisfiowe4ote", user);

        var savedtoken = tokenRepository.save(verificationToken);

        AssertionsForClassTypes.assertThat(savedtoken).hasFieldOrPropertyWithValue("token", verificationToken.getToken());
        AssertionsForClassTypes.assertThat(savedtoken).hasFieldOrPropertyWithValue("user",user);
    }

    @Test
    public void removeAllTest(){
        var user1 = new User("comp3311","helloworld", "12345677", "iloveyu@gmail.com");
        testEntityManager.persist(user1);
        var user2 = new User("comp3331","helloworld", "12345778", "ilveyou@gmail.com");
        testEntityManager.persist(user2);

        var verificationToken1 = new VerificationToken("faisfiowe4ote", user1);
        testEntityManager.persist(verificationToken1);
        var verificationToken2 = new VerificationToken("faiwe4ote", user2);
        testEntityManager.persist(verificationToken2);

        tokenRepository.deleteAll();

        assertThat(tokenRepository.findAll()).isEmpty();
    }

    @Test
    public void findAllTest(){
        var user1 = new User("obama","amabo","12345677", "iloveyu@gmail.com");
        var user2 = new User("trump", "pmurt", "12345778", "ilveyou@gmail.com");
        var user3 = new User("bush", "hsub", "12345678", "iloveyou@gmail.com");

        testEntityManager.persist(user1);
        testEntityManager.persist(user2);
        testEntityManager.persist(user3);

        var verificationToken1 = new VerificationToken("faisfiowe4ote", user1);
        testEntityManager.persist(verificationToken1);
        var verificationToken2 = new VerificationToken("faiwe4ote", user2);
        testEntityManager.persist(verificationToken2);

        assertThat(tokenRepository.findAll()).hasSize(2).contains(verificationToken1,verificationToken2);
    }

    @Test
    public void findByIdTest(){
        var user1 = new User("obama","amabo", "12345677", "iloveyu@gmail.com");
        var user2 = new User("trump", "pmurt", "12345778", "ilveyou@gmail.com");
        var user3 = new User("bush", "hsub", "12345678", "iloveyou@gmail.com");

        testEntityManager.persist(user1);
        testEntityManager.persist(user2);
        testEntityManager.persist(user3);

        var verificationToken1 = new VerificationToken("faisfiowe4ote", user1);
        testEntityManager.persist(verificationToken1);
        var verificationToken2 = new VerificationToken("faiwe4ote", user2);
        testEntityManager.persist(verificationToken2);

        var foundToken = tokenRepository.getOne(verificationToken2.getId());

        AssertionsForClassTypes.assertThat(foundToken).isEqualTo(verificationToken2);
    }


    @Test
    public void findByTokenTest(){
        var user1 = new User("obama","amabo", "12345677", "iloveyu@gmail.com");
        var user2 = new User("trump", "pmurt", "12345778", "ilveyou@gmail.com");
        var user3 = new User("bush", "hsub", "12345678", "iloveyou@gmail.com");

        testEntityManager.persist(user1);
        testEntityManager.persist(user2);
        testEntityManager.persist(user3);

        var verificationToken1 = new VerificationToken("faisfiowe4ote", user1);
        testEntityManager.persist(verificationToken1);
        var verificationToken2 = new VerificationToken("faiwe4ote", user2);
        testEntityManager.persist(verificationToken2);

        AssertionsForClassTypes.assertThat(tokenRepository.findVerificationTokenByToken(verificationToken1.getToken())).isEqualTo(verificationToken1);
        AssertionsForClassTypes.assertThat(tokenRepository.findVerificationTokenByToken(verificationToken2.getToken())).isEqualTo(verificationToken2);
    }
}
