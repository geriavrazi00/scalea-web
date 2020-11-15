package com.scalea.startup;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.scalea.entities.Privilege;
import com.scalea.entities.Role;
import com.scalea.entities.User;
import com.scalea.repositories.PrivilegeRepository;
import com.scalea.repositories.RoleRepository;
import com.scalea.repositories.UserRepository;
import com.scalea.utils.Constants;

/*
 * The class is run when the server is started. It creates the initial roles and privileges and sets data of the admin to a specific user of the 
 * system. 
 */
@Component
public class SetupDataLoader implements ApplicationListener<ContextRefreshedEvent> {
	
	boolean alreadySetup = false;
	 
    @Autowired
    private UserRepository userRepository;
 
    @Autowired
    private RoleRepository roleRepository;
 
    @Autowired
    private PrivilegeRepository privilegeRepository;
 
    @Autowired
    private PasswordEncoder passwordEncoder;

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		if (alreadySetup) return;
		
        Privilege readPrivilege = createPrivilegeIfNotFound("READ_PRIVILEGE");
        Privilege writePrivilege = createPrivilegeIfNotFound("WRITE_PRIVILEGE");
 
        List<Privilege> adminPrivileges = Arrays.asList(readPrivilege, writePrivilege);        
        createRoleIfNotFound("ROLE_ADMIN", adminPrivileges);
        createRoleIfNotFound("ROLE_USER", Arrays.asList(readPrivilege));
 
        Role adminRole = roleRepository.findByName("ROLE_ADMIN");
        createAdminUserIfNotFound(adminRole);
        
        alreadySetup = true;
	}
	
	private User createAdminUserIfNotFound(Role adminRole) {
		User admin = userRepository.findByUsername(Constants.DEFAULT_ADMIN_USERNAME);
		if (admin == null) {
			admin = new User();
			admin.setUsername(Constants.DEFAULT_ADMIN_USERNAME);
			admin.setPassword(passwordEncoder.encode(Constants.DEFAULT_ADMIN_PASSWORD));
			admin.setFirstName(Constants.DEFAULT_ADMIN_FIRSTNAME);
			admin.setLastName(Constants.DEFAULT_ADMIN_LASTNAME);
			admin.setPhoneNumber(Constants.DEFAULT_ADMIN_PHONE);
			admin.setIdentification("");
			admin.setRoles(Arrays.asList(adminRole));
			
	        userRepository.save(admin);
		}
		return admin;
	}
	
	@Transactional
    private Privilege createPrivilegeIfNotFound(String name) {
 
        Privilege privilege = privilegeRepository.findByName(name);
        if (privilege == null) {
            privilege = new Privilege(name);
            privilegeRepository.save(privilege);
        }
        return privilege;
    }
 
    @Transactional
    private Role createRoleIfNotFound(String name, Collection<Privilege> privileges) {
 
        Role role = roleRepository.findByName(name);
        if (role == null) {
            role = new Role(name);
            role.setPrivileges(privileges);
            roleRepository.save(role);
        }
        return role;
    }
}
