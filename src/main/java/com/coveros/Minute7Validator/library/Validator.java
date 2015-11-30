package com.coveros.Minute7Validator.library;

import java.util.ArrayList;
import java.util.Hashtable;

import com.coveros.Minute7Validator.library.models.Job;
import com.coveros.Minute7Validator.library.models.TimecardEntry;

public class Validator {

	public Validator(){
		
	}
	
	public void validateTimcardEntries(
			Hashtable<String, Job> jobList, ArrayList<TimecardEntry> timecardEntries){
		for (TimecardEntry te:timecardEntries){
			if (jobList.containsKey(te.getJob().getTitle())){
				if (jobList.get(te.getJob().getTitle()).getServices().
						containsKey(te.getService().getTitle())){
					if (jobList.get(te.getJob().getTitle()).getServices().
							get(te.getService().getTitle()).getPayrollItems().
							containsKey(te.getPayroll().getTitle())){
						if (jobList.get(te.getJob().getTitle()).getServices().
								get(te.getService().getTitle()).getPayrollItems().
								get(te.getPayroll().getTitle()).getBillable()==te.getBillable()){
							te.setTimecardErrorEnum(TimecardErrorEnum.VALID);
							System.out.println(te.toString());
						} else {
							te.setTimecardErrorEnum(TimecardErrorEnum.BILLABILITY_ERROR);
							System.out.println(te.toString());
						}
					} else {
						te.setTimecardErrorEnum(TimecardErrorEnum.PAYROLL_ERROR);
						System.out.println(te.toString());
					}
				} else {
					te.setTimecardErrorEnum(TimecardErrorEnum.SERVICE_ERROR);
					System.out.println(te.toString());
				}
			} else {
				te.setTimecardErrorEnum(TimecardErrorEnum.JOB_ERROR);
				System.out.println(te.toString());
			}
		}
	}
}
