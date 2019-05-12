package hk.ust.char1.server.controller;

import hk.ust.char1.server.dto.UserDTO;
import hk.ust.char1.server.dto.UserEmailDTO;
import hk.ust.char1.server.dto.UserLoginDTO;
import hk.ust.char1.server.event.OnEmailChangeEvent;
import hk.ust.char1.server.event.OnRegistrationSuccessEvent;
import hk.ust.char1.server.event.OnVerificationRetryEvent;
import hk.ust.char1.server.model.User;
import hk.ust.char1.server.model.VerificationToken;
import hk.ust.char1.server.security.jwt.JWTTokenGenerator;
import hk.ust.char1.server.service.UserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import javax.servlet.ServletException;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Map;

import static hk.ust.char1.server.security.SecurityConstants.HEADER_STRING;
import static hk.ust.char1.server.security.SecurityConstants.TOKEN_PREFIX;
import static hk.ust.char1.server.service.UserDetailsService.*;
import static hk.ust.char1.server.service.UserDetailsService.RegistrationStatus.*;

/**
 * Endpoint class that contains functions for registration, verification and login.
 * @version 0.0.1
 * @author Leo Wong
 */
@RestController
public class UserDetailsController {
    private final UserDetailsService userService;

    private final ApplicationEventPublisher applicationEventPublisher;

    private final AuthenticationProvider authenticationProvider;

    private final MessageSource messageSource;

    private final JWTTokenGenerator jwtTokenGenerator;

    @Autowired
    public UserDetailsController(UserDetailsService userService, ApplicationEventPublisher applicationEventPublisher, AuthenticationProvider authenticationProvider, MessageSource messageSource, JWTTokenGenerator jwtTokenGenerator) {
        this.userService = userService;
        this.applicationEventPublisher = applicationEventPublisher;
        this.authenticationProvider = authenticationProvider;
        this.messageSource = messageSource;
        this.jwtTokenGenerator = jwtTokenGenerator;
    }

    @GetMapping("/")
    public ResponseEntity<String> blankPage(WebRequest webRequest){
        return ResponseEntity.ok("Hello World!");
    }

    /**
     * Registers the user and sends a verification link to the user's email.
     * @param userDTO The details containing the user's username, password, email and phone.
     * @param request The incoming web request.
     * @return HTTP Response that contains :<ul><li>a status code of : <ul><li><code>403</code> (for unsuccessful registrations)</li> <li><code>200</code> (for successful registrations)</li></ul></li> <li>a string in the response body for explanation</li></ul>
     */
    @PostMapping("/registration")
    public ResponseEntity registerNewUser(@RequestBody @Valid UserDTO userDTO, WebRequest request){
        Map<RegistrationStatus, User> returnedHashMap = userService.incomingNewRegistration(userDTO);
        if (returnedHashMap.containsKey(USERNAME_EXISTS)){
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body("The username already exists. Please Use another username");
        } else if (returnedHashMap.containsKey(PHONE_EXISTS)){
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body("The phone is already in use. Please provide another phone number.");
        } else if (returnedHashMap.containsKey(EMAIL_EXISTS)){
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body("The email is already in use. Please provide another email");
        } else if (returnedHashMap.containsKey(POOR_PASSWORD)){
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body("The password is too weak!\nThe password should:\n 1.\tHave at least 8 characters.\n2.\tHave at least a CAPITAL letter and a lowercase letter.\n3.\tContains at least a digit.");
        } else{
            try {
                String appUrl = request.getContextPath();
                applicationEventPublisher.publishEvent(new OnRegistrationSuccessEvent(returnedHashMap.get(AWAIT_FOR_VERIFICATION), appUrl));
            }catch(Exception re) {
                re.printStackTrace();
            }
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body("Registration Success. Check your email to activate your account");
        }
    }


