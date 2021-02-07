package com.scalea.entities;

import java.util.Collection;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.scalea.annotations.Unique;

import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@Table(name="privileges")
public class Privilege {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
 
	@NotNull
	@Column(unique=true)
	@Unique(message="{messages.privilege.exists}")
    private String name;
 
    @ManyToMany(mappedBy = "privileges")
    private Collection<Role> roles;
    
    public Privilege(String name) {
    	this.name = name;
    }

	@Override
	public String toString() {
		return "Privilege [id=" + id + ", name=" + name + "]";
	}
}
