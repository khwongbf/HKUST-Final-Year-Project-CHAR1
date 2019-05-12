package hk.ust.char1.server.service;


import hk.ust.char1.server.dto.UserDTO;
import hk.ust.char1.server.dto.UserEmailDTO;
import hk.ust.char1.server.model.Role;
import hk.ust.char1.server.model.User;
import hk.ust.char1.server.model.VerificationToken;
import hk.ust.char1.server.repository.RoleRepository;
import hk.ust.char1.server.repository.TokenRepository;
import hk.ust.char1.server.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static hk.ust.char1.server.service.UserDetailsService.ActivationStatus.ALLOW_CHANGE;
import static hk.ust.char1.server.service.UserDetailsService.ActivationStatus.ALREADY_ACTIVATED;
import static hk.ust.char1.server.service.UserDetailsService.RegistrationStatus.*;

/**
 * Service that controls the core credentials for users.
 * @author Wong Kwan Ho
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class UserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {
    private static final String USER_ROLE_NAME = "USER";

    private final UserRepository userRepository;

    private final TokenRepository tokenRepository;

    private final RoleRepository roleRepository;

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserDetailsService(UserRepository userRepository, TokenRepository tokenRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Handles a new registration from the client.
     * <p>
     *     It accepts a {@link UserDTO UserDTO} for handling, where the DTO is passed by the client.
     * </p>
     * @param userDTO DTO that contains the new user's details.
     * @return map that contains the outcome of registration, and the user registered if successful.
     */
    public Map<RegistrationStatus, User> incomingNewRegistration(UserDTO userDTO){
        HashMap<RegistrationStatus, User> returnMap = new HashMap<>();
        if (userRepository.existsUserByUsername(userDTO.getUsername().trim())){
            returnMap.put(USERNAME_EXISTS,null);
        } else if (userRepository.existsUserByPhoneNumber(userDTO.getPhone())){
            returnMap.put(PHONE_EXISTS,null);
        } else if (userRepository.existsUserByEmail(userDTO.getEmail())){
            returnMap.put(EMAIL_EXISTS,null);
        } else if (!checkPassword(userDTO.getPassword())){
            returnMap.put(POOR_PASSWORD,null);
        }
        else {
            User user = new User();
            user.setUsername(userDTO.getUsername().trim());
            user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
            user.setPhoneNumber(userDTO.getPhone());
            user.setEmail(userDTO.getEmail());
            user.setRole(new ArrayList<>());
            if (!roleRepository.existsRoleByRole(USER_ROLE_NAME)){
                Role role = new Role();
                role.setRole(USER_ROLE_NAME);
                roleRepository.saveAndFlush(role);
            }

            user.getRole().add(roleRepository.findRoleByRole(USER_ROLE_NAME));

            userRepository.saveAndFlush(user);

            returnMap.put(AWAIT_FOR_VERIFICATION, user);
        }
        return returnMap;
    }

    /**
     * Checks whether the given password is valid and strong.
     * @param password password to be checked
     * @return true if password is strong enough, false otherwise
     */
    private boolean checkPassword(String password) {
        List<Character> passwordCharsList = password
                .chars()
                .mapToObj(c -> (char) c)
                .collect(Collectors.toList());
        return (password.length() >= 8
                &&
                passwordCharsList
                        .parallelStream()
                        .anyMatch(Character::isUpperCase)
                &&
                passwordCharsList
                        .parallelStream()
                        .anyMatch(Character::isLowerCase)
                &&
                passwordCharsList
                        .parallelStream()
                        .anyMatch(Character::isDigit)
                &&
                passwordCharsList
                        .parallelStream()
                        .noneMatch(Character::isSpaceChar)
        );
    }

    /**
     * Creates a verification token for the user
     * <p>
     *     This method creates a verification token for email verification from the {@link User User}. The token is then saved in the database.
     * </p>
     * @param user User to be verified
     * @param token Token generated
     */
    public void createVerificationToken(User user, String token) {
        VerificationToken newUserToken = new VerificationToken(token, user);
        tokenRepository.saveAndFlush(newUserToken);
    }

    /**
     * Updates the verification token for the user due to expiry.
     * @param user The user concerned
     * @param token the new token to be replaced
     */
    public void updateVerificationToken(User user, String token){
        VerificationToken currentToken = tokenRepository.findVerificationTokenByUser_Username(user.getUsername());
        currentToken.setToken(token);
        currentToken.setCreatedDate(LocalDateTime.now());
        currentToken.setExpiryDate(LocalDateTime.now().plusHours(24));
        tokenRepository.saveAndFlush(currentToken);
    }

    /**
     * Searches the verification token in the database.
     * @param token the token to be searched
     * @return the entry of the verification token in the database
     */
    public VerificationToken getVerificationToken(String token){
        return tokenRepository.findVerificationTokenByToken(token);
    }

    public void activateUser(User user) {


        user.setActivated(true);
        userRepository.saveAndFlush(user);

        var token = tokenRepository.findVerificationTokenByUser_Username(user.getUsername());

        if (token != null){
	        tokenRepository.delete(tokenRepository.findVerificationTokenByUser_Username(user.getUsername()));
	        tokenRepository.flush();
        }
    }

    public ActivationStatus handleChangeEmailRequest(UserEmailDTO userEmailDTO) {
        if(!userRepository.existsUserByUsername(userEmailDTO.getUsername())){
            return ActivationStatus.NO_SUCH_USER;
        } else{
            User user = userRepository.findUserByUsername(userEmailDTO.getUsername());
            if (user.isActivated()){
                return ALREADY_ACTIVATED;
            }else {
                user.setEmail(userEmailDTO.getEmail());
                userRepository.saveAndFlush(user);
                return ALLOW_CHANGE;
            }
        }
    }

    public User findUserByUsername(String username){
        return userRepository.findUserByUsername(username);
    }

    public VerificationToken findVerificationTokenByUsername(String username){
        return tokenRepository.findVerificationTokenByUser_Username(username);
    }

    /**
     * Locates the user based on the username. In the actual implementation, the search
     * may possibly be case sensitive, or case insensitive depending on how the
     * implementation instance is configured. In this case, the <code>UserDetails</code>
     * object that comes back may have a username that is of a different case than what
     * was actually requested..
     *
     * @param username the username identifying the user whose data is required.
     * @return a fully populated user record (never <code>null</code>)
     * @throws UsernameNotFoundException if the user could not be found or the user has no
     *                                   GrantedAuthority
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException, DisabledException {
        User user = userRepository.findUserByUsername(username);
        if (user == null){
            throw new UsernameNotFoundException("Username not found");
        }else if (!user.isEnabled()){
            throw new DisabledException("Not Activated");
        }
        else {
            return new org.springframework.security.core.userdetails.User(user.getUsername(),user.getPassword(),user.getAuthorities());
        }
    }


    public void deleteAllUsers(){
        tokenRepository.deleteAll();
        roleRepository.deleteAll();
        userRepository.deleteAll();
    }

    public enum RegistrationStatus {
        USERNAME_EXISTS, POOR_PASSWORD, PHONE_EXISTS, EMAIL_EXISTS, AWAIT_FOR_VERIFICATION
    }

    public enum ActivationStatus{
        ALREADY_ACTIVATED, NO_SUCH_USER, ALLOW_CHANGE
    }
}
