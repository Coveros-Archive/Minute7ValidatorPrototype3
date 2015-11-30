package com.coveros.Minute7Validator.library;

import java.util.ArrayList;
import java.util.Hashtable;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;

import com.coveros.Minute7Validator.library.models.Employee;
import com.coveros.Minute7Validator.library.models.TimecardEntry;

public class NotificationManager {

	public NotificationManager(){
		
	}
	
	public void asyncCheckValidityAndSend(ArrayList<TimecardEntry> timecardEntries, Hashtable<String, Employee> employeeDirectory) throws EmailException{
		for (TimecardEntry te:timecardEntries){
			if (te.getTimecardErrorEnum()!=TimecardErrorEnum.VALID){
				sendNotification(te, employeeDirectory.get(te.getEmployee().getName()));
			}
		}
	}
	
	private void sendNotification (TimecardEntry te, Employee employee) throws EmailException{
		Email email = new SimpleEmail();
		email.setHostName("smtp.googlemail.com");
		email.setSmtpPort(465);
		email.setAuthenticator(new DefaultAuthenticator("", ""));
		email.setSSLOnConnect(true);
		email.setFrom("");
		email.setSubject("TestMail");
		email.setMsg("This is a test mail ... :-)");
		email.addTo("");
		email.send();
	}
	public void sendNotification () throws EmailException{
		Email email = new SimpleEmail();
//		email.setHostName("smtp.gmail.com");
//		email.setSmtpPort(465);
//		//email.setAuthenticator(new DefaultAuthenticator("", ""));
//		email.setAuthenticator(new DefaultAuthenticator("", ""));
//		email.setSSLOnConnect(true);
		email.setFrom("");
		email.setSubject("TestMail");
		email.setMsg("This is a test mail ... :-)");
		email.addTo("");
		email.send();
	}
}