    @GetMapping("/confirmRegistration")
    public ResponseEntity<String> confirmRegistration(WebRequest request, Model model,@RequestParam("token") String token) {
        Locale locale=request.getLocale();
        VerificationToken verificationToken = userService.getVerificationToken(token);
        if(verificationToken == null) {
            String message = messageSource.getMessage("auth.message.invalidToken", null, locale);
            model.addAttribute("message", message);
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Access Denied: Invalid Token");
        }
        User user = verificationToken.getUser();
        if(LocalDateTime.now().isAfter(verificationToken.getExpiryDate())) {
            String message = messageSource.getMessage("auth.message.expired", null, locale);

            String appUrl = request.getContextPath();
            applicationEventPublisher.publishEvent(new OnVerificationRetryEvent(user,appUrl));
            model.addAttribute("message", message);
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body("Access Denied: Token Expired. A new email has been sent to you for confirmation.");
        }

        userService.activateUser(user);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body("Your account has been successfully activated.");
    }

    /**
     * Forget Email function.
     * @param request the incoming request.
     * @param userEmailDTO An object that contains the username and the new email address.
     * @return HTTP Response that contains :<ul><li>a status code of : <ul><li><code>403</code> (for unsuccessful change of email)</li> <li><code>200</code> (for successful change of email)</li></ul></li> <li>a string in the response body for explanation</li></ul>
     */
    @PutMapping("/forgetEmail")
    public ResponseEntity forgetEmailBeforeActivation(WebRequest request, @RequestBody @Valid UserEmailDTO userEmailDTO){
        switch (userService.handleChangeEmailRequest(userEmailDTO)){
            case ALREADY_ACTIVATED:
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("The account was already verified, thus the email cannot be changed.");
            case NO_SUCH_USER:
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("There is no such account with username = " + userEmailDTO.getUsername());
            case ALLOW_CHANGE:
                break;
        }
        applicationEventPublisher.publishEvent(new OnEmailChangeEvent(userService.findUserByUsername(userEmailDTO.getUsername()),request.getContextPath()));
        return ResponseEntity.ok().body("Successfully set the user " + userEmailDTO.getUsername() + " to the new email "+ userEmailDTO.getEmail());
    }

    /**
     * Login the user.
     * @param userLoginDTO An object that contains the username and password.
     * @return HTTP Response that contains :<ul><li>a status code of : <ul><li><code>401</code> (for unsuccessful login attempts)</li> <li><code>200</code> (for successfullogin)</li></ul></li> <li>a string in the response body for explanation</li></ul>
     * @throws ServletException when the username or password is empty, to prevent a server error.
     */
    @PostMapping("/login")
    public ResponseEntity<String> loginUser(@RequestBody UserLoginDTO userLoginDTO) throws ServletException {

        if (userLoginDTO.getUsername().isEmpty() || userLoginDTO.getPassword().isEmpty()){
            throw new ServletException("Please fill in the username and password.");
        }

        String jwtToken;
        try {
            UserDetails user =  userService.loadUserByUsername(userLoginDTO.getUsername());
            authenticationProvider.authenticate(new UsernamePasswordAuthenticationToken(
                    userLoginDTO.getUsername(),
                    userLoginDTO.getPassword(),
                    user.getAuthorities()));

            jwtToken = jwtTokenGenerator.generate(userLoginDTO.getUsername(), user.getAuthorities());
        }catch (UsernameNotFoundException | BadCredentialsException e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("The username or password is incorrect.");
        } catch (DisabledException e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Please activate your account, by checking your email.");
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unknown error occurred");
        }
        return ResponseEntity.status(HttpStatus.OK)
                .header(HEADER_STRING, TOKEN_PREFIX + jwtToken)
                .body("Login Successful.");
    }

    /**
     * Logout the user. The header should be removed from the client application.
     * @param user the concerned user.
     * @return HTTP response of <code>OK (status code: 200)</code> and a string of "Logout Successful".
     */
    @GetMapping("/logout")
    public ResponseEntity logout(@AuthenticationPrincipal final User user){
        return ResponseEntity.ok().header(HEADER_STRING,TOKEN_PREFIX)
                .body("Logout successful");
    }
}
