package com.scalea.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.scalea.entities.User;
import com.scalea.repositories.UserRepository;

@Service
public class UserService implements UserDetailsService {
	
	private UserRepository userRepo;

	@Autowired
	public UserService(UserRepository userRepo) {
		this.userRepo = userRepo;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userRepo.findByUsername(username);
		
		if (user != null) {
			return user;
		}
		
		throw new UsernameNotFoundException("User '" + username + "' not found");
	}
	
	public User findByUsername(String username) {
		return userRepo.findByUsername(username);
	}
}
