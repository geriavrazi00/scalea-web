package com.scalea.services;

import java.util.Calendar;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;

import com.scalea.entities.Process;
import com.scalea.enums.ProcessStatus;
import com.scalea.repositories.ActivityRepository;
import com.scalea.repositories.ProcessRepository;
import com.scalea.utils.Utils;

@Service
@Transactional
public class GeneralStatisticsService {
	private ActivityRepository activityRepo;
	private ProcessRepository processRepo;
	
	@Autowired
	public GeneralStatisticsService(ActivityRepository activityRepo, ProcessRepository processRepo) {
		this.activityRepo = activityRepo;
		this.processRepo = processRepo;
	}
	
	public void buildStatistics(Model model) {
		int currentPage = 1;
        int pageSize = 6;
        Calendar currentDate = Calendar.getInstance();
        int month = currentDate.get(Calendar.MONTH) + 1;
        int year = currentDate.get(Calendar.YEAR);
        
		Double weightedAmount = this.activityRepo.findSumOfWeightedAmount();
		long finishedProcesses = this.processRepo.countByStatusIn(new int[] {ProcessStatus.FINISHED.getStatus()});
		long activeProcesses = this.processRepo.countByStatusIn(new int[] {ProcessStatus.STARTED.getStatus()});
		List<Process> latestProcesses = this.processRepo.findCurrentMonthByOrderByStartedAtDesc(month, year, 
			PageRequest.of(currentPage - 1, pageSize));
		
		model.addAttribute("weightedAmount", Utils.twoDecimalPlacesDouble(weightedAmount));
		model.addAttribute("finishedProcesses", finishedProcesses);
		model.addAttribute("activeProcesses", activeProcesses);
		model.addAttribute("latestProcesses", latestProcesses);
	}
}
