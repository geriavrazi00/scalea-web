package com.scalea.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.scalea.utils.Constants;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	@Autowired
	private UserDetailsService userDetailsService;
	
	@Bean
	public PasswordEncoder encoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailsService).passwordEncoder(encoder());
	}
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		String[] adminRoutes = {"/barcode**", "/barcode/**", "/roles**", "/roles/**", "/users**", "/users/**"};
		String[] sharedRoutes = {"/", "/home", "/areas**", "/areas/**", "/employees**", "/employees/**", "/vacancies**", "/vacancies/**", 
			"/processes**", "/processes/**", "/products**", "/products/**"};
		
		http.authorizeRequests()
			.antMatchers(adminRoutes).access("hasRole('" + Constants.ROLE_ADMIN + "')")
			.antMatchers(sharedRoutes).access("hasAnyRole('" + Constants.ROLE_ADMIN + "', '" + Constants.ROLE_USER + "')")
			.antMatchers("/**").access("permitAll")
			.and().formLogin().loginPage("/login")
			.and().logout().logoutSuccessUrl("/");
	}
}