package com.app.km;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.sql.DataSource;
import java.util.Arrays;

/**
 * Created by Kamil-PC on 19.05.2017.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private DataSource dataSource;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Autowired
    public SecurityConfig(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    protected void configure(HttpSecurity auth) throws Exception {
        auth
                .httpBasic()
                .and()
                .authorizeRequests()
                .antMatchers(HttpMethod.PUT,"/api/users/**").permitAll()
                .antMatchers(HttpMethod.GET,"/api/car/**").permitAll()
                .antMatchers(HttpMethod.GET,"/api/users/**").hasAnyRole("ADMIN","USER")
                .antMatchers(HttpMethod.POST,"/api/users/**").hasAnyRole("ADMIN","USER")
                .antMatchers(HttpMethod.GET,"/api/rent/**").hasAnyRole("ADMIN","USER")
                .antMatchers(HttpMethod.PUT,"/api/rent/**").hasAnyRole("ADMIN","USER")
                .antMatchers(HttpMethod.POST,"/api/rent/**").hasAnyRole("ADMIN","USER")
                .antMatchers(HttpMethod.POST,"/api/car/**").hasRole("ADMIN")
                .antMatchers(HttpMethod.PUT,"/api/car/**").hasRole("ADMIN")
                .antMatchers(HttpMethod.DELETE,"/api/users/**").hasRole("ADMIN")
                .antMatchers(HttpMethod.DELETE,"/api/car/**").hasRole("ADMIN")
                .antMatchers(HttpMethod.DELETE,"/api/rent/**").hasRole("ADMIN")
                .anyRequest().fullyAuthenticated()
                .and()
                .formLogin()
                .usernameParameter("username")
                .passwordParameter("password")
                .and()
                .logout()
                .permitAll()
                .and()
                .csrf()
                .disable();
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .jdbcAuthentication()
                .dataSource(dataSource)
                .passwordEncoder(passwordEncoder())
                .usersByUsernameQuery("select username, password, enabled FROM users where username = ?")
                .authoritiesByUsernameQuery("select u.username, r.role as role_name from role r" +
                        " join users u on u.role_id = r.id" +
                        " where u.username = ?");
    }
}
