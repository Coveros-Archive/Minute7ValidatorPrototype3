package com.coveros.Minute7Validator.library.models;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.coveros.Minute7Validator.library.BillabilityEnum;
import com.coveros.Minute7Validator.library.TimecardErrorEnum;

public class TimecardEntry {
	private Date date;
	private Employee employee;
	private Job job;
	private Service service;
	private Payroll payroll;
	private BillabilityEnum billable;
	private TimecardErrorEnum timecardErrorEnum;
	
	public TimecardEntry (Date date, Employee employee, Job job, 
			Service service, Payroll payroll, BillabilityEnum billable) {

		this.date= date;
		this.employee = employee;
		this.job=job;
		this.service=service;
		this.payroll=payroll;
		this.billable=billable;
		this.timecardErrorEnum=TimecardErrorEnum.NOT_VALIDATED;
	}
	public TimecardEntry () throws ParseException{
		SimpleDateFormat dateformat=new SimpleDateFormat("MM/dd/yyyy");
		this.date= dateformat.parse("01/01/1900");
		this.employee = new Employee("No Name");
		this.job=new Job("No job title");
		this.service=new Service("No service title");
		this.payroll=new Payroll("No payroll title");
		this.billable=BillabilityEnum.NO;
		this.timecardErrorEnum=TimecardErrorEnum.NOT_VALIDATED;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Employee getEmployee() {
		return employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

	public Job getJob() {
		return job;
	}

	public void setJob(Job job) {
		this.job = job;
	}

	public Service getService() {
		return service;
	}

	public void setService(Service service) {
		this.service = service;
	}

	public Payroll getPayroll() {
		return payroll;
	}

	public void setPayroll(Payroll payroll) {
		this.payroll = payroll;
	}

	public BillabilityEnum getBillable() {
		return billable;
	}

	public void setBillable(BillabilityEnum billable) {
		this.billable = billable;
	}
	public TimecardErrorEnum getTimecardErrorEnum() {
		return timecardErrorEnum;
	}
	public void setTimecardErrorEnum(TimecardErrorEnum timecardErrorEnum) {
		this.timecardErrorEnum = timecardErrorEnum;
	}
	@Override
	public String toString() {
		DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
		return "TimecardEntry [date=" + df.format(date) + ", employee=" + employee.getName() + ", job=" + job.getTitle() + ", service=" + service.getTitle()
				+ ", payroll=" + payroll.getTitle() + ", billable=" + getBillable() + ", timecardErrorEnum=" + getTimecardErrorEnum() + "]";
	}
}
