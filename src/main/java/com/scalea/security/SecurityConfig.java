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
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

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
	
	@Bean
	public AuthenticationSuccessHandler myAuthenticationSuccessHandler(){
	    return new AuthenticationRedirectHandler();
	}
	
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailsService).passwordEncoder(encoder());
	}
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
//		String[] adminRoutes = {"/barcode**", "/barcode/**", "/roles**", "/roles/**", "/users**", "/users/**"};
//		String[] sharedRoutes = {"/", "/home", "/areas**", "/areas/**", "/employees**", "/employees/**", "/vacancies**", "/vacancies/**", 
//			"/processes**", "/processes/**", "/products**", "/products/**", "/profile**", "/profile/**", "/finances**", 
//			"/finances/**", "/statistics**", "/statistics/**"};
		
		http.authorizeRequests()
			.antMatchers("/areas**", "/areas/**").access("hasAnyAuthority('" + Constants.VIEW_AREAS_PRIVILEGE + "', '" + Constants.UPSERT_AREAS_PRIVILEGE + "', '" + Constants.DELETE_AREAS_PRIVILEGE + "')")
			.antMatchers("/employees**", "/employees/**").access("hasAnyAuthority('" + Constants.VIEW_EMPLOYEES_PRIVILEGE + "', '" + Constants.UPSERT_EMPLOYEES_PRIVILEGE + "', '" + Constants.DELETE_EMPLOYEES_PRIVILEGE + "', '" + Constants.UPLOAD_EMPLOYEES_PRIVILEGE + "')")
			.antMatchers("/vacancies**", "/vacancies/**").access("hasAnyAuthority('" + Constants.VIEW_VACANCIES_PRIVILEGE + "', '" + Constants.UPSERT_VACANCIES_PRIVILEGE + "', '" + Constants.DELETE_VACANCIES_PRIVILEGE + "')")
			.antMatchers("/processes**", "/processes/**").access("hasAnyAuthority('" + Constants.VIEW_PROCESSES_PRIVILEGE + "', '" + Constants.MANAGE_PROCESSES_PRIVILEGE + "')")
			.antMatchers("/products**", "/products/**").access("hasAnyAuthority('" + Constants.VIEW_PRODUCTS_PRIVILEGE + "', '" + Constants.UPSERT_PRODUCTS_PRIVILEGE + "', '" + Constants.DELETE_PRODUCTS_PRIVILEGE + "')")
			.antMatchers("/profile**", "/profile/**").access("hasAnyAuthority('" + Constants.VIEW_PROFILE_PRIVILEGE + "', '" + Constants.UPDATE_PROFILE_PRIVILEGE + "')")
			.antMatchers("/finances**", "/finances/**").access("hasAnyAuthority('" + Constants.VIEW_FINANCIAL_ACTIVITIES_PRIVILEGE + "')")
			.antMatchers("/statistics**", "/statistics/**").access("hasAnyAuthority('" + Constants.VIEW_GENERAL_STATISTICS_PRIVILEGE + "')")
			.antMatchers("/roles**", "/roles/**").access("hasAnyAuthority('" + Constants.VIEW_ROLES_PRIVILEGE + "', '" + Constants.UPSERT_ROLES_PRIVILEGE + "', '" + Constants.DELETE_ROLES_PRIVILEGE + "')")
			.antMatchers("/users**", "/users/**").access("hasAnyAuthority('" + Constants.VIEW_USERS_PRIVILEGE + "', '" + Constants.UPSERT_USERS_PRIVILEGE + "', '" + Constants.DELETE_USERS_PRIVILEGE + "')")
			.antMatchers("/control**", "/control/**").access("hasAnyAuthority('" + Constants.MANAGE_CONTROL_PRIVILEGE + "')")
			.antMatchers("/", "/home").authenticated()
			.antMatchers("/**").access("permitAll")
			.and().formLogin().loginPage("/login")
			.successHandler(myAuthenticationSuccessHandler())
			.and().logout().logoutSuccessUrl("/")
			.and().csrf().ignoringAntMatchers("/device");
	}
}