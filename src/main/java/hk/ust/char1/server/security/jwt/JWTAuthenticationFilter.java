package hk.ust.char1.server.security.jwt;

import com.auth0.jwt.JWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import hk.ust.char1.server.dto.UserLoginDTO;
import hk.ust.char1.server.service.UserDetailsService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Date;

import static com.auth0.jwt.algorithms.Algorithm.HMAC512;
import static hk.ust.char1.server.security.SecurityConstants.*;

public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final AuthenticationManager authenticationManager;

    private final UserDetailsService userDetailsService;

    public JWTAuthenticationFilter(AuthenticationManager authenticationManager, UserDetailsService userDetailsService) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest req,
                                                HttpServletResponse res) throws AuthenticationException {
        try {
            UserLoginDTO creds = new ObjectMapper()
                    .readValue(req.getInputStream(), UserLoginDTO.class);



            return authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            creds.getUsername(),
                            creds.getPassword(),
                            userDetailsService.loadUserByUsername(creds.getUsername())
                            .getAuthorities()
                            )
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest req,
                                            HttpServletResponse res,
                                            FilterChain chain,
                                            Authentication auth) {

        LocalDateTime currentTime = LocalDateTime.now();


        String token = JWT.create()
                .withSubject(((User) auth.getPrincipal()).getUsername() +  ":" +  auth.getAuthorities().stream().map(GrantedAuthority::getAuthority).reduce("", (s1, s2) -> s1.equals("")? s2 : s1.concat(",").concat(s2)))
                .withIssuedAt(Date.from(currentTime
                        .toInstant(ZoneId.systemDefault().getRules().getOffset(currentTime))))
                .withExpiresAt(Date.from(currentTime
                        .plusSeconds(EXPIRATION_TIME)
                        .toInstant(ZoneId.systemDefault().getRules().getOffset(currentTime))))
                .sign(HMAC512(SECRET.getBytes()));
        res.addHeader(HEADER_STRING, TOKEN_PREFIX + token);
    }
}
