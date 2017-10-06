package com.coveros.Minute7Validator.library;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Hashtable;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import com.coveros.Minute7Validator.library.models.Employee;
import com.coveros.Minute7Validator.library.models.TimecardEntry;

public class NotificationManager {

	private JavaMailSender mailSender;
	private DateFormat df = new SimpleDateFormat("MM/dd/yyyy");

	public NotificationManager(JavaMailSender mailSender) {
		this.mailSender = mailSender;
	}

	public void asyncCheckValidityAndSend(ArrayList<TimecardEntry> timecardEntries,
			Hashtable<String, Employee> employeeDirectory, boolean notify) throws EmailException {
		for (TimecardEntry te : timecardEntries) {
			if (te.getTimecardErrorEnum() != TimecardErrorEnum.VALID&&notify) {
				sendNotification(te, employeeDirectory.get(te.getEmployee().getName()));
			}
		}
	}

	private void sendNotification(TimecardEntry te, Employee employee) throws EmailException {
		if (employee != null) {

			SimpleMailMessage email = new SimpleMailMessage();
			email.setTo(employee.getEmail());
			email.setSubject("Timecard Error Message");
			String messageBody = "Hello, " + employee.getName() + "\n";
			messageBody += "You have made a Minute7 timecard error.\n";
			messageBody += "Date: " + df.format(te.getDate()) + "\n";
			messageBody += "Job (Box 1): " + te.getJob().getTitle() + "\n";
			messageBody += "Service (Box 2): " + te.getService().getTitle() + "\n";
			messageBody += "Payroll (Box 3): " + te.getPayroll().getTitle() + "\n";
			messageBody += "Billability (Check mark): " + te.getBillable() + "\n";
			messageBody += "You most likely made a: " + te.getTimecardErrorEnum()
					+ ". Please visit Minute7 and fix accordingly. \n";
			messageBody += "Thank you!";

			email.setText(messageBody);
			mailSender.send(email);
			
		}
	}
}
