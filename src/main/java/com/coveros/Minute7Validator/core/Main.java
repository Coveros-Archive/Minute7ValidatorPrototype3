package com.coveros.Minute7Validator.core;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Hashtable;

import org.apache.commons.mail.EmailException;

import com.coveros.Minute7Validator.library.*;
import com.coveros.Minute7Validator.library.models.*;

public class Main {

	//private static ArrayList<HashMap<String, HashMap<String, String>>, String> timeEntries;
	private static Hashtable<String, Job> jobList;
	private static ArrayList<TimecardEntry> timecardEntries;
	private static Hashtable<String, Employee> employeeDirectory;
	
	public static void main(String[] args) throws IOException, EmailException, ParseException {
		
		//filepath variables...hardcoded for now
		String filePathTimecardEntries;
		String filePathDependencies= "data\\dependencies\\M7 Entry Dependency Model.xlsx";
		String filePathEmails="/Users/exia/Documents/coveros_directory.xls";
		System.out.println("Loading application...");
		jobList=new Hashtable<String, Job>();
		timecardEntries=new ArrayList<TimecardEntry>();
		employeeDirectory=new Hashtable<String, Employee>();
		
		//Initialize data importer
//		DataImporter di=new DataImporter();
//		//Initialize validator
//		Validator v = new Validator();
//		//initialize notification manager
//		NotificationManager nm=new NotificationManager();
//		
//		//Use Selenium to download the timecard file
//		//filePathTimecardEntries= di.downloadTimecardDataWithSelenium();
//		filePathTimecardEntries= "data\\timecardEntries\\coveros_inc_time_export.xls.txt";
//		
//		di.importDependencies(filePathDependencies, jobList);
//		//di.importTimeEntries(filePathTimecardEntries, jobList, timecardEntries);
//		di.importDependenciesTabDelimitedTXT(filePathTimecardEntries, timecardEntries);
//		//di.importEmails(filePathEmails, employeeDirectory);
//		
//		
//		v.validateTimcardEntries(jobList, timecardEntries);
		//nm.sendNotification();
		
		//nm.asyncCheckValidityAndSend(timecardEntries, employeeDirectory);
		
		
		//go through the timecard entries and send notifications.
		
		//cleanup tasks
		//delete the timecard entries excel sheet
		//di.purgeDataFolder(new File("data\\timecardEntries"));
	}

}
