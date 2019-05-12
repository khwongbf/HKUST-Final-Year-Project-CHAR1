package hk.ust.char1.server.unit.jpa;

import hk.ust.char1.server.model.User;
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
public class UserJPATests {
    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private UserRepository userRepository;

    @Before
    public void clearDatabase(){
        userRepository.deleteAll();
    }

    @Test
    public void findAllInEmptyTest(){
        assertThat(userRepository.findAll()).isEmpty();
    }

    @Test
    public void addSingleUserTest(){
        var savedUser = userRepository.save(new User("comp3311","helloworld", "1234-5678", "iloveyou@gmail.com"));

        AssertionsForClassTypes.assertThat(savedUser).hasFieldOrPropertyWithValue("username", "comp3311");
        AssertionsForClassTypes.assertThat(savedUser).hasFieldOrPropertyWithValue("password","helloworld");
    }

    @Test
    public void removeAllUsersTest(){
        testEntityManager.persist(new User("obama","amabo", "1234-5677", "iloveyu@gmail.com"));
        testEntityManager.persist(new User("trump", "pmurt", "1234-5778", "ilveyou@gmail.com"));

        userRepository.deleteAll();

        assertThat(userRepository.findAll()).isEmpty();
    }

    @Test
    public void findAllTest(){
        var user1 = new User("obama","amabo","1234-5677", "iloveyu@gmail.com");
        var user2 = new User("trump", "pmurt", "1234-5778", "ilveyou@gmail.com");
        var user3 = new User("bush", "hsub", "1234-5678", "iloveyou@gmail.com");

        testEntityManager.persist(user1);
        testEntityManager.persist(user2);
        testEntityManager.persist(user3);

        assertThat(userRepository.findAll()).hasSize(3).contains(user1,user2,user3);
    }

    @Test
    public void findByIdTest(){
        var user1 = new User("obama","amabo", "1234-5677", "iloveyu@gmail.com");
        var user2 = new User("trump", "pmurt", "1234-5778", "ilveyou@gmail.com");
        var user3 = new User("bush", "hsub", "1234-5678", "iloveyou@gmail.com");

        testEntityManager.persist(user1);
        testEntityManager.persist(user2);
        testEntityManager.persist(user3);

        var foundUser = userRepository.getOne(user2.getUsername());

        AssertionsForClassTypes.assertThat(foundUser).isEqualTo(user2);
    }

    @Test
    public void existsByEmailTest(){
        var user1 = new User("obama","amabo", "1234-5678", "iloveyou@gmail.com");
        testEntityManager.persist(user1);

        AssertionsForClassTypes.assertThat(userRepository.existsUserByEmail(user1.getEmail())).isTrue();
        AssertionsForClassTypes.assertThat(userRepository.existsUserByEmail("ilove@gmail.com")).isFalse();
    }

    @Test
    public void existsByPhoneTest(){
        var user1 = new User("obama","amabo", "1234-5678", "iloveyou@gmail.com");
        testEntityManager.persist(user1);

        AssertionsForClassTypes.assertThat(userRepository.existsUserByPhoneNumber("1234-5678")).isTrue();
        AssertionsForClassTypes.assertThat(userRepository.existsUserByPhoneNumber("0000-5678")).isFalse();
    }

    @Test
    public void existsByUsernameTest(){
        var user1 = new User("obama","amabo", "1234-5678", "iloveyou@gmail.com");
        testEntityManager.persist(user1);

        AssertionsForClassTypes.assertThat(userRepository.existsUserByUsername(user1.getUsername())).isTrue();
        AssertionsForClassTypes.assertThat(userRepository.existsUserByUsername("")).isFalse();
    }

    @Test
    public void findUsersActivatedByUsername(){
        var user1 = new User("obama","amabo", "1234-5678", "iloveyou@gmail.com");
        testEntityManager.persist(user1);

        AssertionsForClassTypes.assertThat(userRepository.findUserByUsernameAndActivated(user1.getUsername(), false)).isEqualTo(user1);

        user1.setActivated(true);
        testEntityManager.persist(user1);
        AssertionsForClassTypes.assertThat(userRepository.findUserByUsernameAndActivated(user1.getUsername(), true)).isEqualTo(user1);
    }
}
