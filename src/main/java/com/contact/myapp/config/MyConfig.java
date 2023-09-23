package com.contact.myapp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.contact.myapp.dao.UserDetailsServiceImpl;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

@Configuration
public class MyConfig {

    @Bean
    public UserDetailsService getUserDetailService() {
        return new UserDetailsServiceImpl();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();

        daoAuthenticationProvider.setUserDetailsService(this.getUserDetailService());
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());

        return daoAuthenticationProvider;
    }
    
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    // configure method..
    // @Override
    // protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    //     auth.authenticationProvider(authenticationProvider());
    // }

    // @Override
    // protected void configure(HttpSecurity http) throws Exception {
    //     http.authorizeRequests.antMatchers("/admin/**").hasRole("ADMIN")
    //     .antMatchers("/user/**").hasRole("USER")
    //     .antMatchers("/**").permitAll().and().formLogin().and().csrf().disable();
    // }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        
        // http.authorizeHttpRequests()
        // .requestMatchers("/admin/**").hasRole("ADMIN")
        // .requestMatchers("/user/**").hasRole("USER")
        // .requestMatchers("/**").permitAll().and().formLogin()
        // .loginPage("/signin")
        // .loginProcessingUrl("/do-login")
        // .defaultSuccessUrl("/user/index")
        // .failureUrl("/signin?error=true");

        http.authorizeHttpRequests(auth -> 
            auth.requestMatchers("/admin/**").hasRole("ADMIN")
            .requestMatchers("/user/**").hasRole("USER")
            .requestMatchers("/**").permitAll());
        
        http.formLogin(form -> form.loginPage("/signin")
            .loginProcessingUrl("/do-login")
            // .defaultSuccessUrl("/user/index")
            .failureUrl("/signin?error=true"));

        http.csrf(cs -> cs.disable());
        
        return http.build();
    }

}