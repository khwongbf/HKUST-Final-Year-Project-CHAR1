package hk.ust.char1.server.service;

import hk.ust.char1.server.model.Buyer;
import hk.ust.char1.server.model.Role;
import hk.ust.char1.server.model.User;
import hk.ust.char1.server.repository.BuyerRepository;
import hk.ust.char1.server.repository.RoleRepository;
import hk.ust.char1.server.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * Service that registers user as a new {@link Buyer}.
 * @author Wong Kwan Ho
 */
@Service
public class BuyerRegistrationService {
    private final UserRepository userRepository;

    private final BuyerRepository buyerRepository;

    private final RoleRepository roleRepository;

    @Autowired
    public BuyerRegistrationService(UserRepository userRepository, BuyerRepository buyerRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.buyerRepository = buyerRepository;
        this.roleRepository = roleRepository;
    }

    /**
     * Registers the {@link User} as new {@link Buyer}. Other roles of the user will be nullified
     * <p>
     *     Conditions to meet:
     *     <ol>
     *         <li>There exists a {@link User} for the given username</li>
     *         <li> The {@link User} must not be a {@link Buyer}</li>
     *     </ol>
     *     Return <code>false</code> if the above conditions are not met.
     * </p>
     * @param username The username of the {@link User}.
     * @return <code>true</code> if the {@link User} has successfully registered as a {@link Buyer}, <code>false</code> otherwise.
     */
    @Transactional
    public boolean registerAsNewBuyer(String username){
        if (!userRepository.existsUserByUsername(username) || buyerRepository.existsBuyerByUsername(username)){
            return false;
        }else{
            User user = userRepository.findUserByUsername(username);
            Buyer buyer = new Buyer(user);
            buyer.setBuyerRating(new BigDecimal("0.00"));
            buyer.setNumberBought(0);

            if (!roleRepository.existsRoleByRole("BUYER")){
                Role role = new Role();
                role.setRole("BUYER");
                roleRepository.saveAndFlush(role);
            }

            buyer.getRole().clear();
            buyer.getRole().add(roleRepository.findRoleByRole("USER"));
            buyer.getRole().add(roleRepository.findRoleByRole("BUYER"));

            userRepository.deleteUserByUsername(username);

            buyerRepository.saveAndFlush(buyer);


            return true;
        }
    }
}
