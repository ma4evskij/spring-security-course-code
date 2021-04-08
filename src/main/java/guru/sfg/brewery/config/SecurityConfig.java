package guru.sfg.brewery.config;

import guru.sfg.brewery.security.HeaderRestAuthFilter;
import guru.sfg.brewery.security.SgfPasswordEncoderFactories;
import guru.sfg.brewery.security.UrlRestAuthFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

/**
 * Created by jt on 6/13/20.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    HeaderRestAuthFilter restHeaderAuthFilter(AuthenticationManager manager) {
        var restFilter = new HeaderRestAuthFilter(new AntPathRequestMatcher("/api/**"));

        restFilter.setAuthenticationManager(manager);

        return restFilter;
    }

    UrlRestAuthFilter restUrlAuthFilter(AuthenticationManager manager) {
        var restUrlFilter = new UrlRestAuthFilter(new AntPathRequestMatcher("/api/**"));

        restUrlFilter.setAuthenticationManager(manager);

        return  restUrlFilter;
    }

    @Bean
    PasswordEncoder passwordEncoder(){
        return SgfPasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
                http
                .addFilterBefore(
                        restHeaderAuthFilter(authenticationManager()),
                        UsernamePasswordAuthenticationFilter.class
                )
                .addFilterBefore(
                        restUrlAuthFilter(authenticationManager()),
                        UsernamePasswordAuthenticationFilter.class
                )
                .csrf().disable();

                http
                .authorizeRequests(authorize -> {
                    authorize
                            .antMatchers("/h2-console/**").permitAll()
                            .antMatchers("/", "/webjars/**", "/login", "/resources/**").permitAll()
                            .antMatchers("/beers/find", "/beers*").permitAll()
                            .antMatchers(HttpMethod.GET, "/api/v1/beer/**").permitAll()
                            .mvcMatchers(HttpMethod.GET, "/api/v1/beerUpc/{upc}").permitAll();
                } )
                .authorizeRequests()
                .anyRequest().authenticated()
                .and()
                .formLogin().and()
                .httpBasic();

                http.headers().frameOptions().sameOrigin();
    }

//    @Autowired
//    JpaUserDetailsService jpaUserDetailsService;

//    @Override
//    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        //auth.userDetailsService(jpaUserDetailsService).passwordEncoder(passwordEncoder());

//        auth.inMemoryAuthentication()
//                .withUser("spring")
//                .password("{bcrypt}$2a$10$vfyveX.gzd4kV5I6H1qnQ.PctKATnaOKfeecc2aEeJgJe8hBIF3jm")
//                .roles("ADMIN")
//                .and()
//                .withUser("user")
//                .password("{sha256}4d5af05b2d0f55d1fa99fb93c47cd801a0ca461233c20e5736600ba907a06a036f44460c8e4fb3ff")
//                .roles("USER")
//                .and()
//                .withUser("scott")
//                .password("{bcrypt}$2a$15$EpzHryjEHxy2qaQP.IkG9On9N6I64f7DaBgyygRc7O7Nb46JKTWqC")
//                .roles("CUSTOMER");
//    }

    //    @Override
//    @Bean
//    protected UserDetailsService userDetailsService() {
//        var admin = User.withDefaultPasswordEncoder()
//                .username("spring")
//                .password("guru")
//                .roles("ADMIN")
//                .build();
//
//        var user = User.withDefaultPasswordEncoder()
//                .username("user")
//                .password("password")
//                .roles("USER")
//                .build();
//
//        return new InMemoryUserDetailsManager(admin, user);
//    }
}
