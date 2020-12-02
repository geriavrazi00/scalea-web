package com.scalea.entities;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;

import com.scalea.annotations.FieldsMatch;
import com.sun.istack.NotNull;

import lombok.Data;
import lombok.NoArgsConstructor;
/*
 * Password rules: 
 * ^                 # start-of-string
 * (?=.*[0-9])       # a digit must occur at least once
 * (?=.*[a-z])       # a lower case letter must occur at least once
 * (?=.*[A-Z])       # an upper case letter must occur at least once
 * (?=\S+$)          # no whitespace allowed in the entire string
 * .{8,}             # anything, at least eight places though
 * $                 # end-of-string
 */
@Entity
@Data
@NoArgsConstructor
@Table(name="users")
@FieldsMatch.List({ 
    @FieldsMatch(
      field = "password", 
      fieldVerify = "confirmPassword", 
      message = "{messages.password.do.not.match}"
    )
})
public class User implements UserDetails {

	private static final long serialVersionUID = 7592167015724745706L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@NotNull
	@Size(max=255, message="{messages.username.max}")
	@NotBlank(message="{messages.username.required}")
	private String username;
	
	@NotNull
	@Size(max=255, message="{messages.password.max}")
	@NotBlank(message="{messages.password.required}")
	@Pattern(regexp="^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{8,}$", message="{messages.password.format}")
	private String password;
	
	@Transient
	private String confirmPassword;
	
	@NotNull
	@Size(max=255, message="{messages.name.max}")
	@NotBlank(message="{messages.name.required}")
	@Column(name="firstname")
	private String firstName;
	
	@NotNull
	@Size(max=255, message="{messages.lastname.max}")
	@NotBlank(message="{messages.lastname.required}")
	@Column(name="lastname")
	private String lastName;
	
	@Column(name="phonenumber")
	@Size(max=50, message="{messages.phonenumber.max}")
	@NotBlank(message="{messages.phonenumber.required}")
	private String phoneNumber;
	
	@ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable( 
        name = "users_roles", 
        joinColumns = @JoinColumn(
          name = "user_id", referencedColumnName = "id"), 
        inverseJoinColumns = @JoinColumn(
          name = "role_id", referencedColumnName = "id"))
	@NotEmpty(message="{messages.at.least.one.role}")
    private Collection<Role> roles;
	
	public User(@Size(max = 255) String username, @Size(max = 255) String password, String firstName, String lastName, String phoneNumber, 
		Collection<Role> roles) {
		this.username = username;
		this.password = password;
		this.firstName = firstName;
		this.lastName = lastName;
		this.phoneNumber = phoneNumber;
		this.roles = roles;
	}
	
	@Override
	@Transactional
	public Collection<? extends GrantedAuthority> getAuthorities() {
		List<GrantedAuthority> authorities = new ArrayList<>();
		for (Role role : roles) {
			authorities.add(new SimpleGrantedAuthority(role.getName()));
			role.getPrivileges().stream().map(p -> new SimpleGrantedAuthority(p.getName())).forEach(authorities::add);
		}
		return authorities;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public String toString() {
		return "User [id=" + id + ", username=" + username + ", password=" + password + ", firstName=" + firstName
				+ ", lastName=" + lastName + ", phoneNumber=" + phoneNumber 
				+ "]";
	}
}
