package com.coveros.Minute7Validator.library.models;

import com.coveros.Minute7Validator.library.BillabilityEnum;

public class Payroll {
	private String title;
	private BillabilityEnum billable;
	public Payroll (){
		title="No payroll title";
		billable=BillabilityEnum.NO;
	}
	public Payroll (String title){
		this.title=title;
		billable=BillabilityEnum.NO;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public BillabilityEnum getBillable() {
		return billable;
	}
	public void setBillable(BillabilityEnum billable) {
		this.billable = billable;
	}
}
