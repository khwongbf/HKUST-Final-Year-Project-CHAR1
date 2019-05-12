package hk.ust.char1.server.repository;

import hk.ust.char1.server.model.User;
import hk.ust.char1.server.model.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TokenRepository extends JpaRepository<VerificationToken, Long> {

    /**
     * Queries the {@link VerificationToken VerificationToken} table given the token.
     * @param token the token to be searched through
     * @return the entry that has the token
     */
    VerificationToken findVerificationTokenByToken(String token);


    VerificationToken findVerificationTokenByUser_Username(String username);
}
