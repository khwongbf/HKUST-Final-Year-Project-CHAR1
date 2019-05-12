package hk.ust.char1.server.repository;

import hk.ust.char1.server.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
public interface UserRepository extends JpaRepository<User,String> {
    boolean existsUserByUsername(String username);

    boolean existsUserByPhoneNumber(String phoneNumber);

    boolean existsUserByEmail(String email);

    User findUserByUsername(String username);

//    @Query("SELECT * FROM User u WHERE u.username = :username AND u.activated = :activated")
    User findUserByUsernameAndActivated(String username, boolean activated);

    void deleteUserByUsername(String username);
}
