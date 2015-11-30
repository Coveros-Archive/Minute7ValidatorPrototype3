package com.coveros.Minute7Validator.library.models;

import java.util.ArrayList;
import java.util.Hashtable;

public class Service {
	private String title;
	private Hashtable<String, Payroll> payrollItems;
	public Service (){
		title="No service title";
		payrollItems= new Hashtable<String, Payroll>();
	}
	public Service (String title){
		this.title=title;
		payrollItems= new Hashtable<String, Payroll>();
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public Hashtable<String, Payroll> getPayrollItems() {
		return payrollItems;
	}
	public void setPayrollItems(Hashtable<String, Payroll> payrollItems) {
		this.payrollItems = payrollItems;
	}
}
