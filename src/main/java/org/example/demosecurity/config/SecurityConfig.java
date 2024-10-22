package org.example.demosecurity.config;

import org.example.demosecurity.service.UserDetailsServiceImp;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(Customizer.withDefaults())
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers("/books").hasAnyAuthority("USER", "CREATOR", "EDITOR", "ADMIN");
                    auth.requestMatchers("/new").hasAnyAuthority("CREATOR", "ADMIN");
                    auth.requestMatchers("/edit/**").hasAnyAuthority("EDITOR", "ADMIN");
                    auth.requestMatchers("/delete/**").hasAnyAuthority("ADMIN");
                    auth.requestMatchers(HttpMethod.POST).hasAnyAuthority("ADMIN", "CREATOR", "EDITOR");
                    auth.requestMatchers(HttpMethod.GET).authenticated();
                    auth.requestMatchers(HttpMethod.DELETE).hasAnyAuthority("ADMIN");
                    auth.anyRequest().authenticated();
                })
                .formLogin(login -> {
                    login.permitAll();
                    login.loginPage("/login");
                    login.successHandler(successHandler());
                })
                .logout(logout -> {
                    logout.logoutUrl("/logout");
                    logout.logoutSuccessUrl("/login");
                    logout.invalidateHttpSession(true);
                    logout.clearAuthentication(true);
                    logout.deleteCookies("JSESSIONID");

                })
                .exceptionHandling(exception -> {
                    exception.accessDeniedHandler(deniedHandler());
                })
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(UserDetailsServiceImp userDetailsService) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder());
        provider.setUserDetailsService(userDetailsService);
        return provider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /* Handlers */
    @Bean
    public AccessDeniedHandler deniedHandler() {
        return (request, response, auth) -> {
            response.sendRedirect("/403");
        };
    }

    @Bean
    public AuthenticationSuccessHandler successHandler () {
        return (request, response, auth) -> {
            response.sendRedirect("/books");
        };
    }
}
