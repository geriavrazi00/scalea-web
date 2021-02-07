package com.scalea.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.scalea.entities.Role;
import com.scalea.entities.User;
import com.scalea.exceptions.GenericException;
import com.scalea.repositories.RoleRepository;
import com.scalea.repositories.UserRepository;
import com.scalea.utils.Constants;

@Service
@Transactional
public class UserService implements UserDetailsService {
	
	private UserRepository userRepo;
	private RoleRepository roleRepo;

	@Autowired
	public UserService(UserRepository userRepo, RoleRepository roleRepo) {
		this.userRepo = userRepo;
		this.roleRepo = roleRepo;
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
	
	public boolean existsByUsername(String username) {
		return userRepo.existsByUsername(username);
	}
	
	public boolean existsByUsernameAndNotById(String username, Long id) {
		return userRepo.existsByUsernameAndIdNot(username, id);
	}
	
	public Iterable<User> findAll() {
		return userRepo.findAll();
	}
	
	public Page<User> findAllExceptMe(Long id, Pageable pageable) {
		return userRepo.findByIdNotOrderByUsername(id, pageable);
	}
	
	public Page<User> findAllExceptMeAndNotAdmin(Long id, Pageable pageable) throws GenericException {
		Role admin = roleRepo.findByName(Constants.ROLE_ADMIN);
		if (admin == null) throw new GenericException("Role Admin does not exist");
		return userRepo.findByIdNotAndRoleNotOrderByUsername(id, admin, pageable);
	}
	
	public Optional<User> findById(Long id) {
		return userRepo.findById(id);
	}
	
	public User save(User user) {
		return userRepo.save(user);
	}
	
	public void delete(User user) {
		userRepo.delete(user);
	}
}
