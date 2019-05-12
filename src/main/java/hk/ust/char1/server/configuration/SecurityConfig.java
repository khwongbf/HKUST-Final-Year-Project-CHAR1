package hk.ust.char1.server.configuration;

import hk.ust.char1.server.repository.UserRepository;
import hk.ust.char1.server.security.jwt.JWTAuthenticationFilter;
import hk.ust.char1.server.security.jwt.JWTAuthorizationFilter;
import hk.ust.char1.server.service.UserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.SecurityContextConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import static hk.ust.char1.server.security.SecurityConstants.SIGN_UP_URL;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@EnableWebSecurity
@EnableJpaRepositories(basePackageClasses = UserRepository.class)
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private final BasicAuthenticationPoint authenticationEntryPoint;

    private final UserDetailsService userDetailsService;

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public SecurityConfig(BasicAuthenticationPoint authenticationEntryPoint, UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Used by the default implementation of {@link #authenticationManager()} to attempt
     * to obtain an {@link AuthenticationManager}. If overridden, the
     * {@link AuthenticationManagerBuilder} should be used to specify the
     * {@link AuthenticationManager}.
     *
     * <p>
     * The {@link #authenticationManagerBean()} method can be used to expose the resulting
     * {@link AuthenticationManager} as a Bean. The {@link #userDetailsServiceBean()} can
     * be used to expose the last populated {@link UserDetailsService} that is created
     * with the {@link AuthenticationManagerBuilder} as a Bean. The
     * {@link UserDetailsService} will also automatically be populated on
     * {@link HttpSecurity#getSharedObject(Class)} for use with other
     * {@link SecurityContextConfigurer} (i.e. RememberMeConfigurer )
     * </p>
     *
     * <p>
     * For example, the following configuration could be used to register in memory
     * authentication that exposes an in memory {@link UserDetailsService}:
     * </p>
     *
     * <pre>
     * &#064;Override
     * protected void configure(AuthenticationManagerBuilder auth) {
     * 	auth
     * 	// enable in memory based authentication with a user named
     * 	// &quot;user&quot; and &quot;admin&quot;
     * 	.inMemoryAuthentication().withUser(&quot;user&quot;).password(&quot;password&quot;).roles(&quot;USER&quot;).and()
     * 			.withUser(&quot;admin&quot;).password(&quot;password&quot;).roles(&quot;USER&quot;, &quot;ADMIN&quot;);
     * }
     *
     * // Expose the UserDetailsService as a Bean
     * &#064;Bean
     * &#064;Override
     * public UserDetailsService userDetailsServiceBean() throws Exception {
     * 	return super.userDetailsServiceBean();
     * }
     *
     * </pre>
     *
     * @param auth the {@link AuthenticationManagerBuilder} to use
     * @throws Exception any exception that may arouse when configuring the {@link AuthenticationManagerBuilder}
     */
    @Override
    @Autowired
    protected void configure(AuthenticationManagerBuilder auth) throws Exception{
        auth.userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder);
    }

    /**
     * Override this method to configure the {@link HttpSecurity}. Typically subclasses
     * should not invoke this method by calling super as it may override their
     * configuration. The default configuration is:
     *
     * <pre>
     * http.authorizeRequests().anyRequest().authenticated().and().formLogin().and().httpBasic();
     * </pre>
     *
     * @param http the {@link HttpSecurity} to modify
     * @throws Exception if an error occurs
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors().and().csrf().disable().authorizeRequests()
                .antMatchers("/","/registration", "/confirmRegistration","/forgetEmail","/forgetPassword",SIGN_UP_URL).permitAll()
                .anyRequest().authenticated()
        .and()
        .addFilter(new JWTAuthenticationFilter(authenticationManager(),userDetailsService))
        .addFilter(new JWTAuthorizationFilter(authenticationManager()))
        ;
        http.httpBasic().authenticationEntryPoint(authenticationEntryPoint);

        http.sessionManagement().sessionCreationPolicy(STATELESS);
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", new CorsConfiguration().applyPermitDefaultValues());
        return source;
    }


    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder);
        return authenticationProvider;
    }


}
