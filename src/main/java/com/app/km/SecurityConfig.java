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
                .antMatchers(HttpMethod.GET,"/api/car/*","/api/car*","/api/rent/*","/api/rent*","/api/users*","/api/users/*").hasRole("ADMIN")
                .antMatchers(HttpMethod.POST,"/api/car/*","/api/car*","/api/rent/*","/api/rent*","/api/users*","/api/users/*").hasRole("ADMIN")
                .antMatchers(HttpMethod.PUT,"/api/car/*","/api/car*","/api/rent/*","/api/rent*","/api/users*","/api/users/*").hasRole("ADMIN")
                .antMatchers(HttpMethod.DELETE,"/api/car/*","/api/car*","/api/rent/*","/api/rent*","/api/users*","/api/users/*").hasRole("ADMIN")
                .antMatchers(HttpMethod.GET,"/api/car/*","/api/car*","/api/rent/*","/api/rent*","/api/users*","/api/users/*").hasRole("USER")
                .antMatchers(HttpMethod.POST,"/api/rent/*","/api/rent*").hasRole("USER")
                .antMatchers(HttpMethod.PUT,"/api/users*","/api/users/*").permitAll()
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
