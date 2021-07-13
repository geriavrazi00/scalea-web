package com.scalea.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.scalea.entities.Area;
import com.scalea.entities.Group;
import com.scalea.repositories.GroupRepository;

@Service
@Transactional
public class GroupService {
	private GroupRepository groupRepo;

	@Autowired
	public GroupService(GroupRepository groupRepo) {
		this.groupRepo = groupRepo;
	}
	
	public Group save(Group group) {
		return this.groupRepo.save(group);
	}
	
	public Page<Group> findByArea(Area area, Pageable pageable) {
		return this.groupRepo.findByAreaOrderByName(area, pageable);
	}
	
	public Optional<Group> findById(Long id) {
		return this.groupRepo.findById(id);
	}
	
	public Optional<Group> findDefaultGroupByArea(Area area) {
		return this.groupRepo.findByAreaAndDefaultGroupIsTrue(area);
	}
}
