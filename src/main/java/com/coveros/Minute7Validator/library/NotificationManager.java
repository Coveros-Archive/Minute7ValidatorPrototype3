package com.coveros.Minute7Validator.library;

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

	public NotificationManager(JavaMailSender mailSender) {
		this.mailSender = mailSender;
	}

	public void asyncCheckValidityAndSend(ArrayList<TimecardEntry> timecardEntries,
			Hashtable<String, Employee> employeeDirectory) throws EmailException {
		for (TimecardEntry te : timecardEntries) {
			if (te.getTimecardErrorEnum() != TimecardErrorEnum.VALID) {
				sendNotification(te, employeeDirectory.get(te.getEmployee().getName()));

			}
		}
	}

	private void sendNotification(TimecardEntry te, Employee employee) throws EmailException {
		if (employee != null){
			if (employee.getName().equals("Devin J Lee")) {
				SimpleMailMessage email = new SimpleMailMessage();
				email.setTo(employee.getEmail());
				email.setSubject("Timecard Error Message");
				email.setText(te.toString());
				mailSender.send(email);
			}
		}
	}
}
