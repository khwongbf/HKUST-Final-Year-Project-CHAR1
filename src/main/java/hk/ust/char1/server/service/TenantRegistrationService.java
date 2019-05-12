package hk.ust.char1.server.service;

import hk.ust.char1.server.model.Role;
import hk.ust.char1.server.model.Tenant;
import hk.ust.char1.server.model.User;
import hk.ust.char1.server.repository.RoleRepository;
import hk.ust.char1.server.repository.TenantRepository;
import hk.ust.char1.server.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;

/**
 * Service that registers the user as a new tenant.
 * @author Wong Kwan Ho
 */
@Service
public class TenantRegistrationService {
    private final UserRepository userRepository;

    private final TenantRepository tenantRepository;

    private final RoleRepository roleRepository;

    @Autowired
    public TenantRegistrationService(UserRepository userRepository, TenantRepository tenantRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.tenantRepository = tenantRepository;
        this.roleRepository = roleRepository;
    }

    /**
     * Registers a {@link User} with the given username to be a new {@link Tenant}.
     * <p>
     *     Conditions to meet:
     *     <ol>
     *         <li>There exists a {@link User} with the given username.</li>
     *         <li>The given {@link User} is not currently a {@link Tenant}.</li>
     *     </ol>
     *     Return <code>false</code> if the above conditions are not met, true otherwise.
     * </p>
     * @param username The username of the {@link User}
     * @return <code>true</code> if the operation is successful, <code>false</code> otherwise.
     */
    @Transactional
    public boolean registerAsNewTenant(String username){
        if (!userRepository.existsUserByUsername(username) || tenantRepository.existsTenantByUsername(username)){
            return false;
        }
        User user = userRepository.findUserByUsername(username);
        Tenant tenant = new Tenant(user);
        tenant.setIndividualTenantApartmentPreferences(new ArrayList<>());
        tenant.setNumberRented(0);
        tenant.setInTenantGroup(null);
        tenant.setTenantRating(BigDecimal.ZERO);
        tenant.setTenantFlatmatePreference(null);

        if (!roleRepository.existsRoleByRole("TENANT")){
            Role role = new Role();
            role.setRole("TENANT");
            roleRepository.saveAndFlush(role);
        }

        tenant.getRole().clear();
        tenant.getRole().add(roleRepository.findRoleByRole("USER"));
        tenant.getRole().add(roleRepository.findRoleByRole("TENANT"));
        userRepository.deleteUserByUsername(username);
        userRepository.flush();

        tenantRepository.saveAndFlush(tenant);
        return true;
    }
}
