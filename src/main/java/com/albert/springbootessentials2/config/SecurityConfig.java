package com.albert.springbootessentials2.config;

import com.albert.springbootessentials2.service.AppUserDetailsService;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;

@Log4j2
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    /*
     * Postman code for automatizing CSRF token (2024):
     *      const cookieValue = pm.cookies.get('XSRF-TOKEN');
     *      pm.environment.set('xsrf-token', cookieValue);
     *
     * header to send the token:
     *      X-XSRF-TOKEN : {{ xsrf-token }}
     * */
    @Bean
    public SecurityFilterChain securityFilterChain(@NotNull HttpSecurity http) throws Exception {
        CsrfTokenRequestAttributeHandler requestHandler = new CsrfTokenRequestAttributeHandler();
//        By setting the csrfRequestAttributeName to null, the CsrfToken must first be loaded to determine
//        what attribute name to use. This causes the CsrfToken to be loaded on every request.
        requestHandler.setCsrfRequestAttributeName(null);

        http
                .csrf(csrf -> csrf.disable())
//                .csrf((csrf) -> csrf.csrfTokenRepository(new HttpSessionCsrfTokenRepository()))
//                .csrf(csrf -> csrf
//                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
//                        .csrfTokenRequestHandler(requestHandler))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/animes/admin/**").hasRole("ADMIN")
                        .requestMatchers("/animes/**").hasRole("USER")
                        .requestMatchers("/actuator/**").permitAll()
                        .anyRequest().authenticated())
                .httpBasic(Customizer.withDefaults())
                .formLogin(Customizer.withDefaults());

        return http.build();
    }

    @Autowired
    // this method may have any name
    public void UserAuthentication(@NotNull AuthenticationManagerBuilder auth, AppUserDetailsService appUserDetailsService) throws Exception {
        final PasswordEncoder passwordEncoder =
                PasswordEncoderFactories.createDelegatingPasswordEncoder();
        log.info("password: {}", passwordEncoder.encode("1234"));

        final UserDetails user1 = User
                .withUsername("albert")
                .password(passwordEncoder.encode("1234"))
                .roles("ADMIN", "USER")
                .build();

        auth.inMemoryAuthentication().withUser(user1);

        auth.userDetailsService(appUserDetailsService)
                .passwordEncoder(passwordEncoder);
    }

//    @Bean
//    public InMemoryUserDetailsManager userDetailsService() {
//        final PasswordEncoder passwordEncoder =
//                PasswordEncoderFactories.createDelegatingPasswordEncoder();
//
//        final UserDetails user1 = User
//                .withUsername("albert")
//                .password(passwordEncoder.encode("1234"))
//                .roles("ADMIN", "USER")
//                .build();
//
//        final UserDetails user2 = User
//                .withUsername("kaido")
//                .password(passwordEncoder.encode("1234"))
//                .roles("USER")
//                .build();
//
//        return new InMemoryUserDetailsManager(user1, user2);
//    }

}
