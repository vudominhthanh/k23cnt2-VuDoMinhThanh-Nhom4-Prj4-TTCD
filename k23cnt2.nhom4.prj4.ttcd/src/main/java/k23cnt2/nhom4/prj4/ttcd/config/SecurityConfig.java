package k23cnt2.nhom4.prj4.ttcd.config;

import k23cnt2.nhom4.prj4.ttcd.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFilter;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())

                .cors(cors -> cors.disable())

                .authorizeHttpRequests((requests) -> requests
                        .requestMatchers("/**/*.html", "/css/**", "/js/**", "/images/**", "/fonts/**", "/webjars/**", "/ws-order/**", "/error").permitAll()

                        .requestMatchers("/api/auth/**", "/auth", "/api/products/**", "/", "/product-detail", "/menu", "/about","/customer/**", "/staff/dashboard").permitAll()

                        .requestMatchers("/api/staff/orders/**").hasAnyRole("STAFF", "ADMIN")

                        .requestMatchers("/api/admin/**").hasAnyRole("ADMIN")

                        .requestMatchers("/api/**", "/api/customer/**","/api/cart/**","/api/order/**","/api/payments/mock-callback").authenticated()

                        .anyRequest().authenticated()
                )
//                .formLogin((form) -> form
//                        .loginPage("/auth")
//                        .loginProcessingUrl("/performlogin")
//                        .permitAll()
//                )
                .logout((logout) -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                )
                .rememberMe(remember -> remember
                        .key("uniqueAndSecretKey")
                        .tokenValiditySeconds(86400)
                        .rememberMeParameter("remember-me")
                );

        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
