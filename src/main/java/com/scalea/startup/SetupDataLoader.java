package com.scalea.startup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.scalea.entities.Area;
import com.scalea.entities.Group;
import com.scalea.entities.Privilege;
import com.scalea.entities.Role;
import com.scalea.entities.User;
import com.scalea.entities.Vacancy;
import com.scalea.enums.ApplicationPrivileges;
import com.scalea.enums.ApplicationRoles;
import com.scalea.repositories.AreaRepository;
import com.scalea.repositories.GroupRepository;
import com.scalea.repositories.PrivilegeRepository;
import com.scalea.repositories.RoleRepository;
import com.scalea.repositories.UserRepository;
import com.scalea.repositories.VacancyRepository;
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
    
    @Autowired
    private AreaRepository areaRepository;
    
    @Autowired
    private GroupRepository groupRepository;
    
    @Autowired
    private VacancyRepository vacancyRepository;


    @Transactional
	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		this.manageRolesAndPrivileges();
		this.manageAreasAndGroups();
	}
	
    @Transactional
    private void manageRolesAndPrivileges() {
    	if (alreadySetup) return;
		
		List<Privilege> adminPrivileges = new ArrayList<>();
		List<Privilege> userPrivileges = new ArrayList<>();
		
		/*
		 *  Setting up privileges. The administrator role should have them all. The User role at the moment only has read privileges. This will change 
		 *  with time.
		 */
		for (ApplicationPrivileges initialPrivilege: ApplicationPrivileges.values()) {
			Privilege privilege = createPrivilegeIfNotFound(initialPrivilege.getName());
			adminPrivileges.add(privilege);
			
			if (initialPrivilege.isForUsers()) {
				userPrivileges.add(privilege);
			}
		}
		
        createRoleIfNotFound(ApplicationRoles.ROLE_ADMIN.getName(), adminPrivileges);
        createRoleIfNotFound(ApplicationRoles.ROLE_USER.getName(), userPrivileges);
 
        Role adminRole = roleRepository.findByName(ApplicationRoles.ROLE_ADMIN.getName());
        createAdminUserIfNotFound(adminRole);
        
        alreadySetup = true;
    }
    
	@Transactional
	private User createAdminUserIfNotFound(Role adminRole) {
		User admin = userRepository.findByUsername(Constants.DEFAULT_ADMIN_USERNAME);
		if (admin == null) {
			admin = new User(Constants.DEFAULT_ADMIN_USERNAME, passwordEncoder.encode(Constants.DEFAULT_ADMIN_PASSWORD), Constants.DEFAULT_ADMIN_FIRSTNAME, 
				Constants.DEFAULT_ADMIN_LASTNAME, Constants.DEFAULT_ADMIN_PHONE, adminRole);
			
			admin.setConfirmPassword(admin.getPassword()); // To bypass the validation of the password confirmation
	        userRepository.save(admin);
		}
		return admin;
	}
	
	@Transactional
    private Privilege createPrivilegeIfNotFound(String name) {
 
        Privilege privilege = privilegeRepository.findByName(name);
        if (privilege == null) {
            privilege = new Privilege(name);
            privilege = privilegeRepository.save(privilege);
        }
        return privilege;
    }
 
    @Transactional
    private Role createRoleIfNotFound(String name, Collection<Privilege> privileges) {
 
        Role role = roleRepository.findByName(name);
        if (role == null) {
            role = new Role(name);
            role.setPrivileges(privileges);
            role.setDeletable(false);
        } else {
        	role.setPrivileges(privileges);
        }
        
        role = roleRepository.save(role);
        return role;
    }
    
    @Transactional
    private void manageAreasAndGroups() {
    	Iterable<Area> areas = this.areaRepository.findByEnabled(true);
    	
    	// We get all the areas
    	for (Area area : areas) {
    		// If the areas have no groups, we create a default one and associate all the vacancies to it
			if (area.getGroups() == null || area.getGroups().size() == 0) {
				Group group = new Group();
				group.setArea(area);
				group.setDefaultGroup(true);
				group.setName("1");
				
				group = this.groupRepository.save(group);
				
				Collection<Vacancy> vacancies = (Collection<Vacancy>) this.vacancyRepository.findByAreaAndEnabledOrderByNumber(area, true);
				
				if (vacancies != null && vacancies.size() > 0) {
					for (Vacancy vacancy : vacancies) {
						vacancy.setGroup(group);
						this.vacancyRepository.save(vacancy);
					}
				}
			} else {
				// If the areas have groups, we check if they have a default one. If not, we create one and associate to it all the vacancies with no groups
				boolean foundDefaultGroup = false;
				
				for (Group group : area.getGroups()) {
					if (group.isDefaultGroup()) foundDefaultGroup = true;
				}
				
				if (!foundDefaultGroup) {
					Group group = new Group();
					group.setArea(area);
					group.setDefaultGroup(true);
					group.setName("1");
					
					group = this.groupRepository.save(group);
					
					Collection<Vacancy> vacancies = (Collection<Vacancy>) this.vacancyRepository.findByAreaAndEnabledOrderByNumber(area, true);
					
					if (vacancies != null && vacancies.size() > 0) {
						for (Vacancy vacancy : vacancies) {
							if (vacancy.getGroup() == null) {
								vacancy.setGroup(group);
								this.vacancyRepository.save(vacancy);
							}
						}
					}
				}
			}
		}
    }
}
