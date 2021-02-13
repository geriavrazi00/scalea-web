package com.scalea.entities;

import java.util.Collection;

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
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@Table(name="roles")
public class Role {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	
	@NotNull
	@Size(max=255, message="{messages.name.max}")
	@NotBlank(message="{messages.name.required}")
	@Column(unique=true)
	// @Unique(message="{messages.role.exists}")
    private String name;
    
    @ManyToMany(mappedBy = "roles")
    private Collection<User> users;
 
    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(
        name = "roles_privileges", 
        joinColumns = @JoinColumn(
          name = "role_id", referencedColumnName = "id"), 
        inverseJoinColumns = @JoinColumn(
          name = "privilege_id", referencedColumnName = "id"))
    @NotEmpty(message="{messages.at.least.one.privilege}")
    private Collection<Privilege> privileges;
    
    private boolean deletable = true;
    
    public Role(String name) {
    	this.name = name;
    }

	@Override
	public String toString() {
		return "Role [name=" + name + "]";
	}
}
