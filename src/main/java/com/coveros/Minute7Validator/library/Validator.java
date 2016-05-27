package com.coveros.Minute7Validator.library;

import java.util.ArrayList;
import java.util.Hashtable;

import com.coveros.Minute7Validator.library.models.Job;
import com.coveros.Minute7Validator.library.models.Service;
import com.coveros.Minute7Validator.library.models.TimecardEntry;

public class Validator {

	public Validator() {

	}

	public ArrayList<String> validateTimcardEntries(Hashtable<String, Job> jobList, ArrayList<TimecardEntry> timecardEntries) {
		ArrayList<String> report= new ArrayList<String>();
		for (TimecardEntry te : timecardEntries) {
			if (jobList.containsKey(te.getJob().getTitle())) {
				if (jobList.get(te.getJob().getTitle()).getServices().containsKey(te.getService().getTitle())) {
					if (jobList.get(te.getJob().getTitle()).getServices().get(te.getService().getTitle())
							.getPayrollItems().containsKey(te.getPayroll().getTitle())) {
						if (jobList.get(te.getJob().getTitle()).getServices().get(te.getService().getTitle())
								.getPayrollItems().get(te.getPayroll().getTitle()).getBillable() == te.getBillable()) {
							te.setTimecardErrorEnum(TimecardErrorEnum.VALID);
							// System.out.println(te.toString());
						} else if (jobList.get(te.getJob().getTitle()).getServices().get(te.getService().getTitle())
								.getPayrollItems().get(te.getPayroll().getTitle())
								.getBillable() == BillabilityEnum.EITHER) {
							te.setTimecardErrorEnum(TimecardErrorEnum.VALID);
							// System.out.println(te.toString());
						} else {
							te.setTimecardErrorEnum(TimecardErrorEnum.BILLABILITY_ERROR);
							report.add(te.toString());
							System.out.println(te.toString());
						}
					} else {
						te.setTimecardErrorEnum(TimecardErrorEnum.PAYROLL_ERROR);
						report.add(te.toString());
						System.out.println(te.toString());
					}
				} else if (jobList.get(te.getJob().getTitle()).getServices()
						.containsKey("Multiple by role/person but cannot be Coveros Internal")
						&& te.getService().getTitle() != "Coveros Internal") {
					if (jobList.get(te.getJob().getTitle()).getServices()
							.get("Multiple by role/person but cannot be Coveros Internal").getPayrollItems()
							.containsKey(te.getPayroll().getTitle())) {
						if (jobList.get(te.getJob().getTitle()).getServices()
								.get("Multiple by role/person but cannot be Coveros Internal").getPayrollItems()
								.get(te.getPayroll().getTitle()).getBillable() == te.getBillable()) {
							te.setTimecardErrorEnum(TimecardErrorEnum.VALID);
							// System.out.println(te.toString());
						} else if (jobList.get(te.getJob().getTitle()).getServices()
								.get("Multiple by role/person but cannot be Coveros Internal").getPayrollItems()
								.get(te.getPayroll().getTitle()).getBillable() == BillabilityEnum.EITHER) {
							te.setTimecardErrorEnum(TimecardErrorEnum.VALID);
							// System.out.println(te.toString());
						} else {
							te.setTimecardErrorEnum(TimecardErrorEnum.BILLABILITY_ERROR);
							report.add(te.toString());
							System.out.println(te.toString());
						}
					} else {
						te.setTimecardErrorEnum(TimecardErrorEnum.PAYROLL_ERROR);
						report.add(te.toString());
						System.out.println(te.toString());
					}
				} else {
					te.setTimecardErrorEnum(TimecardErrorEnum.SERVICE_ERROR);
					report.add(te.toString());
					System.out.println(te.toString());
				}
			} else {
				te.setTimecardErrorEnum(TimecardErrorEnum.JOB_ERROR);
				report.add(te.toString());
				System.out.println(te.toString());
			}
		}
		return report;
	}
}
